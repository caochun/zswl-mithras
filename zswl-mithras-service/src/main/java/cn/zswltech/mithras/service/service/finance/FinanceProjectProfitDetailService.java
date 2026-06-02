package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.finance.FinanceProjectCalculationREQ;
import cn.zswltech.mithras.dto.finance.FinanceProjectProfitDetailREQ;
import cn.zswltech.mithras.dto.finance.FinanceProjectProfitDetailRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ExpenseRadioConfig;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.service.enums.projestablish.FactoringType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.finance.FinanceProjectProfitDetailMapper;
import cn.zswltech.mithras.service.mapper.finance.query.FinanceProjectProfitDetailQuery;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.service.mapper.model.finance.ContractAssessDeptDetail;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProvisionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProvisionDetail;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCost;
import cn.zswltech.mithras.service.mapper.model.monthly.MonthlyStampDuty;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.bo.FinanceProfitHelperBO;
import cn.zswltech.mithras.service.service.bo.FinanceProjectProfitDetailBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostService;
import cn.zswltech.mithras.service.service.monthly.MonthlyStampDutyService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.third.jk.JinKongMonthlyReportService;
import cn.zswltech.mithras.third.service.jk.res.ReportBcmBalanceMfRes;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Slf4j
@Service
public class FinanceProjectProfitDetailService extends ServiceImpl<FinanceProjectProfitDetailMapper, FinanceProjectProfitDetail> {
    private static final String INCOME_ACCOUNT_NO = "6001";

    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private FinanceProjectProfitService kpiProjectProfitService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;
    @Resource
    private FinanceProjectProfitService financeProjectProfitService;
    @Resource
    @Lazy
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private KpiProvisionBaseInfoService kpiProvisionBaseInfoService;
    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;
    @Resource
    private ContractAssessDeptDetailService contractAssessDeptDetailService;
    @Resource
    private OrgDOMapper orgDOMapper;
    public void removeByProjectProfitId(Long projectProfitId) {
        LambdaQueryWrapper<FinanceProjectProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfitDetail::getProjectProfitId, projectProfitId);
        this.remove(query);
    }

    public FinanceProjectProfitDetail getLatestOneByContractId(Long contractId) {
        LambdaQueryWrapper<FinanceProjectProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfitDetail::getContractId, contractId);
        query.orderByDesc(FinanceProjectProfitDetail::getYear);
        query.orderByDesc(FinanceProjectProfitDetail::getMonth);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<FinanceProjectProfitDetail> listHasIncomeContractDetail(int year, int month) {
        FinanceProjectProfit kpiProjectProfit = kpiProjectProfitService.getOneByYearMonth(year, month);
        if (Objects.isNull(kpiProjectProfit)) {
            log.error("没有对应月份的项目利润数据[year:{}, month:{}]", year, month);
            return null;
        }
        List<FinanceProjectProfitDetail> detailList = this.listByProjectProfitId(kpiProjectProfit.getId());
        if (CollectionUtil.isEmpty(detailList)) {
            log.error("没有对应月份的项目利润详情数据[projectProfitId:{}]", kpiProjectProfit.getId());
            return null;
        }
        // 去掉营收小于等于0的
        Iterator<FinanceProjectProfitDetail> iterator = detailList.iterator();
        while (iterator.hasNext()) {
            FinanceProjectProfitDetail detail = iterator.next();
            if (Objects.nonNull(detail.getIncomeThisMonth())) {
                BigDecimal b = new BigDecimal(detail.getIncomeThisMonth());
                int i = b.compareTo(BigDecimal.ZERO);
                if (i <= 0) {
                    iterator.remove();
                }
            }
        }
        return detailList;
    }

    public List<FinanceProjectProfitDetail> listByProjectProfitId(Long projectProfitId) {
        LambdaQueryWrapper<FinanceProjectProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfitDetail::getProjectProfitId, projectProfitId);
        return this.list(query);
    }

    public FinanceProjectProfitDetail getSpecificOne(Long contractId, int year, int month) {
        LambdaQueryWrapper<FinanceProjectProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfitDetail::getContractId, contractId);
        query.eq(FinanceProjectProfitDetail::getYear, year);
        query.eq(FinanceProjectProfitDetail::getMonth, month);
        List<FinanceProjectProfitDetail> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new MithrasException("同一月份存在多条项目利润详情数据");
        }
        return list.get(0);
    }

    public PageR<FinanceProjectProfitDetailRSP> pageList(FinanceProjectProfitDetailREQ req) {
        FinanceProjectProfitDetailQuery query = this.buildQuery(req);
        int count = this.getBaseMapper().myPageListCount(query);
        if (count == 0) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<FinanceProjectProfitDetailBO> dbList = this.getBaseMapper().myPageList(query);
        Map<Long, String> orgMap = orgDOMapper.selectAll().stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName));
        Set<Long> clientIds = dbList.stream().map(FinanceProjectProfitDetailBO::getClientId).collect(Collectors.toSet());
        List<Client> clientList = clientService.listByIds(clientIds);
        Map<Long, String> clientVersionMap = clientList.stream().collect(Collectors.toMap(Client::getId, Client::getNewestVersion));
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoLibMap = corpCommerceInfoLibService.getSpecificVersionMap(clientVersionMap);
        List<FinanceProjectProfitDetailRSP> rspList = dbList.stream().map(item -> {
            FinanceProjectProfitDetailRSP rsp = BeanUtil.copyProperties(item, FinanceProjectProfitDetailRSP.class);
            //考核部门ID不存在，考核部门=业务部门
            if (Objects.nonNull(item.getAssessDeptId())) {
                rsp.setAssessDeptName(orgMap.get(item.getAssessDeptId()));
            }else {
                rsp.setAssessDeptName(orgMap.get(item.getBizDeptId()));
            }
            // 特殊参数处理
            if (Objects.nonNull(item.getClientId())) {
                CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMap.get(item.getClientId());
                if (Objects.nonNull(corpCommerceInfoLib)) {
                    RiskControlIndustryClassify riskControlIndustryClassify = RiskControlIndustryClassify.findByName(corpCommerceInfoLib.getRiskControlIndustryClassify());
                    rsp.setRiskControlIndustryClassify(Optional.ofNullable(riskControlIndustryClassify).map(RiskControlIndustryClassify::display).orElse(null));
                }
            }
            if (StrUtil.isNotBlank(item.getBizType())) {
                ProjectBizType projectBizType = ProjectBizType.of(item.getBizType());
                rsp.setBizType(Optional.ofNullable(projectBizType).map(ProjectBizType::display).orElse(null));
                if (Objects.nonNull(projectBizType)) {
                    switch (projectBizType) {
                        case ZL:
                        case ZZ: {
                            LeaseType leaseType = LeaseType.of(item.getLeaseType());
                            rsp.setBizSubType(Optional.ofNullable(leaseType).map(LeaseType::display).orElse(null));
                            break;
                        }
                        case BL: {
                            FactoringType factoringType = FactoringType.of(item.getFactoringType());
                            rsp.setBizSubType(Optional.ofNullable(factoringType).map(FactoringType::display).orElse(null));
                            break;
                        }
                    }
                }
            }
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, count, req.getPage(), req.getPageSize());
    }

    private FinanceProjectProfitDetailQuery buildQuery(FinanceProjectProfitDetailREQ req) {
        FinanceProjectProfitDetailQuery query = new FinanceProjectProfitDetailQuery();
        query.setStart((req.getPage() - 1) * req.getPageSize());
        query.setSize(req.getPageSize());
        query.setProjectProfitId(req.getProjectProfitId());
        query.setDeptId(req.getBizDeptId());
        query.setContractCode(req.getContractCode());
        query.setSponsorUserId(req.getSponsorUserId());
        return query;
    }

    //利润测算
    public void profitCalculation(FinanceProjectCalculationREQ req) {
        if (req.getYearAndMonth() == null){
            throw new MithrasException("请选择正确日期");
        }
        //获取当月月初/月末，月初：startMonth 月末：endMonth
        int targetYear = req.getYearAndMonth().getYear();
        int targetMonth = req.getYearAndMonth().getMonthValue();
        LocalDate startMonth = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate endMonth = LocalDate.of(targetYear, targetMonth, startMonth.lengthOfMonth());
        LocalDate now = LocalDate.now();
        if(now.isBefore(endMonth)){
            throw new MithrasException("，当前日期小于所选月份的最后一个自然日，请选择正确日期");
        }
        //检验是否生成上一个月的利润测算数据
        LocalDate lastMonthDate = req.getYearAndMonth().minusMonths(1);
        LambdaQueryWrapper<FinanceProjectProfit> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfit::getYear, lastMonthDate.getYear());
        query.eq(FinanceProjectProfit::getMonth, lastMonthDate.getMonth());
        List<FinanceProjectProfit> list = financeProjectProfitService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            log.error("{}年{}月的项目利润数据不存在，无法计算", targetYear, targetMonth);
            throw new MithrasException("请先生成上月利润测算数据");
        } else if (list.size() > 1) {
            log.error("{}年{}月的项目利润数据存在多份，无法计算", targetYear, targetMonth);
            throw new MithrasException("存在多份上个月的项目利润数据，请检查");
        }
        FinanceProjectProfit lastMonthProfit = list.get(0);
