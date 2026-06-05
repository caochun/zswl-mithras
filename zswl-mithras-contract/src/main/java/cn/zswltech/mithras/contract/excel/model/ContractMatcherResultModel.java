package cn.zswltech.mithras.contract.excel.model;

import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractMatcherResultModel extends ExcelModel{
    @SimpleExcelHeader(headerName = "部门", headerOrder = 10)
    private String deptName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "系统内合同编号", headerOrder = 30)
    private String sysContractCode;

    @SimpleExcelHeader(headerName = "主办人员", headerOrder = 40)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 50)
    private String clientName;

    @SimpleExcelHeader(headerName = "系统内客户名称", headerOrder = 60)
    private String sysClientName;

    @SimpleExcelHeader(headerName = "异常原因", headerOrder = 70)
    private String reason;

    @SimpleExcelHeader(headerName = "备注", headerOrder = 80)
    private String remark;
}
