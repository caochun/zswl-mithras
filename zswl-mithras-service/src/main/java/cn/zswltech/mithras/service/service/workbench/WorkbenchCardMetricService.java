package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.workbench.CardMetricChooseDto;
import cn.zswltech.mithras.dto.workbench.WorkBenchRoleListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.service.convert.workbench.WorkbenchCardMetricConverter;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricRole;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricTimeScope;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricUnit;
import cn.zswltech.mithras.workbench.application.WorkbenchCardUserRefService;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchCardMetric;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchCardUserRef;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchCardMetricMapper;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.service.service.workbench.cardcal.CardCalculator;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchCardMetricService
        extends ServiceImpl<WorkbenchCardMetricMapper, WorkbenchCardMetric> {

    public static final String CLIENT_COUNT_CARD_NAME = "本年新增投放客户数";
    public static final String PROJ_ESTABLISH_COUNT_CARD_NAME = "本年新增立项数";
    public static final String PROJ_REVIEW_COUNT_CARD_NAME = "本年新增项目评审数";
    public static final String PROJ_REVIEW_PASS_RATE_CARD_NAME = "本年项目评审通过率";
    public static final String TOTAL_LAUNCH_AMOUNT_CARD_NAME = "本年累计投放金额";
    public static final String REMAINING_PRINCIPAL_CARD_NAME = "剩余本金";
    public static final String INVENTORY_PROJECT_COUNT_CARD_NAME = "存量项目个数";
    public static final String OVERDUE_PROJECT_COUNT_CARD_NAME = "逾期项目数";
    public static final String DEFECTIVE_PROJECT_COUNT_CARD_NAME = "不良项目数";
    public static final String MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME = "本月新增立项数";
    public static final String MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME = "本月累计投放金额";

    @Resource
    private WorkbenchCardMetricConverter cardMetricConverter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private List<CardCalculator> cardCalculators;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private WorkbenchCardUserRefService workbenchCardUserRefService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService clientLibService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;

    public List<WorkbenchCardMetricListRsp> list(WorkbenchMetricReq req) {
        String scope;
        if (req.getCurrentRoleCode().equals(WorkbenchMetricRole.COMPREHENSIVE_MANAGEMENT.name())) {
            scope = "ZSZL";
        } else {
            scope = req.getCurrentRoleCode().split("_")[0];
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> metrics = workbenchCardUserRefService.list(Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                .eq(WorkbenchCardUserRef::getUserId, userId)).stream().map(WorkbenchCardUserRef::getMetricName).collect(Collectors.toSet());
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
        List<WorkbenchCardMetric> workbenchCardMetrics = list(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                .in(WorkbenchCardMetric::getMetricName, metrics)
                .eq(WorkbenchCardMetric::getScope, scope));
        List<WorkbenchCardMetricListRsp> rsps = new ArrayList<>();
        for (WorkbenchCardMetric metric : workbenchCardMetrics) {
            WorkbenchCardMetricListRsp rsp = cardMetricConverter.entity2ListRsp(metric);
            WorkbenchMetricUnit unit = WorkbenchMetricUnit.valueOf(rsp.getUnit());
            rsp.setUnitDisplay(unit.display());
            rsps.add(rsp);
        }
        return rsps;
    }

    public List<WorkbenchCardMetricListRsp> listBizPersonMetrics() {
        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> metrics = workbenchCardUserRefService.list(Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                .eq(WorkbenchCardUserRef::getUserId, userId)).stream().map(WorkbenchCardUserRef::getMetricName).collect(Collectors.toSet());
        if (metrics.isEmpty()) {
            return Collections.emptyList();
        }
        List<WorkbenchCardMetricListRsp> rsps = new ArrayList<>();
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime startTime = startDay.atStartOfDay();

        // 本年新增投放客户数
        if (metrics.contains(CLIENT_COUNT_CARD_NAME)) {
            Set<Long> actualPaidClientIds = actualDetailService.queryFirstLaunchClient(startTime)
                    .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toSet());
            int clientCount = clientService.count(Wrappers.<Client>lambdaQuery()
                    .eq(Client::getBelongSponsorId, userId)
                    .in(Client::getId, actualPaidClientIds));
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(CLIENT_COUNT_CARD_NAME).setUnitDisplay("户")
                    .setValue(String.valueOf(clientCount)));
        }
        //本年新增立项个数
        if (metrics.contains(PROJ_ESTABLISH_COUNT_CARD_NAME)) {
            List<ProjEstablishBaseInfo> establishBaseInfos = establishBaseInfoService.list(
                    Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                            .eq(ProjEstablishBaseInfo::getProjSponsorUserId, userId)
                            .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                            .ge(BaseModel::getCreateTime, startTime));
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(PROJ_ESTABLISH_COUNT_CARD_NAME).setUnitDisplay("个")
                    .setValue(String.valueOf(establishBaseInfos.size())));
        }

        List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoService.list(
                Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .eq(ProjReviewBaseInfo::getProjSponsorUserId, userId)
                        .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_UN_SUBMIT.name())
                        .ge(BaseModel::getCreateTime, startTime));
        // 项目评审个数
        if (metrics.contains(PROJ_REVIEW_COUNT_CARD_NAME)) {
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(PROJ_REVIEW_COUNT_CARD_NAME)
                    .setUnitDisplay("个")
                    .setValue(String.valueOf(reviewBaseInfos.size())));
        }
        // 项目评审通过率
        if (metrics.contains(PROJ_REVIEW_PASS_RATE_CARD_NAME)) {
            WorkbenchCardMetricListRsp metricRsp = new WorkbenchCardMetricListRsp().setMetricName(PROJ_REVIEW_PASS_RATE_CARD_NAME)
                    .setUnitDisplay("%");
            if (reviewBaseInfos.isEmpty()) {
                rsps.add(metricRsp.setValue("0"));
            } else {
                List<ProjReviewBaseInfo> effectInfos = reviewBaseInfos.stream()
                        .filter(info -> RecordStatus.TAKE_EFFECT.name().equals(info.getProjReviewStatus()))
                        .collect(Collectors.toList());
                String passRate = new BigDecimal(effectInfos.size() * 100)
                        .divide(new BigDecimal(reviewBaseInfos.size()), 2, RoundingMode.HALF_UP).toString();
                metricRsp.setValue(passRate);
                rsps.add(metricRsp);
            }
        }

        // 累计投放金额
        if (metrics.contains(TOTAL_LAUNCH_AMOUNT_CARD_NAME)) {
            //从客户维度修改为合同维度
            Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                    .eq(ContractBaseInfo::getProjSponsorUserId, userId)
            ).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            String lanchAmount = actualDetailService.writtenOffDetailsByContractIds(targetContractIds,
                            startDay, null).stream()
                    .map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString();
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(TOTAL_LAUNCH_AMOUNT_CARD_NAME)
                    .setUnitDisplay("万元").setValue(lanchAmount));
        }
        // 剩余本金
        if (metrics.contains(REMAINING_PRINCIPAL_CARD_NAME)) {
            WorkbenchCardMetricListRsp remaining = new WorkbenchCardMetricListRsp()
                    .setMetricName(REMAINING_PRINCIPAL_CARD_NAME)
                    .setUnitDisplay("万元");
            Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getBelongSponsorId, userId)
                            .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name())).stream()
                    .map(Client::getId).collect(Collectors.toSet());
            if (targetClientIds.isEmpty()) {
                remaining.setValue("0.00");
            }
            RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
            dto.setClientIds(targetClientIds);
            BigDecimal total = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto).values().stream()
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            remaining.setValue(total
                    .divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString());
            rsps.add(remaining);
        }
        // 存量项目个数
        if (metrics.contains(INVENTORY_PROJECT_COUNT_CARD_NAME)) {
            List<ContractBaseInfo> contracts = contractBaseInfoService.list(
                    Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                            .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT"));
            Set<Long> projReviewIds = contracts.stream()
                    .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(INVENTORY_PROJECT_COUNT_CARD_NAME)
                    .setUnitDisplay("个")
                    .setValue(String.valueOf(projReviewIds.size())));
        }
        // 逾期项目数
        if (metrics.contains(OVERDUE_PROJECT_COUNT_CARD_NAME)) {
            WorkbenchCardMetricListRsp overdueProjectCount = new WorkbenchCardMetricListRsp()
                    .setMetricName(OVERDUE_PROJECT_COUNT_CARD_NAME)
                    .setUnitDisplay("个");
            List<CollectionBaseInfo> overdueRecord = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                    .isNull(CollectionBaseInfo::getCollectionDate)
                    .eq(CollectionBaseInfo::getCashFlowItem, "RENT"));
            if (overdueRecord.isEmpty()) {
                overdueProjectCount.setValue("0");
            }
            Set<Long> contractId = overdueRecord.stream()
                    .map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
            Set<Long> projectReviewIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                            .in(ContractBaseInfo::getId, contractId)).stream().map(ContractBaseInfo::getProjReviewId)
                    .collect(Collectors.toSet());
            overdueProjectCount.setValue(String.valueOf(projectReviewIds.size()));
            rsps.add(overdueProjectCount);
        }
        // 不良项目数
        if (metrics.contains(DEFECTIVE_PROJECT_COUNT_CARD_NAME)) {
            WorkbenchCardMetricListRsp defectiveProjectCount = new WorkbenchCardMetricListRsp()
                    .setMetricName(DEFECTIVE_PROJECT_COUNT_CARD_NAME)
                    .setUnitDisplay("个");
            Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
            if (!assetClassify.isPresent()) {
                defectiveProjectCount.setValue("0");
            } else {
                Set<Long> lastThreeClientIds = clientLibService
                        .newestClassifyClientLib(assetClassify.get().getId())
                        .stream().filter(lib -> AssetClassifyResultEnum.lastThree().contains(lib.getClassifyResult()))
                        .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
                Set<Long> projectReviewIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getClientId, lastThreeClientIds)).stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
                defectiveProjectCount.setValue(String.valueOf(projectReviewIds.size()));
                rsps.add(defectiveProjectCount);
            }
        }

        LocalDate monthStartDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        // 本月新增立项个数
        if (metrics.contains(MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME)) {
            List<ProjEstablishBaseInfo> establishBaseInfos = establishBaseInfoService.list(
                    Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                            .eq(ProjEstablishBaseInfo::getProjSponsorUserId, userId)
                            .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                            .ge(BaseModel::getCreateTime, monthStartDay));
            rsps.add(new WorkbenchCardMetricListRsp()
                    .setMetricName(MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME)
                    .setUnitDisplay("个")
                    .setValue(String.valueOf(establishBaseInfos.size())));
        }
        // 本月累计投放金额
        if (metrics.contains(MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME)) {
            Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                    .eq(ContractBaseInfo::getProjSponsorUserId, userId)
            ).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            String launchAmount = actualDetailService.writtenOffDetailsByContractIds(targetContractIds,
                            monthStartDay, null).stream()
                    .map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString();
            rsps.add(new WorkbenchCardMetricListRsp().setMetricName(MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME)
                    .setUnitDisplay("万元").setValue(launchAmount));
        }
        return rsps;
    }

    public WorkBenchRoleListRsp listRole() {
        Set<String> targetRoles = Arrays.stream(WorkbenchMetricRole.values())
                .map(Enum::name).collect(Collectors.toSet());
        List<String> currentUserRoles = sysUserService.getCurrentUserRoles();
        currentUserRoles.retainAll(targetRoles);
        WorkBenchRoleListRsp rsp = new WorkBenchRoleListRsp();
        List<SelectRSP> roleList = new ArrayList<>();
        int code = 99;
        for (String s : currentUserRoles) {
            WorkbenchMetricRole workbenchMetricRole = WorkbenchMetricRole.valueOf(s);
            SelectRSP selectRSP = new SelectRSP();
            selectRSP.setLabel(workbenchMetricRole.display());
            selectRSP.setValue(s);
            roleList.add(selectRSP);
            if (code > workbenchMetricRole.sortCode()) {
                rsp.setDefaultRole(selectRSP);
                code = workbenchMetricRole.sortCode();
            }
        }
        rsp.setRoleList(roleList);
        return rsp;
    }

    public void calculate() {
        List<WorkbenchCardMetric> workbenchCardMetrics = new ArrayList<>();
        // 计算部门维度数据
        workbenchCardMetrics.addAll(newClient());
        workbenchCardMetrics.addAll(newProjEstablish(WorkbenchMetricTimeScope.YEARLY));
        workbenchCardMetrics.addAll(newReview());
        workbenchCardMetrics.addAll(newLaunchAmount(WorkbenchMetricTimeScope.YEARLY));
        workbenchCardMetrics.addAll(remainingPrincipal());
        workbenchCardMetrics.addAll(inventoryProjectCount());
        workbenchCardMetrics.addAll(overdueProjectCount());
        workbenchCardMetrics.addAll(defectiveProjectCount());
        workbenchCardMetrics.addAll(newProjEstablish(WorkbenchMetricTimeScope.MONTHLY));
        workbenchCardMetrics.addAll(newLaunchAmount(WorkbenchMetricTimeScope.MONTHLY));

        // 调用计算逻辑
        Set<String> metricCodes = cardCalculators.stream().map(CardCalculator::metricCode).collect(Collectors.toSet());
        Map<String, WorkbenchCardMetric> metricMap = baseMapper.selectList(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                        .in(WorkbenchCardMetric::getMetricCode, metricCodes)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getMetricCode, v -> v));
        for (CardCalculator cardCalculator : cardCalculators) {
            String oneRes = cardCalculator.calculate();
            metricMap.get(cardCalculator.metricCode()).setValue(oneRes);
        }
        workbenchCardMetrics.addAll(metricMap.values());
        updateBatchById(workbenchCardMetrics);
    }

    /**
     * 计算各部门的不良项目数
     *
     * @return
     */
    private List<WorkbenchCardMetric> defectiveProjectCount() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, DEFECTIVE_PROJECT_COUNT_CARD_NAME)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
        if (!assetClassify.isPresent()) {
            tmpMap.values().forEach(v -> v.setValue("0"));
            return new ArrayList<>(tmpMap.values());
        }
        Set<Long> lastThreeClientIds = clientLibService
                .newestClassifyClientLib(assetClassify.get().getId())
                .stream().filter(lib -> AssetClassifyResultEnum.lastThree().contains(lib.getClassifyResult()))
                .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
        if (lastThreeClientIds.isEmpty()) {
            tmpMap.values().forEach(v -> v.setValue("0"));
            return new ArrayList<>(tmpMap.values());
        }
        List<ProjReviewBaseInfo> lastThreeTakeEffect = reviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")
                .in(ProjReviewBaseInfo::getClientId, lastThreeClientIds));
        if (lastThreeTakeEffect.isEmpty()) {
            tmpMap.values().forEach(v -> v.setValue("0"));
            return new ArrayList<>(tmpMap.values());
        }
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            WorkbenchCardMetric metric = tmpMap.get(deptScope.name());
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                metric.setValue(String.valueOf(lastThreeTakeEffect.size()));
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                metric.setValue(String.valueOf(lastThreeTakeEffect.stream()
                        .filter(review -> review.getBizDeptId().equals(deptId)).count()));
            }
        }
        return new ArrayList<>(tmpMap.values());
    }

    /**
     * 计算各部门的逾期项目数
     *
     * @return
     */
    private List<WorkbenchCardMetric> overdueProjectCount() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, OVERDUE_PROJECT_COUNT_CARD_NAME)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));
        List<CollectionBaseInfo> overdueRecord = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                .isNull(CollectionBaseInfo::getCollectionDate)
                .eq(CollectionBaseInfo::getCashFlowItem, "RENT"));
        if (overdueRecord.isEmpty()) {
            return new ArrayList<>(tmpMap.values());
        }
        Set<Long> contractId = overdueRecord.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractId));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            WorkbenchCardMetric metric = tmpMap.get(deptScope.name());
            Set<Long> projReviewIds;
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                projReviewIds = contracts.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                projReviewIds = contracts.stream().filter(contract -> contract.getBizDeptId().equals(deptId))
                        .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            }
            metric.setValue(String.valueOf(projReviewIds.size()));
        }
        return new ArrayList<>(tmpMap.values());
    }

    /**
     * 计算各部门的存量项目数
     *
     * @return
     */
    private List<WorkbenchCardMetric> inventoryProjectCount() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, INVENTORY_PROJECT_COUNT_CARD_NAME)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT"));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            WorkbenchCardMetric metric = tmpMap.get(deptScope.name());
            Set<Long> projReviewIds;
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                projReviewIds = contracts.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                projReviewIds = contracts.stream().filter(contract -> contract.getBizDeptId().equals(deptId))
                        .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            }
            metric.setValue(String.valueOf(projReviewIds.size()));
        }
        return new ArrayList<>(tmpMap.values());
    }

    /**
     * 计算各部门的新增投放金额
     *
     * @return
     */
    private List<WorkbenchCardMetric> remainingPrincipal() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, REMAINING_PRINCIPAL_CARD_NAME)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                BigDecimal remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(null);
                tmpMap.get(deptScope.name()).setValue(remainingPrincipal
                        .divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString());
            } else {
                Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                                .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                                .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name())).stream()
                        .map(Client::getId).collect(Collectors.toSet());
                if (targetClientIds.isEmpty()) {
                    tmpMap.get(deptScope.name()).setValue("0.00");
                }
                RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
                dto.setClientIds(targetClientIds);
                BigDecimal total = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto).values().stream()
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                tmpMap.get(deptScope.name()).setValue(total
                        .divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString());
            }
        }
        return new ArrayList<>(tmpMap.values());
    }

    private List<WorkbenchCardMetric> newClient() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, CLIENT_COUNT_CARD_NAME)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));
        // 查询今年有实际投放的客户id
        LocalDate firstDayYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        Set<Long> targetClientIds = actualDetailService
                .queryFirstLaunchClient(firstDayYear.atStartOfDay())
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toSet());
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.name().equals(deptScope.name())) {
                tmpMap.get(deptScope.name()).setValue(String.valueOf(targetClientIds.size()));
            } else {
                int newClientCount = clientService.count(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                        .in(Client::getId, targetClientIds));
                tmpMap.get(deptScope.name()).setValue(String.valueOf(newClientCount));
            }
        }
        return new ArrayList<>(tmpMap.values());
    }

    private List<WorkbenchCardMetric> newProjEstablish(WorkbenchMetricTimeScope timeScope) {
        LocalDate startDate;
        String cardName;
        if (timeScope.equals(WorkbenchMetricTimeScope.YEARLY)) {
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
            cardName = PROJ_ESTABLISH_COUNT_CARD_NAME;
        } else {
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            cardName = MONTH_PROJ_ESTABLISH_COUNT_CARD_NAME;
        }
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery().eq(WorkbenchCardMetric::getMetricName, cardName))
                .stream().collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            int count = projEstablishBaseInfoService.count(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .eq(!WorkbenchMetricDeptScope.ZSZL.name().equals(deptScope.name()), ProjEstablishBaseInfo::getBizDeptId,
                            sysUserService.getOrgIdByCode(deptScope.name()))
                    .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                    .ge(BaseModel::getCreateTime, startDate.atStartOfDay()));
            tmpMap.get(deptScope.name()).setValue(String.valueOf(count));
        }
        return new ArrayList<>(tmpMap.values());
    }

    private List<WorkbenchCardMetric> newReview() {
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .in(WorkbenchCardMetric::getMetricName,
                                        PROJ_REVIEW_COUNT_CARD_NAME, PROJ_REVIEW_PASS_RATE_CARD_NAME))
                .stream().collect(Collectors.toMap(metric ->
                        String.join("-", metric.getScope(), metric.getMetricName()), v -> v));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoService.list(
                    Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                            .eq(!WorkbenchMetricDeptScope.ZSZL.name().equals(deptScope.name()),
                                    ProjReviewBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                            .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_UN_SUBMIT.name())
                            .ge(BaseModel::getCreateTime,
                                    LocalDate.now().atStartOfDay().with(TemporalAdjusters.firstDayOfYear())));
            List<ProjReviewBaseInfo> effectInfos = reviewBaseInfos.stream()
                    .filter(info -> RecordStatus.TAKE_EFFECT.name().equals(info.getProjReviewStatus()))
                    .collect(Collectors.toList());
            tmpMap.get(String.join("-", deptScope.name(), PROJ_REVIEW_COUNT_CARD_NAME))
                    .setValue(String.valueOf(reviewBaseInfos.size()));
            if (reviewBaseInfos.isEmpty()) {
                tmpMap.get(String.join("-", deptScope.name(), PROJ_REVIEW_PASS_RATE_CARD_NAME))
                        .setValue("0");
            } else {
                String passRate = new BigDecimal(effectInfos.size() * 100)
                        .divide(new BigDecimal(reviewBaseInfos.size()), 2, RoundingMode.HALF_UP).toString();
                tmpMap.get(String.join("-", deptScope.name(), PROJ_REVIEW_PASS_RATE_CARD_NAME))
                        .setValue(passRate);
            }
        }
        return new ArrayList<>(tmpMap.values());
    }

    public List<WorkbenchCardMetric> newLaunchAmount(WorkbenchMetricTimeScope timeScope) {
        LocalDate startDate;
        String cardName;
        if (timeScope.equals(WorkbenchMetricTimeScope.YEARLY)) {
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
            cardName = TOTAL_LAUNCH_AMOUNT_CARD_NAME;
        } else {
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            cardName = MONTH_TOTAL_LAUNCH_AMOUNT_CARD_NAME;
        }
        Map<String, WorkbenchCardMetric> tmpMap = baseMapper.selectList(
                        Wrappers.<WorkbenchCardMetric>lambdaQuery()
                                .eq(WorkbenchCardMetric::getMetricName, cardName)).stream()
                .collect(Collectors.toMap(WorkbenchCardMetric::getScope, v -> v));

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            Set<Long> targetContractIds;
            if (WorkbenchMetricDeptScope.ZSZL.name().equals(deptScope.name())) {
                targetContractIds = null;
            } else {
                targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                        .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                ).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            }
            String totalLaunch = actualDetailService
                    .writtenOffDetailsByContractIds(targetContractIds, startDate, null)
                    .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString();
            tmpMap.get(deptScope.name()).setValue(totalLaunch);
        }
        return new ArrayList<>(tmpMap.values());
    }

    public List<CardMetricChooseDto> currentUserChooseVo() {
        List<SelectRSP> selectRsps = listRole().getRoleList();
        if (selectRsps.isEmpty()) {
            return new ArrayList<>();
        }
        // 综合管理人员可以看到所有的卡片，项目经理及业务管能看到的卡片相同
        String scope = "JSSYB";
        for (SelectRSP selectRsp : selectRsps) {
            if ("COMPREHENSIVE_MANAGEMENT".equals(selectRsp.getValue())) {
                scope = "ZSZL";
                break;
            }
        }
        List<WorkbenchCardMetric> targetCards = list(Wrappers.<WorkbenchCardMetric>lambdaQuery()
                .eq(WorkbenchCardMetric::getScope, scope));

        Long userId = AccountUtil.getLoginInfo().getId();
        Set<String> choosedMetricNames = workbenchCardUserRefService.list(
                        Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                                .eq(WorkbenchCardUserRef::getUserId, userId)).stream()
                .map(WorkbenchCardUserRef::getMetricName)
                .collect(Collectors.toSet());
        List<CardMetricChooseDto> result = new ArrayList<>();
        for (WorkbenchCardMetric targetCard : targetCards) {
            CardMetricChooseDto vo = new CardMetricChooseDto();
            vo.setMetricName(targetCard.getMetricName());
            if (choosedMetricNames.contains(targetCard.getMetricName())) {
                vo.setSelected(true);
            } else {
                vo.setSelected(false);
            }
            result.add(vo);
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyChoosedCard(List<CardMetricChooseDto> dtos) {
        Set<String> selectedCards = dtos.stream().filter(CardMetricChooseDto::getSelected).map(CardMetricChooseDto::getMetricName).collect(Collectors.toSet());
        Long userId = AccountUtil.getLoginInfo().getId();
        workbenchCardUserRefService.remove(Wrappers.<WorkbenchCardUserRef>lambdaQuery()
                .eq(WorkbenchCardUserRef::getUserId, userId));

        List<WorkbenchCardUserRef> refs = new ArrayList<>();
        for (String selectedCard : selectedCards) {
            WorkbenchCardUserRef ref = new WorkbenchCardUserRef();
            ref.setUserId(userId);
            ref.setMetricName(selectedCard);
            refs.add(ref);
        }
        workbenchCardUserRefService.saveBatch(refs);
    }
}
