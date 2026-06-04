package cn.zswltech.mithras.service.fund.direct.excel;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundDirectFinancingRepayActualExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "现金流编号", headerOrder = 0)
    private String cashFlowCode;
    /**
     * 还款日期
     */
    @SimpleExcelHeader(headerName = "日期", headerOrder = 10, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate repayDate;

    /**
     * 还款期项
     */
    @SimpleExcelHeader(headerName = "期项", headerOrder = 20)
    private Integer repayPhase;

    /**
     * 本金金额（元）
     */
    @SimpleExcelHeader(headerName = "本金（元）", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal principleAmount;

    /**
     * 利息金额（元）
     */
    @SimpleExcelHeader(headerName = "利息（元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal interestAmount;

    /**
     * 还款金额（元）
     */
    @SimpleExcelHeader(headerName = "应还总额（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal repayAmount;

    /**
     * 剩余未还本金金额（元）
     */
    @SimpleExcelHeader(headerName = "剩余未还本金（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingPrincipleAmount;

    /**
     * 预付差额
     */
    @SimpleExcelHeader(headerName = "预付差额", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal prePayDifference;

    public void check() {
        Assert.notNull(repayDate, () -> MithrasException.newException("日期不能为空"));
        Assert.notNull(repayPhase, () -> MithrasException.newException("期项不能为空"));
        if (Objects.nonNull(principleAmount)) {
            Assert.isTrue(principleAmount.compareTo(BigDecimal.ZERO) >= 0, () -> MithrasException.newException(String.format("第%s期本金必须大于等于0", repayPhase)));
            Assert.isTrue(StringUtil.getDecimalDigits(principleAmount.toPlainString()) <= 2, () -> MithrasException.newException(String.format("请将第%s期本金保留至2位小数后再导入", repayPhase)));
        }
        if (Objects.nonNull(interestAmount)) {
            Assert.isTrue(interestAmount.compareTo(BigDecimal.ZERO) >= 0, () -> MithrasException.newException(String.format("第%s期利息必须大于等于0", repayPhase)));
            Assert.isTrue(StringUtil.getDecimalDigits(interestAmount.toPlainString()) <= 2, () -> MithrasException.newException(String.format("请将第%s期利息保留至2位小数后再导入", repayPhase)));
        }
        if (Objects.nonNull(repayAmount)) {
            Assert.isTrue(repayAmount.compareTo(BigDecimal.ZERO) >= 0, () -> MithrasException.newException(String.format("第%s期应还总额必须大于等于0", repayPhase)));
            Assert.isTrue(StringUtil.getDecimalDigits(repayAmount.toPlainString()) <= 2, () -> MithrasException.newException(String.format("请将第%s期应还总额保留至2位小数后再导入", repayPhase)));
        }
        if (Objects.nonNull(remainingPrincipleAmount)) {
            Assert.isTrue(remainingPrincipleAmount.compareTo(BigDecimal.ZERO) >= 0, () -> MithrasException.newException(String.format("第%s期剩余未还本金必须大于等于0", repayPhase)));
            Assert.isTrue(StringUtil.getDecimalDigits(remainingPrincipleAmount.toPlainString()) <= 2, () -> MithrasException.newException(String.format("请将第%s期剩余未还本金保留至2位小数后再导入", repayPhase)));
        }
    }
}
