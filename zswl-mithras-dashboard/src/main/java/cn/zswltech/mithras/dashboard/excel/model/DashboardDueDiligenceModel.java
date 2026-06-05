package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DashboardDueDiligenceModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "项目编号", headerOrder = 30)
    private String projCode;

    @SimpleExcelHeader(headerName = "金额（万元）", headerOrder = 40)
    private Double amount;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 45)
    private String projManagerName;

    @SimpleExcelHeader(headerName = "风控经理", headerOrder = 50)
    private String riskManagerName;

    @SimpleExcelHeader(headerName = "业务组分类", headerOrder = 55)
    private String businessGroup;

    @SimpleExcelHeader(headerName = "尽调时间", headerOrder = 60, columnStyle = ColumnStyleEnum.DATE)
    private LocalDateTime dueDiligenceTime;

    @SimpleExcelHeader(headerName = "备注", headerOrder = 80)
    private String comment;

}
