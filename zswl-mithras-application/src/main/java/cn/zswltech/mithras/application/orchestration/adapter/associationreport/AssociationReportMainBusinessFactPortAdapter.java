package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessClientSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessContractSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessCorpSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessFactPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessGuaranteeSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessLesseeSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessPaymentActualSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessPaymentSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportMainBusinessReceiptSnapshot;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.enums.contract.MortgageTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.PledgeTypeEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AssociationReportMainBusinessFactPortAdapter implements AssociationReportMainBusinessFactPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;

    @Override
    public List<AssociationReportMainBusinessContractSnapshot> listAllStartRentContracts() {
        return contractBaseInfoService.listAllStartRent().stream()
                .map(this::toContractSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public AssociationReportMainBusinessLesseeSnapshot getMainLessee(Long contractId) {
        ContractTenantry contractTenantry = contractTenantryService.getMain(contractId);
        if (Objects.isNull(contractTenantry)) {
            return null;
        }
        return AssociationReportMainBusinessLesseeSnapshot.builder()
                .lesseeId(contractTenantry.getLesseeId())
                .build();
    }

    @Override
    public AssociationReportMainBusinessClientSnapshot getClient(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            return null;
        }
        return AssociationReportMainBusinessClientSnapshot.builder()
                .id(client.getId())
                .clientName(client.getClientName())
                .uscCode(client.getUscCode())
                .build();
    }

    @Override
    public AssociationReportMainBusinessCorpSnapshot getCorpCommerceInfo(Long clientId) {
        List<CorpCommerceInfo> corpCommerceInfos = corpCommerceInfoService.findByClientId(clientId);
        if (CollectionUtil.isEmpty(corpCommerceInfos)) {
            return null;
        }
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfos.get(0);
        return AssociationReportMainBusinessCorpSnapshot.builder()
                .industryType(corpCommerceInfo.getIndustryType())
                .orgScale(corpCommerceInfo.getOrgScale())
                .build();
    }

    @Override
    public AssociationReportMainBusinessGuaranteeSnapshot getGuarantee(Long contractId) {
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.listByContractId(contractId);
        List<ContractMortgage> contractMortgageList = contractMortgageService.listByContractId(contractId);
        List<ContractPledge> contractPledgeList = contractPledgeService.listByContractId(contractId);
        return AssociationReportMainBusinessGuaranteeSnapshot.builder()
                .guarantorNames(guarantorNames(contractGuarantorList))
                .mortgageDescriptions(mortgageDescriptions(contractMortgageList))
                .pledgeDescriptions(pledgeDescriptions(contractPledgeList))
                .build();
    }

    @Override
    public List<AssociationReportMainBusinessPaymentSnapshot> listPaymentsByContractIds(List<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getContractId, contractIds)
                        .ne(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name())
                ).stream()
                .map(paymentBaseInfo -> AssociationReportMainBusinessPaymentSnapshot.builder()
                        .id(paymentBaseInfo.getId())
                        .receiptId(paymentBaseInfo.getReceiptId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<AssociationReportMainBusinessPaymentActualSnapshot> listPaymentActualByPaymentIds(Collection<Long> paymentIds) {
        if (CollectionUtil.isEmpty(paymentIds)) {
            return Collections.emptyList();
        }
        return paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getPaymentId, paymentIds)
                ).stream()
                .map(paymentActualDetail -> AssociationReportMainBusinessPaymentActualSnapshot.builder()
                        .paymentId(paymentActualDetail.getPaymentId())
                        .paidInDate(paymentActualDetail.getPaidInDate())
                        .paidInAmount(paymentActualDetail.getPaidInAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AssociationReportMainBusinessReceiptSnapshot getReceipt(Long receiptId) {
        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            return null;
        }
        return AssociationReportMainBusinessReceiptSnapshot.builder()
                .id(contractReceipt.getId())
                .actualIrr(contractReceipt.getActualIrr())
                .build();
    }

    private AssociationReportMainBusinessContractSnapshot toContractSnapshot(ContractBaseInfo contractBaseInfo) {
        return AssociationReportMainBusinessContractSnapshot.builder()
                .id(contractBaseInfo.getId())
                .contractCode(contractBaseInfo.getContractCode())
                .leaseType(contractBaseInfo.getLeaseType())
                .bizType(contractBaseInfo.getBizType())
                .leaseItemTypes(contractBaseInfo.getLeaseItemTypes())
                .build();
    }

    private List<String> guarantorNames(List<ContractGuarantor> contractGuarantorList) {
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            return Collections.emptyList();
        }
        List<Long> clientIds = new LinkedList<>();
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            clientIds.addAll(JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class));
        }
        return clientNames(clientIds);
    }

    private List<String> mortgageDescriptions(List<ContractMortgage> contractMortgageList) {
        if (CollectionUtil.isEmpty(contractMortgageList)) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        for (ContractMortgage contractMortgage : contractMortgageList) {
            List<String> names = clientNames(JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class));
            if (CollectionUtil.isEmpty(names)) {
                continue;
            }
            MortgageTypeEnum type = MortgageTypeEnum.findByName(contractMortgage.getContractMortgageType());
            result.add(StrUtil.join("、", names) + Optional.ofNullable(type).map(MortgageTypeEnum::display).orElse(""));
        }
        return result;
    }

    private List<String> pledgeDescriptions(List<ContractPledge> contractPledgeList) {
        if (CollectionUtil.isEmpty(contractPledgeList)) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        for (ContractPledge contractPledge : contractPledgeList) {
            List<String> names = clientNames(JSONUtil.toList(contractPledge.getPledgeIds(), Long.class));
            if (CollectionUtil.isEmpty(names)) {
                continue;
            }
            PledgeTypeEnum type = PledgeTypeEnum.findByName(contractPledge.getContractPledgeType());
            result.add(StrUtil.join("、", names) + Optional.ofNullable(type).map(PledgeTypeEnum::display).orElse(""));
        }
        return result;
    }

    private List<String> clientNames(Collection<Long> clientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        List<Client> clientList = clientMapper.selectBatchIds(clientIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        if (CollectionUtil.isEmpty(clientList)) {
            return Collections.emptyList();
        }
        return clientList.stream()
                .map(Client::getClientName)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
