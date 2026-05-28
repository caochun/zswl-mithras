package cn.zswltech.mithras.service.excel.model;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CashFlowExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "日期", headerOrder = 10, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate cashFlowDate;

    @SimpleExcelHeader(headerName = "期项", headerOrder = 20)
    private Integer cashFlowPhase;

    @SimpleExcelHeader(headerName = "现金流金额（元）", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal cashFlowAmount;

    @SimpleExcelHeader(headerName = "租金（元）", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal rent;

    @SimpleExcelHeader(headerName = "本金（元）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal principal;

    @SimpleExcelHeader(headerName = "利息（元）", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal interest;

    @SimpleExcelHeader(headerName = "剩余本金（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingPrincipal;

    public void projReviewCashFlowCheck() {
        if (Objects.nonNull(cashFlowPhase)) {
            Assert.isTrue(cashFlowPhase >= 0, () -> MithrasException.newException("期项必须大于等于0"));
        }
        this.checkEstimateRent();
    }

    public void checkBasicRent() {
        Assert.notNull(cashFlowPhase, () -> MithrasException.newException("期项不能为空"));
        if (Objects.nonNull(rent)) {
            Assert.isTrue(rent.equals(Optional.ofNullable(principal).orElse(BigDecimal.ZERO).add(Optional.ofNullable(interest).orElse(BigDecimal.ZERO))), () -> MithrasException.newException("第" + cashFlowPhase + "期租金不等于本金+利息"));
        }
    }

    public void checkEstimateRent() {
        Assert.notNull(cashFlowDate, () -> MithrasException.newException("日期不能为空"));
//        Assert.notNull(cashFlowPhase, () -> MithrasException.newException("期项不能为空"));
        Assert.notNull(remainingPrincipal, () -> MithrasException.newException("剩余本金不能为空"));
        this.checkActualRentWithoutPaymentCode();
    }

    public void checkActualRentWithoutPaymentCode() {
//        Assert.notNull(cashFlowPhase, () -> MithrasException.newException("期项不能为空"));
        this.checkBasicRent();
        if (Objects.nonNull(rent)) {
//            Assert.isTrue(rent.compareTo(new BigDecimal(0)) >= 0, () -> MithrasException.newException("租金必须大于等于0"));
            Assert.isTrue(StringUtil.getDecimalDigits(rent.toPlainString()) <= 2, () -> MithrasException.newException("请将租金金额保留至2位小数后再导入"));
        }
        if (Objects.nonNull(principal)) {
            Assert.isTrue(principal.compareTo(new BigDecimal(0)) >= 0, () -> MithrasException.newException("本金必须大于等于0"));
            Assert.isTrue(StringUtil.getDecimalDigits(principal.toPlainString()) <= 2, () -> MithrasException.newException("请将本金金额保留至2位小数后再导入"));
        }
        if (Objects.nonNull(interest)) {
//            Assert.isTrue(interest.compareTo(new BigDecimal(0)) >= 0, () -> MithrasException.newException("利息必须大于等于0"));
            Assert.isTrue(StringUtil.getDecimalDigits(interest.toPlainString()) <= 2, () -> MithrasException.newException("请将利息金额保留至2位小数后再导入"));
        }
        if (Objects.nonNull(remainingPrincipal)) {
            Assert.isTrue(remainingPrincipal.compareTo(new BigDecimal(0)) >= 0, () -> MithrasException.newException("剩余本金必须大于等于0"));
            Assert.isTrue(StringUtil.getDecimalDigits(remainingPrincipal.toPlainString()) <= 2, () -> MithrasException.newException("请将剩余本金金额保留至2位小数后再导入"));
        }
    }

    public void checkActualRentWithPaymentCode() {
        Assert.notNull(cashFlowDate, () -> MithrasException.newException("日期不能为空"));
//        Assert.notNull(cashFlowPhase, () -> MithrasException.newException("期项不能为空"));
        Assert.notNull(cashFlowAmount, () -> MithrasException.newException("现金流金额不能为空"));
        Assert.notNull(remainingPrincipal, () -> MithrasException.newException("剩余本金不能为空"));
        this.checkActualRentWithoutPaymentCode();
//        if (Objects.nonNull(rent) && Objects.nonNull(principal) && Objects.nonNull(interest)) {
//            Assert.isTrue(rent.equals(principal.add(interest)), () -> MithrasException.newException("第" + cashFlowPhase + "期租金不等于本金+利息"));
//        }
    }
}
