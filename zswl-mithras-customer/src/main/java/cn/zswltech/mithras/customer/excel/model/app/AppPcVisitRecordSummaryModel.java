package cn.zswltech.mithras.customer.excel.model.app;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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