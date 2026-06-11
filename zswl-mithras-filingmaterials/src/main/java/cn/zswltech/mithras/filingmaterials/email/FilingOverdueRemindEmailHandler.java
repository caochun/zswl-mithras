package cn.zswltech.mithras.filingmaterials.email;

import cn.zswltech.mithras.dto.filingmaterials.FilingEmailDTO;
import cn.zswltech.mithras.message.enums.EmailType;
import cn.zswltech.mithras.message.service.email.AbstractSendEmailHandler;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @description: 项目资料归档逾期通报
 * @author: lllin
 * @date: 2025/12/08
 * @version: 1.0
 */
@Service
public class FilingOverdueRemindEmailHandler extends AbstractSendEmailHandler<FilingEmailDTO>{

    private final static String OVERDUE_TITLE = "项目资料归档补充资料即将超期提醒";


    @Override
    protected String getSystemUrl() {
        return baseUrl + "/login";
    }

    @Override
    protected String generateEmailTitle() {
        return OVERDUE_TITLE;
    }

    @Override
    protected String generateTable(FilingEmailDTO filingEmailDTO) {
        return null;
    }


    @Override
    protected Boolean getIsHtml() {
        return Boolean.TRUE;
    }

    @Override
    protected String generateEmailHtml(String table, String systemUrl, FilingEmailDTO filingEmailDTO) {
        String html = String.format(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"zh-CN\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <style>\n" +
                        "        p {\n" +
                        "            text-indent: 2em;\n" +
                        "            line-height: 1.8;\n" +
                        "            margin: 0 0 10px 0;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <p>根据档案管理办法规定，被退回档案的经办人应于10个工作日内完成整改并重新归档，" +
                        "【%s】合同当前距离档案管理岗退回已超7个工作日，请于%s前完成资料补充并提交项目资料归档流程。</p>\n" +
                        "    <a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" style=\"text-decoration: none; color: #2980b9;\">融租易2.0</a>\n" + // 占位符2：动态系统链接（合法）
                        "</body>\n" +
                        "</html>",
                filingEmailDTO.getContractCodeStr(),
                filingEmailDTO.getDueDate(),
                systemUrl
        );
        return html;
    }

    @Override
    protected File[] getFiles(FilingEmailDTO businessData) {
        return new File[0];
    }


    @Override
    public boolean  needHandle(EmailType emailType){
        return EmailType.OVERDUE_REMIND_EMAIL.equals(emailType);
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.OVERDUE_REMIND_EMAIL;
    }
}
