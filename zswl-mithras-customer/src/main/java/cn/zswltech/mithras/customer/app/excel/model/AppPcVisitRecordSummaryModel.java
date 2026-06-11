package cn.zswltech.mithras.customer.app.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class AppPcVisitRecordSummaryModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "人员", headerOrder = 20)
    private String createdName;

    @SimpleExcelHeader(headerName = "拜访总次数", headerOrder = 20)
    private String visitCount;

    @SimpleExcelHeader(headerName = "拜访总家数", headerOrder = 20)
    private String clientCount;

}