package cn.zswltech.mithras.service.service.workbench.cardcal;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 本年项目手续费率  本年评审通过项目手续费总额/项目总额 报价方案-手续费/申报授信金额
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_058Calculator implements CardCalculator {
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjReviewAocPriceLibService aocPriceLibService;

    @Override
    public String metricCode() {
        return "WCM_058";
    }

    @Override
    public String calculate() {
        Set<Long> projReviewIds = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .ge(BaseModel::getCreateTime, LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay())
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")).stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet());
        if (projReviewIds.isEmpty()) {
            return "0.00";
        }
        List<ProjReviewLeasePriceLib> leasePriceLibs = leasePriceLibService.listNewestByProjReviewIds(projReviewIds);
        List<ProjReviewFactoringPriceLib> factoringPriceLibs = factoringPriceLibService.listNewestByProjReviewIds(projReviewIds);
        List<ProjReviewAocPriceLib> aocPriceLibs = aocPriceLibService.listNewestByProjReviewIds(projReviewIds);

        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal applyCreditAmount = BigDecimal.ZERO;
        for (ProjReviewLeasePriceLib leasePriceLib : leasePriceLibs) {
            fee = fee.add(new BigDecimal(leasePriceLib.getConsultingFee()));
            applyCreditAmount = applyCreditAmount.add(new BigDecimal(leasePriceLib.getApplyCreditAmount()));
        }
        for (ProjReviewFactoringPriceLib factoringPriceLib : factoringPriceLibs) {
            fee = fee.add(new BigDecimal(factoringPriceLib.getConsultingFee()));
            applyCreditAmount = applyCreditAmount.add(new BigDecimal(factoringPriceLib.getApplyCreditAmount()));
        }
        for (ProjReviewAocPriceLib aocPriceLib : aocPriceLibs) {
            fee = fee.add(new BigDecimal(aocPriceLib.getConsultingFee()));
            applyCreditAmount = applyCreditAmount.add(new BigDecimal(aocPriceLib.getApplyCreditAmount()));
        }

        return fee.divide(applyCreditAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L)).setScale(2).toString();
    }
}
