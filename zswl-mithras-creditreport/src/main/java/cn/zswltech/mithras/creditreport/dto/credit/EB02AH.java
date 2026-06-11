/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;

import cn.zswltech.mithras.creditreport.enums.CreditReportBusinessTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportQualityClassificationEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 其他借贷交易分类汇总信息
 */
@Data
public class EB02AH {

    /**
     *业务类型
     * {@link CreditReportBusinessTypeEnum#getDisplay()}
     **/
    private Integer EB02AD01;

    /**
     * 资产质量分类
     * {@link CreditReportQualityClassificationEnum#getDisplay()}
     **/
    private Integer EB02AD02;

    //账户数
    private int EB02AS04;

    //余额
    private BigDecimal EB02AJ06;

}