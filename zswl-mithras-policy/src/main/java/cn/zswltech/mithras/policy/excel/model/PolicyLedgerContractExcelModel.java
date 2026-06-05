package cn.zswltech.mithras.policy.excel.model;

import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 保单台账 列表
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PolicyLedgerContractExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "保险单号", headerOrder = 10)
    private String policyCode;

    @SimpleExcelHeader(headerName = "续保关系", headerOrder = 15)
    private String renewalRelationship;

    @SimpleExcelHeader(headerName = "保险机构", headerOrder = 20)
    private String insuranceCompany;

    @SimpleExcelHeader(headerName = "险种", headerOrder = 30)
    private String policyType;

    @SimpleExcelHeader(headerName = "保单金额", headerOrder = 40)
    private String policyAmount;

    @SimpleExcelHeader(headerName = "标识信息", headerOrder = 45)
    private String identificationInformation;

    @SimpleExcelHeader(headerName = "保险起始日", headerOrder = 50)
    private String insuranceStartDate;

    @SimpleExcelHeader(headerName = "保险到期日", headerOrder = 60)
    private String insuranceEndDate;

    @SimpleExcelHeader(headerName = "是否续保", headerOrder = 70)
    private String renewInsuranceFlag;

    @SimpleExcelHeader(headerName = "备注", headerOrder = 80)
    private String remark;

    @SimpleExcelHeader(headerName = "创建人", headerOrder = 90)
    private String createName;

    @SimpleExcelHeader(headerName = "创建时间", headerOrder = 100)
    private String createTime;

}
