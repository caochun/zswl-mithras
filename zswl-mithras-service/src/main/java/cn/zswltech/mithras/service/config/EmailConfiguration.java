package cn.zswltech.mithras.service.config;

import cn.hutool.core.net.UserPassAuthenticator;
import cn.hutool.extra.mail.MailAccount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Authenticator;

/**
 * 邮件配置
 *
 * @author wangchuanhao
 * @date 2022/11/18 1:46 PM
 */
@Configuration
public class EmailConfiguration {

    @Value("${mail.common.host}")
    private String host;
    @Value("${mail.common.from}")
    private String from;
    @Value("${mail.common.user}")
    private String user;
    @Value("${mail.common.pass}")
    private String pass;
    @Value("${mail.common.connectTimeout:3000}")
    private Long connectTimeout;
    @Value("${mail.common.timeout:30000}")
    private Long timeout;
    @Value("${mail.common.useSsl}")
    private Boolean useSsl;


    @Value("${mithras.socks.proxy.host:}")
    private String socksProxyHost;
    @Value("${mithras.socks.proxy.port:}")
    private String socksProxyPort;
    @Value("${mithras.socks.proxy.user:}")
    private String socksProxyUser;
    @Value("${mithras.socks.proxy.password:}")
    private String socksProxyPassword;


    @Bean
    public MailAccount commonMainAccount() {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(host);
        mailAccount.setFrom(from);
        mailAccount.setUser(user);
        mailAccount.setPass(pass);
        mailAccount.setConnectionTimeout(connectTimeout);
        mailAccount.setTimeout(timeout);
        mailAccount.setAuth(true);
        if (Boolean.TRUE.equals(useSsl)) {
            mailAccount.setSslEnable(true);
        }
        if (StringUtils.isNotBlank(socksProxyHost) && StringUtils.isNotBlank(socksProxyPort)) {
            // 内网发外网邮件要代理
            mailAccount.setCustomProperty("mail.smtp.socks.host", socksProxyHost);
            mailAccount.setCustomProperty("mail.smtp.socks.port", socksProxyPort);
            mailAccount.setCustomProperty("proxySet", true);
            if (StringUtils.isNotBlank(socksProxyUser) && StringUtils.isNotBlank(socksProxyPassword)) {
                // socks代理要认证
                // { @see java.net.SocksSocketImpl#authenticate }
                Authenticator.setDefault(new UserPassAuthenticator(socksProxyUser, socksProxyPassword.toCharArray()));
            }
        }
        return mailAccount;
    }

}
