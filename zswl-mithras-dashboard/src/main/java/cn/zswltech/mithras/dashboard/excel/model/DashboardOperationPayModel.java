package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationPayModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门名称", headerOrder = 10)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "预算投放金额(万元)", headerOrder = 20, columnStyle = ColumnStyleEnum.MONEY)
    private String payPlanAmount;

    @SimpleExcelHeader(headerName = "实际投放金额(万元)", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String payAmount;

    @SimpleExcelHeader(headerName = "差额(万元)", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String difference;

    @SimpleExcelHeader(headerName = "达成率(%)", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String finishRate;

}