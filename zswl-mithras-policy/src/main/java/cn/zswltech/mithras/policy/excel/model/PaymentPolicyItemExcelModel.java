package cn.zswltech.mithras.policy.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @ClassName PaymentPolicyItemExcelModel
 * @Description 付款申请保单信息列表
 * @Author jackerhe
 * @Date 2022/12/29 7:11 下午
 * @Version 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentPolicyItemExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "保单编号", headerOrder = 10)
    private String policyCode;

    @SimpleExcelHeader(headerName = "保险机构", headerOrder = 20)
    private String insuranceCompany;

    @SimpleExcelHeader(headerName = "险种", headerOrder = 30)
    private String policyType;

    @SimpleExcelHeader(headerName = "保单金额", headerOrder = 40)
    private BigDecimal policyAmount;

    @SimpleExcelHeader(headerName = "标识信息", headerOrder = 45)
    private String identificationInformation;

    @SimpleExcelHeader(headerName = "保险起始日", headerOrder = 50)
    private LocalDate insuranceStartDate;

    @SimpleExcelHeader(headerName = "保险到期日", headerOrder = 60)
    private LocalDate insuranceEndDate;

    @SimpleExcelHeader(headerName = "是否续保", headerOrder = 70)
    private String renewInsuranceFlag;

    @SimpleExcelHeader(headerName = "备注", headerOrder = 80)
    private String remark;

}
