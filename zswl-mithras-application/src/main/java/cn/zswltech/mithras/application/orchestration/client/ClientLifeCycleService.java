package cn.zswltech.mithras.application.orchestration.client;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.*;
import cn.zswltech.mithras.collection.application.CollectionOverdueHistoryService;
import cn.zswltech.mithras.customer.application.client.CorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjStageEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.dto.ClientListParam;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractPrice;
import cn.zswltech.mithras.contract.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.persistence.PaginationProcessor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType.GROUP_CREDIT_REVIEW;
import static cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType.PROJ_ESTABLISH;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 15:30
 */
@Service
@Slf4j
public class ClientLifeCycleService {

    @Resource
    private ClientService clientService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;
    @Resource
    private CollectionOverdueHistoryService collectionOverdueHistoryService;


    private final Cache<String, Object> cache = CacheUtil.newTimedCache(60 * 1000);

    /**
     * 查询有过投放的客户剩余本金
     *
     * @return
     */
    private Map<Long, Long> getRemainingPrincipals() {
        if (cache.get("remainingPrincipals") != null) {
            return (Map<Long, Long>) cache.get("remainingPrincipals");
        } else {
            ConcurrentHashMap<Long, Long> remainingPrincipals = new ConcurrentHashMap<>();
            //投放过的客户数
            PaginationProcessor<PaymentActualDetail> processor =
                    new PaginationProcessor<PaymentActualDetail>(100, PaymentActualDetailMapper.class) {
                        @Override
                        protected void doProcess(List<PaymentActualDetail> list) {
                            Set<Long> actualLaunchClientIds = list.stream()
                                    .map(PaymentActualDetail::getClientId).collect(Collectors.toSet());
                            RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
                            dto.setClientIds(actualLaunchClientIds);
                            remainingPrincipalService.remainingPrincipalGroupByClientId(dto).forEach(remainingPrincipals::putIfAbsent);
                        }
                    };
            processor.process();
            cache.put("remainingPrincipals", remainingPrincipals);
            return remainingPrincipals;
        }
    }

