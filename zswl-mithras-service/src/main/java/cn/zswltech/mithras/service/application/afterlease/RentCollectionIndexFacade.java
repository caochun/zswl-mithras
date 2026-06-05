package cn.zswltech.mithras.service.application.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.afterlease.application.RentCollectionIndexApplicationService;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.service.afterlese.PenaltyReduceBaseInfoService;
import cn.zswltech.mithras.service.service.afterlese.impl.RentCollectionIndexServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:28 PM
 */
@Service
public class RentCollectionIndexFacade implements RentCollectionIndexApplicationService {

    @Resource
    private RentCollectionIndexServiceImpl rentCollectionIndexService;

    @Resource
    private PenaltyReduceBaseInfoService penaltyReduceBaseInfoService;

    @Override
    public R<PageR<RentCollectionListRSP>> indexList(RentCollectionListREQ req) {
        return R.ok(rentCollectionIndexService.indexList(req));
    }

    @Override
    public R<Long> penaltyReductionEffect(@Valid RentCollectionPenaltyReduceREQ req) {
        return R.ok(penaltyReduceBaseInfoService.penaltyReductionEffect(req));
    }

    @Override
    public R<Void> penaltyReductionModify(@Valid PenaltyReduceDetailModifyREQ req) {
        penaltyReduceBaseInfoService.penaltyReductionModify(req);
        return R.ok();
    }

    @Override
    public R<RentCollectionPenaltyReduceDetailRSP> penaltyReductionList(@Valid RentCollectionPenaltyReduceListREQ req) {
        return R.ok(penaltyReduceBaseInfoService.penaltyReductionList(req));
    }

}
