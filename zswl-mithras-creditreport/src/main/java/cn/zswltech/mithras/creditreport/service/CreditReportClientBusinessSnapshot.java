package cn.zswltech.mithras.creditreport.service;

import lombok.Data;

import java.util.List;

@Data
public class CreditReportClientBusinessSnapshot {

    private Long clientId;

    private String tycName;

    private String tycCorpRepresent;

    private List<ShareholderSnapshot> tycShareHolderInfo;

    @Data
    public static class ShareholderSnapshot {

        private String shareholderName;

        private Long capitalPercent;
    }
}
