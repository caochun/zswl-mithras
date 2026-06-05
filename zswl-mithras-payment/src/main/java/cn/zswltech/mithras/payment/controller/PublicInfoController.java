package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PublicInfoApi;
import cn.zswltech.mithras.api.payment.dto.pubinfo.*;
import java.util.List;
import cn.zswltech.mithras.payment.application.PublicInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PublicInfoController implements PublicInfoApi {
    @Resource
    private PublicInfoApplicationService publicInfoApplicationService;

    @Override
    public R<List<PublicInfoClientListRSP>> clientList(PublicInfoClientListREQ req) {
        return publicInfoApplicationService.clientList(req);
    }

    @Override
    public R<Boolean> queryTableCheck(PublicInfoSubmitCheckREQ req) {
        return publicInfoApplicationService.queryTableCheck(req);
    }

    @Override
    public R<PublicInfoQueryRSP> queryTableResult(PublicInfoQueryREQ req) {
        return publicInfoApplicationService.queryTableResult(req);
    }

    @Override
    public R<Void> modifyTableContent(PublicInfoModifyContentREQ req) {
        return publicInfoApplicationService.modifyTableContent(req);
    }

    @Override
    public R<Long> createIntervalTable(PublicInfoCreateREQ req) {
        return publicInfoApplicationService.createIntervalTable(req);
    }

    @Override
    public R<Void> export(PublicInfoExportREQ req) {
        return publicInfoApplicationService.export(req);
    }

    @Override
    public R<Void> deleteIntervalTable(PublicInfoDeleteREQ req) {
        return publicInfoApplicationService.deleteIntervalTable(req);
    }

    @Override
    public R<List<String>> check(PublicInfoCheckREQ req) {
        return publicInfoApplicationService.check(req);
    }

}
