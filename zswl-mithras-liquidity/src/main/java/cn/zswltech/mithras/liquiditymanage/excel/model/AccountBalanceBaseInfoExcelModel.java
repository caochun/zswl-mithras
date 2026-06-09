package cn.zswltech.mithras.liquiditymanage.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户余额表导入实体类
 *
 * @author chenyifei
 * @since 2024/12/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountBalanceBaseInfoExcelModel extends ExcelModel {

    /**
     * 数据时点
     */
    @SimpleExcelHeader(headerName = "日期", headerOrder = 0, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate date;

    /**
     * 开户银行
     */
    @SimpleExcelHeader(headerName = "开户银行", headerOrder = 10, columnStyle = ColumnStyleEnum.DEFAULT)
    private String accountBank;

    /**
     * 银行账号
     */
    @SimpleExcelHeader(headerName = "银行账号", headerOrder = 20, columnStyle = ColumnStyleEnum.DEFAULT)
    private String accountNumber;

    /**
     * 账户性质
     */
    @SimpleExcelHeader(headerName = "账户性质", headerOrder = 30, columnStyle = ColumnStyleEnum.DEFAULT)
    private String accountType;

    /**
     * 提款(编辑字段)
     */
    @SimpleExcelHeader(headerName = "提款", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal drawingsAmount;

    /**
     * 租金回流
     */
    @SimpleExcelHeader(headerName = "租金回流", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal rentReflowAmount;

    /**
     * 其他流入(编辑字段)
     */
    @SimpleExcelHeader(headerName = "其它流入", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal otherFlowAmount;

    /**
     * 投放(编辑字段)
     */
    @SimpleExcelHeader(headerName = "投放", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal paymentAmount;

    /**
     * 还本付息
     */
    @SimpleExcelHeader(headerName = "还本付息", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal repayAmount;

    /**
     * 还本付息-调整(编辑字段)
     */
    @SimpleExcelHeader(headerName = "还本付息（调整）", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal repayEditAmount;

    /**
     * 刚性支出(编辑字段)
     */
    @SimpleExcelHeader(headerName = "刚性支出", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal mustExpenseAmount;

    /**
     * 其他支出(编辑字段)
     */
    @SimpleExcelHeader(headerName = "其它支出", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal otherExpenseAmount;

    /**
     * 结余-预估
     */
    @SimpleExcelHeader(headerName = "结余（预估）", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal estimateBalanceAmount;

    /**
     * 结余受限-预估
     */
    @SimpleExcelHeader(headerName = "结余受限（预估）", headerOrder = 130, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal estimateBalanceLimitAmount;

    /**
     * 结余-实际(编辑字段)
     */
    @SimpleExcelHeader(headerName = "结余（实际）", headerOrder = 140, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal actualBalanceAmount;

    /**
     * 差额
     */
    @SimpleExcelHeader(headerName = "差异", headerOrder = 150, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal diffAmount;

}