    public ClientLifeCycleCardRsp card() {
        ClientLifeCycleCardRsp rsp = new ClientLifeCycleCardRsp();
        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        // 总客户数
        ClientLifeCycleListReq req = new ClientLifeCycleListReq();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        Set<Long> targetClientIds = getClientIds();
        Page<Client> clientPage = getClientPage(targetClientIds, req);
        List<Client> total = clientPage.getRecords().stream().filter(record -> record.getClientStatus().equalsIgnoreCase("TAKE_EFFECT")).collect(Collectors.toList());
        List<Client> totalMonthIncrease = total.stream().filter(client -> client.getCreateTime().isAfter(startOfMonth)).collect(Collectors.toList());
        rsp.setTotalClientNum(total.size());
        rsp.setTotalClientNumMonthIncrease(totalMonthIncrease.size());
        Map<Long, Long> remainingPrincipals = getRemainingPrincipals();
        // 查询第一次投放在本月的投放记录
        Set<Long> firstLaunchClientIds = paymentActualDetailService.queryFirstLaunchClient(startOfMonth)
                .stream().map(PaymentActualDetail::getClientId)
                .collect(Collectors.toSet());
        // 查询本月有回款的客户id
        Set<Long> collectThisMonthClient = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                        .ge(CollectionBaseInfo::getCollectionDate, startOfMonth)).stream().map(CollectionBaseInfo::getClientId)
                .collect(Collectors.toSet());
        // 查询逾期客户id
//        Set<Long> overdueClientIds = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
//                .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now().minusDays(3).atStartOfDay())
//                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())).stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet());
        // 不考虑宽限期，仅计算租金（本金+利息）没有核销完的情况，不考虑罚息
        Set<Long> overdueClientIds = collectionBaseInfoService.overdueListWithoutGracePeriod().stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet());

        int existingClientNum = 0;
        int existingClientNumMonthIncrease = 0;
        int settledClientNum = 0;
        int settledClientNumMonthIncrease = 0;
        int overdueClientNum = 0;
        for (Map.Entry<Long, Long> entry : remainingPrincipals.entrySet()) {
            if (entry.getValue() > 0) {
                existingClientNum++;
                if (firstLaunchClientIds.contains(entry.getKey())) {
                    existingClientNumMonthIncrease++;
                }
                if (overdueClientIds.contains(entry.getKey())) {
                    overdueClientNum++;
                }
            } else {
                if (collectThisMonthClient.contains(entry.getKey())) {
                    settledClientNumMonthIncrease++;
                }
                settledClientNum++;
            }
        }
        rsp.setExistingClientNum(existingClientNum);
        rsp.setExistingClientNumMonthIncrease(existingClientNumMonthIncrease);
        rsp.setSettledClientNum(settledClientNum);
        rsp.setSettledClientNumMonthIncrease(settledClientNumMonthIncrease);
        rsp.setOverdueClientNum(overdueClientNum);
        return rsp;
    }

    public PageR<ClientListRSP> clientList(ClientLifeCycleListReq req) {
        Set<Long> targetClientIds = getTargetClientIds(req.getCardName());
        // 如果目标客户id为空，直接返回空 注意这里不包括null
        if (targetClientIds != null && targetClientIds.isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<Client> clientPage = getClientPage(targetClientIds, req);
        List<Client> records = clientPage.getRecords();
        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
        clientService.fillOtherInfo(list, Boolean.FALSE);
        // 客户全周期列表补充一些数据
        if (CollectionUtil.isNotEmpty(list)) {
            Set<Long> clientIds = list.stream().map(ClientListRSP::getId).collect(Collectors.toSet());
            Map<Long, List<CorpAddressInfo>> addressMap = SpringUtil.getBean(CorpAddressInfoService.class).getRegistryAddressMap(clientIds);
            Map<Long, List<CorpCommerceInfo>> commerceMap = SpringUtil.getBean(CorpCommerceInfoService.class).getCorpCommerceInfoMap(clientIds);
            list.forEach(client -> {
                CorpAddressInfo corpAddressInfo = Optional.ofNullable(addressMap.get(client.getId())).map(e -> e.get(0)).orElse(null);
                CorpCommerceInfo corpCommerceInfo = Optional.ofNullable(commerceMap.get(client.getId())).map(e -> e.get(0)).orElse(null);
                if (Objects.nonNull(corpAddressInfo)) {
                    client.setProvinceCode(corpAddressInfo.getProvince());
                    client.setProvinceName(SpringUtil.getBean(BusinessDataRepository.class).getAddressNameFromLocalCache(corpAddressInfo.getProvince()));
                    client.setCityCode(corpAddressInfo.getCity());
                    client.setCityName(SpringUtil.getBean(BusinessDataRepository.class).getAddressNameFromLocalCache(corpAddressInfo.getCity()));
                    client.setDistrictCode(corpAddressInfo.getDistrict());
                    client.setDistrictName(SpringUtil.getBean(BusinessDataRepository.class).getAddressNameFromLocalCache(corpAddressInfo.getDistrict()));
                }
                if (Objects.nonNull(corpCommerceInfo)) {
                    client.setEnterpriseNature(corpCommerceInfo.getEnterpriseNature());
                    client.setEnterpriseNatureName(Optional.ofNullable(EnterpriseNatureEnum.of(corpCommerceInfo.getEnterpriseNature())).map(EnterpriseNatureEnum::display).orElse(null));
                    client.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
                    client.setRiskControlIndustryClassifyName(Optional.ofNullable(RiskControlIndustryClassify.findByName(corpCommerceInfo.getRiskControlIndustryClassify())).map(RiskControlIndustryClassify::display).orElse(null));
                    client.setIndustryType(corpCommerceInfo.getIndustryType());
                    client.setIndustryTypeName(SpringUtil.getBean(BusinessDataRepository.class).getIndustryTypeNameFromLocalCache(corpCommerceInfo.getIndustryType()));
                }
            });
        }
        return PageR.of(list, clientPage.getTotal(),
                clientPage.getPages(),
                clientPage.getCurrent(),
                clientPage.getSize());
    }

    private Page<Client> getClientPage(Set<Long> targetClientIds, ClientLifeCycleListReq req) {
        ClientListParam param = new ClientListParam();
        param.setTargetClientIds(targetClientIds)
                .setProvinceCode(req.getProvinceCode())
                .setCityCode(req.getCityCode())
                .setDistrictCode(req.getDistrictCode())
                .setEnterpriseNature(req.getEnterpriseNature())
                .setIndustryType(req.getIndustryType())
                .setRiskControlIndustryClassify(req.getRiskControlIndustryClassify())
                //.setClientStatus(ClientStatus.TAKE_EFFECT.name())
                .setCreateFrom(DateUtil.startOfDay(req.getCreateDateFrom()))
                .setCreateTo(DateUtil.endOfDay(req.getCreateDateTo()))
                .setClientName(req.getClientName())
                .setBelongSponsorId(req.getSponsorId())
                .setBelongDeptId(req.getDeptId())
                .setCreateById(req.getCreateBy());
        Page<Client> clientPage = clientMapper.myList(new Page(req.getPage(), req.getPageSize()), param);
        return clientPage;
    }

    public ClientListRSP clientDetail(Long clientId) {
        Client client = clientService.getById(clientId);
        if (client == null) {
            throw new MithrasException("客户不存在");
        }
        ClientListRSP rsp = BeanUtil.copyProperties(client, ClientListRSP.class);
        clientService.fillOtherInfo(Collections.singletonList(rsp), Boolean.FALSE);
        return rsp;
    }

    public Set<Long> getTargetClientIds(String cardName) {
        CardName cardNameEnum;
        try {
            cardNameEnum = CardName.valueOf(cardName);
        } catch (Exception e) {
            throw new MithrasException("不支持的卡片类型");
        }
        if (cardNameEnum == CardName.TOTAL) {
            return getClientIds();
        }
        Set<Long> targetClientIds = new HashSet<>();
        Map<Long, Long> remainingPrincipals = getRemainingPrincipals();
        if (cardNameEnum == CardName.EXISTING) {
            for (Map.Entry<Long, Long> entry : remainingPrincipals.entrySet()) {
                if (entry.getValue() > 0) {
                    targetClientIds.add(entry.getKey());
                }
            }
            return targetClientIds;
        }
        if (cardNameEnum == CardName.SETTLED) {
            for (Map.Entry<Long, Long> entry : remainingPrincipals.entrySet()) {
                if (entry.getValue() <= 0) {
                    targetClientIds.add(entry.getKey());
                }
            }
            return targetClientIds;
        }
        if (cardNameEnum == CardName.OVERDUE) {
            // 逾期大于三天算逾期 ->修改为动态查询
            Set<Long> overdueClientIds;
            Object overdueClientIdsCache = cache.get("overdueClientIds");
            if (overdueClientIdsCache != null) {
                overdueClientIds = (Set<Long>) overdueClientIdsCache;
            } else {
                /*overdueClientIds = collectionOverdueHistoryService.list(
                        Wrappers.<CollectionOverdueHistory>lambdaQuery()
                                .gt(CollectionOverdueHistory::getOverdueDays, 3)).stream().map(CollectionOverdueHistory::getClientId).collect(Collectors.toSet());*/
//                overdueClientIds = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
//                        .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now().minusDays(3).atStartOfDay())
//                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())).stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet());
                // 修改逾期客户逻辑
                overdueClientIds = collectionBaseInfoService.overdueListWithoutGracePeriod().stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet());
                cache.put("overdueClientIds", overdueClientIds);
            }
            for (Map.Entry<Long, Long> entry : remainingPrincipals.entrySet()) {
                if (entry.getValue() > 0 && overdueClientIds.contains(entry.getKey())) {
                    targetClientIds.add(entry.getKey());
                }
            }
            return targetClientIds;
        }
        return null;
    }

    private Set<Long> getClientIds() {
        // 需要按照不同登陆角色处理
        Set<Long> targetClientIds = new HashSet<>();
        // 默认先填一个不可能的值，如果后续逻辑没有往里填充说明没有可看的客户
        targetClientIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        // 自然人看自己创建的
        List<Client> normalList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientType, ClientType.NORMAL.name()).eq(BaseModel::getCreateBy, currentUserId));
        if (CollectionUtil.isNotEmpty(normalList)) {
            targetClientIds.addAll(normalList.stream().map(Client::getId).collect(Collectors.toSet()));
        }
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new HashSet<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            return null;
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerTargetClientIds = clientService.findTargetClientIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerTargetClientIds)) {
                targetClientIds.addAll(projmanagerTargetClientIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetClientIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetClientIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        return targetClientIds;
    }

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjEstablishPriceService projEstablishPriceService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractPriceService contractPriceService;

    public List<ClientLifeCycleProjectListRsp> projectList(ClientLifeCycleDetailReq req) {
        List<ProjEstablishBaseInfo> establish = projEstablishBaseInfoService.list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getClientId, req.getClientId())
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, "TAKE_EFFECT"));
        Map<Long, ClientLifeCycleProjectListRsp> rspMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(establish)) {
            Map<Long, Long> longLongMap = projEstablishPriceService.newestPrice(establish.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toSet()));
            establish.forEach(v -> {
                ClientLifeCycleProjectListRsp rsp = new ClientLifeCycleProjectListRsp();
                rsp.setProjectId(v.getId());
                rsp.setProjectName(v.getProjName());
                rsp.setApplyCreditAmount(longLongMap.get(v.getId()));
                rsp.setProjStage(ProjStageEnum.PROJESTABLISH_STAGE.name());
                rsp.setBizType(v.getBizType());
                rsp.setDataType(PROJ_ESTABLISH.name());
                rsp.setProjectCode(v.getProjCode());
                rsp.setClientId(v.getClientId());
                rspMap.put(v.getId(), rsp);
            });
        }

        List<ProjReviewBaseInfo> review = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getClientId, req.getClientId())
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT"));
        Map<Long, Long> establish2Review = review.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjEstablishId, ProjReviewBaseInfo::getId));
        if (ObjectUtil.isNotEmpty(review)) {
            Map<Long, Long> longLongMap = projReviewPriceService.newestPrice(review.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet()));
            review.forEach(v -> {
                if (GROUP_CREDIT_REVIEW.name().equals(v.getRelationDataType())) {
                    ClientLifeCycleProjectListRsp groupRsp = new ClientLifeCycleProjectListRsp();
                    groupRsp.setDataType(GROUP_CREDIT_REVIEW.name());
                    groupRsp.setProjectName(v.getProjName() + "(集团授信)");
                    groupRsp.setProjectId(v.getId());
                    groupRsp.setApplyCreditAmount(longLongMap.get(v.getId()));
                    groupRsp.setBizType(v.getBizType());
                    groupRsp.setProjectName(v.getProjName());
                    groupRsp.setProjStage(ProjStageEnum.PROJREVIEW_STAGE.name());
                    groupRsp.setProjectCode(v.getProjCode());
                    groupRsp.setClientId(v.getClientId());
                    rspMap.put(v.getId(), groupRsp);
                } else {
                    Long projectId = v.getProjEstablishId();
                    if (ObjectUtil.isEmpty(projectId)) {
                        return;
                    }
                    ClientLifeCycleProjectListRsp rsp = rspMap.get(v.getProjEstablishId());
                    if (ObjectUtil.isEmpty(rsp)) {
                        return;
                    }
                    rsp.setApplyCreditAmount(longLongMap.get(v.getId()));
                    rsp.setProjStage(ProjStageEnum.PROJREVIEW_STAGE.name());
                    rsp.setProjReviewId(establish2Review.get(projectId));
                    rspMap.put(projectId, rsp);
                }
            });
        }
        Map<Long, Long> reviewId2ProjectId = review.stream().filter(v -> "PROJ_ESTABLISH".equals(v.getRelationDataType())).collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
        Map<Long, Long> groupProject = review.stream().filter(v -> "GROUP_CREDIT_REVIEW".equals(v.getRelationDataType())).collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getId));
        if (ObjectUtil.isNotEmpty(groupProject)) {
            reviewId2ProjectId.putAll(groupProject);
        }
        // SETTLE("结清"), START_RENT("起租"), TAKE_EFFECT("生效")
        List<ContractBaseInfo> contract = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, req.getClientId())
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT", "SETTLE"));
        if (ObjectUtil.isNotEmpty(contract)) {
            Map<Long, Long> contractPrice = contractPriceService.queryNewestPrice(
                    contract.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(ContractPrice::getContractId, ContractPrice::getContractAmount));

            contract.forEach(v -> {
                Long projectId = reviewId2ProjectId.get(v.getProjReviewId());
                if (ObjectUtil.isEmpty(projectId)) {
                    return;
                }
                ClientLifeCycleProjectListRsp rsp = rspMap.get(projectId);
                if (ObjectUtil.isEmpty(rsp)) {
                    return;
                }
                rsp.setContractAmount(contractPrice.get(v.getId()));
                if ("SETTLE".equals(v.getContractStatus())) {
                    rsp.setProjStage(ProjStageEnum.CONTRACTSETTLE_STAGE.name());
                } else {
                    rsp.setProjStage(ProjStageEnum.CONTRACT_STAGE.name());
                }
                rspMap.put(projectId, rsp);
            });
        }
        return new ArrayList<>(rspMap.values());
    }

    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;

    public String fiveLevel(ClientLifeCycleDetailReq req) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify(LocalDate.now());
        if (!assetClassify.isPresent()) {
            return "未获取到分类";
        }
        Map<Long, String> result = assetClassifyClientAuxiliaryLibService
                .newestClassifyClientLib(assetClassify.get().getMainId())
                .stream().collect(Collectors.toMap(AssetClassifyClient::getClientId, AssetClassifyClient::getClassifyResult));
        return result.getOrDefault(req.getClientId(), "未获取到分类");
    }

    @Resource
    private ContractReceiptLibService contractReceiptLibService;

    public ClientLifeCycleReceiptRsp receipt(ClientLifeCycleDetailReq req) {
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, req.getClientId())
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT", "SETTLE"));
        List<ContractReceiptLib> contractReceiptLibs = contractReceiptLibService.getBaseMapper().newsetContractReceiptLib(contracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));

        List<ReceiptListRsp> receiptListRsps = new ArrayList<>();
        for (ContractReceiptLib lib : contractReceiptLibs) {
            ReceiptListRsp rsp = new ReceiptListRsp();
            rsp.setContractId(lib.getContractId());
            rsp.setReceiptCode(lib.getReceiptCode());
            receiptListRsps.add(rsp);
        }

        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.getBaseMapper().specifyClientOverdueList(req.getClientId());
        Long overdueAmount = 0L;
        for (CollectionBaseInfo info : collectionBaseInfos) {
            if (info.getPrincipal() == null) {
                continue;
            }
            overdueAmount += (info.getPrincipal() - LongUtil.null2zero(info.getCollectionPrincipal()));
            overdueAmount += (info.getInterest() - LongUtil.null2zero(info.getCollectionInterest()));
        }

        ClientLifeCycleReceiptRsp finalRsp = new ClientLifeCycleReceiptRsp();
        finalRsp.setReceiptPage(receiptListRsps);
        finalRsp.setOverdueAmount(overdueAmount);
        return finalRsp;
    }

}
