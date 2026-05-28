package cn.zswltech.mithras.service.service.afterlese;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.afterlease.PenaltyReduceDetailModifyREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionPenaltyReduceREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.ProcessState;
import cn.zswltech.mithras.service.flow.helper.CalBoardRuleHelper;
import cn.zswltech.mithras.service.mapper.afterlease.PenaltyReduceBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.PenaltyReduceBaseInfo;
import cn.zswltech.mithras.service.mapper.model.afterlease.PenaltyReduceDetailRecord;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 罚息减免基本表
* @author vico
* @date 2024-08-21
*/
@Service
@Slf4j
public class PenaltyReduceBaseInfoService extends ServiceImpl<PenaltyReduceBaseInfoMapper, PenaltyReduceBaseInfo> {

    @Resource
    private PenaltyReduceBaseInfoMapper penaltyReduceBaseInfoMapper;
    @Resource
    private PenaltyReduceDetailRecordService penaltyReduceDetailRecordService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;

    public RentCollectionPenaltyReduceDetailRSP penaltyReductionList(RentCollectionPenaltyReduceListREQ req) {
        if (ObjectUtil.isEmpty(req.getReduceBaseId()) && ObjectUtil.isEmpty(req.getCollectionId())) {
            return null;
        }
        //区分新建和历史数据查询
        if (ObjectUtil.isNotEmpty(req.getReduceBaseId())) {
            return this.getPenaltyReduceDetailByBaseId(req.getReduceBaseId());
        } else {
            return this.getPenaltyReduceDetailByCollectionId(req.getCollectionId());
        }
    }

