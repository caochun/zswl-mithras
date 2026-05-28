package cn.zswltech.mithras.service.service.kpi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 部门业务目标数据实体类
 * @author yangxiong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptExcelModel extends BaseExcelModel{

    /**
     * 部门
     */
    @ExcelProperty("部门名称")
    private String department;

    /**
     * FTP行业分类
     */
    @ExcelProperty("FTP行业分类")
    private String businessType;

    /**
     * 资产余额（万元）
     */
    @ExcelProperty("资产余额（万元）")
    private BigDecimal assetBalanceTarget;

    /**
     * 投放额目标（万元）
     */
    @ExcelProperty("投放额目标（万元）")
    private BigDecimal annualInvestmentTarget;

    /**
     * 营业收入目标（万元）
     */
    @ExcelProperty("营业收入目标（万元）")
    private BigDecimal annualRevenueTarget;

    /**
     * 咨询服务费收入
     */
    @ExcelProperty("咨询服务费收入（万元）")
    private BigDecimal consultingFeeIncome;

    /**
     * 利息收入
     */
    @ExcelProperty("利息收入（万元）")
    private BigDecimal interestIncome;

    /**
     * 经营费用
     */
    @ExcelProperty("经营费用（万元）")
    private BigDecimal bizFee;

    /**
     * 差旅费
     */
    @ExcelProperty("差旅费（万元）")
    private BigDecimal businessTripFee;

    /**
     * 业务招待费
     */
    @ExcelProperty("业务招待费（万元）")
    private BigDecimal businessServeFee;

    /**
     * 拨备前利润目标
     */
    @ExcelProperty("拨备前利润目标（万元）")
    private BigDecimal beforeProfitTarget;

    /**
     * 利润目标（万元）
     */
    @ExcelProperty("利润目标（万元）")
    private BigDecimal annualProfitTarget;
}
