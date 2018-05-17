package cn.wuxia.wechat.pay.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

import javax.net.ssl.SSLContext;

import cn.wuxia.common.web.httpclient.HttpAction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.WechatHttpRequest;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:36
 */
public class PayHttpsRequest {
    protected static Logger logger = LoggerFactory.getLogger(WechatHttpRequest.class);

    public interface ResultListener {

        public void onConnectionPoolTimeoutError();

    }

    //表示请求器是否已经做了初始化工作
    private boolean hasInit = false;

    //连接超时时间，默认10秒
    private int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private int connectTimeout = 30000;

    //请求器的配置
    private RequestConfig requestConfig;

    //HTTP请求器
    private CloseableHttpClient httpClient;

    private static final PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

    PayHttpsRequest(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        if (httpClient != null) {
            hasInit = true;
        }
        //根据默认超时限制初始化requestConfig
        this.requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    public static PayHttpsRequest init(PayAccount account)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream fis = null;

        Resource[] resources = patternResolver.getResources("classpath:ca/apiclient_cert." + account.getPartner() + ".p12");
        logger.info(" ==========长度 : " + resources.length);

        if (resources != null && resources.length > 0) {
            fis = new FileInputStream(resources[0].getFile());
        }

        //加载本地的证书进行https加密传输
        try {
            logger.info("=====支付平台密钥：" + account.getPartner());
            keyStore.load(fis, account.getPartner().toCharArray());//设置证书密码
        } catch (Exception e) {
            logger.error("====加载证书错误：" + e);
        } finally {
            fis.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, account.getPartner().toCharArray()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, new DefaultHostnameVerifier());

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        return new PayHttpsRequest(httpClient);
    }

    /**
     * 通过Https往API post xml数据
     *
     * @param url    API地址
     * @param xmlObj 要提交的XML数据对象
     * @return API回包的实际数据
     * @throws WeChatException
     */

    public String send(HttpAction action, Object xmlObj) throws WeChatException {
        logger.info(ToStringBuilder.reflectionToString(action));
        if (!hasInit) {
            logger.info("======初始化证书文件");
            throw new WeChatException("请先调用init方法");
        }

        String result = null;

        HttpPost httpPost = new HttpPost(action.getUrl());

        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

        logger.debug("API，POST过去的数据是：");
        logger.debug(postDataXML);

        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);

        //设置请求器的配置
        httpPost.setConfig(requestConfig);

        logger.debug("executing request" + httpPost.getRequestLine());

        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw new WeChatException("发送失败", e);
        } finally {
            httpPost.abort();
        }
        logger.info("result:{}", result);
        return result;
    }

    /**
     * 设置连接超时时间
     *
     * @param socketTimeout 连接时长，默认10秒
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        resetRequestConfig();
    }

    /**
     * 设置传输超时时间
     *
     * @param connectTimeout 传输时长，默认30秒
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        resetRequestConfig();
    }

    private void resetRequestConfig() {
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    /**
     * 允许商户自己做更高级更复杂的请求器配置
     *
     * @param requestConfig 设置HttpsRequest的请求器配置
     */
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }
}
