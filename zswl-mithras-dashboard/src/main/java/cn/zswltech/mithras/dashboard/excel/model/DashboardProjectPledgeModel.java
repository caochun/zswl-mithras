package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPledgeModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 30)
    private String businessType;

    @SimpleExcelHeader(headerName = "投放金额(元)", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String totalPayAmount;

    @SimpleExcelHeader(headerName = "剩余租金金额（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String residualRent;

    @SimpleExcelHeader(headerName = "剩余本金金额（元）", headerOrder = 55, columnStyle = ColumnStyleEnum.MONEY)
    private String remainingPrincipal;

    @SimpleExcelHeader(headerName = "质押/监管情况", headerOrder = 60)
    private String pledgeStatus;

    @SimpleExcelHeader(headerName = "户名", headerOrder = 61)
    private String accountName;

    @SimpleExcelHeader(headerName = "开户行", headerOrder = 62)
    private String accountBank;

    @SimpleExcelHeader(headerName = "账号", headerOrder = 63)
    private String accountNumber;

    @SimpleExcelHeader(headerName = "机构名称/产品名称", headerOrder = 70)
    private String orgName;

    @SimpleExcelHeader(headerName = "融资状态", headerOrder = 80)
    private String financingStatus;

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 90)
    private String financingCode;


}