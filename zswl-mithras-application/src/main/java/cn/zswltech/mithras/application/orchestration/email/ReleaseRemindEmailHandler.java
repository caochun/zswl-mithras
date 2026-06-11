package cn.zswltech.mithras.application.orchestration.email;

import cn.zswltech.mithras.customer.application.client.dto.ClientReleaseRemindDTO;
import cn.zswltech.mithras.customer.application.client.dto.ReleaseRemindEmailInfoDTO;
import cn.zswltech.mithras.message.enums.EmailType;
import cn.zswltech.mithras.message.service.email.AbstractSendEmailHandler;
import cn.zswltech.mithras.customer.enums.client.ClientRemindContentEnum;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @description: 客户释放前通知邮件
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Service
public class ReleaseRemindEmailHandler extends AbstractSendEmailHandler<ReleaseRemindEmailInfoDTO>{

    private final static String title = "客户即将释放通知";
    private static final String email = "EMAIL";


    @Override
    protected String getSystemUrl() {
        return baseUrl + "/login";
    }

    @Override
    protected String generateEmailTitle() {
        return title;
    }

    @Override
    protected String generateTable(ReleaseRemindEmailInfoDTO remindEmailInfoDTO) {
        List<ClientReleaseRemindDTO> clientReleaseRemindDTOList = remindEmailInfoDTO.getClientReleaseRemindDTOList();
        Map<Long, String> clientId2Name = remindEmailInfoDTO.getClientId2Name();
        // 使用StringJoiner拼接多个客户项，避免字符串拼接效率问题
        StringJoiner customerJoiner = new StringJoiner("\n");
        // 遍历客户列表，生成单条客户HTML   目前设置15；防止
        int i = 0;
        for (ClientReleaseRemindDTO remindDto : clientReleaseRemindDTOList) {
            String clientName = clientId2Name.get(remindDto.getClientId());
            String reLeaseInfo = bulReleaseInfo(remindDto);
            String customerInfo = String.format("<tr> <td style=\"padding: 8px;\">%s</td><td style=\"padding: 8px;\">%s</td></tr>", clientName, reLeaseInfo);
            customerJoiner.add(customerInfo);
            if (i > 15) {
                break;
            }
            i++;
        }
        // 若客户列表为空，显示提示信息
        if (customerJoiner.length() == 0) {
            return "<div class=\"customer-item\">无数据</div>";
        }
        return customerJoiner.toString();
    }

    /**
     * 拼接释放原因
     */
    private String bulReleaseInfo(ClientReleaseRemindDTO remindDto) {
        StringBuffer relationgBuff = new StringBuffer();
        List<String> sourceList = remindDto.getSourceList();
        sourceList.sort(Comparator.naturalOrder());
        int i = 0;
        //使用&拼接
        for (String source : sourceList) {
            ClientRemindContentEnum contentEnum = ClientRemindContentEnum.getContentEnum(email, source);
            if (Objects.nonNull(contentEnum)) {
                relationgBuff.append(contentEnum.getDisplay());
                if (i != sourceList.size() - 1) {
                    relationgBuff.append("&");
                }
            }
            i++;
        }
        return relationgBuff.toString();
    }


    @Override
    protected Boolean getIsHtml() {
        return Boolean.TRUE;
    }

    @Override
    protected String generateEmailHtml(String table, String systemUrl,ReleaseRemindEmailInfoDTO remindEmailInfoDTO) {
        String html = String.format(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"zh-CN\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: \"Microsoft YaHei\", Arial, sans-serif;\n" +
                        "            margin: 20px;\n" +
                        "            line-height: 1;\n" +
                        "            color: #333;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    以下客户将在 <strong>7天后</strong> 释放，请及时跟进处理：<br><br>\n" +
                        "    \n" +
                        "    <table border=\"1\" style=\"border-collapse: collapse; width: 100%%;\">\n" + // 这里的%改成%%
                        "        <thead>\n" +
                        "            <tr style=\"background-color: #f2f2f2;\">\n" +
                        "                <th style=\"padding: 8px; text-align: left; width: 250px; min-width: 200px;\">客户名称</th>\n" + // 这里的%改成%%
                        "                <th style=\"padding: 8px; text-align: left;\">释放原因</th>\n" +
                        "            </tr>\n" +
                        "        </thead>\n" +
                        "        <tbody>\n" +
                        "            %s\n" + // 占位符1：表格内容（合法的格式化占位符）
                        "        </tbody>\n" +
                        "    </table>\n" +
                        "    <a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" style=\"text-decoration: none; color: #2980b9;\">融租易2.0</a>\n" + // 占位符2：动态系统链接（合法）
                        "</body>\n" +
                        "</html>",
                table, // 对应占位符1：表格内容
                systemUrl // 对应占位符2：实际系统链接
        );
        return html;
    }

    @Override
    protected File[] getFiles(ReleaseRemindEmailInfoDTO remindEmailInfoDTO) {
        return null;
    }



    @Override
    public boolean  needHandle(EmailType emailType){
        return EmailType.BF_RELEASE_REMIND_EMAIL.equals(emailType);
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.BF_RELEASE_REMIND_EMAIL;
    }
}
