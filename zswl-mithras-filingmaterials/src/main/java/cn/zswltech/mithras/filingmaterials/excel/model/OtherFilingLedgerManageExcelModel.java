package cn.zswltech.mithras.filingmaterials.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OtherFilingLedgerManageExcelModel extends ExcelModel{
    @SimpleExcelHeader(headerName = "项目名称")
    private String projectName;

    @SimpleExcelHeader(headerName = "项目编号")
    private String projectNumber;

    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "资料类型")
    private String materialsDesc;

    @SimpleExcelHeader(headerName = "业务部门")
    private String startDeptName;

    @SimpleExcelHeader(headerName = "发起人")
    private String startUserName;

    @SimpleExcelHeader(headerName = "审批状态")
    private String approveStatus;

    @SimpleExcelHeader(headerName = "发起时间")
    private String startTime;

    @SimpleExcelHeader(headerName = "结束时间")
    private String endTime;
}


