package cn.zswltech.mithras.service.controller.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.ModelApi;
import cn.zswltech.mithras.dto.flow.model.ModelBaseREQ;
import cn.zswltech.mithras.dto.flow.model.ModelConfigRSP;
import cn.zswltech.mithras.dto.flow.model.ModelDetailRSP;
import cn.zswltech.mithras.dto.flow.model.ModelListREQ;
import cn.zswltech.mithras.dto.flow.model.ModelListRSP;
import cn.zswltech.mithras.dto.flow.model.SaveModelREQ;
import cn.zswltech.mithras.service.auth.aop.AdminAuthCheck;
import cn.zswltech.mithras.service.service.flow.ModelService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 审批流模型
 *
 * @author wangchuanhao
 * @date 2022/10/24 10:06 PM
 */
@RestController
public class ModelController implements ModelApi {

    @Resource
    private ModelService modelService;

    @Override
    @AdminAuthCheck
    public R<String> save(SaveModelREQ req) {
        return R.ok(modelService.save(req));
    }

    @Override
    public R<ModelDetailRSP> detail(ModelBaseREQ req) {
        return R.ok(modelService.detail(req.getModelId()));
    }

    @Override
    public R<PageR<ModelListRSP>> list(ModelListREQ req) {
        return R.ok(modelService.list(req));
    }

    @Override
    @AdminAuthCheck
    public R<Void> deploy(ModelBaseREQ req) {
        modelService.deploy(req.getModelId());
        return R.ok();
    }

    @Override
    @AdminAuthCheck
    public R<Void> delete(ModelBaseREQ req) {
        modelService.delete(req.getModelId());
        return R.ok();
    }

    @Override
    public R<ModelConfigRSP> modelConfig() {
        return R.ok(modelService.modelConfig());
    }

}
