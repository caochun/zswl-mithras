package cn.zswltech.mithras.finance.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitCalculateExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "业务类型")
    private String bizType;

    @SimpleExcelHeader(headerName = "项目类型")
    private String projectClassify;

    @SimpleExcelHeader(headerName = "业务部门")
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    @SimpleExcelHeader(headerName = "投放时间", columnStyle = ColumnStyleEnum.DATE)
    private LocalDate contractStartDate;

    @SimpleExcelHeader(headerName = "当年已确认收入（税后）", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal confirmIncomeThisYear;

    @SimpleExcelHeader(headerName = "当年测算利息收入（税后）", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal calculateInterestThisYear;

    @SimpleExcelHeader(headerName = "营业收入", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal operatingIncome;

    @SimpleExcelHeader(headerName = "FTP成本", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal ftpInterest;

    @SimpleExcelHeader(headerName = "上期末风险金余额", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal riskBalanceEndOfLastYear;

    @SimpleExcelHeader(headerName = "本期末风险金余额", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal riskBalanceEndOfThisYear;

    @SimpleExcelHeader(headerName = "本年风险金计提/转回", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal riskUsedThisYear;

    @SimpleExcelHeader(headerName = "附加税", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal additionalTax;

    @SimpleExcelHeader(headerName = "利润总额", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal profit;

    @SimpleExcelHeader(headerName = "利润总额（扣除费用）", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal profitExcludeFee;

    @SimpleExcelHeader(headerName = "本年末剩余本金", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingPrincipleEndOfThisYear;

    @SimpleExcelHeader(headerName = "本年末保证金余额", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingEarnestEndOfThisYear;

    @SimpleExcelHeader(headerName = "年末敞口", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal riskExposureEndOfThisYear;
}
