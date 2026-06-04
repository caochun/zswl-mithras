package cn.zswltech.mithras.service.service.projpricing;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projpricing.price.*;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingAocPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.ProjPricingLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingAocPriceLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingFactoringPriceLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingLeasePriceLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/3 17:29
 */
@Service
@Slf4j
public class ProjPricingPriceService {

    @Resource
    private ProjPricingBaseInfoService baseInfoService;
    @Resource
    private ProjPricingLeasePriceService leasePriceService;
    @Resource
    private ProjPricingFactoringPriceService factoringPriceService;
    @Resource
    private ProjPricingAocPriceService aocPriceService;
    @Resource
    private ProjPricingLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjPricingFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjPricingAocPriceLibService aocPriceLibService;
    @Resource
    private ProjPricingPriceConverter priceConverter;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ProjPricingLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjPricingFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjPricingAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ProjPricingLeasePriceLibHandler leasePriceLibHandler;
    @Resource
    private ProjPricingFactoringPriceLibHandler factoringPriceLibHandler;
    @Resource
    private ProjPricingAocPriceLibHandler aocPriceLibHandler;

    public void saveIrr(Long projPricingId, Integer irr) {
        ProjPricingBaseInfo projPricingBaseInfo = baseInfoService.getById(projPricingId);
        Assert.notNull(projPricingBaseInfo, () -> MithrasException.newException("项目定价信息不存在"));

        if (Objects.equals(ProjectBizType.BL.name(), projPricingBaseInfo.getBizType())) {
            factoringPriceService.saveIrr(projPricingId, irr);
        } else if (Objects.equals(ProjectBizType.ZR.name(), projPricingBaseInfo.getBizType())) {
            aocPriceService.saveIrr(projPricingId, irr);
        } else {
            leasePriceService.saveIrr(projPricingId, irr);
        }
    }


    /**
     * 创建定价时添加报价方案
     *
     * @param price
     */
    public void add(BaseModel price) {
        price.setCreateTime(null);
        price.setUpdateTime(null);
        price.setUpdateBy(null);
        if (price instanceof ProjPricingAocPrice) {
            aocPriceService.add((ProjPricingAocPrice) price);
        } else if (price instanceof ProjPricingFactoringPrice) {
            factoringPriceService.add((ProjPricingFactoringPrice) price);
        } else if (price instanceof ProjPricingLeasePrice) {
            leasePriceService.add((ProjPricingLeasePrice) price);
        }
    }

    /**
     * 复合更新报价接口
     *
     * @param req
     */
    @Transactional(rollbackFor = Throwable.class)
    public void modify(ProjPricingPriceModifyREQ req) {
        // 集团授信项目要校验剩余可用额度
        ProjPricingBaseInfo projPricingBaseInfo = baseInfoService.getById(req.getProjectId());
        if (Objects.isNull(projPricingBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 如果有合同，校验金额
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(projPricingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            long totalContractAmount = contractBaseInfoList.stream().mapToLong(ContractBaseInfo::getApplyCreditAmount).sum();
            Assert.isTrue(req.getAmount() >= totalContractAmount, () -> MithrasException.newException("项目授信金额不能小于所有合同金额之和"));
        }
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projPricingBaseInfo.getRelationDataType())) {
            // 集团授信发起的评审要锁操作
            String gcrRemainAmountLockKey = CacheEnum.GROUP_CREDIT_PRICING_REMAIN_AMOUNT_LOCK.buildKey(projPricingBaseInfo.getGroupCreditReviewId());
            boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(gcrRemainAmountLockKey, 1000L);
            if (!getLockFlag) {
                throw new MithrasException(CONCURRENT_OPERATION);
            }
            try {
                Long remainCreditAmount = groupCreditReviewService.getRemainCreditAmount(projPricingBaseInfo.getGroupCreditReviewId());
                // 修改报价方案的上限需要把原值释放出来
                remainCreditAmount = remainCreditAmount + Optional.ofNullable(projPricingBaseInfo.getDeclaredAmount()).orElse(0L);
                if (remainCreditAmount <= 0) {
                    throw new MithrasException("该授信可用额度已小于等于0，不可再进行操作");
                }
                Long applyCreditAmount = Optional.ofNullable(req.getAocPriceModifyREQ())
                        .map(ProjPricingAocPriceModifyREQ::getApplyCreditAmount)
                        .orElse(Optional.ofNullable(req.getFactoringPriceModifyREQ())
                                .map(ProjPricingFactoringPriceModifyREQ::getApplyCreditAmount)
                                .orElse(Optional.ofNullable(req.getLeasePriceModifyREQ()).map(ProjPricingLeasePriceModifyREQ::getApplyCreditAmount).orElse(0L))
                        );
                if (applyCreditAmount > remainCreditAmount) {
                    throw new MithrasException("定价金额需大于0且小于等于授信剩余可用金额" + Util.mithrasLong2BigDecimal(remainCreditAmount).toPlainString());
                }
                doModify(req);
            } finally {
                redisDistLock.unlock(gcrRemainAmountLockKey);
            }
        } else {
            doModify(req);
        }

    }

