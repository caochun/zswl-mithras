package cn.zswltech.mithras.application.orchestration.adapter.workbench.cardcal;

import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.workbench.application.cardcal.WorkbenchCardProjReviewPort;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchProjReviewPrice;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchCardProjReviewPortAdapter implements WorkbenchCardProjReviewPort {
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjReviewAocPriceLibService aocPriceLibService;

    @Override
    public List<WorkbenchProjReviewPrice> listTakeEffectReviewPrices(LocalDateTime startTime) {
        Set<Long> projReviewIds = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .ge(BaseModel::getCreateTime, startTime)
                        .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT"))
                .stream()
                .map(ProjReviewBaseInfo::getId)
                .collect(Collectors.toSet());
        List<WorkbenchProjReviewPrice> prices = new ArrayList<>();
        if (projReviewIds.isEmpty()) {
            return prices;
        }
        for (ProjReviewLeasePriceLib priceLib : leasePriceLibService.listNewestByProjReviewIds(projReviewIds)) {
            prices.add(new WorkbenchProjReviewPrice(priceLib.getConsultingFee(), priceLib.getApplyCreditAmount()));
        }
        for (ProjReviewFactoringPriceLib priceLib : factoringPriceLibService.listNewestByProjReviewIds(projReviewIds)) {
            prices.add(new WorkbenchProjReviewPrice(priceLib.getConsultingFee(), priceLib.getApplyCreditAmount()));
        }
        for (ProjReviewAocPriceLib priceLib : aocPriceLibService.listNewestByProjReviewIds(projReviewIds)) {
            prices.add(new WorkbenchProjReviewPrice(priceLib.getConsultingFee(), priceLib.getApplyCreditAmount()));
        }
        return prices;
    }
}
