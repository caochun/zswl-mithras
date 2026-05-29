package cn.zswltech.mithras.service.service.Listener.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.groupcreditestablish.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/9/18
 * @description
 */
@Slf4j
@Component
public class ClientViewAuthorityEventListener implements ApplicationListener<ClientViewAuthorityEvent> {
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public void onApplicationEvent(@NotNull ClientViewAuthorityEvent event) {
        log.info("客户查看权-收到客户查看权变更通知[{}]", JSONUtil.toJsonStr(event.getInfo()));
        ClientViewAuthorityEvent.ClientViewAuthorityInfo info = event.getInfo();
        if (Objects.isNull(info) || Objects.isNull(info.getBusinessModule()) || Objects.isNull(info.getBizId())) {
            return;
        }
        try {
            switch (info.getBusinessModule()) {
                case GROUP_CREDIT_ESTABLISH: {
                    this.doGroupCreditEstablish(info);
                    break;
                }
                case GROUP_CREDIT_REVIEW: {
                    this.doGroupCreditReview(info);
                    break;
                }
                case PROJ_ESTABLISH: {
                    this.doProjEstablish(info);
                    break;
                }
                case PROJ_REVIEW: {
                    this.doProjReview(info);
                    break;
                }
                case PROJ_PRICING: {
                    this.doProjPricing(info);
                    break;
                }
                case CONTRACT: {
                    this.doContract(info);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("客户查看权-处理客户查看权变更通知发生异常[{}]", JSONUtil.toJsonStr(info), e);
        }
    }

    private void doGroupCreditEstablish(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = SpringUtil.getBean(GroupCreditEstablishBaseInfoMapper.class).selectById(info.getBizId());
        if (Objects.isNull(groupCreditEstablishBaseInfo)) {
            log.error("客户查看权-<集团授信立项>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(groupCreditEstablishBaseInfo.getGroupCreditEstablishStatus(), RecordStatus.CLOSED.name()) || Objects.equals(groupCreditEstablishBaseInfo.getGroupCreditEstablishStatus(), RecordStatus.EXPIRE.name())) {
            this.doClientAuthority(info, null, null, groupCreditEstablishBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(groupCreditEstablishBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(groupCreditEstablishBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充授信主体
        if (Objects.nonNull(groupCreditEstablishBaseInfo.getClientId())) {
            clientIds.add(groupCreditEstablishBaseInfo.getClientId());
        }
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, groupCreditEstablishBaseInfo.getBizDeptId());
    }

    private void doGroupCreditReview(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = SpringUtil.getBean(GroupCreditReviewBaseInfoService.class).getById(info.getBizId());
        if (Objects.isNull(groupCreditReviewBaseInfo)) {
            log.error("客户查看权-<集团授信评审>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(groupCreditReviewBaseInfo.getGroupCreditReviewStatus(), RecordStatus.CLOSED.name()) || Objects.equals(groupCreditReviewBaseInfo.getGroupCreditReviewStatus(), RecordStatus.EXPIRE.name())) {
            this.doClientAuthority(info, null, null, groupCreditReviewBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(groupCreditReviewBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(groupCreditReviewBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充授信主体
        if (Objects.nonNull(groupCreditReviewBaseInfo.getClientId())) {
            clientIds.add(groupCreditReviewBaseInfo.getClientId());
        }
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, groupCreditReviewBaseInfo.getBizDeptId());
    }

    private void doProjEstablish(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        ProjEstablishBaseInfo projEstablishBaseInfo = SpringUtil.getBean(ProjEstablishBaseInfoService.class).getById(info.getBizId());
        if (Objects.isNull(projEstablishBaseInfo)) {
            log.error("客户查看权-<项目立项>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(projEstablishBaseInfo.getProjEstablishStatus(), RecordStatus.CLOSED.name()) || Objects.equals(projEstablishBaseInfo.getProjEstablishStatus(), RecordStatus.EXPIRE.name())) {
            this.doClientAuthority(info, null, null, projEstablishBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(projEstablishBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(projEstablishBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充承租人
        this.fillClientIdsByJsonStr(projEstablishBaseInfo.getLesseeInfo(), clientIds);
        // 填充担保人
        this.fillClientIdsByJsonStr(projEstablishBaseInfo.getGuaranteeInfo(), clientIds);
        // 填充抵押人
        this.fillClientIdsByJsonStr(projEstablishBaseInfo.getMortgagorInfo(), clientIds);
        // 填充质押人
        this.fillClientIdsByJsonStr(projEstablishBaseInfo.getPledgorInfo(), clientIds);
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, projEstablishBaseInfo.getBizDeptId());
    }

    private void doProjReview(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(info.getBizId());
        if (Objects.isNull(projReviewBaseInfo)) {
            log.error("客户查看权-<项目评审>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.CLOSED.name()) || Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.EXPIRE.name())) {
            this.doClientAuthority(info, null, null, projReviewBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(projReviewBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(projReviewBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充承租人
        this.fillClientIdsByJsonStr(projReviewBaseInfo.getLesseeInfo(), clientIds);
        // 填充担保人
        this.fillClientIdsByJsonStr(projReviewBaseInfo.getGuaranteeInfo(), clientIds);
        // 填充抵押人
        this.fillClientIdsByJsonStr(projReviewBaseInfo.getMortgagorInfo(), clientIds);
        // 填充质押人
        this.fillClientIdsByJsonStr(projReviewBaseInfo.getPledgorInfo(), clientIds);
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, projReviewBaseInfo.getBizDeptId());
    }

    private void doProjPricing(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getById(info.getBizId());
        if (Objects.isNull(projPricingBaseInfo)) {
            log.error("客户查看权-<项目定价>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(projPricingBaseInfo.getProjPricingStatus(), RecordStatus.CLOSED.name())) {
            this.doClientAuthority(info, null, null, projPricingBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(projPricingBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(projPricingBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充承租人
        this.fillClientIdsByJsonStr(projPricingBaseInfo.getLesseeInfo(), clientIds);
        // 填充担保人
        this.fillClientIdsByJsonStr(projPricingBaseInfo.getGuaranteeInfo(), clientIds);
        // 填充抵押人
        this.fillClientIdsByJsonStr(projPricingBaseInfo.getMortgagorInfo(), clientIds);
        // 填充质押人
        this.fillClientIdsByJsonStr(projPricingBaseInfo.getPledgorInfo(), clientIds);
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, projPricingBaseInfo.getBizDeptId());
    }

    private void doContract(ClientViewAuthorityEvent.ClientViewAuthorityInfo info) {
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(info.getBizId());
        if (Objects.isNull(contractBaseInfo)) {
            log.error("客户查看权-<合同管理>数据不存在[id:{}]", info.getBizId());
            return;
        }
        if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.INVALID.name())) {
            this.doClientAuthority(info, null, null, contractBaseInfo.getBizDeptId());
            return;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        // 填充协办
        if (StrUtil.isNotBlank(contractBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        // 填充承租人
        List<ContractTenantry> contractTenantryList = SpringUtil.getBean(ContractTenantryService.class).listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            clientIds.addAll(contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet()));
        }
        // 填充担保人
        List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractBaseInfo.getId());
        contractGuarantorList.removeIf(e -> !Objects.equals(e.getGuarantorType(), ClientType.CORPORATION.name()));
        if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
            for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                if (StrUtil.isNotBlank(contractGuarantor.getGuarantorIds())) {
                    List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
            }
        }
        // 填充抵押人
        List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractId(contractBaseInfo.getId());
        contractMortgageList.removeIf(e -> !Objects.equals(e.getMortgageType(), ClientType.CORPORATION.name()));
        if (CollectionUtil.isNotEmpty(contractMortgageList)) {
            for (ContractMortgage contractMortgage : contractMortgageList) {
                if (StrUtil.isNotBlank(contractMortgage.getMortgageIds())) {
                    List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
            }
        }
        // 填充质押人
        List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractId(contractBaseInfo.getId());
        contractPledgeList.removeIf(e -> !Objects.equals(e.getPledgeType(), ClientType.CORPORATION.name()));
        if (CollectionUtil.isNotEmpty(contractPledgeList)) {
            for (ContractPledge contractPledge : contractPledgeList) {
                if (StrUtil.isNotBlank(contractPledge.getPledgeIds())) {
                    List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
            }
        }
        // 处理权限
        this.doClientAuthority(info, userIds, clientIds, contractBaseInfo.getBizDeptId());
    }

    private void doClientAuthority(ClientViewAuthorityEvent.ClientViewAuthorityInfo info, Set<Long> userIds, Set<Long> clientIds, Long bizDeptId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 先统一删除
                LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
                query.eq(ClientAuthority::getSourceBusinessType, info.getBusinessModule().name());
                query.eq(ClientAuthority::getSourceId, info.getBizId());
                clientAuthorityService.remove(query);
                // 再统一新增
                if (CollectionUtil.isEmpty(userIds) || CollectionUtil.isEmpty(clientIds)) {
                    log.info("客户查看权-必要数据集合不存在，不执行添加权限逻辑[info:{}, userIds:{}, clientIds:{}]", JSONUtil.toJsonStr(info), JSONUtil.toJsonStr(userIds), JSONUtil.toJsonStr(clientIds));
                    return;
                }
                List<ClientAuthority> clientAuthorityList = new LinkedList<>();
                for (Long userId : userIds) {
                    for (Long clientId : clientIds) {
                        ClientAuthority clientAuthority = new ClientAuthority();
                        clientAuthority.setDeptId(bizDeptId);
                        clientAuthority.setClientId(clientId);
                        clientAuthority.setUserId(userId);
                        clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                        clientAuthority.setSourceBusinessType(info.getBusinessModule().name());
                        clientAuthority.setSourceId(info.getBizId());
                        clientAuthorityList.add(clientAuthority);
                    }
                }
                if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
                    clientAuthorityService.saveBatch(clientAuthorityList);
                }
            } catch (Exception e) {
                log.error("客户查看权-保存数据发生异常[info:{}, userIds:{}, clientIds:{}]", JSONUtil.toJsonStr(info), JSONUtil.toJsonStr(userIds), JSONUtil.toJsonStr(clientIds), e);
                transactionStatus.setRollbackOnly();
            }
        });
    }

    private void fillClientIdsByJsonStr(String jsonArray, Set<Long> clientIds) {
        if (StrUtil.isNotBlank(jsonArray)) {
            List<ClientInfo> clientInfoList = JSONUtil.toList(jsonArray, ClientInfo.class);
            if (CollectionUtil.isNotEmpty(clientInfoList)) {
                clientIds.addAll(clientInfoList.stream().filter(e -> Objects.nonNull(e.getClientId())).map(ClientInfo::getClientId).collect(Collectors.toSet()));
            }
        }
    }
}
