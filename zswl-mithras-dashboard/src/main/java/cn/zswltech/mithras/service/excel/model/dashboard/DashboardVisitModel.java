package cn.zswltech.mithras.service.excel.model.dashboard;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DashboardVisitModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "姓名", headerOrder = 20)
    private String projManagerName;

    @SimpleExcelHeader(headerName = "业务组分类", headerOrder = 30)
    private String businessGroup;

    @SimpleExcelHeader(headerName = "拜访日期", headerOrder = 40, columnStyle = ColumnStyleEnum.DATE)
    private LocalDateTime visitTime;

    @SimpleExcelHeader(headerName = "拜访客户数", headerOrder = 120)
    private Integer visitClientCount;
}
