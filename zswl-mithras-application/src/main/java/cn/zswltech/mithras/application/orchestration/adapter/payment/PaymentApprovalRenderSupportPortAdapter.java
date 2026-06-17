package cn.zswltech.mithras.application.orchestration.adapter.payment;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.contract.versioning.service.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractLeasePriceLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractMortgageLibService;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.payment.application.render.PaymentApprovalRenderSnapshot;
import cn.zswltech.mithras.payment.application.render.PaymentApprovalRenderSupportPort;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentApprovalRenderSupportPortAdapter implements PaymentApprovalRenderSupportPort {

    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;

    @Override
    public PaymentApprovalRenderSnapshot getBaseRenderSnapshot(PaymentBaseInfo paymentBaseInfo) {
        OrgDO org = orgDOMapper.selectByPrimaryKey(paymentBaseInfo.getConBizDeptId());
        UserDO user = userDOMapper.selectByPrimaryKey(paymentBaseInfo.getCreateBy());
        ContractBaseInfoLib contractBaseInfo = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());

        return PaymentApprovalRenderSnapshot.builder()
                .bizDeptName(Optional.ofNullable(org).map(OrgDO::getName).orElse(""))
                .sponsorName(Optional.ofNullable(user).map(UserDO::getUserName).orElse(""))
                .projReviewId(Optional.ofNullable(contractBaseInfo).map(ContractBaseInfoLib::getProjReviewId).orElse(null))
                .projectType(Optional.ofNullable(contractBaseInfo).map(ContractBaseInfoLib::getProjectType).orElse(null))
                .consultingContractCode(Optional.ofNullable(contractBaseInfo).map(ContractBaseInfoLib::getConsultingContractCode).orElse(""))
                .build();
    }

    @Override
    public PaymentApprovalRenderSnapshot getLeaseRenderSnapshot(PaymentBaseInfo paymentBaseInfo) {
        PaymentApprovalRenderSnapshot baseSnapshot = getBaseRenderSnapshot(paymentBaseInfo);
        Client client = clientMapper.selectById(paymentBaseInfo.getClientId());
        CorpCommerceInfoLib corpCommerceInfo = getNewestCorpCommerceInfo(paymentBaseInfo.getClientId(), client);
        ContractLeasePrice contractLeasePrice = contractLeasePriceLibService.getLatestLib(paymentBaseInfo.getContractId());
        ContractBaseInfoLib contractBaseInfo = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());

        return PaymentApprovalRenderSnapshot.builder()
                .bizDeptName(baseSnapshot.getBizDeptName())
                .sponsorName(baseSnapshot.getSponsorName())
                .projReviewId(baseSnapshot.getProjReviewId())
                .projectType(baseSnapshot.getProjectType())
                .lesseeName(Optional.ofNullable(client).map(Client::getClientName).orElse(""))
                .applyCreditAmount(Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getApplyCreditAmount).orElse(null))
                .earnestMoney(Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getEarnestMoney).orElse(null))
                .leaseMonthCount(Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getLeaseMonthCount).orElse(null))
                .consultingFee(Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getConsultingFee).orElse(null))
                .nominalPrice(Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getNominalPrice).orElse(null))
                .legalPerson(Optional.ofNullable(corpCommerceInfo).map(CorpCommerceInfo::getCorpRepresent).orElse(""))
                .consultingContractCode(baseSnapshot.getConsultingContractCode())
                .guarantorContractCodes(listGuarantorContractCodes(paymentBaseInfo.getContractId(), contractBaseInfo))
                .mortgageContractCodes(listMortgageContractCodes(paymentBaseInfo.getContractId(), contractBaseInfo))
                .build();
    }

    private CorpCommerceInfoLib getNewestCorpCommerceInfo(Long clientId, Client client) {
        return corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                .eq(CorpCommerceInfoLib::getClientId, clientId)
                .eq(CorpCommerceInfoLib::getVersion, Optional.ofNullable(client).map(Client::getNewestVersion).orElse(null))
                .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL));
    }

    private List<String> listGuarantorContractCodes(Long contractId, ContractBaseInfoLib contractBaseInfo) {
        if (contractBaseInfo == null) {
            return Collections.emptyList();
        }
        List<ContractGuarantorLib> guarantors = contractGuarantorLibService.listByVersion(contractId, contractBaseInfo.getVersion());
        if (CollUtil.isEmpty(guarantors)) {
            return Collections.emptyList();
        }
        return guarantors.stream()
                .map(ContractGuarantor::getGuarantorContractCode)
                .collect(Collectors.toList());
    }

    private List<String> listMortgageContractCodes(Long contractId, ContractBaseInfoLib contractBaseInfo) {
        if (contractBaseInfo == null) {
            return Collections.emptyList();
        }
        List<ContractMortgageLib> mortgages = contractMortgageLibService.listByVersion(contractId, contractBaseInfo.getVersion());
        if (CollUtil.isEmpty(mortgages)) {
            return Collections.emptyList();
        }
        return mortgages.stream()
                .map(ContractMortgage::getMortgageContractCode)
                .collect(Collectors.toList());
    }
}
