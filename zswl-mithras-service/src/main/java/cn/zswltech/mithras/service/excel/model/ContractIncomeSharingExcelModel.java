package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yupengfei
 * @date 2024/6/11 15:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractIncomeSharingExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "借据编号")
    private String receiptCode;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    @SimpleExcelHeader(headerName = "日期")
    private String incomeDate;

    @SimpleExcelHeader(headerName = "长期应收款期初余额")
    private String beginOfTermBalance;

    @SimpleExcelHeader(headerName = "当天应收租金")
    private String rent;

    @SimpleExcelHeader(headerName = "当天确认收入")
    private String income;

    @SimpleExcelHeader(headerName = "不含税收入")
    private String incomeWithoutTax;

    @SimpleExcelHeader(headerName = "税额")
    private String tax;

    @SimpleExcelHeader(headerName = "长期应收余额")
    private String endOfTermBalance;

    @SimpleExcelHeader(headerName = "日折现率")
    private String dailyDiscountRate;

    @SimpleExcelHeader(headerName = "收入是否确认")
    private String isConfirmed;
}
