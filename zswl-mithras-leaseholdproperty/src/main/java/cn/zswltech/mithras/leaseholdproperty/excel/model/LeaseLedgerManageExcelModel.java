package cn.zswltech.mithras.leaseholdproperty.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.*;

/**
 * @author yangxiong
 * @description 台账分页返回实体
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeaseLedgerManageExcelModel extends ExcelModel{

    @SimpleExcelHeader(headerName = "租赁物审核流程编号")
    private String leaseAuditFlowNumber;

    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称")
    private String projectName;

    @SimpleExcelHeader(headerName = "项目编号")
    private String projectNumber;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractNumber;

    @SimpleExcelHeader(headerName = "项目主办")
    private String projectOrganizer;

    @SimpleExcelHeader(headerName = "项目协办")
    private String projectCoOrganizer;

    @SimpleExcelHeader(headerName = "租赁物状态")
    private String leaseStatus;

    @SimpleExcelHeader(headerName = "创建时间")
    private String createTime;
}


