package cn.zswltech.mithras.service.service.afterlese.impl;
import cn.zswltech.mithras.workflow.domain.enums.ProcessVarEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.afterlease.domain.enums.RentCollectionLevelEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.CollectionPenaltyReductionRelation;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import cn.zswltech.mithras.afterlease.application.impl.CollectionPenaltyReductionRelationService;
import cn.zswltech.mithras.service.service.afterlese.ReceiptCollectionService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.afterlease.domain.util.CollectionLevelUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @ClassName ReceiptCollectionServiceImpl
 * @Author jackerhe
 * @Date 2022/11/19 4:51 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class ReceiptCollectionServiceImpl implements ReceiptCollectionService {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionPenaltyReductionService collectionPenaltyReductionService;
    @Resource
    private CollectionPenaltyReductionRelationService collectionPenaltyReductionRelationService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public Page<CollectionBaseInfo> list(ReceiptCollectionListREQ req) {
        return collectionBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getPaymentCode, req.getPaymentCode())
                .gt(CollectionBaseInfo::getPenaltyInterest, 0L));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void collectionNotice(Long reduceId) {
        CollectionPenaltyReductionInfo reductionInfo = collectionPenaltyReductionService.getById(reduceId);
        if (ObjectUtil.isNull(reductionInfo)) {
            log.info("ReceiptCollectionServiceImpl collectionNotice reduceId {} not fund", reduceId);
            return;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(reductionInfo.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            log.info("ReceiptCollectionServiceImpl collectionNotice reduceId {} not fund contract", reduceId);
            return;
        }
        //判断是否合同主办 --系统调用不再判断
        //dataAuthCreatorRule.check(BusinessModuleEnum.CONTRACT, reductionInfo.getContractId());
        //已推送
        // 查询产生罚息且罚息未全部收款期限
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, reductionInfo.getContractId())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPenaltyInterest, 0)
                .apply(" penalty_interest > IFNULl(collection_penalty_interest, 0)")
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .orderByAsc(CollectionBaseInfo::getReceiptCode));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return;
        }
        //if(Boolean.FALSE.equals(noticeFinancialDecide(collectionBaseInfos))) throw new MithrasException("已通知财务系统！");
        //查询减免金额
        List<CollectionPenaltyReductionInfo> penaltyReductionInfos = collectionPenaltyReductionService.list(Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery()
                .eq(CollectionPenaltyReductionInfo::getContractId, reductionInfo.getContractId())
                .eq(CollectionPenaltyReductionInfo::getCollectionStatus, RecordStatus.TAKE_EFFECT.name())
                .gt(CollectionPenaltyReductionInfo::getPenaltyInterestSurplusAmount, 0));
        if (ObjectUtil.isNull(collectionBaseInfos)) {
            return;
        }
        long reduceTemp;
        long surplusAmount;
        List<CollectionPenaltyReductionRelation> reductionRelations = new ArrayList<>();
        List<CollectionBaseInfo> sendCollection = new ArrayList<>();
        //计算减免
        if (ObjectUtil.isNotEmpty(penaltyReductionInfos)) {
            //依次处理减免金额
            for (CollectionPenaltyReductionInfo reduceInfo : penaltyReductionInfos) {
                //罚息减免剩余金额大于0
                if (reduceInfo.getPenaltyInterestSurplusAmount() > 0) {
                    for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
                        //以往减免
                        reduceTemp = LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount());
                        //本期剩余 = 罚息 - 实收 - 减免
                        surplusAmount = LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) - reduceTemp;
                        if (surplusAmount > 0) {
                            //开始减免
                            if (reduceInfo.getPenaltyInterestSurplusAmount() > surplusAmount) {
                                //减免大于本期
                                reduceInfo.setPenaltyInterestSurplusAmount(reduceInfo.getPenaltyInterestSurplusAmount() - surplusAmount);
                                baseInfo.setPenaltyInterestDeductionAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) + surplusAmount);
                                reductionRelations.add(buildCollectionPenaltyReductionRelation(reduceInfo.getId(), baseInfo.getCode(), baseInfo.getPaymentCode(), surplusAmount));
                            } else {
                                //减免小于等于本期
                                baseInfo.setPenaltyInterestDeductionAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) + reduceInfo.getPenaltyInterestSurplusAmount());
                                reductionRelations.add(buildCollectionPenaltyReductionRelation(reduceInfo.getId(), baseInfo.getCode(), baseInfo.getPaymentCode(), reduceInfo.getPenaltyInterestSurplusAmount()));
                                reduceInfo.setPenaltyInterestSurplusAmount(0L);
                                baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                                //通知次数+1
                                baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                                sendCollection.add(baseInfo);
                                //结束
                                break;
                            }
                            baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                            //通知次数+1
                            baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                            sendCollection.add(baseInfo);
                        }
                    }
                }
            }
        } else {
            //无减免，全部通知
            for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
                baseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                //通知次数+1
                baseInfo.setOverdueCollectionCount(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) + 1);
                sendCollection.add(baseInfo);
            }
        }
        //批量保存
        if (ObjectUtil.isNotEmpty(sendCollection)) {
            collectionBaseInfoService.updateBatchById(sendCollection);
        }
        if (ObjectUtil.isNotEmpty(penaltyReductionInfos)) {
            collectionPenaltyReductionService.updateBatchById(penaltyReductionInfos);
        }
        if (ObjectUtil.isNotEmpty(reductionRelations)) {
            collectionPenaltyReductionRelationService.saveBatch(reductionRelations);
        }
        contractBaseInfo.setOverdueCollectionFlag(Long.valueOf(YesOrNoNumberEnum.YES.getCode()));
        contractBaseInfoService.updateById(contractBaseInfo);
        //通知苍穹
        //financialManagerService.collectionExec(sendCollection.get());
    }

    /*private Boolean noticeFinancialDecide(List<CollectionBaseInfo> collectionBaseInfos){
        //非今天且有未核销完毕到罚息即可再次通知
        if(ObjectUtil.isNotEmpty(collectionBaseInfos)){
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfos){
                if((ObjectUtil.isNotNull(collectionBaseInfo.getPlanPenaltyInterestDate()) && !collectionBaseInfo.getPlanPenaltyInterestDate().equals(LocalDate.now())) && (LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest())) > 0){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }*/

    @Override
    public CollectionOverdueRSP overdue(CollectionOverdueREQ req) {
        //计算
        CollectionOverdueRSP rsp = new CollectionOverdueRSP();
        rsp.setCollectionLevel(getCollectionLevel(req.getContractId()));
        rsp.setFileList(materialsListService.list(BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name(), Collections.singletonList(MaterialsEnum.OVERDUE_COLLECTION.name()),
                Collections.singletonList(req.getContractId())).stream().map(base -> BeanUtil.copyProperties(base, FileListRSP.class)).collect(Collectors.toList()));
        return rsp;
    }

    private String getCollectionLevel(Long contractId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .gt(CollectionBaseInfo::getPenaltyInterest, 0)
                .apply(" penalty_interest > IFNULl(collection_penalty_interest, 0)")
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(collectionBaseInfo)) {
            return null;
        }
        return Optional.ofNullable(CollectionLevelUtil.getCollectionLevel(CollectionLevelUtil.getOverdueDay(collectionBaseInfo.getPlanCollectionDate()))).map(RentCollectionLevelEnum::display).orElse(null);
    }

    private CollectionPenaltyReductionRelation buildCollectionPenaltyReductionRelation(Long reduceId, String code, String paymentCode, Long reduceAmount) {
        CollectionPenaltyReductionRelation collectionPenaltyReductionRelation = new CollectionPenaltyReductionRelation();
        collectionPenaltyReductionRelation.setReduceId(reduceId);
        collectionPenaltyReductionRelation.setCode(code);
        collectionPenaltyReductionRelation.setPaymentCode(paymentCode);
        collectionPenaltyReductionRelation.setReduceAmount(reduceAmount);
        collectionPenaltyReductionRelation.setStatus(Long.valueOf(YesOrNoNumberEnum.YES.getCode()));
        return collectionPenaltyReductionRelation;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void effect(CollectionPenaltyReductionEffectREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //合同下有未生效的不许提交
        if (collectionPenaltyReductionService.count(Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery()
                .eq(CollectionPenaltyReductionInfo::getContractId, req.getContractId())
                .eq(CollectionPenaltyReductionInfo::getCollectionStatus, RecordStatus.NEW.name())) > 0) {
            throw new MithrasException("此合同罚息减免审批未结束！");
        }
        //判断是否合同主办
        dataAuthSponsorUserRule.check(BusinessModuleEnum.CONTRACT, req.getContractId());
        CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = BeanUtil.copyProperties(req, CollectionPenaltyReductionInfo.class);
        collectionPenaltyReductionInfo.setPenaltyInterestSurplusAmount(collectionPenaltyReductionInfo.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        collectionPenaltyReductionInfo.setCollectionStatus(RecordStatus.NEW.name());
        collectionPenaltyReductionService.save(collectionPenaltyReductionInfo);
        if (ObjectUtil.isNotEmpty(req.getFiles())) {
            req.getFiles().forEach(file -> materialsListService.add(file, collectionPenaltyReductionInfo.getId(), MaterialsEnum.DEDUCTION_INTEREST.name(), BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name()));
        }
        StartProcessReq startProcessReq = buildCommonStartProcessReq(collectionPenaltyReductionInfo);
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(CollectionPenaltyReductionModifyREQ req) {
        CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = collectionPenaltyReductionService.getById(req.getId());
        if (ObjectUtil.isNull(collectionPenaltyReductionInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (RecordStatus.TAKE_EFFECT.name().equals(collectionPenaltyReductionInfo.getCollectionStatus()) || RecordStatus.CLOSED.name().equals(collectionPenaltyReductionInfo.getCollectionStatus())) {
            throw new MithrasException("减免已完结！");
        }
        collectionPenaltyReductionInfo.setPenaltyInterestDeductionAmount(req.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setPenaltyInterestSurplusAmount(collectionPenaltyReductionInfo.getPenaltyInterestDeductionAmount());
        collectionPenaltyReductionInfo.setReasonExplain(req.getReasonExplain());
        if (ObjectUtil.isNotEmpty(req.getFiles())) {
            req.getFiles().forEach(file -> materialsListService.add(file, collectionPenaltyReductionInfo.getId(), MaterialsEnum.DEDUCTION_INTEREST.name(), BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name()));
        }
        if (ObjectUtil.isNotEmpty(req.getRemoveFileIds())) {
            materialsListService.remove(req.getRemoveFileIds());
        }
        collectionPenaltyReductionService.updateById(collectionPenaltyReductionInfo);
    }

    @Override
    public Long calculationInterest(Long contractId) {
        List<CollectionBaseInfo> baseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId));
        if (ObjectUtil.isEmpty(baseInfos)) {
            return 0L;
        }
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        for (CollectionBaseInfo baseInfo : baseInfos) {
            sum.updateAndGet(v -> v + LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()));
        }
        return sum.get();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        if (processPass) {
            recordCollectionPenaltyReductionStatus(id, RecordStatus.TAKE_EFFECT, ProcessStatus.APPROVAL_PASS);
        } else {
            recordCollectionPenaltyReductionStatus(id, RecordStatus.CLOSED, ProcessStatus.APPROVAL_REJECT);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordCollectionPenaltyReductionStatus(Long id, RecordStatus reductionStatus,
                                                       ProcessStatus reductionProcessStatus) {
        if (id == null || (reductionStatus == null && reductionProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<CollectionPenaltyReductionInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CollectionPenaltyReductionInfo::getId, id);
        if (reductionStatus != null) {
            updateWrapper.set(CollectionPenaltyReductionInfo::getCollectionStatus, reductionStatus.name());
        }
        if (reductionProcessStatus != null) {
            updateWrapper.set(CollectionPenaltyReductionInfo::getProcessStatus, reductionProcessStatus.name());
        }
        updateWrapper.set(CollectionPenaltyReductionInfo::getUpdateTime, LocalDateTime.now());
        collectionPenaltyReductionService.update(null, updateWrapper);
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return StartProcessReq
     */
    private StartProcessReq buildCommonStartProcessReq(CollectionPenaltyReductionInfo collectionPenaltyReductionInfo) {
        //初始化参数
        StartProcessReq startProcessReq = new StartProcessReq();
        ContractBaseInfo baseInfo = contractBaseInfoService.getById(collectionPenaltyReductionInfo.getContractId());
        //todo 罚息减免流程待补充
        startProcessReq.setModelKey(ProcessModelTypeEnum.RentCollectionExemptionFlow.name());

        startProcessReq.setVariables(MapUtil.of(
                //todo 罚息减免流程待补充
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ?
                        ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of(ProcessVarEnum.applyCreditAmount.name(), NumberUtil.div(baseInfo.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue())
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(collectionPenaltyReductionInfo.getId()));
        //项目类型
        startProcessReq.setSubModule(baseInfo.getBizType());
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ?
                new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId())
                .map(String::valueOf).orElse(null));
        return startProcessReq;
    }
}
