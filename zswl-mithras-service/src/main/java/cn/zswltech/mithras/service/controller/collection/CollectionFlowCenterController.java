package cn.zswltech.mithras.service.controller.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.collection.CollectionFlowCenterApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionFlowCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName CollectionFlowCenterController
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/2/26 3:39 下午
 * @Version 1.0
 **/
@Slf4j
@RestController
public class CollectionFlowCenterController implements CollectionFlowCenterApi {

    @Resource
    private CollectionFlowCenterService collectionFlowCenterService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<CollectionFlowCenterBusinessPaymentListRSP>> paymentList(@Valid CollectionFlowCenterBusinessPaymentListREQ req) {
        return R.ok(collectionFlowCenterService.paymentList(req));
    }

    @Override
    public R<Void> paymentManualRecord(@Valid CollectionFlowCenterBusinessPaymentManualRecordREQ req) {
        collectionFlowCenterService.paymentManualRecord(req);
        return R.ok();
    }

    @Override
    public R<List<CollectionFlowCenterBusinessPaymentManualRecordRSP>> paymentManualCashFlowList(CollectionFlowCenterBusinessPaymentManualCashFlowListREQ req) {
        return R.ok(collectionFlowCenterService.paymentManualCashFlowList(req));
    }

    @Override
    public R<List<CollectionFlowCenterBusinessPaymentSettleDetailRSP>> paymentSettleDetail(@Valid CollectionFlowCenterBusinessPaymentSettleDetailREQ req) {
        return R.ok(collectionFlowCenterService.paymentSettleDetail(req));
    }

    @Override
    public R<PageR<CollectionFlowCenterBusinessCollectionListRSP>> collectionList(@Valid CollectionFlowCenterBusinessCollectionListREQ req) {
        return R.ok(collectionFlowCenterService.collectionList(req));
    }

    @Override
    public R<Void> collectionManualRecord(@Valid CollectionFlowCenterBusinessCollectionManualRecordREQ req) {
        collectionFlowCenterService.collectionManualRecord(req, true);
        return R.ok();
    }

    @Override
    public R<List<CollectionFlowCenterBusinessCollectionSettleDetailRSP>> collectionSettleDetail(@Valid CollectionFlowCenterBusinessCollectionSettleDetailREQ req) {
        return R.ok(collectionFlowCenterService.collectionSettleDetail(req));
    }

    @Override
    public R<CollectionFlowCenterCountRSP> flowCenterCount() {
        CollectionFlowCenterCountRSP collectionFlowCenterCountRSP = new CollectionFlowCenterCountRSP();
        CollectionFlowCenterBusinessCollectionListREQ req = new CollectionFlowCenterBusinessCollectionListREQ();
        req.setPageSize(1);
        req.setPage(1);
        req.setWriteOffStatusList(Collections.singletonList(CollectionWriteOffStatusEnum.UNCOLLECTION.name()));
        PageR<CollectionFlowCenterBusinessCollectionListRSP> rsp = collectionFlowCenterService.collectionList(req);
        if (ObjectUtil.isNotEmpty(rsp)) {
            collectionFlowCenterCountRSP.setTotal(rsp.getTotal());
        }
        return R.ok(collectionFlowCenterCountRSP);
    }

    @Override
    public void exportBusinessCollection(CollectionFlowCenterBusinessCollectionExportListREQ collectionExportListREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            collectionFlowCenterService.exportBusinessCollection(collectionExportListREQ, httpServletResponse);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出业务流水收据发生未知异常", e);
            throw new MithrasException("导出业务流水收据发生未知异常");
        }

    }

    @Override
    public R<List<CollectionFlowCenterBusinessCollectionListRSP>> businessCollectionExportList(CollectionFlowCenterBusinessCollectionExportREQ collectionExportREQ) {
        return R.ok(collectionFlowCenterService.businessCollectionExportList(collectionExportREQ));
    }

    @Override
    public R<List<CollectionFlowCenterClientContractMarginRSP>> clientContractMargin(@Valid CollectionFlowCenterClientContractMarginREQ req) {
        return R.ok(collectionFlowCenterService.clientContractMargin(req));
    }

    @Override
    public R<CollectionFlowCenterRecycleMarginPlanRSP> recycleMarginPlan(@Valid CollectionFlowCenterRecycleMarginPlanREQ req) {
        CollectionBaseInfo nextCollectionMessage = collectionBaseInfoService.getNextCollectionMessage(req.getCollectionId());
        CollectionFlowCenterRecycleMarginPlanRSP planRSP = new CollectionFlowCenterRecycleMarginPlanRSP();
        if (ObjectUtil.isNotEmpty(nextCollectionMessage)) {
            planRSP.setPlanCollectionDate(nextCollectionMessage.getPlanCollectionDate());
        }
        return R.ok(planRSP);
    }
}
