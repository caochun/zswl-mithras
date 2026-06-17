package cn.zswltech.mithras.creditreport.service;

import lombok.Data;

import java.util.List;

@Data
public class CreditReportClientCommerceSnapshot {

    private Long clientId;

    private String clientName;

    private String clientType;

    private Boolean corporation;

    private String corpRepresent;

    private List<ShareholderSnapshot> shareholders;

    @Data
    public static class ShareholderSnapshot {

        private String shareholderName;

        private Long capitalPercent;
    }
}
