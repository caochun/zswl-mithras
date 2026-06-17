package cn.zswltech.mithras.application.orchestration.adapter.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoSupportPort;
import cn.zswltech.mithras.payment.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoContractContextSnapshot;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoContractParticipantSnapshot;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoContractSnapshot;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoMaterialSnapshot;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoOuterQuerySnapshot;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.third.providence.persistence.model.OuterInfoRecord;
import cn.zswltech.mithras.third.providence.service.impl.OuterInfoRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentPublicInfoSupportPortAdapter implements PaymentPublicInfoSupportPort {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FileService fileService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private OuterInfoRecordService outerInfoRecordService;

    @Override
    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        return id2NameService.clientId2Name(clientIds);
    }

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }

    @Override
    public List<UserDO> getUserByDeptCode(String orgCode) {
        return sysUserService.getUserByDeptCode(orgCode);
    }

    @Override
    public List<PaymentPublicInfoMaterialSnapshot> listMaterialSnapshots(Collection<Long> belongIds) {
        if (CollUtil.isEmpty(belongIds)) {
            return Collections.emptyList();
        }
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BUSINESS_TYPE_PUBLIC_INFO)
                        .in(MaterialsList::getBelongId, belongIds))
                .stream()
                .map(this::toMaterialSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileListRSP> listMaterialFiles(Collection<Long> belongIds, Collection<PublicInfoFileTypeEnum> fileTypes) {
        if (CollUtil.isEmpty(belongIds)) {
            return Collections.emptyList();
        }
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BUSINESS_TYPE_PUBLIC_INFO)
                .in(MaterialsList::getBelongId, belongIds)
                .in(CollUtil.isNotEmpty(fileTypes), MaterialsList::getMaterialSubType,
                        fileTypes.stream().map(PublicInfoFileTypeEnum::name).collect(Collectors.toList())))
                .stream()
                .map(this::toFileListRSP)
                .collect(Collectors.toList());
    }

    @Override
    public Long addMaterial(InputStream inputStream, String fileName, Long belongId, String materialsType, String materialsSubType, YesOrNoNumberEnum systemGenerate) throws IOException {
        return materialsListService.add(inputStream, fileName, belongId, materialsType, materialsSubType, BUSINESS_TYPE_PUBLIC_INFO, systemGenerate);
    }

    @Override
    public PaymentPublicInfoContractContextSnapshot getContractContextByContractId(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        if (contractBaseInfo == null) {
            return null;
        }
        Client client = clientMapper.selectById(contractBaseInfo.getClientId());
        return PaymentPublicInfoContractContextSnapshot.builder()
                .contractId(contractBaseInfo.getId())
                .clientId(contractBaseInfo.getClientId())
                .originClientType(Optional.ofNullable(client).map(Client::getClientType).orElse(null))
                .projSponsorUserId(contractBaseInfo.getProjSponsorUserId())
                .projReviewId(contractBaseInfo.getProjReviewId())
                .build();
    }

    @Override
    public PaymentPublicInfoContractParticipantSnapshot getContractParticipantSnapshot(Long contractId) {
        return PaymentPublicInfoContractParticipantSnapshot.builder()
                .tenantryIds(listTenantryIds(contractId))
                .guarantorIds(listGuarantorIds(contractId))
                .mortgageIds(listMortgageIds(contractId))
                .pledgeIds(listPledgeIds(contractId))
                .build();
    }

    @Override
    public List<PaymentPublicInfoContractSnapshot> listContractSnapshotsByProjReviewId(Long projReviewId) {
        return contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjReviewId, projReviewId))
                .stream()
                .map(this::toContractSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public PublicInfoOuterQuerySnapshot getLatestOuterQuerySnapshot(Long publicInfoQueryId) {
        OuterInfoRecord latest = outerInfoRecordService.getOne(Wrappers.<OuterInfoRecord>lambdaQuery()
                .eq(OuterInfoRecord::getPublicInfoQueryId, publicInfoQueryId)
                .orderByDesc(OuterInfoRecord::getVersion)
                .last("limit 1"));
        if (latest == null) {
            return null;
        }
        Map<String, List<String>> queryResultByConfigKey = outerInfoRecordService.list(Wrappers.<OuterInfoRecord>lambdaQuery()
                        .eq(OuterInfoRecord::getPublicInfoQueryId, publicInfoQueryId)
                        .eq(OuterInfoRecord::getVersion, latest.getVersion()))
                .stream()
                .collect(Collectors.groupingBy(OuterInfoRecord::getConfigKey,
                        Collectors.mapping(record -> String.join(".",
                                Optional.ofNullable(record.getIndex()).map(String::valueOf).orElse(""),
                                Optional.ofNullable(record.getQueryResult()).orElse("")), Collectors.toList())));
        return PublicInfoOuterQuerySnapshot.builder()
                .queryTime(latest.getCreateTime())
                .queryResultByConfigKey(queryResultByConfigKey)
                .build();
    }

    private FileListRSP toFileListRSP(MaterialsList materialsList) {
        FileListRSP fileListRSP = new FileListRSP();
        fileService.fillFiledValue(materialsList, fileListRSP);
        return fileListRSP;
    }

    private PaymentPublicInfoMaterialSnapshot toMaterialSnapshot(MaterialsList materialsList) {
        return PaymentPublicInfoMaterialSnapshot.builder()
                .belongId(materialsList.getBelongId())
                .ossFilename(materialsList.getOssFilename())
                .filename(materialsList.getFilename())
                .materialsType(materialsList.getMaterialsType())
                .materialSubType(materialsList.getMaterialSubType())
                .build();
    }

    private PaymentPublicInfoContractSnapshot toContractSnapshot(ContractBaseInfo contractBaseInfo) {
        return PaymentPublicInfoContractSnapshot.builder()
                .id(contractBaseInfo.getId())
                .build();
    }

    private List<Long> listTenantryIds(Long contractId) {
        List<ContractTenantry> contractTenancies = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, contractId));
        if (CollUtil.isEmpty(contractTenancies)) {
            return Collections.emptyList();
        }
        return contractTenancies.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
    }

    private List<Long> listGuarantorIds(Long contractId) {
        List<ContractGuarantor> contractGuarantors = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .eq(ContractGuarantor::getContractId, contractId));
        if (CollUtil.isEmpty(contractGuarantors)) {
            return Collections.emptyList();
        }
        return contractGuarantors.stream()
                .flatMap(contractGuarantor -> JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class).stream())
                .collect(Collectors.toList());
    }

    private List<Long> listMortgageIds(Long contractId) {
        List<ContractMortgage> contractMortgages = contractMortgageService.list(Wrappers.<ContractMortgage>lambdaQuery()
                .eq(ContractMortgage::getContractId, contractId));
        if (CollUtil.isEmpty(contractMortgages)) {
            return Collections.emptyList();
        }
        return contractMortgages.stream()
                .flatMap(contractMortgage -> JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class).stream())
                .collect(Collectors.toList());
    }

    private List<Long> listPledgeIds(Long contractId) {
        List<ContractPledge> contractPledges = contractPledgeService.list(Wrappers.<ContractPledge>lambdaQuery()
                .eq(ContractPledge::getContractId, contractId));
        if (CollUtil.isEmpty(contractPledges)) {
            return Collections.emptyList();
        }
        return contractPledges.stream()
                .flatMap(contractPledge -> JSONUtil.toList(contractPledge.getPledgeIds(), Long.class).stream())
                .collect(Collectors.toList());
    }
}
