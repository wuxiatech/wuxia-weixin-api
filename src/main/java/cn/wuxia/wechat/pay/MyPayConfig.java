package cn.wuxia.wechat.pay;

import cn.wuxia.wechat.PayAccount;
import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;

@Slf4j
public class MyPayConfig extends WXPayConfig {

    private byte[] certData;

    private PayAccount account;

    private static final PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

    public MyPayConfig(PayAccount account) throws Exception {
        this.account = account;
        FileInputStream certStream = null;

        Resource[] resources = patternResolver.getResources("classpath:ca/apiclient_cert." + account.getPartner() + ".p12");
        log.info(" ==========长度 : " + resources.length);

        if (resources != null && resources.length > 0) {
            File file = resources[0].getFile();
            certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
        } else {
            throw new FileNotFoundException("classpath:ca/apiclient_cert." + account.getPartner() + ".p12");
        }
        certStream.close();
    }

    @Override
    public String getAppID() {
        return account.getAppid();
    }

    @Override
    public String getMchID() {
        return account.getPartner();
    }

    @Override
    public String getKey() {
        return account.getAppKey();
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    protected IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }
}
