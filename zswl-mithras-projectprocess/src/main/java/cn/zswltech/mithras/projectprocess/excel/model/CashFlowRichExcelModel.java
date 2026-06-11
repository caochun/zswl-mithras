package cn.zswltech.mithras.projectprocess.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;

/**
 * @author dingqi
 * @date 2022/9/28
 * @description 导出现金流表(复杂样式)数据模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CashFlowRichExcelModel extends ExcelModel {
    // 业务类型
    private String bizType;
    // 租金（还款）计算方式
    private String rentCalculateWay;
    // 支付方式
    private String payWay;
    // 项目金额（元）
    private BigDecimal projectAmount;
    // 首期租金（元）
    private BigDecimal downPayment;
    // 租期（月）
    private Integer monthCount;
    // 合同利率
    private BigDecimal interestRate;
    // 年还款次数
    private Integer repayTimesInYear;
    // 还款期数
    private Integer repayTimesTotal;
    // 计划起租日
    private LocalDate startRentDate;
    // 咨询费（元）
    private BigDecimal consultingFee;
    // 保证金（元）
    private BigDecimal earnest;
    // 留购价款
    private BigDecimal nominalPrice;
    // IRR
    private BigDecimal irr;
    // 现金流量表
    private LinkedList<CashFlowExcelModel> cashFlowList;
    // 现金流量表收入金额字段名称（租赁为"租金"，保理为"应收保理款"，债转为"回收款"）
    private String moneyCellName;

    /**
     * 手续费(元)
     */
    private BigDecimal commission;

}
