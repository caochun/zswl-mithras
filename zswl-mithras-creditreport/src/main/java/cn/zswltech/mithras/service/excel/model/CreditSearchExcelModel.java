package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 征信查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreditSearchExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "查询编号", headerOrder = 10)
    private String creditCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 20)
    private String clientName;

    @SimpleExcelHeader(headerName = "统一社会信用代码", headerOrder = 30)
    private String cscCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 40)
    private String projectName;

    @SimpleExcelHeader(headerName = "查询原因", headerOrder = 50)
    private String searchReason;

    @SimpleExcelHeader(headerName = "申请人", headerOrder = 60)
    private String applyUserName;

    @SimpleExcelHeader(headerName = "申请部门", headerOrder = 70)
    private String applyOrgName;

    @SimpleExcelHeader(headerName = "申请状态", headerOrder = 80)
    private String applyStatus;

    @SimpleExcelHeader(headerName = "申请通过时间", headerOrder = 90)
    private LocalDateTime applyTime;

    @SimpleExcelHeader(headerName = "查询状态", headerOrder = 100)
    private String searchStatus;

    @SimpleExcelHeader(headerName = "查询完成时间", headerOrder = 110)
    private LocalDateTime searchTime;

}
