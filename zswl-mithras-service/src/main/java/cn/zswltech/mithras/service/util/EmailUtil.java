package cn.zswltech.mithras.service.util;


import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.zswltech.mithras.service.others.SpringContextHolder;

import java.io.File;

/**
 * 发送邮件
 *
 * @author wangchuanhao
 * @date 2022/11/18 1:49 PM
 */
public class EmailUtil {

    /**
     * 发送邮件给多人
     *
     * @param to          收件人，多个收件人逗号或者分号隔开
     * @param subject     标题
     * @param content     正文
     * @param isHtml      是否为HTML格式
     * @param files       附件列表
     * @return message-id
     * @since 3.2.0
     */
    public static String send(String to, String subject, String content, boolean isHtml, File... files) {
        MailAccount commonMailAccount = SpringContextHolder.getBean(MailAccount.class);
        return MailUtil.send(commonMailAccount, to, subject, content, isHtml);
    }


}
