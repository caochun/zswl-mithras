package cn.zswltech.mithras.liquidity.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 流入明细下载
 * @author: jackerhe
 * @date: 2023/5/17 3:44 下午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LiquidityRiskInflowExcelModel extends ExcelModel {

    //项目名称
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    //合同编号
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    //申报授信金额-合同金额
    @SimpleExcelHeader(headerName = "合同总金额（万元）", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String applyCreditAmount;

    //现金流入时间
    @SimpleExcelHeader(headerName = "现金流入时间", headerOrder = 40, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate flowInDate;

    //本金
    @SimpleExcelHeader(headerName = "本金（万元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String principal;

    //利息
    @SimpleExcelHeader(headerName = "利息（万元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String interest;

    //首期租金
    @SimpleExcelHeader(headerName = "首期租金（万元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String downPayment;

    //保证金
    @SimpleExcelHeader(headerName = "保证金（万元）", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String earnestMoney;

    //服务费/咨询费
    @SimpleExcelHeader(headerName = "服务费/咨询费/手续费/其他（万元）", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String consultingFee;

    //合计金额
    @SimpleExcelHeader(headerName = "合计金额（万元）", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String totalAmount;

    //预估流入现金流合计
    @SimpleExcelHeader(headerName = "预计流入现金流合计（万元）", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private String estimatedCashInFlowTotal;

}
