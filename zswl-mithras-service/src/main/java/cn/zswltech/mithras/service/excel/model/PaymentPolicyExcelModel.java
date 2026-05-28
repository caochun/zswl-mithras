package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保单台账 列表
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentPolicyExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "保险单号", headerOrder = 10)
    private String policyCode;

    @SimpleExcelHeader(headerName = "保险机构", headerOrder = 20)
    private String insuranceCompany;

    @SimpleExcelHeader(headerName = "保单种类", headerOrder = 30)
    private String policyType;

    @SimpleExcelHeader(headerName = "保单金额(元)", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal policyAmount;

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

    private Long createBy;
    @SimpleExcelHeader(headerName = "创建人", headerOrder = 90)
    private String createByName;

    @SimpleExcelHeader(headerName = "创建时间", headerOrder = 100, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate createTime;

}
