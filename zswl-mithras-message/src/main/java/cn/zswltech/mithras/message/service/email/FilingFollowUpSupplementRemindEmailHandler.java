package cn.zswltech.mithras.message.service.email;

import cn.zswltech.mithras.dto.filingmaterials.FilingEmailDTO;
import cn.zswltech.mithras.message.enums.EmailType;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @description: 项目资料归档补充资料即将超期提醒
 * @author: lllin
 * @date: 2025/12/08
 * @version: 1.0
 */
@Service
public class FilingFollowUpSupplementRemindEmailHandler extends AbstractSendEmailHandler<FilingEmailDTO>{

    private final static String FOLLOW_UP_ON_SUPPLEMENT_TITLE = "项目资料归档逾期通报";


    @Override
    protected String getSystemUrl() {
        return baseUrl + "/login";
    }

    @Override
    protected String generateEmailTitle() {
        return FOLLOW_UP_ON_SUPPLEMENT_TITLE;
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
                        "    <p>根据档案管理办法规定，项目经理应在项目放款审批结束后45个工作日内完成档案移交工作，" +
                        "%s合同当前距离项目投放已超45个工作日，归档已逾期，请于十个工作日内完成档案移交并提交项目资料归档流程。</p>\n" +
                        "    <a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" style=\"text-decoration: none; color: #2980b9;\">融租易2.0</a>\n" + // 占位符2：动态系统链接（合法）
                        "</body>\n" +
                        "</html>",
                filingEmailDTO.getContractCodeStr(),
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
        return EmailType.SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL.equals(emailType);
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL;
    }
}
