package cn.zswltech.mithras.service.config;


import cn.zswltech.mithras.service.util.email.TianyiMailAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @description: 新邮件配置
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Configuration
public class TianyiEmailConfiguration {



    @Value("${mail.tianyi.host}")
    private String host;
    @Value("${mail.tianyi.port}")
    private Integer port;
    @Value("${mail.tianyi.from}")
    private String from;
    @Value("${mail.tianyi.user}")
    private String user;
    @Value("${mail.tianyi.pass}")
    private String pass;
    @Value("${mail.tianyi.connectTimeout:6000}")
    private Long connectTimeout;
    @Value("${mail.tianyi.timeout:30000}")
    private Long timeout;
    @Value("${mail.tianyi.useSsl}")
    private Boolean useSsl;


    /**
     *  天翼企业有限配置
     */
    @Bean
    public TianyiMailAccount tyDefaultAccount() {
        TianyiMailAccount mailAccount = new TianyiMailAccount();
        mailAccount.setHost(host);//SMTP服务器
        mailAccount.setPort(port);// 端口
        mailAccount.setFrom(from);// 发件人昵称
        mailAccount.setUser(user);// 发件人账号（与认证一致）
        mailAccount.setPass(pass);// 密码/授权码
        mailAccount.setAuth(true);//开启SMTP认证（必须）
        if (Boolean.TRUE.equals(useSsl)) {
            mailAccount.setSslEnable(true); // 开启SSL加密（465端口必须启用）
        }
        mailAccount.setConnectionTimeout(connectTimeout);
        mailAccount.setTimeout(timeout);
        return mailAccount;
    }





}
