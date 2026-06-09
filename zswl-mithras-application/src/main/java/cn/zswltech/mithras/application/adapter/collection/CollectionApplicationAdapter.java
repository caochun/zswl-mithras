package cn.zswltech.mithras.application.adapter.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.collection.application.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.collection.service.CollectionBaseInfoApplicationService;
import cn.zswltech.mithras.collection.service.CollectionFlowCenterApplicationService;
import cn.zswltech.mithras.collection.service.CollectionRecordInfoApplicationService;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoListRSP;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoREQ;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionExportListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionExportREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionListRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionManualRecordREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionSettleDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionSettleDetailRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentManualCashFlowListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentManualRecordREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentManualRecordRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentSettleDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentSettleDetailRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterClientContractMarginREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterClientContractMarginRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterRecycleMarginPlanREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterRecycleMarginPlanRSP;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestREQ;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestRSP;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailRSP;
import cn.zswltech.mithras.dto.collection.CollectionRecordListREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordListRSP;
import cn.zswltech.mithras.dto.collection.PenaltyInterestListRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewSubAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionFlowCenterService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class CollectionApplicationAdapter implements CollectionBaseInfoApplicationService,
        CollectionFlowCenterApplicationService, CollectionRecordInfoApplicationService {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private CollectionFlowCenterService collectionFlowCenterService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;

    @Override
    public PageR<CollectionBaseInfoListRSP> list(CollectionBaseInfoREQ req) {
        return collectionBaseInfoService.list(req);
    }

    @Override
    public void exportList(CollectionBaseInfoREQ req, ServletOutputStream outputStream) {
        collectionBaseInfoService.exportList(req, outputStream);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = "COLLECTION")
    public CollectionBaseInfoRSP detail(CollectionBaseInfoDetailREQ req) {
        return collectionBaseInfoService.detail(req);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = "COLLECTION")
    public CollectionPenaltyInterestRSP penaltyInterestDetail(CollectionBaseInfoDetailREQ req) {
        return collectionOverdueRecordInfoService.detail(req);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = "COLLECTION")
    public PageR<PenaltyInterestListRSP> penaltyInterestList(CollectionPenaltyInterestREQ req) {
        return collectionOverdueRecordInfoService.list(req);
    }

    @Override
    public PageR<CollectionFlowCenterBusinessPaymentListRSP> paymentList(CollectionFlowCenterBusinessPaymentListREQ req) {
        return collectionFlowCenterService.paymentList(req);
    }

    @Override
    public void paymentManualRecord(CollectionFlowCenterBusinessPaymentManualRecordREQ req) {
        collectionFlowCenterService.paymentManualRecord(req);
    }

    @Override
    public List<CollectionFlowCenterBusinessPaymentManualRecordRSP> paymentManualCashFlowList(CollectionFlowCenterBusinessPaymentManualCashFlowListREQ req) {
        return collectionFlowCenterService.paymentManualCashFlowList(req);
    }

    @Override
    public List<CollectionFlowCenterBusinessPaymentSettleDetailRSP> paymentSettleDetail(CollectionFlowCenterBusinessPaymentSettleDetailREQ req) {
        return collectionFlowCenterService.paymentSettleDetail(req);
    }

    @Override
    public PageR<CollectionFlowCenterBusinessCollectionListRSP> collectionList(CollectionFlowCenterBusinessCollectionListREQ req) {
        return collectionFlowCenterService.collectionList(req);
    }

    @Override
    public void collectionManualRecord(CollectionFlowCenterBusinessCollectionManualRecordREQ req) {
        collectionFlowCenterService.collectionManualRecord(req, true);
    }

    @Override
    public List<CollectionFlowCenterBusinessCollectionSettleDetailRSP> collectionSettleDetail(CollectionFlowCenterBusinessCollectionSettleDetailREQ req) {
        return collectionFlowCenterService.collectionSettleDetail(req);
    }

    @Override
    public void exportBusinessCollection(CollectionFlowCenterBusinessCollectionExportListREQ req, HttpServletResponse response) throws IOException {
        collectionFlowCenterService.exportBusinessCollection(req, response);
    }

    @Override
    public List<CollectionFlowCenterBusinessCollectionListRSP> businessCollectionExportList(CollectionFlowCenterBusinessCollectionExportREQ req) {
        return collectionFlowCenterService.businessCollectionExportList(req);
    }

    @Override
    public List<CollectionFlowCenterClientContractMarginRSP> clientContractMargin(CollectionFlowCenterClientContractMarginREQ req) {
        return collectionFlowCenterService.clientContractMargin(req);
    }

    @Override
    public CollectionFlowCenterRecycleMarginPlanRSP recycleMarginPlan(CollectionFlowCenterRecycleMarginPlanREQ req) {
        CollectionBaseInfo nextCollectionMessage = collectionBaseInfoService.getNextCollectionMessage(req.getCollectionId());
        CollectionFlowCenterRecycleMarginPlanRSP planRSP = new CollectionFlowCenterRecycleMarginPlanRSP();
        if (ObjectUtil.isNotEmpty(nextCollectionMessage)) {
            planRSP.setPlanCollectionDate(nextCollectionMessage.getPlanCollectionDate());
        }
        return planRSP;
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = "COLLECTION")
    public CollectionRecordListRSP list(CollectionRecordListREQ req) {
        return collectionRecordInfoService.list(req);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewSubAuthCheckerNew.class, mapperClass = CollectionRecordInfoMapper.class, businessModule = "COLLECTION")
    public CollectionRecordDetailRSP detail(CollectionRecordDetailREQ req) {
        return collectionRecordInfoService.detail(req);
    }

    @Override
    public R<String> addFinancialRecord() {
        CollectionRecordInfo info = new CollectionRecordInfo();
        info.setDataSource("财务系统");
        info.setSourceFlag(0);
        return collectionRecordInfoService.addRecord(info);
    }
}
