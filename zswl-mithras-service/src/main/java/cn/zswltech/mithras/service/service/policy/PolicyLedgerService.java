package cn.zswltech.mithras.service.service.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.liquidityrisk.ContractLastDate;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.policy.domain.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyDataStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.excel.exporter.PolicyLedgerListExcelExporter;
import cn.zswltech.mithras.policy.excel.model.PolicyLedgerExcelModel;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.NearPolicyEndTimeDTO;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.PaymentPolicyEndTimeDTO;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.PolicyCodeCountDTO;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.PolicyListDTO;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.PolicyListParam;
import cn.zswltech.mithras.service.mapper.dto.*;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoTmp;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentPolicyInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.json.JSONUtil.toBean;

/**
 * @create: 2023-06-16
 **/

@Slf4j
@Service
public class PolicyLedgerService {

    private static final String PAYMENT = "payment";
    private static final String POLICY = "policy";

    @Resource
    private PaymentPolicyInfoService paymentPolicyInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PolicyLedgerListExcelExporter policyLedgerListExcelExporter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PolicyInfoTmpService policyInfoTmpService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;


    public PolicyLedgerDetailRSP detail(PolicyLedgerDetailREQ req) {
        List<MaterialsList> materials;
        ProjReviewBaseInfo projReviewBaseInfo;
        PolicyLedgerDetailRSP rsp = new PolicyLedgerDetailRSP();
        if (PAYMENT.equals(req.getDataSource())) {
            PaymentPolicyInfo info = paymentPolicyInfoService.getById(req.getId());
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(info.getPaymentId());
            Long contractId;
            if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                contractId = paymentBaseInfo.getContractId();
            } else {
                contractId = info.getContractId();
            }
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
            if (ObjectUtil.isEmpty(contractBaseInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
            materials = materialsListService.getList(info.getId(), "PAYMENTPOLICY", "policy", null);
            rsp.setId(info.getId());
            rsp.setInsuranceStartDate(info.getInsuranceStartDate());
            rsp.setInsuranceEndDate(info.getInsuranceEndDate());
            rsp.setInsuranceCompany(info.getInsuranceCompany());
            rsp.setPolicyCode(info.getPolicyCode());
            rsp.setPolicyAmount(info.getPolicyAmount());
            rsp.setPolicyType(info.getPolicyType());
            rsp.setRemark(info.getRemark());
            rsp.setContractId(contractId);
            rsp.setRenewInsuranceFlag(info.getRenewInsuranceFlag());
            rsp.setIdentificationInformation(info.getIdentificationInformation());
            rsp.setPaymentId(info.getPaymentId());
        } else {
            PolicyInfo info = policyInfoMapper.selectById(req.getId());
            projReviewBaseInfo = projReviewBaseInfoMapper.selectById(info.getProjId());
            materials = materialsListService.getList(info.getId(), BusinessModuleEnum.POLICY.name(), BusinessModuleEnum.POLICY.name(), null);
            rsp.setId(info.getId());
            rsp.setInsuranceStartDate(info.getInsuranceStartDate());
            rsp.setInsuranceEndDate(info.getInsuranceEndDate());
            rsp.setInsuranceCompany(info.getInsuranceCompany());
            rsp.setPolicyCode(info.getPolicyCode());
            rsp.setPolicyAmount(info.getPolicyAmount());
            rsp.setPolicyType(info.getPolicyType());
            rsp.setRemark(info.getRemark());
            rsp.setContractId(info.getContractId());
            rsp.setRenewInsuranceFlag(info.getRenewInsuranceFlag());
            rsp.setIdentificationInformation(info.getIdentificationInformation());
            rsp.setPaymentId(info.getPaymentId());
        }
        rsp.setProjId(projReviewBaseInfo.getId());
        rsp.setProjName(projReviewBaseInfo.getProjName());
        rsp.setProjCode(projReviewBaseInfo.getProjCode());
        rsp.setClientId(projReviewBaseInfo.getClientId());
        rsp.setProjSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        rsp.setProjCosponsorUserIds(isBlank(projReviewBaseInfo.getProjCosponsorUserIds()) ? null : toBean(projReviewBaseInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
        }, true));
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        List<PolicyInfoMaterialsListRSP> policyMaterials = new ArrayList<>();
        if (CollUtil.isNotEmpty(materials)) {
            for (MaterialsList material : materials) {
                PolicyInfoMaterialsListRSP tmp = new PolicyInfoMaterialsListRSP();
                tmp.setId(material.getId());
                tmp.setName(material.getFilename());
                tmp.setCreateBy(material.getCreateBy());
                tmp.setCreateTime(material.getCreateTime());
                policyMaterials.add(tmp);
            }
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(policyMaterials.stream().map(PolicyInfoMaterialsListRSP::getCreateBy).collect(Collectors.toList()));
            policyMaterials.forEach(base -> {
                base.setCreateName(userId2Name.get(base.getCreateBy()));
            });
        }
        rsp.setMaterials(policyMaterials);
        rsp.setActualFinishDate(contractBaseInfoService.getContractExpirationDateByRent(Collections.singletonList(rsp.getContractId())).get(rsp.getContractId()));
        return rsp;
    }

