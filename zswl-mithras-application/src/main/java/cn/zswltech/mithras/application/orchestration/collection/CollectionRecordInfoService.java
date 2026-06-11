package cn.zswltech.mithras.application.orchestration.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.collection.application.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailRSP;
import cn.zswltech.mithras.dto.collection.CollectionRecordListREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.collection.convert.CollectionRecordInfoConverter;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.collection.enums.CollectionRecordWriteOffStatus;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.payment.enums.PaymentMethod;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.enums.*;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionWriteOffRecordMapper;
import cn.zswltech.mithras.fund.mapper.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.fund.model.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionWriteOffRecord;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.contract.overdue.application.collection.OverdueCollectionRefreshService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.contract.event.ContractPriceChangeEvent;
import cn.zswltech.mithras.application.orchestration.listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.budget.application.EclExecuteClientPromotionResultService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.application.orchestration.third.FinanceFlowRecordService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2CollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PlanCollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.SyncCqReqBizInfo;
import cn.zswltech.mithras.third.financialshare.enums.*;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @create: 2022-08-20
 **/
@Slf4j
@Service
public class CollectionRecordInfoService extends ServiceImpl<CollectionRecordInfoMapper, CollectionRecordInfo> {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    //    @Resource
//    private MaterialsListService materialsListService;
    @Resource
    private CollectionWriteOffRecordMapper collectionWriteOffRecordMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    //    @Resource
//    private ApplicationContext applicationContext;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;

    @Resource
    private CollectionRecordInfoConverter collectionRecordInfoConverter;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private OverdueCollectionRefreshService overdueCollectionRefreshService;


//    @SneakyThrows
//    @Transactional(rollbackFor = Exception.class)
//    public R<String> add(CollectionRecordInfo info, MultipartFile file){
//        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(info.getCollectionId());
////        if (hasWriteOffUser(baseInfo)) {
////            return R.fail("汇总核销进行中不允许新增!");
////        }
//        if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())){
//            info.setPrincipal(null);
//            info.setInterest(null);
//            info.setPenaltyInterest(null);
//        }
//        info.setWriteOffStatus(CollectionRecordWriteOffStatus.TO_BE_WRITE_OFF.name());
//
//        if (file != null) {
//            Long materialsId = materialsListService.add(file.getInputStream(), file.getOriginalFilename(), info.getId(), "MARGIN_PROVE", BusinessType.MARGIN_RECORD.name());
//            info.setEnclosureId(materialsId);
//            info.setEnclosureName(file.getOriginalFilename());
//        }
//        baseInfo.setAllRecordSort(LongUtil.null2zero(baseInfo.getAllRecordSort())+1);
//        info.setSortId(baseInfo.getAllRecordSort());
//        if (CollectionWriteOffStatusEnum.UNCOLLECTION.name().equals(baseInfo.getWriteOffStatus())){
//            baseInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
//        }
//        collectionBaseInfoMapper.updateById(baseInfo);
//        collectionRecordInfoMapper.insert(info);
//        return R.ok();
//    }

//    @SneakyThrows
//    @Transactional(rollbackFor = Throwable.class)
//    public R<String> modify(CollectionRecordAddREQ info, MultipartFile file){
//        CollectionRecordInfo recordInfo = collectionRecordInfoMapper.selectById(info.getId());
//        if (recordInfo.getWriteOffStatus().equals(CollectionRecordWriteOffStatus.WRITTEN_OFF.name())){
//            return R.fail("已核销不可修改!");
//        }
//        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(recordInfo.getCollectionId());
//        if (hasWriteOffUser(baseInfo)) {
//            return R.fail("汇总核销进行中不允许修改!");
//        }
//        if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())){
//            info.setPrincipal(null);
//            info.setInterest(null);
//            info.setPenaltyInterest(null);
//        }
//        Long oldenclosureId = recordInfo.getEnclosureId();
//        recordInfo.setCollectionDate(info.getCollectionDate());
//        recordInfo.setCollectionAmount(info.getCollectionAmount());
//        recordInfo.setPrincipal(info.getPrincipal());
//        recordInfo.setInterest(info.getInterest());
//        recordInfo.setPenaltyInterest(info.getPenaltyInterest());
//        recordInfo.setPostscript(info.getPostscript());
//        recordInfo.setEnclosureId(info.getEnclosureId());
//        recordInfo.setEnclosureName(info.getEnclosureName());
//        recordInfo.setOurAccountId(info.getOurAccountId());
//        recordInfo.setOurAccountName(info.getOurAccountName());
//        recordInfo.setOurAccountNumber(info.getOurAccountNumber());
//        recordInfo.setOurAccountBank(info.getOurAccountBank());
//        if (file != null) {
//            Long materialsId = materialsListService.add(file.getInputStream(), file.getOriginalFilename(), info.getId(), "MARGIN_PROVE", BusinessType.MARGIN_RECORD.name());
//            recordInfo.setEnclosureId(materialsId);
//            recordInfo.setEnclosureName(file.getOriginalFilename());
//            materialsListService.remove(Collections.singletonList(oldenclosureId));
//        }
//        collectionRecordInfoMapper.updateById(recordInfo);
//        return R.ok();
//    }

//    private boolean hasWriteOffUser(CollectionBaseInfo baseInfo) {
//        String writeOffUserIdsJson = baseInfo.getWriteOffUserIds();
//        List<Long> writeOffUserIds = JSON.parseObject(writeOffUserIdsJson, new TypeReference<List<Long>>() {
//        });
//        if (ObjectUtil.isNotEmpty(writeOffUserIds)) {
//            return true;
//        }
//        return false;
//    }