//        //获取利润测算数据，检查本月利润测算数据是否生成
//        List<FinanceProjectProfit> profitDetails = financeProjectProfitService.list(Wrappers.<FinanceProjectProfit>lambdaQuery()
//                .eq(FinanceProjectProfit::getYear, targetYear)
//                .eq(FinanceProjectProfit::getMonth, targetMonth));
//        if (CollectionUtil.isNotEmpty(profitDetails) && profitDetails.size() > 0) {
//            throw new MithrasException("该月份利润测算数据已生成,请选择正确日期");
//        }
        //当月-月结管理是否确认 可以优化为直接取值月结管理分页列表
        boolean settlementStatus = this.isMonthSettlement(req.getYearAndMonth());
        if (!settlementStatus){
            throw new MithrasException("请先确认当月月结管理");
        }
        //FTP任务是否跑批
        List<FtpInterestDetailRecord> ftpList = ftpInterestDetailRecordService.list(Wrappers.<FtpInterestDetailRecord>lambdaQuery()
                .ge(FtpInterestDetailRecord::getInterestDate, startMonth)
                .le(FtpInterestDetailRecord::getInterestDate, endMonth)
                .last(StringUtil.mysqlLimitOne()));
        Assert.notEmpty(ftpList, () -> MithrasException.newException("当月FTP计息任务跑批未完成"));
        // 获取拨备计提数据,检查本月拨备计提数据是否生效
        KpiProvisionBaseInfo kpiProvisionBaseInfo = kpiProvisionBaseInfoService.getEffectOneByYearMonth(targetYear, targetMonth);
        if (Objects.isNull(kpiProvisionBaseInfo)) {
            log.error("{}年{}月的拨备计提数据不存在，无法计算", targetYear, targetMonth);
            throw new MithrasException("拨备计提数据未生效");
        }
        List<KpiProvisionDetail> kpiProvisionDetailList = kpiProvisionDetailService.listByProvisionId(kpiProvisionBaseInfo.getId());
        Map<Long, List<KpiProvisionDetail>> kpiProvisionDetailMap = kpiProvisionDetailList.stream().collect(Collectors.groupingBy(KpiProvisionDetail::getContractId));
        // 获取外部数据
        Map<String, FinanceProfitHelperBO> profitHelperMap = this.getDataByThird(targetYear, targetMonth);
        // 获取上一个月的明细
        Map<Long, FinanceProjectProfitDetail> lastMonthDetailMap;
        if (targetMonth > 1) {
            List<FinanceProjectProfitDetail> lastMonthDetailList = financeProjectProfitDetailService.listByProjectProfitId(lastMonthProfit.getId());
            lastMonthDetailMap = lastMonthDetailList.stream().collect(Collectors.toMap(FinanceProjectProfitDetail::getContractId, e -> e));
        } else {
            // 如果是1月份，因为是按年计算的，1月份就从头开始计算，不需要上月数据
            lastMonthDetailMap = Collections.emptyMap();
        }
        //计算每一个目标合同的项目利润数据
        Set<Long> targetContractIds = new HashSet<>();
        if (CollectionUtil.isNotEmpty(lastMonthDetailMap)) {
            targetContractIds.addAll(lastMonthDetailMap.keySet());
        }
        List<ContractIncomeSharing>  contractIncomeSharingList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
                        .lt(ContractIncomeSharing::getIncomeDate, endMonth)
                        .ge(ContractIncomeSharing::getIncomeDate, startMonth)
                        .groupBy(ContractIncomeSharing::getContractId));
        List<FinanceProjectProfitDetail> detailList = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(contractIncomeSharingList)){
            targetContractIds.addAll(contractIncomeSharingList.stream().map(ContractIncomeSharing::getContractId).collect(Collectors.toSet()));
        }
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(targetContractIds);
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList){
            detailList.add(this.doCalculate(req.getYearAndMonth(), contractBaseInfo, kpiProvisionDetailMap.get(contractBaseInfo.getId()), profitHelperMap.get(contractBaseInfo.getContractCode()), lastMonthDetailMap.get(contractBaseInfo.getId())));
        }

        // 执行DB操作
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 查询是否已有数据
                FinanceProjectProfit exist = financeProjectProfitService.getOneByYearMonth(targetYear, targetMonth);
                if (Objects.nonNull(exist)) {
                    // 删除已有数据
                    financeProjectProfitService.removeById(exist.getId());
                    financeProjectProfitDetailService.removeByProjectProfitId(exist.getId());
                }
                // 保存数据
                this.save(targetYear, targetMonth, detailList);
            } catch (Exception e) {
                log.error("保存项目利润数据异常", e);
            }
        });
    }

    private Map<String, FinanceProfitHelperBO> getDataByThird(int targetYear, int targetMonth) {
        List<ReportBcmBalanceMfRes.Data> bcmBalanceDataList = jinKongMonthlyReportService.listBcmBalanceData(GlobalConstants.ZSZL_LOCAL_ORG_CODE, targetYear, targetMonth);
        if (CollectionUtil.isEmpty(bcmBalanceDataList)) {
            throw new MithrasException(String.format("没有获取到%s年%s月的科目余额表数据", targetYear, targetMonth));
        }
        // 过滤出科目余额表中指定科目编码的数据
        bcmBalanceDataList.removeIf(item -> (StrUtil.isBlank(item.getAcct_no())) || (!item.getAcct_no().startsWith(INCOME_ACCOUNT_NO)));
        if (CollectionUtil.isEmpty(bcmBalanceDataList)) {
            throw new MithrasException("科目余额表中没有指定科目编码的数据");
        }
        log.info("科目余额表过滤后的数据:{}", JSONUtil.toJsonStr(bcmBalanceDataList));
        // 调用金控接口获取辅助核算表数据
        Map<Long, String> helpMap = jinKongMonthlyReportService.listBcmFflexAssistData();
        if (CollectionUtil.isEmpty(helpMap)) {
            throw new MithrasException("科目余额辅助核算表中没有合同类型的数据");
        }
        log.info("辅助核算表过滤后的数据:{}", JSONUtil.toJsonStr(helpMap));
        // 计算每一个合同的相关数据
        Map<String, List<ReportBcmBalanceMfRes.Data>> bcmBalanceMap = bcmBalanceDataList.stream().collect(Collectors.groupingBy(ReportBcmBalanceMfRes.Data::getFassgrpid));
        Map<String, FinanceProfitHelperBO> result = new HashMap<>();
        for (Map.Entry<Long, String> entry : helpMap.entrySet()) {
            Long fid = entry.getKey();
            String contractCode = entry.getValue();
            if (Objects.isNull(fid) || StrUtil.isBlank(contractCode)) {
                log.error("辅助核算必要数据不存在[{}]", JSONUtil.toJsonStr(entry));
                continue;
            }
            List<ReportBcmBalanceMfRes.Data> balanceList = bcmBalanceMap.get(String.valueOf(fid));
            if (CollectionUtil.isEmpty(balanceList)) {
                log.warn("通过辅助核算id没有找到对应的科目余额表数据[fid:{}]", fid);
                continue;
            } else {
                log.info("通过辅助核算id找到的科目余额表数据[fid:{}, contractCode:{}, data:{}]", fid, contractCode, JSONUtil.toJsonStr(balanceList));
            }
            long interest;
            long other;
            // 本年累计利息收入取以下科目的贷方本年发生额
            // 600104010101 + 600104010201 + 6001040103 + 600104020101 + 600104020201 + 6001040203 + 60019901 + 60019902
            interest = balanceList.stream()
                    .filter(e -> CharSequenceUtil.equalsAny(e.getAcct_no(), "600104010101", "600104010201", "6001040103", "600104020101", "600104020201", "6001040203", "60019901", "60019902"))
                    .filter(e -> Objects.nonNull(e.getCredit_ytd()))
                    .mapToLong(e -> Util.mithrasLongDecimalTwo(e.getCredit_ytd().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()))
                    .sum();
            // 本年累计其他收入取以下科目的贷方本年发生额
            // 600104010102 + 600104010202 + 600104020102 + 600104020202
            other = balanceList.stream()
                    .filter(e -> CharSequenceUtil.equalsAny(e.getAcct_no(), "600104010102", "600104010202", "600104020102", "600104020202"))
                    .filter(e -> Objects.nonNull(e.getCredit_ytd()))
                    .mapToLong(e -> Util.mithrasLongDecimalTwo(e.getCredit_ytd().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()))
                    .sum();
            FinanceProfitHelperBO bo = result.get(contractCode);
            if (Objects.isNull(bo)) {
                result.put(contractCode, new FinanceProfitHelperBO(contractCode, interest, other));
            } else {
                bo.setInterestIncomeThisYear(bo.getInterestIncomeThisYear() + interest);
                bo.setOtherIncomeThisYear(bo.getOtherIncomeThisYear() + other);
            }
        }
        return result;
    }

    //利润测算
    private FinanceProjectProfitDetail doCalculate(LocalDate yearAndMonth, ContractBaseInfo contractBaseInfo, List<KpiProvisionDetail> kpiProvisionDetailList, FinanceProfitHelperBO financeProfitHelperBO, FinanceProjectProfitDetail lastMonthProfitDetail) {
        int year = yearAndMonth.getYear();
        int month = yearAndMonth.getMonthValue();
        // 本年累计利息收入
        long interestThisYear = Optional.ofNullable(financeProfitHelperBO).map(FinanceProfitHelperBO::getInterestIncomeThisYear).orElse(0L);
        // 本年累计其他收入
        long otherIncomeThisYear = Optional.ofNullable(financeProfitHelperBO).map(FinanceProfitHelperBO::getOtherIncomeThisYear).orElse(0L);
        long lastMonthTotalIncomeYear = 0L;
        if (Objects.nonNull(lastMonthProfitDetail)) {
            lastMonthTotalIncomeYear = Optional.ofNullable(lastMonthProfitDetail.getTotalIncomeThisYear()).orElse(0L);
        }
        // 本年累计收入 = 本年累计利息收入+本年累计其他收入
        long totalIncomeThisYear = interestThisYear + otherIncomeThisYear;
        // 本月收入 = 本年累计收入 - 上月本年累计收入 （1月份的不需要减）
        long revenueThisMonth = totalIncomeThisYear;
        if (month > 1) {
            revenueThisMonth = totalIncomeThisYear - lastMonthTotalIncomeYear;
        }
        // 本月风险金余额
        long totalRiskThisYear = 0L;
        long riskThisMonth = 0L;
        if (CollectionUtil.isNotEmpty(kpiProvisionDetailList)) {
            totalRiskThisYear = kpiProvisionDetailList.stream().filter(e -> Objects.nonNull(e.getProfitCurrent())).mapToLong(KpiProvisionDetail::getProfitCurrent).sum();
            //本月风险金计提/冲抵
            riskThisMonth = kpiProvisionDetailList.stream().filter(e ->Objects.nonNull(e.getBonusCurrent())).mapToLong(KpiProvisionDetail::getBonusCurrent).sum();
        }
        //查询合同id对应的业务部门，获取对应的费用计提比例
        BigDecimal expenseRadio = new BigDecimal("0.2");
        ContractAssessDeptDetail dept = contractAssessDeptDetailService.getOne(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                .eq(ContractAssessDeptDetail::getContractId, contractBaseInfo.getId()));
        if (ObjectUtil.isNotEmpty(dept)){
            if(Objects.equals(dept.getAssessDeptId(),contractBaseInfo.getBizDeptId())){
                expenseRadio = kpiParameterConfigService.ensureExpenseRadio(dept.getAssessDeptId());
            }
        }else{
            expenseRadio = kpiParameterConfigService.ensureExpenseRadio(contractBaseInfo.getBizDeptId());
        }
        // 本年累计资金成本
        long totalCostThisYear = this.calculateTotalCostThisYear(yearAndMonth, contractBaseInfo.getId());
        // 附加税
        long totalAdditionalTaxThisYear = this.calculateAdditionalTax(contractBaseInfo, totalIncomeThisYear, totalCostThisYear);
        // 印花税-新版计算
        long totalStampTaxThisYear = this.calculateStampTax(contractBaseInfo, yearAndMonth);
        //本年累计毛利
        long totalGrossProfitThisYear = (BigDecimal.valueOf(totalIncomeThisYear).subtract(BigDecimal.valueOf(totalCostThisYear))).longValue();
        //(新数据年初风险金余额==上年末的风险金余额)
        FinanceProjectProfitDetail riskBlance = this.getOne(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                .eq(FinanceProjectProfitDetail::getContractId, contractBaseInfo.getId())
                .eq(FinanceProjectProfitDetail::getYear,year )
                .eq(FinanceProjectProfitDetail::getMonth, month-1));
        //年初风险金余额
        Long riskBalanceBeginYear = Optional.ofNullable(riskBlance).map(FinanceProjectProfitDetail::getRiskBalanceBeginYear).orElse(0L);
        // 封装对象
        FinanceProjectProfitDetail detail = new FinanceProjectProfitDetail();
        detail.setYear(yearAndMonth.getYear());
        detail.setMonth(yearAndMonth.getMonthValue());
        detail.setExpenseRadio(expenseRadio.multiply(BigDecimal.valueOf(1000000)).intValue());
        detail.setContractId(contractBaseInfo.getId());
        detail.setContractStartDate(paymentBaseInfoService.getEarliestPayDate(contractBaseInfo.getId()));
        detail.setTotalIncomeThisYear(totalIncomeThisYear);//本年累计收入
        detail.setTotalCostThisYear(totalCostThisYear);//本年累计资金成本
        detail.setTotalRiskThisYear(totalRiskThisYear);//本月风险金余额
        detail.setTotalAdditionalTaxThisYear(totalAdditionalTaxThisYear);//附加税
        detail.setTotalStampTaxThisYear(totalStampTaxThisYear);//印花税
        detail.setRevenueThisMonth(revenueThisMonth);//本月收入
        detail.setTotalGrossProfitThisYear(totalGrossProfitThisYear);//本年累计毛利
        detail.setRiskThisMonth(riskThisMonth);//本月风险金计提/冲抵
        //判断当前月份是否为12月,年初风险金余额
        if (isDecember(yearAndMonth)) {
            detail.setRiskBalanceBeginYear(totalRiskThisYear);
        } else {
            detail.setRiskBalanceBeginYear(riskBalanceBeginYear);
        }

        if (Objects.nonNull(dept)) {
            detail.setAssessDeptId(dept.getAssessDeptId());//考核部门id
        }
        //累计风险金计提/冲抵 = (本月风险金余额 - 年初风险金余额)
        long totalRiskBalanceThisYear = 0L;
        if (Objects.nonNull(riskBalanceBeginYear)) {
            totalRiskBalanceThisYear = (BigDecimal.valueOf(Optional.ofNullable(detail.getTotalRiskThisYear()).orElse(0L)).subtract(BigDecimal.valueOf(riskBalanceBeginYear))).longValue();
        }
        detail.setTotalRiskBalanceThisYear(totalRiskBalanceThisYear);
        //本年累计利润总额(扣费前) = (本年累计毛利 - 累计风险计提/冲抵 - 本年累计附加税 - 本年累计印花税)
        long totalProfitThisYearBefore = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(totalGrossProfitThisYear - totalRiskBalanceThisYear - totalAdditionalTaxThisYear - totalStampTaxThisYear).longValue());
        detail.setTotalProfitThisYearBefore(totalProfitThisYearBefore);
        // 本年累计利润总额(扣费后) = (本年累计毛利 - 累计风险计提/冲抵 - 本年累计附加税 - 本年累计印花税) * (1 - 费用计提比例)
        // 本年累计利润总额 ≥0，则“本年累计利润总额（扣费后）”=本年累计利润总额(扣费前) * （1-费用比例）； else，“本年累计利润总额（扣费后）”=本年累计利润总额(扣费前) * （1+费用比例）
        long totalProfitThisYearAfter = 0L;
        if (totalProfitThisYearBefore >= 0){
            totalProfitThisYearAfter = Util.mithrasLongDecimalTwo((BigDecimal.valueOf(totalProfitThisYearBefore)).multiply(BigDecimal.ONE.subtract(expenseRadio)).longValue());
        }else if (totalProfitThisYearBefore < 0){
            totalProfitThisYearAfter = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(totalProfitThisYearBefore).multiply(BigDecimal.ONE.add(expenseRadio)).longValue());
        }
        detail.setTotalProfitThisYear(totalProfitThisYearAfter);
        this.fillDiffThisMonth(detail, lastMonthProfitDetail);
        //本月毛利 = 本月收入 - 本月资金成本
        long grossProfitThisMonth = (BigDecimal.valueOf(revenueThisMonth).subtract(BigDecimal.valueOf(Optional.ofNullable(detail.getCostThisMonth()).orElse(0L)))).longValue();
        detail.setGrossProfitThisMonth(grossProfitThisMonth);
        return detail;
    }

    private long calculateTotalCostThisYear(LocalDate targetDate, Long contractId) {
        // 资金成本(FTP计息是借据维度的，合同维度需加总)
        List<FtpInterestDetailRecord> ftpInterestDetailRecordList = ftpInterestDetailRecordService.listEndOfMonthByContractYearMonth(targetDate.getYear(), targetDate.getMonthValue(), contractId);
        return ftpInterestDetailRecordList.stream().mapToLong(FtpInterestDetailRecord::getTotalInterestThisYear).sum();
    }

    private long calculateAdditionalTax(ContractBaseInfo contractBaseInfo, long totalIncomeThisYear, long totalCostThisYear) {
        // 附加税 = max(0, (当年累计收入 - 当年累计资金成本) * 增值税税率 * 0.12)
        BigDecimal additionalTaxRate = kpiParameterConfigService.ensureZZSRate(contractBaseInfo.getBizType(), contractBaseInfo.getLeaseType());
        if (Objects.isNull(additionalTaxRate)) {
            throw new MithrasException(String.format("<%s>没有获取到对应的增值税税率，放弃计算项目利润", contractBaseInfo.getContractCode()));
        }
        BigDecimal result = BigDecimal.valueOf(totalIncomeThisYear).subtract(BigDecimal.valueOf(totalCostThisYear)).multiply(additionalTaxRate).multiply(BigDecimal.valueOf(0.12));
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return 0L;
        } else {
            return Util.mithrasLongDecimalTwo(result.longValue());
        }
    }

    // 印花税处理
    private long calculateStampTax(ContractBaseInfo contractBaseInfo, LocalDate targetMonth) {
        // 计算本月：月初/月末
        LocalDate startMonth = targetMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endMonth = YearMonth.from(targetMonth).atEndOfMonth();
        Long noWStampDutyCount = 0L;
        Long LastStampDutyCount = 0L;
        //当月印花税
        List<MonthlyStampDuty> list = monthlyStampDutyService.list(Wrappers.<MonthlyStampDuty>lambdaQuery()
                .eq(MonthlyStampDuty::getDeleted, false)
                .eq(MonthlyStampDuty::getIsConfirmed, true)
                .eq(MonthlyStampDuty::getContractId, contractBaseInfo.getId())
                .le(MonthlyStampDuty::getDate, endMonth)
                .ge(MonthlyStampDuty::getDate, startMonth));
        if(CollectionUtils.isNotEmpty(list)){
            noWStampDutyCount = list.stream().mapToLong(MonthlyStampDuty::getStampDuty).sum();
        }
        // 获取上个月的印花税数据
        LocalDate lastMonthDate = targetMonth.minusMonths(1);
        FinanceProjectProfitDetail lastMonthDetail = financeProjectProfitDetailService.getSpecificOne(contractBaseInfo.getId(), lastMonthDate.getYear(), lastMonthDate.getMonthValue());
        if(Objects.nonNull(lastMonthDetail)){
            LastStampDutyCount = lastMonthDetail.getTotalStampTaxThisYear();
        }
        long stampDutyCount = (BigDecimal.valueOf(noWStampDutyCount).add(BigDecimal.valueOf(LastStampDutyCount))).longValue();
        return Util.mithrasLongDecimalTwo(stampDutyCount);
    }

    private void fillDiffThisMonth(FinanceProjectProfitDetail thisMonthDetail, FinanceProjectProfitDetail lastMonthDetail) {
        if (Objects.isNull(lastMonthDetail)) {
            // 理论上说明该合同进入投放后第一个月，所以没有上个月的数据，当月相关金额等同于当年金额
            thisMonthDetail.setIncomeThisMonth(thisMonthDetail.getTotalIncomeThisYear());//本月利息收入
            thisMonthDetail.setCostThisMonth(thisMonthDetail.getTotalCostThisYear());//本月资金成本
            thisMonthDetail.setProfitThisMonth(thisMonthDetail.getTotalProfitThisYear());////当月利润
        } else {
            // 存在上月数据则当月减上月得出当月金额
            thisMonthDetail.setIncomeThisMonth(thisMonthDetail.getTotalIncomeThisYear() - Optional.ofNullable(lastMonthDetail.getTotalIncomeThisYear()).orElse(0L));
            thisMonthDetail.setCostThisMonth(thisMonthDetail.getTotalCostThisYear() - Optional.ofNullable(lastMonthDetail.getTotalCostThisYear()).orElse(0L));
            thisMonthDetail.setProfitThisMonth(thisMonthDetail.getTotalProfitThisYear() - Optional.ofNullable(lastMonthDetail.getTotalProfitThisYear()).orElse(0L));
        }
    }

    private Long calculateInterestIncomeThisMonth(ContractBaseInfo contractBaseInfo, LocalDate targetMonth) {
        // 计算月初
        LocalDate startMonth = targetMonth.with(TemporalAdjusters.firstDayOfMonth());
        // 计算月末
        LocalDate endMonth = YearMonth.from(targetMonth).atEndOfMonth();
        //实际利率法：按照天计算  剩余本金法：按照月计算
        List<ContractIncomeSharing> contractIncomeSharingList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
                .le(ContractIncomeSharing::getIncomeDate, endMonth)
                .ge(ContractIncomeSharing::getIncomeDate, startMonth)
                .eq(ContractIncomeSharing::getDeleted, false)
                .eq(ContractIncomeSharing::getIsConfirmed, true)
                .eq(ContractIncomeSharing::getContractId, contractBaseInfo.getId())
                .orderByDesc(ContractIncomeSharing::getUpdateTime));
        IncomeConfirmTypeEnum incomeConfirmTypeEnum = IncomeConfirmTypeEnum.find(contractBaseInfo.getIncomeConfirmType());
        Long interestCount = 0L;
        if (CollectionUtil.isNotEmpty(contractIncomeSharingList)){
            for (ContractIncomeSharing sharing : contractIncomeSharingList) {
                if (incomeConfirmTypeEnum != null) {
                    switch (incomeConfirmTypeEnum) {
                        case RP:
                            interestCount += sharing.getIncome();
                            break;
                        case AIR:
                            interestCount += sharing.getIncome();
                            break;
                    }
                }
            }
        }
        return Util.mithrasLongDecimalTwo(interestCount);
    }

    //计算本月其他收入
    private Long calculateOtherIncomeThisMonth(ContractBaseInfo contractBaseInfo, LocalDate targetMonth) {
        // 计算月初/月末
        LocalDate startMonth = targetMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endMonth = YearMonth.from(targetMonth).atEndOfMonth();
        Long otherIncomeThisMonth = 0L;
        //获取流水中心业务数据，计算本月其他收入
        if (StringUtils.isNotBlank(contractBaseInfo.getIncomeConfirmType())){
            LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
            query.ge(CollectionBaseInfo::getPlanCollectionDate,startMonth);
            query.le(CollectionBaseInfo::getPlanCollectionDate,endMonth);
            query.eq(CollectionBaseInfo::getContractId,contractBaseInfo.getId());
            //剩余本金法
            if (IncomeConfirmTypeEnum.RP.name().equals(contractBaseInfo.getIncomeConfirmType())){
                query.and(i -> i.or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.OTHERAMOUNT.name())
                        .or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.COMMISSION.name())
                        .or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.EARLY_STOP_COMPENSATION.name())
                        .or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.NOMINAL_PRICE.name()));
                List<CollectionBaseInfo> suammry = collectionBaseInfoMapper.selectList(query);
                if (CollectionUtil.isNotEmpty(suammry)){
                    otherIncomeThisMonth = suammry.stream().filter(e-> Objects.nonNull(e.getCollectionAmount()))
                            .mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                }
            } else if (IncomeConfirmTypeEnum.AIR.name().equals(contractBaseInfo.getIncomeConfirmType())) {
                query.and(i -> i.or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.OTHERAMOUNT.name())
                        .or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.EARLY_STOP_COMPENSATION.name())
                        .or().eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.NOMINAL_PRICE.name()));
                List<CollectionBaseInfo> suammry = collectionBaseInfoMapper.selectList(query);
                if (CollectionUtil.isNotEmpty(suammry)){
                    otherIncomeThisMonth = suammry.stream().filter(e-> Objects.nonNull(e.getCollectionAmount()))
                            .mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                }
            }
        }
        return otherIncomeThisMonth;
    }
    //当月-月结管理是否已确认
    private boolean isMonthSettlement(LocalDate yearAndMonth) {
        //获取当月，月初/月末 时间
        LocalDate startMonth = yearAndMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endMonth = YearMonth.from(yearAndMonth).atEndOfMonth();
        List<ContractIncomeSharing> contractIncomeSharingList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
                .lt(ContractIncomeSharing::getIncomeDate, endMonth)
                .ge(ContractIncomeSharing::getIncomeDate, startMonth)
                .eq(ContractIncomeSharing::getDeleted, false)
                .eq(ContractIncomeSharing::getIsConfirmed, true));
        // 成本计提处理
        List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.list(Wrappers.<FundsDailyCost>lambdaQuery()
                .lt(FundsDailyCost::getInterestDate, endMonth)
                .ge(FundsDailyCost::getInterestDate, startMonth)
                .eq(FundsDailyCost::getDeleted,false)
                .eq(FundsDailyCost::getIsConfirmed,true));
        //印花税验
        List<MonthlyStampDuty> stampDutyList = monthlyStampDutyService.list(Wrappers.<MonthlyStampDuty>lambdaQuery()
                .eq(MonthlyStampDuty::getDeleted, false)
                .eq(MonthlyStampDuty::getIsConfirmed, true)
                .lt(MonthlyStampDuty::getDate, endMonth)
                .ge(MonthlyStampDuty::getDate, startMonth));
        if (CollectionUtil.isEmpty(contractIncomeSharingList) && CollectionUtil.isEmpty(fundsDailyCostList) && CollectionUtil.isEmpty(stampDutyList) ) {
            return false;
        }
        return true;
    }

    //校验本月存续合同所属的考核部门，费用计提比例是否配置
    public boolean checkEnsureExpenseRadio(Map<Long, String> helpMap) {
        // 过滤出符合条件的合同，获取合同所属的考核部门
        List<String> uniqueValues = helpMap.values().stream().distinct().collect(Collectors.toList());
        List<ContractBaseInfo> infoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractCode, uniqueValues)
                .notIn(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()))
        );
        List<String> uniqueDepts = new ArrayList<>();
        if (CollectionUtil.isEmpty(infoList)) {
            uniqueDepts = infoList.stream().map(ContractBaseInfo::getBizDeptId).distinct()
                    .map(String::valueOf).collect(Collectors.toList());
        }
        //获取考核部门配置的对应费用计提比例
        KpiParameterConfig kpiParameterConfig = kpiParameterConfigService.getOneByConfigCode(KpiParameterConfigCodeEnum.EXPENSE_RADIO);
        List<ExpenseRadioConfig.Data> dataList = JSONUtil.toList(kpiParameterConfig.getConfigValue(), ExpenseRadioConfig.Data.class);
        List<String> uniqueDeptList = dataList.stream().filter(StreamUtil.distinctByKey(ExpenseRadioConfig.Data::getAssessDept))
                .map(ExpenseRadioConfig.Data::getAssessDept).collect(Collectors.toList());
        if (uniqueDeptList.containsAll(uniqueDepts)) {
            return true;
        }
        return false;
    }

    private void save(int year, int month, List<FinanceProjectProfitDetail> detailList) {
        // 保存主表
        FinanceProjectProfit financeProjectProfit = new FinanceProjectProfit();
        financeProjectProfit.setYear(year);
        financeProjectProfit.setMonth(month);
        // 计算合计值
        long revenueThisMonth = 0L;
        long costThisMonth = 0L;
        long riskThisMonth = 0L;
        long profitThisMonth = 0L;
        long totalIncomeThisYear = 0L;
        long totalCostThisYear = 0L;
        long totalRiskThisYear = 0L;
        long totalProfitThisYear = 0L;
        long totalAdditionalTaxThisYear = 0L;
        long totalStampTaxThisYear = 0L;
        if (CollectionUtil.isNotEmpty(detailList)) {
            for (FinanceProjectProfitDetail detail : detailList) {
                revenueThisMonth += Optional.ofNullable(detail.getRevenueThisMonth()).orElse(0L);
                costThisMonth += Optional.ofNullable(detail.getCostThisMonth()).orElse(0L);
                riskThisMonth += Optional.ofNullable(detail.getRiskThisMonth()).orElse(0L);
                profitThisMonth += Optional.ofNullable(detail.getProfitThisMonth()).orElse(0L);
                totalIncomeThisYear += Optional.ofNullable(detail.getTotalIncomeThisYear()).orElse(0L);
                totalCostThisYear += Optional.ofNullable(detail.getTotalCostThisYear()).orElse(0L);
                totalRiskThisYear += Optional.ofNullable(detail.getTotalRiskThisYear()).orElse(0L);
                totalProfitThisYear += Optional.ofNullable(detail.getTotalProfitThisYear()).orElse(0L);
                totalAdditionalTaxThisYear += Optional.ofNullable(detail.getTotalAdditionalTaxThisYear()).orElse(0L);
                totalStampTaxThisYear += Optional.ofNullable(detail.getTotalStampTaxThisYear()).orElse(0L);
            }
        }
        financeProjectProfit.setIncomeThisMonth(revenueThisMonth);
        financeProjectProfit.setCostThisMonth(costThisMonth);
        financeProjectProfit.setRiskThisMonth(riskThisMonth);
        financeProjectProfit.setProfitThisMonth(profitThisMonth);
        financeProjectProfit.setTotalIncomeThisYear(totalIncomeThisYear);
        financeProjectProfit.setTotalCostThisYear(totalCostThisYear);
        financeProjectProfit.setTotalRiskThisYear(totalRiskThisYear);
        financeProjectProfit.setTotalProfitThisYear(totalProfitThisYear);
        financeProjectProfit.setTotalAdditionalTaxThisYear(totalAdditionalTaxThisYear);
        financeProjectProfit.setTotalStampTaxThisYear(totalStampTaxThisYear);
        financeProjectProfitService.save(financeProjectProfit);
        // 保存子表
        if (CollectionUtil.isNotEmpty(detailList)) {
            for (FinanceProjectProfitDetail detail : detailList) {
                detail.setProjectProfitId(financeProjectProfit.getId());
            }
            financeProjectProfitDetailService.saveBatch(detailList);
        }
    }
    //判断是否是12月
    public static boolean isDecember(LocalDate date) {
        return date.getMonth().equals(Month.DECEMBER);
    }

}
