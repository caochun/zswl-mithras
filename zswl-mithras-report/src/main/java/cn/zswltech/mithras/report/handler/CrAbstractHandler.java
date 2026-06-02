package cn.zswltech.mithras.report.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.mapper.HandleRecordMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.base.model.CrRepayPlanBase;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.mapper.model.HandleRecord;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.service.formal.CrRepayPlanService;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.aspose.slides.Collections.Specialized.CollectionsUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据采集处理器
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:51 AM
 */
public abstract class CrAbstractHandler<DRAFT extends CrBaseModel, FORMAL extends CrBaseModel> {

    @Autowired
    protected IService<DRAFT> draftService;
    @Autowired
    protected BaseMapper<DRAFT> draftMapper;
    @Autowired
    protected IService<FORMAL> formalService;
    @Autowired
    protected BaseMapper<FORMAL> formalMapper;
    @Resource
    protected HandleRecordMapper handleRecordMapper;
    @Resource
    protected ReportDataRepository reportDataRepository;


    /**
     * 显式控制bean的执行顺序
     *
     * @return
     */
    public abstract Integer sort();

    /**
     * 模块枚举
     *
     * @return
     */
    public abstract ReportModuleEnum reportModule();

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void handle(LocalDateTime dealTime) {
        HandleRecord handleRecord = handleRecordMapper.selectOne(Wrappers.<HandleRecord>lambdaQuery()
                .eq(HandleRecord::getModule, reportModule().name())
                .last(StringUtil.mysqlLimitOne()));

        LocalDateTime lastDealTime = Optional.ofNullable(handleRecord).map(HandleRecord::getDealTime).orElse(LocalDateTime.MIN);
        // 各模块自处理
        moduleHandle(dealTime, lastDealTime);
        clearNeedDeleteContract(dealTime, lastDealTime);
    }

    /**
     * 各个模块的后置补偿处理
     *
     * @param dealTime
     */
    public void afterHandle(LocalDateTime dealTime) {
        HandleRecord handleRecord = handleRecordMapper.selectOne(Wrappers.<HandleRecord>lambdaQuery()
                .eq(HandleRecord::getModule, reportModule().name())
                .last(StringUtil.mysqlLimitOne()));

        LocalDateTime lastDealTime = Optional.ofNullable(handleRecord).map(HandleRecord::getDealTime).orElse(LocalDateTime.MIN);
        SpringContextHolder.getBean(this.getClass()).afterModuleHandle(dealTime, lastDealTime);

        if (Objects.isNull(handleRecord)) {
            handleRecord = new HandleRecord();
            handleRecord.setModule(reportModule().name());
            handleRecord.setDealTime(dealTime);
            handleRecordMapper.insert(handleRecord);
        } else {
            handleRecord.setDealTime(dealTime);
            handleRecordMapper.updateById(handleRecord);
        }
    }