    public List<CollectionRecordInfo> listByCollectionIds(Collection<Long> collectionIds) {
        if (ObjectUtil.isEmpty(collectionIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CollectionRecordInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionRecordInfo::getCollectionId, collectionIds);
        return this.list(query);
    }

    public List<CollectionRecordInfo> listByCollectionIdsAndPlanDate(Collection<Long> collectionIds, LocalDate planDate) {
        if (ObjectUtil.isEmpty(collectionIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CollectionRecordInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionRecordInfo::getCollectionId, collectionIds);
        query.le(CollectionRecordInfo::getCollectionDate, planDate);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public R<String> addRecord(CollectionRecordInfo info) {
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(info.getCollectionId());
        if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())
                || CashFlowItemEnum.EARNEST_MONEY.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.EARLY_STOP_COMPENSATION.name().equals(baseInfo.getCashFlowItem())) {
            info.setPrincipal(null);
            info.setInterest(null);
            info.setPenaltyInterest(null);
        }

        baseInfo.setAllRecordSort(LongUtil.null2zero(baseInfo.getAllRecordSort()) + 1);
        info.setSortId(baseInfo.getAllRecordSort());
        baseInfo.setCollectionAmount(LongUtil.null2zero(baseInfo.getCollectionAmount()) + LongUtil.null2zero(info.getCollectionAmount()));
        if (baseInfo.getCollectionDate() == null || baseInfo.getCollectionDate().isBefore(info.getCollectionDate())) {
            baseInfo.setCollectionDate(info.getCollectionDate());
        }
        baseInfo.setCollectionPrincipal(LongUtil.null2zero(baseInfo.getCollectionPrincipal()) + LongUtil.null2zero(info.getPrincipal()));
        baseInfo.setCollectionInterest(LongUtil.null2zero(baseInfo.getCollectionInterest()) + LongUtil.null2zero(info.getInterest()));
        baseInfo.setCollectionPenaltyInterest(LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) + LongUtil.null2zero(info.getPenaltyInterest()));
        if (baseInfo.getCollectionAmount() > 0 && baseInfo.getCollectionAmount() < (baseInfo.getCashFlowAmount() + baseInfo.getPenaltyInterest())) {
            baseInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name());
        } else if (baseInfo.getCollectionAmount() > 0 && baseInfo.getCollectionAmount() == (baseInfo.getCashFlowAmount() + baseInfo.getPenaltyInterest())) {
            baseInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        }
        collectionBaseInfoMapper.updateById(baseInfo);
        collectionRecordInfoMapper.insert(info);
        return R.ok();
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public synchronized CollectionRecordInfo financialAdd(ThirdCollectionRecordREQ req) {
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCode, req.getCollectionCode()));
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException("无此收款记录 " + req.getCollectionCode());
        }
        CollectionRecordInfo info = collectionRecordInfoConverter.financialRecordREQToBean(req);
        info.setCollectionId(baseInfo.getId());
        if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())
                || CashFlowItemEnum.EARNEST_MONEY.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.EARLY_STOP_COMPENSATION.name().equals(baseInfo.getCashFlowItem())) {
            info.setPrincipal(null);
            info.setInterest(null);
            info.setPenaltyInterest(null);
        }
        //乘10000
        info.setPrincipal(LongUtil.other2Long(ObjectUtil.isNull(req.getPrincipal()) ? null : req.getPrincipal().toString()));
        info.setCollectionAmount(LongUtil.other2Long(ObjectUtil.isNull(req.getCollectionAmount()) ? null : req.getCollectionAmount().toString()));
        info.setInterest(LongUtil.other2Long(ObjectUtil.isNull(req.getInterest()) ? null : req.getInterest().toString()));
        info.setPenaltyInterest(LongUtil.other2Long(ObjectUtil.isNull(req.getPenaltyInterest()) ? null : req.getPenaltyInterest().toString()));
        //实收金额不包括罚息金额
        info.setCollectionAmount(info.getCollectionAmount() - info.getPenaltyInterest());
        info.setWriteOffStatus(CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
        info.setBankDetailNo(req.getBankDetailNo());
        info.setFinanceFlowId(req.getFinanceFlowId());
        //基本收款时间 这里先修改为当前时间-核销时会根据反核销修正为最晚核销日期
        baseInfo.setCollectionDate(req.getCollectionDate());
        //实际收款金额
        baseInfo.setCollectionAmount(LongUtil.null2zero(baseInfo.getCollectionAmount()) + info.getCollectionAmount());
        //实收本金
        baseInfo.setCollectionPrincipal(LongUtil.null2zero(baseInfo.getCollectionPrincipal()) + info.getPrincipal());
        //实收利息
        baseInfo.setCollectionInterest(LongUtil.null2zero(baseInfo.getCollectionInterest()) + info.getInterest());
        //实收罚息
        baseInfo.setCollectionPenaltyInterest(LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) + LongUtil.null2zero(info.getPenaltyInterest()));
        baseInfo.setAllRecordSort(LongUtil.null2zero(baseInfo.getAllRecordSort()) + 1);
        info.setSortId(baseInfo.getAllRecordSort());
        //维护核销方式
        if (ObjectUtil.equals(req.getDataSource(), WriteOffTypeEnum.MANUAL_RECORD.display())) {
            info.setWriteOffType(WriteOffTypeEnum.MANUAL_RECORD.name());
        }
        /*//苍穹核销后变更通知
        baseInfo.setNoticeFinancialFlag(YesOrNoNumberEnum.NO.getCode());*/
        collectionBaseInfoMapper.updateById(baseInfo);
        collectionRecordInfoMapper.insert(info);
        addCollectionToFinanceWriteOffDetail(FinanceFlowDetailTableEnum.COLLECTION_RECORD_INFO.name(), info);
        try {
            if (Objects.equals(info.getCollectionType(), GlobalConstants.CQ_PAYMENT_METHOD_PJ) && !ObjectUtil.equals(req.getDataSource(), WriteOffTypeEnum.MANUAL_RECORD.display()) && Objects.nonNull(info.getCollectionAmount()) && info.getCollectionAmount() > 0) {
                billManagementService.initAfterCqNotify(info.getId(), BillTypeEnum.COLLECTION.name());
            }
            //通知变更
            ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, baseInfo.getContractId()));
        } catch (Exception e) {
            log.error("收款核销增加默认票据信息异常[recordInfo:{}]", JSONUtil.toJsonStr(info), e);
        }
        //通知重新计算罚息
        //分离事物
        log.info("重新计算罚息入参数， {}", baseInfo.getId());
        SpringContextHolder.getBean(CollectionOverdueRecordInfoService.class).doRerunPenaltyInterest(Collections.singletonList(baseInfo.getId()));
