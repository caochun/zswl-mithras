package cn.zswltech.mithras.application.orchestration.client.authority;
import cn.zswltech.mithras.customer.application.client.NewCorpRelatedEnterpriseService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpBankAccountService;
import cn.zswltech.mithras.customer.application.client.NewCorpShareHolderInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpBondInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpContactInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpCommerceInfoService;
import cn.zswltech.mithras.customer.application.client.ClientCreateRecordService;
import cn.zswltech.mithras.customer.application.client.ClientUserRefService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientInfoEnum;
import cn.zswltech.mithras.customer.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.*;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.application.orchestration.client.*;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientDataCopyHandlerFactory;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ContractUtil
 * @Description
 * @Author jackerhe
 * @Date 2022/8/15 11:24 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class ClientAuthorityUtil {

    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private NewCorpCommerceInfoMapper newCorpCommerceInfoMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private NewCorpAddressInfoMapper newCorpAddressInfoMapper;
    @Resource
    private NewCorpBankAccountMapper newCorpBankAccountMapper;
    @Resource
    private NewCorpBondInfoMapper newCorpBondInfoMapper;
    @Resource
    private NewCorpContactInfoMapper newCorpContactInfoMapper;
    @Resource
    private NewCorpRelatedEnterpriseMapper newCorpRelatedEnterpriseMapper;
    @Resource
    private NewCorpShareholderInfoMapper newCorpShareholderInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientUserRefService clientUserRefService;
    @Resource
    private MaterialsListMapper materialsListMapper;

    public boolean searchNewInfo(Long clientId, String moduleName, Long startUserId) {
        Client client = clientMapper.selectById(clientId);
        if (client == null) {
            throw new MithrasException("客户已不存在");
        }
        //判断是快照详情还是客户详情
        boolean searchNewInfo = false;
        if (startUserId == null) {
            startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                    .map(AccountVO::getId)
                    .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        }
        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, client.getId())
                .eq(ClientAuthority::getUserId, startUserId));
        if (isNewClient(client)) {
            searchNewInfo = true;
            return searchNewInfo;
        }
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                && clientAuthority != null
                && ClientLevelEnum.APPLY.getLevel() == clientAuthority.getLevel()) {
            return searchNewInfo;
        }
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                && clientAuthority != null
                && ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
            searchNewInfo = true;
            return searchNewInfo;
        }
        //经理A释放客户1，经理A看客户1信息所有自己老表模块，经理B看客户1，3模块新表自己版本，没有则空，其余模块经理A的老表模块，
        //所有模块经理B编辑保存过，都展示自己的最新版本
        if (isReleasedClient(client)) {
            if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_COMMERCE_INFO.name())) {
                NewCorpCommerceInfo newCorpCommerceInfo = newCorpCommerceInfoMapper.selectOne(Wrappers.<NewCorpCommerceInfo>lambdaQuery()
                                .eq(NewCorpCommerceInfo::getClientId, client.getId())
                                .eq(NewCorpCommerceInfo::getUserId, startUserId));
                if (newCorpCommerceInfo != null) {
                    searchNewInfo = true;
                } else if (newCorpCommerceInfo == null
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0){
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_ADDRESS_INFO.name())) {
                List<NewCorpAddressInfo> newCorpAddressInfoList = newCorpAddressInfoMapper.selectList(Wrappers.<NewCorpAddressInfo>lambdaQuery()
                        .eq(NewCorpAddressInfo::getClientId, client.getId())
                        .eq(NewCorpAddressInfo::getUserId, startUserId));
                if (!newCorpAddressInfoList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpAddressInfoList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0){
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_BANK_ACCOUNT.name())) {
                List<NewCorpBankAccount> newCorpBankAccountList = newCorpBankAccountMapper.selectList(Wrappers.<NewCorpBankAccount>lambdaQuery()
                        .eq(NewCorpBankAccount::getClientId, client.getId())
                        .eq(NewCorpBankAccount::getUserId, startUserId));
                searchNewInfo = true;
                if (!newCorpBankAccountList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpBankAccountList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0) {
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_BOND_INFO.name())) {
                List<NewCorpBondInfo> newCorpBondInfoList = newCorpBondInfoMapper.selectList(Wrappers.<NewCorpBondInfo>lambdaQuery()
                        .eq(NewCorpBondInfo::getClientId, client.getId())
                        .eq(NewCorpBondInfo::getUserId, startUserId));
                if (!newCorpBondInfoList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpBondInfoList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0){
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_CONTACT_INFO.name())) {
                List<NewCorpContactInfo> newCorpContactInfoList = newCorpContactInfoMapper.selectList(Wrappers.<NewCorpContactInfo>lambdaQuery()
                        .eq(NewCorpContactInfo::getClientId, client.getId())
                        .eq(NewCorpContactInfo::getUserId, startUserId));
                searchNewInfo = true;
                if (!newCorpContactInfoList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpContactInfoList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0) {
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_RELATED_ENTERPRISE.name())) {
                List<NewCorpRelatedEnterprise> newCorpRelatedEnterpriseList = newCorpRelatedEnterpriseMapper.selectList(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
                        .eq(NewCorpRelatedEnterprise::getClientId, client.getId())
                        .eq(NewCorpRelatedEnterprise::getUserId, startUserId));
                if (!newCorpRelatedEnterpriseList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpRelatedEnterpriseList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0){
                    searchNewInfo = false;
                }
                return searchNewInfo;
            } else if (moduleName.equalsIgnoreCase(ClientInfoEnum.CORP_SHAREHOLDER_INFO.name())) {
                List<NewCorpShareholderInfo> newCorpShareholderInfoList = newCorpShareholderInfoMapper.selectList(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
                        .eq(NewCorpShareholderInfo::getClientId, client.getId())
                        .eq(NewCorpShareholderInfo::getUserId, startUserId));
                if (!newCorpShareholderInfoList.isEmpty()) {
                    searchNewInfo = true;
                } else if (newCorpShareholderInfoList.isEmpty()
                        && clientAuthority != null && clientAuthority.getLevel() != null && clientAuthority.getLevel() > 0){
                    searchNewInfo = false;
                }
                return searchNewInfo;
            }
        }
        return searchNewInfo;
    }

    public boolean isIntraGroupCollaboration(Long clientId) {
        // 判断是否公海客户
        boolean isIntraGroup = false;
        List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(clientId);
        if (corpCommerceInfoList == null || corpCommerceInfoList.isEmpty()) {
            return isIntraGroup;
        }
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
        if (Objects.equals(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())) {
            isIntraGroup = true;
        }
        return isIntraGroup;
    }

    public boolean isReleasedClient(Client client) {
        boolean isReleased = false;
        if (ClientStatus.NEW.name().equalsIgnoreCase(client.getClientStatus())
                && Objects.equals(client.getIsReleased(), YesOrNoNumberEnum.YES.getCode())) {
            isReleased = true;
        }
        return isReleased;
    }

    public boolean isNewClient(Client client) {
        boolean isNew = false;
        if (ClientStatus.NEW.name().equalsIgnoreCase(client.getClientStatus())
                && Objects.equals(client.getIsReleased(), YesOrNoNumberEnum.NO.getCode())) {
            isNew = true;
        }
        return isNew;
    }

    public boolean isEffectClient(Client client) {
        boolean isEffect = false;
        if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())) {
            isEffect = true;
        }
        return isEffect;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void copyFromNewToOld(ClientCopyInfoBO clientCopyInfoBO) {
        Client client = clientMapper.selectById(clientCopyInfoBO.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException("客户信息不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            // 自然人无需处理
            return;
        }
        List<InfoModule> moduleList = clientCopyInfoBO.getModuleList();
        // 如果没有指定模块，默认所有模块都抄数据
        if (CollectionUtil.isEmpty(moduleList)) {
            moduleList = this.listAllModule();
        }
        moduleList.forEach(module -> ClientDataCopyHandlerFactory.getInstance(module).copyFromNewToOld(clientCopyInfoBO));
        // 更新客户最近更新人
        Client updateClient = new Client();
        updateClient.setId(clientCopyInfoBO.getClientId());
        updateClient.setLatestUserId(clientCopyInfoBO.getCurrentUserId());
        clientMapper.updateById(updateClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void copyFromOldToNew(ClientCopyInfoBO clientCopyInfoBO) {
        Client client = clientMapper.selectById(clientCopyInfoBO.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException("客户信息不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            // 自然人无需处理
            return;
        }
        List<InfoModule> moduleList = clientCopyInfoBO.getModuleList();
        // 如果没有指定模块，默认所有模块都抄数据
        if (CollectionUtil.isEmpty(moduleList)) {
            moduleList = this.listAllModule();
        }
        moduleList.forEach(module -> ClientDataCopyHandlerFactory.getInstance(module).copyFromOldToNew(clientCopyInfoBO));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void copyFromNewToNew(ClientCopyInfoBO clientCopyInfoBO) {
        Client client = clientMapper.selectById(clientCopyInfoBO.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException("客户信息不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            // 自然人无需处理
            return;
        }
        List<InfoModule> moduleList = clientCopyInfoBO.getModuleList();
        // 如果没有指定模块，默认所有模块都抄数据
        if (CollectionUtil.isEmpty(moduleList)) {
            moduleList = this.listAllModule();
        }
        moduleList.forEach(module -> ClientDataCopyHandlerFactory.getInstance(module).copyFromNewToNew(clientCopyInfoBO));
    }

    private List<InfoModule> listAllModule() {
        return ListUtil.of(
                InfoModule.CORP_COMMERCE,
                InfoModule.CORP_ADDRESS,
                InfoModule.CORP_CONTACT,
                InfoModule.CORP_SHAREHOLDER,
                InfoModule.CORP_RELATED_ENTERPRISE,
                InfoModule.CORP_BANK_ACCOUNT
        );
    }

    public Long getManageUserId(Long clientId) {
        Long userId = null;
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, clientId)
                .eq(ClientAuthority::getDeleted, 0));
        if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
            for (ClientAuthority clientAuthority : clientAuthorityList) {
                if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
                    return clientAuthority.getUserId();
                }
            }
        }
        return userId;
    }

    // 确定非审批流中的客户数据应该展示哪个用户的
    public Long ensureNoProcessViewWhichUserData(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            throw new MithrasException("客户主数据不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return null;
        }
        // 判断是否公海客户
        if (this.isIntraGroupCollaboration(clientId)) {
            return client.getLatestUserId();
        }
        // 非公海需要进一步处理
        // 过滤出业务部门的岗位
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            // 按照中后台部门进行处理，生效的取管护权人，未生效的取最近一次更新人
            if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                return Optional.ofNullable(clientAuthority).map(ClientAuthority::getUserId).orElse(null);
            } else {
                return client.getLatestUserId();
            }
        } else {
            // 分管领导
            if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
                if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                    ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                    return Optional.ofNullable(clientAuthority).map(ClientAuthority::getUserId).orElse(null);
                } else {
                    List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                    ClientUserRef clientUserRef = clientUserRefService.findLastOperateByDeptIds(clientId, deptIds);
                    return Optional.ofNullable(clientUserRef).map(ClientUserRef::getUserId).orElse(null);
                }
            }
            // 部门负责人
            if (CollectionUtil.isNotEmpty(businessheadList)) {
                if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                    ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                    return Optional.ofNullable(clientAuthority).map(ClientAuthority::getUserId).orElse(null);
                } else {
                    List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                    ClientUserRef clientUserRef = clientUserRefService.findLastOperateByDeptIds(clientId, deptIds);
                    return Optional.ofNullable(clientUserRef).map(ClientUserRef::getUserId).orElse(null);
                }
            }
            // 项目经理
            if (CollectionUtil.isNotEmpty(projmanagerList)) {
                // 如果非公海且生效，展示管护权人数据（结合另一个只能看工商信息的逻辑能够实现看生效工商信息）
                if (this.isEffectClient(client)) {
                    ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                    if (Objects.isNull(clientAuthority)) {
                        // 说明是由公海变成非公海的，需要特殊处理，看最近一个更新人的数据
                        return client.getLatestUserId();
                    } else {
                        return clientAuthority.getUserId();
                    }
                }
                // 其他的默认看自己
                int count = clientUserRefService.countByClientUser(clientId, currentUserId);
                if (count > 0) {
                    return currentUserId;
                }
            }
        }
        return null;
    }

    public void clearNewData(Long clientId, Long userId) {
        SpringUtil.getBean(ClientCreateRecordService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(ClientUserRefService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpCommerceInfoService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpAddressInfoService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpContactInfoService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpBondInfoService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpShareHolderInfoService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpBankAccountService.class).removeByClientUser(clientId, userId);
        SpringUtil.getBean(NewCorpRelatedEnterpriseService.class).removeByClientUser(clientId, userId);
    }

    public void copyMaterialListFromOldToNew(ClientCopyInfoBO clientCopyInfoBO) {
        // 如果移交前后主办没有变化则不需要处理
        if (Objects.equals(clientCopyInfoBO.getDbUserId(), clientCopyInfoBO.getCurrentUserId())) {
            log.info("{}-移交前后主办一致，文件无需拷贝", clientCopyInfoBO.getClientId());
            return;
        }
        List<MaterialsList> newMaterialsLists = materialsListMapper
                .selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBelongId, clientCopyInfoBO.getClientId())
                        .eq(MaterialsList::getCreateBy, clientCopyInfoBO.getCurrentUserId())
                        .eq(MaterialsList::getBusinessType, "CLIENT")
                        .in(MaterialsList::getMaterialsType,
                                ListUtil.toList("BASIC_INFORMATION", "LEASE_APPLICATION", "CREDIT_LETTER", "FINANCIAL_INFORMATION", "BUSINESS_INFORMATION", "OTHERS"))
                        .orderByDesc(MaterialsList::getUpdateTime));
        if (newMaterialsLists != null && !newMaterialsLists.isEmpty()) {
            for (MaterialsList materialsList : newMaterialsLists) {
                materialsListMapper.deleteById(materialsList.getId());
            }
        }
        List<MaterialsList> oldMaterialsLists = materialsListMapper
                .selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBelongId, clientCopyInfoBO.getClientId())
                        .eq(MaterialsList::getCreateBy, clientCopyInfoBO.getDbUserId())
                        .eq(MaterialsList::getBusinessType, "CLIENT")
                        .in(MaterialsList::getMaterialsType,
                                ListUtil.toList("BASIC_INFORMATION", "LEASE_APPLICATION", "CREDIT_LETTER", "FINANCIAL_INFORMATION", "BUSINESS_INFORMATION", "OTHERS"))
                        .orderByDesc(MaterialsList::getUpdateTime));
        if (oldMaterialsLists != null && !oldMaterialsLists.isEmpty()) {
            for (MaterialsList oldMaterialsList : oldMaterialsLists) {
                MaterialsList newMaterialsList = BeanUtil.copyProperties(oldMaterialsList, MaterialsList.class);
                newMaterialsList.setCreateBy(clientCopyInfoBO.getCurrentUserId());
                newMaterialsList.setUpdateBy(clientCopyInfoBO.getCurrentUserId());
                materialsListMapper.insert(newMaterialsList);
            }
        }
    }

    public String ignoreBracket(String str){
        if(StringUtils.isNotBlank(str)){
            String str1 = str.replaceAll("（", "");
            return str1.replaceAll("）", "");
        }
        return "";
    }
}
