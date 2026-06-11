package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workbench.application.WorkbenchRadarChartMetricPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchRadarChartMetricPortAdapter implements WorkbenchRadarChartMetricPort {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ClientService clientService;

    @Override
    public int countUsersByDeptScope(String deptScope) {
        if ("ZSZL".equals(deptScope)) {
            return sysUserService.countUserByRoleAndOrg(null);
        }
        return sysUserService.countUserByRoleAndOrg(deptScope);
    }

    @Override
    public Set<Long> listBusinessUserIds(String roleCode) {
        return sysUserService.getUserIdsByRole(roleCode);
    }

    @Override
    public Set<Long> listCurrentYearContractIdsByDeptScope(String deptScope, LocalDateTime startTime,
                                                           Collection<String> contractStatuses) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(!"ZSZL".equals(deptScope), ContractBaseInfo::getBizDeptId,
                                sysUserService.getOrgIdByCode(deptScope))
                        .ge(ContractBaseInfo::getCreateTime, startTime)
                        .in(ContractBaseInfo::getContractStatus, contractStatuses))
                .stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> listCurrentYearContractIdsBySponsor(Long userId, LocalDateTime startTime,
                                                         Collection<String> contractStatuses) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .ge(ContractBaseInfo::getCreateTime, startTime)
                        .in(ContractBaseInfo::getContractStatus, contractStatuses))
                .stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public BigDecimal calculateLaunchAmountByDeptScope(String deptScope, LocalDate start) {
        Set<Long> targetClientIds;
        if ("ZSZL".equals(deptScope)) {
            targetClientIds = null;
        } else {
            targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope))
                            .eq(Client::getClientStatus, "TAKE_EFFECT"))
                    .stream()
                    .map(Client::getId)
                    .collect(Collectors.toSet());
        }
        return paymentActualDetailService.writtenOffDetailsByClientIds(targetClientIds, start, null).stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateLaunchAmountBySponsor(Long userId, LocalDate start) {
        Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getBelongSponsorId, userId)
                        .eq(Client::getClientStatus, "TAKE_EFFECT"))
                .stream()
                .map(Client::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetClientIds)) {
            return BigDecimal.ZERO;
        }

        BigDecimal targetAmount = paymentActualDetailService.writtenOffDetailsByClientIds(targetClientIds, start, null)
                .stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return targetAmount.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateConsultingFees(Set<Long> contractIds, int personCount) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return BigDecimal.ZERO;
        }
        List<ContractLeasePriceLib> contractLeasePriceLibs = contractLeasePriceLibService.queryNewestLib(contractIds);
        List<ContractFactoringPriceLib> contractFactoringPriceLibs = contractFactoringPriceLibService.queryNewestLib(contractIds);
        List<ContractAocPriceLib> contractAocPriceLibs = contractAocPriceLibService.queryNewestLib(contractIds);
        BigDecimal totalFee = contractLeasePriceLibs.stream().map(c -> {
                    Long consultingFee = Objects.isNull(c.getConsultingFee()) ? 0L : c.getConsultingFee();
                    Long commission = Objects.isNull(c.getCommission()) ? 0L : c.getCommission();
                    return consultingFee + commission;
                })
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFee = contractFactoringPriceLibs.stream().map(ContractFactoringPriceLib::getConsultingFee)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(totalFee, BigDecimal::add);
        totalFee = contractAocPriceLibs.stream().map(ContractAocPriceLib::getConsultingFee)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(totalFee, BigDecimal::add);

        return totalFee.divide(BigDecimal.valueOf(personCount * 100000000L), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateCollectionRateByDeptScope(String deptScope, LocalDate start, LocalDate end) {
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(!"ZSZL".equals(deptScope), ContractBaseInfo::getBizDeptId,
                                sysUserService.getOrgIdByCode(deptScope))
                        .in(ContractBaseInfo::getContractStatus, "START_RENT"))
                .stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetContractIds)) {
            return BigDecimal.valueOf(100);
        }
        return calculateCollectionRate(targetContractIds, start, end, true);
    }

    @Override
    public BigDecimal calculateCollectionRateBySponsor(Long userId, LocalDate start, LocalDate end) {
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getContractStatus, "START_RENT"))
                .stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetContractIds)) {
            return BigDecimal.valueOf(100);
        }
        return calculateCollectionRate(targetContractIds, start, end, false);
    }

    @Override
    public BigDecimal calculateProfit(Set<Long> contractIds) {
        Map<Long, Pair<Long, Integer>> rateMap = contractPriceService.queryNewestIrrRate(contractIds);
        if (rateMap.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalAmount = rateMap.values().stream()
                .map(Pair::getKey)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = BigDecimal.ZERO;
        for (Map.Entry<Long, Pair<Long, Integer>> entry : rateMap.entrySet()) {
            Long contractAmount = entry.getValue().getKey();
            Integer contractRate = entry.getValue().getValue();
            average = totalAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(contractAmount).divide(totalAmount, 10, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(contractRate))
                            .add(average);
        }
        return average.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCollectionRate(Set<Long> contractIds, LocalDate start, LocalDate end,
                                               boolean zeroTotalAsZero) {
        List<CollectionBaseInfo> collections = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, contractIds)
                .ge(CollectionBaseInfo::getPlanCollectionDate, start)
                .le(CollectionBaseInfo::getPlanCollectionDate, end));
        BigDecimal totalPrincipal = collections.stream().map(CollectionBaseInfo::getPrincipal)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOverduePrincipal = collections.stream()
                .filter(CollectionBaseInfo::overdued)
                .map(CollectionBaseInfo::getPrincipal)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tmp;
        if (totalPrincipal.compareTo(BigDecimal.ZERO) == 0) {
            tmp = zeroTotalAsZero ? BigDecimal.ZERO : totalOverduePrincipal.divide(BigDecimal.ONE, 6, RoundingMode.HALF_UP);
        } else {
            tmp = totalOverduePrincipal.divide(totalPrincipal, 6, RoundingMode.HALF_UP);
        }
        return new BigDecimal(1).subtract(tmp).multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
