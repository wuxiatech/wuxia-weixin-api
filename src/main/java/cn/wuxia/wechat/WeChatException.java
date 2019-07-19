/*
* Created on :29 Aug, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat;

public class WeChatException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 2714496706512845530L;

    public WeChatException() {
        super();
    }

    public WeChatException(Exception e) {
        super("", e);
    }

    public WeChatException(String message, Exception e) {
        super(message, e);
    }

    public WeChatException(String message) {
        super(message);
    }
}
