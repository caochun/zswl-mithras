package cn.zswltech.mithras.others;

import cn.hutool.core.net.UserPassAuthenticator;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

import java.net.Authenticator;

/**
 * 邮件测试代理
 *
 * @author wangchuanhao
 * @date 2022/11/19 11:14 AM
 */
public class EmailTest {

    public static void main(String[] args) {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost("smtp.163.com");
        mailAccount.setFrom("mithrastest@163.com");
        mailAccount.setUser("mithrastest@163.com");
        mailAccount.setPass("WOZAAFTIFXKSOBLJ");
        mailAccount.setConnectionTimeout(30000);
        mailAccount.setTimeout(30000);
        mailAccount.setAuth(true);
        mailAccount.setSslEnable(true);
//        mailAccount.setCustomProperty("mail.smtp.proxy.host", "172.16.200.17");
//        mailAccount.setCustomProperty("mail.smtp.proxy.port", "13129");
        mailAccount.setCustomProperty("mail.smtp.socks.host", "172.16.200.17");
        mailAccount.setCustomProperty("mail.smtp.socks.port", "11080");
        mailAccount.setCustomProperty("mail.smtp.proxy.user", "zswltech");
        mailAccount.setCustomProperty("mail.smtp.proxy.password", "Zs@12345678");
        mailAccount.setCustomProperty("proxySet", "true");
        Authenticator.setDefault(new UserPassAuthenticator("zswltech", "Zs@12345678".toCharArray()));
        MailUtil.send(mailAccount, "wangchuanhao@zswl.cn", "主体", "内容", false);
    }

}
