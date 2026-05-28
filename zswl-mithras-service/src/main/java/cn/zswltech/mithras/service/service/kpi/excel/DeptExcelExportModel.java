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
public class DeptExcelExportModel extends BaseExcelModel{

    /**
     * 部门
     */
    @ExcelProperty("部门")
    private String department;

    /**
     * FTP行业
     */
    @ExcelProperty("FTP行业分类")
    private String businessType;

    /**
     * 投放额目标（万元）
     */
    @ExcelProperty("投放额目标（万元）")
    private BigDecimal annualInvestmentTarget;


    /**
     * 利润目标（万元）
     */
    @ExcelProperty("利润目标（万元）")
    private BigDecimal annualProfitTarget;

    /**
     * 拨备前利润目标
     */
    @ExcelProperty("利润目标（拨备前）（万元）")
    private BigDecimal beforeProfitTarget;

    /**
     * 营业收入目标（万元）
     */
    @ExcelProperty("营业收入目标（万元）")
    private BigDecimal annualRevenueTarget;

    /**
     * 营业收入-咨询服务费收入
     */
    @ExcelProperty("营业收入-咨询服务费收入（万元）")
    private BigDecimal consultingFeeIncome;

    /**
     * 营业输入-利息收入
     */
    @ExcelProperty("营业收入-利息收入（万元）")
    private BigDecimal interestIncome;

    /**
     * 业务规模
     */
    @ExcelProperty("业务规模（万元）")
    private BigDecimal assetBalanceTarget;

    /**
     * 经营费用
     */
    @ExcelProperty("经营费用（万元）")
    private BigDecimal bizFee;

    /**
     * 经营费用-业务招待费
     */
    @ExcelProperty("经营费用-业务招待费（万元）")
    private BigDecimal businessServeFee;


    /**
     * 经营费用-差旅费
     */
    @ExcelProperty("经营费用-差旅费用（万元）")
    private BigDecimal businessTripFee;

}
