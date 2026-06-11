package cn.zswltech.mithras.third.financialshare.client.req;

import lombok.Data;

import java.util.List;

/**
 * 苍穹2期 删除
 **/
@Data
public class CQ2WithdrawReq {
    private String appId;
    private String appSecret;
    private String appSecuret;
    private String fromSys;
    private String billIdentification;//删除单据类型，参数值为固定四个值中的一个。记账申请单、付款申请单、应收单、收款单的标识分别为 jzsqd、fksqd、ysd、skd
    private List<String> billNo;
}