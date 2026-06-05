package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardOperationTimeModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门名称", headerOrder = 10)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "立项均耗", headerOrder = 20)
    private String projEstaTotalTime;

    @SimpleExcelHeader(headerName = "尽调-出具尽调报告均耗", headerOrder = 30)
    private String dueDiligenceTotalTime;

    @SimpleExcelHeader(headerName = "评审均耗", headerOrder = 40)
    private String reviewTotalTime;

    @SimpleExcelHeader(headerName = "纪要均耗", headerOrder = 50)
    private String summaryTotalTime;

    @SimpleExcelHeader(headerName = "立项-投放均耗", headerOrder = 60)
    private String projEstaPaidInTotalTime;

    @SimpleExcelHeader(headerName = "评审-投放均耗", headerOrder = 70)
    private String reviewPaidInTotalTime;


}