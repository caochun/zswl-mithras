package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.afterlease.application.RentCollectionIndexApplicationService;
import cn.zswltech.mithras.api.afterlease.RentCollectionIndexApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.PenaltyReduceDetailModifyREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class RentCollectionIndexController implements RentCollectionIndexApi {

    @Resource
    private RentCollectionIndexApplicationService rentCollectionIndexApplicationService;

    @Override
    public R<PageR<RentCollectionListRSP>> indexList(@Valid RentCollectionListREQ req) {
        return rentCollectionIndexApplicationService.indexList(req);
    }

    @Override
    public R<Long> penaltyReductionEffect(@Valid RentCollectionPenaltyReduceREQ req) {
        return rentCollectionIndexApplicationService.penaltyReductionEffect(req);
    }

    @Override
    public R<Void> penaltyReductionModify(@Valid PenaltyReduceDetailModifyREQ req) {
        return rentCollectionIndexApplicationService.penaltyReductionModify(req);
    }

    @Override
    public R<RentCollectionPenaltyReduceDetailRSP> penaltyReductionList(@Valid RentCollectionPenaltyReduceListREQ req) {
        return rentCollectionIndexApplicationService.penaltyReductionList(req);
    }
}
