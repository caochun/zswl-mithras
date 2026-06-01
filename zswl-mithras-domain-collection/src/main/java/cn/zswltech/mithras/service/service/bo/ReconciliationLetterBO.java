package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName ReconciliationLetterBO
 * @Description 各法人与租赁资金往来
 * @Author jackerhe
 * @Date 2023/3/22 6:54 下午
 * @Version 1.0
 **/
@Data
public class ReconciliationLetterBO {
    private Long clientId;

    private String clientName;

    private String data;

    private BigDecimal lastPrincipal;

    ////租赁，转租赁，债权转让
    //融资租赁租金及留购货款（租金+名义价款） 剩余租金及留购价款
    private BigDecimal rentAndNominalPrice;

    //融资租赁保证金（保证金余额）+ 保理保证金  + 融资性售后回租保证金（保证金余额）
    private BigDecimal margin;

    /////保理

    //保理本息
    private BigDecimal blPrincipalAndInterest;




}
