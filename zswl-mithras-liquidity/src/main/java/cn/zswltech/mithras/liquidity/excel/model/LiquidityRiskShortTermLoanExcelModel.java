package cn.zswltech.mithras.liquidity.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 短期贷款明细下载
 * @author: jackerhe
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LiquidityRiskShortTermLoanExcelModel extends ExcelModel {

    //项目名称
    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 10)
    private String organizationName;

    //合同编号
    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 20)
    private String financingCode;

    //申报授信金额-合同金额
    @SimpleExcelHeader(headerName = "融资总额（万元）", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String financingAmount;

    //现金流入时间
    @SimpleExcelHeader(headerName = "还本日", headerOrder = 40, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate repaymentDate;

    //本金
    @SimpleExcelHeader(headerName = "应还本金（万元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String principleAmount;

    //利息
    @SimpleExcelHeader(headerName = "应还利息（万元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String interestAmount;

    //合计金额
    @SimpleExcelHeader(headerName = "合计还款总金额（万元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String totalAmount;


}
