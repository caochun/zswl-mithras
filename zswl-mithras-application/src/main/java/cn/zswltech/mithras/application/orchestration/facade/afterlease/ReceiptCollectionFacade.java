package cn.zswltech.mithras.application.orchestration.facade.afterlease;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.afterlease.application.ReceiptCollectionApplicationService;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.enums.MaterialsEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.afterlease.mapper.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.afterlease.application.ReceiptCollectionService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.afterlease.util.CollectionLevelUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ReceiptCollectionController
 * 租后催收-罚息减免
 * @Author jackerhe
 * @Date 2022/11/18 2:33 下午
 * @Version 1.0
 **/
@Service
public class ReceiptCollectionFacade implements ReceiptCollectionApplicationService {

    @Resource
    private CollectionPenaltyReductionService collectionPenaltyReductionService;
    @Resource
    private ReceiptCollectionService receiptCollectionService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MaterialsListService materialsListService;


    @Override
    public R<PageR<ReceiptCollectionListRSP>> list(@Valid ReceiptCollectionListREQ req) {
        Page<CollectionBaseInfo> list = receiptCollectionService.list(req);
        List<ReceiptCollectionListRSP> receiptCollectionListRSPS = new ArrayList<>();
        ReceiptCollectionListRSP receiptCollectionListRSP;
        for(CollectionBaseInfo baseInfo : list.getRecords()){
            receiptCollectionListRSP = BeanUtil.copyProperties(baseInfo, ReceiptCollectionListRSP.class);
            receiptCollectionListRSP.setOverdueDays(CollectionLevelUtil.getOverdueDay(receiptCollectionListRSP.getPlanCollectionDate()));
            receiptCollectionListRSPS.add(receiptCollectionListRSP);
        }
        return R.ok(PageR.of(list, receiptCollectionListRSPS));
    }

    @Override
    public R<CollectionOverdueRSP> overdue(@Valid CollectionOverdueREQ req) {
        CollectionOverdueRSP overdue = receiptCollectionService.overdue(req);
        List<FileListRSP> fileList = overdue.getFileList();
        List<Long> ids = fileList.stream().map(FileListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> idsMap = id2NameService.sysUserId2Name(ids);
        for(FileListRSP rsp : fileList){
            rsp.setCreateByName(idsMap.get(rsp.getCreateBy()));
        }
        return R.ok(overdue);
    }

   /* @Override
    public R<Void> collectionNotice(@Valid ReceiptCollectionNoticeREQ req) {
        receiptCollectionService.collectionNotice(req);
        return R.ok();
    }*/

    @Override
    public R<Void> effect(@Valid CollectionPenaltyReductionEffectREQ req) {
        if(req.getPenaltyInterestDeductionAmount() <= 0){
            throw new MithrasException("罚息减免金额需大于0");
        }
        if(req.getPenaltyInterestDeductionAmount() > receiptCollectionService.calculationInterest(req.getContractId())){
            throw new MithrasException("罚息减免金额大于剩余罚息");
        }
        receiptCollectionService.effect(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonModifyMainAuthCheckerNew.class, businessModule = "OVERDUE_COLLECTION_REDUCTION")
    public R<Void> modify(@Valid CollectionPenaltyReductionModifyREQ req) {
        receiptCollectionService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<CollectionPenaltyReductionInfoListRSP>> reductionList(CollectionPenaltyReductionInfoListREQ req){
        Page<CollectionPenaltyReductionInfo> data = collectionPenaltyReductionService.list(req);
        List<CollectionPenaltyReductionInfoListRSP> list = data.getRecords().stream().map(base -> {
            CollectionPenaltyReductionInfoListRSP collectionPenaltyReductionInfoListRSP = BeanUtil.copyProperties(base, CollectionPenaltyReductionInfoListRSP.class);
            //生效填充审批通过时间
            if(RecordStatus.TAKE_EFFECT.name().equals(base.getCollectionStatus())){
                collectionPenaltyReductionInfoListRSP.setProcessTime(base.getUpdateTime());
            }
            return collectionPenaltyReductionInfoListRSP;
        }).collect(Collectors.toList());

        List<Long> ids = list.stream().map(CollectionPenaltyReductionInfoListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> idsMap = id2NameService.sysUserId2Name(ids);
        for(CollectionPenaltyReductionInfoListRSP rsp : list){
            rsp.setCreateByName(idsMap.get(rsp.getCreateBy()));
            rsp.setFileList(materialsListService.list(BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name(), Collections.singletonList(MaterialsEnum.DEDUCTION_INTEREST.name()),
                    Collections.singletonList(rsp.getId())).stream().map(base -> BeanUtil.copyProperties(base, FileListRSP.class)).collect(Collectors.toList()));
            rsp.getFileList().forEach(fileListRSP -> fileListRSP.setCreateByName(idsMap.get(fileListRSP.getCreateBy())));

        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<CollectionRelationContractRSP> relationContract(@Valid CollectionRelationContractREQ req) {
        return R.ok(collectionPenaltyReductionService.relationContract(req));
    }

    @Override
    public R<ReceiptReduceInterestRSP> calculationInterest(@Valid ReceiptReduceInterstREQ req) {
        return R.ok(ReceiptReduceInterestRSP.builder().penaltyInterestSurplusAmount(receiptCollectionService.calculationInterest(req.getContractId())).build());
    }
}
