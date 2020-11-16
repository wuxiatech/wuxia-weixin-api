package cn.wuxia.wechat.test;

import cn.wuxia.common.util.HTMLUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.XMLUtil;
import cn.wuxia.wechat.message.util.DTSUtil;
import cn.wuxia.wechat.pay.bean.GetRedPackInfoResult;
import com.google.common.collect.Maps;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.nutz.json.Json;
import org.springframework.core.task.AsyncTaskExecutor;

import javax.xml.bind.JAXBException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

    public static void main(String[] args) {
        Map map = Maps.newHashMap();
//        map.put("errcode", 0);
//        map.put("errmsg", "ok1");
        map.put("media_id", "abc");
        if (MapUtils.isNotEmpty(map) && ((StringUtil.isNotBlank(map.get("errcode")) && MapUtils.getIntValue(map, "errcode") != 0))
                || (StringUtil.isNotBlank(MapUtils.getString(map, "errmsg"))
                && !StringUtil.equalsIgnoreCase(MapUtils.getString(map, "errmsg"), "ok"))) {
            System.out.println("errmsg="+ map);
        }
        System.out.println(map);


        map.put("value", "abcdefg\\n仲文之后\\n");
        System.out.println(Json.toJson(map));

        System.out.println(FilenameUtils.getPathNoEndSeparator("http://abc.eft/a/b.jpg"));
        System.out.println(FilenameUtils.getName("http://abc.eft/a/b.jpg"));
//        ruletest();



    }

    @Test
    public void test(){
      new  Thread(()->{
          Test1.getToken("abc", "11111");
      }).start();
        new  Thread(()->{
            Test1.getToken("abc", "222222");
        }).start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static class Test1{


        public synchronized static String getToken(String appid, String content){
//            synchronized (appid){
                System.out.println(content);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "";
//            }
        }
    }

}