    /**
     * 生效保存+提起流程
     **/
    @Transactional(rollbackFor = Throwable.class)
    public Long penaltyReductionEffect(RentCollectionPenaltyReduceREQ req) {
        if(ObjectUtil.isEmpty(req.getItems())) {
            throw new MithrasException("至少存在一条减免记录");
        }
        PenaltyReduceBaseInfo baseInfo = new PenaltyReduceBaseInfo();
        baseInfo.setNotes(req.getNotes());
        penaltyReduceBaseInfoMapper.insert(baseInfo);
        List<PenaltyReduceDetailRecord> reduceDetailRecords = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(req.getItems())) {
            req.getItems().forEach(item -> {
                reduceDetailRecords.add(getPenaltyReduceDetailRecordByReduceItem(item, baseInfo, "id"));
            });
            penaltyReduceDetailRecordService.saveBatch(reduceDetailRecords);
        }
        //保存文件 文件+基本信息一起保存，文件预保存未使用，暂时先如此，不推荐
        if(ObjectUtil.isNotEmpty(req.getFiles())) {
            req.getFiles().forEach(file -> {
                materialsListService.add(file, baseInfo.getId(), MaterialsEnum.NEW_DEDUCTION_INTEREST.name(), BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION.name());
            });
        }
        //发起流程
        StartProcessReq startProcessReq = buildCommonStartProcessReq(baseInfo, reduceDetailRecords);
        processApiService.start(startProcessReq);
        LambdaUpdateWrapper<PenaltyReduceBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PenaltyReduceBaseInfo::getPenaltyReduceStatus, ProcessState.COMMIT.name());
        updateWrapper.eq(PenaltyReduceBaseInfo::getId, baseInfo.getId());
        penaltyReduceBaseInfoMapper.update(null, updateWrapper);

        //更新收款表状态
        LambdaUpdateWrapper<CollectionBaseInfo> collectionBaseInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectionBaseInfoLambdaUpdateWrapper.set(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.YES.getCode());
        collectionBaseInfoLambdaUpdateWrapper.in(CollectionBaseInfo::getId, reduceDetailRecords.stream().map(PenaltyReduceDetailRecord::getCollectionId).collect(Collectors.toList()));
        collectionBaseInfoService.update(null, collectionBaseInfoLambdaUpdateWrapper);
        return baseInfo.getId();
    }

    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        RentCollectionPenaltyReduceDetailRSP penaltyReduceDetailByBaseId = this.getPenaltyReduceDetailByBaseId(id);
        if (ObjectUtil.isEmpty(penaltyReduceDetailByBaseId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (processPass) {
            SpringContextHolder.getBean(PenaltyReduceBaseInfoService.class).recordPenaltyReductionStatus(id, ProcessStatus.APPROVAL_PASS);
        } else {
            recordPenaltyReductionStatus(id, ProcessStatus.APPROVAL_REJECT);
            //更新状态
            if (ObjectUtil.isNotEmpty(penaltyReduceDetailByBaseId.getItems())) {
                List<Long> collectionIds = penaltyReduceDetailByBaseId.getItems().stream().map(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem::getCollectionId).collect(Collectors.toList());
                LambdaUpdateWrapper<CollectionBaseInfo> collectionBaseInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                collectionBaseInfoLambdaUpdateWrapper.set(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.NO.getCode());
                collectionBaseInfoLambdaUpdateWrapper.in(CollectionBaseInfo::getId, collectionIds);
                collectionBaseInfoService.update(null, collectionBaseInfoLambdaUpdateWrapper);
                // 重新计算
                collectionOverdueRecordInfoService.doRerunPenaltyInterest(collectionIds);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordPenaltyReductionStatus(Long id, ProcessStatus reductionProcessStatus) {
        if (id == null || reductionProcessStatus == null) {
            return;
        }
        LambdaUpdateWrapper<PenaltyReduceBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PenaltyReduceBaseInfo::getId, id);
        updateWrapper.set(PenaltyReduceBaseInfo::getPenaltyReduceStatus, reductionProcessStatus.name());
        updateWrapper.set(PenaltyReduceBaseInfo::getUpdateTime, LocalDateTime.now());
        penaltyReduceBaseInfoMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doPenaltyReduce(Long reduceBaseId) {
        PenaltyReduceBaseInfo penaltyReduceBaseInfo = penaltyReduceBaseInfoMapper.selectById(reduceBaseId);
        if (ObjectUtil.isNull(penaltyReduceBaseInfo)) {
            log.info("doPenaltyReduce collectionNotice reduceId {} not fund", penaltyReduceBaseInfo);
            return;
        }
        //查询减免金额
        RentCollectionPenaltyReduceDetailRSP penaltyReduceDetailByBase = this.getPenaltyReduceDetailByBaseId(reduceBaseId);
        if (ObjectUtil.isNull(penaltyReduceDetailByBase) || ObjectUtil.isEmpty(penaltyReduceDetailByBase.getItems())) {
            return;
        }
        //查询需要减免的期限
        Map<Long, CollectionBaseInfo> collectionId2Bean = collectionBaseInfoService.listByIds(penaltyReduceDetailByBase.getItems().stream().map(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem::getCollectionId).collect(Collectors.toList())).stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
        //计算减免
        if (ObjectUtil.isNotEmpty(penaltyReduceDetailByBase)) {
            CollectionBaseInfo collectionBaseInfo = null;
            //依次处理减免金额
            for (RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem reduceInfo : penaltyReduceDetailByBase.getItems()) {
                //罚息减免剩余金额大于0
                collectionBaseInfo = collectionId2Bean.get(reduceInfo.getCollectionId());
                if(ObjectUtil.isNotEmpty(collectionBaseInfo)){
                    collectionBaseInfo.setPenaltyInterestDeductionAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()) + LongUtil.null2zero(reduceInfo.getReducePenaltyInterest()));
                    collectionBaseInfo.setPenaltyInterest(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(reduceInfo.getReducePenaltyInterest()));
                    //通知次数+1
                    collectionBaseInfo.setOverdueCollectionCount(LongUtil.null2zero(collectionBaseInfo.getOverdueCollectionCount()) + 1);
                    collectionBaseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                    collectionBaseInfo.setOverdueCollectionCount(LongUtil.null2zero(collectionBaseInfo.getOverdueCollectionCount()) + 1);
                }
            }
        }
        //批量保存
        if (ObjectUtil.isNotEmpty(collectionId2Bean.values())) {
            collectionBaseInfoService.updateBatchById(collectionId2Bean.values());
        }
    }

    private StartProcessReq buildCommonStartProcessReq(PenaltyReduceBaseInfo penaltyReduceBaseInfo, List<PenaltyReduceDetailRecord> reduceDetailRecords) {
        //初始化参数
        StartProcessReq startProcessReq = new StartProcessReq();
        List<ContractBaseInfo> contractBaseInfos = null;
        if (ObjectUtil.isNotEmpty(reduceDetailRecords)) {
            contractBaseInfos = contractBaseInfoService.listByIds(reduceDetailRecords.stream().map(PenaltyReduceDetailRecord::getContractId).collect(Collectors.toList()));
        }
        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            throw new MithrasException("没有找到任何合同信息");
        }
        startProcessReq.setModelKey(ProcessModelTypeEnum.NewRentCollectionExemptionFlow.name());
        List<String> bizDeptLeader = new ArrayList<>();
        List<String> bizDivisionLeader = new ArrayList<>();
        List<String> projCosponsorUserIds = new ArrayList<>();
        Long maxApplyCreditAmount = 0L;
        if(contractBaseInfos != null) {
            bizDeptLeader = contractBaseInfos.stream().map(contractBaseInfo -> String.valueOf(contractBaseInfo.getBizDeptLeaderId())).distinct().collect(Collectors.toList());
            bizDivisionLeader = contractBaseInfos.stream().map(contractBaseInfo -> String.valueOf(contractBaseInfo.getBizDivisionLeaderId())).distinct().collect(Collectors.toList());
            ContractBaseInfo maxContractBaseInfo = contractBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getApplyCreditAmount())).max(Comparator.comparing(ContractBaseInfo::getApplyCreditAmount)).get();
            if (ObjectUtil.isNotEmpty(maxContractBaseInfo)) {
                maxApplyCreditAmount += maxContractBaseInfo.getApplyCreditAmount();
            }
            contractBaseInfos.forEach(baseInfo -> {
                if(ObjectUtil.isNotEmpty(baseInfo.getProjCosponsorUserIds())){
                    projCosponsorUserIds.addAll(JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
                }
            });
        }
        boolean needChairman = false;
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            needChairman = SpringUtil.getBean(CalBoardRuleHelper.class).contractNeedChairmanApprove(contractBaseInfo.getId());
            if (needChairman) {
                break;
            }
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", bizDeptLeader) ,
                Pair.of("bizDivisionLeader", bizDivisionLeader),
                Pair.of("contractNeedChairmanApprove", needChairman),
                // 是否经过董事长条件调整，新逻辑根据合同是否经过来确定（有一个合同经过则罚息减免需要过董事长），老逻辑保留是为了兼容历史流程
                Pair.of(ProcessVarEnum.applyCreditAmount.name(), NumberUtil.div(maxApplyCreditAmount.toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).doubleValue())

        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(penaltyReduceBaseInfo.getId()));
        //项目类型
        startProcessReq.setSubModule(ProcessModelTypeEnum.NewRentCollectionExemptionFlow.name());
        startProcessReq.setProcessInstanceName(String.join("-",LocalDate.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)), ProcessModelTypeEnum.NewRentCollectionExemptionFlow.getDisplay()));
        startProcessReq.setCcUserIdList(projCosponsorUserIds);
        return startProcessReq;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void penaltyReductionModify(PenaltyReduceDetailModifyREQ req){
        PenaltyReduceBaseInfo baseInfo = penaltyReduceBaseInfoMapper.selectById(req.getId());
        this.commonCheck(baseInfo);
        LambdaUpdateWrapper<PenaltyReduceBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PenaltyReduceBaseInfo::getNotes, req.getNotes());
        updateWrapper.eq(PenaltyReduceBaseInfo::getId, req.getId());
        penaltyReduceBaseInfoMapper.update(null, updateWrapper);
        if (ObjectUtil.isNotEmpty(req.getItems())) {
            List<PenaltyReduceDetailRecord> reduceDetailRecords = new ArrayList<>();
            req.getItems().forEach(item -> {
                reduceDetailRecords.add(getPenaltyReduceDetailRecordByReduceItem(item, baseInfo));
            });
            penaltyReduceDetailRecordService.updateBatchById(reduceDetailRecords);
        }
    }

    private PenaltyReduceDetailRecord getPenaltyReduceDetailRecordByReduceItem(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem item, PenaltyReduceBaseInfo baseInfo, String... ignoreProperties ) {
        PenaltyReduceDetailRecord reduceDetailRecord = BeanUtil.copyProperties(item, PenaltyReduceDetailRecord.class, ignoreProperties);
        reduceDetailRecord.setReduceBaseId(baseInfo.getId());
        return reduceDetailRecord;
    }

    public RentCollectionPenaltyReduceDetailRSP getPenaltyReduceDetailByBaseId(Long reduceBaseId) {
        PenaltyReduceBaseInfo penaltyReduceBaseInfo = penaltyReduceBaseInfoMapper.selectById(reduceBaseId);
        if (ObjectUtil.isEmpty(penaltyReduceBaseInfo)) {
            return null;
        }
        List<PenaltyReduceDetailRecord> penaltyReduceDetailRecords = penaltyReduceDetailRecordService.list(Wrappers.<PenaltyReduceDetailRecord>lambdaQuery()
                .eq(PenaltyReduceDetailRecord::getReduceBaseId, penaltyReduceBaseInfo.getId()));
        RentCollectionPenaltyReduceDetailRSP rentCollectionPenaltyReduceDetailRSP = new RentCollectionPenaltyReduceDetailRSP();
        rentCollectionPenaltyReduceDetailRSP.setId(penaltyReduceBaseInfo.getId());
        rentCollectionPenaltyReduceDetailRSP.setNotes(penaltyReduceBaseInfo.getNotes());
        List<RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem> items = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(penaltyReduceDetailRecords)) {
            Map<Long, String> clientId2Name = id2NameService.clientId2Name(penaltyReduceDetailRecords.stream().map(PenaltyReduceDetailRecord::getClientId).collect(Collectors.toList()));
            penaltyReduceDetailRecords.forEach(e -> {
                RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem rentCollectionPenaltyReduceItem = BeanUtil.copyProperties(e, RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem.class);
                rentCollectionPenaltyReduceItem.setClientName(clientId2Name.get(e.getClientId()));
                items.add(rentCollectionPenaltyReduceItem);
            });
        }
        rentCollectionPenaltyReduceDetailRSP.setItems(items);
        return rentCollectionPenaltyReduceDetailRSP;
    }

    public RentCollectionPenaltyReduceDetailRSP getPenaltyReduceDetailByCollectionId(List<Long> collectionIds) {
        if(ObjectUtil.isEmpty(collectionIds)) {
            return null;
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectBatchIds(collectionIds);
        if(ObjectUtil.isEmpty(collectionBaseInfos)) {
            return null;
        }
        RentCollectionPenaltyReduceDetailRSP rsp = new RentCollectionPenaltyReduceDetailRSP();
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(collectionBaseInfos.stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toList()));
        //获取合同金额
        Map<Long, Long> contractId2Amount = contractBaseInfoService.listByIds(collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList())).stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getApplyCreditAmount, (a, b) -> a));
        List<RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem> items = new ArrayList<>(collectionBaseInfos.size());
        LocalDate penaltyCloseDate = LocalDate.now().minusDays(1);
        collectionBaseInfos.forEach(e -> {
            RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem item = BeanUtil.copyProperties(e, RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem.class, "id");
            item.setCollectionId(e.getId());
            item.setClientName(clientId2Name.get(e.getClientId()));
            item.setApplyCreditAmount(contractId2Amount.get(e.getContractId()));
            //补充罚息截止日期
            item.setPenaltyCloseDate(penaltyCloseDate);
            items.add(item);
        });
        rsp.setItems(items);
        return rsp;
    }

    public Long getMaxContractAmountByReduceId(Long reduceId) {
        RentCollectionPenaltyReduceDetailRSP penaltyReduceDetailByBaseId = this.getPenaltyReduceDetailByBaseId(reduceId);
        if (ObjectUtil.isEmpty(penaltyReduceDetailByBaseId) || ObjectUtil.isEmpty(penaltyReduceDetailByBaseId.getItems())) {
            return 0L;
        }
        return Optional.of(penaltyReduceDetailByBaseId.getItems().stream().filter(e -> ObjectUtil.isNotEmpty(e.getApplyCreditAmount())).max(Comparator.comparing(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem::getApplyCreditAmount)).map(RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem::getApplyCreditAmount)).map(Optional::get).orElse(0L);
    }

    private void commonCheck(PenaltyReduceBaseInfo baseInfo) {
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equals(baseInfo.getPenaltyReduceStatus(), ProcessState.PASS.name())) {
            throw new MithrasException("已生效，不支持修改");
        }
    }

}