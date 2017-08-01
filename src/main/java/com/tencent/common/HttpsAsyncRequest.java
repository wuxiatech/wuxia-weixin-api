package com.tencent.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.collect.Lists;
import com.tencent.service.IServiceRequest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import cn.wuxia.common.web.httpclient.HttpAsyncClientUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:36
 */
public class HttpsAsyncRequest extends TokenUtil implements IServiceRequest {

    public interface ResultListener {

        public void onConnectionPoolTimeoutError();

    }

    private static Log log = new Log(LoggerFactory.getLogger(HttpsAsyncRequest.class));

    //表示请求器是否已经做了初始化工作
    private boolean hasInit = false;

    //连接超时时间，默认10秒
    private int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private int connectTimeout = 30000;

    //请求器的配置
    private RequestConfig requestConfig;

    //HTTP请求器
    private CloseableHttpAsyncClient httpAsyncClient;

    private static final PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

    public HttpsAsyncRequest() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        init();
    }

    private void init() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream fis = null;

        try {
            Resource[] resources = patternResolver.getResources("classpath:ca/apiclient_cert.p12");
            logger.info("==============resource : " + resources + " ==========长度 : " + resources.length);

            if (resources != null && resources.length > 0) {
                fis = new FileInputStream(resources[0].getFile());
            }
            logger.info("=========instream : " + fis);
        } catch (IOException e) {
            logger.error("读取证书出错 ： " + e);
        }

        //加载本地的证书进行https加密传输
        try {
            logger.info("=====支付平台密钥：" + properties.get("PARTNER").toString());
            keyStore.load(fis, properties.get("PARTNER").toString().toCharArray());//设置证书密码
        } catch (Exception e) {
            logger.error("====加载证书错误：" + e);
        } finally {
            fis.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, properties.get("PARTNER").toString().toCharArray()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, new DefaultHostnameVerifier());

        httpAsyncClient = HttpAsyncClients.custom().setSSLContext(sslcontext).build();

        //根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();

        hasInit = true;
    }

    public String sendPost(String url, Object xmlObj)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {
        try {
            return sendPost(url, new Object[] { xmlObj }).get(0);
        } catch (HttpClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过Https往API post xml数据
     *
     * @param url    API地址
     * @param xmlObj 要提交的XML数据对象
     * @return API回包的实际数据
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws HttpClientException 
     */

    public List<String> sendPost(String url, Object[] xmlObj)
            throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, HttpClientException {

        if (!hasInit) {
            logger.info("======初始化证书文件");
            init();
        }
        httpAsyncClient.start();
        List<String> result = Lists.newArrayList();
        List<Future<HttpResponse>> lists = Lists.newArrayList();
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        for (Object param : xmlObj) {
            final HttpPost httpPost = new HttpPost(url);
            //解决XStream对出现双下划线的bug
            //将要提交给API的数据对象转换成XML格式数据Post给API
            String postDataXML = xStreamForRequestPostData.toXML(param);
            Util.log("API，POST过去的数据是：");
            Util.log(postDataXML);
            //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
            StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(postEntity);
            //设置请求器的配置
            httpPost.setConfig(requestConfig);
            Util.log("executing request " + httpPost.getRequestLine());
            try {
                Future<org.apache.http.HttpResponse> future = httpAsyncClient.execute(httpPost, new FutureCallback<org.apache.http.HttpResponse>() {
                    public void completed(final org.apache.http.HttpResponse response) {
                        logger.info(httpPost.getRequestLine() + "->" + response.getStatusLine());
                    }

                    public void failed(final Exception ex) {
                        logger.warn(httpPost.getRequestLine().toString(), ex);
                    }

                    public void cancelled() {
                        logger.warn(httpPost.getRequestLine().toString() + " cancelled");
                    }

                });
                lists.add(future);
            } catch (Exception e) {
                log.e("http get throw Exception " + e.getMessage());
            } finally {
            }
        }
        for (Future<HttpResponse> future : lists) {
            HttpClientResponse response = HttpAsyncClientUtil.getResponse(future);
            result.add(new String(response.getByteResult(), "UTF-8"));
        }
        return result;
    }

    /**
     * 设置连接超时时间
     *
     * @param socketTimeout 连接时长，默认10秒
     */
    public void setSocketTimeout(int socketTimeout) {
        socketTimeout = socketTimeout;
        resetRequestConfig();
    }

    /**
     * 设置传输超时时间
     *
     * @param connectTimeout 传输时长，默认30秒
     */
    public void setConnectTimeout(int connectTimeout) {
        connectTimeout = connectTimeout;
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
        requestConfig = requestConfig;
    }
}
