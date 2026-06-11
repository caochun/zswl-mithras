package cn.zswltech.mithras.customer.excel;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class ClientTransferExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;
    @SimpleExcelHeader(headerName = "客户类型")
    private String clientType;
    @SimpleExcelHeader(headerName = "资产五级分类结果")
    private String assertClassifyResult;
    @SimpleExcelHeader(headerName = "当前所属主办")
    private String belongSponsorName;
    @SimpleExcelHeader(headerName = "当前所属部门名称")
    private String belongDeptName;
    @SimpleExcelHeader(headerName = "项目名称")
    private String projectName;
    @SimpleExcelHeader(headerName = "项目编号")
    private String projCode;
    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;
    @SimpleExcelHeader(headerName = "业务类型")
    private String bizType;
    @SimpleExcelHeader(headerName = "新的主办")
    private String toSponsorName;
    @SimpleExcelHeader(headerName = "新的部门")
    private String toBelongDeptName;
    @SimpleExcelHeader(headerName = "新的协办")
    private String toCosponsorNames;
    @SimpleExcelHeader(headerName = "项目流状态")
    private String projStatus;
    @SimpleExcelHeader(headerName = "项目资料归档状态")
    private String projArchiveStatus;
    @SimpleExcelHeader(headerName = "风险移交比例")
    private Integer riskTransferValue;
    @SimpleExcelHeader(headerName = "收益移交比例")
    private Integer incomeTransferValue;

}
