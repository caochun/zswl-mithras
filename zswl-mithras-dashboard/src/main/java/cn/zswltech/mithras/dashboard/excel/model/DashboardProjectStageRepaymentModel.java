package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageRepaymentModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "已收租金", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String totalCollectionRent;

    @SimpleExcelHeader(headerName = "剩余金额", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String totalRentBalance;

    @SimpleExcelHeader(headerName = "存量风险敞口", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String stockRiskExposure;
}
