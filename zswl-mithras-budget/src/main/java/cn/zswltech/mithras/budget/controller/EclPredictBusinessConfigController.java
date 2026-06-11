package cn.zswltech.mithras.budget.controller;

import cn.zswltech.mithras.api.budget.EclPredictBusinessConfigApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.application.EclPredictBusinessConfigApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
* @description ecl_预测业务配置表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclPredictBusinessConfigController implements EclPredictBusinessConfigApi {

    @Resource
    private EclPredictBusinessConfigApplicationService eclPredictBusinessConfigService;

    @Override
    public R<Void> modify(EclPredictBusinessConfigModifyREQ req){
        eclPredictBusinessConfigService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclPredictBusinessConfigListRSP>> list(@Valid EclPredictBusinessConfigListREQ req) {
        return R.ok(eclPredictBusinessConfigService.pageList(req));
    }

    @Override
    public R<EclPredictBusinessConfigDetailRSP> detail(@Valid EclPredictBusinessConfigDetailREQ req) {
        return R.ok(eclPredictBusinessConfigService.detailRsp(req));
    }



}
