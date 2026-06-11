package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardFundFinanceCreditInfoModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "授信编号", headerOrder = 20)
    private String creditCode;

    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 30)
    private String orgName;

    @SimpleExcelHeader(headerName = "授信产品", headerOrder = 50)
    private String financingBizTypeDisplay;

    @SimpleExcelHeader(headerName = "授信额度（万元）", headerOrder = 60, columnStyle = ColumnStyleEnum.DATE)
    private String creditTotalAmount;

    @SimpleExcelHeader(headerName = "已用额度（万元）", headerOrder = 70, columnStyle = ColumnStyleEnum.DATE)
    private String creditUsedAmount;

    @SimpleExcelHeader(headerName = "是否可循环", headerOrder = 80)
    private String isCycle;

    @SimpleExcelHeader(headerName = "授信到期日", headerOrder = 90, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate deadline;
}