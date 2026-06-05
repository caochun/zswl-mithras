package cn.zswltech.mithras.policy.excel.model;

import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 保单台账 列表
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PolicyLedgerExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "保险单号", headerOrder = 10)
    private String policyCode;

    @SimpleExcelHeader(headerName = "续保关系", headerOrder = 20)
    private String renewalRelationship;

    @SimpleExcelHeader(headerName = "保险机构", headerOrder = 30)
    private String insuranceCompany;

    @SimpleExcelHeader(headerName = "保险起始日", headerOrder = 40)
    private String insuranceStartDate;

    @SimpleExcelHeader(headerName = "保险到期日", headerOrder = 50)
    private String insuranceEndDate;

    @SimpleExcelHeader(headerName = "标识信息", headerOrder = 55)
    private String identificationInformation;

    @SimpleExcelHeader(headerName = "是否续保", headerOrder = 60)
    private String renewInsuranceFlag;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 70)
    private String clientName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 80)
    private String contractCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 90)
    private String projName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 100)
    private String projSponsorUser;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 104)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "逾期天数", headerOrder = 107)
    private Long overdueDays;

    @SimpleExcelHeader(headerName = "续保保单反馈日", headerOrder = 110)
    private LocalDate renewalPolicyFeedbackDate;

    @SimpleExcelHeader(headerName = "状态", headerOrder = 120)
    private String policyStatus;

    @SimpleExcelHeader(headerName = "创建时间", headerOrder = 130)
    private String createTime;

}
