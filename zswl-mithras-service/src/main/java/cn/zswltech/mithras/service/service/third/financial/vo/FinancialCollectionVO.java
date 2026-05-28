package cn.zswltech.mithras.service.service.third.financial.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @ClassName FinancialCollectionVO
 * @Description 用于传输普通收款至苍穹
 * @Author jackerhe
 * @Date 2023/4/3 1:01 下午
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class FinancialCollectionVO {

    //收款编号
    private String code;

    private String contractCode;

    /**
     * 计划收款金额
     */
    private Long planCollectionAmount;
    /**
     * 计划收款日期
     */
    private LocalDate planCollectionDate;

    /**
     * 现金流项目
     */
    private String cashFlowItem;

    private String maturityDate;

}