//        try {
//
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCommit() {
//
//                }
//            });
//        } catch (Exception e) {
//            log.error("重新计算罚息异常", e);
//        }
        return info;
    }

    private void addCollectionToFinanceWriteOffDetail(String tableName, CollectionRecordInfo info) {
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setRecordMainTable(tableName);
        financeFlowWriteOffDetail.setMainId(info.getId());
        financeFlowWriteOffDetail.setBankDetailNo(info.getBankDetailNo());
        financeFlowWriteOffDetail.setFinanceFlowId(info.getFinanceFlowId());
        getBean(FinanceFlowWriteOffDetailMapper.class).insert(financeFlowWriteOffDetail);
    }

    //这里单条核销打标
    @Transactional(rollbackFor = Throwable.class)
    public void cancelWriteRecord(CollectionRecordInfo info) {
        if (LongUtil.null2zero(info.getCollectionAmount()) >= 0) {
            //收款大于0不需要查找对应反核销期限
            return;
        }
        //查找对应反核销记录
        CollectionRecordInfo collectionRecordInfo = collectionRecordInfoMapper.selectOne(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, info.getCollectionId())
                .eq(CollectionRecordInfo::getCancelWriteOffFlag, YesOrNoNumberEnum.NO.getCode())
                .eq(CollectionRecordInfo::getCollectionDate, info.getCollectionDate())
                .eq(CollectionRecordInfo::getCollectionAmount, Math.abs(LongUtil.null2zero(info.getCollectionAmount())))
                .eq(CollectionRecordInfo::getPrincipal, Math.abs(LongUtil.null2zero(info.getPrincipal())))
                .eq(CollectionRecordInfo::getInterest, Math.abs(LongUtil.null2zero(info.getInterest())))
                .orderByAsc(CollectionRecordInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(collectionRecordInfo)) {
            collectionRecordInfo.setCancelWriteOffFlag(YesOrNoNumberEnum.YES.getCode());
            info.setCancelWriteOffFlag(YesOrNoNumberEnum.YES.getCode());
            info.setCancelWriteOffId(collectionRecordInfo.getId());
            collectionRecordInfo.setCancelWriteOffId(info.getId());
            collectionRecordInfoMapper.updateById(info);
            collectionRecordInfoMapper.updateById(collectionRecordInfo);

            //财务在系统反结算后发通知给征信报送岗
            getBean(CollectionRecordInfoService.class).sendMessage(collectionRecordInfo);
        }
    }

    public void sendMessage(CollectionRecordInfo collectionRecordInfo) {
        SysUserService userService = getBean(SysUserService.class);
        Set<Long> userIds = userService.getUserIdsByRole("ZXBSGL");
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionRecordInfo.getCollectionId());
        ContractBaseInfo infoServiceById = contractBaseInfoService.getById(collectionBaseInfo.getContractId());
        Map<Long, String> longStringMap = id2NameService.clientId2Name(Collections.singleton(infoServiceById.getClientId()));
        String name = longStringMap.getOrDefault(infoServiceById.getClientId(), "");
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setMessageType(MessageTypeEnum.FINANCIAL_ANTI_SETTLEMENT_NOTICE.name());
                if (CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    messageAddREQ.setRelation(String.format("%s-【%s】-%s-期项%s进行了反核销处理，请及时关注！",
                            name, infoServiceById.getContractCode(), CashFlowItemEnum.RENT.display,
                            collectionBaseInfo.getPhase()));
                }

                if (CashFlowItemEnum.EARNEST_MONEY.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    messageAddREQ.setRelation(String.format("%s【%s】-%s进行了反核销处理，请及时关注！",
                            name, infoServiceById.getContractCode(), CashFlowItemEnum.EARNEST_MONEY.display));
                }
                messageAddREQ.setContent(String.valueOf(collectionBaseInfo.getCode()));
                messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.COLLECTION_COMPLETE.pcUrl, collectionBaseInfo.getId()));
                messageAddREQ.setTo(Collections.singletonList(userId));
                messageAddREQ.setNeedQa(false);
                messageAddREQ.setNoticeSource(NoticeSourceENUM.CREDIT_REPORT.name());
                messageService.sendMessage(getBean(MessageConver.class).reqToMessage(messageAddREQ));
            }
        }
    }

    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(Long id) {
        CollectionRecordInfo collectionRecordInfo = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(collectionRecordInfo)) {
            return;
        }
        LambdaUpdateWrapper<CollectionRecordInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CollectionRecordInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(CollectionRecordInfo::getId, collectionRecordInfo.getId());
        baseMapper.update(null, updateWrapper);
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(collectionRecordInfo.getCollectionId());
        if (ObjectUtil.isNotEmpty(baseInfo)) {
            //计算baseinfo状态
            //检查核销状态
            Long amount = calculateCollectionAmount(Collections.singletonList(baseInfo.getId()));
            amount = amount - LongUtil.null2zero(collectionRecordInfo.getCollectionAmount());
            CollectionWriteOffStatusEnum collectionWriteOffStatusEnum;
            long applayMount = LongUtil.null2zero(baseInfo.getCashFlowAmount()) + LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount());
            if (amount <= 0) {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.UNCOLLECTION;
            } else if (amount < applayMount) {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF;
            } else {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;
            }
            baseInfo.setWriteOffStatus(collectionWriteOffStatusEnum.name());
            //除去反核销数据后，计算最晚核销时间
            LocalDate lastWriteDate = getLastWriteDate(collectionRecordInfo.getCollectionId());
            baseInfo.setCollectionDate(lastWriteDate);
            //实际收款金额
            baseInfo.setCollectionAmount(LongUtil.null2zero(baseInfo.getCollectionAmount()) - collectionRecordInfo.getCollectionAmount());
            //实收本金
            baseInfo.setCollectionPrincipal(LongUtil.null2zero(baseInfo.getCollectionPrincipal()) - collectionRecordInfo.getPrincipal());
            //实收利息
            baseInfo.setCollectionInterest(LongUtil.null2zero(baseInfo.getCollectionInterest()) - collectionRecordInfo.getInterest());
            //实收罚息
            baseInfo.setCollectionPenaltyInterest(LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) - LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest()));
            baseInfo.setAllRecordSort(LongUtil.null2zero(baseInfo.getAllRecordSort()) + 1);
            collectionBaseInfoMapper.updateById(baseInfo);
        }
        financeFlowRecordService.withdrawBankFlow(collectionRecordInfo.getId(), FinanceFlowDetailTableEnum.COLLECTION_RECORD_INFO.name(), LongUtil.null2zero(collectionRecordInfo.getCollectionAmount()) + LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest()));
    }


    @Transactional(rollbackFor = Throwable.class)
    public void financialWriteOff(Long collectionId, Long recordId, String operateInfo, String cashFlowItem, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(collectionId);
        //检查核销状态
        Long amount = calculateCollectionAmount(Collections.singletonList(baseInfo.getId()));
        CollectionWriteOffStatusEnum collectionWriteOffStatusEnum;
        Long applayMount = LongUtil.null2zero(baseInfo.getCashFlowAmount()) + LongUtil.null2zero(baseInfo.getPenaltyInterest());
        if (amount <= 0) {
            collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.UNCOLLECTION;
        } else if (amount < applayMount) {
            collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF;
        } else if (ObjectUtil.equals(amount, applayMount)) {
            collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;
        } else {
            //todo 可能存在尾数精度问题，暂时认为大于也是核销完毕，手工核销认为这里不再核销，后续可能做日志记录
            //collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;
            throw new MithrasException("本次核销将导致 累计核销⾦额⼤于计划⾦额，操作失败！");
        }
        baseInfo.setWriteOffStatus(collectionWriteOffStatusEnum.name());
        //除去反核销数据后，计算最晚核销时间
        LocalDate lastWriteDate = getLastWriteDate(collectionId);
        if (lastWriteDate != null) {
            baseInfo.setCollectionDate(lastWriteDate);
        }
        collectionBaseInfoMapper.updateById(baseInfo);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(baseInfo.getWriteOffStatus())) {
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setMessageType(MessageTypeEnum.COLLECTION.name());
            String clientName = id2NameService.clientId2Name(Collections.singleton(baseInfo.getClientId())).get(baseInfo.getClientId());
            messageAddREQ.setRelation(clientName + "的" + contractBaseInfo.getContractCode());
            messageAddREQ.setContent(baseInfo.getCode());
            messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.COLLECTION_COMPLETE.pcUrl, baseInfo.getId()));
            Set<Long> to = new HashSet<>();
            if (contractBaseInfo.getProjSponsorUserId() != null) {
                to.add(contractBaseInfo.getProjSponsorUserId());
            }
            if (contractBaseInfo.getBizDeptLeaderId() != null) {
                to.add(contractBaseInfo.getBizDeptLeaderId());
            }
            messageAddREQ.setTo(new ArrayList<>(to));
            messageAddREQ.setNeedOa(true);
            messageAddREQ.setNoticeSource(NoticeSourceENUM.PAYMENT.name());
            messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
        }

        if (isNotNull(collectionCode)){
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getCode, collectionCode)
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                            CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())));
            if (!collectionBaseInfoList.isEmpty()) {
                Set<Long> userIds = new HashSet<>();
                CollectionBaseInfo info = collectionBaseInfoList.get(0);
                //法律合规部
                Set<Long> flhgbSet = sysUserService.getUserByDeptCode("FLHGB_ZCBQ").stream().map(UserDO::getId).collect(Collectors.toSet());
                //财务部
                Set<Long> jhcwbSet = sysUserService.getUserByDeptCode("JHCWB").stream().map(UserDO::getId).collect(Collectors.toSet());
                //资金部
                Set<Long> zjglbSet = sysUserService.getUserByDeptCode("ZJGLB").stream().map(UserDO::getId).collect(Collectors.toSet());
                userIds.addAll(flhgbSet);
                userIds.addAll(jhcwbSet);
                userIds.addAll(zjglbSet);
                if (isNotNull(contractBaseInfo.getProjSponsorUserId())) {
                    userIds.add(contractBaseInfo.getProjSponsorUserId());
                }
                if (isNotNull(contractBaseInfo.getProjCosponsorUserIds())) {
                    List<Long> cosponsorUserIds = JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class);
                    userIds.addAll(cosponsorUserIds);
                }
                if (isNotNull(contractBaseInfo.getBizDeptLeaderId())) {
                    userIds.add(contractBaseInfo.getBizDeptLeaderId());
                }
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setMessageType(MessageTypeEnum.RENT_RECEIVED.name());
                String clientName = id2NameService.clientId2Name(Collections.singleton(contractBaseInfo.getClientId())).get(contractBaseInfo.getClientId());
                BigDecimal cashFlowAmountB = LongUtil.tenThousand2Dollar(String.valueOf(info.getCashFlowAmount())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal paidInAmountB = LongUtil.tenThousand2Dollar(String.valueOf(paidInAmount)).setScale(2, RoundingMode.HALF_UP);
                messageAddREQ.setRelation(String.format("%s-%s-第%s期租金%s元于%s到账%s元，请知悉!",
                        clientName, contractBaseInfo.getContractCode(), info.getPhase(), cashFlowAmountB, paidInDate, paidInAmountB));
                messageAddREQ.setContent(info.getCode());
                messageAddREQ.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", info.getId()));
                messageAddREQ.setTo(new ArrayList<>(userIds));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setNoticeSource(NoticeSourceENUM.RENT_RECEIVED.name());
                messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
            }
        }

        if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
            Integer count = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, baseInfo.getContractId())
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED));
            MarginBaseInfo info = marginBaseInfoMapper.selectOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                    .eq(MarginBaseInfo::getContractId, baseInfo.getContractId()));
            if (count == 0 && (info == null || info.getCollectionAmount() <= 0)) {
                log.info("收款核销完毕，通知合同执行结清操作[contractId: {}]", baseInfo.getContractId());
                //这里修改为正常结清通过可结清，提前结清的需在结清确认流程中
                ProcessPageReq req = new ProcessPageReq();
                req.setBusinessKey(String.valueOf(baseInfo.getContractId()));
                req.setPageIndex(1);
                req.setPageSize(1);
                req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleFlow.name());
                req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                        .stream().findFirst().orElse(null);
                if (ObjectUtil.isEmpty(processResp)) {
                    contractBaseInfoService.contractSettle(baseInfo.getContractId());
                } else {
                    req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
                    req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                    if (ObjectUtil.isNotEmpty(flowTaskApiService.queryProcess(req).getContents()
                            .stream().findFirst().orElse(null))) {
                        contractBaseInfoService.contractSettle(baseInfo.getContractId());
                    } else {
                        req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
                        req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                        if (ObjectUtil.isNotEmpty(flowTaskApiService.queryProcess(req).getContents()
                                .stream().findFirst().orElse(null))) {
                            contractBaseInfoService.contractSettle(baseInfo.getContractId());
                        }
                    }
                }
            }
        }
        // 记录日志
        CollectionWriteOffRecord history = new CollectionWriteOffRecord();
        history.setReceiptStatus(baseInfo.getWriteOffStatus());
        history.setCollectionId(collectionId);
        history.setRecordId(recordId);
        history.setOperateInfo(operateInfo);
        history.setOperate(collectionWriteOffStatusEnum.display);
        collectionWriteOffRecordMapper.insert(history);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 发送指标速算事件
                MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
                metricComputeEventBus.post(metricComputeEvent);
                // 更新逾期催收表
                overdueCollectionRefreshService.refreshClient(baseInfo.getClientId());
                //给苍穹推送应收单
                if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(baseInfo.getWriteOffStatus())) {
                    List<CollectionRecordInfo> allCollectionRecordInfos = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                            .eq(CollectionRecordInfo::getCollectionId, baseInfo.getId())
                            .ne(CollectionRecordInfo::getCancelWriteOffFlag, YesOrNoNumberEnum.YES.getCode())
                            .orderByAsc(CollectionRecordInfo::getCollectionDate));
                    //核销完毕且流水为空的时候推送
                    if (ObjectUtil.isNotEmpty(allCollectionRecordInfos) && allCollectionRecordInfos.stream().filter(e -> ObjectUtil.isEmpty(e.getBankDetailNo())).count() == allCollectionRecordInfos.size()) {
                        //换聚合维度为收款
                        StringBuilder sb = new StringBuilder();
                        CollectionRecordInfo collectionRecordInfo;
                        Set<String> flowSet = new HashSet<>();
                        for (int i = 0; i < allCollectionRecordInfos.size(); i++) {
                            collectionRecordInfo = allCollectionRecordInfos.get(i);
                            if (flowSet.contains(collectionRecordInfo.getBankDetailNo())) {
                                continue;
                            }
                            flowSet.add(collectionRecordInfo.getBankDetailNo());
                            //银行流水为空，填充ID
                            sb.append(baseInfo.getCode());
                            sb.append("-");
                            sb.append(collectionRecordInfo.getId());
                            if (i < allCollectionRecordInfos.size() - 1) {
                                sb.append(",");
                            }
                        }
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
                            // 移除最后一个字符
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        //Map<String, List<CollectionRecordInfo>> collectionMap = allCollectionRecordInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getBankDetailNo())).collect(Collectors.groupingBy(CollectionRecordInfo::getBankDetailNo));
                        SyncCqReqBizInfo bizInfo = SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(baseInfo.getContractId());
                        //应收单中的 客户编码，需关联【合同管理】模块中的 租金往来方，取资金往来方的客户编码。
                        ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                                .eq(ContractTenantry::getContractId, baseInfo.getContractId())
                                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                                .last(StringUtil.mysqlLimitOne()));
                        if(ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())){
                            Client client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
                            if(ObjectUtil.isNotEmpty(client)){
                                bizInfo.setCustomer(client.getClientCode());
                                bizInfo.setCustomerName(client.getClientName());
                            }
                        }
                        CQ2PlanCollectionVO cq2PlanCollectionVO = buildPlanCollectionReq(baseInfo, allCollectionRecordInfos, bizInfo, sb.toString());
                        //罚息单独拆分
                        CQ2PlanCollectionVO penaltyInterestPlanCollectionVO = buildPenaltyInterestPlanCollectionReq(cq2PlanCollectionVO, baseInfo, allCollectionRecordInfos, bizInfo);
                        List<CQ2PlanCollectionVO> collectionVOS = new ArrayList<>();
                        collectionVOS.add(cq2PlanCollectionVO);
                        if (ObjectUtil.isNotEmpty(penaltyInterestPlanCollectionVO)) {
                            collectionVOS.add(penaltyInterestPlanCollectionVO);
                        }
                        //20251218名义价款需要把 保证金额抵扣部分拆分出来
                        CQ2PlanCollectionVO nominalPriceCollectionVo = buildNomnalPricePlanCollectionReq(cq2PlanCollectionVO, baseInfo, allCollectionRecordInfos, bizInfo);
                        if (ObjectUtil.isNotEmpty(nominalPriceCollectionVo)) {
                            collectionVOS.add(nominalPriceCollectionVo);
                        }
                        financialManagerServiceImpl2.planCollectionExec(collectionVOS);
                    }
                }
                //维护客户上迁数据
                SpringContextHolder.getBean(EclExecuteClientPromotionResultService.class).savePromotionResult(Collections.singletonList(baseInfo.getClientId()), 6);
            }
        });
    }


    //收款单 这里也要查询是否有保证金抵扣相关数据
    public List<CQ2CollectionVO> getCollectionVO(List<Long> ids){
        if(ObjectUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<CollectionRecordInfo> collectionRecordInfos = baseMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
        .in(CollectionRecordInfo::getId, ids)
        .isNotNull(CollectionRecordInfo::getBankDetailNo));
        if (ObjectUtil.isEmpty(collectionRecordInfos)) {
            return Collections.emptyList();
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectBatchIds(collectionRecordInfos.stream().map(CollectionRecordInfo::getCollectionId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return Collections.emptyList();
        }
        List<CQ2CollectionVO> rsps = new ArrayList<>();
        Map<Long, CollectionBaseInfo> collectionId2Bean = collectionBaseInfos.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, List<CollectionRecordInfo>> collectionId2RecordMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        //收款维度
        collectionId2RecordMap.forEach((collectionId, recordInfos) -> {
            CollectionBaseInfo collectionBaseInfo = collectionId2Bean.get(collectionId);
            if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                SyncCqReqBizInfo bizInfo = SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(collectionBaseInfo.getContractId());
                //收款单中的 付款人名称、客户编码，如果付款人类型是“bd_supplier”、“bd_customer”------提供客户（租金往来方）名称
                ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                        .eq(ContractTenantry::getContractId, collectionBaseInfo.getContractId())
                        .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
                    Client client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
                    if (ObjectUtil.isNotEmpty(client)) {
                        bizInfo.setCustomer(client.getClientCode());
                        bizInfo.setCustomerName(client.getClientName());
                    }
                }
                rsps.add(buildCollection(collectionBaseInfo, recordInfos, bizInfo, recordInfos.get(0).getBankDetailNo()));
            }
        });
        return rsps;
    }

    public List<CQ2PlanCollectionVO> buildPlanCollectionReqByCollectionRecord(List<Long> collectionRecordIds) {
        if (ObjectUtil.isEmpty(collectionRecordIds)) {
            return ListUtil.empty();
        }
        //查询该流水下所有收款
        List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoMapper.selectBatchIds(collectionRecordIds);
        if(ObjectUtil.isEmpty(collectionRecordInfos)) {
            return ListUtil.empty();
        }
        Map<Long, List<CollectionRecordInfo>> collectId2RecordMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        //补充抵扣的钱，产生一正一负数据
        this.suppleOtherRecord(collectId2RecordMap);
        return getCQ2PlanCollectionByMap(collectId2RecordMap);
    }

    //判断是否包含第一笔银行流水收款
    private void suppleOtherRecord(Map<Long, List<CollectionRecordInfo>> collectId2RecordMap) {
        //查询所有
        Map<Long, List<CollectionRecordInfo>> collectId2RecordAllMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectId2RecordMap.keySet())
                .orderByAsc(CollectionRecordInfo::getId)).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        collectId2RecordMap.forEach((collectId, records) -> {
            if (ObjectUtil.isNotEmpty(records)) {
                List<CollectionRecordInfo> collectionRecordInfos = collectId2RecordAllMap.get(collectId);
                List<CollectionRecordInfo> collect = collectionRecordInfos.stream().filter(e -> !ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)).collect(Collectors.toList());
                if (collect.size() > 0 && records.stream().map(CollectionRecordInfo::getId).collect(Collectors.toSet()).contains(collect.get(0).getId())) {
                    records.addAll(collectionRecordInfos.stream().filter(e -> ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)).collect(Collectors.toList()));
                }
            }
        });
    }

    private List<CQ2PlanCollectionVO> getCQ2PlanCollectionByMap(Map<Long, List<CollectionRecordInfo>> collectId2RecordMap) {
        Map<Long, CollectionBaseInfo> collectId2Bean = collectionBaseInfoMapper.selectBatchIds(collectId2RecordMap.keySet()).stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
        List<CQ2PlanCollectionVO> rsps = new ArrayList<>();
        collectId2RecordMap.forEach((collectionId, allCollectionRecordInfos) -> {
            CollectionBaseInfo baseInfo = collectId2Bean.get(collectionId);
            //部分收款推应收单
            if (CashFlowItemEnum.needPlanCollection.contains(baseInfo.getCashFlowItem())) {
                SyncCqReqBizInfo bizInfo = SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(baseInfo.getContractId());
                //应收单中的 客户编码，需关联【合同管理】模块中的 租金往来方，取资金往来方的客户编码。
                ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                        .eq(ContractTenantry::getContractId, baseInfo.getContractId())
                        .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
                    Client client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
                    if (ObjectUtil.isNotEmpty(client)) {
                        bizInfo.setCustomer(client.getClientCode());
                        bizInfo.setCustomerName(client.getClientName());
                    }
                }
                CQ2PlanCollectionVO cq2PlanCollectionVO = buildPlanCollectionReq(baseInfo, allCollectionRecordInfos, bizInfo, getBankDetailNo(allCollectionRecordInfos));
                if (ObjectUtil.isNotEmpty(cq2PlanCollectionVO) && ObjectUtil.isNotEmpty(cq2PlanCollectionVO.getEntry()) && cq2PlanCollectionVO.getEntry().size() > 0) {
                    //保证金核销
                    //planCollectionMarginManager(allCollectionRecordInfos, cq2PlanCollectionVO, bizInfo, 2);
                    rsps.add(cq2PlanCollectionVO);
                }
                //罚息
                CQ2PlanCollectionVO penaltyInterestPlanCollectionVO = buildPenaltyInterestPlanCollectionReq(cq2PlanCollectionVO, baseInfo, allCollectionRecordInfos, bizInfo);
                if (ObjectUtil.isNotEmpty(penaltyInterestPlanCollectionVO) && ObjectUtil.isNotEmpty(penaltyInterestPlanCollectionVO.getEntry()) && penaltyInterestPlanCollectionVO.getEntry().size() > 0) {
                    planCollectionMarginManager(allCollectionRecordInfos, penaltyInterestPlanCollectionVO, bizInfo, 1);
                    rsps.add(penaltyInterestPlanCollectionVO);
                }
                //无需特殊处理
                CQ2PlanCollectionVO nominalPriceCollectionVo = buildNomnalPricePlanCollectionReq(cq2PlanCollectionVO, baseInfo, allCollectionRecordInfos, bizInfo);
                if (ObjectUtil.isNotEmpty(nominalPriceCollectionVo)) {
                    rsps.add(nominalPriceCollectionVo);
                }
            }
        });
        return rsps;
    }

    private String getBankDetailNo(List<CollectionRecordInfo> allCollectionRecordInfos) {
        StringBuilder sb = new StringBuilder();
        CollectionRecordInfo collectionRecordInfo;
        Set<String> flowSet = new HashSet<>();
        for (int i = 0; i < allCollectionRecordInfos.size(); i++) {
            collectionRecordInfo = allCollectionRecordInfos.get(i);
            if (flowSet.contains(collectionRecordInfo.getBankDetailNo())) {
                continue;
            }
            flowSet.add(collectionRecordInfo.getBankDetailNo());
            sb.append(collectionRecordInfo.getBankDetailNo());
            if (i < allCollectionRecordInfos.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 构建通用二期 应收单对象
     */
    public CQ2PlanCollectionVO buildPlanCollectionReq(CollectionBaseInfo baseInfo, List<CollectionRecordInfo> collectionRecordInfos, SyncCqReqBizInfo bizInfo, String flowId){
        CQ2PlanCollectionVO cq2PlanCollectionVO = new CQ2PlanCollectionVO();
        cq2PlanCollectionVO.setRequestId(IdUtil.simpleUUID());
        String join = String.join("-", baseInfo.getCode(), UUIDUtil.genUuid());
        cq2PlanCollectionVO.setCico_srcbillno(join.substring(0, Math.min(49, join.length() - 1)));
        cq2PlanCollectionVO.setBizdate(collectionRecordInfos.get(collectionRecordInfos.size() - 1).getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        if(CashFlowItemEnum.RETENTION_MONEY.name().equals(baseInfo.getCashFlowItem())){
            cq2PlanCollectionVO.setAsstacttype(FinancialConstants.BD_SUPPLIER);
        } else {
            cq2PlanCollectionVO.setAsstacttype(FinancialConstants.BD_CUSTOMER);
        }
        cq2PlanCollectionVO.setExchangerate(BigDecimal.valueOf(1));
        cq2PlanCollectionVO.setCico_isinvoice(false);
        cq2PlanCollectionVO.setCurrency_number(FinancialConstants.RMB);
        cq2PlanCollectionVO.setCico_paynum_rby(flowId);
        cq2PlanCollectionVO.setCico_contractnum_number(baseInfo.getContractCode());
        cq2PlanCollectionVO.setDepartment_number(bizInfo.getOrgCode());
        cq2PlanCollectionVO.setAsstact_number(bizInfo.getCustomer());
        // 租金 拆分为本金+利息
        if (CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem())) {
            buildPlanCollectionVOBody(cq2PlanCollectionVO, bizInfo, baseInfo, true, collectionRecordInfos);
        }
        buildPlanCollectionVOBody(cq2PlanCollectionVO, bizInfo, baseInfo, false, collectionRecordInfos);
        //20251218如果是名义价款 这个时候就需要把保证金抵扣部分单独拆分出去
        if (!CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())){
            //构建抵扣的body
            planCollectionMarginManager(collectionRecordInfos, cq2PlanCollectionVO, bizInfo, 2);
        }
        //应收，业务收款维度，故存放collection_base_info 表的数据
        cq2PlanCollectionVO.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
        cq2PlanCollectionVO.setBusinessKey(String.valueOf(baseInfo.getId()));
        cq2PlanCollectionVO.setBusinessTitle(baseInfo.getCode());
        return cq2PlanCollectionVO;
    }

    /**
     * 抵扣应收单添加负数抵消
     * @param type 1 罚息 2其他
     **/
    public void planCollectionMarginManager(List<CollectionRecordInfo> collectionRecordInfos, CQ2PlanCollectionVO cq2PlanCollectionVO, SyncCqReqBizInfo bizInfo, int type) {
        if (ObjectUtil.isEmpty(collectionRecordInfos) || ObjectUtil.isEmpty(cq2PlanCollectionVO) || ObjectUtil.isEmpty(bizInfo)) {
            return;
        }
        //保证金抵扣补充负数金额的数据
        Long marginAmount = 0L;
        for (CollectionRecordInfo e : collectionRecordInfos) {
            if (ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)) {
                if(type == 1) {
                    marginAmount += LongUtil.null2zero(e.getPenaltyInterest());
                } else {
                    marginAmount += LongUtil.null2zero(e.getCollectionAmount());
                }
            }
        }
        if (marginAmount > 0) {
            CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cqCollectionBody = cq2PlanCollectionVO.new CQ2PlanCollectionVOBody();
            cqCollectionBody.setE_quantity(BigDecimal.valueOf(1));
            if (ProjectBizType.ZL.name().equals(bizInfo.getBizType()) && LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())) {
                cqCollectionBody.setE_taxrate(BigDecimal.valueOf(13.0));
            } else {
                cqCollectionBody.setE_taxrate(BigDecimal.valueOf(6.0));
            }
            cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(-marginAmount)));
            cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice());
            cqCollectionBody.setE_tax(BigDecimal.ZERO);
            cqCollectionBody.setE_recamount(cqCollectionBody.getE_taxunitprice());
            List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> entry = cq2PlanCollectionVO.getEntry();
            if (ObjectUtil.isNotEmpty(entry) && entry.size() > 0) {
                CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cq2PlanCollectionVOBody = entry.get(0);
                cqCollectionBody.setE_remark(cq2PlanCollectionVOBody.getE_remark());
                cqCollectionBody.setCico_e_businesstype_number(cq2PlanCollectionVOBody.getCico_e_businesstype_number());
                cqCollectionBody.setProject_name(cq2PlanCollectionVOBody.getProject_name());
            }
            cqCollectionBody.setCico_incomeitems_number(CQRevenueItemENUM.SX049.name());
            entry.add(cqCollectionBody);
        }
    }

    //构建名义价款 保证金额抵扣应收单对象
    public CQ2PlanCollectionVO buildNomnalPricePlanCollectionReq(CQ2PlanCollectionVO cq2PlanCollectionVO, CollectionBaseInfo baseInfo, List<CollectionRecordInfo> collectionRecordInfos, SyncCqReqBizInfo bizInfo){
        // 非名义价款不生成
        if (ObjectUtil.isEmpty(collectionRecordInfos) || ObjectUtil.isEmpty(cq2PlanCollectionVO)
                || ObjectUtil.isEmpty(bizInfo) || !CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())) {
            return null;
        }
        CQ2PlanCollectionVO rsp = BeanUtil.copyProperties(cq2PlanCollectionVO, CQ2PlanCollectionVO.class);
        List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> rspEntry = new ArrayList<>();
        //补充保证金抵扣的 汇总金额
        Long marginAmount = 0L;
        for (CollectionRecordInfo e : collectionRecordInfos) {
            if (ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)) {
                    marginAmount += LongUtil.null2zero(e.getCollectionAmount());
            }
        }
        // 如果没有保证金抵扣 这个时候不生成
        if (marginAmount <= 0) {
            return null;
        }
        CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cqCollectionBody = cq2PlanCollectionVO.new CQ2PlanCollectionVOBody();
        cqCollectionBody.setE_quantity(BigDecimal.valueOf(1));
        if (ProjectBizType.ZL.name().equals(bizInfo.getBizType()) && LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())) {
            cqCollectionBody.setE_taxrate(BigDecimal.valueOf(13.0));
        } else {
            cqCollectionBody.setE_taxrate(BigDecimal.valueOf(6.0));
        }
        cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(-marginAmount)));
        cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice());
        cqCollectionBody.setE_tax(BigDecimal.ZERO);
        cqCollectionBody.setE_recamount(cqCollectionBody.getE_taxunitprice());
        List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> entry = cq2PlanCollectionVO.getEntry();
        if (ObjectUtil.isNotEmpty(entry) && entry.size() > 0) {
            CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cq2PlanCollectionVOBody = entry.get(0);
            cqCollectionBody.setE_remark(cq2PlanCollectionVOBody.getE_remark());
            cqCollectionBody.setCico_e_businesstype_number(cq2PlanCollectionVOBody.getCico_e_businesstype_number());
            cqCollectionBody.setProject_name(cq2PlanCollectionVOBody.getProject_name());
        }
        cqCollectionBody.setCico_incomeitems_number(CQRevenueItemENUM.SX049.name());
        rspEntry.add(cqCollectionBody);
        rsp.setEntry(rspEntry);
        rsp.setCico_srcbillno(String.join("-", baseInfo.getCode(), UUIDUtil.genUuid()));
        return rsp;
    }


    //构建罚息-应收单信息
    public CQ2PlanCollectionVO buildPenaltyInterestPlanCollectionReq(CQ2PlanCollectionVO planCollectionVO, CollectionBaseInfo baseInfo, List<CollectionRecordInfo> recordInfos, SyncCqReqBizInfo bizInfo){

        Long penaltyInterest = 0L;
        long deductionAmount = 0L;
        ProjectBizType bizType = ProjectBizType.of(bizInfo.getBizType());
        LeaseType leaseType = LeaseType.of(bizInfo.getLeaseType());
        CQ2PlanCollectionVO rsp = BeanUtil.copyProperties(planCollectionVO, CQ2PlanCollectionVO.class);
        List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> rspEntry = new ArrayList<>();
        for (CollectionRecordInfo collectionRecordInfo : recordInfos) {
            penaltyInterest += LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest());
            if (ObjectUtil.equal(collectionRecordInfo.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)) {
                deductionAmount += LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest());
            }
        }
        if (penaltyInterest <= 0) {
            return null;
        }
        CQ2PlanCollectionVO.CQ2PlanCollectionVOBody penaltyInterestBody = planCollectionVO.new CQ2PlanCollectionVOBody();
        penaltyInterestBody.setE_quantity(BigDecimal.valueOf(1));
        //税率
        if (ProjectBizType.ZL.name().equals(bizInfo.getBizType()) && LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())) {
            penaltyInterestBody.setE_taxrate(BigDecimal.valueOf(13.0));
        } else {
            penaltyInterestBody.setE_taxrate(BigDecimal.valueOf(6.0));
        }
        //利息
        penaltyInterestBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(penaltyInterest)));
        //实收金额/（1+税率）
        penaltyInterestBody.setE_unitprice(penaltyInterestBody.getE_taxunitprice().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(penaltyInterestBody.getE_taxrate()), 10, RoundingMode.HALF_UP));
        //不含税单价* 税率
        penaltyInterestBody.setE_tax(penaltyInterestBody.getE_unitprice().multiply(penaltyInterestBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        penaltyInterestBody.setE_recamount(penaltyInterestBody.getE_taxunitprice());
        penaltyInterestBody.setCico_incomeitems_number(CQRevenueItemENUM.SX046.name());
        penaltyInterestBody.setCico_e_businesstype_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(bizType, leaseType)).map(CQBusinessTypeENUM::getCode).orElse(null));
        penaltyInterestBody.setProject_name(baseInfo.getContractCode());
        rspEntry.add(penaltyInterestBody);
         //补充抵扣信息
        if (deductionAmount > 0) {
            CQ2PlanCollectionVO.CQ2PlanCollectionVOBody deductionBody = BeanUtil.copyProperties(penaltyInterestBody, CQ2PlanCollectionVO.CQ2PlanCollectionVOBody.class);
            deductionBody.setE_recamount(LongUtil.tenThousand2Dollar(String.valueOf(-deductionAmount)));
            deductionBody.setE_taxunitprice(deductionBody.getE_recamount());
            deductionBody.setE_unitprice(LongUtil.tenThousand2Dollar(String.valueOf(deductionAmount)).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(penaltyInterestBody.getE_taxrate()), 10, RoundingMode.HALF_UP).negate());
            deductionBody.setE_tax(deductionBody.getE_unitprice().multiply(deductionBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            rspEntry.add(deductionBody);
        }
        rsp.setEntry(rspEntry);
        rsp.setCico_srcbillno(String.join("-", baseInfo.getCode(), UUIDUtil.genUuid()));
        return rsp;
    }


    /**
     * 构建应收单中的body对象
     */
    private void buildPlanCollectionVOBody(CQ2PlanCollectionVO cq2PlanCollectionVO, SyncCqReqBizInfo bizInfo, CollectionBaseInfo baseInfo, boolean isPrincipal, List<CollectionRecordInfo> recordInfos){
        Long collectionAmount = 0L;
        Long principal = 0L;
        Long interest = 0L;
        //Long penaltyInterest = 0L;
        for (CollectionRecordInfo collectionRecordInfo : recordInfos) {
            collectionAmount += LongUtil.null2zero(collectionRecordInfo.getCollectionAmount());
            principal += LongUtil.null2zero(collectionRecordInfo.getPrincipal());
            interest += LongUtil.null2zero(collectionRecordInfo.getInterest());
           // penaltyInterest += LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest());
        }
        CashFlowItemEnum cashFlowItemEnum = CashFlowItemEnum.of(baseInfo.getCashFlowItem());
        ProjectBizType bizType = ProjectBizType.of(bizInfo.getBizType());
        LeaseType leaseType = LeaseType.of(bizInfo.getLeaseType());
        CQRevenueItemENUM cqRevenueByBusiness = CQRevenueItemENUM.getCQRevenueByBusiness(cashFlowItemEnum, bizType, leaseType, isPrincipal);
        if (cqRevenueByBusiness == null) {
            return;
        }
        CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cqCollectionBody = cq2PlanCollectionVO.new CQ2PlanCollectionVOBody();
        cqCollectionBody.setE_quantity(BigDecimal.valueOf(1));
        //税率
        if (ProjectBizType.ZL.name().equals(bizInfo.getBizType()) && LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())) {
            cqCollectionBody.setE_taxrate(BigDecimal.valueOf(13.0));
        } else {
            cqCollectionBody.setE_taxrate(BigDecimal.valueOf(6.0));
        }
        //计算
        /**
         * 1）现金流项目=本金/客户保证金/厂商质保金，则 不含税单价= 实收金额；
         * 2）现金流项目=else，则 不含税单价= 实收金额/（1+税率）
         **/
        //保证金、质保金
        if(StrUtil.equalsAny(baseInfo.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name(), CashFlowItemEnum.RETENTION_MONEY.name())){
            cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(collectionAmount)));
            cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice());
            cqCollectionBody.setE_tax(BigDecimal.ZERO);
        } else if(CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem()) && isPrincipal){
            //本金
            cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(principal)));
            //除直租外，本金的含税价=不含税价，直租的本金不含税价=含税价/（1+13%）
            if(LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())){
                //实收金额/（1+税率）
                cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(cqCollectionBody.getE_taxrate()), 10, RoundingMode.HALF_UP));
                cqCollectionBody.setE_tax(cqCollectionBody.getE_unitprice().multiply(cqCollectionBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            } else {
                cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice());
                cqCollectionBody.setE_tax(BigDecimal.ZERO);
            }
        }else if(CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem()) && !isPrincipal){
            //利息
            cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(interest)));
            //实收金额/（1+税率）
            cqCollectionBody.setE_unitprice(cqCollectionBody.getE_taxunitprice().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(cqCollectionBody.getE_taxrate()), 10, RoundingMode.HALF_UP));
            //不含税单价* 税率
            cqCollectionBody.setE_tax(cqCollectionBody.getE_unitprice().multiply(cqCollectionBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        } else{
            //其他现金流项目
            cqCollectionBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(collectionAmount)));
            cqCollectionBody.setE_unitprice(LongUtil.tenThousand2Dollar(String.valueOf(collectionAmount)).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(cqCollectionBody.getE_taxrate()), 10, RoundingMode.HALF_UP));
            cqCollectionBody.setE_tax(cqCollectionBody.getE_unitprice().multiply(cqCollectionBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10,RoundingMode.HALF_UP));
        }
        cqCollectionBody.setE_recamount(cqCollectionBody.getE_taxunitprice());
        //设置
        cqCollectionBody.setCico_incomeitems_number(cqRevenueByBusiness.name());
        cqCollectionBody.setCico_e_businesstype_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(bizType, leaseType)).map(CQBusinessTypeENUM::getCode).orElse(null));
        cqCollectionBody.setE_remark(Optional.ofNullable(IncomeConfirmTypeEnum.find(bizInfo.getInterestWay())).map(IncomeConfirmTypeEnum::getDisplay).orElse(null));
        cqCollectionBody.setProject_name(baseInfo.getContractCode());
        if(CollectionUtil.isEmpty(cq2PlanCollectionVO.getEntry())){
            cq2PlanCollectionVO.setEntry(new ArrayList<>());
        }
        cq2PlanCollectionVO.setCico_comment(Optional.ofNullable(IncomeConfirmTypeEnum.find(bizInfo.getInterestWay())).map(IncomeConfirmTypeEnum::getDisplay).orElse(null));
        if(ObjectUtil.isNotEmpty(cq2PlanCollectionVO) && (BigDecimal.ZERO.compareTo(cqCollectionBody.getE_recamount()) != 0 || BigDecimal.ZERO.compareTo(cqCollectionBody.getE_taxunitprice()) != 0)){
            cq2PlanCollectionVO.getEntry().add(cqCollectionBody);
        }
    }

    private CQ2CollectionVO buildCollection(CollectionBaseInfo baseInfo, List<CollectionRecordInfo> collectionRecordInfos, SyncCqReqBizInfo bizInfo, String flowId){
        CQ2CollectionVO vo = new CQ2CollectionVO();
        vo.setBizdate(collectionRecordInfos.get(collectionRecordInfos.size() - 1).getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        if (CashFlowItemEnum.RETENTION_MONEY.name().equals(baseInfo.getCashFlowItem())) {
            vo.setPayertype(FinancialConstants.BD_SUPPLIER);
        } else {
            vo.setPayertype(FinancialConstants.BD_CUSTOMER);
        }
        //vo.setItempayertype(vo.getPayertype());//同 payertype
        vo.setCico_srcbillno(String.join("-", baseInfo.getCode(), UUIDUtil.genUuid()));
        vo.setPayername(bizInfo.getCustomerName());
        vo.setPayernumber(bizInfo.getCustomer());
        vo.setCico_srcsystem(FinancialConstants.RZY);
        vo.setTxt_description(String.format("收到%s客户%s", bizInfo.getCustomerName(), Optional.ofNullable(CashFlowItemEnum.of(baseInfo.getCashFlowItem())).map(CashFlowItemEnum::display).orElse(baseInfo.getCashFlowItem())));//收到xxx客户现金流
        vo.setCico_relateddepartments_number(bizInfo.getOrgCode());//关联部门

        List<CQ2CollectionVO.CQ2CollectionVOBody> bodys = new ArrayList<>();
        collectionRecordInfos.forEach(recordInfo -> {
            //抵扣的无收款申请
            if (!PaymentMethod.REFUND_MARGIN_DEDUCT.display().equals(recordInfo.getCollectionType())) {
                CQ2CollectionVO.CQ2CollectionVOBody body = vo.new CQ2CollectionVOBody();
                if (CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem())) {
                    //本金
                    vo.setReceivingtype_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), true)).map(CQCollectionTypeENUM::getCode).orElse(null));
                    body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getPrincipal()))));
                    vo.setCico_srcbillno(String.join(baseInfo.getCode() + "bj", UUIDUtil.genUuid()));
                } else {
                    //其他
                    vo.setReceivingtype_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), false)).map(CQCollectionTypeENUM::getCode).orElse(null));
                    body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getCollectionAmount()))));
                }
                body.setE_actamt(body.getE_receivableamt());
                body.setCico_customerfield_number(bizInfo.getCustomer());
                body.setE_fundflowitem_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), true)).map(CQCollectionTypeENUM::getChannelCode).orElse(null));
                body.setCico_purposeoffunds_number(body.getE_fundflowitem_number());
                vo.setSettletype_number(Optional.ofNullable(CQPaymentMethodENUM.ofDisplay(recordInfo.getCollectionType())).map(CQPaymentMethodENUM::name).orElse(recordInfo.getCollectionType()));//结算方式
                if (ObjectUtil.isNotEmpty(body.getE_receivableamt()) && BigDecimal.ZERO.compareTo(body.getE_receivableamt()) != 0) {
                    bodys.add(body);
                }
                //添加利息
                if (CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem())) {
                    CQ2CollectionVO.CQ2CollectionVOBody body1 = BeanUtil.copyProperties(body, CQ2CollectionVO.CQ2CollectionVOBody.class);
                    body1.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getInterest()))));
                    body1.setE_actamt(body1.getE_receivableamt());
                    CQCollectionTypeENUM cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), false);
                    if (cqTypeByBusiness != null) {
                        body1.setE_fundflowitem_number(cqTypeByBusiness.getChannelCode());
                        body1.setCico_purposeoffunds_number(cqTypeByBusiness.getChannelCode());
                    }
                    if (ObjectUtil.isNotEmpty(body1.getE_receivableamt()) && BigDecimal.ZERO.compareTo(body1.getE_receivableamt()) != 0) {
                        bodys.add(body1);
                    }
                    //添加罚息
                    CQ2CollectionVO.CQ2CollectionVOBody body2 = BeanUtil.copyProperties(body, CQ2CollectionVO.CQ2CollectionVOBody.class);
                    body2.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getPenaltyInterest()))));
                    body2.setE_actamt(body2.getE_receivableamt());
                    if (cqTypeByBusiness != null) {
                        body2.setE_fundflowitem_number(cqTypeByBusiness.getChannelCode());
                        body2.setCico_purposeoffunds_number(cqTypeByBusiness.getChannelCode());
                    }
                    if (ObjectUtil.isNotEmpty(body2.getE_receivableamt()) && BigDecimal.ZERO.compareTo(body2.getE_receivableamt()) != 0) {
                        bodys.add(body2);
                    }
                }
                if (ObjectUtil.isNotEmpty(recordInfo.getOurAccountNumber())) {
                    vo.setAccountbank_number(recordInfo.getOurAccountNumber());
                }
            }
        });
        vo.setEntry(bodys);
        vo.setActrecamt(bodys.stream().map(CQ2CollectionVO.CQ2CollectionVOBody::getE_receivableamt).reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setLocalamt(vo.getActrecamt());
        vo.setSourcebillnumber(flowId);
        //这里是项目端收款，存放流水信息，可管理改收款下所有信息
        vo.setSource(ExceptionSourceENUM.BANK_FLOW.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getCode());
        //添加保证金抵扣相关数据
        suppleOtherRecord(baseInfo, collectionRecordInfos, vo, bizInfo);
        return vo;
    }

    //判断是否包含第一笔银行流水收款
    private void suppleOtherRecord(CollectionBaseInfo baseInfo, List<CollectionRecordInfo> collectionRecordInfos, CQ2CollectionVO vo, SyncCqReqBizInfo bizInfo) {
        //查询所有
        List<CollectionRecordInfo> allCollectionRecordInfo = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, baseInfo.getId())
                .orderByAsc(CollectionRecordInfo::getId));
        if (ObjectUtil.isEmpty(allCollectionRecordInfo) || ObjectUtil.isEmpty(vo) || ObjectUtil.isEmpty(vo.getEntry())) {
            return;
        }
        List<CollectionRecordInfo> flowCollects = allCollectionRecordInfo.stream().filter(e -> !ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)).collect(Collectors.toList());
        List<CollectionRecordInfo> marginCollects = allCollectionRecordInfo.stream().filter(e -> ObjectUtil.equal(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.display)).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(marginCollects) && ObjectUtil.isNotEmpty(flowCollects) && collectionRecordInfos.stream().map(CollectionRecordInfo::getId).collect(Collectors.toSet()).contains(flowCollects.get(0).getId())) {
            marginCollects.forEach(marginCollect -> {
                if (CashFlowItemEnum.RENT.name().equals(baseInfo.getCashFlowItem())) {
                    suppleOtherBody(vo, baseInfo, marginCollect, bizInfo, 1);
                    suppleOtherBody(vo, baseInfo, marginCollect, bizInfo, 2);
                    suppleOtherBody(vo, baseInfo, marginCollect, bizInfo, 3);
                } else {
                    suppleOtherBody(vo, baseInfo, marginCollect, bizInfo, 4);
                }
            });
        }
    }

    /**
     * @param type 1本金， 2利息，3罚息，4其他
     **/
    private void suppleOtherBody(CQ2CollectionVO vo, CollectionBaseInfo baseInfo, CollectionRecordInfo marginCollect, SyncCqReqBizInfo bizInfo, int type) {
        CQ2CollectionVO.CQ2CollectionVOBody body = vo.new CQ2CollectionVOBody();
        CQCollectionTypeENUM cqTypeByBusiness = null;
        if (type == 1) {
            //本金
            body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(marginCollect.getPrincipal()))));
            vo.setCico_srcbillno(String.join(baseInfo.getCode() + "bj", UUIDUtil.genUuid()));
            cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), true);
        } else if (type == 2) {
            //利息
            cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), false);
            body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(marginCollect.getInterest()))));
            vo.setCico_srcbillno(String.join(baseInfo.getCode() + "lx", UUIDUtil.genUuid()));
        } else if (type == 3) {
            //罚息
            cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), false);
            body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(marginCollect.getPenaltyInterest()))));
            vo.setCico_srcbillno(String.join(baseInfo.getCode() + "fx", UUIDUtil.genUuid()));
        } else {
            //其他
            cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByBusiness(CashFlowItemEnum.of(baseInfo.getCashFlowItem()), ProjectBizType.of(bizInfo.getBizType()), false);
            body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(marginCollect.getCollectionAmount()))));
        }
        body.setE_actamt(body.getE_receivableamt());
        body.setCico_customerfield_number(bizInfo.getCustomer());
        vo.setSettletype_number(CQPaymentMethodENUM.JSFS04.name());//结算方式
        if (cqTypeByBusiness != null) {
            vo.setReceivingtype_number(cqTypeByBusiness.getCode());
            body.setE_fundflowitem_number(cqTypeByBusiness.getChannelCode());
            body.setCico_purposeoffunds_number(cqTypeByBusiness.getChannelCode());
        }
        if (ObjectUtil.isNotEmpty(body.getE_receivableamt()) && BigDecimal.ZERO.compareTo(body.getE_receivableamt()) != 0) {
            CQ2CollectionVO.CQ2CollectionVOBody oppositeBody = BeanUtil.copyProperties(body, CQ2CollectionVO.CQ2CollectionVOBody.class);
            oppositeBody.setE_actamt(body.getE_actamt().negate());
            oppositeBody.setE_receivableamt(body.getE_receivableamt().negate());
            vo.getEntry().add(body);
            vo.getEntry().add(oppositeBody);
        }
    }


    /**
     * 计算生效的收款申请已支付的金额
     * 主要用以统一单份合同
     *
     * @param collectionIds
     * @return
     */
    public Long calculateCollectionAmount(List<Long> collectionIds) {
        if (ObjectUtil.isEmpty(collectionIds)) {
            return 0L;
        }
        List<CollectionRecordInfo> writeOff = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionIds)
                .eq(CollectionRecordInfo::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        long amount = 0L;
        for (CollectionRecordInfo collectionRecordInfo : writeOff) {
            amount += LongUtil.null2zero(collectionRecordInfo.getCollectionAmount());
            amount += LongUtil.null2zero(collectionRecordInfo.getPenaltyInterest());
        }
        return amount;
    }

    //除去反核销后最晚核销日期
    public LocalDate getLastWriteDate(Long collectionId) {
        List<CollectionRecordInfo> writeOff = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, collectionId)
                .eq(CollectionRecordInfo::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        if (CollectionUtil.isNotEmpty(writeOff)) {
            //<时间-金额, 次数>
            Map<LocalDate, Map<Long, Integer>> map = new HashMap<>();
            Map<Long, Integer> twoMap;
            for (CollectionRecordInfo base : writeOff) {
                if (LongUtil.null2zero(base.getCollectionAmount()) >= 0) {
                    twoMap = map.getOrDefault(base.getCollectionDate(), new HashMap<>());
                    twoMap.put(base.getCollectionAmount(), twoMap.getOrDefault(base.getCollectionAmount(), 0) + 1);
                    map.put(base.getCollectionDate(), twoMap);
                } else {
                    twoMap = map.getOrDefault(base.getCollectionDate(), new HashMap<>());
                    twoMap.put(Math.abs(base.getCollectionAmount()), twoMap.getOrDefault(Math.abs(base.getCollectionAmount()), 0) - 1);
                    map.put(base.getCollectionDate(), twoMap);
                }
            }
            LocalDate localDate = null;
            for (Map.Entry<LocalDate, Map<Long, Integer>> entry : map.entrySet()) {
                LocalDate key = entry.getKey();
                Map<Long, Integer> value = entry.getValue();
                for (Integer v : value.values()) {
                    if (v > 0) {
                        if (ObjectUtil.isNull(localDate) || key.isAfter(localDate)) {
                            localDate = key;
                        }
                    }
                }
            }
            return localDate;
        }
        return null;
    }

    public CollectionRecordListRSP list(CollectionRecordListREQ req) {
        List<CollectionRecordInfo> infoList = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, req.getId()).orderByAsc(CollectionRecordInfo::getSortId));
        CollectionRecordListRSP result = new CollectionRecordListRSP();
        List<CollectionRecordListRSP.Records> rsps = new LinkedList<>();
