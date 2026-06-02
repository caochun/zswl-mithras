package cn.zswltech.mithras.others.service.util;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.PayType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.service.bo.CashFlowBO;
import cn.zswltech.mithras.service.service.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.service.util.FinancialUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
public class FinancialUtilTest {


    @Test
    public void testCalcCashFlow() {
        CashFlowCalculateBO param = new CashFlowCalculateBO();
        param.setConsultingFee(100_000_0000L);
        param.setDownPayment(100_000_0000L);
        param.setEarnestMoney(10_000_0000L);
        param.setCreditAmount(1_000_000_0000L);
        param.setInterestRate(10_000);
        param.setNominalPrice(100_000_0000L);
        param.setRepayTimes(12);
        param.setRepayRate(RepayRateEnum.MONTH.name());
        LocalDate startDate = LocalDate.of(2023, 7, 1);
        param.setStartDate(startDate);
        //等额租金+后付
        param.setRentalCalcType(RepayCalcType.DEBX.name());
        param.setPayType(PayType.AFTERWARD.name());
        List<CashFlowBO> result = FinancialUtil.calcCashFlow(param);
        Assertions.assertEquals(result.get(1).getCashFlowDate(), startDate.plusMonths(1));
        //等额租金+先付
        param.setRentalCalcType(RepayCalcType.DEBX.name());
        param.setPayType(PayType.ADVANCED.name());
        result = FinancialUtil.calcCashFlow(param);
        Assertions.assertEquals(result.get(1).getCashFlowDate(), startDate);
        System.out.println(JSONUtil.toJsonStr(result, new JSONConfig().setDateFormat("yyyy-MM-dd")));
        //等额本金+后付
        param.setRentalCalcType(RepayCalcType.DEBJ.name());
        param.setPayType(PayType.AFTERWARD.name());
        result = FinancialUtil.calcCashFlow(param);
        Assertions.assertEquals(result.get(1).getCashFlowDate(), startDate.plusMonths(1));
        //等额本金+先付
        param.setRentalCalcType(RepayCalcType.DEBJ.name());
        param.setPayType(PayType.ADVANCED.name());
        result = FinancialUtil.calcCashFlow(param);
        Assertions.assertEquals(result.get(1).getCashFlowDate(), startDate);
    }
}
