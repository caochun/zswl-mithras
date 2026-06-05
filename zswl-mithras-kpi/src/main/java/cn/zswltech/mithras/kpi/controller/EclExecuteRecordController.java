package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.EclExecuteRecordApi;
import cn.zswltech.mithras.kpi.application.EclExecuteRecordApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;
import java.util.List;
import java.util.Set;

@RestController
public class EclExecuteRecordController implements EclExecuteRecordApi {
    @Resource
    private EclExecuteRecordApplicationService eclExecuteRecordApplicationService;

    @Override
    public R<Void> add(EclExecuteRecordAddREQ req) {
        return eclExecuteRecordApplicationService.add(req);
    }

    @Override
    public R<String> addCheck(EclExecuteRecordAddREQ req) {
        return eclExecuteRecordApplicationService.addCheck(req);
    }

    @Override
    public R<Void> importFile(EclExecuteRecordImportREQ req) {
        return eclExecuteRecordApplicationService.importFile(req);
    }

    @Override
    public R<Set<String>> importFileCheck(EclExecuteRecordImportREQ req) {
        return eclExecuteRecordApplicationService.importFileCheck(req);
    }

    @Override
    public R<Void> modify(EclExecuteRecordModifyREQ req) {
        return eclExecuteRecordApplicationService.modify(req);
    }

    @Override
    public R<PageR<EclExecuteRecordListRSP>> list(EclExecuteRecordListREQ req) {
        return eclExecuteRecordApplicationService.list(req);
    }

    @Override
    public R<List<EclExecuteRecordListRSP>> listCompare(EclExecuteRecordListREQ req) {
        return eclExecuteRecordApplicationService.listCompare(req);
    }

    @Override
    public R<Void> remove(EclExecuteRecordRemoveREQ req) {
        return eclExecuteRecordApplicationService.remove(req);
    }

    @Override
    public R<Void> allRecordRemove(EclExecuteRecordRemoveREQ req) {
        return eclExecuteRecordApplicationService.allRecordRemove(req);
    }

}
