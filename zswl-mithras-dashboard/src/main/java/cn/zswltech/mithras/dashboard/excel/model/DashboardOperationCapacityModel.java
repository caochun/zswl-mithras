package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationCapacityModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门名称", headerOrder = 10)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "调整人数(人)", headerOrder = 20)
    private String adjustPersonSum;

    @SimpleExcelHeader(headerName = "总个数", headerOrder = 30)
    private String projSum;

    @SimpleExcelHeader(headerName = "总金额(万元)", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String amountSum;

    @SimpleExcelHeader(headerName = "人均个数", headerOrder = 50)
    private String personAverageProjSum;

    @SimpleExcelHeader(headerName = "人均金额(万元)", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String personAverageAmountSum;

    @SimpleExcelHeader(headerName = "件均金额(万元)", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String pieceAverageAmountSum;


}