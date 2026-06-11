package cn.zswltech.mithras.finance.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author luyujie
 * @date 2026/1/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StampDutyExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "申报税目名称")
    private String name;

    @SimpleExcelHeader(headerName = "合同编号/融资编号")
    private String belongCode;

    @SimpleExcelHeader(headerName = "客户名称/融资机构")
    private String clientName;

    @SimpleExcelHeader(headerName = "业务部门")
    private String belongOrgName;

    @SimpleExcelHeader(headerName = "借据编号")
    private String receiptCode;

    @SimpleExcelHeader(headerName = "实际起租日", columnStyle = ColumnStyleEnum.DATE)
    private LocalDate startDate;

    @SimpleExcelHeader(headerName = "不含税租金", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal rent;

    @SimpleExcelHeader(headerName = "不含税手续费", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal commission;

    @SimpleExcelHeader(headerName = "不含税咨询费", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal consultingFee;

    @SimpleExcelHeader(headerName = "金额", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal amount;

    @SimpleExcelHeader(headerName = "印花税率（%）")
    private String taxRate;

    @SimpleExcelHeader(headerName = "印花税", columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal stampDuty;

}
