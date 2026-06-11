package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DashboardAdjustPersonModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "职位", headerOrder = 20)
    private String position;

    @SimpleExcelHeader(headerName = "姓名", headerOrder = 30)
    private String projManagerName;

    @SimpleExcelHeader(headerName = "业务组分类", headerOrder = 40)
    private String businessGroup;

}
