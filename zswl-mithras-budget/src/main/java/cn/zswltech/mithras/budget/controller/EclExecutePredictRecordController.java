package cn.zswltech.mithras.budget.controller;
import cn.zswltech.mithras.api.budget.EclExecutePredictRecordApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.application.EclExecutePredictRecordApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Set;

/**
* @description 资产减值预测详情记录表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclExecutePredictRecordController implements EclExecutePredictRecordApi {

    @Resource
    private EclExecutePredictRecordApplicationService eclExecutePredictRecordService;

    @Override
    public R<Void> add(EclExecutePredictRecordAddREQ req) {
        eclExecutePredictRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<String> addCheck(@Valid EclExecutePredictRecordAddREQ req) {
        return R.ok(eclExecutePredictRecordService.addCheck(req));
    }

    @Override
    public R<Void> importFile(@Valid EclExecutePredictRecordImportREQ req) {
        eclExecutePredictRecordService.importFile(req);
        return R.ok();
    }

    @Override
    public R<Set<String>> importFileCheck(@Valid EclExecutePredictRecordImportREQ req) {
        return R.ok(eclExecutePredictRecordService.importFileCheck(req));
    }

    @Override
    public R<Void> modify(EclExecutePredictRecordModifyREQ req){
        eclExecutePredictRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclExecutePredictRecordListRSP>> list(EclExecutePredictRecordListREQ req){
        return R.ok(eclExecutePredictRecordService.pageList(req));
    }

    @Override
    public R<Void> remove(EclExecutePredictRecordRemoveREQ req){
        eclExecutePredictRecordService.remove(req);
        return R.ok();
    }

}
