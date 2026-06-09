package cn.zswltech.mithras.kpi.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangxiong
 * @date 2024/7/1/21:41
 * @description
 */
@Data
public class BaseExcelModel {

    // 从1月到12月的目标定义，类型为BigDecimal
    @ExcelProperty("1月")
    private BigDecimal januaryTarget;

    @ExcelProperty("2月")
    private BigDecimal februaryTarget;

    @ExcelProperty("3月")
    private BigDecimal marchTarget;

    @ExcelProperty("4月")
    private BigDecimal aprilTarget;

    @ExcelProperty("5月")
    private BigDecimal mayTarget;

    @ExcelProperty("6月")
    private BigDecimal juneTarget;

    @ExcelProperty("7月")
    private BigDecimal julyTarget;

    @ExcelProperty("8月")
    private BigDecimal augustTarget;

    @ExcelProperty("9月")
    private BigDecimal septemberTarget;

    @ExcelProperty("10月")
    private BigDecimal octoberTarget;

    @ExcelProperty("11月")
    private BigDecimal novemberTarget;

    @ExcelProperty("12月")
    private BigDecimal decemberTarget;
}
