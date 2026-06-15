package cn.zswltech.mithras.credit.application.groupcredit.establish;

import lombok.Data;

@Data
public class GroupCreditEstablishProcessInfo {

    private String startUserId;

    private String processInstanceId;

    private String currentTaskActivityIds;
}
