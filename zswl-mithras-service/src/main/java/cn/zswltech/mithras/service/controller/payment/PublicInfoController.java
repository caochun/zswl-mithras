package cn.zswltech.mithras.service.controller.payment;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PublicInfoApi;
import cn.zswltech.mithras.api.payment.dto.pubinfo.*;
import cn.zswltech.mithras.service.service.payment.pubinfo.PublicInfoQueryService;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.HttpResource;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author bigbear
 * @date 2024/9/10 19:27
 * @description
 */
@Slf4j
@RestController
public class PublicInfoController implements PublicInfoApi {

    private static final Logger log = LoggerFactory.getLogger(PublicInfoController.class);
    @Resource
    private PublicInfoQueryService publicInfoQueryService;

    @Override
    public R<List<PublicInfoClientListRSP>> clientList(PublicInfoClientListREQ req) {
        return R.ok(publicInfoQueryService.clientList(req));
    }

    @Override
    public R<Boolean> queryTableCheck(PublicInfoSubmitCheckREQ req) {
        return R.ok(publicInfoQueryService.queryTableCheck(req));
    }

    @Override
    public R<PublicInfoQueryRSP> queryTableResult(PublicInfoQueryREQ req) {
        return R.ok(publicInfoQueryService.queryTableResult(req));
    }

    @Override
    public R<Void> modifyTableContent(PublicInfoModifyContentREQ req) {
        publicInfoQueryService.modifyTableContent(req);
        return R.ok();
    }

    @Override
    public R<Long> createIntervalTable(PublicInfoCreateREQ req) {
        return R.ok(publicInfoQueryService.createIntervalTable(req));
    }

    @Override
    public R<Void> export(PublicInfoExportREQ req) {
        try {
            publicInfoQueryService.export(req);
        } catch (IOException e) {
            log.error("导出失败", e);
            return R.fail("导出失败");
        }
        return R.ok();
    }

    @Override
    public R<Void> deleteIntervalTable(PublicInfoDeleteREQ req) {
        publicInfoQueryService.deleteIntervalTable(req);
        return R.ok();
    }

    @Override
    public R<List<String>> check(PublicInfoCheckREQ req) {
        return R.ok(publicInfoQueryService.check(req));
    }
}
