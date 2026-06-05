package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayWeeklyReportDetailApplicationService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.*;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetStatusEnum;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanPayWeeklyReportDetailMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanPayWeeklyReportMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-投放计划（月度）-项目周报-详情
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetPlanPayWeeklyReportDetailService extends ServiceImpl<BudgetPlanPayWeeklyReportDetailMapper, BudgetPlanPayWeeklyReportDetail> implements BudgetPlanPayWeeklyReportDetailApplicationService {

    @Resource
    private BudgetPlanPayWeeklyReportDetailMapper budgetPlanPayWeeklyReportDetailMapper;
    @Resource
    private BudgetPlanPayWeeklyReportMapper budgetPlanPayWeeklyReportMapper;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    public List<BudgetPlanPayWeeklyReportDetail> listByReportId(Long reportId) {
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, reportId);
        return this.list(query);
    }

    public List<BudgetPlanPayWeeklyReportDetail> findLatestByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayWeeklyReport> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayWeeklyReport::getBudgetPlanId, budgetPlanId);
        query.ne(BudgetPlanPayWeeklyReport::getPlanStatus, BudgetStatusEnum.COLLECTING.name());
        query.orderByDesc(BudgetPlanPayWeeklyReport::getDateFrom);
        query.last(StringUtil.mysqlLimitOne());
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = SpringUtil.getBean(BudgetPlanPayWeeklyReportService.class).getOne(query);
        if (Objects.isNull(budgetPlanPayWeeklyReport)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> queryDetail = Wrappers.lambdaQuery();
        queryDetail.eq(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, budgetPlanPayWeeklyReport.getId());
        return this.list(queryDetail);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(BudgetPlanPayWeeklyReportDetailModifyREQ req) {
        BudgetPlanPayWeeklyReportDetail originalInfo = budgetPlanPayWeeklyReportDetailMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BudgetPlanPayWeeklyReportDetail info = BeanUtil.copyProperties(req, BudgetPlanPayWeeklyReportDetail.class);
        budgetPlanPayWeeklyReportDetailMapper.updateById(info);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void removeBatch(MultiplePkREQ req) {
        this.removeByIds(req.getIds());
    }

   /* public BudgetPlanPayWeeklyReportDetailStatisticsRSP statisticsMonth(BudgetPlanPayWeeklyReportDetailStatisticsREQ req) {
        // 找到投放计划
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = budgetPlanPayWeeklyReportMapper.selectById(req.getBudgetPlanWeeklyReportId());
        if (Objects.isNull(budgetPlanPayWeeklyReport)) {
            throw new MithrasException("<周报计划>不存在");
        }
        //找到投放计划
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(budgetPlanPayWeeklyReport.getBudgetPlanPayId());
        LocalDate now = LocalDate.now();
        BudgetPlanPayWeeklyReportDetailStatisticsRSP rsp = new BudgetPlanPayWeeklyReportDetailStatisticsRSP();
        // 计算统计数据
        // 存量资产合同的截止到上月末的剩余本金
        rsp.setEndOfLastMonthBalanceTotal(this.calculateTotalRemainingPrincipal(req.getBizDeptId(), LocalDate.of(now.getYear(), 1, 1)));
        // 投放日在预算区间内投放金额之和
        rsp.setNewActualPayThisMonthTotal(this.calculateTotalPay(req.getBizDeptId(), budgetPlanPay));
        // 预算区间内irr按照投放金额的加权平均之和
        rsp.setPriorityAverageIrr(this.calculatePriorityAverageIrr(req.getBizDeptId(), budgetPlanPay));
        // 投放日在预算区间内营业收入合计（不含税）之和
        rsp.setIncomeWithoutTaxTotal(this.calculateIncomeWithoutTax(req.getBizDeptId(), budgetPlanPay));
        // 投放日在预算区间内考核利润之和
        rsp.setProfitTotal(this.calculateProfit(req.getBizDeptId(), budgetPlanPay));
        // 投放日在预算区间内扣费后考核利润之和
        rsp.setProfitWithoutExpenseTotal(this.calculateProfitWithoutExpense(req.getBizDeptId(), budgetPlanPay));
        // 存量项目+新增投放的到预算区间当月末的剩余本金之和
        rsp.setEndOfThisMonthBalanceTotal(null);
        return rsp;
    }*/

    public BudgetPlanPayWeeklyReportDetailStatisticsRSP statisticsMonth(BudgetPlanPayWeeklyReportDetailListREQ req) {
        // 找到投放计划
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = budgetPlanPayWeeklyReportMapper.selectById(req.getBudgetPlanWeeklyReportId());
        if (Objects.isNull(budgetPlanPayWeeklyReport)) {
            throw new MithrasException("<周报计划>不存在");
        }
        BudgetPlanPay budgetPlanPay = SpringContextHolder.getBean(BudgetPlanPayService.class).getById(budgetPlanPayWeeklyReport.getBudgetPlanPayId());
        // 需要和列表联动，此处使用列表接口相同参数
        req.setPage(1);
        req.setPageSize(5000);
        PageR<BudgetPlanPayWeeklyReportDetailListRSP> pageResult = this.pageListMonth(req);
        Set<Long> candidateDetailIds = pageResult.getList().stream().map(BudgetPlanPayWeeklyReportDetailListRSP::getId).collect(Collectors.toSet());
        // 查询利润预算数据
        Set<Long> queryProfitDetailIds = new HashSet<>(candidateDetailIds);
        // 存量项目在利润预算明细表中的detailId默认是0
        queryProfitDetailIds.add(0L);
        LambdaQueryWrapper<BudgetPlanProfitDetail> profitQuery = Wrappers.lambdaQuery();
        profitQuery.eq(BudgetPlanProfitDetail::getBudgetPlanId, budgetPlanPayWeeklyReport.getBudgetPlanId());
        profitQuery.in(BudgetPlanProfitDetail::getBudgetPlanPayDetailId, queryProfitDetailIds);
        List<BudgetPlanProfitDetail> budgetPlanProfitDetailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(profitQuery);
        // 处理返回参数
        BudgetPlanPayWeeklyReportDetailStatisticsRSP rsp = new BudgetPlanPayWeeklyReportDetailStatisticsRSP();
        if (CollectionUtil.isNotEmpty(budgetPlanProfitDetailList)) {
            long endOfLastMonthBalance = 0L;
            long incomeWithoutTax = 0L;
            long assessmentProfit = 0L;
            long assessmentProfitWithoutExpense = 0L;
            long endOfThisMonthBalance = 0L;
            for (BudgetPlanProfitDetail profitDetail : budgetPlanProfitDetailList) {
                endOfLastMonthBalance += Optional.ofNullable(profitDetail.getEndOfLastPeriodBalance()).orElse(0L);
                incomeWithoutTax += Optional.ofNullable(profitDetail.getIncomeWithoutTax()).orElse(0L);
                assessmentProfit += Optional.ofNullable(profitDetail.getAssessmentProfit()).orElse(0L);
                assessmentProfitWithoutExpense += Optional.ofNullable(profitDetail.getAssessmentProfitWithoutExpense()).orElse(0L);
                endOfThisMonthBalance += Optional.ofNullable(profitDetail.getEndOfThisPeriodBalance()).orElse(0L);
            }
            rsp.setEndOfLastMonthBalanceTotal(endOfLastMonthBalance);
            rsp.setIncomeWithoutTaxTotal(incomeWithoutTax);
            rsp.setProfitTotal(assessmentProfit);
            rsp.setProfitWithoutExpenseTotal(assessmentProfitWithoutExpense);
            rsp.setEndOfThisMonthBalanceTotal(endOfThisMonthBalance);
        }
        if (CollectionUtil.isEmpty(candidateDetailIds)) {
            return rsp;
        }
        // 寻找投放日在预算区间内的数据
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayWeeklyReportDetail::getId, candidateDetailIds);
        query.ge(BudgetPlanPayWeeklyReportDetail::getPlanPayDate, budgetPlanPay.getBudgetDateFrom());
        query.le(BudgetPlanPayWeeklyReportDetail::getPlanPayDate, budgetPlanPay.getBudgetDateTo());
        List<BudgetPlanPayWeeklyReportDetail> budgetPlanPayDetailList = this.list(query);
        // 月度新增投放额 = 投放日在预算区间内投放金额之和
        if (CollectionUtil.isNotEmpty(budgetPlanPayDetailList)) {
            rsp.setNewActualPayThisMonthTotal(budgetPlanPayDetailList.stream().filter(e -> Objects.nonNull(e.getPlanPayAmount())).mapToLong(BudgetPlanPayWeeklyReportDetail::getPlanPayAmount).sum());
        }
        // 加权平均IRR = 预算区间内按照投放金额的加权平均值和
        if (Objects.nonNull(rsp.getNewActualPayThisMonthTotal())) {
            BigDecimal irrTempBD = null;
            for (BudgetPlanPayWeeklyReportDetail detail : budgetPlanPayDetailList) {
                if (Objects.isNull(detail.getPlanPayAmount()) || Objects.isNull(detail.getIrr())) {
                    continue;
                }
                if (Objects.isNull(irrTempBD)) {
                    irrTempBD = BigDecimal.ZERO;
                }
                irrTempBD = irrTempBD.add(BigDecimal.valueOf(detail.getPlanPayAmount() * detail.getIrr()));
            }
            if (Objects.nonNull(irrTempBD)) {
                BigDecimal irrAverageBD = irrTempBD.divide(BigDecimal.valueOf(rsp.getNewActualPayThisMonthTotal()), 20, RoundingMode.HALF_UP);
                rsp.setPriorityAverageIrr(Util.mithrasIntegerDecimalTwo(irrAverageBD.intValue()));
            }
        }
        return rsp;
    }

    public PageR<BudgetPlanPayWeeklyReportDetailListRSP> pageListMonth(BudgetPlanPayWeeklyReportDetailListREQ req) {
        Page<BudgetPlanPayWeeklyReportDetail> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> conditionQuery = this.initDefaultQuery(req.getBudgetPlanWeeklyReportId());
        conditionQuery.orderByAsc(BudgetPlanPayWeeklyReportDetail::getBelongDeptId);
        conditionQuery.orderByAsc(BudgetPlanPayWeeklyReportDetail::getFtpIndustryCategory);
        //conditionQuery.orderByDesc(BudgetPlanPayWeeklyReportDetail::getIrrFtpDiff);
        if (Objects.nonNull(req.getDeptId())) {
            conditionQuery.eq(BudgetPlanPayWeeklyReportDetail::getBelongDeptId, req.getDeptId());
        }
        if (StrUtil.isNotBlank(req.getFtpIndustryCategory())) {
            conditionQuery.eq(BudgetPlanPayWeeklyReportDetail::getFtpIndustryCategory, req.getFtpIndustryCategory());
        }
        if (StrUtil.isNotBlank(req.getLeaseType())) {
            conditionQuery.like(BudgetPlanPayWeeklyReportDetail::getLeaseType, req.getLeaseType());
        }
        if (Objects.nonNull(req.getSponsorUserId())) {
            conditionQuery.eq(BudgetPlanPayWeeklyReportDetail::getSponsorUserId, req.getSponsorUserId());
        }
        if (Objects.nonNull(req.getPlanPayDateFrom())) {
            conditionQuery.ge(BudgetPlanPayWeeklyReportDetail::getPlanPayDate, req.getPlanPayDateFrom());
        }
        if (Objects.nonNull(req.getPlanPayDateTo())) {
            conditionQuery.le(BudgetPlanPayWeeklyReportDetail::getPlanPayDate, req.getPlanPayDateTo());
        }
        Page<BudgetPlanPayWeeklyReportDetail> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<BudgetPlanPayWeeklyReportDetailListRSP> rspList = BeanUtil.copyToList(dbResult.getRecords(), BudgetPlanPayWeeklyReportDetailListRSP.class);
        // 进一步加工一下
        this.fillExtraInfo(rspList);
        return PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    private LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> initDefaultQuery(Long budgetPlanWeeklyReportId) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, budgetPlanWeeklyReportId);
        boolean isProjManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name());
        if (isProjManager && !isBusinesshead) {
            query.eq(BudgetPlanPayWeeklyReportDetail::getSponsorUserId, currentUserId);
            return query;
        }
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        if (Objects.isNull(canViewDeptIds)) {
            // 不限制
            return query;
        }
        if (canViewDeptIds.isEmpty()) {
            // 没有可看的部门，填一个不可能的部门id即可
            query.eq(BudgetPlanPayWeeklyReportDetail::getBelongDeptId, -100);
            return query;
        }
        // 可以看指定部门
        query.in(BudgetPlanPayWeeklyReportDetail::getBelongDeptId, canViewDeptIds);
        return query;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addDetailMonth(BudgetPlanPayWeeklyReportDetailAddREQ req) {
        //查询周报信息
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = budgetPlanPayWeeklyReportMapper.selectById(req.getBudgetPlanWeeklyReportId());
        if (ObjectUtil.isEmpty(budgetPlanPayWeeklyReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 查询预算信息
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(budgetPlanPayWeeklyReport.getBudgetPlanPayId());
       // 确认是否已有该项目评审
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, req.getBudgetPlanWeeklyReportId());
        query.eq(BudgetPlanPayWeeklyReportDetail::getProjReviewId, req.getProjReviewId());
        int count = this.count(query);
        if (count > 0) {
//            throw new MithrasException("已存在该项目，不允许重复选择");
        }
        BudgetPlanPayWeeklyReportDetail budgetPlanPayDetail = this.copyFromProjReview(req.getProjReviewId());
        budgetPlanPayDetail.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
        budgetPlanPayDetail.setBudgetPlanPayId(budgetPlanPay.getId());
        budgetPlanPayDetail.setBudgetPlanWeeklyReportId(req.getBudgetPlanWeeklyReportId());
        // 保存
        this.save(budgetPlanPayDetail);
    }

    private BudgetPlanPayWeeklyReportDetail copyFromProjReview(Long projReviewId) {
        BudgetPlanPayDetail budgetPlanPayDetail = SpringUtil.getBean(BudgetPlanPayDetailService.class).copyFromProjReview(projReviewId);
        return BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayWeeklyReportDetail.class);
    }

    public void modifyDetailMonth(BudgetPlanPayWeekReportDetailModifyREQ req) {
        BudgetPlanPayWeeklyReportDetail exist = this.getById(req.getId());
        BudgetPlanPayWeeklyReportDetail update = BeanUtil.copyProperties(req, BudgetPlanPayWeeklyReportDetail.class);
        // 如果有FTP值需要进行计算
        if (Objects.nonNull(exist.getIrr()) && Objects.nonNull(update.getFtp())) {
            update.setIrrFtpDiff(exist.getIrr() - update.getFtp());
        }
        update.setLastOperateUserId(AccountUtil.getLoginInfo().getId());
        this.updateById(update);
    }

    public List<BudgetPlanPayWeeklyReportDetailListRSP> listMonthDetailContract(Long detailId) {
        BudgetPlanPayWeeklyReportDetail budgetPlanPayDetail = this.getById(detailId);
        if (Objects.isNull(detailId)) {
            throw new MithrasException("项目周报明细数据不存在");
        }
        // 查询合同信息
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).selectListByProjId(budgetPlanPayDetail.getProjReviewId());
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyList();
        }
        // 根据合同数量生成对应数量的返回数据
        List<BudgetPlanPayWeeklyReportDetailListRSP> rspList = new ArrayList<>(contractBaseInfoList.size());
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            BudgetPlanPayWeeklyReportDetailListRSP rsp = BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayWeeklyReportDetailListRSP.class);
            // 替换一些参数
            rsp.setId(contractBaseInfo.getId());
            rsp.setProjCode(contractBaseInfo.getContractCode());
            rsp.setCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setLeaseType(contractBaseInfo.getLeaseType());
            ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
            contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
            ContractPriceDetailRSP contractPriceDetailRSP = SpringUtil.getBean(ContractPriceService.class).detail(contractPriceDetailREQ);
            if (Objects.nonNull(contractPriceDetailRSP)) {
                rsp.setIrr(contractPriceDetailRSP.getIrr());
                int lpr = Optional.ofNullable(contractPriceDetailRSP.getLprPercent()).orElse(0);
                int lprAdd = Optional.ofNullable(contractPriceDetailRSP.getLprAddPercent()).orElse(0);
                rsp.setContractInterestRate(lpr + lprAdd);
            }
            rspList.add(rsp);
        }
        // 填充一些额外信息
        this.fillExtraInfo(rspList);
        return rspList;
    }

    public List<BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP> listBizDeptConfirmInfo(Long budgetPlanPayId) {
        LambdaQueryWrapper<BudgetPlanPayWeeklyReportDetail> query = this.initDefaultQuery(budgetPlanPayId);
        List<BudgetPlanPayWeeklyReportDetail> all = this.list(query);
        // 按部门分组
        Map<Long, List<BudgetPlanPayWeeklyReportDetail>> detailGroupByDeptMap = all.stream().collect(Collectors.groupingBy(BudgetPlanPayWeeklyReportDetail::getBelongDeptId));
        // 部门信息
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(detailGroupByDeptMap.keySet());
        // 处理返回
        List<BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP> result = new LinkedList<>();
        for (Map.Entry<Long, List<BudgetPlanPayWeeklyReportDetail>> entry : detailGroupByDeptMap.entrySet()) {
            List<BudgetPlanPayWeeklyReportDetail> list = entry.getValue();
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            long businessheadConfirmCount = list.stream().filter(e -> Objects.equals(e.getIsBusinessheadConfirm(), YesOrNoNumberEnum.YES.getCode())).count();
            BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP rsp = new BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP();
            rsp.setBizDeptId(entry.getKey());
            rsp.setBizDeptName(deptNameMap.get(entry.getKey()));
            rsp.setCount(list.size());
            rsp.setIsBusinessheadConfirm(businessheadConfirmCount == list.size() ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            result.add(rsp);
        }
        // 排序
        result.sort(Comparator.comparing(BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP::getBizDeptId));
        return result;
    }

    private void fillExtraInfo(List<BudgetPlanPayWeeklyReportDetailListRSP> rspList) {
        List<Long> projReviewIds = rspList.stream().map(BudgetPlanPayWeeklyReportDetailListRSP::getProjReviewId).collect(Collectors.toList());
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewIds);
        Map<Long, List<ContractBaseInfo>> contractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(rspList.stream().map(BudgetPlanPayWeeklyReportDetailListRSP::getSponsorUserId).collect(Collectors.toSet()));
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(rspList.stream().map(BudgetPlanPayWeeklyReportDetailListRSP::getBelongDeptId).collect(Collectors.toSet()));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(rspList.stream().map(BudgetPlanPayWeeklyReportDetailListRSP::getClientId).collect(Collectors.toSet()));
        rspList.forEach(e -> {
            e.setProvinceName(businessDataRepository.getAddressNameFromLocalCache(e.getProvince()));
            e.setCityName(businessDataRepository.getAddressNameFromLocalCache(e.getCity()));
            e.setDistrictName(businessDataRepository.getAddressNameFromLocalCache(e.getDistrict()));
            e.setBelongDeptName(deptNameMap.get(e.getBelongDeptId()));
            e.setSponsorUserName(userNameMap.get(e.getSponsorUserId()));
            e.setClientName(clientNameMap.get(e.getClientId()));
            e.setContractCount(Optional.ofNullable(contractMap.get(e.getProjReviewId())).map(List::size).orElse(0));
        });
    }

}