//        long collectionAmount = 0,principal = 0,interest = 0, penaltyInterest = 0;
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(req.getId());
        //现金流编号转合同编号
        Map<Long, String> marginBaseId2ContractCode = new HashMap<>();
        Set<Long> marginBaseIdSet = infoList.stream().map(CollectionRecordInfo::getDeductionMarginBaseId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(marginBaseIdSet)) {
            List<MarginBaseInfo> marginBaseInfos = marginBaseInfoMapper.selectBatchIds(marginBaseIdSet);
            if (ObjectUtil.isNotEmpty(marginBaseInfos)) {
                marginBaseId2ContractCode.putAll(marginBaseInfos.stream().collect(Collectors.toMap(MarginBaseInfo::getId, MarginBaseInfo::getContractCode, (a, b) -> a)));
            }
        }
        for (CollectionRecordInfo o : infoList) {
            CollectionRecordListRSP.Records rsp = new CollectionRecordListRSP.Records();
            BeanUtil.copyProperties(o, rsp);
            rsp.setCollectionWay(Objects.equals(rsp.getCollectionType(), GlobalConstants.CQ_PAYMENT_METHOD_PJ) ? 1 : 0);
            if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())) {
                rsp.setPrincipal(null);
                rsp.setInterest(null);
                rsp.setPenaltyInterest(null);
            }
            /*if (o.getCollectionType().equals(RecordTypeEnum.REFUND_MARGIN_DEDUCT.name())) {
                rsp.setMarginId(o.getDataSource());
                //一般只有一条抵扣记录
                MarginBaseInfo info = marginBaseInfoMapper.selectById(Long.parseLong(rsp.getMarginId()));
                rsp.setDataSource(info.getMarginCode());
            }*/
