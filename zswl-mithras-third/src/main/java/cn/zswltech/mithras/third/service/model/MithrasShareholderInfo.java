package cn.zswltech.mithras.third.service.model;

import lombok.Data;

/**
 * @author junke
 */
@Data
public class MithrasShareholderInfo {

    private String shareholderName;

    private String shareholderType;

    private Long capitalPercent;

    private String capitalWay;

    private Long actualPaidTotal;

    private Long paidTotal;

    private Boolean realController;
}
