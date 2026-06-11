package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.collection.application.job.CollectionMailJobSupportPort;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.contract.versioning.service.ContractLeasePriceLibService;
import cn.zswltech.mithras.creditreport.model.CrRepayPlan;
import cn.zswltech.mithras.creditreport.service.CreditRepayPlanService;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpContactInfo;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpContactInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.email.CollectionRentEmailHandler;
import cn.zswltech.mithras.application.orchestration.email.RentExpireEmailHandler;
import cn.zswltech.mithras.foundation.util.StringUtils;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CollectionMailJobSupportPortAdapter implements CollectionMailJobSupportPort {

    @Resource
    private CreditRepayPlanService creditRepayPlanService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpContactInfoService corpContactInfoService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private ContractLeasePriceLibService leasePriceLibService;
    @Resource
    private CollectionRentEmailHandler collectionRentEmailHandler;
    @Resource
    private RentExpireEmailHandler rentExpireEmailHandler;

    @Override
    public String getGracePeriod(Long contractId, Long paymentId, Integer phase) {
        CrRepayPlan creditRepayPlan = creditRepayPlanService.getOne(Wrappers.<CrRepayPlan>lambdaQuery()
                .eq(CrRepayPlan::getContractId, contractId)
                .eq(CrRepayPlan::getPaymentId, paymentId)
                .eq(CrRepayPlan::getPhase, phase));
        if (creditRepayPlan == null || StringUtils.isBlank(creditRepayPlan.getGracePeriod())) {
            return "0";
        }
        return creditRepayPlan.getGracePeriod();
    }

    @Override
    public ContractBaseInfo getContractById(Long contractId) {
        return contractBaseInfoService.getById(contractId);
    }

    @Override
    public Set<Long> getUserIdsByRole(String roleCode) {
        return sysUserService.getUserIdsByRole(roleCode);
    }

    @Override
    public Set<String> getUserEmailSet(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new HashSet<>();
        }
        List<UserVO> userVOList = userServiceAPI.getUserInfoByIds(new ArrayList<>(userIds));
        if (CollectionUtils.isEmpty(userVOList)) {
            return new HashSet<>();
        }
        return userVOList.stream()
                .filter(userVO -> Objects.equals(0, userVO.getStatus()))
                .filter(userVO -> StringUtils.isNotBlank(userVO.getEmail()))
                .map(UserVO::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public Client getClientById(Long clientId) {
        return clientService.getById(clientId);
    }

    @Override
    public List<CorpContactInfo> listCorpContactInfo(Long clientId) {
        return corpContactInfoService.list(Wrappers.<CorpContactInfo>lambdaQuery()
                .eq(CorpContactInfo::getClientId, clientId)
                .orderByDesc(CorpContactInfo::getMain)
                .orderByDesc(CorpContactInfo::getId));
    }

    @Override
    public String getLatestContractVersion(Long contractId) {
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, contractId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        return ObjectUtil.isNull(commonVersion) ? null : commonVersion.getVersion();
    }

    @Override
    public String getReportedLesseeNames(Long contractId, String version) {
        ContractTenantryLib mainLessee = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantryLib::getVersion, version)
                .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));
        if (mainLessee == null) {
            return null;
        }
        StringBuilder names = new StringBuilder(mainLessee.getLesseeName());
        List<ContractTenantryLib> jointLessees = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantryLib::getVersion, version)
                .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));
        for (ContractTenantryLib jointLessee : jointLessees) {
            names.append("、").append(jointLessee.getLesseeName());
        }
        return names.toString();
    }

    @Override
    public boolean isWorkDay(LocalDate date) {
        return baseDataSpecialDateService.isWorkDay(date);
    }

    @Override
    public int countWorkdayNumber(LocalDate start, LocalDate end) {
        return DateUtil.countWorkdayNumber(start, end);
    }

    @Override
    public Long getNominalPrice(Long contractId, String version) {
        ContractLeasePrice contractLeasePrice = leasePriceLibService.getByVersion(contractId, version);
        return contractLeasePrice == null ? null : contractLeasePrice.getNominalPrice();
    }

    @Override
    public void sendCollectionRentEmail(Set<String> receivers, Set<String> carbonCopies, RentCollectionBaseInfo data) {
        collectionRentEmailHandler.sendEmail(receivers, carbonCopies, new HashSet<>(), data);
    }

    @Override
    public void sendRentExpireEmail(Set<String> receivers, Set<String> carbonCopies, RentCollectionBaseInfo data) {
        rentExpireEmailHandler.sendEmail(receivers, carbonCopies, new HashSet<>(), data);
    }
}