//            rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
            if (StrUtil.isNotEmpty(o.getInvoiceFlag())) {
                rsp.setInvoice("1".equals(o.getInvoiceFlag()) ? "是" : "否");
            }

            rsp.setContractCode(marginBaseId2ContractCode.get(o.getDeductionMarginBaseId()));
            rsps.add(rsp);
        }
        CollectionRecordListRSP.Sum sum = new CollectionRecordListRSP.Sum();
        sum.setCollectionAmount(baseInfo.getCollectionAmount());
        sum.setInterest(baseInfo.getCollectionInterest());
        sum.setPrincipal(baseInfo.getCollectionPrincipal());
        sum.setPenaltyInterest(baseInfo.getCollectionPenaltyInterest());
        if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(baseInfo.getCashFlowItem()) || CashFlowItemEnum.NOMINAL_PRICE.name().equals(baseInfo.getCashFlowItem())) {
            sum.setInterest(null);
            sum.setPrincipal(null);
            sum.setPenaltyInterest(null);

        }
        result.setRecords(rsps);
        result.setSum(sum);
        return result;
    }


    public CollectionRecordDetailRSP detail(CollectionRecordDetailREQ req) {
        CollectionRecordInfo recordInfo = collectionRecordInfoMapper.selectById(req.getId());
        CollectionRecordDetailRSP rsp = new CollectionRecordDetailRSP();
        rsp.setOurBankInfo(getBankInfo(recordInfo));
        BeanUtil.copyProperties(recordInfo, rsp);
//        rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
        if (StrUtil.isNotEmpty(recordInfo.getInvoiceFlag())) {
            rsp.setInvoice("1".equals(recordInfo.getInvoiceFlag()) ? "是" : "否");
        }
//        if (recordInfo.getSourceFlag() == 1) {
//            Map<Long, String> id2Name = id2NameService.sysUserId2Name(Collections.singleton(Long.parseLong(recordInfo.getDataSource())));
//            rsp.setDataSource(id2Name.get(Long.parseLong(recordInfo.getDataSource())));
//        }
        return rsp;
    }

    public void writeOffRecord(CollectionRecordInfo entity, CollectionBaseInfo baseInfo) {
        baseInfo.setCollectionAmount(LongUtil.null2zero(baseInfo.getCollectionAmount()) + LongUtil.null2zero(entity.getCollectionAmount()));
        if (baseInfo.getCollectionDate() == null || baseInfo.getCollectionDate().isBefore(entity.getCollectionDate())) {
            baseInfo.setCollectionDate(entity.getCollectionDate());
        }
        baseInfo.setCollectionPrincipal(LongUtil.null2zero(baseInfo.getCollectionPrincipal()) + LongUtil.null2zero(entity.getPrincipal()));
        baseInfo.setCollectionInterest(LongUtil.null2zero(baseInfo.getCollectionInterest()) + LongUtil.null2zero(entity.getInterest()));
        baseInfo.setCollectionPenaltyInterest(LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()) + LongUtil.null2zero(entity.getPenaltyInterest()));
        if (baseInfo.getWriteOffStatus().equals(CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name()) || baseInfo.getWriteOffStatus().equals(CollectionWriteOffStatusEnum.UNCOLLECTION.name())) {
            baseInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name());
        }
    }

    private CollectionRecordDetailRSP.BankInfo getBankInfo(CollectionRecordInfo recordInfo) {
        CollectionRecordDetailRSP.BankInfo bankInfo = new CollectionRecordDetailRSP.BankInfo();
        bankInfo.setOurAccountId(recordInfo.getOurAccountId());
        bankInfo.setOurAccountName(recordInfo.getOurAccountName());
        bankInfo.setOurAccountNumber(recordInfo.getOurAccountNumber());
        bankInfo.setOurAccountBank(recordInfo.getOurAccountBank());
        return bankInfo;
    }

}