    public void doModify(ProjPricingPriceModifyREQ req) {
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


    /**
     * 查询复合接口
     *
     * @param projectId
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ProjPricingPriceDetailRSP detail(Long projectId) {
        ProjPricingPriceDetailRSP res = new ProjPricingPriceDetailRSP();
        // 处理租赁
        res.setLeasePriceDetailRSP(priceConverter.entityToLeaseRsp(leasePriceService.getByProjectId(projectId)));
        // 处理保理
        ProjPricingFactoringPriceRSP pricingFactoringPriceRSP =
                priceConverter.entityToFactoringRsp(factoringPriceService.getByProjectId(projectId));
        if (pricingFactoringPriceRSP != null) {
            // 有存续租赁授信，则查询合同列表
//            ProjReviewBaseInfo byId = baseInfoService.getById(projectId);
//            pricingFactoringPriceRSP.setContracts(contractService.contractMock(byId.getClientId()));
            pricingFactoringPriceRSP.setContracts(getSurvivingContract(projectId));
        }
        res.setFactoringPriceDetailRSP(pricingFactoringPriceRSP);
        // 处理债权转让
        res.setAocPriceDetailRSP(priceConverter.entityToAocRsp(aocPriceService.getByProjectId(projectId)));
        return res;
    }

    public List<ContractBaseInfoRSP> getSurvivingContract(Long projectId) {
        ProjPricingBaseInfo projPricingBaseInfo = baseInfoService.getById(projectId);
        if (!Objects.equals(projPricingBaseInfo.getBizType(), ProjectBizType.BL.name())) {
            return Collections.emptyList();
        }
        Map<Long, ClientInfo> creditMap;
        Map<Long, ClientInfo> debtorMap;
        if (StrUtil.isNotBlank(projPricingBaseInfo.getCreditorInfo())) {
            creditMap = JSONUtil.toList(projPricingBaseInfo.getCreditorInfo(), ClientInfo.class).stream().collect(Collectors.toMap(ClientInfo::getClientId, e -> e));
        } else {
            creditMap = Collections.emptyMap();
        }
        if (StrUtil.isNotBlank(projPricingBaseInfo.getDebtorInfo())) {
            debtorMap = JSONUtil.toList(projPricingBaseInfo.getDebtorInfo(), ClientInfo.class).stream()
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
//    public Long sumApplyByContractId(List<Long> ids) {
//        return leasePriceService.sumApplyByContractId(ids) + aocPriceService.sumApplyByContractId(ids) + factoringPriceService.sumApplyByContractId(ids);
//    }

    public ProjPricingPriceDetailRSP oldDetail(Long projectId) {
        ProjPricingPriceDetailRSP res = new ProjPricingPriceDetailRSP();
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

    public ProjPricingPriceDetailRSP detailVersion(Long projectId, String version) {
        ProjPricingPriceDetailRSP res = new ProjPricingPriceDetailRSP();
        // 处理租赁
        ProjPricingLeasePriceLib leasePriceLib = leasePriceLibMapper.selectOne(Wrappers.<ProjPricingLeasePriceLib>lambdaQuery().eq(ProjPricingLeasePriceLib::getProjectId, projectId).eq(ProjPricingLeasePriceLib::getVersion, version).last("LIMIT 1"));
        res.setLeasePriceDetailRSP(Optional.ofNullable(leasePriceLib).map(leasePriceLibHandler::actualLib2Rsp).orElse(null));
        // 处理保理
        ProjPricingFactoringPriceLib factoringPriceLib = factoringPriceLibMapper.selectOne(Wrappers.<ProjPricingFactoringPriceLib>lambdaQuery().eq(ProjPricingFactoringPriceLib::getProjectId, projectId).eq(ProjPricingFactoringPriceLib::getVersion, version).last("LIMIT 1"));
        res.setFactoringPriceDetailRSP(Optional.ofNullable(factoringPriceLib).map(factoringPriceLibHandler::actualLib2Rsp).orElse(null));
        if (Objects.nonNull(res.getFactoringPriceDetailRSP())) {
            // TODO 拿不到审批时的存续合同
            res.getFactoringPriceDetailRSP().setContracts(getSurvivingContract(projectId));
        }
        // 债权转让
        ProjPricingAocPriceLib aocPriceLib = aocPriceLibMapper.selectOne(Wrappers.<ProjPricingAocPriceLib>lambdaQuery().eq(ProjPricingAocPriceLib::getProjectId, projectId).eq(ProjPricingAocPriceLib::getVersion, version).last("LIMIT 1"));
        res.setAocPriceDetailRSP(Optional.ofNullable(aocPriceLib).map(aocPriceLibHandler::actualLib2Rsp).orElse(null));
        return res;
    }


    public Map<Long, Long> newestPrice(Set<Long> reviewIds) {
        List<ProjPricingAocPriceLib> projPricingAocPriceLibs = aocPriceLibService.listNewestByProjPricingIds(reviewIds);
        List<ProjPricingFactoringPriceLib> projPricingFactoringPriceLibs = factoringPriceLibService.listNewestByProjPricingIds(reviewIds);
        List<ProjPricingLeasePriceLib> projPricingLeasePriceLibs = leasePriceLibService.listNewestByProjPricingIds(reviewIds);

        Map<Long, Long> priceMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(projPricingAocPriceLibs)) {
            for (ProjPricingAocPriceLib projPricingAocPriceLib : projPricingAocPriceLibs) {
                priceMap.put(projPricingAocPriceLib.getProjectId(), LongUtil.null2zero(projPricingAocPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projPricingFactoringPriceLibs)) {
            for (ProjPricingFactoringPriceLib projPricingFactoringPriceLib : projPricingFactoringPriceLibs) {
                priceMap.put(projPricingFactoringPriceLib.getProjectId(), LongUtil.null2zero(projPricingFactoringPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projPricingLeasePriceLibs)) {
            for (ProjPricingLeasePriceLib projPricingLeasePriceLib : projPricingLeasePriceLibs) {
                priceMap.put(projPricingLeasePriceLib.getProjectId(), LongUtil.null2zero(projPricingLeasePriceLib.getApplyCreditAmount()));
            }
        }
        return priceMap;
    }
}
