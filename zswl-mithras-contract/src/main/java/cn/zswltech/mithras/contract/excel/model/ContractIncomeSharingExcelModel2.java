package cn.zswltech.mithras.contract.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author yupengfei
 * @date 2024/6/12 10:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractIncomeSharingExcelModel2 extends ExcelModel {

    @SimpleExcelHeader(headerName = "借据编号")
    private String receiptCode;

    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称")
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    @SimpleExcelHeader(headerName = "合同状态")
    private String contractStatus;

    @SimpleExcelHeader(headerName = "起租日期")
    private LocalDate startRentTime;

    @SimpleExcelHeader(headerName = "收入分摊方式")
    private String incomeConfirmType;

    @SimpleExcelHeader(headerName = "是否逾期")
    private String overdueType;

    @SimpleExcelHeader(headerName = "逾期开始日期")
    private LocalDate overdueStartTime;

    @SimpleExcelHeader(headerName = "含税收入合计")
    private String incomeSum;

    @SimpleExcelHeader(headerName = "不含税收入合计")
    private String incomeWithoutTaxSum;

    @SimpleExcelHeader(headerName = "已确认收入合计")
    private String confirmedIncomeSum;

    @SimpleExcelHeader(headerName = "未确认收入合计")
    private String unconfirmedIncomeSum;

    @SimpleExcelHeader(headerName = "月度含税收入合计")
    private String monthlyIncomeSum;

    @SimpleExcelHeader(headerName = "月度不含税收入合计")
    private String monthlyIncomeWithoutTaxSum;

    @SimpleExcelHeader(headerName = "月度已确认收入合计")
    private String monthlyConfirmedIncomeWithoutTaxSum;

    @SimpleExcelHeader(headerName = "业务分类")
    private String businessType;

    @SimpleExcelHeader(headerName = "项目主办名称")
    private String sponsorUserName;

    @SimpleExcelHeader(headerName = "所属部门名称")
    private String belongDeptName;
}
