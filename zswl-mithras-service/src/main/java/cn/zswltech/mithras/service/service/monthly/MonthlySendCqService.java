package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.service.convert.monthlymanage.MonthlyManageConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.fund.OrganizationType;
import cn.zswltech.mithras.service.enums.monthly.StampDutyTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.third.enums.CQAccountApplicationTypeENUM;
import cn.zswltech.mithras.third.enums.CQCostDescribeENUM;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCost;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyStampDuty;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yangxiong
 * @date 2024/7/31/14:39
 * @description
 */
@Slf4j
@Service
public class MonthlySendCqService {
    private static final String BEGINNING_ITEM_TEXT = "期初余额";
    @Resource
    private MonthlyManageApiService monthlyManageApiService;
    @Resource
    private MonthlyManageConvert monthlyManageConvert;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService fundOrganizationService;


    //同步数据至财务系统
    public void accountApplication(String yearAndMonth, String batchNumber) {
        //实际利率法
        this.sendMonthlyAIR(yearAndMonth, batchNumber);
        //剩余本金法
        this.sendMonthlyRP(yearAndMonth, batchNumber);
        //成本计提
        this.sendMonthlyCost(yearAndMonth, batchNumber);
        //印花税 项目端
        this.sendMonthlyStampDutyProj(yearAndMonth, batchNumber);
        //印花税 资产端
        this.sendMonthlyStampDutyFin(yearAndMonth, batchNumber);
        //这里发送完成任务已经确认
        getBean(MonthlySendCqService.class).callBackApplication(yearAndMonth, batchNumber);
    }

    //回调方法-修改状态
    @Transactional(rollbackFor = Throwable.class)
    public void callBackApplication(String yearAndMonth, String batchNumber) {
        LocalDateTime now = LocalDateTime.now();
        contractIncomeSharingService.update(Wrappers.<ContractIncomeSharing>lambdaUpdate()
                .eq(ContractIncomeSharing::getConfirmBatch, batchNumber)
                .set(ContractIncomeSharing::getIsConfirmed, true)
                .set(ContractIncomeSharing::getConfirmTime, now));
        monthlyStampDutyService.update(Wrappers.<MonthlyStampDuty>lambdaUpdate()
                .eq(MonthlyStampDuty::getConfirmBatch, batchNumber)
                .set(MonthlyStampDuty::getIsConfirmed, true)
                .set(MonthlyStampDuty::getConfirmTime, now));

        List<FundsDailyCost> list = fundsDailyCostService.list(Wrappers.<FundsDailyCost>lambdaQuery().eq(FundsDailyCost::getConfirmBatch, batchNumber));
        if (CollectionUtils.isNotEmpty(list)) {
            LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_MONTH_PATTERN);
            LocalDate endDate = localDate.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
            LocalDate startDate = endDate.plusMonths(-1);
            Map<String, List<FundsDailyCost>> financingIdList = list.stream().collect(Collectors.groupingBy(FundsDailyCost::getType));
            if (CollectionUtils.isNotEmpty(financingIdList)) {
                List<FundsDailyCost> dk = financingIdList.get("DK");
                if (CollectionUtils.isNotEmpty(dk)) {
                    List<Long> dkFinancingIdList = dk.stream().map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
                    fundsDailyCostService.update(Wrappers.<FundsDailyCost>lambdaUpdate()
                            .lt(FundsDailyCost::getInterestDate, endDate)
                            .ge(FundsDailyCost::getInterestDate, startDate)
                            .eq(FundsDailyCost::getType, "DK")
                            .in(FundsDailyCost::getFinancingId, dkFinancingIdList)
                            .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
                            .set(FundsDailyCost::getIsConfirmed, true)
                            .set(FundsDailyCost::getConfirmTime, now));
                }
                List<FundsDailyCost> zr = financingIdList.get("ZR");
                if (CollectionUtils.isNotEmpty(zr)) {
                    List<Long> zrFinancingIdList = zr.stream().map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
                    fundsDailyCostService.update(Wrappers.<FundsDailyCost>lambdaUpdate()
                            .lt(FundsDailyCost::getInterestDate, endDate)
                            .ge(FundsDailyCost::getInterestDate, startDate)
                            .eq(FundsDailyCost::getType, "ZR")
                            .in(FundsDailyCost::getFinancingId, zrFinancingIdList)
                            .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
                            .set(FundsDailyCost::getIsConfirmed, true)
                            .set(FundsDailyCost::getConfirmTime, now));
                }
            }
        }

