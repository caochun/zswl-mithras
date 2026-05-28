package cn.zswltech.mithras.service.service.kpi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangxiong
 * @date 2024/7/1/11:32
 * @description
 */
@Data
//@EqualsAndHashCode(callSuper = true)
public class PersonalExcelModel {

    /**
     * 登陆账户名称
     */
    @ExcelProperty("登陆账户名称")
    private String loginAccountName;

    /**
     * 业务人员名称
     */
    @ExcelProperty("业务人员名称")
    private String staffName;

    /**
     * 所属部门
     */
    @ExcelProperty("部门名称")
    private String department;

    /**
     * 业务类型
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
