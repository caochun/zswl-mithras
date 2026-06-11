package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workbench.application.WorkbenchCardMetricPort;
import cn.zswltech.mithras.workbench.application.WorkbenchCardReviewStats;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricTimeScope;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchCardMetricPortAdapter implements WorkbenchCardMetricPort {
    private static final BigDecimal DISPLAY_AMOUNT_DIVISOR = BigDecimal.valueOf(100000000L);

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService clientLibService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;

    @Override
    public List<String> getCurrentUserRoles() {
        return sysUserService.getCurrentUserRoles();
    }

    @Override
    public String countFirstLaunchClientsBySponsor(Long userId, LocalDateTime startTime) {
        Set<Long> actualPaidClientIds = actualDetailService.queryFirstLaunchClient(startTime)
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toSet());
        int clientCount = clientService.count(Wrappers.<Client>lambdaQuery()
                .eq(Client::getBelongSponsorId, userId)
                .in(Client::getId, actualPaidClientIds));
        return String.valueOf(clientCount);
    }

    @Override
    public String countProjectEstablishBySponsor(Long userId, LocalDateTime startTime) {
        int count = projEstablishBaseInfoService.count(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjSponsorUserId, userId)
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                .ge(BaseModel::getCreateTime, startTime));
        return String.valueOf(count);
    }

    @Override
    public WorkbenchCardReviewStats reviewStatsBySponsor(Long userId, LocalDateTime startTime) {
        List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoService.list(
                Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .eq(ProjReviewBaseInfo::getProjSponsorUserId, userId)
                        .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_UN_SUBMIT.name())
                        .ge(BaseModel::getCreateTime, startTime));
        return reviewStats(reviewBaseInfos);
    }

    @Override
    public String calculateLaunchAmountBySponsor(Long userId, LocalDate startDay) {
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus,
                                ListUtil.toList(ContractStatus.TAKE_EFFECT.name(),
                                        ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId))
                .stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        BigDecimal amount = actualDetailService.writtenOffDetailsByContractIds(targetContractIds, startDay, null).stream()
                .map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return displayAmount(amount);
    }

    @Override
    public String calculateRemainingPrincipalBySponsor(Long userId) {
        Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getBelongSponsorId, userId)
                        .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name()))
                .stream().map(Client::getId).collect(Collectors.toSet());
        if (targetClientIds.isEmpty()) {
            return "0.00";
        }
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(targetClientIds);
        BigDecimal total = remainingPrincipalService.remainingPrincipalGroupByClientId(dto).values().stream()
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        return displayAmount(total);
    }

    @Override
    public String countInventoryProjectsBySponsor(Long userId) {
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT"));
        Set<Long> projReviewIds = contracts.stream()
                .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
        return String.valueOf(projReviewIds.size());
    }

    @Override
    public String countOverdueProjectsBySponsor(Long userId) {
        List<CollectionBaseInfo> overdueRecord = listOverdueRentCollections();
        if (overdueRecord.isEmpty()) {
            return "0";
        }
        Set<Long> contractId = overdueRecord.stream()
                .map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        Set<Long> projectReviewIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getId, contractId))
                .stream().map(ContractBaseInfo::getProjReviewId)
                .collect(Collectors.toSet());
        return String.valueOf(projectReviewIds.size());
    }

    @Override
    public String countDefectiveProjectsBySponsor(Long userId) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
        if (!assetClassify.isPresent()) {
            return "0";
        }
        Set<Long> lastThreeClientIds = listLastThreeClientIds(assetClassify.get().getId());
        if (lastThreeClientIds.isEmpty()) {
            return "0";
        }
        Set<Long> projectReviewIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getClientId, lastThreeClientIds))
                .stream()
                .map(ContractBaseInfo::getProjReviewId)
                .collect(Collectors.toSet());
        return String.valueOf(projectReviewIds.size());
    }

    @Override
    public Map<String, String> countFirstLaunchClientsByDeptScope() {
        Map<String, String> result = new HashMap<>();
        LocalDate firstDayYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        Set<Long> targetClientIds = actualDetailService.queryFirstLaunchClient(firstDayYear.atStartOfDay())
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toSet());
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                result.put(deptScope.name(), String.valueOf(targetClientIds.size()));
            } else {
                int newClientCount = clientService.count(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                        .in(Client::getId, targetClientIds));
                result.put(deptScope.name(), String.valueOf(newClientCount));
            }
        }
        return result;
    }

    @Override
    public Map<String, String> countProjectEstablishByDeptScope(WorkbenchMetricTimeScope timeScope) {
        Map<String, String> result = new HashMap<>();
        LocalDate startDate = startDate(timeScope);
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            int count = projEstablishBaseInfoService.count(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .eq(!WorkbenchMetricDeptScope.ZSZL.equals(deptScope), ProjEstablishBaseInfo::getBizDeptId,
                            sysUserService.getOrgIdByCode(deptScope.name()))
                    .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                    .ge(BaseModel::getCreateTime, startDate.atStartOfDay()));
            result.put(deptScope.name(), String.valueOf(count));
        }
        return result;
    }

    @Override
    public Map<String, WorkbenchCardReviewStats> reviewStatsByDeptScope() {
        Map<String, WorkbenchCardReviewStats> result = new HashMap<>();
        LocalDateTime startTime = LocalDate.now().atStartOfDay().with(TemporalAdjusters.firstDayOfYear());
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoService.list(
                    Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                            .eq(!WorkbenchMetricDeptScope.ZSZL.equals(deptScope),
                                    ProjReviewBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                            .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_UN_SUBMIT.name())
                            .ge(BaseModel::getCreateTime, startTime));
            result.put(deptScope.name(), reviewStats(reviewBaseInfos));
        }
        return result;
    }

    @Override
    public Map<String, String> calculateLaunchAmountByDeptScope(WorkbenchMetricTimeScope timeScope) {
        Map<String, String> result = new HashMap<>();
        LocalDate startDate = startDate(timeScope);
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            Set<Long> targetContractIds;
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                targetContractIds = null;
            } else {
                targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                                .in(ContractBaseInfo::getContractStatus,
                                        ListUtil.toList(ContractStatus.TAKE_EFFECT.name(),
                                                ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                                .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name())))
                        .stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            }
            BigDecimal totalLaunch = actualDetailService.writtenOffDetailsByContractIds(targetContractIds, startDate, null)
                    .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put(deptScope.name(), displayAmount(totalLaunch));
        }
        return result;
    }

    @Override
    public Map<String, String> calculateRemainingPrincipalByDeptScope() {
        Map<String, String> result = new HashMap<>();
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                result.put(deptScope.name(), displayAmount(remainingPrincipalService.remainingPrincipal(null)));
                continue;
            }
            Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                            .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name()))
                    .stream().map(Client::getId).collect(Collectors.toSet());
            if (targetClientIds.isEmpty()) {
                result.put(deptScope.name(), "0.00");
                continue;
            }
            RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
            dto.setClientIds(targetClientIds);
            BigDecimal total = remainingPrincipalService.remainingPrincipalGroupByClientId(dto).values().stream()
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put(deptScope.name(), displayAmount(total));
        }
        return result;
    }

    @Override
    public Map<String, String> countInventoryProjectsByDeptScope() {
        Map<String, String> result = new HashMap<>();
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT"));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            Set<Long> projReviewIds;
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                projReviewIds = contracts.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                projReviewIds = contracts.stream().filter(contract -> contract.getBizDeptId().equals(deptId))
                        .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            }
            result.put(deptScope.name(), String.valueOf(projReviewIds.size()));
        }
        return result;
    }

    @Override
    public Map<String, String> countOverdueProjectsByDeptScope() {
        Map<String, String> result = defaultZeroResult();
        List<CollectionBaseInfo> overdueRecord = listOverdueRentCollections();
        if (overdueRecord.isEmpty()) {
            return result;
        }
        Set<Long> contractId = overdueRecord.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractId));
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            Set<Long> projReviewIds;
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                projReviewIds = contracts.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                projReviewIds = contracts.stream().filter(contract -> contract.getBizDeptId().equals(deptId))
                        .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
            }
            result.put(deptScope.name(), String.valueOf(projReviewIds.size()));
        }
        return result;
    }

    @Override
    public Map<String, String> countDefectiveProjectsByDeptScope() {
        Map<String, String> result = defaultZeroResult();
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
        if (!assetClassify.isPresent()) {
            return result;
        }
        Set<Long> lastThreeClientIds = listLastThreeClientIds(assetClassify.get().getId());
        if (lastThreeClientIds.isEmpty()) {
            return result;
        }
        List<ProjReviewBaseInfo> lastThreeTakeEffect = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")
                .in(ProjReviewBaseInfo::getClientId, lastThreeClientIds));
        if (lastThreeTakeEffect.isEmpty()) {
            return result;
        }
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                result.put(deptScope.name(), String.valueOf(lastThreeTakeEffect.size()));
            } else {
                Long deptId = sysUserService.getOrgIdByCode(deptScope.name());
                result.put(deptScope.name(), String.valueOf(lastThreeTakeEffect.stream()
                        .filter(review -> review.getBizDeptId().equals(deptId)).count()));
            }
        }
        return result;
    }

    private WorkbenchCardReviewStats reviewStats(List<ProjReviewBaseInfo> reviewBaseInfos) {
        if (reviewBaseInfos.isEmpty()) {
            return new WorkbenchCardReviewStats("0", "0");
        }
        long effectCount = reviewBaseInfos.stream()
                .filter(info -> RecordStatus.TAKE_EFFECT.name().equals(info.getProjReviewStatus()))
                .count();
        String passRate = new BigDecimal(effectCount * 100)
                .divide(new BigDecimal(reviewBaseInfos.size()), 2, RoundingMode.HALF_UP)
                .toString();
        return new WorkbenchCardReviewStats(String.valueOf(reviewBaseInfos.size()), passRate);
    }

    private List<CollectionBaseInfo> listOverdueRentCollections() {
        return collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                .isNull(CollectionBaseInfo::getCollectionDate)
                .eq(CollectionBaseInfo::getCashFlowItem, "RENT"));
    }

    private Set<Long> listLastThreeClientIds(Long classifyId) {
        return clientLibService.newestClassifyClientLib(classifyId)
                .stream()
                .filter(lib -> AssetClassifyResultEnum.lastThree().contains(lib.getClassifyResult()))
                .map(AssetClassifyClient::getClientId)
                .collect(Collectors.toSet());
    }

    private LocalDate startDate(WorkbenchMetricTimeScope timeScope) {
        if (WorkbenchMetricTimeScope.YEARLY.equals(timeScope)) {
            return LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        }
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    private Map<String, String> defaultZeroResult() {
        Map<String, String> result = new HashMap<>();
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            result.put(deptScope.name(), "0");
        }
        return result;
    }

    private String displayAmount(BigDecimal amount) {
        return amount.divide(DISPLAY_AMOUNT_DIVISOR, 2, RoundingMode.HALF_UP).toString();
    }
}
