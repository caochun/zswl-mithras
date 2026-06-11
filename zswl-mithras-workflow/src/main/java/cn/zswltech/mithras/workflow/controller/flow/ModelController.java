package cn.zswltech.mithras.workflow.controller.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.ModelApi;
import cn.zswltech.mithras.dto.flow.model.ModelBaseREQ;
import cn.zswltech.mithras.dto.flow.model.ModelConfigRSP;
import cn.zswltech.mithras.dto.flow.model.ModelDetailRSP;
import cn.zswltech.mithras.dto.flow.model.ModelListREQ;
import cn.zswltech.mithras.dto.flow.model.ModelListRSP;
import cn.zswltech.mithras.dto.flow.model.SaveModelREQ;
import javax.annotation.Resource;
import cn.zswltech.mithras.workflow.flow.ModelApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelController implements ModelApi {
    @Resource
    private ModelApplicationService modelApplicationService;

    @Override
    public R<String> save(SaveModelREQ req) {
        return modelApplicationService.save(req);
    }

    @Override
    public R<ModelDetailRSP> detail(ModelBaseREQ req) {
        return modelApplicationService.detail(req);
    }

    @Override
    public R<PageR<ModelListRSP>> list(ModelListREQ req) {
        return modelApplicationService.list(req);
    }

    @Override
    public R<Void> deploy(ModelBaseREQ req) {
        return modelApplicationService.deploy(req);
    }

    @Override
    public R<Void> delete(ModelBaseREQ req) {
        return modelApplicationService.delete(req);
    }

    @Override
    public R<ModelConfigRSP> modelConfig() {
        return modelApplicationService.modelConfig();
    }
}
