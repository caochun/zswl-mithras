package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.OrgRoleVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryRSP;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.dto.AccountReq;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.client.lifecycle.ClientLifeCycleDetailReq;
import cn.zswltech.mithras.dto.client.lifecycle.ClientLifeCycleReceiptRsp;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.customer.application.client.api.CorpSubjectItemApplicationService;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemDisplayDimension;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemType;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.DashboardProjectStageMapper;
import cn.zswltech.mithras.contract.mapper.dto.OcContractDto;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import cn.zswltech.mithras.third.providence.mapper.BillOverdueMapper;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.dashboard.application.DashboardAuthQueryHelper;
import cn.zswltech.mithras.service.service.dashboard.DashboardClientOverviewService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zswltec.providence.dto.HeadBodyReq;
import com.zswltec.providence.dto.HeadBodyRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.basic.Constant.fiveClassMap;

/**
 * @author junke
 */
@Slf4j
@Service
public class ClientUnifiedViewService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ContractService contractService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private DashboardProjectStageMapper dashboardProjectStageMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private DashboardClientOverviewService dashboardClientOverviewService;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;
    @Resource
    private ClientLifeCycleService clientLifeCycleService;
    @Resource
    private BillOverdueMapper billOverdueMapper;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private OrgDOMapper orgDOMapper;

    public List<SelectRSP> orgList() {

        Example example = new Example(OrgDO.class);
        example.createCriteria().andEqualTo("type", "1");
        List<OrgDO> orgDOList = orgDOMapper.selectByExample(example);
        List<SelectRSP> result = new ArrayList<>();
        if (orgDOList != null) {
            result = orgDOList.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString(), e.getState())).collect(Collectors.toList());
        }

        /*
         * 项目经理、团队长角色，仅可查看自己部门内客户数据
         * 分管领导可以查看其分管的所有部门项下的所有客户数据
         * 其他用户能看到统一视图 都为领导层看全部客户信息
         * */
        Set<String> roles = new TreeSet<>(sysUserService.getCurrentUserRoles());
        if(roles.contains("XMJL") || roles.contains("TDZ")){
            /*找到 登陆用户所在的部门信息 */
            Set<Long> orgSet = sysUserService.getUserBizDeptList().stream().map(OrgDO::getId).collect(Collectors.toSet());
            List<SelectRSP> orgList = new ArrayList<>();
            orgList = result.stream().filter(e -> orgSet.contains(Long.valueOf(e.getValue()))).collect(Collectors.toList());
            return orgList;
        }