    /**
     * 对主承租人不上报征信的合同 的数据 进行删除
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void clearNeedDeleteContract(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<Long> needDeleteContractIdList = reportDataRepository.listDeleteCrDataContractId(dealTime, lastDealTime);
        if (CollectionUtils.isNotEmpty(needDeleteContractIdList)) {
            draftMapper.delete(Wrappers.<DRAFT>lambdaQuery().in(DRAFT::getContractId, needDeleteContractIdList));
            formalMapper.delete(Wrappers.<FORMAL>lambdaQuery().in(FORMAL::getContractId, needDeleteContractIdList));
        }
    }

    /**
     * 模块自定义处理时间 左开右闭
     *
     * @param dealTime     此次处理时间 包含
     * @param lastDealTime 上次最后处理时间 不包含
     */
    protected abstract void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime);

    /**
     * 模块自定义处理时间 左开右闭
     *
     * @param dealTime     此次处理时间 包含
     * @param lastDealTime 上次最后处理时间 不包含
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
    }

    /**
     * 提供一个处理结清的方法
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void handlerSettleRecord(Collection<String> paymentApplyCodeCollection) {
        if (CollectionUtils.isEmpty(paymentApplyCodeCollection)) {
            return;
        }
        List<DRAFT> drafts = draftService.query()
                .in("payment_apply_code", paymentApplyCodeCollection)
                .list();
        if (CollectionUtils.isEmpty(drafts)) {
            return;
        }
        // 判断当前类是不是还款计划， 不是还款计划的只需要将状态改为已上报即可
        boolean instance = drafts.get(0) instanceof CrRepayPlanDraft;
        if (!instance) {
            drafts.forEach(draft -> ReflectUtil.setFieldValue(draft, "reportState", ReportState.REPORTED.name()));
            draftService.updateBatchById(drafts);
        } else {
            CrRepayPlanService repayPlanService = SpringContextHolder.getBean(CrRepayPlanService.class);
            Map<String, List<CrRepayPlan>> collect = repayPlanService.list(Wrappers.<CrRepayPlan>lambdaQuery()
                            .in(CrRepayPlan::getPaymentApplyCode, paymentApplyCodeCollection))
                    .stream().collect(Collectors.groupingBy(CrRepayPlan::getPaymentApplyCode));
            if (CollUtil.isEmpty(collect)) {
                return;
            }
            Map<String, Map<Integer, CrRepayPlan>> effectRepayPlanMap = new HashMap<>(32);
            collect.forEach((paymentApplyCode, repayPlans) -> {
                Map<Integer, CrRepayPlan> phaseRepayPlanMap = repayPlans.stream().collect(Collectors.toMap(CrRepayPlan::getPhase, repayPlan -> repayPlan));
                effectRepayPlanMap.put(paymentApplyCode, phaseRepayPlanMap);
            });
            drafts.forEach(draft -> {
                // 将展示状态改为不展示  并且需要将已经生效的数据覆盖回来
                String paymentApplyCode = (String) ReflectUtil.getFieldValue(draft, "paymentApplyCode");
                Map<Integer, CrRepayPlan> integerCrRepayPlanMap = effectRepayPlanMap.get(paymentApplyCode);
                // 找不到计划
                if(Objects.isNull(integerCrRepayPlanMap)){
                    draftMapper.deleteById((CrRepayPlanDraft) draft);
                    return;
                }
                CrRepayPlan plan = integerCrRepayPlanMap.get((Integer) ReflectUtil.getFieldValue(draft, "phase"));
                if (Objects.isNull(plan)) {
                    // 如果找不到计划，说明是这次新增的，直接删除就好
                    draftMapper.deleteById((CrRepayPlanDraft) draft);
                    return;
                }
                // 租金
                ReflectUtil.setFieldValue(draft, "rent", plan.getRent());
                // 本金也是一样的
                ReflectUtil.setFieldValue(draft, "principal", plan.getPrincipal());
                ReflectUtil.setFieldValue(draft, "isShow", YesOrNoNumberEnum.NO.getCode());
            });
            draftService.updateBatchById(drafts);
        }
    }


    @Autowired
    private ContractBaseInfoService contractBaseInfoService;
    @Autowired
    private ContractTenantryService contractTenantryService;
    @Autowired
    private ContractPledgeService contractPledgeService;
    @Autowired
    private ContractMortgageService contractMortgageService;
    @Autowired
    private ContractGuarantorService contractGuarantorService;
    @Autowired
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    /**
     * 查询当前客户是否还存在在租合同
     * clientId: 可能是承租人，担保人，抵押人，质押人
     */
    protected boolean isExistInContract(Long clientId) {
        // 过关斩将，只要一个满足就返回true
        Set<Long> contractIds = new HashSet<>();
        // 1、承租人
        List<ContractTenantry> tenantryList = contractTenantryService.listByClientIds(Collections.singletonList(clientId));
        if (CollectionUtils.isNotEmpty(tenantryList)) {
            contractIds.addAll(tenantryList.stream().map(ContractTenantry::getContractId).collect(Collectors.toSet()));
        }
        // 2、担保人
        List<ContractGuarantor> contractGuarantors = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .like(ContractGuarantor::getGuarantorIds, clientId));
        if (CollectionUtils.isNotEmpty(contractGuarantors)) {
            contractIds.addAll(contractGuarantors.stream().map(ContractGuarantor::getContractId).collect(Collectors.toSet()));
        }
        // 3、抵押人
        List<ContractMortgage> contractMortgages = contractMortgageService.list(Wrappers.<ContractMortgage>lambdaQuery()
                .like(ContractMortgage::getMortgageIds, clientId));
        if (CollectionUtils.isNotEmpty(contractMortgages)) {
            contractIds.addAll(contractMortgages.stream().map(ContractMortgage::getContractId).collect(Collectors.toSet()));
        }
        // 4、质押人
        List<ContractPledge> contractPledges = contractPledgeService.list(Wrappers.<ContractPledge>lambdaQuery()
                .like(ContractPledge::getPledgeIds, clientId));
        if (CollectionUtils.isNotEmpty(contractPledges)) {
            contractIds.addAll(contractPledges.stream().map(ContractPledge::getContractId).collect(Collectors.toSet()));
        }

        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractIds);
        if (CollUtil.isEmpty(contractBaseInfos)) {
            return false;
        }
        // 查询合同的还款信息，如果还有未还的，则还需要继续报送
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIds)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT)
                .gt(CollectionBaseInfo::getPhase, 0));
        /// 客户下所有借据租金&罚息已核销，则不需要报送，否则需要报送
        return collectionBaseInfos.stream().anyMatch(e -> {
            long l = LongUtil.null2zero(e.getPlanCollectionAmount()) - LongUtil.null2zero(e.getCollectionAmount());
            long p = LongUtil.null2zero(e.getPenaltyInterest()) - LongUtil.null2zero(e.getCollectionPenaltyInterest());
            return l > 0 || p > 0;
        });
    }

}
