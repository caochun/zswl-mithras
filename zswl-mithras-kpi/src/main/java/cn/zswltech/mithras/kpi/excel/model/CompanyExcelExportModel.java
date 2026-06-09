package cn.zswltech.mithras.kpi.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author yangxiong
 * @date 2024/7/1/11:23
 * 公司业务目标数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyExcelExportModel extends BaseExcelModel{

    /**
     * FTP行业类型
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
    private BigDecimal investmentTarget;

    /**
     * 营业收入目标（万元）
     */
    @ExcelProperty("营业收入目标（万元）")
    private BigDecimal revenueTarget;

    /**
     * 利润目标（万元）
     */
    @ExcelProperty("利润目标（万元）")
    private BigDecimal profitTarget;
}
