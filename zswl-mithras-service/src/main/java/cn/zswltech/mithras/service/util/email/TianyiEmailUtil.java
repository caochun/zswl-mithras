package cn.zswltech.mithras.service.util.email;


import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.alibaba.nacos.common.utils.CollectionUtils;

import java.io.File;
import java.util.Set;

/**
 * @description: 邮件工具类
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
public class TianyiEmailUtil {

    private static final String SUCCESS = "SUCCESS";


    /**
     * 使用天翼发送
     *
     * @param to      收件人，多个收件人逗号或者分号隔开
     * @param subject 标题
     * @param content 正文
     * @param isHtml  是否为HTML格式  true 举个例子<h1 style='color:blue'>HTML测试</h1><p>这是<span style='color:red'>带样式</span>的邮件</p>
     * @param files   附件列表
     */
    public static String sendEMail(String to, String subject, String content, boolean isHtml, File[] files) {
        try {
            MailAccount tyDefaultAccount = SpringContextHolder.getBean(TianyiMailAccount.class);
            //附件 发送
            MailUtil.send(tyDefaultAccount, to, subject, content, false, files); //
            return SUCCESS;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 使用天翼发送
     *
     * @param toSet   收件人
     * @param ccSet   抄送人
     * @param bccSet  密送送人
     * @param subject 标题
     * @param content 正文
     * @param isHtml  是否为HTML格式  true 举个例子<h1 style='color:blue'>HTML测试</h1><p>这是<span style='color:red'>带样式</span>的邮件</p>
     * @param files   附件列表
     */
    public static String sendEMail(Set<String> toSet, Set<String> ccSet, Set<String> bccSet, String subject, String content, boolean isHtml, File... files) {
        if (CollectionUtils.isEmpty(toSet)) {
            throw new RuntimeException("收件人为空！！！！");
        }
        MailAccount tyDefaultAccount = SpringContextHolder.getBean(TianyiMailAccount.class);
        String sessionId = MailUtil.send(tyDefaultAccount, toSet, ccSet, bccSet, subject, content, isHtml, files);
        return sessionId;

    }
}
