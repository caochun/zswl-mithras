package cn.zswltech.mithras.others.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.capital.job.CapitalBankFlowHotFixJob;
import cn.zswltech.mithras.capital.job.ManualWriteOffReleaseBankFlowJob;
import cn.zswltech.mithras.contract.job.ContractJob;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import cn.zswltech.mithras.metric.job.MonthlyTaskGenerator;
import cn.zswltech.mithras.capital.job.CQFinanceJob;
import cn.zswltech.mithras.capital.job.FinanceAutoWriteOffJob;
import cn.zswltech.mithras.kpi.job.KpiDeptWeightDataInitJob;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.base.model.CrOverdueRecordBase;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrOverdueRecordDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.service.formal.CrRepayPlanService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.job.RentRepayNoticeFinanceJob;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.job.*;
import cn.zswltech.mithras.projectprocess.job.data_init.ProjRiskControlIndustryTypeJob;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.service.service.dashboard.DashboardFundFinanceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.ChineseToPinyinUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author luyi
 */
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestFillClosedDate {

    @Resource
    private CapitalBankFlowHotFixJob capitalBankFlowHotFixJob;

    @Test
    public void setHotFixJob() {
        capitalBankFlowHotFixJob.financeFlowRecordJob();
    }

    @Resource
    private ManualWriteOffReleaseBankFlowJob manualWriteOffReleaseBankFlowJob;

    @Test
    public void testReleaseBankFlowJob() {
        manualWriteOffReleaseBankFlowJob.releaseBankFlow();
    }


    @Resource
    private ContractJob contractJob;

    @Test
    public void jobTest1() {
        contractJob.tryAutoStartRentJob();
    }

    @Test
    public void testAutoWriteOff() {
        getBean(FinanceAutoWriteOffJob.class).financeFlowAutoWriteOffJob();
    }

    @Test
    public void sendWriteOffNotice() {
        getBean(CQFinanceJob.class).sendWriteOffNotice();
    }

    @Autowired
    private CrFacade crFacade;
    @Autowired
    private List<CrAbstractHandler> crHandlerList;

    @Test
    public void sync() {
        LocalDateTime dateTime = LocalDateTimeUtil.offset(LocalDateTime.now(), 1, ChronoUnit.DAYS);
        crFacade.handle(LocalDateTimeUtil.beginOfDay(dateTime).plusMinutes(15));
//        crFacade.handle(LocalDateTimeUtil.beginOfDay(LocalDateTime.now()));
    }

    @Test
    public void sort() {
        crHandlerList.sort(Comparator.comparing(CrAbstractHandler::sort));
        for (CrAbstractHandler handler : crHandlerList) {
            System.out.println(handler.toString());
        }
    }

    @Test
    public void testUpdateNull() {
        accountDraftService.lambdaUpdate()
                .eq(CrAccountDraft::getContractId, 4309L)
                .set(CrAccountDraft::getClosedDate, null)
                .update();
    }

    @Test
    public void testPayment() {
        PaymentBaseInfo baseInfo = paymentBaseInfoService.getById(5119L);
        //getBean(FinanceFlowAutoWriteOffService.class).autoWriteOffSomeAmount(baseInfo);
    }

    @Autowired
    private NewFtpJob newFtpJob;

    @Autowired
    private RentRepayNoticeFinanceJob rentRepayNoticeFinanceJob;

    @Test
    public void testJob() {
        newFtpJob.calculateFtpFinancingCostPricing();
        //getBean(NewFtpFinancingCostPricingConfigService.class).add(LocalDate.now().minusMonths(1));
    }


    @Autowired
    ContractRentActualLibMapper contractRentActualLibMapper;
    @Autowired
    CrAccountDraftService accountDraftService;
    @Autowired
    CrRepayPlanDraftService crRepayPlanDraftService;
    @Autowired
    PaymentBaseInfoService paymentBaseInfoService;
    @Value("${report.overdue.days}")
    String days;

    @Resource
    private DashboardFundFinanceService dashboardFundFinanceService;

    @Test
    public void testFund() {
        DashboardFundFinanceLoanInfoREQ loanInfoREQ = new DashboardFundFinanceLoanInfoREQ();
        loanInfoREQ.setPage(1);
        loanInfoREQ.setPageSize(Integer.MAX_VALUE);
        List<DashboardFundFinanceLoanInfoRSP> loanInfoRSPList = dashboardFundFinanceService.listLoanInfo(loanInfoREQ).getList();

        DashboardFundFinanceBalanceREQ balanceREQ = new DashboardFundFinanceBalanceREQ();
        balanceREQ.setPage(1);
        balanceREQ.setPageSize(Integer.MAX_VALUE);
        List<DashboardFundFinanceBalanceRSP> balanceRSPList = dashboardFundFinanceService.listBalance(balanceREQ).getList();

        List<String> l1 = loanInfoRSPList.stream()
                .filter(a -> !balanceRSPList.stream().map(DashboardFundFinanceBalanceRSP::getIdKey).collect(Collectors.toList()).contains(a.getIdKey()))
                .map(a -> a.getIdKey().substring(a.getIdKey().indexOf("_")))
                .collect(Collectors.toList());
        List<String> l2 = balanceRSPList.stream()
                .filter(a -> !loanInfoRSPList.stream().map(DashboardFundFinanceLoanInfoRSP::getIdKey).collect(Collectors.toList()).contains(a.getIdKey()))
                .map(DashboardFundFinanceBalanceRSP::getFinancingCode)
                .collect(Collectors.toList());

        log.info("");
    }


    @Resource
    private NewFtpJob job;

    @Test
    public void jobTest() {
        job.calculateFtpFinancingCostPricing();
    }

    /**
     * 拆分单个合同的还款计划，用于数据订正
     */
    @Test
    public void insertReplyPlan() {
        //查询实际租金表并根据期项映射
        List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                .eq(ContractRentActualLib::getContractId, 1106L)
                .eq(ContractRentActualLib::getReceiptId, 3071L)
                .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                .eq(ContractRentActualLib::getVersion, "000320230529")
                .ne(ContractRentActualLib::getCashFlowCode, "")
                .isNotNull(ContractRentActualLib::getCashFlowCode)
        );
        if (CollUtil.isEmpty(contractRentActualLibs)) {
            //当前时间段没有新的租金表生成
            return;
        }
        Map<Integer, ContractRentActualLib> integerContractRentActualLibMap = contractRentActualLibs
                .stream().collect(Collectors.toMap(ContractRentActualLib::getCashFlowPhase, Function.identity(), (k1, k2) -> k1));

        //当前付款申请的所有金额之和，后面分表更新需要根据这个值计算比例
        List<CrAccountDraft> accountDrafts = accountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery().eq(CrBaseModel::getContractId, 1106L));
        long sum = accountDrafts.stream().mapToLong(CrAccountDraft::getPaymentAmount).sum();
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(982L);
        if (sum == 0) {
            return;
        }
        //还没有还款表，需要进行新增
        List<CrRepayPlanDraft> needInsertList = new LinkedList<>();
        for (ContractRentActualLib actualLib : integerContractRentActualLibMap.values()) {
            long totalRent = 0L;
            long totalPrincipal = 0L;
            for (int i = 0; i < accountDrafts.size(); i++) {
                CrAccountDraft accountDraft = accountDrafts.get(i);
                BigDecimal decimal = BigDecimal.valueOf(sum);
                long principal = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(Optional.ofNullable(actualLib.getPrincipal()).orElse(0L))
                        .multiply(BigDecimal.valueOf(accountDraft.getPaymentAmount()))
                        .divide(decimal, 30, RoundingMode.HALF_UP).longValue());
                long rent = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(Optional.ofNullable(actualLib.getRent()).orElse(0L))
                        .multiply(BigDecimal.valueOf(accountDraft.getPaymentAmount()))
                        .divide(decimal, 30, RoundingMode.HALF_UP).longValue());
                if (i == accountDrafts.size() - 1) {
                    principal = Optional.ofNullable(actualLib.getPrincipal()).orElse(0L) - totalPrincipal;
                    rent = Optional.ofNullable(actualLib.getRent()).orElse(0L) - totalRent;
                }
                CrRepayPlanDraft draft = buildCrRepayPlan(paymentBaseInfo, rent, principal, actualLib, accountDraft);
                needInsertList.add(draft);
                totalPrincipal += draft.getPrincipal();
                totalRent += draft.getRent();
            }
        }
        crRepayPlanDraftService.saveBatch(needInsertList);
    }

    private CrRepayPlanDraft buildCrRepayPlan(PaymentBaseInfo paymentBaseInfo, long rent, long principal, ContractRentActualLib actualLib, CrAccountDraft accountDraft) {
        CrRepayPlanDraft build = new CrRepayPlanDraft();
        build.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
        build.setProcBusinessKey(null);
        build.setPlanPenaltyInterest(0L);
        build.setAlreadyPenaltyInterest(0L);
        build.setReportState(ReportState.TO_BE_REPORT.name());
        build.setPrincipal(principal);
        build.setPhase(actualLib.getCashFlowPhase());
        build.setRent(rent);
        build.setPaymentId(paymentBaseInfo.getId());
        build.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
        build.setCashFlowDate(actualLib.getCashFlowDate());
        build.setGracePeriod(days);
        build.setBusinessKey(build.genBusinessKey());
        build.setContractId(paymentBaseInfo.getContractId());
        return build;
    }


    @Resource
    private FileTemplateService fileTemplateService;

    @Test
    public void testFileTemplate() {
        List<FileTemplate> effectList = fileTemplateService.list(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getOutdated, 0));
        List<FileTemplate> needUpdateList = new LinkedList<>();
        for (FileTemplate fileTemplate : effectList) {
            String filename = fileTemplate.getFilename();
            // 需要初始化文件模版的Key
            String string = filename.substring(0, filename.lastIndexOf("."));
            String substring = string;
            if (substring.contains(".")) {
                substring = string.substring(string.lastIndexOf(".") + 1);
            }
            String pinYinHeadChar = ChineseToPinyinUtil.getPinYinHeadChar(substring.replaceAll("（", "-").replaceAll("）", ""));
            FileTemplate e = new FileTemplate();
            e.setFileTemplateKey("MB_" + pinYinHeadChar);
            e.setFaceSignShowFlag(0);
            e.setId(fileTemplate.getId());
            needUpdateList.add(e);
        }
        fileTemplateService.updateBatchById(needUpdateList);
        Map<String, FileTemplate> fileTemplateMap = effectList.stream().collect(Collectors.toMap(FileTemplate::getFilename, Function.identity(), (k1, k2) -> k1));

        // 需要更新历史文件模版的Key
        List<FileTemplate> historyList = fileTemplateService.list(Wrappers.<FileTemplate>lambdaQuery()
                .eq(FileTemplate::getOutdated, 1));
        List<FileTemplate> needUpdateHistoryList = new LinkedList<>();
        for (FileTemplate fileTemplate : historyList) {
            FileTemplate template = fileTemplateMap.get(fileTemplate.getFilename());
            fileTemplate.setFileTemplateKey(template.getFileTemplateKey());
            fileTemplate.setFaceSignShowFlag(0);
            needUpdateHistoryList.add(fileTemplate);
        }
        fileTemplateService.updateBatchById(needUpdateHistoryList);

        // 上线的时候需要初始化历史文件模版的Key
        String[] arr = new String[]{"合同模版展示清单：",
                "船舶_融资租赁合同_直租",
                "1.融资租赁合同（直租）",
                "船舶_买卖合同_直租",
                "1-1.直租买卖合同（可根据实际情况修改）",
                "船舶_融资租赁合同_回租",
                "船舶_买卖合同_回租",
                "合同_租赁合同_主合同_回租_共同承租人",
                "合同_租赁合同_主合同_回租_单一承租人",
                "合同_保证合同_法人",
                "合同_保证合同_自然人",
                "合同_抵押合同_动产_一般抵押",
                "合同_抵押合同_附属_抵押物清单",
                "5.质押合同-股权",
                "6.质押合同-应收账款",
                "合同_咨询合同_共同承租人",
                "合同_咨询合同_单一承租人"};
        List<FileTemplate> needUpdateList1 = new LinkedList<>();
        for (FileTemplate template : effectList) {
            if (Arrays.stream(arr).anyMatch(s -> template.getFilename().contains(s))) {
                FileTemplate e = new FileTemplate();
                e.setId(template.getId());
                e.setFaceSignShowFlag(1);
                needUpdateList1.add(e);
            }
        }
        fileTemplateService.updateBatchById(needUpdateList1);
    }

    @Test
    public void testInitTemplateKey() {
        List<FileTemplate> list = fileTemplateService.list();
        StringBuilder builder = new StringBuilder();
        list.forEach(e -> {
            String string = e.getFilename().substring(0, e.getFilename().lastIndexOf("."));
            String substring = string;
            if (substring.contains(".")) {
                substring = string.substring(string.lastIndexOf(".") + 1);
            }
            String pinYinHeadChar = ChineseToPinyinUtil.getPinYinHeadChar(substring.replaceAll("（", "-").replaceAll("）", ""));
            builder.append("UPDATE file_template SET file_template_key = 'MB_").append(pinYinHeadChar).append("' WHERE id = ").append(e.getId()).append(";");
        });
        log.info(builder.toString());
    }


    @Autowired
    private ReportDataRepository reportDataRepository;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;

    @Test
    public void testSql() {
        LocalDateTime dealTime = LocalDate.now().atStartOfDay();
        LocalDateTime lastDealTime = LocalDate.now().minusDays(1).atStartOfDay();
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTable(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            paymentBaseInfoList = Collections.emptyList();
        }

        //提前查询数据库，提升效率
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        Set<Long> receiptIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet());
        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, paymentIds)
        ).stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));

        Map<Long, List<ContractReceiptLib>> paymentIdReceiptListMap = contractReceiptLibMapper.selectList(Wrappers.<ContractReceiptLib>lambdaQuery()
                .in(ContractReceiptLib::getOriginId, receiptIds)
        ).stream().collect(Collectors.groupingBy(ContractReceiptLib::getOriginId));

        List<PaymentBaseInfo> paymentBaseInfoTempList = new ArrayList<>();
        // 过滤掉没有实际租金表变更的数据
        paymentBaseInfoList.forEach(paymentBaseInfo -> {
            //拿到当前付款申请的所有账户
            List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
            if (CollUtil.isEmpty(accountDrafts) || Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
                //还没有账户
                return;
            }
            List<ContractReceiptLib> contractReceiptLibs = paymentIdReceiptListMap.get(paymentBaseInfo.getReceiptIdFinal());
            contractReceiptLibs.sort(Comparator.comparing(ContractReceiptLib::getVersion).reversed());
            ContractReceiptLib contractReceiptLib = contractReceiptLibs.get(0);
            //查询实际租金表并根据期项映射
            List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                    .eq(ContractRentActualLib::getContractId, paymentBaseInfo.getContractId())
                    .eq(ContractRentActualLib::getReceiptId, contractReceiptLib.getOriginId())
                    .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractRentActualLib::getVersion, contractReceiptLib.getVersion())
                    .gt(ContractRentActualLib::getCreateTime, lastDealTime)
                    .le(ContractRentActualLib::getCreateTime, dealTime)
                    .ne(ContractRentActualLib::getCashFlowCode, "")
                    .isNotNull(ContractRentActualLib::getCashFlowCode)
            );
            if (CollUtil.isNotEmpty(contractRentActualLibs)) {
                //当前时间段没有新的租金表生成
                paymentBaseInfoTempList.add(paymentBaseInfo);
            }
        });
        paymentBaseInfoTempList.forEach(System.out::println);
    }


    @Test
    public void overdueTest() {
        List<CollectionBaseInfo> overdueCollectionBaseInfoList = SpringContextHolder.getBean(CollectionBaseInfoMapper.class)
                .selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .ne(CollectionBaseInfo::getPhase, 0)
                        .le(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                );
        List<CrOverdueRecordDraft> needInsertData = new ArrayList<>();
        if (CollUtil.isNotEmpty(overdueCollectionBaseInfoList)) {
            //过滤掉直租的合同
            Set<Long> contractIds = SpringContextHolder.getBean(ContractBaseInfoLibMapper.class).selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                            .in(ContractBaseInfoLib::getOriginId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()))
                            .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL))
                    .stream()
                    .filter(a -> !LeaseType.zhi_zu.name().equals(a.getLeaseType()))
                    .map(ContractBaseInfoLib::getOriginId)
                    .collect(Collectors.toSet());

            //实际过滤
            overdueCollectionBaseInfoList = overdueCollectionBaseInfoList.stream()
                    .filter(a -> contractIds.contains(a.getContractId()))
                    .collect(Collectors.toList());

            //这里需要查询还款计划表，找出宽限期，然后做过滤
            Map<Long, Map<Integer, List<CrRepayPlanDraft>>> planMap = new HashMap<>(8);
            Map<Long, List<CrRepayPlanDraft>> contractPlanListMap = SpringContextHolder.getBean(CrRepayPlanDraftService.class).list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                            .in(CrRepayPlanDraft::getContractId, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()))
                            .in(CrRepayPlanDraft::getPhase, overdueCollectionBaseInfoList.stream().map(CollectionBaseInfo::getPhase).collect(Collectors.toList())))
                    .stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getContractId));

            contractPlanListMap.forEach((contractId, planList) -> {
                planMap.put(contractId, planList.stream().collect(Collectors.groupingBy(CrRepayPlanDraft::getPhase)));
            });

            //中间变量，用户存储过滤后需要处理的数据
            List<CollectionBaseInfo> needHandleDataList = new ArrayList<>(overdueCollectionBaseInfoList.size());
            overdueCollectionBaseInfoList.sort(Comparator.comparing(CollectionBaseInfo::getPhase));
            for (CollectionBaseInfo collectionBaseInfo : overdueCollectionBaseInfoList) {
                //没有数据的话需要考虑宽限期, 但是前提是现在改合同在征信模块没有逾期信息
                Map<Integer, List<CrRepayPlanDraft>> integerListMap = planMap.get(collectionBaseInfo.getContractId());
                if (CollUtil.isEmpty(integerListMap)) {
                    continue;
                }
                List<CrRepayPlanDraft> crRepayPlanDrafts = integerListMap.get(collectionBaseInfo.getPhase());
                if (CollUtil.isEmpty(crRepayPlanDrafts)) {
                    log.error(String.format("合同ID为%s, 期项为%s的还款计划为空, 请业务验证数据的正确性！", collectionBaseInfo.getContractId(), collectionBaseInfo.getPhase()));
                    continue;
                }
                for (CrRepayPlanDraft planDraft : crRepayPlanDrafts) {
                    LocalDate gracePeriodDate = LocalDate.now().minusDays(Long.valueOf(planDraft.getGracePeriod()));
                    if (collectionBaseInfo.getPlanCollectionDate().isBefore(gracePeriodDate)) {
                        //还款时间比现在到过去宽限期的天数还要小，则认为是逾期， 并且多账户只需要添加一次
                        needHandleDataList.add(collectionBaseInfo);
                        break;
                    }
                }
            }
            overdueCollectionBaseInfoList.forEach(System.out::println);
        }
    }

    @Resource
    private CrOverdueRecordDraftService crOverdueRecordDraftService;
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Test
    public void runOverdueRecord() {
        reRunOverdueRecord(5);
    }

    public void reRunOverdueRecord(Integer preDays) {
        List<CrOverdueRecordDraft> needHandleDataList = new LinkedList<>();
        // 先找到缺失记录的最新一条数据
        List<CrOverdueRecordDraft> crOverdueRecordDrafts = crOverdueRecordDraftService.list(Wrappers.<CrOverdueRecordDraft>lambdaQuery()
                .eq(CrOverdueRecordBase::getOverdueChangeDate, LocalDate.of(2025, 7, 10)));
        for (CrOverdueRecordDraft draft : crOverdueRecordDrafts) {
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, draft.getContractId())
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .isNotNull(CollectionBaseInfo::getCode));
            for (int i = 1; i <= preDays; i++) {
                // 找到预期记录
                List<CollectionOverdueRecordInfo> overdueRecordInfos = collectionOverdueRecordInfoService.list(Wrappers.<CollectionOverdueRecordInfo>lambdaQuery()
                        .in(CollectionOverdueRecordInfo::getCollectionId, collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()))
                        .le(CollectionOverdueRecordInfo::getRecordDate, draft.getOverdueChangeDate())
                        .gt(CollectionOverdueRecordInfo::getRecordDate, draft.getOverdueChangeDate().minusDays(i)));
                List<CollectionOverdueRecordInfo> recordInfos = collectionOverdueRecordInfoService.list(Wrappers.<CollectionOverdueRecordInfo>lambdaQuery()
                        .in(CollectionOverdueRecordInfo::getCollectionId, collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()))
                        .eq(CollectionOverdueRecordInfo::getRecordDate, draft.getOverdueChangeDate().minusDays(i)));
                if (CollUtil.isEmpty(recordInfos) || draft.getOverdueDay() - i < 11) {
                    continue;
                }

                // 生成前i天的预期记录
                CrOverdueRecordDraft newRecord = BeanUtil.copyProperties(draft, CrOverdueRecordDraft.class, GlobalConstants.COPY_IGNORE_COMMON_FIELD);
                newRecord.setGenDate(draft.getGenDate().minusDays(i));
                newRecord.setBusinessKey(draft.genBusinessKey(newRecord.getGenDate()));
                newRecord.setOverdueChangeDate(draft.getOverdueChangeDate().minusDays(i));
                newRecord.setOverdueDay(draft.getOverdueDay() - i);
                newRecord.setOverdueTotal(draft.getOverdueTotal() - overdueRecordInfos.stream().mapToLong(CollectionOverdueRecordInfo::getDayPenaltyInterest).summaryStatistics().getSum());
                needHandleDataList.add(newRecord);
            }
        }
        crOverdueRecordDraftService.saveBatch(needHandleDataList);
    }


    // 将生效的数据覆盖编辑区
    @Test
    public void copyEffectiveDataToEditArea() {
        // 获取生效数据
        List<CrRepayPlan> effectiveData = getBean(CrRepayPlanService.class).list(Wrappers.<CrRepayPlan>lambdaQuery()
                .eq(CrRepayPlan::getContractId, 1464L));
        // 获取编辑区数据
        List<CrRepayPlanDraft> editAreaData = getBean(CrRepayPlanDraftService.class).list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .eq(CrRepayPlanDraft::getContractId, 1464L));
        // 将两组数据按照业务Key和气象台做映射
        Map<String, CrRepayPlan> effectiveDataMap = effectiveData.stream().collect(Collectors.toMap(a -> a.getPaymentApplyCode() + a.getPhase(), Function.identity()));
        Map<String, CrRepayPlanDraft> editAreaDataMap = editAreaData.stream().collect(Collectors.toMap(a -> a.getPaymentApplyCode() + a.getPhase(), Function.identity()));
        // 将生效数据覆盖编辑区数据，跳过期项6
        List<StringBuilder> updateSqlList = new LinkedList<>();
        for (Map.Entry<String, CrRepayPlanDraft> entry : editAreaDataMap.entrySet()) {
            CrRepayPlan effectiveDataItem = effectiveDataMap.get(entry.getKey());
            if (effectiveDataItem != null && effectiveDataItem.getPhase() != 6) {
                StringBuilder sb = new StringBuilder("UPDATE cr_repay_plan_draft SET rent = ");
                sb.append(effectiveDataItem.getRent())
                        .append(", principal = ").append(effectiveDataItem.getPrincipal())
                        .append(" WHERE id = ").append(entry.getValue().getId()).append(";");
                updateSqlList.add(sb);
            }
        }

        // 获取生效数据
        List<CrRepayPlan> effectiveData1 = getBean(CrRepayPlanService.class).list(Wrappers.<CrRepayPlan>lambdaQuery()
                .eq(CrRepayPlan::getContractId, 987L));
        // 获取编辑区数据
        List<CrRepayPlanDraft> editAreaData1 = getBean(CrRepayPlanDraftService.class).list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .eq(CrRepayPlanDraft::getContractId, 987L));
        // 将两组数据按照业务Key和气象台做映射
        Map<String, CrRepayPlan> effectiveDataMap1 = effectiveData1.stream().collect(Collectors.toMap(a -> a.getPaymentApplyCode() + a.getPhase(), Function.identity()));
        Map<String, CrRepayPlanDraft> editAreaDataMap1 = editAreaData1.stream().collect(Collectors.toMap(a -> a.getPaymentApplyCode() + a.getPhase(), Function.identity()));

        List<StringBuilder> updateSqlList1 = new LinkedList<>();
        for (Map.Entry<String, CrRepayPlanDraft> entry : editAreaDataMap1.entrySet()) {
            CrRepayPlan effectiveDataItem = effectiveDataMap1.get(entry.getKey());
            if (effectiveDataItem != null && effectiveDataItem.getPhase() <= 12) {
                StringBuilder sb = new StringBuilder("UPDATE cr_repay_plan_draft SET rent = ");
                sb.append(effectiveDataItem.getRent())
                        .append(", principal = ").append(effectiveDataItem.getPrincipal())
                        .append(" WHERE id = ").append(entry.getValue().getId()).append(";");
                updateSqlList1.add(sb);
            }
        }
        for (StringBuilder sb : updateSqlList) {
            System.out.println(sb);
        }
        for (StringBuilder sb : updateSqlList1) {
            System.out.println(sb);
        }
    }

    @Resource
    KpiDeptWeightDataInitJob kpiDeptWeightDataInitJob;

    @Test
    public void testKpiDeptWeightDataInitJob() {
        kpiDeptWeightDataInitJob.kpiDeptWeightDataInitJob();
    }


    @Resource
    private MonthlyTaskGenerator monthlyTaskGenerator;

    @Test
    public void testMonthlyTaskGenerator() {
        monthlyTaskGenerator.generate();
    }


    @Test
    public void testProjRiskControlIndustryTypeJob() {
        SpringContextHolder.getBean(ProjRiskControlIndustryTypeJob.class).projRiskControlIndustryTypeJob();
    }
}
