package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.AccountReq;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.CardName;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.dashboard.DashboardCardGroupEnum;
import cn.zswltech.mithras.service.enums.projestablish.FactoringType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projestablish.ZrType;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.dashboard.DashboardProjectInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.client.DashboardClientBasicDTO;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.dashboard.*;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientLifeCycleService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @ClassName DashboardClientOverviewService
 * @Description TODO
 * @Date 2024/6/18 4:51 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class DashboardClientOverviewService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientLifeCycleService clientLifeCycleService;
    @Resource
    private DashboardProjectInfoMapper dashboardProjectInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    private static final String WAN = "万元";

    public List<DashboardClientOverviewStatisticsRSP> statisticsList() throws ExecutionException, InterruptedException {
        LocalDate now = LocalDate.now();  // 获取当前日期
        // 获取月初日期
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        // 获取月末日期
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<DashboardClientOverviewStatisticsRSP> rsps = new ArrayList<>();
        //所有客户
        CompletableFuture<PageR<DashboardClientOverviewAllRSP>> allClients = CompletableFuture.supplyAsync(() -> allPageList(DashboardClientOverviewAllREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).clientStatus(Collections.singletonList(ClientStatus.TAKE_EFFECT.name())).build()));
        //所有客户-本月新增
        CompletableFuture<PageR<DashboardClientOverviewAllRSP>> allAddClients = CompletableFuture.supplyAsync(() -> allPageList(DashboardClientOverviewAllREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).clientStatus(Collections.singletonList(ClientStatus.TAKE_EFFECT.name())).createFrom(firstDayOfMonth).createTo(lastDayOfMonth).build()));
        //存续客户明细
        CompletableFuture<PageR<DashboardClientOverviewSurvivalRSP>> survivalClients = CompletableFuture.supplyAsync(() -> survivalPageList(DashboardClientOverviewSurvivalREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
        //存续客户明细-本月新增
        CompletableFuture<PageR<DashboardClientOverviewSurvivalRSP>> survivalAddClients = CompletableFuture.supplyAsync(() -> survivalPageList(DashboardClientOverviewSurvivalREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).createFrom(firstDayOfMonth).createTo(lastDayOfMonth).build()));
        //三个月结清客户
        CompletableFuture<PageR<DashboardClientOverviewSettleInThreeMonthRSP>> settleInThreeMonthClients = CompletableFuture.supplyAsync(() -> settleInThreeMonthPageList(DashboardClientOverviewSettleInThreeMonthREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
        //三个月结清客户-本月新增
        CompletableFuture<PageR<DashboardClientOverviewSettleInThreeMonthRSP>> settleInThreeMonthAddClients = CompletableFuture.supplyAsync(() -> settleInThreeMonthPageList(DashboardClientOverviewSettleInThreeMonthREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).queryDateFrom(firstDayOfMonth).queryDateTo(lastDayOfMonth).build()));
        //逾期客户 + 逾期客户本月新增
        DashboardClientOverviewOverdueREQ clientOverviewOverdueReq1 = DashboardClientOverviewOverdueREQ.builder()
                .accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build();
        clientOverviewOverdueReq1.setPage(1);
        clientOverviewOverdueReq1.setPageSize(5000);
        DashboardClientOverviewStatisticsRSP overdueRsp = new DashboardClientOverviewStatisticsRSP();
        CompletableFuture<PageR<DashboardClientOverviewOverdueRSP>> overduePage1 = CompletableFuture.supplyAsync(() ->
                overduePageList(clientOverviewOverdueReq1));
        List<DashboardClientOverviewOverdueRSP> list1 = overduePage1.get().getList();
        overdueRsp.setGroupCode(DashboardCardGroupEnum.CLIENT_OVERDUE.name());
        overdueRsp.setGroup(DashboardCardGroupEnum.CLIENT_OVERDUE.getDisplay());
        overdueRsp.setQuantity(CollUtil.isNotEmpty(list1) ? (long)list1.stream().map(DashboardClientOverviewOverdueRSP::getClientId).collect(Collectors.toSet()).size() : 0);
        DashboardClientOverviewOverdueREQ clientOverviewOverdueReq2 = DashboardClientOverviewOverdueREQ.builder()
                .accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class))
                .beginTime(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()))
                .build();
        clientOverviewOverdueReq2.setPage(1);
        clientOverviewOverdueReq2.setPageSize(5000);
        CompletableFuture<PageR<DashboardClientOverviewOverdueRSP>> overduePage2 = CompletableFuture.supplyAsync(() ->
                overduePageList(clientOverviewOverdueReq2));
        List<DashboardClientOverviewOverdueRSP> list2 = overduePage2.get().getList();
        overdueRsp.setIncrementThisMonth(CollUtil.isNotEmpty(list2) ? (long) list2.stream().map(DashboardClientOverviewOverdueRSP::getClientId).collect(Collectors.toSet()).size() : 0);
        //已结清客户
        CompletableFuture<PageR<DashboardClientOverviewSettledRSP>> settleClients = CompletableFuture.supplyAsync(() -> settledPageList(DashboardClientOverviewSettledREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
        CompletableFuture<PageR<DashboardClientOverviewSettledRSP>> settleAddClients = CompletableFuture.supplyAsync(() -> settledPageList(DashboardClientOverviewSettledREQ.builder().accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).dealLineFrom(firstDayOfMonth).dealLineTo(lastDayOfMonth).build()));

        //所有客户
        PageR<DashboardClientOverviewAllRSP> allClientsRsp = allClients.get();
        PageR<DashboardClientOverviewAllRSP> allAddClientsRsp = allAddClients.get();
        rsps.add(DashboardClientOverviewStatisticsRSP.builder()
                .group(DashboardCardGroupEnum.CLIENT_ALL.display())
                .groupCode(DashboardCardGroupEnum.CLIENT_ALL.name())
                .quantity(allClientsRsp == null ? 0L : allClientsRsp.getTotal())
                .incrementThisMonth(allAddClientsRsp == null ? 0L : allAddClientsRsp.getTotal())
                .build());
        //存续客户
        PageR<DashboardClientOverviewSurvivalRSP> survivalClientsRsp = survivalClients.get();
        PageR<DashboardClientOverviewSurvivalRSP> survivalAddClientsRsp = survivalAddClients.get();
        rsps.add(DashboardClientOverviewStatisticsRSP.builder()
                .group(DashboardCardGroupEnum.CLIENT_SURVIVAL.display())
                .groupCode(DashboardCardGroupEnum.CLIENT_SURVIVAL.name())
                .quantity(survivalClientsRsp == null ? 0L : survivalClientsRsp.getTotal())
                .incrementThisMonth(survivalAddClientsRsp == null ? 0L : survivalAddClientsRsp.getTotal())
                .build());

        //三个月结清客户
        PageR<DashboardClientOverviewSettleInThreeMonthRSP> settleInThreeMonthClientsRsp = settleInThreeMonthClients.get();
        PageR<DashboardClientOverviewSettleInThreeMonthRSP> settleInThreeMonthAddClientsRsp = settleInThreeMonthAddClients.get();
        rsps.add(DashboardClientOverviewStatisticsRSP.builder()
                .group(DashboardCardGroupEnum.CLIENT_THREE_MONTH_SETTLE.display())
                .groupCode(DashboardCardGroupEnum.CLIENT_THREE_MONTH_SETTLE.name())
                .quantity(settleInThreeMonthClientsRsp == null ? 0L : settleInThreeMonthClientsRsp.getTotal())
                .incrementThisMonth(settleInThreeMonthAddClientsRsp == null ? 0L : settleInThreeMonthAddClientsRsp.getTotal())
                .build());
        //逾期客户
        rsps.add(overdueRsp);

        //已结清客户
        PageR<DashboardClientOverviewSettledRSP> settleClientsRsp = settleClients.get();
        PageR<DashboardClientOverviewSettledRSP> settleAddClientsRsp = settleAddClients.get();
        rsps.add(DashboardClientOverviewStatisticsRSP.builder()
                .group(DashboardCardGroupEnum.CLIENT_SETTLE.display())
                .groupCode(DashboardCardGroupEnum.CLIENT_SETTLE.name())
                .quantity(settleClientsRsp == null ? 0L : settleClientsRsp.getTotal())
                .incrementThisMonth(settleAddClientsRsp == null ? 0L : settleAddClientsRsp.getTotal())
                .build());

        return rsps;
    }

    //业务工作台-客户视图-客户一览-所有客户明细
    public PageR<DashboardClientOverviewAllRSP> allPageList(DashboardClientOverviewAllREQ req) {
        StopWatch st = new StopWatch();
        st.start("所有客户明细");
        //查询所有客户
        DashboardClientOverviewAllQuery dashboardClientOverviewAllQuery = BeanUtil.copyProperties(req, DashboardClientOverviewAllQuery.class);
        dashboardClientOverviewAllQuery.setAccountVO(BeanUtil.copyProperties(req.getAccountVo(), AccountVO.class));
        dashboardClientOverviewAllQuery.fillAuthQuery();
        Page<DashboardClientBasicDTO> dashboardClientBasicDTOPage = clientMapper.dashboardAllPageList(new Page(req.getPage(), req.getPageSize()), dashboardClientOverviewAllQuery);
        List<DashboardClientOverviewAllRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(dashboardClientBasicDTOPage.getRecords(), DashboardClientOverviewAllRSP.class);
        if (ObjectUtil.isEmpty(dashboardClientOverviewAllRSPS)) {
            return null;
        }
        buildDashboardClientBasicRsp(dashboardClientOverviewAllRSPS);
        Set<Long> clientIdSet = dashboardClientOverviewAllRSPS.stream().map(DashboardClientOverviewAllRSP::getClientId).collect(Collectors.toSet());
        //剩余本金，存量敞口，授信总金额
        List<Client> clients = clientService.listByIds(clientIdSet);
        List<ClientListRSP> list = BeanUtil.copyToList(clients, ClientListRSP.class);
        clientService.fillOtherInfo(list, Boolean.FALSE);
        Map<Long, ClientListRSP> clientMap = list.stream().collect(Collectors.toMap(ClientListRSP::getId, e -> e));
        ClientListRSP clientListRSP;
        for (DashboardClientOverviewAllRSP rsp : dashboardClientOverviewAllRSPS) {
            clientListRSP = clientMap.get(rsp.getClientId());
            if (ObjectUtil.isNotEmpty(clientListRSP)) {
                rsp.setCreditAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getApplyCreditAmount()).toString()).toString(), WAN));
                rsp.setPrincipalBalanceAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getLastPrincipal()).toString()).toString(), WAN));
                rsp.setStockRiskExposure(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getApplyCreditAmount()).toString()).toString(), WAN));
            }
        }
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(dashboardClientOverviewAllRSPS, dashboardClientBasicDTOPage.getTotal());
    }

    //存续客户明细
    public PageR<DashboardClientOverviewSurvivalRSP> survivalPageList(DashboardClientOverviewSurvivalREQ req) {
        StopWatch st = new StopWatch();
        st.start("存续客户明细");
        //存续客户
        Set<Long> targetClientIds = clientLifeCycleService.getTargetClientIds(CardName.EXISTING.name());
        if (ObjectUtil.isEmpty(targetClientIds)) {
            return null;
        }
        DashboardClientOverviewAllQuery dashboardClientOverviewAllQuery = BeanUtil.copyProperties(req, DashboardClientOverviewAllQuery.class);
        dashboardClientOverviewAllQuery.setAccountVO(BeanUtil.copyProperties(req.getAccountVo(), AccountVO.class));
        dashboardClientOverviewAllQuery.fillAuthQuery();
        dashboardClientOverviewAllQuery.setLimitClientIds(targetClientIds);
        Page<DashboardClientBasicDTO> dashboardClientBasicDTOPage = clientMapper.dashboardAllPageList(new Page(req.getPage(), req.getPageSize()), dashboardClientOverviewAllQuery);
        List<DashboardClientOverviewSurvivalRSP> dashboardClientOverviewSurvivalRSPS = BeanUtil.copyToList(dashboardClientBasicDTOPage.getRecords(), DashboardClientOverviewSurvivalRSP.class);
        if (ObjectUtil.isEmpty(dashboardClientOverviewSurvivalRSPS)) {
            return null;
        }
        buildDashboardClientBasicRsp(dashboardClientOverviewSurvivalRSPS);
        Set<Long> clientIdSet = dashboardClientOverviewSurvivalRSPS.stream().map(DashboardClientOverviewSurvivalRSP::getClientId).collect(Collectors.toSet());
        //剩余本金，存量敞口，授信总金额
        List<Client> clients = clientService.listByIds(clientIdSet);
        List<ClientListRSP> list = BeanUtil.copyToList(clients, ClientListRSP.class);
        clientService.fillOtherInfo(list, Boolean.FALSE);
        Map<Long, ClientListRSP> clientMap = list.stream().collect(Collectors.toMap(ClientListRSP::getId, e -> e));
        ClientListRSP clientListRSP;
        for (DashboardClientOverviewSurvivalRSP rsp : dashboardClientOverviewSurvivalRSPS) {
            clientListRSP = clientMap.get(rsp.getClientId());
            if (ObjectUtil.isNotEmpty(clientListRSP)) {
                rsp.setCreditAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getApplyCreditAmount()).toString()).toString(), WAN));
                rsp.setPrincipalBalanceAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getLastPrincipal()).toString()).toString(), WAN));
                rsp.setStockRiskExposure(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getStockRiskExposure()).toString()).toString(), WAN));
            }
        }
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(dashboardClientOverviewSurvivalRSPS, dashboardClientBasicDTOPage.getTotal());
    }

    //三个月结清客户  cn.zswltech.mithras.service.mapper.dashboard.DashboardProjectInfoMapper#listSettleInThreeMonth
    public PageR<DashboardClientOverviewSettleInThreeMonthRSP> settleInThreeMonthPageList(DashboardClientOverviewSettleInThreeMonthREQ req) {
        StopWatch st = new StopWatch();
        st.start("三个月结清客户");
        DashboardProjectInfoSettleInThreeMonthQuery settleInThreeMonthQuery = BeanUtil.copyProperties(req, DashboardProjectInfoSettleInThreeMonthQuery.class);
        //todo 日期要可控制
        if(ObjectUtil.isEmpty(settleInThreeMonthQuery.getQueryDateFrom())){
            settleInThreeMonthQuery.setQueryDateFrom(LocalDate.now());
        }
        if(ObjectUtil.isEmpty(settleInThreeMonthQuery.getQueryDateTo())){
            settleInThreeMonthQuery.setQueryDateTo(LocalDate.now().plusDays(90));
        }
        List<DashboardProjectInfoSettleInThreeMonthResult> dashboardProjectInfoSettleInThreeMonthResults = dashboardProjectInfoMapper.listSettleInThreeMonth(settleInThreeMonthQuery);
        if (ObjectUtil.isEmpty(dashboardProjectInfoSettleInThreeMonthResults)) {
            return null;
        }
        Map<Long, List<DashboardProjectInfoSettleInThreeMonthResult>> clientId2Settle = dashboardProjectInfoSettleInThreeMonthResults.stream().collect(Collectors.groupingBy(DashboardProjectInfoSettleInThreeMonthResult::getClientId));
        //查询客户信息
        DashboardClientOverviewAllQuery dashboardClientOverviewAllQuery = BeanUtil.copyProperties(req, DashboardClientOverviewAllQuery.class);
        dashboardClientOverviewAllQuery.setAccountVO(BeanUtil.copyProperties(req.getAccountVo(), AccountVO.class));
        dashboardClientOverviewAllQuery.fillAuthQuery();
        dashboardClientOverviewAllQuery.setLimitClientIds(clientId2Settle.keySet());
        Page<DashboardClientBasicDTO> dashboardClientBasicDTOPage = clientMapper.dashboardAllPageList(new Page(req.getPage(), req.getPageSize()), dashboardClientOverviewAllQuery);
        List<DashboardClientOverviewSettleInThreeMonthRSP> dashboardClientOverviewSettleInThreeMonthRSPS = BeanUtil.copyToList(dashboardClientBasicDTOPage.getRecords(), DashboardClientOverviewSettleInThreeMonthRSP.class);
        if (ObjectUtil.isEmpty(dashboardClientOverviewSettleInThreeMonthRSPS)) {
            return null;
        }
        buildDashboardClientBasicRsp(dashboardClientOverviewSettleInThreeMonthRSPS);
        Set<Long> clientIdSet = dashboardClientOverviewSettleInThreeMonthRSPS.stream().map(DashboardClientOverviewSettleInThreeMonthRSP::getClientId).collect(Collectors.toSet());
        //剩余本金，存量敞口，授信总金额
        List<Client> clients = clientService.listByIds(clientIdSet);
        List<ClientListRSP> list = BeanUtil.copyToList(clients, ClientListRSP.class);
        clientService.fillOtherInfo(list, Boolean.FALSE);
        Map<Long, ClientListRSP> clientMap = list.stream().collect(Collectors.toMap(ClientListRSP::getId, e -> e));
        ClientListRSP clientListRSP;
        List<DashboardProjectInfoSettleInThreeMonthResult> settleList;
        StringBuilder sb = new StringBuilder();
        for (DashboardClientOverviewSettleInThreeMonthRSP rsp : dashboardClientOverviewSettleInThreeMonthRSPS) {
            clientListRSP = clientMap.get(rsp.getClientId());
            if (ObjectUtil.isNotEmpty(clientListRSP)) {
                rsp.setCreditAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getApplyCreditAmount()).toString()).toString(), WAN));
                rsp.setPrincipalBalanceAmount(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getLastPrincipal()).toString()).toString(), WAN));
                rsp.setStockRiskExposure(new ValueUnitDTO(LongUtil.tenThousand2Dollar(LongUtil.null2zero(clientListRSP.getApplyCreditAmount()).toString()).toString(), WAN));
            }
            //填充金额信息
            settleList = clientId2Settle.get(rsp.getClientId());
            if (ObjectUtil.isNotEmpty(settleList)) {
                for (DashboardProjectInfoSettleInThreeMonthResult settle : settleList) {
                    rsp.setCollectionPrincipalAmount(this.getAmountDto(rsp.getCollectionPrincipalAmount(), settle.getTotalCollectionPrincipal(), true));
                    rsp.setCollectionInterestAmount(this.getAmountDto(rsp.getCollectionInterestAmount(), settle.getTotalCollectionInterest(), true));
                    //租金
                    rsp.setCollectionAmount(this.getAmountDto(rsp.getCollectionAmount(), settle.getTotalCollectionPrincipal(), true));
                    rsp.setCollectionAmount(this.getAmountDto(rsp.getCollectionAmount(), settle.getTotalCollectionInterest(), true));
                    //剩余本金 应收-已收本金
                    //rsp.setInterestPrincipalAmount(this.getAmountDto(rsp.getInterestPrincipalAmount(), settle.getTotalPlanPrincipal(), true));
                    //rsp.setInterestPrincipalAmount(this.getAmountDto(rsp.getInterestPrincipalAmount(), settle.getTotalCollectionPrincipal(), false));
                    //剩余利息
                    rsp.setInterestInterestAmount(this.getAmountDto(rsp.getInterestInterestAmount(), settle.getTotalPlanInterest(), true));
                    rsp.setInterestInterestAmount(this.getAmountDto(rsp.getInterestInterestAmount(), settle.getTotalCollectionInterest(), false));
                    sb.append(settle.getContractCode());
                    sb.append(",");
                    if (ObjectUtil.isEmpty(rsp.getDeadline()) || settle.getLastRentPlanDate().isAfter(rsp.getDeadline())){
                        rsp.setDeadline(settle.getLastRentPlanDate());
                        rsp.setRemainingDuration(ChronoUnit.DAYS.between(LocalDate.now(), settle.getLastRentPlanDate()));
                    }
                    if (ObjectUtil.isEmpty(rsp.getProjSet())) {
                        rsp.setProjSet(new HashSet<>());
                        rsp.getProjSet().add(settle.getProjName());
                        rsp.setProjNames(settle.getProjName());
                    } else if (!rsp.getProjSet().contains(settle.getProjName())) {
                        rsp.getProjSet().add(settle.getProjName());
                        rsp.setProjNames(rsp.getProjNames() + "," + settle.getProjName());
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
                rsp.setContractCodes(sb.toString());
                sb.setLength(0);  // 清空StringBuilder的内容
            }
        }
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(dashboardClientOverviewSettleInThreeMonthRSPS, dashboardClientBasicDTOPage.getTotal());
    }

    private ValueUnitDTO getAmountDto(ValueUnitDTO amountDto, Long amount, Boolean isAdd) {
        if (ObjectUtil.isEmpty(amountDto)) {
            amountDto = new ValueUnitDTO();
            amountDto.setValue(BigDecimal.ZERO.toPlainString());
            amountDto.setUnit(WAN);
        }
        BigDecimal amountDecimal = Util.mithrasLong2BigDecimalWY(LongUtil.null2zero(amount));
        if (isAdd) {
            amountDto.setValue(new BigDecimal(amountDto.getValue()).add(amountDecimal).setScale(10, RoundingMode.HALF_UP).toPlainString());
        } else {
            amountDto.setValue(new BigDecimal(amountDto.getValue()).subtract(amountDecimal).setScale(10, RoundingMode.HALF_UP).toPlainString());
        }
        return amountDto;
    }

    //逾期客户 cn.zswltech.mithras.service.mapper.dashboard.DashboardProjectInfoMapper#listOverdue
    public PageR<DashboardClientOverviewOverdueRSP> overduePageList(DashboardClientOverviewOverdueREQ req) {
        StopWatch st = new StopWatch();
        st.start("逾期客户");
        DashboardProjectInfoOverdueQuery query = BeanUtil.copyProperties(req, DashboardProjectInfoOverdueQuery.class);
        query.setAccountVO(BeanUtil.copyProperties(req.getAccountVo(), AccountVO.class));
        query.fillAuthQuery();
        /*
        List<DashboardProjectInfoOverdueResult> dashboardProjectInfoOverdueResults = getBean(DashboardProjectInfoMapper.class).listOverdue(query);
        if (CollUtil.isEmpty(dashboardProjectInfoOverdueResults)) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Set<Long> clientIds = dashboardProjectInfoOverdueResults.stream().map(DashboardProjectBasicResult::getClientId).collect(Collectors.toSet());
        */
        Set<Long> clientIds = getBean(ClientLifeCycleService.class).getTargetClientIds(CardName.OVERDUE.name());
        Page<DashboardClientOverviewOverdueRSP> overdueRspPage = new Page<>();
        overdueRspPage.setCurrent(req.getPage());
        overdueRspPage.setSize(req.getPageSize());

        Page<DashboardClientOverviewOverdueRSP> overviewOverdueRspPage = getBean(CollectionBaseInfoMapper.class).overduePageQuery(overdueRspPage, query, clientIds);
        //填充返回体
        fillResult(overviewOverdueRspPage.getRecords());
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(overviewOverdueRspPage.getRecords(), overviewOverdueRspPage.getTotal(), overviewOverdueRspPage.getCurrent(), overviewOverdueRspPage.getSize());
    }

    private void fillResult(List<DashboardClientOverviewOverdueRSP> respList) {
        List<Long> userIds = respList.stream().map(DashboardClientOverviewOverdueRSP::getProjSponsorUserId).collect(Collectors.toList());
        List<Long> deptIds = respList.stream().map(DashboardClientOverviewOverdueRSP::getBizDeptId).collect(Collectors.toList());
        Map<Long, String> userId2NameMap = getBean(Id2NameService.class).sysUserId2Name(userIds);
        Map<Long, String> deptId2NameMap = getBean(Id2NameService.class).deptId2Name(deptIds);
        Map<Long, ContractBaseInfo> contractBaseInfoMap = new HashMap<>();
        Map<Long, ContractLeasePrice> contractLeasePriceMap = new HashMap<>();

        List<Long> contractIds = respList.stream().map(DashboardClientOverviewOverdueRSP::getContractId).collect(Collectors.toList());
        List<ContractBaseInfo> contractBaseInfos = null;
        List<ContractLeasePrice> contractLeasePrices = null;
        if (ObjectUtil.isNotEmpty(contractIds)) {
            contractBaseInfos = contractBaseInfoService.listByIds(contractIds);
            contractLeasePrices = getBean(ContractLeasePriceMapper.class).selectList(
                    Wrappers.<ContractLeasePrice>lambdaQuery()
                            .in(ContractLeasePrice::getContractId, contractIds));
        }
        if(ObjectUtil.isNotEmpty(contractLeasePrices)) {
            contractLeasePriceMap = contractLeasePrices.stream().collect(Collectors.toMap(ContractLeasePrice::getContractId, e -> e, (a, b) -> a));
        }

        if(ObjectUtil.isNotEmpty(contractBaseInfos)){
            contractBaseInfoMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
        }
        Set<Long> projCosponsorUserIds = new HashSet<>();
        respList.stream().map(o -> JSONUtil.toList(o.getProjCosponsorUserIds(), Long.class)).collect(Collectors.toSet())
                .forEach(projCosponsorUserIds::addAll);
        Map<Long, String> userId2Name = getBean(Id2NameService.class).sysUserId2Name(projCosponsorUserIds);
        Set<Long> guarantorList = new HashSet<>();
        respList.stream().map(o -> JSONUtil.toList(o.getGuarantorList(), Long.class)).collect(Collectors.toSet())
                .forEach(guarantorList::addAll);
        Map<Long, String> guarantorMap = getBean(Id2NameService.class).sysUserId2Name(guarantorList);
        Map<Long, ContractBaseInfo> finalContractBaseInfoMap = contractBaseInfoMap;
        Map<Long, ContractLeasePrice> finalContractLeasePriceMap = contractLeasePriceMap;
        respList.forEach(dto -> {
            dto.setProjSponsorUserName(userId2NameMap.get(dto.getProjSponsorUserId()));
            dto.setBizDeptName(deptId2NameMap.get(dto.getBizDeptId()));

            List<Long> projCosponsorUserid = JSONUtil.toList(dto.getProjCosponsorUserIds(), Long.class);
            List<String> list = projCosponsorUserid.stream().map(userId2Name::get).collect(Collectors.toList());
            dto.setProjCosponsorUserNames(CharSequenceUtil.join(",", list));
            dto.setProjCosponsorUserIds(CharSequenceUtil.join(",", projCosponsorUserIds));

            List<Long> guarantors = JSONUtil.toList(dto.getGuarantorList(), Long.class);
            List<String> strings = guarantors.stream().map(guarantorMap::get).collect(Collectors.toList());
            dto.setGuarantorList(CharSequenceUtil.join(",", strings));

            setPenaltyInterestRate(dto, finalContractBaseInfoMap.get(dto.getContractId()), finalContractLeasePriceMap.get(dto.getContractId()));
            //这个必须放在最后
            dto.setBizType(Optional.ofNullable(ProjectBizType.of(dto.getBizType())).map(o -> o.display).orElse(""));
        });
    }

    private static void setPenaltyInterestRate(DashboardClientOverviewOverdueRSP dto, ContractBaseInfo contractBaseInfo, ContractLeasePrice contractLeasePrice) {
        String rate = "";
        String bizModel = "";
        if (ObjectUtil.isNull(contractBaseInfo)) {
            return;
        }
        switch (ProjectBizType.of(dto.getBizType())) {
            case ZZ:
            case ZL: {
                rate = BigDecimal.valueOf(contractLeasePrice.getDefaultInterestRate()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
                bizModel = Optional.ofNullable(LeaseType.of(contractBaseInfo.getLeaseType())).map(LeaseType::getDisplay).orElse("");
                break;
            }
            case BL: {
                bizModel = Optional.ofNullable(FactoringType.of(contractBaseInfo.getFactoringType())).map(FactoringType::getDisplay).orElse("");
                break;
            }
            case ZR: {
                bizModel = Optional.ofNullable(ZrType.of(contractBaseInfo.getZrType())).map(ZrType::getDisplay).orElse("");
                break;
            }
            default:
                break;
        }
        dto.setBizModel(bizModel);
        dto.setPenaltyInterestRate(rate);
    }

    //已结清客户明细
    public PageR<DashboardClientOverviewSettledRSP> settledPageList(DashboardClientOverviewSettledREQ req) {
        StopWatch st = new StopWatch();
        st.start("已结清客户明细");
        //结清客户
        Set<Long> targetClientIds = clientLifeCycleService.getTargetClientIds(CardName.SETTLED.name());
        if (ObjectUtil.isEmpty(targetClientIds)) {
            return null;
        }
        DashboardClientOverviewAllQuery dashboardClientOverviewAllQuery = BeanUtil.copyProperties(req, DashboardClientOverviewAllQuery.class);
        dashboardClientOverviewAllQuery.setAccountVO(BeanUtil.copyProperties(req.getAccountVo(), AccountVO.class));
        dashboardClientOverviewAllQuery.fillAuthQuery();
        dashboardClientOverviewAllQuery.setLimitClientIds(targetClientIds);
        //分页过滤数据
        Page<DashboardClientBasicDTO> dashboardClientBasicDTOPage = clientMapper.dashboardAllPageList(new Page(req.getPage(), req.getPageSize()), dashboardClientOverviewAllQuery);
        if (ObjectUtil.isEmpty(dashboardClientBasicDTOPage) || ObjectUtil.isEmpty(dashboardClientBasicDTOPage.getRecords())) {
            return null;
        }
        List<DashboardClientBasicDTO> records = dashboardClientBasicDTOPage.getRecords();
        List<DashboardClientOverviewSettledRSP> rsps = new ArrayList<>();
        //填充数据
        Set<Long> clientIdSet = records.stream().map(DashboardClientBasicDTO::getClientId).collect(Collectors.toSet());
        List<ContractBaseInfo> list = contractBaseInfoService.list(clientIdSet);
        if (ObjectUtil.isEmpty(list)) {
            return null;
        }
        List<Long> deptIds = new ArrayList<>();
        List<Long> systemIds = new ArrayList<>();
        for (ContractBaseInfo base : list) {
            if (ObjectUtil.isNotEmpty(base.getBizDeptId())) {
                deptIds.add(base.getBizDeptId());
            }
            if (ObjectUtil.isNotEmpty(base.getProjSponsorUserId())) {
                systemIds.add(base.getProjSponsorUserId());
            }
            if (ObjectUtil.isNotEmpty(base.getProjCosponsorUserIds())) {
                systemIds.addAll(JSONUtil.toList(base.getProjCosponsorUserIds(), Long.class));
            }
        }
        Map<Long, DashboardClientBasicDTO> id2RecordMap = records.stream().collect(Collectors.toMap(DashboardClientBasicDTO::getClientId, e -> e, (a, b) -> a));
        records.forEach(e -> {
            systemIds.add(e.getProjSponsorUserId());
            deptIds.add(e.getBizDeptId());
        });
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIdSet);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(systemIds);
        Map<Long, String> projId2Name = list.stream().collect(Collectors.toMap(ContractBaseInfo::getProjReviewId, ContractBaseInfo::getProjName, (a, b) -> a));
        Map<Long, List<ContractBaseInfo>> clientId2ContractMap = list.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        clientIdSet.forEach(clientId -> {
            DashboardClientOverviewSettledRSP rsp = new DashboardClientOverviewSettledRSP();
            rsp.setClientId(clientId);
            rsp.setClientName(clientId2Name.get(clientId));
            DashboardClientBasicDTO dashboardClientBasicDTO = id2RecordMap.get(clientId);
            if(ObjectUtil.isNotEmpty(dashboardClientBasicDTO)){
                rsp.setProjSponsorUserId(dashboardClientBasicDTO.getProjSponsorUserId());
                rsp.setProjSponsorUserName(userId2Name.get(dashboardClientBasicDTO.getProjSponsorUserId()));
                rsp.setBizDeptId(dashboardClientBasicDTO.getBizDeptId());
                rsp.setBizDeptName(deptId2Name.get(dashboardClientBasicDTO.getBizDeptId()));
            }
            rsp.setProjReviewList(new ArrayList<>());
            List<ContractBaseInfo> contractBaseInfos = clientId2ContractMap.get(clientId);
            if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
                contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId)).forEach((k, v) -> {
                    DashboardClientOverviewSettledRSP.OverviewSettledProjRSP projReviewBody = new DashboardClientOverviewSettledRSP.OverviewSettledProjRSP();
                    projReviewBody.setProjReviewId(k);
                    projReviewBody.setProjName(projId2Name.get(k));
                    projReviewBody.setContractList(new ArrayList<>());
                    v.forEach(base -> {
                        DashboardClientOverviewSettledRSP.OverviewSettledContractRSP contractRSP = BeanUtil.copyProperties(base, DashboardClientOverviewSettledRSP.OverviewSettledContractRSP.class, "projCosponsorUserIds");
                        contractRSP.setBizDeptName(deptId2Name.get(contractRSP.getBizDeptId()));
                        contractRSP.setProjSponsorUserName(clientId2Name.get(contractRSP.getProjSponsorUserId()));
                        if (ObjectUtil.isNotEmpty(base.getProjCosponsorUserIds())) {
                            List<Long> longs = JSONUtil.toList(base.getProjCosponsorUserIds(), Long.class);
                            contractRSP.setProjCosponsorUserIds(longs);
                            contractRSP.setProjCosponsorUserNames(new ArrayList<>());
                            longs.forEach(id -> {
                                contractRSP.getProjCosponsorUserNames().add(userId2Name.get(id));
                            });
                        }
                        contractRSP.setProjSponsorUserName(userId2Name.get(contractRSP.getProjSponsorUserId()));
                        projReviewBody.getContractList().add(contractRSP);
                    });
                    rsp.getProjReviewList().add(projReviewBody);
                });
                rsps.add(rsp);
            }
        });
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return PageR.of(rsps, dashboardClientBasicDTOPage.getTotal());
    }

    private <T extends DashboardClientBasicRSP> void buildDashboardClientBasicRsp(List<T> rsps) {
        if (ObjectUtil.isEmpty(rsps)) {
            return;
        }
        //补充国标行业分类,省份
        Set<String> industryTypeCodeSet = new HashSet<>();
        Map<String, IndustryType> industryMap = new HashMap<>();
        Set<String> provinceCodeSet = new HashSet<>();
        Map<String, String> addressMap = new HashMap<>();
        Set<Long> clientIdSet = new HashSet<>();
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(rsps.stream().map(T::getBizDeptId).collect(Collectors.toList()));
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(rsps.stream().map(T::getProjSponsorUserId).collect(Collectors.toList()));
        Map<Long, String> createId2Name = id2NameService.sysUserId2Name(rsps.stream().map(T::getCreateBy).collect(Collectors.toList()));
        rsps.forEach(rsp -> {
            String code = rsp.getIndustryTypeCode();
            if (ObjectUtil.isNotEmpty(code)) {
                for (int i = code.length(); i > 0; i--) {
                    String s = rsp.getIndustryTypeCode().substring(0, i);
                    industryTypeCodeSet.add(s);
                }
                provinceCodeSet.add(rsp.getProvinceCode());
            }
            clientIdSet.add(rsp.getClientId());
        });
        if (!industryTypeCodeSet.isEmpty()) {
            industryMap = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                    .in(IndustryType::getCode, industryTypeCodeSet)).stream().collect(Collectors.toMap(IndustryType::getCode, e -> e, (a, b) -> a));
        }
        if (!provinceCodeSet.isEmpty()) {
            addressMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                    .in(AddressDictionary::getCode, provinceCodeSet)).stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (a, b) -> a));
        }
        //剩余本金，存量敞口，授信总金额
        List<Client> clients = clientService.listByIds(clientIdSet);
        List<ClientListRSP> list = BeanUtil.copyToList(clients, ClientListRSP.class);
        clientService.fillOtherInfo(list, Boolean.FALSE);
        for (T rsp : rsps) {
            rsp.setRiskControlIndustryClassifyDisplay(Optional.ofNullable(RiskControlIndustryClassify.of(rsp.getRiskControlIndustryClassifyCode())).map(RiskControlIndustryClassify::display).orElse(null));
            rsp.setAssetClassifyResultDisplay(Optional.ofNullable(AssetClassifyResultEnum.of(rsp.getAssetClassifyResultCode())).map(AssetClassifyResultEnum::display).orElse(null));
            rsp.setClientTypeName(Optional.ofNullable(ClientType.of(rsp.getClientType())).map(ClientType::display).orElse(null));
            // 找到行业分类及其父分类
            if (StrUtil.isNotBlank(rsp.getIndustryTypeCode())) {
                int length = rsp.getIndustryTypeCode().length();
                List<IndustryType> industryTypeList = new ArrayList<>();
                // code的父子关系呈现位数关系，eg: A A01 A011 A0111
                // 查询条件不特殊处理A0，不存在这样的数据，多带一个无效条件问题不大
                IndustryType industryTypeTemp;
                for (int i = length; i > 0; i--) {
                    String s = rsp.getIndustryTypeCode().substring(0, i);
                    industryTypeTemp = industryMap.get(s);
                    if (ObjectUtil.isNotEmpty(industryTypeTemp)) {
                        industryTypeList.add(industryTypeTemp);
                    }
                }
                industryTypeList.sort(Comparator.comparing(IndustryType::getLevel));
                List<String> result = new ArrayList<>(industryTypeList.size());
                StringBuilder builder = new StringBuilder();
                for (IndustryType industryType : industryTypeList) {
                    if (builder.length() != 0) {
                        builder.append("/");
                    }
                    builder.append(industryType.getDisplay());
                    result.add(industryType.getCode());
                }
                rsp.setIndustryTypeDisplay(builder.toString());
                rsp.setIndustryTypeDisplayCodeList(result);
                rsp.setCreatorName(createId2Name.get(rsp.getCreateBy()));
            }
            rsp.setEnterpriseNatureDisplay(Optional.ofNullable(EnterpriseNatureEnum.of(rsp.getEnterpriseNatureCode())).map(EnterpriseNatureEnum::display).orElse(null));
            rsp.setProvinceDisplay(addressMap.get(rsp.getProvinceCode()));
            rsp.setBizDeptName(deptId2Name.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(userId2Name.get(rsp.getProjSponsorUserId()));
        }
    }
}