    //同步至付款保单下
    @Transactional(rollbackFor = Throwable.class)
    public void syncLedgerPolicy(PolicyLedgerTmpSyncREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息为空");
        }
        List<PolicyInfoTmp> list = policyInfoTmpService.list(Wrappers.<PolicyInfoTmp>lambdaQuery().eq(PolicyInfoTmp::getContractId, req.getContractId()));
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> policyCodes = list.stream().map(PolicyInfoTmp::getPolicyCode).collect(Collectors.toList());
        List<Long> policyTmpIds = list.stream().map(PolicyInfoTmp::getId).collect(Collectors.toList());
        Map<String, Long> tmpPolicyCode2Id = list.stream().collect(Collectors.toMap(PolicyInfoTmp::getPolicyCode, PolicyInfoTmp::getId, (a, b) -> a));
        Map<String, Integer> policyCodeCountDTOMap = policyInfoMapper.countPolicyCodes(policyCodes).stream().collect(Collectors.toMap(PolicyCodeCountDTO::getPolicyCode, PolicyCodeCountDTO::getPolicyNum, Integer::sum));
        //检查保单号唯一性
        StringBuilder sb = new StringBuilder();
//        policyCodes.forEach(code -> {
//            if (policyCodeCountDTOMap.getOrDefault(code, 0) > 0) {
//                sb.append(code);
//                sb.append(",");
//            }
//        });
//        if (sb.length() > 0) {
//            sb.deleteCharAt(sb.length() - 1);
//            sb.append("保单号重复");
//            throw new MithrasException(sb.toString());
//        }
        //校验附件是否都已经必填
        Map<Long, List<MaterialsList>> policyTmpMap = materialsListService.list(BusinessModuleEnum.POLICY_TMP.name(), null, policyTmpIds).stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        list.forEach(policy -> {
            if (ObjectUtil.isEmpty(policyTmpMap.get(policy.getId()))) {
                sb.append(policy.getPolicyCode());
                sb.append(",");
            }
        });
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("未上传附件");
            throw new MithrasException(sb.toString());
        }
        List<PaymentPolicyInfo> paymentPolicyInfos = BeanUtil.copyToList(list, PaymentPolicyInfo.class);
        paymentPolicyInfoService.saveBatch(paymentPolicyInfos);
        //同步附件
        List<MaterialsList> needUpdateList = new ArrayList<>();
        paymentPolicyInfos.forEach(paymentPolicyInfo -> {
            List<MaterialsList> materialsLists = policyTmpMap.get(tmpPolicyCode2Id.get(paymentPolicyInfo.getPolicyCode()));
            if (CollectionUtil.isNotEmpty(materialsLists)) {
                materialsLists.forEach(materialsList -> {
                    materialsList.setBelongId(paymentPolicyInfo.getId());
                    materialsList.setBusinessType("PAYMENTPOLICY");
                    materialsList.setMaterialsType("POLICY");
                    needUpdateList.add(materialsList);
                });
            }
        });
        materialsListService.updateBatchById(needUpdateList);
        //删除
        policyInfoTmpService.remove(Wrappers.<PolicyInfoTmp>lambdaQuery().eq(PolicyInfoTmp::getContractId, contractBaseInfo.getId()));
    }

    public PolicyLedgerContractDetailRSP contractDetail(PolicyLedgerDetailREQ req) {
        PolicyLedgerContractDetailRSP rsp = new PolicyLedgerContractDetailRSP();
        Set<Long> sysUserIds = new HashSet<>();
        Map<Long, String> sysUserMap;
        Long contractId = null;
        if (ObjectUtil.isEmpty(req.getContractId())) {
            if (PAYMENT.equals(req.getDataSource())) {
                PaymentPolicyInfo info = paymentPolicyInfoService.getById(req.getId());
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(info.getPaymentId());
                if (ObjectUtil.isEmpty(paymentBaseInfo)) {
                    contractId = info.getContractId();
                } else {
                    contractId = paymentBaseInfo.getContractId();
                }
            } else {
                PolicyInfo info = policyInfoMapper.selectById(req.getId());
                contractId = info.getContractId();
                if (ObjectUtils.isNotEmpty(rsp.getClientId())) {
                    rsp.setClientName(id2NameService.clientId2NameSingle(rsp.getClientId()));
                }
            }
        } else {
            contractId = req.getContractId();
        }
        if (ObjectUtils.isNotEmpty(contractId)) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
            sysUserIds.add(contractBaseInfo.getProjSponsorUserId());
            if (ObjectUtils.isNotEmpty(contractBaseInfo.getProjCosponsorUserIds())) {
                List<Long> longs = toBean(contractBaseInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
                }, true);
                sysUserIds.addAll(longs);
                rsp.setProjCosponsorUserIds(longs);
            }
            sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            rsp.setId(contractBaseInfo.getId());
            rsp.setContractCode(contractBaseInfo.getContractCode());
            rsp.setActualLeaseDate(contractBaseInfoService.getFistPaymentDate(contractBaseInfo.getId()));
            rsp.setActualFinishDate(contractBaseInfoService.getContractExpirationDateByRent(Collections.singletonList(contractBaseInfo.getId())).get(contractBaseInfo.getId()));
            rsp.setProjSponsorUserId(contractBaseInfo.getProjSponsorUserId());
            rsp.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setProjName(contractBaseInfo.getProjName());
            rsp.setClientId(contractBaseInfo.getClientId());
            rsp.setClientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()));
            rsp.setProjSponsorUserId(contractBaseInfo.getProjSponsorUserId());
            rsp.setProjSponsorUserName(sysUserMap.get(contractBaseInfo.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
            }
        }
        return rsp;
    }

    public List<PolicyInfoDetailRSP> contractPolicy(PolicyLedgerContractPolicyREQ req) {
        List<PolicyInfo> policyInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery().eq(PolicyInfo::getContractId, req.getContractId()).in(ObjectUtils.isNotEmpty(req.getPolicyIds()), PolicyInfo::getId, req.getPolicyIds()));
        if (CollectionUtil.isEmpty(policyInfos)) {
            return null;
        }
        List<PolicyInfoDetailRSP> policyInfoDetailRSPS = BeanUtil.copyToList(policyInfos, PolicyInfoDetailRSP.class);
        Set<Long> clientIds = new HashSet<>();
        Set<Long> systemIds = new HashSet<>();
        policyInfoDetailRSPS.forEach(base -> {
            clientIds.add(base.getClientId());
            systemIds.add(base.getCreateBy());
        });
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(systemIds);
        policyInfoDetailRSPS.forEach(base -> {
            base.setClientName(clientId2Name.get(base.getClientId()));
            base.setCreateName(userId2Name.get(base.getCreateBy()));
        });
        return policyInfoDetailRSPS;
    }

    public PageR<PolicyLedgerListRSP> list(PolicyLedgerListREQ req) {
        PolicyListParam param = req2param(req);
        checkAuth(param);
        Page<PolicyListDTO> page = policyInfoMapper.ledgerList(new Page<>(req.getPage(), req.getPageSize()), param);
        List<PolicyLedgerListRSP> rsps = getPolicyLedgerListRSPS(page);
        //todo 逾期天数
        return PageR.of(rsps, page.getTotal(), page.getPages(), page.getCurrent(), page.getSize());
    }

    private void checkAuth(PolicyListParam param) {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        List<Long> deptids = sysUserService.canViewDeptIds();
        if (deptids == null) {
            return;
        }
        if (deptids.size() == 0) {
            param.setAuthId(userId);
        } else {
            param.setAuthDeptIds(deptids);
        }
    }

    @NotNull
    private List<PolicyLedgerListRSP> getPolicyLedgerListRSPS(Page<PolicyListDTO> page) {
        List<PolicyLedgerListRSP> rsps = new ArrayList<>();
        if (CollUtil.isNotEmpty(page.getRecords())) {
            Set<Long> sysUserIds = new HashSet<>();
            Set<Long> clientIds = new HashSet<>();
            for (PolicyListDTO record : page.getRecords()) {
                clientIds.add(record.getClientId());
                sysUserIds.add(record.getProjSponsorUserId());
                List<Long> ids = isBlank(record.getProjCosponsorUserIds()) ? null : toBean(record.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
                }, true);
                if (CollUtil.isNotEmpty(ids)) {
                    record.setProjCosponsorUserIdList(ids);
                    sysUserIds.addAll(ids);
                }
            }
            //查询过期日
            Map<Long, LocalDate> overdueDaysByPolicyIds = policyInfoService.getOverdueDaysByPolicyIds(page.getRecords());
            Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            for (PolicyListDTO record : page.getRecords()) {
                PolicyLedgerListRSP rsp = new PolicyLedgerListRSP();
                rsp.setPolicyCode(record.getPolicyCode());
                rsp.setProjId(record.getProjId());
                rsp.setProjName(record.getProjName());
                rsp.setClientId(record.getClientId());
                rsp.setClientName(clientMap.get(record.getClientId()));
                rsp.setRenewalRelationship(getPolicyLevel(record.getRenewalRelationship()));
                rsp.setInsuranceStartDate(record.getInsuranceStartDate());
                rsp.setInsuranceEndDate(record.getInsuranceEndDate());
                rsp.setInsuranceCompany(record.getInsuranceCompany());
                rsp.setRenewInsuranceFlag(record.getRenewInsuranceFlag());
                rsp.setContractCode(record.getContractCode());
                rsp.setProjSponsorUserId(record.getProjSponsorUserId());
                rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
                rsp.setProjCosponsorUserIds(record.getProjCosponsorUserIdList());
                rsp.setPolicyStatus(record.getPolicyStatus());
                rsp.setRenewalPolicyFeedbackDate(overdueDaysByPolicyIds.get(record.getId()));
                if (PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name().equals(rsp.getRenewInsuranceFlag())) {
                    rsp.setOverdueDays(Math.max(0L, overdueDaysByPolicyIds.getOrDefault(record.getId(), LocalDate.now()).toEpochDay() - rsp.getInsuranceEndDate().toEpochDay()));
                }
                if (CollUtil.isNotEmpty(record.getProjCosponsorUserIdList())) {
                    rsp.setProjCosponsorUserNames(record.getProjCosponsorUserIdList().stream().map(sysUserMap::get).collect(Collectors.toList()));
                }
                rsp.setDataSource(record.getDataSource());
                if (PAYMENT.equals(record.getDataSource())) {
                    ProcessStatus of = ProcessStatus.of(record.getApprovalStatus());
                    if (of != null) {
                        rsp.setApprovalStatus(paymentProcess2Policy(of));
                    }
                } else {
                    rsp.setApprovalStatus(record.getApprovalStatus());
                }
                rsp.setId(record.getId());
                rsp.setKey(record.getId() + record.getDataSource());
                rsp.setCreateTime(record.getCreateTime());
                rsp.setIdentificationInformation(record.getIdentificationInformation());
                rsps.add(rsp);
            }
        }
        return rsps;
    }

    private void addChildToList(Long parentId, Map<Long, List<PolicyInfo>> map, List<PolicyInfo> sortedList) {
        if (map.containsKey(parentId)) {
            for (PolicyInfo child : map.get(parentId)) {
                sortedList.add(child);
                addChildToList(child.getId(), map, sortedList);
            }
        }
    }

    private String getPolicyLevel(String level) {
        if (level == null || level.isEmpty() || level.equals("0")) {
            return "原保单";
        } else {
            return "第" + level + "次续保";
        }
    }

    private PolicyListParam req2param(PolicyLedgerListREQ req) {
        PolicyListParam policyListParam = new PolicyListParam();
        policyListParam.setInsuranceCompany(req.getInsuranceCompany());
        policyListParam.setClientId(req.getClientId());
        policyListParam.setProjName(req.getProjName());
        policyListParam.setPolicyCode(req.getPolicyCode());
        policyListParam.setInsuranceStartDateFrom(req.getInsuranceStartDateFrom());
        policyListParam.setInsuranceStartDateTo(req.getInsuranceStartDateTo());
        policyListParam.setInsuranceEndDateFrom(req.getInsuranceEndDateFrom());
        policyListParam.setInsuranceEndDateTo(req.getInsuranceEndDateTo());
        policyListParam.setContractCode(req.getContractCode());
        policyListParam.setRenewInsuranceFlag(req.getRenewInsuranceFlag());
        policyListParam.setPolicyStatus(req.getPolicyStatus());
        policyListParam.setDataStatus(PolicyDataStatusEnum.FORMAL.name());
        if (req.getExpires() != null && req.getExpires()) {
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(15);
            if (req.getInsuranceEndDateFrom() != null) {
                policyListParam.setInsuranceEndDateFrom(start.isAfter(req.getInsuranceEndDateFrom()) ? start : req.getInsuranceEndDateFrom());
            } else {
                policyListParam.setInsuranceEndDateFrom(start);
            }
            if (req.getInsuranceEndDateTo() != null) {
                policyListParam.setInsuranceEndDateTo(end.isBefore(req.getInsuranceEndDateTo()) ? end : req.getInsuranceEndDateTo());
            } else {
                policyListParam.setInsuranceEndDateTo(end);
            }
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        policyListParam.setProjSponsorUserId(req.getProjSponsorUserId());
        policyListParam.setProjCosponsorUserId(req.getProjCosponsorUserId());
        if (sysUserService.userIsSpecificJob(userId, JobEnum.assetmanagement.name(), JobEnum.chiefriskofficer.name())) {
            policyListParam.setIsAllJob(true);
        } else {
            policyListParam.setIsAllJob(false);
            policyListParam.setCurrentUserId(userId);
        }
        policyListParam.setApprovalStatus(req.getApprovalStatus());
        PolicyApprovalStatusEnum of = PolicyApprovalStatusEnum.of(req.getApprovalStatus());
        if (of != null) {
            policyListParam.setPaymentApprovalStatus(policy2PaymentProcess(of));
        }
        return policyListParam;
    }

    private String paymentProcess2Policy(ProcessStatus processStatus) {
        switch (processStatus) {
            case UNDER_APPROVAL:
                return PolicyApprovalStatusEnum.NEW_UNDER_APPROVAL.name();
            case UN_SUBMIT:
                return PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name();
            case APPROVAL_PASS:
                return PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name();
            case APPROVAL_REJECT:
                return PolicyApprovalStatusEnum.APPROVAL_REJECT.name();
            case CANCELED:
                return PolicyApprovalStatusEnum.CANCEL_NEW.name();
            default:
                return null;
        }
    }

    private String policy2PaymentProcess(PolicyApprovalStatusEnum processStatus) {
        switch (processStatus) {
            case NEW_UN_SUBMIT:
                return ProcessStatus.UN_SUBMIT.name();
            case NEW_UNDER_APPROVAL:
                return ProcessStatus.UNDER_APPROVAL.name();
            case CANCEL_NEW:
                return ProcessStatus.CANCELED.name();
            case NEW_APPROVAL_PASS:
                return ProcessStatus.APPROVAL_PASS.name();
            case CHANGING_UN_SUBMIT:
            case CHANGING_UNDER_APPROVAL:
            case CANCEL_CHANGE:
            case CHANGING_APPROVAL_PASS:
                return processStatus.name();
            case APPROVAL_REJECT:
                return ProcessStatus.APPROVAL_REJECT.name();
            default:
                return null;
        }
    }

    @SneakyThrows
    public void export(PolicyLedgerListExportREQ req, ServletOutputStream outputStream) {
        PolicyListParam param = req2param(req);
        param.setPaymentPolicyIds(req.getPaymentExportIds());
        param.setPolicyIds(req.getPolicyExportIds());
        Page<PolicyListDTO> page = policyInfoMapper.ledgerList(new Page<>(1, Integer.MAX_VALUE), param);
        //  全量数据
        Page<PolicyListDTO> pageTotal = policyInfoMapper.ledgerList(new Page<>(1, Integer.MAX_VALUE), new PolicyListParam());
        List<PolicyLedgerListRSP> rsps = getPolicyLedgerListRSPS(page);
        //全量和部分导出层级设置
        if (!ObjectUtils.isEmpty(page.getTotal()) && !ObjectUtils.isEmpty(pageTotal.getTotal()) && page.getTotal() == pageTotal.getTotal()) {
            //  全量导出时 对结果进行父子排列
            String policyKey = "policy";
            List<PolicyLedgerListRSP> policyRsp = rsps.stream().filter(e -> e.getKey().contains(policyKey)).collect(Collectors.toList());
            Map<String, List<PolicyLedgerListRSP>> policyKeyMap = policyRsp.stream().collect(Collectors.groupingBy(PolicyLedgerListRSP::getKey));

            List<Long> policyIds = policyRsp.stream().map(e -> Long.valueOf(e.getKey().substring(0, e.getKey().indexOf(policyKey)))).collect(Collectors.toList());
            //  重新组排父子关系数据
            List<PolicyInfo> policyList = policyInfoMapper.selectBatchIds(policyIds)
                    .stream().sorted(Comparator.comparing(PolicyInfo::getCreateTime)).collect(Collectors.toList());
            Map<Long, List<PolicyInfo>> map = new HashMap<>();
            List<PolicyInfo> roots = new ArrayList<>();
            for (PolicyInfo policy : policyList) {
                //父子节点重新分别存入
                if (policy.getParentId() == null) {
                    roots.add(policy);
                } else {
                    if (!map.containsKey(policy.getParentId())) {
                        map.put(policy.getParentId(), new ArrayList<>());
                    }
                    map.get(policy.getParentId()).add(policy);
                }
            }
            List<PolicyInfo> sortedList = new ArrayList<>();
            for (PolicyInfo root : roots) {
                Long rootId = root.getId();
                sortedList.add(root);
                // 对根节点进行遍历，重新组合父子关系数据
                addChildToList(rootId, map, sortedList);
            }
            List<PolicyLedgerListRSP> policyRspSored = new ArrayList<>();
            sortedList.forEach(policyInfo -> {
                List<PolicyLedgerListRSP> policyLedgerListRSPS = policyKeyMap.get(policyInfo.getId() + policyKey);
                if (!ObjectUtils.isEmpty(policyLedgerListRSPS)) {
                    policyRspSored.add(policyLedgerListRSPS.get(0));
                }
            });
            Map<String, List<PolicyLedgerListRSP>> sortedPolicyKeyMap = policyRspSored.stream().collect(Collectors.groupingBy(PolicyLedgerListRSP::getKey));
            policyKeyMap.forEach((k, v) -> {
                if (!sortedPolicyKeyMap.containsKey(k)) {
                    policyRspSored.addAll(v);
                }
            });
            List<PolicyLedgerListRSP> paymentRsp = rsps.stream().filter(e -> e.getKey().contains("payment")).collect(Collectors.toList());
            policyRspSored.addAll(paymentRsp);
            rsps = policyRspSored;
        } else {
            //  选择性导出时续保关系无法排序，滞空即可
            rsps.forEach(e -> e.setRenewalRelationship(""));
        }
        List<PolicyLedgerExcelModel> excelModelList = rsps.stream().map(o -> {
            PolicyLedgerExcelModel model = new PolicyLedgerExcelModel();
            model.setPolicyCode(o.getPolicyCode());
            model.setRenewalRelationship(o.getRenewalRelationship());
            model.setInsuranceCompany(o.getInsuranceCompany());
            model.setInsuranceStartDate(Optional.ofNullable(o.getInsuranceStartDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            model.setInsuranceEndDate(Optional.ofNullable(o.getInsuranceEndDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            PolicyRenewInsuranceEnum of = PolicyRenewInsuranceEnum.of(o.getRenewInsuranceFlag());
            if (of != null) {
                model.setRenewInsuranceFlag(of.display());
            }
            model.setClientName(o.getClientName());
            model.setContractCode(o.getContractCode());
            model.setProjName(o.getProjName());
            model.setProjSponsorUser(o.getProjSponsorUserName());
            model.setProjCosponsorUserNames(CollUtil.isNotEmpty(o.getProjCosponsorUserNames()) ? String.join(",", o.getProjCosponsorUserNames()) : "");
            model.setIdentificationInformation(o.getIdentificationInformation());
            PolicyStatusEnum of1 = PolicyStatusEnum.of(o.getPolicyStatus());
            model.setPolicyStatus(of1 == null ? null : of1.display);
            model.setCreateTime(LocalDateTimeUtil.format(o.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            model.setOverdueDays(o.getOverdueDays());
            model.setRenewalPolicyFeedbackDate(o.getRenewalPolicyFeedbackDate());
            return model;
        }).collect(Collectors.toList());
        policyLedgerListExcelExporter.exportExcel(excelModelList, outputStream);
    }


    public PageR<PolicyMaintenanceRSP> maintenanceList(PageReq req) {
        Set<Long> needAdd = new HashSet<>();
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getPolicyFlag, 1));
        Map<Long, List<PaymentBaseInfo>> cpMap = null;
        Map<Long, List<ContractBaseInfo>> rcMap = null;
        if (CollUtil.isNotEmpty(paymentBaseInfos)) {
            cpMap = paymentBaseInfos.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
            Set<Long> cIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet());
            List<ContractBaseInfo> infos = contractBaseInfoMapper.selectBatchIds(cIds);
            rcMap = infos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
            needAdd.addAll(infos.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList()));
        }
        LocalDate end = LocalDate.now().plusDays(15);
        Map<Long, LocalDate> projEndDate;
        List<NearPolicyEndTimeDTO> endTimeList = policyInfoMapper.nearPolicyEndTimeList(end);
        Map<Long, LocalDate> dateMap = null;
        Map<Long, List<PaymentPolicyEndTimeDTO>> pmap = null;
        if (CollUtil.isNotEmpty(endTimeList)) {
            dateMap = endTimeList.stream().collect(Collectors.toMap(NearPolicyEndTimeDTO::getProjId, NearPolicyEndTimeDTO::getMaxDate));
            List<Long> npIds = endTimeList.stream().map(NearPolicyEndTimeDTO::getProjId).collect(Collectors.toList());
            List<PaymentPolicyEndTimeDTO> paymentMaxTimeList = policyInfoMapper.paymentMaxTimeList(npIds);
            pmap = paymentMaxTimeList.stream().collect(Collectors.groupingBy(PaymentPolicyEndTimeDTO::getProjId));
            projEndDate = getProjEndDate(npIds);
            Set<Long> noSettleProj = policyInfoService.noSettleProj();
            Map<Long, LocalDate> finalProjEndDate = projEndDate;
            needAdd.addAll(endTimeList.stream().filter(o -> finalProjEndDate.get(o.getProjId()) != null && o.getMaxDate().isBefore(finalProjEndDate.get(o.getProjId())) && noSettleProj.contains(o.getProjId())).map(NearPolicyEndTimeDTO::getProjId).collect(Collectors.toList()));
        }
        if (needAdd.size() > 0) {
            Page<ProjReviewBaseInfo> page = projReviewBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, needAdd).eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()).and(e -> e.eq(ProjReviewBaseInfo::getProjSponsorUserId, AccountUtil.getLoginInfo().getId()).or().apply(" json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", AccountUtil.getLoginInfo().getId())));
            List<ProjReviewBaseInfo> records = page.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                List<PolicyMaintenanceRSP> rsps = new ArrayList<>();
                Set<Long> clientIds = records.stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toSet());
                Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
                List<PolicyInfo> policyInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery().eq(PolicyInfo::getAutomatic, 1).eq(PolicyInfo::getApprovalStatus, PolicyApprovalStatusEnum.NEW_UN_SUBMIT));
                Map<Long, Long> map = policyInfos.stream().collect(Collectors.toMap(PolicyInfo::getProjId, PolicyInfo::getId));
                for (ProjReviewBaseInfo record : records) {
                    PolicyMaintenanceRSP rsp = new PolicyMaintenanceRSP();
                    rsp.setAutomatic(0);
                    rsp.setProjId(record.getId());
                    if (map.containsKey(record.getId())) {
                        rsp.setPolicyId(map.get(record.getId()));
                        rsp.setAutomatic(1);
                    }
                    rsp.setProjName(record.getProjName());
                    rsp.setClientId(record.getClientId());
                    rsp.setClientName(clientMap.get(record.getClientId()));
                    if (CollUtil.isNotEmpty(rcMap)) {
                        List<ContractBaseInfo> infos = rcMap.get(record.getId());
                        if (CollUtil.isNotEmpty(infos)) {
                            for (ContractBaseInfo info : infos) {
                                List<PaymentBaseInfo> paymentBaseInfos1 = cpMap.get(info.getId());
                                if (CollUtil.isNotEmpty(paymentBaseInfos1)) {
                                    rsp.setPaymentCode(paymentBaseInfos1.get(0).getPaymentCode());
                                    break;
                                }
                            }
                        }
                    }
                    if (CollUtil.isNotEmpty(dateMap) && dateMap.containsKey(record.getId()) && CollUtil.isNotEmpty(pmap) && pmap.containsKey(record.getId())) {
                        List<PaymentPolicyEndTimeDTO> paymentPolicyEndTimeDTOS = pmap.get(record.getId());
                        for (PaymentPolicyEndTimeDTO dto : paymentPolicyEndTimeDTOS) {
                            if (dto.getMaxDate().equals(dateMap.get(record.getId()))) {
                                rsp.setPaymentCode(dto.getPaymentCode());
                                break;
                            }
                        }
                    }
                    rsps.add(rsp);
                }
                return PageR.of(rsps, page.getTotal(), page.getPages(), page.getCurrent(), page.getSize());
            }
        }
        return PageR.empty(req.getPage(), req.getPageSize());
    }

    //待维护保单列表
    public List<PolicyMaintenanceRSP> maintenanceList2(PolicyMaintenanceREQ req, List<Long> policyIds, List<Long> paymentPolicyIds) {
        LocalDate start = null;
        LocalDate end = null;
        LocalDate now = LocalDate.now();
        if(PolicyMaintenanceREQ.notOverdue.equals(req.getPolicyOverdueType())){
            start = now;
            end = now.plusDays(15);
        }else if(PolicyMaintenanceREQ.overdue.equals(req.getPolicyOverdueType())){
            end = now.plusDays(-1);
        }else{
            // 兼容原工作台
            end = now.plusDays(15);
//            throw new MithrasException("不支持的保单逾期类型");
        }
        List<PolicyInfo> records;
        if (CollUtil.isEmpty(policyIds) && CollUtil.isNotEmpty(paymentPolicyIds)) {
            records = null;
        } else {
            records = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .ne(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.YES.getCode())
                    .in(CollUtil.isNotEmpty(policyIds), PolicyInfo::getId, policyIds)
                    .like(StringUtil.isNotEmpty(req.getContractCode()), PolicyInfo::getContractCode, req.getContractCode())
                    .like(StringUtil.isNotEmpty(req.getPolicyCode()), PolicyInfo::getPolicyCode, req.getPolicyCode())
                    .like(StringUtil.isNotEmpty(req.getInsuranceCompany()), PolicyInfo::getInsuranceCompany, req.getInsuranceCompany())
                    .like(StringUtil.isNotEmpty(req.getIdentificationInformation()), PolicyInfo::getIdentificationInformation, req.getIdentificationInformation())
                    .between(BeanUtil.isNotEmpty(req.getInsuranceStartDateFrom()) && BeanUtil.isNotEmpty(req.getInsuranceStartDateTo()), PolicyInfo::getInsuranceStartDate, req.getInsuranceStartDateFrom(), req.getInsuranceStartDateTo())
                    .between(BeanUtil.isNotEmpty(req.getInsuranceEndDateFrom()) && BeanUtil.isNotEmpty(req.getInsuranceEndDateTo()), PolicyInfo::getInsuranceEndDate, req.getInsuranceEndDateFrom(), req.getInsuranceEndDateTo())
                    .ge(Objects.nonNull(start),PolicyInfo::getInsuranceEndDate,start)
                    .le(PolicyInfo::getInsuranceEndDate, end));
        }
        List<PolicyMaintenanceRSP> rsps = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(records)) {
            List<Long> contractIds = records.stream().map(PolicyInfo::getContractId).collect(Collectors.toList());
            Map<Long, LocalDate> contractExpirationDateByRent = contractBaseInfoService.getContractExpirationDateByRent(contractIds);
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getId,contractIds)
                    .ne(ContractBaseInfo::getContractStatus,ContractStatus.SETTLE.name()));
            Map<Long, ContractBaseInfo> contractBaseInfoMap = new HashMap();
            if (ObjectUtils.isNotEmpty(contractBaseInfos)) {
                contractBaseInfoMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
            }
            Map<Long, String> clientId2Name = id2NameService.clientId2Name(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
            Set<Long> sysUserIds = new HashSet<>();
            rsps = BeanUtil.copyToList(records, PolicyMaintenanceRSP.class);
            List<Long> noSettleContractId = Optional.of(contractBaseInfoMap.values().stream().map(ContractBaseInfo::getId).collect(Collectors.toList())).orElse(new ArrayList<>());
            rsps = rsps.stream().filter(f -> noSettleContractId.contains(f.getContractId())).collect(Collectors.toList());
            ContractBaseInfo orDefault;
            //<policyId, 逾期天数>
            Map<Long, LocalDate> overdueDaysByPolicyIdsMap = policyInfoService.getOverdueDaysByPolicyIds(BeanUtil.copyToList(records, PolicyListDTO.class));
            for (PolicyMaintenanceRSP base : rsps) {
                orDefault = contractBaseInfoMap.getOrDefault(base.getContractId(), new ContractBaseInfo());
                base.setClientId(orDefault.getClientId());
                sysUserIds.add(orDefault.getProjSponsorUserId());
                base.setProjSponsorUserId(orDefault.getProjSponsorUserId());
                base.setProjName(orDefault.getProjName());
                List<Long> ids = isBlank(orDefault.getProjCosponsorUserIds()) ? null : toBean(orDefault.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
                }, true);
                base.setProjCosponsorUserIds(ids);
                base.setDataSource("policy");
                if (CollUtil.isNotEmpty(ids)) {
                    sysUserIds.addAll(ids);
                }
                base.setActualFinishDate(contractExpirationDateByRent.get(base.getContractId()));
            }
            Map<Long, String> systemId2Name = id2NameService.sysUserId2Name(sysUserIds);
            rsps.forEach(rsp -> {
                rsp.setClientName(clientId2Name.get(rsp.getClientId()));
                rsp.setProjSponsorUserName(systemId2Name.get(rsp.getProjSponsorUserId()));
                if (ObjectUtil.isEmpty(rsp.getInsuranceEndDate()) && PolicyMaintenanceREQ.overdue.equals(req.getPolicyOverdueType())) {
                    rsp.setOverdueDays(0L);
                } else {
                    rsp.setOverdueDays(Math.max(0L, overdueDaysByPolicyIdsMap.getOrDefault(rsp.getId(), LocalDate.now()).toEpochDay() - rsp.getInsuranceEndDate().toEpochDay()));
                }
                if (ObjectUtils.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                    rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(systemId2Name::get).collect(Collectors.toList()));
                }
            });
        }
        //查询待维护保单
        List<PolicyListDTO> policyInfos = null;
        if (ObjectUtil.isAllEmpty(req.getPolicyCode(), req.getInsuranceCompany(), req.getInsuranceStartDateFrom(), req.getInsuranceStartDateTo(), req.getInsuranceEndDateFrom(), req.getInsuranceEndDateTo(), req.getIdentificationInformation()) && !(CollectionUtil.isNotEmpty(policyIds) && CollectionUtil.isEmpty(paymentPolicyIds))) {
            policyInfos = policyInfoMapper.listPaymentNeedRenewInsurance(paymentPolicyIds, req.getContractCode(), ContractStatus.SETTLE.name());
        }
        if (ObjectUtils.isNotEmpty(policyInfos)) {
            List<PolicyMaintenanceRSP> policyMaintenanceRSPS = BeanUtil.copyToList(policyInfos, PolicyMaintenanceRSP.class);
            Set<Long> sysUserIds = new HashSet<>();
            policyInfos.forEach(base -> {
                sysUserIds.add(base.getProjSponsorUserId());
                List<Long> ids = isBlank(base.getProjCosponsorUserIds()) ? null : toBean(base.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
                }, true);
                if (ids != null) {
                    sysUserIds.addAll(ids);
                }
            });
            Map<Long, String> clientId2Name = id2NameService.clientId2Name(policyInfos.stream().map(PolicyListDTO::getClientId).collect(Collectors.toList()));
            Map<Long, String> systemId2Name = id2NameService.sysUserId2Name(sysUserIds);
            policyMaintenanceRSPS.forEach(base -> {
                base.setClientName(clientId2Name.get(base.getClientId()));
                base.setProjSponsorUserName(systemId2Name.get(base.getProjSponsorUserId()));
                if (ObjectUtils.isNotEmpty(base.getProjCosponsorUserIds())) {
                    base.setProjCosponsorUserNames(base.getProjCosponsorUserIds().stream().map(systemId2Name::get).collect(Collectors.toList()));
                }
            });
            rsps.addAll(policyMaintenanceRSPS);
        }
        //添加合同相关额外信息
        this.policyMaintenanceRSPSuppleOtherMessage(rsps);
        return rsps;
    }

    //保单续保详情
    public List<PolicyLedgerRenewInsuranceRSP> renewInsurance(PolicyLedgerRenewInsuranceREQ req) {
        List<PolicyInfo> list = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getParentId, req.getId()));
        if (ObjectUtil.isEmpty(list)) {
            return null;
        }
        List<PolicyLedgerRenewInsuranceRSP> policyLedgerRenewInsuranceRSPS = BeanUtil.copyToList(list, PolicyLedgerRenewInsuranceRSP.class);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(policyLedgerRenewInsuranceRSPS.stream().map(PolicyLedgerRenewInsuranceRSP::getCreateBy).collect(Collectors.toList()));
        policyLedgerRenewInsuranceRSPS.forEach(e -> {
            e.setCreateByName(userId2Name.get(e.getCreateBy()));
            List<MaterialsList> materials = materialsListService.getList(e.getId(), BusinessModuleEnum.POLICY.name(), BusinessModuleEnum.POLICY.name(), null);
            List<PolicyInfoMaterialsListRSP> policyMaterials = new ArrayList<>();
            if (CollUtil.isNotEmpty(materials)) {
                Map<Long, String> userId2Name1 = id2NameService.sysUserId2Name(materials.stream().map(MaterialsList::getCreateBy).collect(Collectors.toList()));
                for (MaterialsList material : materials) {
                    PolicyInfoMaterialsListRSP tmp = new PolicyInfoMaterialsListRSP();
                    tmp.setId(material.getId());
                    tmp.setName(material.getFilename());
                    tmp.setCreateBy(material.getCreateBy());
                    tmp.setCreateTime(material.getCreateTime());
                    policyMaterials.add(tmp);
                }
                policyMaterials.forEach(base -> {
                    base.setCreateName(userId2Name1.get(base.getCreateBy()));
                });
            }
            e.setMaterials(policyMaterials);
        });
        return policyLedgerRenewInsuranceRSPS;
    }

    private void policyMaintenanceRSPSuppleOtherMessage(List<PolicyMaintenanceRSP> rsps) {
        if (ObjectUtil.isEmpty(rsps)) {
            return;
        }
        List<Long> contractIds = rsps.stream().map(PolicyMaintenanceRSP::getContractId).collect(Collectors.toList());
        //剩余未还本金
        Map<Long, Long> remainingUnpaidPrincipalByContract = collectionBaseInfoService.sumRemainingUnpaidPrincipalByContract(contractIds);
        //合同到期日
        Map<Long, LocalDate> contractExpirationDateByRent = contractBaseInfoService.getContractExpirationDateByRent(contractIds);
        rsps.forEach(rsp -> {
            rsp.setRemainingUnpaidPrincipal(remainingUnpaidPrincipalByContract.get(rsp.getContractId()));
            rsp.setContractExpirationDate(contractExpirationDateByRent.get(rsp.getContractId()));
        });
    }


    public Map<Long, LocalDate> getProjEndDate(List<Long> projId) {
        Map<Long, LocalDate> endDate = new HashMap<>();
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjReviewId, projId).in(ContractBaseInfo::getContractStatus, Arrays.asList("START_RENT", "TAKE_EFFECT", "SETTLE")));
        List<Long> cids = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<ContractLastDate> contractLastDates = contractBaseInfoMapper.ContractRentActualMaxDate(cids);
        Map<Long, LocalDate> maxDateMap = contractLastDates.stream().collect(Collectors.toMap(ContractLastDate::getContractId, ContractLastDate::getCashFlowDate));
        for (ContractBaseInfo o : contractBaseInfos) {
            LocalDate newest = maxDateMap.get(o.getId());
            if (endDate.containsKey(o.getProjReviewId()) && endDate.get(o.getProjReviewId()) != null) {
                LocalDate old = endDate.get(o.getProjReviewId());
                LocalDate max = old.isAfter(newest) ? old : newest;
                endDate.put(o.getProjReviewId(), max);
            } else {
                endDate.put(o.getProjReviewId(), newest);
            }
        }
        return endDate;
    }
}
