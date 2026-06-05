package cn.zswltech.mithras.policy.excel.model;

import cn.zswltech.mithras.service.excel.model.ExcelModel;
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
public class PolicyMaintenanceExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 10)
    private String contractCode;

    @SimpleExcelHeader(headerName = "剩余未还本金(元)", headerOrder = 13)
    private BigDecimal remainingUnpaidPrincipal;

    @SimpleExcelHeader(headerName = "合同到期日", headerOrder = 17, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate contractExpirationDate;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 20)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 30)
    private String projName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 40)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 50)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "保单编号", headerOrder = 60)
    private String policyCode;

    @SimpleExcelHeader(headerName = "标识信息", headerOrder = 63)
    private String identificationInformation;

    @SimpleExcelHeader(headerName = "逾期天数", headerOrder = 65)
    private Long overdueDays;

    @SimpleExcelHeader(headerName = "保单金额", headerOrder = 68)
    private BigDecimal policyAmount;

    @SimpleExcelHeader(headerName = "保单机构", headerOrder = 70)
    private String insuranceCompany;

    @SimpleExcelHeader(headerName = "险种", headerOrder = 80)
    private String policyType;

    @SimpleExcelHeader(headerName = "保险起始日", headerOrder = 90)
    private String insuranceStartDate;

    @SimpleExcelHeader(headerName = "保险到期日", headerOrder = 100)
    private String insuranceEndDate;

}
