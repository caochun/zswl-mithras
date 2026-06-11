package cn.zswltech.mithras.fund.mapper.lib.financing;

import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/24 19:06
 */
@Data
public class FinancingDto {
    //融资机构Id
    private Long financingOrgId;

    //融资机构
    private String financingOrg;

    //融资详情Id
    private Long financingId;

    //融资编码
    private String financingCode;

    //融资总额
    private Long financingAmount;

    //现金流出时间
    private LocalDate cashOutflowTime;

    //本金
    private Long principle;

    //利息
    private Long interest;

    //来源
    private String source;
}
