package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workbench.application.WorkbenchOverallReturnRatePort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchOverallReturnRatePortAdapter implements WorkbenchOverallReturnRatePort {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;

    @Override
    public Set<Long> listActiveClientIdsByDeptCode(String deptCode) {
        return clientService.list(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptCode))
                        .eq(Client::getClientStatus, "TAKE_EFFECT"))
                .stream()
                .map(Client::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> listClientIdsByIndustry(String industry) {
        return corpCommerceInfoLibService.listNewestCommerceInfo(new CorpCommerceInfoLibDto()
                        .setInRiskControlIndustryClassify(java.util.Collections.singletonList(industry)))
                .stream()
                .map(ClientBaseModel::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> listClientIdsExcludingIndustries(Set<String> industries) {
        return corpCommerceInfoLibService.listNewestCommerceInfo(new CorpCommerceInfoLibDto()
                        .setNotInRiskControlIndustryClassify(industries.stream().collect(Collectors.toList())))
                .stream()
                .map(ClientBaseModel::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public BigDecimal calculateAverageIrr(Set<Long> clientIds, @Nullable LocalDate endTime) {
        List<ContractBaseInfo> contracts;
        if (clientIds == null) {
            contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getContractStatus, "START_RENT")
                    .le(ContractBaseInfo::getCreateTime, endTime));
        } else {
            if (clientIds.isEmpty()) {
                return BigDecimal.ZERO;
            }
            contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getClientId, clientIds)
                    .eq(ContractBaseInfo::getContractStatus, "START_RENT")
                    .le(ContractBaseInfo::getCreateTime, endTime));
        }
        if (contracts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> contractIds = contracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<PaymentActualDetail> actualDetails =
                paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getContractId, contractIds)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(PaymentActualDetail::getPaidInDate, endTime));

        if (actualDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAmount = actualDetails.stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<Long, List<PaymentActualDetail>> groupByContract = actualDetails.stream()
                .collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        Map<Long, Integer> irrs = contractPriceService.queryNewestIrr(contractIds);

        BigDecimal averageIrr = BigDecimal.ZERO;
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : groupByContract.entrySet()) {
            Integer irr = irrs.get(entry.getKey());
            if (ObjectUtil.isEmpty(irr)) {
                continue;
            }
            BigDecimal oneContractTotal = entry.getValue().stream()
                    .map(PaymentActualDetail::getPaidInAmount)
                    .map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageIrr = oneContractTotal.divide(totalAmount, 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(irr)).add(averageIrr);
        }
        return averageIrr.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
    }
}
