package cn.zswltech.mithras.application.orchestration.projectprocess.projreview;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.*;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewAocPriceLibHandler;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewFactoringPriceLibHandler;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/3 17:29
 */
@Service
public class ProjReviewPriceService {

    @Resource
    private ProjReviewBaseInfoService baseInfoService;
    @Resource
    private ProjReviewLeasePriceService leasePriceService;
    @Resource
    private ProjReviewFactoringPriceService factoringPriceService;
    @Resource
    private ProjReviewAocPriceService aocPriceService;
    @Resource
    private ProjReviewLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjReviewAocPriceLibService aocPriceLibService;
    @Resource
    private ProjReviewPriceConverter priceConverter;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ProjReviewLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjReviewFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjReviewAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ProjReviewLeasePriceLibHandler leasePriceLibHandler;
    @Resource
    private ProjReviewFactoringPriceLibHandler factoringPriceLibHandler;
    @Resource
    private ProjReviewAocPriceLibHandler aocPriceLibHandler;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingPriceService pricingPriceService;

    public void saveIrr(Long projReviewId, Integer irr) {
        ProjReviewBaseInfo projReviewBaseInfo = baseInfoService.getById(projReviewId);
        Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("项目评审信息不存在"));
        // 流程中修改irr，需要校验不得低于定价审批通过的irr
        if (ProjProcessState.NEW_UNDER_APPROVAL.name().equals(projReviewBaseInfo.getProjReviewProcessStatus())
                || ProjProcessState.CHANGING_UNDER_APPROVAL.name().equals(projReviewBaseInfo.getProjReviewProcessStatus())) {

            ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getPricingByReview(projReviewBaseInfo);
            if(ObjectUtil.isNotEmpty(pricingBaseInfo)){
                ProjPricingPriceDetailRSP detail = pricingPriceService.detail(pricingBaseInfo.getId());
                if (ObjectUtil.isNotEmpty(detail)
                        && irr < detail.getIrr()) {
                    throw new MithrasException("IRR不允许低于定价审批时的值");
                }
            }


        }
        if (Objects.equals(ProjectBizType.BL.name(), projReviewBaseInfo.getBizType())) {
            factoringPriceService.saveIrr(projReviewId, irr);
        } else if (Objects.equals(ProjectBizType.ZR.name(), projReviewBaseInfo.getBizType())) {
            aocPriceService.saveIrr(projReviewId, irr);
        } else {
            leasePriceService.saveIrr(projReviewId, irr);
        }
    }


    /**
     * 创建评审时添加报价方案
     *
     * @param price
     */
    public void add(BaseModel price) {
        price.setCreateTime(null);
        price.setUpdateTime(null);
        price.setUpdateBy(null);
        if (price instanceof ProjReviewAocPrice) {
            aocPriceService.add((ProjReviewAocPrice) price);
        } else if (price instanceof ProjReviewFactoringPrice) {
            factoringPriceService.add((ProjReviewFactoringPrice) price);
        } else if (price instanceof ProjReviewLeasePrice) {
            leasePriceService.add((ProjReviewLeasePrice) price);
        }
    }

    /**
     * 复合更新报价接口
     *
     * @param req
     */
    @Transactional(rollbackFor = Throwable.class)
    public void modify(ProjReviewPriceModifyREQ req) {
        // 集团授信项目要校验剩余可用额度
        ProjReviewBaseInfo projReviewBaseInfo = baseInfoService.getById(req.getProjectId());
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 如果有合同，校验金额
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(projReviewBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            long totalContractAmount = contractBaseInfoList.stream().mapToLong(ContractBaseInfo::getApplyCreditAmount).sum();
            Assert.isTrue(req.getAmount() >= totalContractAmount, () -> MithrasException.newException("项目授信金额不能小于所有合同金额之和"));
        }
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projReviewBaseInfo.getRelationDataType())) {
            // 集团授信发起的评审要锁操作
            String gcrRemainAmountLockKey = CacheEnum.GROUP_CREDIT_REVIEW_REMAIN_AMOUNT_LOCK.buildKey(projReviewBaseInfo.getGroupCreditReviewId());
            boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(gcrRemainAmountLockKey, 1000L);
            if (!getLockFlag) {
                throw new MithrasException(CONCURRENT_OPERATION);
            }
            try {
                Long remainCreditAmount = groupCreditReviewService.getRemainCreditAmount(projReviewBaseInfo.getGroupCreditReviewId());
                // 修改报价方案的上限需要把原值释放出来
                remainCreditAmount = remainCreditAmount + Optional.ofNullable(projReviewBaseInfo.getDeclaredAmount()).orElse(0L);
                if (remainCreditAmount <= 0) {
                    throw new MithrasException("该授信可用额度已小于等于0，不可再进行操作");
                }
                Long applyCreditAmount = Optional.ofNullable(req.getAocPriceModifyREQ())
                        .map(ProjReviewAocPriceModifyREQ::getApplyCreditAmount)
                        .orElse(Optional.ofNullable(req.getFactoringPriceModifyREQ())
                                .map(ProjReviewFactoringPriceModifyREQ::getApplyCreditAmount)
                                .orElse(Optional.ofNullable(req.getLeasePriceModifyREQ()).map(ProjReviewLeasePriceModifyREQ::getApplyCreditAmount).orElse(0L))
                        );
                if (applyCreditAmount > remainCreditAmount) {
                    throw new MithrasException("评审金额需大于0且小于等于授信剩余可用金额" + Util.mithrasLong2BigDecimal(remainCreditAmount).toPlainString());
                }
                doModify(req);
            } finally {
                redisDistLock.unlock(gcrRemainAmountLockKey);
            }
        } else {
            doModify(req);
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public void doModify(ProjReviewPriceModifyREQ req) {
        if (req.getAocPriceModifyREQ() != null) {
            aocPriceService.modify(req.getAocPriceModifyREQ());
            baseInfoService.updateDeclaredAmount(req.getProjectId(), req.getAocPriceModifyREQ().getApplyCreditAmount());
        }
        if (req.getFactoringPriceModifyREQ() != null) {
            factoringPriceService.modify(req.getFactoringPriceModifyREQ());
            baseInfoService.updateDeclaredAmount(req.getProjectId(), req.getFactoringPriceModifyREQ().getApplyCreditAmount());
        }
        if (req.getLeasePriceModifyREQ() != null) {
            leasePriceService.modify(req.getLeasePriceModifyREQ());
            baseInfoService.updateDeclaredAmount(req.getProjectId(), req.getLeasePriceModifyREQ().getApplyCreditAmount());
        }
    }
    @Transactional(rollbackFor = Throwable.class)
    public void doModifyProjectApprovalAmount(Long projectId, Long projectApprovalAmount) {
        aocPriceService.lambdaUpdate()
                .set(ProjReviewAocPrice::getProjectApprovalAmount, projectApprovalAmount)
                .eq(ProjReviewAocPrice::getProjectId, projectId)
                .update();
        leasePriceService.lambdaUpdate()
                .set(ProjReviewLeasePrice::getProjectApprovalAmount, projectApprovalAmount)
                .eq(ProjReviewLeasePrice::getProjectId, projectId)
                .update();
        factoringPriceService.lambdaUpdate()
                .set(ProjReviewFactoringPrice::getProjectApprovalAmount, projectApprovalAmount)
                .eq(ProjReviewFactoringPrice::getProjectId, projectId)
                .update();
    }


    /**
     * 查询复合接口
     *
     * @param projectId
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ProjReviewPriceDetailRSP detail(Long projectId) {
        ProjReviewPriceDetailRSP res = new ProjReviewPriceDetailRSP();
        // 处理租赁
        res.setLeasePriceDetailRSP(priceConverter.entityToLeaseRsp(leasePriceService.getByProjectId(projectId)));
        // 处理保理
        ProjReviewFactoringPriceRSP reviewFactoringPriceRSP =
                priceConverter.entityToFactoringRsp(factoringPriceService.getByProjectId(projectId));
        if (reviewFactoringPriceRSP != null) {
            // 有存续租赁授信，则查询合同列表
//            ProjReviewBaseInfo byId = baseInfoService.getById(projectId);
//            reviewFactoringPriceRSP.setContracts(contractService.contractMock(byId.getClientId()));
            reviewFactoringPriceRSP.setContracts(getSurvivingContract(projectId));
        }
        res.setFactoringPriceDetailRSP(reviewFactoringPriceRSP);
        // 处理债权转让
        res.setAocPriceDetailRSP(priceConverter.entityToAocRsp(aocPriceService.getByProjectId(projectId)));
        return res;
    }

    /**
     * 查询复合接口
     *
     */
    @Transactional(rollbackFor = Throwable.class)
    public Map<Long, ProjReviewPriceDetailRSP> list(List<Long> projectIds) {
        if (ObjectUtil.isEmpty(projectIds)) {
            return MapUtil.empty();
        }
        Map<Long, ProjReviewPriceDetailRSP> rsps = new HashMap<>();
        Map<Long, ProjReviewLeasePrice> leasePriceMap = leasePriceService.listByProjectIds(projectIds).stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, e -> e, (a, b) -> a));
        Map<Long, ProjReviewFactoringPrice> factoringMap = factoringPriceService.listByProjectIds(projectIds).stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, e -> e, (a, b) -> a));
        Map<Long, ProjReviewAocPrice> aocPriceMap = aocPriceService.listByProjectIds(projectIds).stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, e -> e, (a, b) -> a));

        projectIds.forEach(projectId -> {
            ProjReviewPriceDetailRSP rsp = new ProjReviewPriceDetailRSP();
            // 处理租赁
            rsp.setLeasePriceDetailRSP(priceConverter.entityToLeaseRsp(leasePriceMap.get(projectId)));
            // 处理保理
            rsp.setFactoringPriceDetailRSP(priceConverter.entityToFactoringRsp(factoringMap.get(projectId)));
            // 处理债权转让
            rsp.setAocPriceDetailRSP(priceConverter.entityToAocRsp(aocPriceMap.get(projectId)));
            rsps.put(projectId, rsp);
        });
        return rsps;
    }

    public List<ContractBaseInfoRSP> getSurvivingContract(Long projectId) {
        ProjReviewBaseInfo projReviewBaseInfo = baseInfoService.getById(projectId);
        if (!Objects.equals(projReviewBaseInfo.getBizType(), ProjectBizType.BL.name())) {
            return Collections.emptyList();
        }
        Map<Long, ClientInfo> creditMap;
        Map<Long, ClientInfo> debtorMap;
        if (StrUtil.isNotBlank(projReviewBaseInfo.getCreditorInfo())) {
            creditMap = JSONUtil.toList(projReviewBaseInfo.getCreditorInfo(), ClientInfo.class).stream().collect(Collectors.toMap(ClientInfo::getClientId, e -> e));
        } else {
            creditMap = Collections.emptyMap();
        }
        if (StrUtil.isNotBlank(projReviewBaseInfo.getDebtorInfo())) {
            debtorMap = JSONUtil.toList(projReviewBaseInfo.getDebtorInfo(), ClientInfo.class).stream()
                    // 非法人无需关注，直接过滤掉
                    .filter(e -> !Objects.equals(e.getClientType(), GlobalConstants.DEBTOR_NO_CORPORATION))
                    .collect(Collectors.toMap(ClientInfo::getClientId, e -> e));
        } else {
            debtorMap = Collections.emptyMap();
        }
        // 取出所有客户id
        List<Long> clientIds = new LinkedList<>();
        clientIds.addAll(creditMap.keySet());
        clientIds.addAll(debtorMap.keySet());
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        // 查询合同模块债权债务人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByClientIds(clientIds);
        if (CollectionUtil.isEmpty(contractTenantryList)) {
            return Collections.emptyList();
        }
        // 债权债务人和合同的关系
        Map<Long, List<Long>> clientContractMap = new HashMap<>();
        for (ContractTenantry contractTenantry : contractTenantryList) {
            List<Long> contractIdList = clientContractMap.get(contractTenantry.getLesseeId());
            if (Objects.isNull(contractIdList)) {
                contractIdList = new LinkedList<>();
                clientContractMap.put(contractTenantry.getLesseeId(), contractIdList);
            }
            contractIdList.add(contractTenantry.getContractId());
        }
        // 取出所有合同id查询合同信息
        Set<Long> contractIds = contractTenantryList.stream().map(ContractTenantry::getContractId).collect(Collectors.toSet());
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractIds);
        // 过滤出新建、生效和起租的租赁合同
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoList.stream()
                .filter(e -> {
                    if (!Objects.equals(e.getBizType(), ProjectBizType.ZL.name())) {
                        return false;
                    }
                    return Objects.equals(e.getContractStatus(), ContractStatus.TAKE_EFFECT.name())
                            || Objects.equals(e.getContractStatus(), ContractStatus.START_RENT.name())
                            || Objects.equals(e.getContractStatus(), ContractStatus.NEW.name());
                })
                .collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        if (CollectionUtil.isEmpty(contractBaseInfoMap)) {
            return Collections.emptyList();
        }
        // 拼装返回参数
        List<ContractBaseInfoRSP> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(creditMap)) {
            List<ContractBaseInfoRSP> list = this.assembly(contractBaseInfoMap, creditMap, clientContractMap, CreditorDebtorTypeEnum.CREDITOR);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        if (CollectionUtil.isNotEmpty(debtorMap)) {
            List<ContractBaseInfoRSP> list = this.assembly(contractBaseInfoMap, debtorMap, clientContractMap, CreditorDebtorTypeEnum.DEBTOR);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        return result;
//        ProjReviewBaseInfo byId = baseInfoService.getById(projectId);
//        Map<Long, ClientInfo> clientMap = new HashMap<>();
//        if (StrUtil.isNotBlank(byId.getLesseeInfo())) {
//            List<ClientInfo> clientInfoList = JSONUtil.toList(byId.getLesseeInfo(), ClientInfo.class);
//            for (ClientInfo clientInfo : clientInfoList) {
//                clientMap.put(clientInfo.getClientId(), clientInfo);
//            }
//        }
//        if (StrUtil.isNotBlank(byId.getCreditorInfo())) {
//            List<ClientInfo> clientInfoList = JSONUtil.toList(byId.getCreditorInfo(), ClientInfo.class);
//            for (ClientInfo clientInfo : clientInfoList) {
//                clientMap.put(clientInfo.getClientId(), clientInfo);
//            }
//        }
//        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(clientMap.keySet());
//        Map<Long, String> clientNames = id2NameService.clientId2Name(clientMap.keySet());
//        List<ContractBaseInfoRSP> trueContracts = new ArrayList<>();
//        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
//            ContractBaseInfoRSP rsp = new ContractBaseInfoRSP();
//            Long currentClient = contractBaseInfo.getClientId();
//            rsp.setClientType(clientMap.get(currentClient).getClientType());
//            rsp.setClientId(currentClient);
//            rsp.setContractDueDate(contractBaseInfo.getActualFinishDate());
//            rsp.setContractAmount(contractBaseInfo.getApplyCreditAmount());
//            rsp.setClientName(clientNames.get(currentClient));
//            rsp.setContractNo(contractBaseInfo.getContractCode());
//            rsp.setContractStatus(contractBaseInfo.getContractStatus());
//            trueContracts.add(rsp);
//        }
//        return trueContracts;
    }

    private List<ContractBaseInfoRSP> assembly(Map<Long, ContractBaseInfo> contractBaseInfoMap, Map<Long, ClientInfo> personInfoMap, Map<Long, List<Long>> clientContractMap, CreditorDebtorTypeEnum creditorDebtorTypeEnum) {
        List<ContractBaseInfoRSP> result = new LinkedList<>();
        for (Map.Entry<Long, ClientInfo> entry : personInfoMap.entrySet()) {
            Long clientId = entry.getKey();
            ClientInfo personInfo = entry.getValue();
            List<Long> contractIdList = clientContractMap.get(clientId);
            if (CollectionUtil.isEmpty(contractIdList)) {
                continue;
            }
            for (Long contractId : contractIdList) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(contractId);
                if (Objects.isNull(contractBaseInfo)) {
                    continue;
                }
                ContractBaseInfoRSP rsp = new ContractBaseInfoRSP();
                rsp.setClientId(clientId);
                rsp.setClientName(personInfo.getClientName());
                rsp.setClientType(creditorDebtorTypeEnum.name());
                rsp.setClientTypeDisplay(creditorDebtorTypeEnum.display());
                rsp.setContractId(contractId);
                rsp.setContractNo(contractBaseInfo.getContractCode());
                rsp.setContractAmount(contractBaseInfo.getApplyCreditAmount());
                rsp.setContractDueDate(contractBaseInfo.getActualFinishDate());
                rsp.setContractStatus(contractBaseInfo.getContractStatus());
                result.add(rsp);
            }
        }
        return result;
    }

    //合同使用金额
    public Long sumApplyByContractId(List<Long> ids) {
        return leasePriceService.sumApplyByContractId(ids) + aocPriceService.sumApplyByContractId(ids) + factoringPriceService.sumApplyByContractId(ids);
    }

    public ProjReviewPriceDetailRSP oldDetail(Long projectId) {
        ProjReviewPriceDetailRSP res = new ProjReviewPriceDetailRSP();
        try {
            res.setAocPriceDetailRSP(aocPriceLibService.getOldEdition(projectId));
        }catch (MithrasException ignored){}
        try {
            res.setLeasePriceDetailRSP(leasePriceLibService.getOldEdition(projectId));
        }catch (MithrasException ignored){}
        try {
            res.setFactoringPriceDetailRSP(factoringPriceLibService.getOldEdition(projectId));
        }catch (MithrasException ignored){}

        return res;
    }

    public ProjReviewPriceDetailRSP detailVersion(Long projectId, String version) {
        ProjReviewPriceDetailRSP res = new ProjReviewPriceDetailRSP();
        // 处理租赁
        ProjReviewLeasePriceLib leasePriceLib = leasePriceLibMapper.selectOne(Wrappers.<ProjReviewLeasePriceLib>lambdaQuery().eq(ProjReviewLeasePriceLib::getProjectId, projectId).eq(ProjReviewLeasePriceLib::getVersion, version).last("LIMIT 1"));
        res.setLeasePriceDetailRSP(Optional.ofNullable(leasePriceLib).map(leasePriceLibHandler::actualLib2Rsp).orElse(null));
        // 处理保理
        ProjReviewFactoringPriceLib factoringPriceLib = factoringPriceLibMapper.selectOne(Wrappers.<ProjReviewFactoringPriceLib>lambdaQuery().eq(ProjReviewFactoringPriceLib::getProjectId, projectId).eq(ProjReviewFactoringPriceLib::getVersion, version).last("LIMIT 1"));
        res.setFactoringPriceDetailRSP(Optional.ofNullable(factoringPriceLib).map(factoringPriceLibHandler::actualLib2Rsp).orElse(null));
        if (Objects.nonNull(res.getFactoringPriceDetailRSP())) {
            // TODO 拿不到审批时的存续合同
            res.getFactoringPriceDetailRSP().setContracts(getSurvivingContract(projectId));
        }
        // 债权转让
        ProjReviewAocPriceLib aocPriceLib = aocPriceLibMapper.selectOne(Wrappers.<ProjReviewAocPriceLib>lambdaQuery().eq(ProjReviewAocPriceLib::getProjectId, projectId).eq(ProjReviewAocPriceLib::getVersion, version).last("LIMIT 1"));
        res.setAocPriceDetailRSP(Optional.ofNullable(aocPriceLib).map(aocPriceLibHandler::actualLib2Rsp).orElse(null));
        return res;
    }


    public Map<Long, Long> newestPrice(Set<Long> reviewIds) {
        List<ProjReviewAocPriceLib> projReviewAocPriceLibs = aocPriceLibService.listNewestByProjReviewIds(reviewIds);
        List<ProjReviewFactoringPriceLib> projReviewFactoringPriceLibs = factoringPriceLibService.listNewestByProjReviewIds(reviewIds);
        List<ProjReviewLeasePriceLib> projReviewLeasePriceLibs = leasePriceLibService.listNewestByProjReviewIds(reviewIds);

        Map<Long, Long> priceMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(projReviewAocPriceLibs)) {
            for (ProjReviewAocPriceLib projReviewAocPriceLib : projReviewAocPriceLibs) {
                priceMap.put(projReviewAocPriceLib.getProjectId(), LongUtil.null2zero(projReviewAocPriceLib.getProjectApprovalAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projReviewFactoringPriceLibs)) {
            for (ProjReviewFactoringPriceLib projReviewFactoringPriceLib : projReviewFactoringPriceLibs) {
                priceMap.put(projReviewFactoringPriceLib.getProjectId(), LongUtil.null2zero(projReviewFactoringPriceLib.getProjectApprovalAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projReviewLeasePriceLibs)) {
            for (ProjReviewLeasePriceLib projReviewLeasePriceLib : projReviewLeasePriceLibs) {
                priceMap.put(projReviewLeasePriceLib.getProjectId(), LongUtil.null2zero(projReviewLeasePriceLib.getProjectApprovalAmount()));
            }
        }
        return priceMap;
    }

    //按月获取授信金额
    public Map<Long, Long> getReviewApplyCreditByMonth(Set<Long> reviewIds) {
        List<ProjReviewAocPriceLib> projReviewAocPriceLibs = aocPriceLibService.listNewestByProjReviewIds(reviewIds);
        List<ProjReviewFactoringPriceLib> projReviewFactoringPriceLibs = factoringPriceLibService.listNewestByProjReviewIds(reviewIds);
        List<ProjReviewLeasePriceLib> projReviewLeasePriceLibs = leasePriceLibService.listNewestByProjReviewIds(reviewIds);

        Map<Long, Long> priceMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(projReviewAocPriceLibs)) {
            for (ProjReviewAocPriceLib projReviewAocPriceLib : projReviewAocPriceLibs) {
                Long orDefault = priceMap.getOrDefault(projReviewAocPriceLib.getProjectId(), 0L);
                priceMap.put(projReviewAocPriceLib.getProjectId(), orDefault + LongUtil.null2zero(projReviewAocPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projReviewFactoringPriceLibs)) {
            for (ProjReviewFactoringPriceLib projReviewFactoringPriceLib : projReviewFactoringPriceLibs) {
                Long orDefault = priceMap.getOrDefault(projReviewFactoringPriceLib.getProjectId(), 0L);
                priceMap.put(projReviewFactoringPriceLib.getProjectId(), orDefault + LongUtil.null2zero(projReviewFactoringPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projReviewLeasePriceLibs)) {
            for (ProjReviewLeasePriceLib projReviewLeasePriceLib : projReviewLeasePriceLibs) {
                Long orDefault = priceMap.getOrDefault(projReviewLeasePriceLib.getProjectId(), 0L);
                priceMap.put(projReviewLeasePriceLib.getProjectId(), orDefault + LongUtil.null2zero(projReviewLeasePriceLib.getApplyCreditAmount()));
            }
        }
        return priceMap;
    }
}
