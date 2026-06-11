package cn.zswltech.mithras.budget.controller;

import cn.zswltech.mithras.api.budget.EclExecutePredictBaseInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.application.EclExecutePredictBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
* @description 资产减值预测表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclExecutePredictBaseInfoController implements EclExecutePredictBaseInfoApi {

    @Resource
    private EclExecutePredictBaseInfoApplicationService eclExecutePredictBaseInfoService;

    @Override
    public R<Long> add(EclExecutePredictBaseInfoAddREQ req) {
        return R.ok(eclExecutePredictBaseInfoService.createFromManual(req));
    }

    @Override
    public R<PageR<EclExecutePredictBaseInfoListRSP>> list(EclExecutePredictBaseInfoListREQ req){
        return R.ok(eclExecutePredictBaseInfoService.pageList(req));
    }

    @Override
    public R<Void> remove(EclExecutePredictBaseInfoRemoveREQ req){
        eclExecutePredictBaseInfoService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> calculation(@Valid EclExecutePredictBaseInfoDetailREQ req) {
        eclExecutePredictBaseInfoService.calculationAsync(req.getId());
        return R.ok();
    }

}
