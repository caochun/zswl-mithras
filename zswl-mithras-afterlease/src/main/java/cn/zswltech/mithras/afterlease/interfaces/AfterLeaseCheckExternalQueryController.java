package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckExternalQueryApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryClientInfoModifyReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryConclusionModifyReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryDetailRsp;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryIdReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListRsp;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListStatisticsRsp;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryModifyReq;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class AfterLeaseCheckExternalQueryController implements AfterLeaseCheckExternalQueryApi {

    @Resource
    private AfterLeaseCheckExternalQueryApplicationService afterLeaseCheckExternalQueryApplicationService;

    @Override
    public R<Void> submit(@Valid AfterLeaseCheckExternalQueryIdReq id) {
        return afterLeaseCheckExternalQueryApplicationService.submit(id);
    }

    @Override
    public R<Void> modify(@Valid AfterLeaseCheckExternalQueryModifyReq req) {
        return afterLeaseCheckExternalQueryApplicationService.modify(req);
    }

    @Override
    public R<Void> modifyConclusion(@Valid AfterLeaseCheckExternalQueryConclusionModifyReq req) {
        return afterLeaseCheckExternalQueryApplicationService.modifyConclusion(req);
    }

    @Override
    public R<AfterLeaseCheckExternalQueryListStatisticsRsp> listStatistics(@Valid AfterLeaseCheckExternalQueryListReq req) {
        return afterLeaseCheckExternalQueryApplicationService.listStatistics(req);
    }

    @Override
    public R<PageR<AfterLeaseCheckExternalQueryListRsp>> list(@Valid AfterLeaseCheckExternalQueryListReq req) {
        return afterLeaseCheckExternalQueryApplicationService.list(req);
    }

    @Override
    public R<AfterLeaseCheckExternalQueryDetailRsp> detail(@Valid AfterLeaseCheckExternalQueryIdReq req) {
        return afterLeaseCheckExternalQueryApplicationService.detail(req);
    }

    @Override
    public R<Void> clientInfoModify(@Valid AfterLeaseCheckExternalQueryClientInfoModifyReq req) {
        return afterLeaseCheckExternalQueryApplicationService.clientInfoModify(req);
    }

    @Override
    public R<Void> downLoadReport(@Valid AfterLeaseCheckExternalQueryIdReq req) throws Exception {
        return afterLeaseCheckExternalQueryApplicationService.downLoadReport(req);
    }
}
