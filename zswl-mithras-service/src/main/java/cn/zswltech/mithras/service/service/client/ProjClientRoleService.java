package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.client.ProjClientRoleMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.client.ProjClientRole;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.lib.contract.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.archive.service.ContractTenantryLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/7/25 09:50
 */
@Service
public class ProjClientRoleService extends ServiceImpl<ProjClientRoleMapper, ProjClientRole> {

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    /**
     * 评审提交 创建或变更
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projReviewSubmit(Long mainId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(mainId);
        List<ProjClientRole> newClientRoles = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getLesseeInfo())) {
            List<ClientInfo> lesseeInfos = JSONArray.parseArray(projReviewBaseInfo.getLesseeInfo()).toJavaList(ClientInfo.class);
            for (ClientInfo lesseeInfo : lesseeInfos) {
                ProjClientRole role = new ProjClientRole();
                role.setClientId(lesseeInfo.getClientId());
                role.setMainId(mainId);
                role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                role.setRole("LESSEE");
                newClientRoles.add(role);
            }
        }
        if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getGuaranteeInfo())) {
            List<ClientInfo> guarantorInfos = JSONArray.parseArray(projReviewBaseInfo.getGuaranteeInfo()).toJavaList(ClientInfo.class);
            for (ClientInfo guarantorInfo : guarantorInfos) {
                if (ClientType.CORPORATION.name().equals(guarantorInfo.getClientType())) {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(guarantorInfo.getClientId());
                    role.setMainId(mainId);
                    role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                    role.setRole("GUARANTOR");
                    newClientRoles.add(role);
                }
            }
        }
        remove(Wrappers.<ProjClientRole>lambdaQuery().eq(ProjClientRole::getMainId, mainId)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.PROJ_REVIEW.name()));
        saveBatch(newClientRoles);
    }

    /**
     * 评审关闭 关闭或者关闭新建流程
     *
     * @param mainIds
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projReviewFinish(Collection<Long> mainIds) {
        remove(Wrappers.<ProjClientRole>lambdaQuery().in(ProjClientRole::getMainId, mainIds)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.PROJ_REVIEW.name()));
    }


    /**
     * 定价关闭 关闭或者关闭新建流程
     *
     * @param mainIds
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projPricingFinish(Collection<Long> mainIds) {
        remove(Wrappers.<ProjClientRole>lambdaQuery().in(ProjClientRole::getMainId, mainIds)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.PROJ_PRICING.name()));
    }

    /**
     * 变更流程不通过时回滚
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projReviewRollBackLastVersion(Long mainId) {
        String newstEffectVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getModule, BusinessModuleEnum.PROJ_REVIEW.name())
                .eq(CommonVersion::getMainId, mainId).orderByDesc(CommonVersion::getVersion)
                .last("limit 1")).getVersion();

        ProjReviewBaseInfoLib projReviewBaseInfoLib = projReviewBaseInfoLibService.getOne(
                Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
                        .eq(ProjReviewBaseInfoLib::getVersion, newstEffectVersion)
                        .eq(ProjReviewBaseInfoLib::getOriginId, mainId));

        List<ProjClientRole> newClientRoles = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(projReviewBaseInfoLib.getLesseeInfo())) {
            List<ClientInfo> lesseeInfos = JSONArray.parseArray(projReviewBaseInfoLib.getLesseeInfo()).toJavaList(ClientInfo.class);
            for (ClientInfo lesseeInfo : lesseeInfos) {
                ProjClientRole role = new ProjClientRole();
                role.setClientId(lesseeInfo.getClientId());
                role.setMainId(mainId);
                role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                role.setRole("LESSEE");
                newClientRoles.add(role);
            }
        }
        if (ObjectUtil.isNotEmpty(projReviewBaseInfoLib.getGuaranteeInfo())) {
            List<ClientInfo> guarantorInfos = JSONArray.parseArray(projReviewBaseInfoLib.getGuaranteeInfo()).toJavaList(ClientInfo.class);
            for (ClientInfo guarantorInfo : guarantorInfos) {
                if (guarantorInfo.getClientType().equals(ClientType.CORPORATION.name())) {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(guarantorInfo.getClientId());
                    role.setMainId(mainId);
                    role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                    role.setRole("GUARANTOR");
                    newClientRoles.add(role);
                }
            }
        }
        remove(Wrappers.<ProjClientRole>lambdaQuery().eq(ProjClientRole::getMainId, mainId)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.PROJ_REVIEW.name()));
        saveBatch(newClientRoles);
    }

    /**
     * 闯将合同提交 或 其他变更提交时调用
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projContractSubmit(Long mainId) {
        List<ProjClientRole> newClientRoles = new ArrayList<>();
        contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                        .eq(ContractGuarantor::getContractId, mainId))
                .forEach(contractGuarantor -> {
                    if (ClientType.CORPORATION.name().equals(contractGuarantor.getGuarantorType())) {
                        List<Long> longs = JSON.parseObject(contractGuarantor.getGuarantorIds(), new TypeReference<List<Long>>() {
                        });
                        for (Long aLong : longs) {
                            ProjClientRole role = new ProjClientRole();
                            role.setClientId(aLong);
                            role.setMainId(mainId);
                            role.setModuleType(BusinessModuleEnum.CONTRACT.name());
                            role.setRole("GUARANTOR");
                            newClientRoles.add(role);
                        }
                    }
                });
        contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .eq(ContractTenantry::getContractId, mainId))
                .forEach(contractTenantry -> {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(contractTenantry.getLesseeId());
                    role.setMainId(mainId);
                    role.setModuleType(BusinessModuleEnum.CONTRACT.name());
                    role.setRole("LESSEE");
                    newClientRoles.add(role);
                });
        remove(Wrappers.<ProjClientRole>lambdaQuery().eq(ProjClientRole::getMainId, mainId)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()));
        saveBatch(newClientRoles);
    }

    /**
     * 取消新建流程时调用
     *
     * @param mainIds
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projContractFinish(Collection<Long> mainIds) {
        remove(Wrappers.<ProjClientRole>lambdaQuery().in(ProjClientRole::getMainId, mainIds)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()));
    }

    /**
     * 合同结清，清除合同相关的客户角色，以及合同的评审相关角色
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void contractSettle(Long mainId) {

        ContractBaseInfo contract = contractBaseInfoService.getById(mainId);
        remove(Wrappers.<ProjClientRole>lambdaQuery().eq(ProjClientRole::getMainId, mainId)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()));

        if (ObjectUtil.isNotEmpty(contract.getProjReviewId())) {
            remove(Wrappers.<ProjClientRole>lambdaQuery()
                    .eq(ProjClientRole::getMainId, contract.getProjReviewId())
                    .eq(ProjClientRole::getModuleType, BusinessModuleEnum.PROJ_REVIEW.name()));
        }
    }

    /**
     * 合同变更审批不通过，回滚客户角色
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void projContractRollBack(Long mainId) {
        CommonVersion lastVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CommonVersion::getVersion).last("limit 1"));
        List<ProjClientRole> newClientRoles = new ArrayList<>();
        contractGuarantorLibService.list(Wrappers.<ContractGuarantorLib>lambdaQuery()
                        .eq(ContractGuarantor::getContractId, mainId)
                        .eq(ContractGuarantorLib::getVersion, lastVersion.getVersion()))
                .forEach(contractGuarantor -> {
                    if (ClientType.CORPORATION.name().equals(contractGuarantor.getGuarantorType())) {
                        List<Long> longs = JSON.parseObject(contractGuarantor.getGuarantorIds(), new TypeReference<List<Long>>() {
                        });
                        for (Long aLong : longs) {
                            ProjClientRole role = new ProjClientRole();
                            role.setClientId(aLong);
                            role.setMainId(mainId);
                            role.setModuleType(BusinessModuleEnum.CONTRACT.name());
                            role.setRole("GUARANTOR");
                            newClientRoles.add(role);
                        }
                    }
                });
        contractTenantryLibService.list(Wrappers.<ContractTenantryLib>lambdaQuery()
                        .eq(ContractTenantryLib::getVersion, lastVersion.getVersion())
                        .eq(ContractTenantry::getContractId, mainId))
                .forEach(contractTenantry -> {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(contractTenantry.getLesseeId());
                    role.setMainId(mainId);
                    role.setModuleType(BusinessModuleEnum.CONTRACT.name());
                    role.setRole("LESSEE");
                    newClientRoles.add(role);
                });
        remove(Wrappers.<ProjClientRole>lambdaQuery().eq(ProjClientRole::getMainId, mainId)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()));
        saveBatch(newClientRoles);
    }


    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("projClientRoleInitJobHandler")
    public void initData() {
        List<ProjClientRole> newClientRoles = new ArrayList<>();
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.SETTLE.name())
                        .notIn(ContractBaseInfo::getContractProcessStatus, ContractProcessStatusEnum.NEW_UNCOMMIT.name(), ContractProcessStatusEnum.NEW_CANCEL.name()));
        Set<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());


        contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .in(ContractGuarantor::getContractId, contractIds)).forEach(contractGuarantorLib -> {
            if (ClientType.CORPORATION.name().equals(contractGuarantorLib.getGuarantorType())) {
                List<Long> guarantorIds = JSON.parseObject(contractGuarantorLib.getGuarantorIds(), new TypeReference<List<Long>>() {
                });
                for (Long guarantorId : guarantorIds) {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(guarantorId);
                    role.setMainId(contractGuarantorLib.getContractId());
                    role.setModuleType(BusinessModuleEnum.CONTRACT.name());
                    role.setRole("GUARANTOR");
                    newClientRoles.add(role);
                }
            }
        });
        contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .in(ContractTenantry::getContractId, contractIds)).forEach(contractTenantryLib -> {
            ProjClientRole role = new ProjClientRole();
            role.setClientId(contractTenantryLib.getLesseeId());
            role.setMainId(contractTenantryLib.getContractId());
            role.setModuleType(BusinessModuleEnum.CONTRACT.name());
            role.setRole("LESSEE");
            newClientRoles.add(role);
        });

        Set<Long> settledContractsReviewIds = contractBaseInfoService.list(
                        Wrappers.<ContractBaseInfo>lambdaQuery()
                                .eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name())).stream()
                .map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());


        List<ProjReviewBaseInfo> projReviewBaseInfoLibs = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ObjectUtil.isNotEmpty(settledContractsReviewIds), ProjReviewBaseInfo::getId, settledContractsReviewIds)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.NEW.name(), RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .notIn(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_REJECT.name(), ProjProcessState.NEW_UN_SUBMIT.name(), ProjProcessState.CANCEL_NEW.name()));
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoLibs) {
            if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getLesseeInfo())) {
                List<ClientInfo> lesseeInfos = JSONArray.parseArray(projReviewBaseInfo.getLesseeInfo()).toJavaList(ClientInfo.class);
                for (ClientInfo lesseeInfo : lesseeInfos) {
                    ProjClientRole role = new ProjClientRole();
                    role.setClientId(lesseeInfo.getClientId());
                    role.setMainId(projReviewBaseInfo.getMainId());
                    role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                    role.setRole("LESSEE");
                    newClientRoles.add(role);
                }
            }
            if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getGuaranteeInfo())) {
                List<ClientInfo> guarantorInfos = JSONArray.parseArray(projReviewBaseInfo.getGuaranteeInfo()).toJavaList(ClientInfo.class);
                for (ClientInfo guarantorInfo : guarantorInfos) {
                    if (guarantorInfo.getClientType().equals(ClientType.CORPORATION.name())) {
                        ProjClientRole role = new ProjClientRole();
                        role.setClientId(guarantorInfo.getClientId());
                        role.setMainId(projReviewBaseInfo.getMainId());
                        role.setModuleType(BusinessModuleEnum.PROJ_REVIEW.name());
                        role.setRole("GUARANTOR");
                        newClientRoles.add(role);
                    }
                }
            }
        }
        remove(Wrappers.<ProjClientRole>lambdaQuery().ge(ProjClientRole::getId, 1));
        saveBatch(newClientRoles);
    }

}