        //由于现在存储了每一次拉取的数据，所以需要单独修改这写数据的状态
        getBean(MonthlyManagementBaseInfoService.class).updateMiddleTable(yearAndMonth, batchNumber);
    }

    private void sendMonthlyAIR(String yearAndMonth, String batchNumber) {
        //实际利率法
        MonthlyAIRListREQ airReq = new MonthlyAIRListREQ();
        airReq.setYearAndMonth(yearAndMonth);
        airReq.setPage(1);
//        airReq.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
        airReq.setPageSize(Integer.MAX_VALUE);
        airReq.setBatchNumber(batchNumber);
        PageR<MonthlyAIRListRSP> monthlyAIRListRSPPageR = monthlyManageApiService.airList(airReq);
        //实际利率法
        if (ObjectUtil.isNotEmpty(monthlyAIRListRSPPageR) && ObjectUtil.isNotEmpty(monthlyAIRListRSPPageR.getList())) {
            //contractId 实际利率
            Map<Long, List<MonthlyAIRListRSP>> contractId2rsp = monthlyAIRListRSPPageR.getList().stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).collect(Collectors.groupingBy(MonthlyAIRListRSP::getContractId));
            //projReviewId contract
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractId2rsp.keySet());
            Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
            //租金往来方
            Map<Long, String> contractId2clientId = contractTenantryService.listRentConcatAccountByContractId(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
            List<Long> clientIds = new ArrayList<>();
            clientIds.addAll(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
            clientIds.addAll(contractId2clientId.values().stream().map(Long::valueOf).collect(Collectors.toList()));
            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
            monthlyAIRListRSPPageR.getList().forEach(air -> {
                ContractBaseInfo contractBaseInfo = contractId2Bean.get(air.getContractId());
                Client client = clientId2Bean.get(contractId2clientId.get(air.getContractId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(air.getContractId())));
                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    monthlyManageConvert.sendAirAccountApplication(air, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE, orgId2Dept.get(contractBaseInfo.getBizDeptId()), client, yearAndMonth, contractBaseInfo);
                }
            });

            /*proj2Contract.forEach((projReviewId, contracts) -> {
                List<MonthlyAIRListRSP> allMonthlyAir = new ArrayList<>();
                contracts.forEach((contract) -> allMonthlyAir.addAll(contractId2rsp.get(contract.getId())));//
                monthlyManageConvert.sendAirAccountApplication(allMonthlyAir, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE, orgId2Dept.get(contracts.get(0).getBizDeptId()), clientId2Bean.get(contracts.get(0).getClientId()), yearAndMonth);
            });*/
        }
    }

    private void sendMonthlyRP(String yearAndMonth, String batchNumber) {
        MonthlyRPListREQ rpListREQ = new MonthlyRPListREQ();
        rpListREQ.setYearAndMonth(yearAndMonth);
//        rpListREQ.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
        rpListREQ.setPage(1);
        rpListREQ.setPageSize(Integer.MAX_VALUE);
        rpListREQ.setBatchNumber(batchNumber);
        PageR<MonthlyRPListRSP> monthlyRPListRSPPageR = monthlyManageApiService.rpList(rpListREQ);
        //剩余本金法
        if (ObjectUtil.isNotEmpty(monthlyRPListRSPPageR) && ObjectUtil.isNotEmpty(monthlyRPListRSPPageR.getList())) {
            //contractId 实际利率
            Map<Long, List<MonthlyRPListRSP>> contractId2rsp = monthlyRPListRSPPageR.getList().stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).collect(Collectors.groupingBy(MonthlyRPListRSP::getContractId));
            //projReviewId contract
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractId2rsp.keySet());
            Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
            //租金往来方
            Map<Long, String> contractId2clientId = contractTenantryService.listRentConcatAccountByContractId(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
            List<Long> clientIds = new ArrayList<>();
            clientIds.addAll(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
            clientIds.addAll(contractId2clientId.values().stream().map(Long::valueOf).collect(Collectors.toList()));
            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
            monthlyRPListRSPPageR.getList().forEach(rp -> {
                ContractBaseInfo contractBaseInfo = contractId2Bean.get(rp.getContractId());
                Client client = clientId2Bean.get(contractId2clientId.get(rp.getContractId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(rp.getContractId())));
                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    monthlyManageConvert.sendRPAccountApplication(rp, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_REMAINING_PRINCIPAL, orgId2Dept.get(contractBaseInfo.getBizDeptId()), client, yearAndMonth, contractBaseInfo);
                }
            });
           /* proj2Contract.forEach((projReviewId, contracts) -> {
                List<MonthlyRPListRSP> allMonthlyRP = new ArrayList<>();
                contracts.forEach((contract) -> allMonthlyRP.addAll(contractId2rsp.get(contract.getId())));
                monthlyManageConvert.sendRPAccountApplication(allMonthlyRP, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_REMAINING_PRINCIPAL, orgId2Dept.get(contracts.get(0).getBizDeptId()), clientId2Bean.get(contracts.get(0).getClientId()), yearAndMonth);
            });*/
        }
    }

    private void sendMonthlyCost(String yearAndMonth, String batchNumber) {
        //成本计提
        MonthlyCostREQ req = new MonthlyCostREQ();
        req.setYearAndMonth(yearAndMonth);
//        req.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setBatchNumber(batchNumber);
        List<MonthlyCostRSP> monthlyCostRSPS = monthlyManageApiService.costList(req).getList();
        if (ObjectUtil.isNotEmpty(monthlyCostRSPS)) {
            List<Long> dkFinancingIds = monthlyCostRSPS.stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).filter(e -> "DK".equalsIgnoreCase(e.getBusinessType())).map(MonthlyCostRSP::getFinancingId).collect(Collectors.toList());
            List<Long> zrFinancingIds = monthlyCostRSPS.stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).filter(e -> "ZR".equalsIgnoreCase(e.getBusinessType())).map(MonthlyCostRSP::getFinancingId).collect(Collectors.toList());
            Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = null;
            Map<Long, List<FundFinancingCreditRef>> dkFinancingId2Origin = new HashMap<>();
            Map<Long, String> originId2Type = new HashMap<>();
            if (CollectionUtil.isNotEmpty(dkFinancingIds)) {
                fundFinancingId2Bean = financingBaseInfoService.listByIds(dkFinancingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
                dkFinancingId2Origin.putAll(financingCreditRefService.queryBatchByFinancingId(fundFinancingId2Bean.keySet()));
                originId2Type = fundOrganizationService.listByIds(dkFinancingId2Origin.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(FundOrganization::getId, FundOrganization::getOrganizationType, (a, b) -> b));
            }
            Map<Long, FundDirectFinancingBaseInfo> fundDirectFinancingId2Bean = null;
            if (CollectionUtil.isNotEmpty(zrFinancingIds)) {
                fundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(zrFinancingIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            }
            Map<Long, List<FundFinancingPledgeInfo>> dkPledgeMap = new HashMap<>();
            Map<Long, List<FundDirectFinancingPledgeInfo>> zrPledgeMap = new HashMap<>();
            List<Long> contractIds = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(dkFinancingIds)) {
                List<FundFinancingPledgeInfo> list = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                        .in(FundFinancingPledgeInfo::getFinancingId, dkFinancingIds));
                dkPledgeMap = list.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
                contractIds.addAll(list.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            }
            if (ObjectUtil.isNotEmpty(zrFinancingIds)) {
                List<FundDirectFinancingPledgeInfo> list = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .in(FundDirectFinancingPledgeInfo::getFinancingId, zrFinancingIds));
                zrPledgeMap = list.stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
                contractIds.addAll(list.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            }
            //查询合同信息
            Map<Long, ContractBaseInfo> contractId2Bean = new HashMap<>();

            //租金往来方
            Map<Long, String> contractId2clientId = contractTenantryService.listRentConcatAccountByContractId(contractIds);
            List<Long> clientIds = new ArrayList<>();
            clientIds.addAll(contractId2clientId.values().stream().map(Long::valueOf).collect(Collectors.toList()));
            if (!contractIds.isEmpty()) {
                contractId2Bean = contractBaseInfoMapper.selectBatchIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
                clientIds.addAll(contractId2Bean.values().stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
            }
            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));

            List<Long> orgIds = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(fundFinancingId2Bean) && ObjectUtil.isNotEmpty(fundFinancingId2Bean.values())) {
                orgIds.addAll(fundFinancingId2Bean.values().stream().map(FundFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
            }
            if (ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean) && ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean.values())) {
                orgIds.addAll(fundDirectFinancingId2Bean.values().stream().map(FundDirectFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
            }
            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(orgIds, null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
            //区分出直间融类型

            CQAccountApplicationTypeENUM.ACCRUE_FINANCING_COST.getDisplay();
            String description;
            //间融
            for (MonthlyCostRSP monthlyCost : monthlyCostRSPS) {//间融
                if ("DK".equalsIgnoreCase(monthlyCost.getBusinessType())) {
                    FundFinancingBaseInfo baseInfo = fundFinancingId2Bean.get(monthlyCost.getFinancingId());
                    if (ObjectUtil.isNotEmpty(baseInfo)) {
                        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = dkPledgeMap.get(baseInfo.getId());
                        List<FundFinancingCreditRef> fundFinancingCreditRefs = dkFinancingId2Origin.get(monthlyCost.getFinancingId());
                        if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
                            //有回租取回租
                            ContractBaseInfo contractBaseInfo = null;
                            ContractBaseInfo tempContract;
                            for(FundFinancingPledgeInfo pledgeInfo : fundFinancingPledgeInfos) {
                                tempContract = contractId2Bean.get(pledgeInfo.getContractId());
                                if (BeanUtil.isEmpty(contractBaseInfo)) {
                                    contractBaseInfo = tempContract;
                                } else if (ObjectUtil.isNotEmpty(tempContract) && ObjectUtil.equals(tempContract.getLeaseType(), LeaseType.zhi_zu.name())) {
                                    contractBaseInfo = tempContract;
                                    break;
                                }
                            }
                            if (ObjectUtil.isNotEmpty(fundFinancingCreditRefs)) {
                                String originType = null;
                                for(FundFinancingCreditRef creditRef : fundFinancingCreditRefs) {
                                    if (ObjectUtil.isNotEmpty(originId2Type.get(creditRef.getOrganizationId()))) {
                                        originType = originId2Type.get(creditRef.getOrganizationId());
                                    }
                                    if(ObjectUtil.equals(originType,  OrganizationType.BANK.name())) {
                                        break;
                                    }
                                }

                                description = CQCostDescribeENUM.getCqBusinessType(monthlyCost.getBusinessType(), originType, baseInfo.getTimeLimitType()).getDisplay();
                            } else {
                                description = null;
                            }

                            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                                Client client = clientId2Bean.get(contractId2clientId.get(contractBaseInfo.getId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(contractBaseInfo.getId())));
                                monthlyManageConvert.sendCostList(monthlyCost, orgId2Dept.get(baseInfo.getDeptId()), yearAndMonth, client, contractBaseInfo, description);
                            }
                        }
                    }
                } else {
                    FundDirectFinancingBaseInfo baseInfo = fundDirectFinancingId2Bean.get(monthlyCost.getFinancingId());
                    if (ObjectUtil.isNotEmpty(baseInfo)) {
                        List<FundDirectFinancingPledgeInfo> fundFinancingPledgeInfos = zrPledgeMap.get(baseInfo.getId());
                        if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
                            //有回租取回租
                            ContractBaseInfo contractBaseInfo = null;
                            ContractBaseInfo tempContract;
                            for(FundDirectFinancingPledgeInfo pledgeInfo : fundFinancingPledgeInfos) {
                                tempContract = contractId2Bean.get(pledgeInfo.getContractId());
                                if (BeanUtil.isEmpty(contractBaseInfo)) {
                                    contractBaseInfo = tempContract;
                                } else if (ObjectUtil.isNotEmpty(tempContract) && ObjectUtil.equals(tempContract.getLeaseType(), LeaseType.zhi_zu.name())) {
                                    contractBaseInfo = tempContract;
                                    break;
                                }
                            }
                            if (ObjectUtil.isNotEmpty(baseInfo)) {
                                description = CQCostDescribeENUM.getCqBusinessType(monthlyCost.getBusinessType(), baseInfo.getDirectFinancingType(), null).getDisplay();
                            } else {
                                description = null;
                            }
                            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                                Client client = clientId2Bean.get(contractId2clientId.get(contractBaseInfo.getId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(contractBaseInfo.getId())));
                                monthlyManageConvert.sendCostList(monthlyCost, orgId2Dept.get(baseInfo.getDeptId()), yearAndMonth, client, contractBaseInfo, description);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendMonthlyStampDutyProj(String yearAndMonth, String batchNumber) {
        //印花税 项目端
        MonthlyStampDutyProjREQ projREQ = new MonthlyStampDutyProjREQ(yearAndMonth, YesOrNoNumberEnum.YES.getCode());
        projREQ.setPage(1);
        projREQ.setPageSize(Integer.MAX_VALUE);
        projREQ.setBatchNumber(batchNumber);
        PageR<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPPageR = monthlyManageApiService.projPage(projREQ);
        if (ObjectUtil.isNotEmpty(monthlyStampDutyProjRSPPageR) && ObjectUtil.isNotEmpty(monthlyStampDutyProjRSPPageR.getList())) {
            List<MonthlyStampDutyProjRSP> dutyProjRSPS = monthlyStampDutyProjRSPPageR.getList();
            //合同信息
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(dutyProjRSPS.stream().map(MonthlyStampDutyProjRSP::getContractId).collect(Collectors.toList()));
            if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
                Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
                Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
                //租金往来方
                Map<Long, String> contractId2clientId = contractTenantryService.listRentConcatAccountByContractId(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
                List<Long> clientIds = new ArrayList<>();
                clientIds.addAll(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
                clientIds.addAll(contractId2clientId.values().stream().map(Long::valueOf).collect(Collectors.toList()));
                Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
                dutyProjRSPS.stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).forEach(dutyProjRSP -> {
                    ContractBaseInfo contractBaseInfo = contractId2Bean.get(dutyProjRSP.getContractId());
                    Client client = clientId2Bean.get(contractId2clientId.get(dutyProjRSP.getContractId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(dutyProjRSP.getContractId())));
                    if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                        monthlyManageConvert.sendStampDuty(dutyProjRSP, orgId2Dept.get(contractBaseInfo.getBizDeptId()), client, yearAndMonth, contractBaseInfo);
                    }
                });
            }
        }
    }

    private void sendMonthlyStampDutyFin(String yearAndMonth, String batchNumber) {
        //印花税 资产端 StampDutyTypeEnum
        MonthlyStampDutyFinREQ stampDutyFinReq = new MonthlyStampDutyFinREQ();
        stampDutyFinReq.setYearAndMonth(yearAndMonth);
//        stampDutyFinReq.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
        stampDutyFinReq.setPage(1);
        stampDutyFinReq.setPageSize(Integer.MAX_VALUE);
        stampDutyFinReq.setBatchNumber(batchNumber);
        PageR<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPPageR = monthlyManageApiService.finPage(stampDutyFinReq);
        if (ObjectUtil.isNotEmpty(monthlyStampDutyFinRSPPageR) && ObjectUtil.isNotEmpty(monthlyStampDutyFinRSPPageR.getList())) {
            List<MonthlyStampDutyFinRSP> dutyProjRSPS = monthlyStampDutyFinRSPPageR.getList();
            //合同信息
            List<Long> dkFinancingIds = dutyProjRSPS.stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).filter(e -> StampDutyTypeEnum.FIN_FIN.name().equalsIgnoreCase(e.getType())).map(MonthlyStampDutyFinRSP::getFinancingId).collect(Collectors.toList());
            List<Long> zrFinancingIds = dutyProjRSPS.stream().filter(e -> !ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), e.getIsEffect())).filter(e -> StampDutyTypeEnum.FIN_DIRECT_FIN.name().equalsIgnoreCase(e.getType())).map(MonthlyStampDutyFinRSP::getFinancingId).collect(Collectors.toList());
            Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = new HashMap<>();
            Map<Long, FundDirectFinancingBaseInfo> fundDirectFinancingId2Bean = new HashMap<>();
            Map<Long, List<FundFinancingPledgeInfo>> dkPledgeMap = new HashMap<>();
            Map<Long, List<FundDirectFinancingPledgeInfo>> zrPledgeMap = new HashMap<>();
            List<Long> contractIds = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(dkFinancingIds)) {
                fundFinancingId2Bean = financingBaseInfoService.listByIds(dkFinancingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
                List<FundFinancingPledgeInfo> list = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                        .in(FundFinancingPledgeInfo::getFinancingId, dkFinancingIds));
                dkPledgeMap = list.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
                contractIds.addAll(list.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            }
            if (ObjectUtil.isNotEmpty(zrFinancingIds)) {
                fundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(zrFinancingIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
                List<FundDirectFinancingPledgeInfo> list = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .in(FundDirectFinancingPledgeInfo::getFinancingId, zrFinancingIds));
                zrPledgeMap = list.stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
                contractIds.addAll(list.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            }
            //查询合同信息
            Map<Long, ContractBaseInfo> contractId2Bean = new HashMap<>();
            //租金往来方
            Map<Long, String> contractId2clientId = contractTenantryService.listRentConcatAccountByContractId(contractIds);
            List<Long> clientIds = new ArrayList<>();
            clientIds.addAll(contractId2clientId.values().stream().map(Long::valueOf).collect(Collectors.toList()));
            if (!contractIds.isEmpty()) {
                contractId2Bean = contractBaseInfoMapper.selectBatchIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
                clientIds.addAll(contractId2Bean.values().stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
            }
            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));

            List<Long> orgIds = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(fundFinancingId2Bean) && ObjectUtil.isNotEmpty(fundFinancingId2Bean.values())) {
                orgIds.addAll(fundFinancingId2Bean.values().stream().map(FundFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
            }
            if (ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean) && ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean.values())) {
                orgIds.addAll(fundDirectFinancingId2Bean.values().stream().map(FundDirectFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
            }
            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(orgIds, null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));

            for (MonthlyStampDutyFinRSP dutyProjRSP : dutyProjRSPS) {
                ContractBaseInfo contractBaseInfo = null;
                //融资
                if (StampDutyTypeEnum.FIN_FIN.name().equalsIgnoreCase(dutyProjRSP.getType())) {
                    List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = dkPledgeMap.get(dutyProjRSP.getFinancingId());
                    if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
                        contractBaseInfo = contractId2Bean.get(fundFinancingPledgeInfos.get(0).getContractId());
                    }
                } else {
                    List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfo = zrPledgeMap.get(dutyProjRSP.getFinancingId());
                    if (ObjectUtil.isNotEmpty(directFinancingPledgeInfo)) {
                        contractBaseInfo = contractId2Bean.get(directFinancingPledgeInfo.get(0).getContractId());
                    }
                }
                if (contractBaseInfo != null) {
                    Client client = clientId2Bean.get(contractId2clientId.get(contractBaseInfo.getId()) == null ? contractBaseInfo.getClientId() : Long.valueOf(contractId2clientId.get(contractBaseInfo.getId())));
                    monthlyManageConvert.sendFinPage(dutyProjRSP, orgId2Dept.get(contractBaseInfo.getBizDeptId()), client, yearAndMonth, contractBaseInfo);
                }
            }
        }
    }
}