//        else if(roles.contains("XXXXXX")){  // 分管领导
//            /*找到 登陆用户所在的部门信息 然后从所有部门中过滤出其子部门  一起参与查询 */
//            orgSet = sysUserService.getUserBizDeptList().stream().map(OrgDO::getId).collect(Collectors.toSet());
//            List<OrgDO> orgs = orgDOMapper.queryAll();
//            final Set<Long> finalOrgSet = orgSet;
//            List<Long> sonOrgs = orgs.stream().filter(e -> finalOrgSet.contains(e.getId())).map(OrgDO::getId).collect(Collectors.toList());
//            orgSet.addAll(sonOrgs);
//        }
        return result;
    }

    public PageR<ClientUnifiedViewListRSP> unifiedViewList(ClientUnifiedViewListREQ req) {
        if(ObjectUtil.isEmpty(req.getOrgCodeList())){
            return PageR.of(null, 0);
        }
        Page<Client> clientPage = clientMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<Client>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(req.getClientName()), Client::getClientName, req.getClientName())
                .eq(Client::getClientType, ClientType.CORPORATION.name())
                .in(ObjectUtil.isNotEmpty(req.getClientStatus()), Client::getClientStatus, req.getClientStatus())
                .in(ObjectUtil.isNotEmpty(req.getOrgCodeList()), Client::getBelongDeptId, req.getOrgCodeList())
                .orderByAsc(Client::getClientName));
        if (ObjectUtil.isEmpty(clientPage) || ObjectUtil.isEmpty(clientPage.getRecords())) {
            return PageR.of(null, 0);
        }
        Map<Long, CorpCommerceInfo> clientId2CorpCommerceMap = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .in(CorpCommerceInfo::getClientId, clientPage.getRecords().stream().map(Client::getId).collect(Collectors.toSet())))
                .stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> b));
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(clientPage.getRecords().stream().map(Client::getBelongDeptId).filter(ObjectUtil::isNotNull).collect(Collectors.toSet()));
        List<ClientUnifiedViewListRSP> corpCommerceInfos = clientPage.getRecords().stream().map(e -> {
            CorpCommerceInfo corpCommerceInfo = clientId2CorpCommerceMap.get(e.getId());
            ClientUnifiedViewListRSP rsp = new ClientUnifiedViewListRSP();
            rsp.setClientName(e.getClientName());
            rsp.setClientStatus(e.getClientStatus());
            rsp.setBelongDeptId(e.getBelongDeptId());
            rsp.setCertType(e.getCertType());
            rsp.setClientId(e.getId());
            rsp.setCertNumber(e.getCertNumber());
            rsp.setUscCode(e.getUscCode());
            rsp.setBelongDeptName(deptId2Name.get(e.getBelongDeptId()));
            if (ObjectUtil.isNotEmpty(corpCommerceInfo)) {
                rsp.setCorpGender(corpCommerceInfo.getCorpGender());
                rsp.setCorpRepresent(corpCommerceInfo.getCorpRepresent());
                rsp.setRegisterCapital(corpCommerceInfo.getRegisterCapital());
                rsp.setEstablishDate(corpCommerceInfo.getEstablishDate());
                rsp.setClientType(corpCommerceInfo.getClientType());
                rsp.setBizScope(corpCommerceInfo.getBizScope());
            }
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(corpCommerceInfos, clientPage.getTotal());
    }

    public ClientUnifiedViewDetailRSP unifiedViewDetail(ClientUnifiedApplyCreditREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (ObjectUtil.isEmpty(client)) {
            return null;
        }
        //客户工商信息
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, client.getId())
                .last(StringUtil.mysqlLimitOne()));
        ClientUnifiedViewDetailRSP rsp = new ClientUnifiedViewDetailRSP();
        rsp.setClientName(client.getClientName());
        rsp.setClientStatus(client.getClientStatus());
        rsp.setBelongDeptId(client.getBelongDeptId());
        rsp.setCertType(client.getCertType());
        rsp.setClientId(client.getId());
        rsp.setCertNumber(client.getCertNumber());
        rsp.setBelongDeptName(id2NameService.deptId2NameSingle(client.getBelongDeptId()));
        if (ObjectUtil.isNotEmpty(corpCommerceInfo)) {
            rsp.setCorpGender(corpCommerceInfo.getCorpGender());
            rsp.setRegisterCapital(corpCommerceInfo.getRegisterCapital());
            rsp.setEstablishDate(corpCommerceInfo.getEstablishDate());
            rsp.setClientType(corpCommerceInfo.getClientType());
            rsp.setUscCode(corpCommerceInfo.getUscCode());
            rsp.setBizScope(corpCommerceInfo.getBizScope());
        }
        return rsp;
    }

    //获取客户授信信息
    public ClientUnifiedApplyCreditRSP clientApplyCredit(ClientUnifiedApplyCreditREQ req) {
        //1查询客户项目评审授信额度
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByClients(Collections.singletonList(req.getClientId()));
        if (ObjectUtil.isEmpty(projReviewBaseInfos)) {
            return null;
        }
        Set<Long> projReviewIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet());
        ArrayList<Long> projReviewIdList = new ArrayList<>(projReviewIds);
        //2.查询项目评审额度 = 授信总额度
        Map<Long, Long> projReviewMount = projReviewPriceService.newestPrice(projReviewIds);
        //3.查询项目下合同占用额度
        Map<Long, Long> projUseAmount = contractPriceService.projUseApplyCreditAmount(projReviewIdList);
        //查询额度循环的合同本金还款情况
        Map<Long, Long> projReviewCycleQuotaReturnAmount = contractService.getProjReviewCycleQuotaReturnAmount(projReviewIdList);
        ClientUnifiedApplyCreditRSP rsp = new ClientUnifiedApplyCreditRSP();
        rsp.setApplyCreditAmount(projReviewMount.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
        Long useAmount = projUseAmount.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
        Long returnAmount = projReviewCycleQuotaReturnAmount.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
        rsp.setUsedCreditAmount(useAmount - returnAmount);
        rsp.setUnusedCreditAmount(rsp.getApplyCreditAmount() - rsp.getUsedCreditAmount());
        rsp.setUsageRate(ObjectUtil.equals(rsp.getApplyCreditAmount(), 0L) ? BigDecimal.ZERO : new BigDecimal(rsp.getUsedCreditAmount()).divide(new BigDecimal(rsp.getApplyCreditAmount()), 2, BigDecimal.ROUND_HALF_UP));
        //补充客户剩余本金
        Map<Long, Long> clientRemainingPrincipalMap = SpringContextHolder.getBean(ClientService.class).getClientRemainingPrincipalMap(Collections.singletonList(req.getClientId()));
        if (ObjectUtil.isNotEmpty(clientRemainingPrincipalMap)) {
            rsp.setRemainingPrincipal(clientRemainingPrincipalMap.getOrDefault(req.getClientId(), 0L));
        }
        return rsp;
    }

    /**
     * 获取客户历史授信信息
     **/
    public Map<String, Long> clientApplyCreditHistory(ClientUnifiedApplyCreditREQ req) {
        //1查询客户项目评审授信额度
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByClients(Collections.singletonList(req.getClientId()));
        if (ObjectUtil.isEmpty(projReviewBaseInfos)) {
            return null;
        }
        Set<Long> projReviewIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet());
        ArrayList<Long> projReviewIdList = new ArrayList<>(projReviewIds);
        //2.查询每月项目评审额度
        Map<Long, Long> reviewApplyCreditByMonth = projReviewPriceService.getReviewApplyCreditByMonth(projReviewIds);
        //查询项目生效日期
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(5000);
        processPageReq.setBusinessKeyList(projReviewIdList.stream().map(String::valueOf).collect(Collectors.toList()));
        processPageReq.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        if(ObjectUtil.isEmpty(processRespPage) || ObjectUtil.isEmpty(processRespPage.getContents())) {
            return MapUtil.empty();
        }
        Map<String, Date> projReviewEffectDate = processRespPage.getContents().stream().collect(Collectors.toMap(ProcessResp::getBusinessKey, ProcessResp::getEndTime, (a, b) -> a));
        Map<String, Long> rsps = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        projReviewEffectDate.forEach((reviewId, effectDate) -> {
            String format = dateFormat.format(effectDate);
            Long orDefault = rsps.getOrDefault(format, 0L);
            rsps.put(format, orDefault + LongUtil.null2zero(reviewApplyCreditByMonth.getOrDefault(Long.parseLong(reviewId), 0L)));
        });
        return rsps;
    }

    //项目信息，各阶段数据
    public List<ClientUnifiedProjListRSP> clientProjList(ClientUnifiedApplyCreditREQ req) throws ExecutionException, InterruptedException {
        AccountVO accountVO = AccountUtil.getLoginInfo();
        // 立项阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> establish = CompletableFuture.supplyAsync(() -> establishStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_ESTABLISH, accountVO));
        // 评审阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> review = CompletableFuture.supplyAsync(() -> reviewStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REVIEW, 1, accountVO));
        // 评审通过未创建合同
        CompletableFuture<List<ClientUnifiedProjListRSP>> reviewNoContract = CompletableFuture.supplyAsync(() -> reviewStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REVIEW_NO_CONTRACT, 2, accountVO));
        // 签约（合同）阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> contract = CompletableFuture.supplyAsync(() -> contractStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_CONTRACT, accountVO));
        // 付款阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> preparePayment = CompletableFuture.supplyAsync(() -> paymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_PREPARE_PAYMENT, 1, accountVO));
        // 投放阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> payment = CompletableFuture.supplyAsync(() -> paymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_PAYMENT, 2, accountVO));
        // 还款阶段
        CompletableFuture<List<ClientUnifiedProjListRSP>> repayment = CompletableFuture.supplyAsync(() -> repaymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REPAYMENT, accountVO));
        // 放入返回参数中
        List<ClientUnifiedProjListRSP> result = new ArrayList<>();
        result.addAll(establish.get());
        result.addAll(review.get());
        result.addAll(reviewNoContract.get());
        result.addAll(contract.get());
        result.addAll(preparePayment.get());
        result.addAll(payment.get());
        result.addAll(repayment.get());
        //补充授信金额
        if (ObjectUtil.isNotEmpty(result)) {
            Map<Long, List<ProjReviewBaseInfo>> esId2Review = SpringContextHolder.getBean(ProjReviewBaseInfoService.class).listByProjEstablishIds(result.stream().map(ClientUnifiedProjListRSP::getId).collect(Collectors.toList()))
                    .stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getProjEstablishId));
            Set<Long> projReviewIds = new HashSet<>();
            esId2Review.values().forEach(list -> {
                if (ObjectUtil.isNotEmpty(list)) {
                    projReviewIds.addAll(list.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
                }
            });
            Map<Long, ProjReviewPriceDetailRSP> list = SpringContextHolder.getBean(ProjReviewPriceService.class).list(new ArrayList<>(projReviewIds));
            result.forEach(e -> {
                List<ProjReviewBaseInfo> projReviewBaseInfos = esId2Review.get(e.getId());
                if (ObjectUtil.isNotEmpty(projReviewBaseInfos)) {
                    projReviewBaseInfos.forEach(projReviewBaseInfo -> {
                        ProjReviewPriceDetailRSP projReviewPriceDetailRSP = list.get(projReviewBaseInfo.getId());
                        if (ObjectUtil.isNotEmpty(projReviewPriceDetailRSP)) {
                            e.setApplyCreditAmount(LongUtil.null2zero(e.getApplyCreditAmount()) + LongUtil.null2zero(projReviewPriceDetailRSP.getApprovedAmount()));
                        }
                    });
                }
            });
        }
        return result;
    }

    //合同信息
    public List<ClientUnifiedContractListRSP> clientContractList(ClientUnifiedApplyCreditREQ req) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByClients(Collections.singletonList(req.getClientId()));
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return null;
        }
        List<ClientUnifiedContractListRSP> rsps = new ArrayList<>();
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()));
        contractBaseInfos.forEach(contractBaseInfo -> {
            ClientUnifiedContractListRSP rsp = new ClientUnifiedContractListRSP();
            rsp.setId(contractBaseInfo.getId());
            rsp.setContractCode(contractBaseInfo.getContractCode());
            rsp.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setContractStatus(contractBaseInfo.getContractStatus());
            rsp.setBelongDeptId(contractBaseInfo.getBizDeptId());
            rsp.setBelongDeptName(deptId2Name.get(contractBaseInfo.getBizDeptId()));
            rsps.add(rsp);
        });
        return rsps;
    }

    public List<ClientUnifiedCustomerTrendsRSP> customerTrends() throws ExecutionException, InterruptedException {
        Map<LocalDate, LocalDate> timeMap = getTime(LocalDate.now());
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        ClientUnifiedCustomerTrendsRSP allClientList = new ClientUnifiedCustomerTrendsRSP();
        allClientList.setCordName(DashboardCardGroupEnum.CLIENT_ALL.name());
        allClientList.setItems(new ArrayList<>());
        ClientUnifiedCustomerTrendsRSP survivalClientList = new ClientUnifiedCustomerTrendsRSP();
        survivalClientList.setCordName(DashboardCardGroupEnum.CLIENT_SURVIVAL.name());
        survivalClientList.setItems(new ArrayList<>());
        ClientUnifiedCustomerTrendsRSP settleClientList = new ClientUnifiedCustomerTrendsRSP();
        settleClientList.setCordName(DashboardCardGroupEnum.CLIENT_SETTLE.name());
        settleClientList.setItems(new ArrayList<>());
        ClientUnifiedCustomerTrendsRSP overdueClientList = new ClientUnifiedCustomerTrendsRSP();
        overdueClientList.setCordName(DashboardCardGroupEnum.CLIENT_OVERDUE.name());
        overdueClientList.setItems(new ArrayList<>());
        List<ClientUnifiedCustomerTrendsRSP> rsps = new ArrayList<>();
        rsps.add(allClientList);
        rsps.add(survivalClientList);
        rsps.add(settleClientList);
        rsps.add(overdueClientList);
        LocalTime time = LocalTime.of(0, 0);
        for (Map.Entry<LocalDate, LocalDate> entry : timeMap.entrySet()) {
            //LocalDate began = entry.getKey();
            LocalDate end = entry.getValue();
            //所有客户
            CompletableFuture<PageR<DashboardClientOverviewAllRSP>> allClients = CompletableFuture.supplyAsync(() -> dashboardClientOverviewService.allPageList(DashboardClientOverviewAllREQ.builder().createTo(end).accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
            //存续客户明细
            CompletableFuture<PageR<DashboardClientOverviewSurvivalRSP>> survivalClients = CompletableFuture.supplyAsync(() -> dashboardClientOverviewService.survivalPageList(DashboardClientOverviewSurvivalREQ.builder().createTo(end).accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
            //已结清客户
            CompletableFuture<PageR<DashboardClientOverviewSettledRSP>> settleClients = CompletableFuture.supplyAsync(() -> dashboardClientOverviewService.settledPageList(DashboardClientOverviewSettledREQ.builder().dealLineTo(end).accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build()));
            //逾期客户
            DashboardClientOverviewOverdueREQ clientOverviewOverdueReq = DashboardClientOverviewOverdueREQ.builder()
                    .accountVo(BeanUtil.copyProperties(loginInfo, AccountReq.class)).build();
            clientOverviewOverdueReq.setPage(1);
            clientOverviewOverdueReq.setPageSize(5000);
            //clientOverviewOverdueReq.setBeginTime(LocalDateTime.of(began, time));
            clientOverviewOverdueReq.setEndTime(LocalDateTime.of(end, time));
            CompletableFuture<PageR<DashboardClientOverviewOverdueRSP>> overdueClients = CompletableFuture.supplyAsync(() ->
                    dashboardClientOverviewService.overduePageList(clientOverviewOverdueReq));
            //获取值
            PageR<DashboardClientOverviewAllRSP> allRSPPageR = allClients.get();
            allClientList.getItems().add(ClientUnifiedCustomerTrendsRSP.CustomerTrendsBody.builder().belongTime(end).clientSum(allRSPPageR == null ? 0 : allRSPPageR.getTotal()).build());

            PageR<DashboardClientOverviewSurvivalRSP> survivalRSPPageR = survivalClients.get();
            survivalClientList.getItems().add(ClientUnifiedCustomerTrendsRSP.CustomerTrendsBody.builder().belongTime(end).clientSum(survivalRSPPageR == null ? 0 : survivalRSPPageR.getTotal()).build());

            PageR<DashboardClientOverviewSettledRSP> settleRSPPageR = settleClients.get();
            settleClientList.getItems().add(ClientUnifiedCustomerTrendsRSP.CustomerTrendsBody.builder().belongTime(end).clientSum(settleRSPPageR == null ? 0 : settleRSPPageR.getTotal()).build());

            PageR<DashboardClientOverviewOverdueRSP> overdueRSPPageR = overdueClients.get();
            overdueClientList.getItems().add(ClientUnifiedCustomerTrendsRSP.CustomerTrendsBody.builder().belongTime(end).clientSum(overdueRSPPageR == null ? 0 : overdueRSPPageR.getTotal()).build());
        }
        return rsps;
    }

    private Map<LocalDate, LocalDate> getTime(LocalDate today) {
        Map<LocalDate, LocalDate> dateMap = new HashMap<>();
        if (ObjectUtil.isEmpty(today)) {
            today = LocalDate.now();
        }
        for (int i = 5; i >= 0; i--) {
            LocalDate firstDayOfMonth = today.withDayOfMonth(1).minusMonths(i);
            LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            dateMap.put(firstDayOfMonth, lastDayOfMonth);
        }
        return dateMap;
    }

    private List<ClientUnifiedProjListRSP> establishStatistics(ClientUnifiedApplyCreditREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageEstablishQuery query = new DashboardProjectStageEstablishQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        List<DashboardProjectStageEstablishResult> list = dashboardProjectStageMapper.listProjectOverviewEstablishStage(query);
        if (ObjectUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(list.stream().map(DashboardProjectStageEstablishResult::getBizDeptId).collect(Collectors.toList()));
        List<ClientUnifiedProjListRSP> rsps = new ArrayList<>();
        list.forEach(result -> {
            ClientUnifiedProjListRSP temp = new ClientUnifiedProjListRSP();
            temp.setId(result.getMainId());
            temp.setProjName(result.getProjName());
            temp.setApplyCreditAmount(result.getCreditAmount());
            temp.setBelongDeptId(result.getBizDeptId());
            temp.setBelongDeptName(deptId2Name.get(result.getBizDeptId()));
            temp.setProjectStage(dashboardCardGroupEnum.name());
            temp.setBizType(result.getBizTypeCode());
            rsps.add(temp);
        });
        return rsps;
    }

    private List<ClientUnifiedProjListRSP> reviewStatistics(ClientUnifiedApplyCreditREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, int viewType, AccountVO accountVO) {
        DashboardProjectStageReviewQuery query = new DashboardProjectStageReviewQuery();
        query.setViewType(viewType);
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        List<DashboardProjectStageReviewResult> list = dashboardProjectStageMapper.listProjectOverviewReviewStage(query);
        if (ObjectUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(list.stream().map(DashboardProjectStageReviewResult::getBizDeptId).collect(Collectors.toList()));
        List<ClientUnifiedProjListRSP> rsps = new ArrayList<>();
        list.forEach(result -> {
            ClientUnifiedProjListRSP temp = new ClientUnifiedProjListRSP();
            temp.setId(result.getMainId());
            temp.setProjName(result.getProjName());
            temp.setApplyCreditAmount(result.getCreditAmount());
            temp.setBelongDeptId(result.getBizDeptId());
            temp.setBelongDeptName(deptId2Name.get(result.getBizDeptId()));
            temp.setProjectStage(dashboardCardGroupEnum.name());
            temp.setBizType(result.getBizTypeCode());
            rsps.add(temp);
        });
        return rsps;
    }

    private List<ClientUnifiedProjListRSP> contractStatistics(ClientUnifiedApplyCreditREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageContractQuery query = new DashboardProjectStageContractQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        List<DashboardProjectStageContractResult> list = dashboardProjectStageMapper.listProjectOverviewContractStage(query);
        if (ObjectUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(list.stream().map(DashboardProjectStageContractResult::getBizDeptId).collect(Collectors.toList()));
        List<ClientUnifiedProjListRSP> rsps = new ArrayList<>();
        Map<Long, Long> contractId2EsId = this.contractId2ProjEsId(list.stream().map(DashboardProjectStageContractResult::getMainId).collect(Collectors.toList()));
        list.forEach(result -> {
            ClientUnifiedProjListRSP temp = new ClientUnifiedProjListRSP();
            temp.setId(contractId2EsId.get(result.getMainId()));
            temp.setProjName(result.getProjName());
            temp.setApplyCreditAmount(result.getCreditAmount());
            temp.setBelongDeptId(result.getBizDeptId());
            temp.setBelongDeptName(deptId2Name.get(result.getBizDeptId()));
            temp.setProjectStage(dashboardCardGroupEnum.name());
            temp.setBizType(result.getBizTypeCode());
            rsps.add(temp);
        });
        return rsps;
    }

    private Map<Long, Long> contractId2ProjEsId (List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        Map<Long, Long> contractId2ProjReviewId = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId, (a, b) -> a));
        Map<Long, Long> reviewId2EsId = projReviewBaseInfoService.listByIds(contractId2ProjReviewId.values()).stream().filter(e -> ObjectUtil.isNotEmpty(e.getProjEstablishId())).collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId, (a, b) -> a));
        Map<Long, Long> contractId2EsId = new HashMap<>();
        contractIds.forEach(id -> {
            contractId2EsId.put(id, reviewId2EsId.get(contractId2ProjReviewId.get(id)));
        });
        return contractId2EsId;
    }

    private List<ClientUnifiedProjListRSP> paymentStatistics(ClientUnifiedApplyCreditREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, int viewType, AccountVO accountVO) {
        DashboardProjectStagePaymentQuery query = new DashboardProjectStagePaymentQuery();
        query.setViewType(viewType);
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        List<DashboardProjectStagePaymentResult> list = dashboardProjectStageMapper.listProjectOverviewPaymentStage(query);
        if (ObjectUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(list.stream().map(DashboardProjectStagePaymentResult::getBizDeptId).collect(Collectors.toList()));
        List<PaymentBaseInfo> paymentBaseInfos = SpringContextHolder.getBean(PaymentBaseInfoMapper.class).selectBatchIds(list.stream().map(DashboardProjectStageContractResult::getMainId).collect(Collectors.toList()));
        Map<Long, Long> paymentId2ContractId = paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getContractId, (a, b) -> a));
        Map<Long, Long> contractId2ProjEsId = this.contractId2ProjEsId(paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList()));
        List<ClientUnifiedProjListRSP> rsps = new ArrayList<>();
        list.forEach(result -> {
            ClientUnifiedProjListRSP temp = new ClientUnifiedProjListRSP();
            temp.setId(contractId2ProjEsId.get(paymentId2ContractId.get(result.getMainId())));
            temp.setProjName(result.getProjName());
            temp.setApplyCreditAmount(result.getCreditAmount());
            temp.setBelongDeptId(result.getBizDeptId());
            temp.setBelongDeptName(deptId2Name.get(result.getBizDeptId()));
            temp.setProjectStage(dashboardCardGroupEnum.name());
            temp.setBizType(result.getBizTypeCode());
            rsps.add(temp);
        });
        return rsps;
    }

    private List<ClientUnifiedProjListRSP> repaymentStatistics(ClientUnifiedApplyCreditREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageRepaymentQuery query = new DashboardProjectStageRepaymentQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        List<DashboardProjectStageRepaymentResult> allList = dashboardProjectStageMapper.listProjectOverviewRepaymentStage(query);
        if (ObjectUtil.isEmpty(allList)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStageRepaymentResult> list = new ArrayList<>();
        Set<Long> idSet = new HashSet<>();
        allList.forEach(e -> {
            if (!idSet.contains(e.getMainId())) {
                list.add(e);
                idSet.add(e.getMainId());
            }
        });
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(list.stream().map(DashboardProjectStageRepaymentResult::getBizDeptId).collect(Collectors.toList()));
        Map<Long, Long> contractId2EsId = this.contractId2ProjEsId(list.stream().map(DashboardProjectStageRepaymentResult::getMainId).collect(Collectors.toList()));
        List<ClientUnifiedProjListRSP> rsps = new ArrayList<>();
        list.forEach(result -> {
            ClientUnifiedProjListRSP temp = new ClientUnifiedProjListRSP();
            temp.setId(contractId2EsId.get(result.getMainId()));
            temp.setProjName(result.getProjName());
            temp.setApplyCreditAmount(result.getCreditAmount());
            temp.setBelongDeptId(result.getBizDeptId());
            temp.setBelongDeptName(deptId2Name.get(result.getBizDeptId()));
            temp.setProjectStage(dashboardCardGroupEnum.name());
            temp.setBizType(result.getBizTypeCode());
            rsps.add(temp);
        });
        return rsps;
    }

    public void headBodySupplementOther(HeadBodyRsp rsp, HeadBodyReq req) {
        // 票据逾期
        BillOverdue billOverdue = billOverdueMapper.selectOne(Wrappers.<BillOverdue>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getUscc()), BillOverdue::getOrgCode, req.getUscc())
                .eq(ObjectUtil.isNotEmpty(req.getEnterpriseName()), BillOverdue::getOrgName, req.getEnterpriseName())
                .orderByDesc(BaseModel::getCreateTime).last("limit 1"));
        if (ObjectUtil.isNotEmpty(billOverdue)) {
            rsp.setBillUsagePeriod(billOverdue.getOverdueStartDate());
        }

        // 最新业务日期
        String latestBusiDate = billOverdueMapper.selectLatestBusiDate();
        rsp.setBusiDate(latestBusiDate);
        BillOverdue overdue = billOverdueMapper.selectOne(new LambdaQueryWrapper<BillOverdue>().eq(BillOverdue::getBusiDate, latestBusiDate).eq(BillOverdue::getOrgCode, req.getUscc()));
        if (ObjectUtil.isNotNull(overdue)){
            rsp.setOverdue(true);
            rsp.setOverdueStartDate(overdue.getOverdueStartDate());
        }

        //获取客户信息
        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getEnterpriseName()), Client::getClientName, req.getEnterpriseName())
                .eq(ObjectUtil.isNotEmpty(req.getUscc()), Client::getUscCode, req.getUscc()));
        if (ObjectUtil.isEmpty(client)) {
            rsp.setRegionTab(false);
            return;
        }

        // 查询客户工商信息表，获取风控行业分类
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, client.getId())
                .last("limit 1"));
        if(ObjectUtil.isNotEmpty(corpCommerceInfo)){
            String riskControlIndustryClassify = corpCommerceInfo.getRiskControlIndustryClassify();
            if(ObjectUtil.isNotEmpty(riskControlIndustryClassify)){
                RiskControlIndustryClassify riskControlIndustryClassifyEnum = RiskControlIndustryClassify.valueOf(riskControlIndustryClassify);
                if(Objects.equals(riskControlIndustryClassifyEnum, RiskControlIndustryClassify.PUBLIC_UTILITIES) ||
                        Objects.equals(riskControlIndustryClassifyEnum, RiskControlIndustryClassify.CIVIL_CONSUMPTION)){
                    rsp.setRegionTab(true);
                }
            }else{
                rsp.setRegionTab(false);
            }
        }

        BlackGrayLibraryREQ blackGrayLibraryREQ = new BlackGrayLibraryREQ();
        blackGrayLibraryREQ.setEnterpriseName(client.getClientName());
        blackGrayLibraryREQ.setUnifiedSocialCreditCode(client.getUscCode());
        //获取黑灰名单
        BlackGrayLibraryRSP blackGrayLibraryRSP = blackGrayLibraryService.libraryRecord(blackGrayLibraryREQ);
        if (ObjectUtil.isNotEmpty(blackGrayLibraryRSP)) {
            rsp.setBlackFlag(blackGrayLibraryRSP.getBlackGrayType());
        }
        //逾期金额
        ClientLifeCycleDetailReq clientLifeCycleDetailReq = new ClientLifeCycleDetailReq();
        clientLifeCycleDetailReq.setClientId(client.getId());
        ClientLifeCycleReceiptRsp receipt = clientLifeCycleService.receipt(clientLifeCycleDetailReq);
        if (ObjectUtil.isNotEmpty(receipt)) {
            rsp.setOverdueAmount(receipt.getOverdueAmount());
        }
        CorpSubjectItemListREQ corpSubjectItemListREQ = new CorpSubjectItemListREQ();
        corpSubjectItemListREQ.setClientId(client.getId());
        corpSubjectItemListREQ.setReportType(JSONUtil.toJsonStr(Arrays.stream(SubjectItemType.values()).map(SubjectItemType::name).collect(Collectors.toList())));
        corpSubjectItemListREQ.setSubjectType(SubjectItemType.CAPITAL_BALANCE.name());
        corpSubjectItemListREQ.setLatest(Boolean.TRUE);
        corpSubjectItemListREQ.setDisplayDimensions(ListUtil.toList(SubjectItemDisplayDimension.BASE.name(), SubjectItemDisplayDimension.PERCENT.name(), SubjectItemDisplayDimension.OVER_YEAR.name()));
        R<List<CorpSubjectItemListRSP>> corpSubjectRsp = SpringContextHolder.getBean(CorpSubjectItemApplicationService.class).list(corpSubjectItemListREQ);
        if (ObjectUtil.isNotEmpty(corpSubjectRsp) && ObjectUtil.isNotEmpty(corpSubjectRsp.getData())) {
            rsp.setDataYear(corpSubjectRsp.getData().get(0).getYear());
            rsp.setReportPeriod(corpSubjectRsp.getData().get(0).getQuarter());
        }

    }
    public ClientClassificationRSP classification(ClientUnifiedApplyCreditREQ req) {
        ClientClassificationRSP rsp = new ClientClassificationRSP();
        /*通过客户信息找到所有的合同信息*/
        LambdaQueryWrapper<AssetClassifyClient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetClassifyClient::getClientId, req.getClientId());
        wrapper.orderByDesc(AssetClassifyClient::getCreateTime);
        List<AssetClassifyClient> list = assetClassifyClientService.list(wrapper);
        if(list.size() >0){
            AssetClassifyClient assetClassifyClient = list.get(0);
            /*如果有建议五级分类优先   否则取初分分类结果*/
            rsp.setClassification(fiveClassMap.get(assetClassifyClient.getSuggestResult()));
            if(rsp.getClassification() == null){
                rsp.setClassification(fiveClassMap.get(assetClassifyClient.getInitClassifyResult()));
            }
        }
        Map<Long, ClientClassificationRSP.OvedueContarctRSP> ovedueContarctRSPMap = new HashMap<>();
        List<OcContractDto> ocContractDtos = contractBaseInfoMapper.ocContractListByClientId(req.getClientId());
        for(OcContractDto dto : ocContractDtos){
            if(dto.getOverdueAmount() == 0 || dto.getOverdueDays() == 0){
                continue;
            }
            ClientClassificationRSP.OvedueContarctRSP  ovedueContarctRSP;
            if(ovedueContarctRSPMap.containsKey(dto.getContractId())){
                ovedueContarctRSP = ovedueContarctRSPMap.get(dto.getContractId());
                ovedueContarctRSP.setPhase(ovedueContarctRSP.getPhase() + "," +dto.getPhase());
                if(ovedueContarctRSP.getOverdueDays() < dto.getOverdueDays()) {
                    ovedueContarctRSP.setOverdueDays(dto.getOverdueDays());
                }
                ovedueContarctRSP.setOverdueAmount(ovedueContarctRSP.getOverdueAmount() + dto.getOverdueAmount());
            }else{
                ovedueContarctRSP = new ClientClassificationRSP.OvedueContarctRSP();
                ovedueContarctRSP.setContractCode(dto.getContractCode());
                ovedueContarctRSP.setPhase(dto.getPhase());
                ovedueContarctRSP.setOverdueDays(dto.getOverdueDays());
                ovedueContarctRSP.setOverdueAmount(dto.getOverdueAmount());
                ovedueContarctRSPMap.put(dto.getContractId(), ovedueContarctRSP);
            }
            if(rsp.getMaxOverdueDayCount() < dto.getOverdueDays()){
                rsp.setMaxOverdueDayCount(dto.getOverdueDays());
            }
            rsp.setTotalAmountOverdue(rsp.getTotalAmountOverdue() + dto.getOverdueAmount());
        }
        List<ClientClassificationRSP.OvedueContarctRSP> ovedueContarctRSPList = new ArrayList<>(ovedueContarctRSPMap.values());
        rsp.setContarctRSPList(ovedueContarctRSPList);
        return rsp;
    }

}
