package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchProjReviewPrice;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkbenchCardProjReviewPort {
    List<WorkbenchProjReviewPrice> listTakeEffectReviewPrices(LocalDateTime startTime);
}
