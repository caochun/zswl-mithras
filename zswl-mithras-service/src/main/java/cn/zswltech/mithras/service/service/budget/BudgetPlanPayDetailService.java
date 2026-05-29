package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessTaskExtra;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.service.convert.flow.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.InterestWayEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanPayFundPlanEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanTypeEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetStatusEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.newftp.RelatedTermRange;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.dto.BudgetPlanPayDetailExpenseGroupDTO;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.mapper.model.budget.*;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.bo.*;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.impl.ContractRentActualServiceImpl;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.impl.GroupCreditReviewBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.SpringUtils;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.service.mapper.budget.BudgetPlanPayDetailMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划-明细
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayDetailService extends ServiceImpl<BudgetPlanPayDetailMapper, BudgetPlanPayDetail> {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    public List<BudgetPlanPayDetail> listByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long calculateNotMonth(BudgetPlanPayDetailNotMonthCalculateREQ req) {
        BudgetPlanPay budgetPlanPay = SpringUtil.getBean(BudgetPlanPayService.class).getById(req.getBudgetPlanPayId());
        if (Objects.isNull(budgetPlanPay)) {
            throw new MithrasException("投放计划数据不存在");
        }
        // 先处理主表
        BudgetPlanPayDetail budgetPlanPayDetail = this.buildDetail(budgetPlanPay, req);
        if (Objects.isNull(req.getBudgetPlanPayDetailId())) {
            // 说明是新增，需要填充一些特殊数据
            this.fillNewSingletonSpecialInfo(budgetPlanPay, budgetPlanPayDetail);
        }
        SpringUtil.getBean(BudgetPlanPayDetailService.class).saveOrUpdate(budgetPlanPayDetail);
        Long detailId = budgetPlanPayDetail.getId();
        // 处理子表
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = this.buildDetailPrice(detailId, budgetPlanPay, req);
        List<BudgetPlanPayDetailCashFlow> budgetPlanPayDetailCashFlowList = this.buildDetailCashFlow(budgetPlanPay, budgetPlanPayDetail, budgetPlanPayDetailPrice, req);
        List<BudgetPlanPayDetailIncomeSharing> budgetPlanPayDetailIncomeSharingList = this.buildDetailIncome(budgetPlanPay, budgetPlanPayDetail, budgetPlanPayDetailPrice, budgetPlanPayDetailCashFlowList);
        List<BudgetPlanPayDetailFtpInterest> budgetPlanPayDetailFtpInterestList = this.buildDetailCost(budgetPlanPay, budgetPlanPayDetail, budgetPlanPayDetailPrice, budgetPlanPayDetailCashFlowList);
        List<BudgetPlanPayDetailExpense> budgetPlanPayDetailExpenseList = this.buildDetailExpense(budgetPlanPay, budgetPlanPayDetail, budgetPlanPayDetailPrice, budgetPlanPayDetailCashFlowList, budgetPlanPayDetailIncomeSharingList, budgetPlanPayDetailFtpInterestList);
        // 子表采用删除重新保存的方式
        SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).deleteByBudgetPlanPayDetailIds(Collections.singletonList(req.getBudgetPlanPayDetailId()));
        SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).deleteByBudgetPlanPayDetailIds(Collections.singletonList(req.getBudgetPlanPayDetailId()));
        SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).deleteByBudgetPlanPayDetailIds(Collections.singletonList(req.getBudgetPlanPayDetailId()));
        SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).deleteByBudgetPlanPayDetailIds(Collections.singletonList(req.getBudgetPlanPayDetailId()));
        SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).deleteByBudgetPlanPayDetailIds(Collections.singletonList(req.getBudgetPlanPayDetailId()));
        // 保存（重要！！！最后再调用保存，中间过程的build方法中可能会有参数回填逻辑，如果先保存了可能会丢失字段值）
        SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).save(budgetPlanPayDetailPrice);
        SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).saveBatch(budgetPlanPayDetailCashFlowList);
        SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).saveBatch(budgetPlanPayDetailIncomeSharingList);
        SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).saveBatch(budgetPlanPayDetailFtpInterestList);
        SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).saveBatch(budgetPlanPayDetailExpenseList);
        // 同步到利润预算中进行利润测算
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
        // 同步到成本预算中进行成本测算
        SpringUtil.getBean(BudgetPlanCostService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
        return detailId;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long copyNotMonth(Long detailId) {
        BudgetPlanPayDetail budgetPlanPayDetail = this.getById(detailId);
        if (Objects.isNull(budgetPlanPayDetail)) {
            throw new MithrasException("原始数据不存在");
        }
        // 拷贝主表
        BudgetPlanPayDetail copy = BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayDetail.class);
        copy.reset();
        this.save(copy);
        // 拷贝报价方案
        SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).copyByDetailId(detailId, copy.getId());
        // 拷贝现金流计划
        SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).copyByDetailId(detailId, copy.getId());
        // 拷贝收入情况
        SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).copyByDetailId(detailId, copy.getId());
        // 拷贝成本情况
        SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).copyByDetailId(detailId, copy.getId());
        // 拷贝费用
        SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).copyByDetailId(detailId, copy.getId());
        // 同步到利润预算中进行利润测算
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(copy);
        // 同步到成本预算中进行成本测算
        SpringUtil.getBean(BudgetPlanCostService.class).recalculateByBudgetPlanPayDetail(copy);
        return copy.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addDetailMonth(BudgetPlanPayDetailMonthAddREQ req) {
        // 查询预算信息
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(req.getBudgetPlanPayId());
        // 确认是否已有该项目评审
        LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetail::getBudgetPlanPayId, req.getBudgetPlanPayId());
        query.eq(BudgetPlanPayDetail::getProjReviewId, req.getProjReviewId());
        int count = this.count(query);
        if (count > 0) {
//            throw new MithrasException("已存在该项目，不允许重复选择");
        }

        if(!checkPlanPayAmount(req.getProjReviewId(),null,null)){
            throw new MithrasException("该项目授信额度已用尽，请更换其他项目！");
        }

        BudgetPlanPayDetail budgetPlanPayDetail = this.copyFromProjReview(req.getProjReviewId());
        budgetPlanPayDetail.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
        budgetPlanPayDetail.setBudgetPlanPayId(budgetPlanPay.getId());
        budgetPlanPayDetail.setLastOperateUserId(AccountUtil.getLoginInfo().getId());
        this.fillNewSingletonSpecialInfo(budgetPlanPay, budgetPlanPayDetail);
        // 保存
        this.save(budgetPlanPayDetail);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyDetailMonth(BudgetPlanPayDetailMonthModifyREQ req) {
        BudgetPlanPayDetail update = BeanUtil.copyProperties(req, BudgetPlanPayDetail.class);
        // 如果有FTP值需要进行计算
        if (Objects.nonNull(update.getIrr()) && Objects.nonNull(update.getFtp())) {
            update.setIrrFtpDiff(update.getIrr() - update.getFtp());
        }
        update.setLastOperateUserId(AccountUtil.getLoginInfo().getId());

        // 拟投放金额不为空
        if(Objects.nonNull(req.getPlanPayAmount())){
            BudgetPlanPayDetail byId = budgetPlanPayDetailService.getById(req.getId());
            boolean checked = checkPlanPayAmount(byId.getProjReviewId(), update.getPlanPayAmount(),req.getId());
            if(!checked){
                throw new MithrasException("授信额度不足");
            }

            /*投放计划下 一个项目投放日对应月份数据只能有一份，如果已经存在并且不是本id  抛出异常*/
            BudgetPlanPayDetail detail = this.getById(req.getId());
            // 计算月份的起止日期
            LocalDate date = req.getPlanPayDate();
            LocalDate startOfMonth = date.withDayOfMonth(1);
            LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
            LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
            query.eq(BudgetPlanPayDetail::getBudgetPlanPayId, detail.getBudgetPlanId());
//        query.eq(BudgetPlanPayDetail::getBudgetPlanId, detail.getBudgetPlanId());
            query.eq(BudgetPlanPayDetail::getProjCode, detail.getProjCode());
            query.between(BudgetPlanPayDetail::getPlanPayDate, startOfMonth, endOfMonth);
            query.notIn(BudgetPlanPayDetail::getId, req.getId());
            List<BudgetPlanPayDetail> list = this.list(query);
            if(list.size() > 0){
                throw new MithrasException("本月已有投放计划，请选择其他月份");
            }
        }

        this.updateById(update);
        // 刷新一下动态数据
        this.refreshDynamicData(req.getId());
        // 同步到利润预算中进行利润测算
        BudgetPlanPayDetail budgetPlanPayDetail = this.getById(req.getId());
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
        // 同步到成本预算中进行成本测算
        SpringUtil.getBean(BudgetPlanCostService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
    }

    public boolean checkPlanPayAmount(Long projReviewId,Long planPayAmount,Long budgetPlanPayId){
        // BigDecimal creditAmount = queryCreditAmount(projReviewId,budgetPlanPayId);
        // BigDecimal checkAmount = Objects.nonNull(planPayAmount) ? BigDecimal.valueOf(planPayAmount) : BigDecimal.ZERO;
        // log.info("授信额度：{}，拟投放金额：{}",creditAmount,checkAmount);
        // return Objects.nonNull(creditAmount) && (Objects.nonNull(planPayAmount) ? (creditAmount.compareTo(checkAmount) >= 0) : (creditAmount.compareTo(checkAmount) > 0));
        return true;
    }

    // 查询当前审批项目剩余授信额度
    private BigDecimal queryCreditAmount(Long projReviewId,Long budgetPlanPayId){
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
//        // 1.集团授信
//        if(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projReviewBaseInfo.getRelationDataType())){
//            Long groupCreditReviewId = projReviewBaseInfo.getGroupCreditReviewId();
//            GroupCreditReviewBaseInfoLib reviewBaseInfoLib = SpringUtils.getBean(GroupCreditReviewBaseInfoLibHandler.class).queryLatestDataByOriginId(groupCreditReviewId);
//            if(Objects.isNull(reviewBaseInfoLib)){
//                return null;
//            }
//            // 集团授信额度
//            BigDecimal remainCreditAmountDecimal = new BigDecimal(reviewBaseInfoLib.getProjectApprovalAmount());
//
//            // 1.1 集团授信下探该授信下所有项目是否存在付款申请
//            List<ProjReviewBaseInfo> projReviewList = projReviewBaseInfoService.getBaseMapper()
//                    .selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
//                    .eq(ProjReviewBaseInfo::getRelationDataType, ReviewRelationDataType.GROUP_CREDIT_REVIEW.name())
//                    .eq(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReviewId)
//                    .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
//                    .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_REJECT.name())
//            );
//
//            // 补充需要查询的预算预览ID
//            List<Long> needQueryPlanPreviewIds = projReviewList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
//
//            // 集团授信下项目关联合同
//            List<Pair<Long,Long>> contractIdPairs = SpringUtil.getBean(ContractBaseInfoService.class)
//                    .listByProjReviewIds(projReviewList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()))
//                    .stream().map(contractBaseInfo -> Pair.of(contractBaseInfo.getId(),contractBaseInfo.getProjReviewId())).collect(Collectors.toList());
//
//            if(CollectionUtil.isNotEmpty(contractIdPairs)){
//                // 获取付款金额
//                List<Long> allContractIds = contractIdPairs.stream().map(Pair::getKey).collect(Collectors.toList());
//                BigDecimal paymentAmount = SpringUtil.getBean(PaymentBaseInfoService.class).calculateBatchForBudget(allContractIds);
//                if(Objects.nonNull(paymentAmount)){
//                    remainCreditAmountDecimal = remainCreditAmountDecimal.subtract(paymentAmount);
//                    Set<Long> needRemove = contractIdPairs.stream().filter(pair -> allContractIds.contains(pair.getKey())).map(Pair::getValue).collect(Collectors.toSet());
//                    needQueryPlanPreviewIds.removeAll(needRemove);
//                }
//            }
//
//            if(CollectionUtil.isEmpty(needQueryPlanPreviewIds)){
//                return remainCreditAmountDecimal;
//            }
//
//            // 1.2 所有计划中的拟投放金额
//            BigDecimal bigDecimal = queryPlanPayAmount(needQueryPlanPreviewIds,budgetPlanPayId,projReviewId);
//            return remainCreditAmountDecimal.subtract(bigDecimal);
//
//        }else {
            // 2.项目授信
            ProjReviewPriceDetailRSP detail = SpringUtil.getBean(ProjReviewPriceService.class).detail(projReviewBaseInfo.getId());
            BigDecimal creditAmount = Objects.nonNull(detail.getApprovedAmount()) ? new BigDecimal(detail.getApprovedAmount())
                    : Objects.nonNull(detail.getApplyCreditAmount()) ? new BigDecimal(detail.getApplyCreditAmount()) : BigDecimal.ZERO;

            // 项目关联合同
            List<Long> contractIds = SpringUtil.getBean(ContractBaseInfoService.class)
                    .listByProjReviewIds(Collections.singletonList(projReviewId))
                    .stream().map(ContractBaseInfo::getId).collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(contractIds)){
                BigDecimal paymentAmount = SpringUtil.getBean(PaymentBaseInfoService.class).calculateBatchForBudget(contractIds);
                if(Objects.nonNull(paymentAmount)){
                    return creditAmount.subtract(paymentAmount);
                }
            }
            // 找计划中的拟投放金额
            //BigDecimal bigDecimal = queryPlanPayAmount(Collections.singletonList(projReviewId),budgetPlanPayId,projReviewId);
            return creditAmount;//creditAmount.subtract(bigDecimal);
        //}
    }

    // 查询项目下所有月度计划拟投放金额
    private BigDecimal queryPlanPayAmount(List<Long> projReviewIds,Long budgetPlanPayId,Long projReviewId){
        BigDecimal planPay = new BigDecimal(0);
        // 投放计划-明细
        List<BudgetPlanPayDetail> budgetPlanPayDetails =
                this.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery().in(BudgetPlanPayDetail::getProjReviewId, projReviewIds))
                        .stream()
                        .filter(item -> Objects.isNull(budgetPlanPayId) || !(budgetPlanPayId.equals(item.getId()) && projReviewId.equals(item.getProjReviewId())))
                        .collect(Collectors.toList());

        if(CollectionUtil.isEmpty(budgetPlanPayDetails)){
            return planPay;
        }
        // 月度投放计划
        List<Long> planPayIds = budgetPlanPayService.list(Wrappers.<BudgetPlanPay>lambdaQuery()
                        .in(BudgetPlanPay::getId, budgetPlanPayDetails.stream().map(BudgetPlanPayDetail::getBudgetPlanPayId).distinct().collect(Collectors.toList()))
                        .in(BudgetPlanPay::getBudgetStatus, Arrays.asList(BudgetStatusEnum.COLLECT_FINISH.name(), BudgetStatusEnum.CONFIRM.name(), BudgetStatusEnum.COLLECTING.name()))
                )
                .stream().filter(budgetPlanPay -> BudgetPlanTypeEnum.MONTH.name().equals(budgetPlanPay.getBudgetType()))
                .map(BudgetPlanPay::getId)
                .collect(Collectors.toList());

        if(CollectionUtil.isEmpty(planPayIds)){
            return planPay;
        }
        // 月度计划拟投放金额累计
        for (BudgetPlanPayDetail budgetPlanPayDetail : budgetPlanPayDetails) {
            if(planPayIds.contains(budgetPlanPayDetail.getBudgetPlanPayId()) && Objects.nonNull(budgetPlanPayDetail.getPlanPayAmount())){
                planPay = planPay.add(new BigDecimal(budgetPlanPayDetail.getPlanPayAmount()));
            }
        }

        return planPay;
    }


    public void refreshDynamicData(Long planPayDetailId) {
        BudgetPlanPayDetail exist = this.getById(planPayDetailId);
        if (Objects.isNull(exist.getProjReviewId())) {
            return;
        }
        BudgetPlanPayDetail update = this.copyFromProjReview(exist.getProjReviewId());
        SpringUtil.getBean(BudgetPlanPayDetailService.class).updateById(update);
    }

    public PageR<BudgetPlanPayDetailNotMonthListRSP> pageListNotMonth(BudgetPlanPayDetailNotMonthListREQ req) {
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(req.getBudgetPlanPayId());
        if (Objects.isNull(budgetPlanPay)) {
            throw new MithrasException("投放计划主数据不存在");
        }
        Page<BudgetPlanPayDetail> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BudgetPlanPayDetail> conditionQuery = this.initDefaultQuery(req.getBudgetPlanPayId());
        conditionQuery.orderByDesc(BudgetPlanPayDetail::getId);
        if (Objects.nonNull(req.getBelongDeptId())) {
            conditionQuery.eq(BudgetPlanPayDetail::getBelongDeptId, req.getBelongDeptId());
        }
        if (StrUtil.isNotBlank(req.getFtpIndustryCategory())) {
            conditionQuery.eq(BudgetPlanPayDetail::getFtpIndustryCategory, req.getFtpIndustryCategory());
        }
        if (StrUtil.isNotBlank(req.getLeaseType())) {
            conditionQuery.eq(BudgetPlanPayDetail::getLeaseType, req.getLeaseType());
        }
        if (Objects.nonNull(req.getSponsorUserId())) {
            conditionQuery.eq(BudgetPlanPayDetail::getSponsorUserId, req.getSponsorUserId());
        }
        if (Objects.nonNull(req.getActualPayFrom())) {
            conditionQuery.ge(BudgetPlanPayDetail::getPlanPayDate, req.getActualPayFrom());
        }
        if (Objects.nonNull(req.getActualPayTo())) {
            conditionQuery.le(BudgetPlanPayDetail::getPlanPayDate, req.getActualPayTo());
        }
        Page<BudgetPlanPayDetail> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        // 组装数据
        List<BudgetPlanPayDetailNotMonthListRSP> rspList = this.buildNotMonthRspList(budgetPlanPay, dbResult.getRecords());
        return PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public BudgetPlanPayDetail copyFromProjReview(Long projReviewId) {
        // 查询项目评审信息
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        if (StrUtil.isBlank(projReviewBaseInfo.getFtpIndustryCategory())) {
            throw new MithrasException("FTP行业分类为空，请先补全数据");
        }
        // 查询项目评审报价方案
        ProjReviewLeasePrice projReviewLeasePrice = SpringUtil.getBean(ProjReviewLeasePriceService.class).getByProjectId(projReviewBaseInfo.getId());
        // 查询现金流量表
        List<ProjReviewCashFlowPlan> cashFlowPlanList = SpringUtil.getBean(ProjReviewCashFlowPlanService.class).listByProjReviewId(projReviewBaseInfo.getId(), null);
        // 组装数据
        BudgetPlanPayDetail budgetPlanPayDetail = new BudgetPlanPayDetail();
        budgetPlanPayDetail.setLastOperateUserId(AccountUtil.getLoginInfo().getId());
        budgetPlanPayDetail.setProjReviewId(projReviewBaseInfo.getId());
        budgetPlanPayDetail.setBelongDeptId(projReviewBaseInfo.getBizDeptId());
        budgetPlanPayDetail.setProjCode(projReviewBaseInfo.getProjCode());
        budgetPlanPayDetail.setClientId(projReviewBaseInfo.getClientId());
        if (StrUtil.isNotBlank(projReviewBaseInfo.getLeaseTypes())) {
            List<String> leaseTypes = JSONUtil.toList(projReviewBaseInfo.getLeaseTypes(), String.class);
            if (leaseTypes.size() == 1) {
                budgetPlanPayDetail.setLeaseType(leaseTypes.get(0));
            }
        }
        // XMX-58 行业分类 优先取当前项目生效状态对应的行业分类，若不存在，则取新建的
        String projectClassify = projReviewBaseInfo.getProjectClassify();
        if (!RecordStatus.NEW.name().equals(projReviewBaseInfo.getProjReviewStatus())) {
            ProjReviewBaseInfoLib effectLatestOne = SpringUtil.getBean(ProjReviewBaseInfoLibService.class)
                    .getEffectLatestOne(projReviewBaseInfo.getId());
            if (Objects.nonNull(effectLatestOne)) {
                projectClassify = effectLatestOne.getProjectClassify();
            }
        }
        budgetPlanPayDetail.setProjectClassify(projectClassify);

        budgetPlanPayDetail.setCountry(projReviewBaseInfo.getCountry());
        budgetPlanPayDetail.setProvince(projReviewBaseInfo.getProvince());
        budgetPlanPayDetail.setCity(projReviewBaseInfo.getCity());
        budgetPlanPayDetail.setDistrict(projReviewBaseInfo.getDistrict());
        budgetPlanPayDetail.setEvaluationSubjectId(projReviewBaseInfo.getEvaluationSubjectId());
        budgetPlanPayDetail.setFtpIndustryCategory(projReviewBaseInfo.getFtpIndustryCategory());
        budgetPlanPayDetail.setRiskControlIndustryClassify(projReviewBaseInfo.getRiskControlIndustryClassify());
        budgetPlanPayDetail.setProjSource(projReviewBaseInfo.getProjSource());
        budgetPlanPayDetail.setSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
        if (Objects.nonNull(projReviewLeasePrice)) {
            budgetPlanPayDetail.setCreditAmount(projReviewLeasePrice.getApplyCreditAmount());
            budgetPlanPayDetail.setIrr(projReviewLeasePrice.getIrrPercent());
            budgetPlanPayDetail.setContractInterestRate(projReviewLeasePrice.getLeaseRatePercent());
        }
        Map<Long, Long> paidAmountMap = SpringUtil.getBean(PaymentActualDetailService.class).calculatePayAmountByProjReviewId(Collections.singletonList(projReviewId));
        long paidAmount = Optional.ofNullable(paidAmountMap.get(projReviewId)).orElse(0L);
        budgetPlanPayDetail.setPaidAmount(paidAmount);
        if (CollectionUtil.isNotEmpty(cashFlowPlanList)) {
            // 测算XIRR
            List<CashFlowExcelModel> cashFlowExcelModelList = cashFlowPlanList.stream().map(e -> {
                CashFlowExcelModel cashFlowExcelModel = new CashFlowExcelModel();
                cashFlowExcelModel.setCashFlowPhase(e.getCashFlowPhase());
                cashFlowExcelModel.setCashFlowDate(e.getCashFlowDate());
                cashFlowExcelModel.setCashFlowAmount(BigDecimal.valueOf(Optional.ofNullable(e.getCashFlowAmount()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                cashFlowExcelModel.setRent(BigDecimal.valueOf(Optional.ofNullable(e.getRent()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                cashFlowExcelModel.setPrincipal(BigDecimal.valueOf(Optional.ofNullable(e.getPrincipal()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                cashFlowExcelModel.setInterest(BigDecimal.valueOf(Optional.ofNullable(e.getInterest()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                cashFlowExcelModel.setRemainingPrincipal(BigDecimal.valueOf(Optional.ofNullable(e.getRemainingPrincipal()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                return cashFlowExcelModel;
            }).collect(Collectors.toList());
            // 如果没有第0期需要补全
            if (cashFlowExcelModelList.stream().anyMatch(e -> Objects.equals(e.getCashFlowPhase(), 0))) {
                CashFlowExcelModel zeroPhase = new CashFlowExcelModel();
                zeroPhase.setCashFlowPhase(0);
                zeroPhase.setCashFlowDate(projReviewLeasePrice.getPlannedStartingDate());
                zeroPhase.setCashFlowAmount(BigDecimal.valueOf(-1 * projReviewLeasePrice.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
                zeroPhase.setRent(BigDecimal.ZERO);
                zeroPhase.setPrincipal(BigDecimal.ZERO);
                zeroPhase.setInterest(BigDecimal.ZERO);
                zeroPhase.setRemainingPrincipal(zeroPhase.getCashFlowAmount());
                cashFlowExcelModelList.add(zeroPhase);
            }
            cashFlowExcelModelList.sort(Comparator.comparing(CashFlowExcelModel::getCashFlowPhase));
            double xirr = SpringUtil.getBean(ContractRentActualServiceImpl.class).calculateXIRR(cashFlowExcelModelList);
            budgetPlanPayDetail.setXirr(xirr * 1000000);
        }
        // 查询流程信息
        ProcessListREQ processListREQ = new ProcessListREQ();
        processListREQ.setPage(1);
        processListREQ.setPageSize(Integer.MAX_VALUE);
        ProcessTaskExtra processTaskExtra = new ProcessTaskExtra();
        processTaskExtra.setProjName(projReviewBaseInfo.getProjName());
        processListREQ.setExtra(processTaskExtra);
        processListREQ.setModelKeyList(ListUtil.of(
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(),
                ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name(),
                ProcessModelTypeEnum.ContractCreateFlow.name(),
                ProcessModelTypeEnum.ContractStartRentFlow.name(),
                ProcessModelTypeEnum.PaymentCreateFlow.name(),
                ProcessModelTypeEnum.PaymentActualDetailFlow.name()
        ));
        ProcessPageReq flowReq = SpringUtil.getBean(FlowProcessConvert.class).req2FlowReq(processListREQ);
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(flowReq);
        if (CollectionUtil.isNotEmpty(flowRespPage.getContents())) {
            // 只取审批中和审批通过的
            flowRespPage.getContents().removeIf(e -> !Objects.equals(e.getProcessStatus(), ProcessBusinessStatusEnum.PASS.getType()) && !Objects.equals(e.getProcessStatus(), ProcessBusinessStatusEnum.PASS_ALL.getType()) && !Objects.equals(e.getProcessStatus(), ProcessBusinessStatusEnum.RUNNING.getType()));
            // 取最新的一个流程
            if (CollectionUtil.isNotEmpty(flowRespPage.getContents())) {
                // 排序
                flowRespPage.getContents().sort(Comparator.comparing(ProcessResp::getStartTime).reversed());
                ProcessResp recent = flowRespPage.getContents().get(0);
                budgetPlanPayDetail.setRzyProcessStage(recent.getModelKey());
            }
        }
        return budgetPlanPayDetail;
    }

    private List<BudgetPlanPayDetailNotMonthListRSP> buildNotMonthRspList(BudgetPlanPay budgetPlanPay, List<BudgetPlanPayDetail> detailList) {
        // 从子表获取一些数据
        Set<Long> detailIds = detailList.stream().map(BudgetPlanPayDetail::getId).collect(Collectors.toSet());
        Set<Long> deptIds = detailList.stream().map(BudgetPlanPayDetail::getBelongDeptId).collect(Collectors.toSet());
        Set<Long> userIds = detailList.stream().map(BudgetPlanPayDetail::getSponsorUserId).collect(Collectors.toSet());
        // 部门
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        // 用户
        Map<Long, String> userMap = id2NameService.sysUserId2Name(userIds);
        // 报价方案
        Map<Long, BudgetPlanPayDetailPrice> priceMap = SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).getPriceMapByDetailIds(detailIds);
        // 收入情况
        Map<Long, Long> incomeMap = SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).getIncomeMapByDetailIds(detailIds, budgetPlanPay.getBudgetDateFrom(), budgetPlanPay.getBudgetDateTo());
        // 成本情况
        Map<Long, Long> costMap = SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).getCostMapByDetailIds(detailIds, budgetPlanPay.getBudgetDateFrom(), budgetPlanPay.getBudgetDateTo());
        // 费用情况
        Map<Long, BudgetPlanPayDetailExpenseGroupDTO> expenseMap = SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).getExpenseMapByDetailIds(detailIds, budgetPlanPay.getBudgetDateFrom(), budgetPlanPay.getBudgetDateTo());
        List<BudgetPlanPayDetailNotMonthListRSP> result = new LinkedList<>();
        for (BudgetPlanPayDetail budgetPlanPayDetail : detailList) {
            BudgetPlanPayDetailNotMonthListRSP rsp = BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayDetailNotMonthListRSP.class);
            rsp.setBelongDeptName(deptMap.get(rsp.getBelongDeptId()));
            rsp.setSponsorUserName(userMap.get(rsp.getSponsorUserId()));
            // 报价方案
            BudgetPlanPayDetailPrice price = priceMap.get(budgetPlanPayDetail.getId());
            if (Objects.nonNull(price)) {
                rsp.setCommissionRate(price.getCommissionRate());
                rsp.setConsultingFeeRate(price.getConsultingFeeRate());
                rsp.setContractInterestRate(price.getContractInterestRate());
                rsp.setContractInterestRateType(price.getContractInterestRateType());
                rsp.setDepositRate(price.getDepositRate());
                rsp.setFirstRentRate(price.getFirstRentRate());
                rsp.setPayDate(price.getPayDate());
                rsp.setIrr(price.getIrr());
                rsp.setInterestCalculateWay(price.getInterestCalculateWay());
                rsp.setNominalPrice(price.getNominalPrice());
                rsp.setPayType(price.getPayType());
                rsp.setProjectAmount(price.getProjectAmount());
                rsp.setRepayFrequency(price.getRepayFrequency());
                rsp.setRepayTimesTotal(price.getRepayTimesTotal());
                rsp.setTermMonth(price.getTermMonth());
            }
            BigDecimal taxRate = FinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType());
            // 收入情况
            Long incomeSum = incomeMap.get(budgetPlanPayDetail.getId());
            if (Objects.nonNull(incomeSum) && Objects.nonNull(taxRate)) {
                // 计算不含税金额
                BigDecimal b = BigDecimal.valueOf(incomeSum).divide(BigDecimal.ONE.add(taxRate), 20, RoundingMode.HALF_UP);
                rsp.setIncomeWithoutTax(b.longValue());
            }
            // 成本情况
            Long costSum = costMap.get(budgetPlanPayDetail.getId());
            if (Objects.nonNull(costSum) && Objects.nonNull(taxRate)) {
//                // 计算不含税金额
//                BigDecimal b = BigDecimal.valueOf(costSum).divide(BigDecimal.ONE.add(taxRate), 20, RoundingMode.HALF_UP);
//                rsp.setCostWithoutTax(b.longValue());
                rsp.setCostWithoutTax(costSum);
            }
            // 费用情况
            BudgetPlanPayDetailExpenseGroupDTO expense = expenseMap.get(budgetPlanPayDetail.getId());
            if (Objects.nonNull(expense)) {
                rsp.setTaxAndOther(expense.getTaxSum());
                rsp.setRiskReserve(expense.getRiskFundDiffSum());
                rsp.setProfit(expense.getAssessmentProfitSum());
                rsp.setExpense(expense.getExpenseSum());
                if (Objects.nonNull(expense.getAssessmentProfitSum())) {
                    rsp.setProfitWithoutExpense(expense.getAssessmentProfitSum() - Optional.ofNullable(expense.getExpenseSum()).orElse(0L));
                }
            }

            // 添加区域
            if(Objects.nonNull(budgetPlanPayDetail.getProvince())){
                Map<String, String> nameMap = addressDictionaryMapper
                        .selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, ListUtil.toList(budgetPlanPayDetail.getProvince(), budgetPlanPayDetail.getCity(), budgetPlanPayDetail.getDistrict())))
                        .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
                StringBuilder st = new StringBuilder();
                if (ObjectUtil.isNotNull(nameMap.get(budgetPlanPayDetail.getProvince()))) {
                    st.append(nameMap.get(budgetPlanPayDetail.getProvince()));
                }
                if (ObjectUtil.isNotNull(nameMap.get(budgetPlanPayDetail.getCity()))) {
                    st.append(nameMap.get(budgetPlanPayDetail.getCity()));
                }
                if (ObjectUtil.isNotNull(nameMap.get(budgetPlanPayDetail.getDistrict()))) {
                    st.append(nameMap.get(budgetPlanPayDetail.getDistrict()));
                }
                rsp.setAreaName(st.toString());
            }

            result.add(rsp);
        }
        return result;
    }

    public BudgetPlanPayDetailNotMonthBaseRSP getByDetailId(Long budgetPlanPayDetailId) {
        BudgetPlanPayDetail budgetPlanPayDetail = this.getById(budgetPlanPayDetailId);
        BudgetPlanPayDetailNotMonthBaseRSP rsp = BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayDetailNotMonthBaseRSP.class);
        if (Objects.nonNull(rsp.getSponsorUserId())) {
            rsp.setSponsorUserName(id2NameService.sysUserId2NameSingle(rsp.getSponsorUserId()));
        }
        if (Objects.nonNull(rsp.getBizDeptLeaderId())) {
            rsp.setBizDeptLeaderName(id2NameService.sysUserId2NameSingle(rsp.getBizDeptLeaderId()));
        }
        if (Objects.nonNull(rsp.getBelongDeptId())) {
            rsp.setBelongDeptName(id2NameService.deptId2NameSingle(rsp.getBelongDeptId()));
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        this.removeByIds(budgetPlanPayDetailIds);
        // 删除其他关联子表
        SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        // 删除对应利润预算数据
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
        // 删除对应成本预算数据
        SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).deleteByBudgetPlanPayDetailIds(budgetPlanPayDetailIds);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanId);
        this.remove(query);
        // 删除其他关联子表
        SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).deleteByBudgetPlanId(budgetPlanId);
        SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).deleteByBudgetPlanId(budgetPlanId);
        SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingService.class).deleteByBudgetPlanId(budgetPlanId);
        SpringUtil.getBean(BudgetPlanPayDetailFtpInterestService.class).deleteByBudgetPlanId(budgetPlanId);
        SpringUtil.getBean(BudgetPlanPayDetailExpenseService.class).deleteByBudgetPlanId(budgetPlanId);
    }

    public List<BudgetPlanPayDetailDeptConfirmInfoRSP> listBizDeptConfirmInfo(BudgetPlanPayDetailDeptConfirmInfoREQ req) {
        LambdaQueryWrapper<BudgetPlanPayDetail> query = this.initDefaultQuery(req.getBudgetPlanPayId());
        if (Objects.nonNull(req.getBelongDeptId())) {
            query.eq(BudgetPlanPayDetail::getBelongDeptId, req.getBelongDeptId());
        }
        List<BudgetPlanPayDetail> all = this.list(query);
        // 按部门分组
        Map<Long, List<BudgetPlanPayDetail>> detailGroupByDeptMap = all.stream().collect(Collectors.groupingBy(BudgetPlanPayDetail::getBelongDeptId));
        // 部门信息
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(detailGroupByDeptMap.keySet());
        // 处理返回
        List<BudgetPlanPayDetailDeptConfirmInfoRSP> result = new LinkedList<>();
        for (Map.Entry<Long, List<BudgetPlanPayDetail>> entry : detailGroupByDeptMap.entrySet()) {
            List<BudgetPlanPayDetail> list = entry.getValue();
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            long businessheadConfirmCount = list.stream().filter(e -> Objects.equals(e.getIsBusinessheadConfirm(), YesOrNoNumberEnum.YES.getCode())).count();
            long leaderinchargeConfirmCount = list.stream().filter(e -> Objects.equals(e.getIsLeaderinchargeConfirm(), YesOrNoNumberEnum.YES.getCode())).count();
            BudgetPlanPayDetailDeptConfirmInfoRSP rsp = new BudgetPlanPayDetailDeptConfirmInfoRSP();
            rsp.setBizDeptId(entry.getKey());
            rsp.setBizDeptName(deptNameMap.get(entry.getKey()));
            rsp.setCount(list.size());
            rsp.setIsBusinessheadConfirm(businessheadConfirmCount == list.size() ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            rsp.setIsLeaderinchargeConfirm(leaderinchargeConfirmCount == list.size() ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            result.add(rsp);
        }
        // 排序
        result.sort(Comparator.comparing(BudgetPlanPayDetailDeptConfirmInfoRSP::getBizDeptId));
        return result;
    }

    public BudgetPlanPayDetailNotMonthStatisticsRSP statisticsNotMonth(BudgetPlanPayDetailNotMonthListREQ req) {
        BudgetPlanStatisticsBO statisticsBO = this.statistics(req.getBudgetPlanPayId(), req.getBelongDeptId());
        BudgetPlanPayDetailNotMonthStatisticsRSP rsp = new BudgetPlanPayDetailNotMonthStatisticsRSP();
        rsp.setEndOfLastYearBalanceTotal(statisticsBO.getEndOfLastPeriodBalanceTotal());
        rsp.setEndOfThisYearBalanceTotal(statisticsBO.getEndOfThisPeriodBalanceTotal());
        rsp.setNewActualPayThisYearTotal(statisticsBO.getNewActualPayThisPeriodTotal());
        rsp.setIncomeWithoutTaxTotal(statisticsBO.getIncomeWithoutTaxTotal());
        rsp.setProfitTotal(statisticsBO.getProfitTotal());
        rsp.setProfitWithoutExpenseTotal(statisticsBO.getProfitWithoutExpenseTotal());
        rsp.setPriorityAverageIrr(statisticsBO.getPriorityAverageIrr());
        return rsp;
//        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(req.getBudgetPlanPayId());
//        if (Objects.isNull(budgetPlanPay)) {
//            throw new MithrasException("投放计划数据不存在");
//        }
//        // 需要和列表联动，此处使用列表接口相同参数
//        req.setPage(1);
//        req.setPageSize(5000);
//        PageR<BudgetPlanPayDetailNotMonthListRSP> pageResult = this.pageListNotMonth(req);
//        Set<Long> candidateDetailIds = pageResult.getList().stream().map(BudgetPlanPayDetailNotMonthListRSP::getId).collect(Collectors.toSet());
//        // 查询利润预算数据
//        Set<Long> queryProfitDetailIds = new HashSet<>(candidateDetailIds);
//        // 存量项目在利润预算明细表中的detailId默认是0
//        queryProfitDetailIds.add(0L);
//        LambdaQueryWrapper<BudgetPlanProfitDetail> profitQuery = Wrappers.lambdaQuery();
//        profitQuery.eq(BudgetPlanProfitDetail::getBudgetPlanId, budgetPlanPay.getBudgetPlanId());
//        profitQuery.in(BudgetPlanProfitDetail::getBudgetPlanPayDetailId, queryProfitDetailIds);
//        if (Objects.nonNull(req.getBelongDeptId())) {
//            profitQuery.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
//        }
//        List<BudgetPlanProfitDetail> budgetPlanProfitDetailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(profitQuery);
//        // 处理返回参数
//        BudgetPlanPayDetailNotMonthStatisticsRSP rsp = new BudgetPlanPayDetailNotMonthStatisticsRSP();
//        if (CollectionUtil.isNotEmpty(budgetPlanProfitDetailList)) {
//            long endOfLastYearBalance = 0L;
//            long incomeWithoutTax = 0L;
//            long assessmentProfit = 0L;
//            long assessmentProfitWithoutExpense = 0L;
//            long endOfThisYearBalance = 0L;
//            for (BudgetPlanProfitDetail profitDetail : budgetPlanProfitDetailList) {
//                endOfLastYearBalance += Optional.ofNullable(profitDetail.getEndOfLastPeriodBalance()).orElse(0L);
//                incomeWithoutTax += Optional.ofNullable(profitDetail.getIncomeWithoutTax()).orElse(0L);
//                assessmentProfit += Optional.ofNullable(profitDetail.getAssessmentProfit()).orElse(0L);
//                assessmentProfitWithoutExpense += Optional.ofNullable(profitDetail.getAssessmentProfitWithoutExpense()).orElse(0L);
//                endOfThisYearBalance += Optional.ofNullable(profitDetail.getEndOfThisPeriodBalance()).orElse(0L);
//            }
//            rsp.setEndOfLastYearBalanceTotal(endOfLastYearBalance);
//            rsp.setIncomeWithoutTaxTotal(incomeWithoutTax);
//            rsp.setProfitTotal(assessmentProfit);
//            rsp.setProfitWithoutExpenseTotal(assessmentProfitWithoutExpense);
//            rsp.setEndOfThisYearBalanceTotal(endOfThisYearBalance);
//        }
//        if (CollectionUtil.isEmpty(candidateDetailIds)) {
//            return rsp;
//        }
//        // 寻找投放日在预算区间内的数据
//        LambdaQueryWrapper<BudgetPlanPayDetailPrice> priceQuery = Wrappers.lambdaQuery();
//        priceQuery.in(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, candidateDetailIds);
//        priceQuery.ge(BudgetPlanPayDetailPrice::getPayDate, budgetPlanPay.getBudgetDateFrom());
//        priceQuery.le(BudgetPlanPayDetailPrice::getPayDate, budgetPlanPay.getBudgetDateTo());
//        List<BudgetPlanPayDetailPrice> budgetPlanPayDetailPriceList = SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).list(priceQuery);
//        // 全年新增投放额 = 投放日在预算区间内投放金额之和
//        if (CollectionUtil.isNotEmpty(budgetPlanPayDetailPriceList)) {
//            rsp.setNewActualPayThisYearTotal(budgetPlanPayDetailPriceList.stream().filter(e -> Objects.nonNull(e.getProjectAmount())).mapToLong(BudgetPlanPayDetailPrice::getProjectAmount).sum());
//        }
//        // 加权平均IRR = 预算区间内按照投放金额的加权平均值和
//        if (Objects.nonNull(rsp.getNewActualPayThisYearTotal())) {
//            BigDecimal irrTempBD = null;
//            for (BudgetPlanPayDetailPrice price : budgetPlanPayDetailPriceList) {
//                if (Objects.isNull(price.getProjectAmount()) || Objects.isNull(price.getIrr())) {
//                    continue;
//                }
//                if (Objects.isNull(irrTempBD)) {
//                    irrTempBD = BigDecimal.ZERO;
//                }
//                irrTempBD = irrTempBD.add(BigDecimal.valueOf(price.getProjectAmount() * price.getIrr()));
//            }
//            if (Objects.nonNull(irrTempBD)) {
//                BigDecimal irrAverageBD = irrTempBD.divide(BigDecimal.valueOf(rsp.getNewActualPayThisYearTotal()), 20, RoundingMode.HALF_UP);
//                rsp.setPriorityAverageIrr(Util.mithrasIntegerDecimalTwo(irrAverageBD.intValue()));
//            }
//        }
//        return rsp;
    }

    public BudgetPlanPayDetailMonthStatisticsRSP statisticsMonth(BudgetPlanPayDetailMonthListREQ req) {
        BudgetPlanStatisticsBO statisticsBO = this.statistics(req.getBudgetPlanPayId(), req.getBelongDeptId());
        BudgetPlanPayDetailMonthStatisticsRSP rsp = new BudgetPlanPayDetailMonthStatisticsRSP();
        rsp.setEndOfLastMonthBalanceTotal(statisticsBO.getEndOfLastPeriodBalanceTotal());
        rsp.setEndOfThisMonthBalanceTotal(statisticsBO.getEndOfThisPeriodBalanceTotal());
        rsp.setNewActualPayThisMonthTotal(statisticsBO.getNewActualPayThisPeriodTotal());
        rsp.setIncomeWithoutTaxTotal(statisticsBO.getIncomeWithoutTaxTotal());
        rsp.setProfitTotal(statisticsBO.getProfitTotal());
        rsp.setProfitWithoutExpenseTotal(statisticsBO.getProfitWithoutExpenseTotal());
        rsp.setPriorityAverageIrr(statisticsBO.getPriorityAverageIrr());
        return rsp;
//        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(req.getBudgetPlanPayId());
//        if (Objects.isNull(budgetPlanPay)) {
//            throw new MithrasException("投放计划数据不存在");
//        }
//        // 需要和列表联动，此处使用列表接口相同参数
//        req.setPage(1);
//        req.setPageSize(5000);
//        PageR<BudgetPlanPayDetailMonthListRSP> pageResult = this.pageListMonth(req);
//        Set<Long> candidateDetailIds = pageResult.getList().stream().map(BudgetPlanPayDetailMonthListRSP::getId).collect(Collectors.toSet());
//        // 查询利润预算数据
//        Set<Long> queryProfitDetailIds = new HashSet<>(candidateDetailIds);
//        // 存量项目在利润预算明细表中的detailId默认是0
//        queryProfitDetailIds.add(0L);
//        LambdaQueryWrapper<BudgetPlanProfitDetail> profitQuery = Wrappers.lambdaQuery();
//        profitQuery.eq(BudgetPlanProfitDetail::getBudgetPlanId, budgetPlanPay.getBudgetPlanId());
//        profitQuery.in(BudgetPlanProfitDetail::getBudgetPlanPayDetailId, queryProfitDetailIds);
//        if (Objects.nonNull(req.getBelongDeptId())) {
//            profitQuery.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
//        }
//        List<BudgetPlanProfitDetail> budgetPlanProfitDetailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(profitQuery);
//        // 处理返回参数
//        BudgetPlanPayDetailMonthStatisticsRSP rsp = new BudgetPlanPayDetailMonthStatisticsRSP();
//        if (CollectionUtil.isNotEmpty(budgetPlanProfitDetailList)) {
//            long endOfLastMonthBalance = 0L;
//            long incomeWithoutTax = 0L;
//            long assessmentProfit = 0L;
//            long assessmentProfitWithoutExpense = 0L;
//            long endOfThisMonthBalance = 0L;
//            for (BudgetPlanProfitDetail profitDetail : budgetPlanProfitDetailList) {
//                endOfLastMonthBalance += Optional.ofNullable(profitDetail.getEndOfLastPeriodBalance()).orElse(0L);
//                incomeWithoutTax += Optional.ofNullable(profitDetail.getIncomeWithoutTax()).orElse(0L);
//                assessmentProfit += Optional.ofNullable(profitDetail.getAssessmentProfit()).orElse(0L);
//                assessmentProfitWithoutExpense += Optional.ofNullable(profitDetail.getAssessmentProfitWithoutExpense()).orElse(0L);
//                endOfThisMonthBalance += Optional.ofNullable(profitDetail.getEndOfThisPeriodBalance()).orElse(0L);
//            }
//            rsp.setEndOfLastMonthBalanceTotal(endOfLastMonthBalance);
//            rsp.setIncomeWithoutTaxTotal(incomeWithoutTax);
//            rsp.setProfitTotal(assessmentProfit);
//            rsp.setProfitWithoutExpenseTotal(assessmentProfitWithoutExpense);
//            rsp.setEndOfThisMonthBalanceTotal(endOfThisMonthBalance);
//        }
//        if (CollectionUtil.isEmpty(candidateDetailIds)) {
//            return rsp;
//        }
//        // 寻找投放日在预算区间内的数据
//        LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
//        query.in(BudgetPlanPayDetail::getId, candidateDetailIds);
//        query.ge(BudgetPlanPayDetail::getPlanPayDate, budgetPlanPay.getBudgetDateFrom());
//        query.le(BudgetPlanPayDetail::getPlanPayDate, budgetPlanPay.getBudgetDateTo());
//        List<BudgetPlanPayDetail> budgetPlanPayDetailList = this.list(query);
//        // 月度新增投放额 = 投放日在预算区间内投放金额之和
//        if (CollectionUtil.isNotEmpty(budgetPlanPayDetailList)) {
//            rsp.setNewActualPayThisMonthTotal(budgetPlanPayDetailList.stream().filter(e -> Objects.nonNull(e.getPlanPayAmount())).mapToLong(BudgetPlanPayDetail::getPlanPayAmount).sum());
//        }
//        // 加权平均IRR = 预算区间内按照投放金额的加权平均值和
//        if (Objects.nonNull(rsp.getNewActualPayThisMonthTotal())) {
//            BigDecimal irrTempBD = null;
//            for (BudgetPlanPayDetail detail : budgetPlanPayDetailList) {
//                if (Objects.isNull(detail.getPlanPayAmount()) || Objects.isNull(detail.getIrr())) {
//                    continue;
//                }
//                if (Objects.isNull(irrTempBD)) {
//                    irrTempBD = BigDecimal.ZERO;
//                }
//                irrTempBD = irrTempBD.add(BigDecimal.valueOf(detail.getPlanPayAmount() * detail.getIrr()));
//            }
//            if (Objects.nonNull(irrTempBD) && rsp.getNewActualPayThisMonthTotal() != 0) {
//                BigDecimal irrAverageBD = irrTempBD.divide(BigDecimal.valueOf(rsp.getNewActualPayThisMonthTotal()), 20, RoundingMode.HALF_UP);
//                rsp.setPriorityAverageIrr(Util.mithrasIntegerDecimalTwo(irrAverageBD.intValue()));
//            }
//        }
//        return rsp;
    }

    public PageR<BudgetPlanPayDetailMonthListRSP> pageListMonth(BudgetPlanPayDetailMonthListREQ req) {
        Page<BudgetPlanPayDetail> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BudgetPlanPayDetail> conditionQuery = this.initDefaultQuery(req.getBudgetPlanPayId());
        conditionQuery.orderByAsc(BudgetPlanPayDetail::getBelongDeptId);
        conditionQuery.orderByAsc(BudgetPlanPayDetail::getFtpIndustryCategory);
        conditionQuery.orderByDesc(BudgetPlanPayDetail::getIrrFtpDiff);
        if (Objects.nonNull(req.getBelongDeptId())) {
            conditionQuery.eq(BudgetPlanPayDetail::getBelongDeptId, req.getBelongDeptId());
        }
        if (StrUtil.isNotBlank(req.getFtpIndustryCategory())) {
            conditionQuery.eq(BudgetPlanPayDetail::getFtpIndustryCategory, req.getFtpIndustryCategory());
        }
        if (StrUtil.isNotBlank(req.getLeaseType())) {
            conditionQuery.eq(BudgetPlanPayDetail::getLeaseType, req.getLeaseType());
        }
        if (Objects.nonNull(req.getSponsorUserId())) {
            conditionQuery.eq(BudgetPlanPayDetail::getSponsorUserId, req.getSponsorUserId());
        }
        if (Objects.nonNull(req.getPlanPayDateFrom())) {
            conditionQuery.ge(BudgetPlanPayDetail::getPlanPayDate, req.getPlanPayDateFrom());
        }
        if (Objects.nonNull(req.getPlanPayDateTo())) {
            conditionQuery.le(BudgetPlanPayDetail::getPlanPayDate, req.getPlanPayDateTo());
        }
        Page<BudgetPlanPayDetail> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<BudgetPlanPayDetailMonthListRSP> rspList = BeanUtil.copyToList(dbResult.getRecords(), BudgetPlanPayDetailMonthListRSP.class);
        // 进一步加工一下
        this.fillExtraInfo(rspList);
        return PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public List<Pair<String, Long>> listMonthDetailProjReview(BudgetChooseProjectREQ req) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        boolean isProjManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name());
        boolean isLeaderincharge = sysUserService.userIsSpecificJob(currentUserId, JobEnum.leaderincharge.name());
        LambdaQueryWrapper<ProjReviewBaseInfo> query = Wrappers.lambdaQuery();
        query.in(ProjReviewBaseInfo::getProjReviewStatus, ListUtil.of(RecordStatus.TAKE_EFFECT.name(), RecordStatus.NEW.name()));
        if (Objects.nonNull(req.getBelongDeptId())) {
            query.eq(ProjReviewBaseInfo::getBizDeptId, req.getBelongDeptId());
        }
        if (isProjManager && !isBusinesshead && !isLeaderincharge) {
            query.eq(ProjReviewBaseInfo::getProjSponsorUserId, currentUserId);
        } else {
            List<Long> targetDeptIds = sysUserService.canViewDeptIds();
            if (Objects.nonNull(targetDeptIds)) {
                if (targetDeptIds.isEmpty()) {
                    // 填一个不可能的id
                    query.eq(ProjReviewBaseInfo::getBizDeptId, -100);
                } else {
                    query.in(ProjReviewBaseInfo::getBizDeptId, targetDeptIds);
                }
            }
        }
        List<ProjReviewBaseInfo> result = SpringUtil.getBean(ProjReviewBaseInfoService.class).list(query);
        return result.stream().map(e -> new Pair<>(e.getProjName(), e.getId())).collect(Collectors.toList());
    }

    public List<BudgetPlanPayDetailMonthListRSP> listMonthDetailContract(Long detailId) {
        BudgetPlanPayDetail budgetPlanPayDetail = this.getById(detailId);
        if (Objects.isNull(detailId)) {
            throw new MithrasException("投放计划明细数据不存在");
        }
        // 查询合同信息
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).selectListByProjId(budgetPlanPayDetail.getProjReviewId());
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyList();
        }
        // 根据合同数量生成对应数量的返回数据
        List<BudgetPlanPayDetailMonthListRSP> rspList = new ArrayList<>(contractBaseInfoList.size());
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            BudgetPlanPayDetailMonthListRSP rsp = BeanUtil.copyProperties(budgetPlanPayDetail, BudgetPlanPayDetailMonthListRSP.class);
            // 置空一些参数
            rsp.contractInfoResetNull();
            // 替换一些参数
            rsp.setId(contractBaseInfo.getId());
            rsp.setProjCode(contractBaseInfo.getContractCode());
            rsp.setLeaseType(contractBaseInfo.getLeaseType());
            ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
            contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
            ContractPriceDetailRSP contractPriceDetailRSP = SpringUtil.getBean(ContractPriceService.class).detail(contractPriceDetailREQ);
            if (Objects.nonNull(contractPriceDetailRSP)) {
                rsp.setCreditAmount(contractPriceDetailRSP.getApplyCreditAmount());
                rsp.setPaidAmount(SpringUtil.getBean(PaymentActualDetailMapper.class).totalPayByContractId(contractBaseInfo.getId()));
                rsp.setTermMonth(contractPriceDetailRSP.getMonthCount());
                rsp.setDeposit(contractPriceDetailRSP.getEarnestMoney());
                rsp.setConsultingFee(contractPriceDetailRSP.getConsultingFee());
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

    private void fillExtraInfo(List<BudgetPlanPayDetailMonthListRSP> rspList) {
        List<Long> projReviewIds = rspList.stream().map(BudgetPlanPayDetailMonthListRSP::getProjReviewId).collect(Collectors.toList());
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewIds);
        Map<Long, List<ContractBaseInfo>> contractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(rspList.stream().map(BudgetPlanPayDetailMonthListRSP::getSponsorUserId).collect(Collectors.toSet()));
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(rspList.stream().map(BudgetPlanPayDetailMonthListRSP::getBelongDeptId).collect(Collectors.toSet()));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(rspList.stream().map(BudgetPlanPayDetailMonthListRSP::getClientId).collect(Collectors.toSet()));
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

    public LambdaQueryWrapper<BudgetPlanPayDetail> initDefaultQuery(Long budgetPlanPayId) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<BudgetPlanPayDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetail::getBudgetPlanPayId, budgetPlanPayId);
        boolean isProjManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name());
        boolean isLeaderincharge = sysUserService.userIsSpecificJob(currentUserId, JobEnum.leaderincharge.name());
        if (isProjManager && !isBusinesshead && !isLeaderincharge) {
            query.eq(BudgetPlanPayDetail::getSponsorUserId, currentUserId);
            return query;
        }
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        if (Objects.isNull(canViewDeptIds)) {
            // 不限制
            return query;
        }
        if (canViewDeptIds.isEmpty()) {
            // 没有可看的部门，填一个不可能的部门id即可
            query.eq(BudgetPlanPayDetail::getBelongDeptId, -100);
            return query;
        }
        // 可以看指定部门
        query.in(BudgetPlanPayDetail::getBelongDeptId, canViewDeptIds);
        return query;
    }

    private BudgetPlanPayDetail buildDetail(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetailNotMonthCalculateREQ req) {
        BudgetPlanPayDetail budgetPlanPayDetail = new BudgetPlanPayDetail();
        budgetPlanPayDetail.setLastOperateUserId(AccountUtil.getLoginInfo().getId());
        budgetPlanPayDetail.setId(req.getBudgetPlanPayDetailId());
        budgetPlanPayDetail.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
        budgetPlanPayDetail.setBudgetPlanPayId(budgetPlanPay.getId());
        budgetPlanPayDetail.setClientName(req.getClientName());
        budgetPlanPayDetail.setFtpIndustryCategory(req.getFtpIndustryCategory());
        budgetPlanPayDetail.setRiskControlIndustryClassify(req.getRiskControlIndustryClassify());
        budgetPlanPayDetail.setLeaseType(req.getLeaseType());
        budgetPlanPayDetail.setSponsorUserId(req.getSponsorUserId());
        budgetPlanPayDetail.setBelongDeptId(req.getBelongDeptId());
        budgetPlanPayDetail.setBizDeptLeaderId(req.getBizDeptLeaderId());
        budgetPlanPayDetail.setProvince(req.getProvince());
        budgetPlanPayDetail.setCity(req.getCity());
        budgetPlanPayDetail.setDistrict(req.getDistrict());
        budgetPlanPayDetail.setProvince(req.getProvince());
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(req.getFtpIndustryCategory());
        Integer ftp = SpringUtil.getBean(BudgetParameterConfigService.class).getFtpByFtpIndustryCategory(ftpIndustryCategoryEnum, req.getTermMonth());
        if (Objects.isNull(ftp)) {
            throw new MithrasException("没有找到符合条件的<参数配置-FTP定价>");
        }
        budgetPlanPayDetail.setFtp(ftp);
        budgetPlanPayDetail.setPlanPayDate(req.getPayDate());
        return budgetPlanPayDetail;
    }

    private BudgetPlanPayDetailPrice buildDetailPrice(Long detailId, BudgetPlanPay budgetPlanPay, BudgetPlanPayDetailNotMonthCalculateREQ req) {
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = new BudgetPlanPayDetailPrice();
        budgetPlanPayDetailPrice.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
        budgetPlanPayDetailPrice.setBudgetPlanPayId(budgetPlanPay.getId());
        budgetPlanPayDetailPrice.setBudgetPlanPayDetailId(detailId);
        budgetPlanPayDetailPrice.setProjectAmount(req.getProjectAmount());
        budgetPlanPayDetailPrice.setFirstRentRate(req.getFirstRentRate());
        budgetPlanPayDetailPrice.setTermMonth(req.getTermMonth());
        budgetPlanPayDetailPrice.setDepositRate(req.getDepositRate());
        budgetPlanPayDetailPrice.setRepayFrequency(req.getRepayFrequency());
        budgetPlanPayDetailPrice.setConsultingFeeRate(req.getConsultingFeeRate());
        budgetPlanPayDetailPrice.setRepayTimesTotal(req.getRepayTimesTotal());
        budgetPlanPayDetailPrice.setCommissionRate(req.getCommissionRate());
        budgetPlanPayDetailPrice.setPayType(req.getPayType());
        budgetPlanPayDetailPrice.setNominalPrice(req.getNominalPrice());
        budgetPlanPayDetailPrice.setInterestCalculateWay(req.getInterestCalculateWay());
        budgetPlanPayDetailPrice.setContractInterestRateType(req.getContractInterestRateType());
        budgetPlanPayDetailPrice.setContractInterestRate(req.getContractInterestRate());
        budgetPlanPayDetailPrice.setPayDate(req.getPayDate());
        budgetPlanPayDetailPrice.setIrr(req.getIrr());
        return budgetPlanPayDetailPrice;
    }

    private List<BudgetPlanPayDetailCashFlow> buildDetailCashFlow(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetail budgetPlanPayDetail, BudgetPlanPayDetailPrice budgetPlanPayDetailPrice, BudgetPlanPayDetailNotMonthCalculateREQ req) {
        List<CashFlowBO> cashFlowBOList;
        if (Objects.equals(budgetPlanPayDetailPrice.getInterestCalculateWay(), RepayCalcType.BGZHK.name())) {
            // 如果是其他不规则分期还款，理应先通过导入生成现金流（前端暂存），不再自动生成，这里直接使用前端传过来的
            if (CollectionUtil.isEmpty(req.getCashFlowList())) {
                throw new MithrasException("<" + RepayCalcType.BGZHK.display() + ">请先导入现金流");
            }
            cashFlowBOList = req.getCashFlowList().stream().map(e -> BeanUtil.copyProperties(e, CashFlowBO.class)).collect(Collectors.toList());
        } else {
            CashFlowCalculateBO cashFlowCalculateBO = new CashFlowCalculateBO();
            cashFlowCalculateBO.setCreditAmount(budgetPlanPayDetailPrice.getProjectAmount());
            cashFlowCalculateBO.setPayType(budgetPlanPayDetailPrice.getPayType());
            cashFlowCalculateBO.setRepayRate(budgetPlanPayDetailPrice.getRepayFrequency());
            cashFlowCalculateBO.setRepayTimes(budgetPlanPayDetailPrice.getRepayTimesTotal());
            cashFlowCalculateBO.setInterestWay(InterestWayEnum.ACTUAL_RATE.name());
            cashFlowCalculateBO.setRentalCalcType(budgetPlanPayDetailPrice.getInterestCalculateWay());
            cashFlowCalculateBO.setInterestRate(budgetPlanPayDetailPrice.getContractInterestRate());
            cashFlowCalculateBO.setStartDate(budgetPlanPayDetailPrice.getPayDate());
            cashFlowCalculateBO.setTotalMonth(budgetPlanPayDetailPrice.getTermMonth());
            if (Objects.nonNull(budgetPlanPayDetailPrice.getCommissionRate())) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getCommissionRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                cashFlowCalculateBO.setCommission(Util.mithrasLongDecimalTwo(b.longValue()));
            }
            if (Objects.nonNull(budgetPlanPayDetailPrice.getFirstRentRate())) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getFirstRentRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                cashFlowCalculateBO.setDownPayment(Util.mithrasLongDecimalTwo(b.longValue()));
            }
            if (Objects.nonNull(budgetPlanPayDetailPrice.getDepositRate())) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                cashFlowCalculateBO.setEarnestMoney(Util.mithrasLongDecimalTwo(b.longValue()));
            }
            if (Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                cashFlowCalculateBO.setConsultingFee(Util.mithrasLongDecimalTwo(b.longValue()));
            }
            cashFlowBOList = FinancialUtil.calcCashFlow(cashFlowCalculateBO);
            if (CollectionUtil.isEmpty(cashFlowBOList)) {
                throw new MithrasException("现金流计划表生成失败");
            }
        }
        // 测算IRR并回填
        RepayRateEnum repayRateEnum = RepayRateEnum.of(budgetPlanPayDetailPrice.getRepayFrequency());
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(budgetPlanPayDetailPrice.getTermMonth(), repayRateEnum, cashFlowBOList);
        budgetPlanPayDetailPrice.setIrr(Util.mithrasIntegerDecimalTwo(cashFlowIRRBO.getIrr().multiply(BigDecimal.valueOf(1000000)).intValue()));
        // 测算XIRR并回填
        List<CashFlowExcelModel> list = cashFlowBOList.stream().map(e -> {
            CashFlowExcelModel cashFlowExcelModel = new CashFlowExcelModel();
            cashFlowExcelModel.setCashFlowPhase(e.getCashFlowPhase());
            cashFlowExcelModel.setCashFlowDate(e.getCashFlowDate());
            cashFlowExcelModel.setCashFlowAmount(BigDecimal.valueOf(Optional.ofNullable(e.getCashFlowAmount()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            cashFlowExcelModel.setRent(BigDecimal.valueOf(Optional.ofNullable(e.getRent()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            cashFlowExcelModel.setPrincipal(BigDecimal.valueOf(Optional.ofNullable(e.getPrincipal()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            cashFlowExcelModel.setInterest(BigDecimal.valueOf(Optional.ofNullable(e.getInterest()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            cashFlowExcelModel.setRemainingPrincipal(BigDecimal.valueOf(Optional.ofNullable(e.getRemainingPrincipal()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            return cashFlowExcelModel;
        }).collect(Collectors.toList());
        double xirr = SpringUtil.getBean(ContractRentActualServiceImpl.class).calculateXIRR(list);
        budgetPlanPayDetailPrice.setXirr(xirr * 1000000);
        // 模型转换
        return cashFlowBOList.stream().map(e -> {
            BudgetPlanPayDetailCashFlow cf = BeanUtil.copyProperties(e, BudgetPlanPayDetailCashFlow.class);
            cf.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            cf.setBudgetPlanPayId(budgetPlanPay.getId());
            cf.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
            return cf;
        }).collect(Collectors.toList());
    }

    private List<BudgetPlanPayDetailIncomeSharing> buildDetailIncome(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetail budgetPlanPayDetail, BudgetPlanPayDetailPrice budgetPlanPayDetailPrice, List<BudgetPlanPayDetailCashFlow> cashFlowList) {
        if (CollectionUtil.isEmpty(cashFlowList)) {
            throw new MithrasException("没有现金流计划，无法计算收入");
        }
        cashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowPhase));
        // 计算日折现率
        List<CashFlowBO> cashFlowBOList = cashFlowList.stream().map(e -> {
            CashFlowBO cashFlowBO = new CashFlowBO();
            cashFlowBO.setCashFlowDate(e.getCashFlowDate());
            cashFlowBO.setCashFlowAmount(e.getCashFlowAmount());
            cashFlowBO.setCashFlowPhase(e.getCashFlowPhase());
            cashFlowBO.setRent(e.getRent());
            cashFlowBO.setPrincipal(e.getPrincipal());
            cashFlowBO.setInterest(e.getInterest());
            cashFlowBO.setRemainingPrincipal(e.getRemainingPrincipal());
            // 因为现金流是上层传入的，传入的现金流第0期如果有咨询费的话需要减除（因为咨询费不进行收入分摊）
            if (cashFlowBO.getCashFlowPhase() == 0 && Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                long consultingFee = Util.mithrasLongDecimalTwo(b.longValue());
                cashFlowBO.setCashFlowAmount(cashFlowBO.getCashFlowAmount() - consultingFee);
            }
            return cashFlowBO;
        }).collect(Collectors.toList());
        DailyDiscountRateCalcResultBO dailyDiscountRateCalcResultBO = FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);
        log.info("预算管理-投放计划-{}-计算日折现率结果:{}", budgetPlanPayDetail.getClientName(), JSONUtil.toJsonStr(dailyDiscountRateCalcResultBO));
        // 实际利率法进行收入分摊
        CashFlowBO zeroCashFlowBO = cashFlowBOList.get(0);
        if (!Objects.equals(zeroCashFlowBO.getCashFlowPhase(), 0)) {
            throw new MithrasException("没有找到第0期现金流，无法测算收入");
        }
        cashFlowBOList.remove(0);
        long lastPhaseAdjustAmount = 0L;
        if (Objects.nonNull(budgetPlanPayDetailPrice.getCommissionRate())) {
            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getCommissionRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
            lastPhaseAdjustAmount = lastPhaseAdjustAmount + Util.mithrasLongDecimalTwo(b.longValue());
        }
        log.info("{}-投放计划-测算收入[zeroCashFlowBO:{}, cashFlowBOList:{}, lastPhaseAdjustAmount:{}]", budgetPlanPay.getBudgetPlanName(), JSONUtil.toJsonStr(zeroCashFlowBO), JSONUtil.toJsonStr(cashFlowBOList), lastPhaseAdjustAmount);
        List<IncomeSharingCashFlowBO> incomeSharingCashFlowBOList = FinancialUtil.calculateIncomeSharingByAIR(Collections.singletonList(zeroCashFlowBO), cashFlowBOList, lastPhaseAdjustAmount, dailyDiscountRateCalcResultBO.getDailyDiscountRate(), budgetPlanPayDetailPrice.getPayDate());
        if (CollectionUtil.isEmpty(incomeSharingCashFlowBOList)) {
            throw new MithrasException("收入情况测算失败");
        }
        // 数据模型转换
        return incomeSharingCashFlowBOList.stream().map(e -> {
            BudgetPlanPayDetailIncomeSharing budgetPlanPayDetailIncomeSharing = new BudgetPlanPayDetailIncomeSharing();
            budgetPlanPayDetailIncomeSharing.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            budgetPlanPayDetailIncomeSharing.setBudgetPlanPayId(budgetPlanPay.getId());
            budgetPlanPayDetailIncomeSharing.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
            budgetPlanPayDetailIncomeSharing.setIncomeDate(e.getCashFlowDate());
            budgetPlanPayDetailIncomeSharing.setIncomeYear(e.getCashFlowDate().getYear());
            budgetPlanPayDetailIncomeSharing.setIncomeMonth(e.getCashFlowDate().getMonthValue());
            budgetPlanPayDetailIncomeSharing.setIncomePhase(e.getCashFlowPhase());
            budgetPlanPayDetailIncomeSharing.setBeginOfTermBalance(e.getBeginOfTermBalance());
            budgetPlanPayDetailIncomeSharing.setRent(e.getRent());
            budgetPlanPayDetailIncomeSharing.setIncome(e.getIncome());
            budgetPlanPayDetailIncomeSharing.setEndOfTermBalance(e.getEndOfTermBalance());
            budgetPlanPayDetailIncomeSharing.setDailyDiscountRate(dailyDiscountRateCalcResultBO.getDailyDiscountRate().toPlainString());
            return budgetPlanPayDetailIncomeSharing;
        }).collect(Collectors.toList());
    }

    private List<BudgetPlanPayDetailFtpInterest> buildDetailCost(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetail budgetPlanPayDetail, BudgetPlanPayDetailPrice budgetPlanPayDetailPrice, List<BudgetPlanPayDetailCashFlow> cashFlowList) {
        Integer ftp = budgetPlanPayDetail.getFtp();
        if (Objects.isNull(ftp)) {
            throw new MithrasException("FTP价格不存在，无法测算成本");
        }
        // 确定起止日期
        cashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowDate));
        LocalDate startDate = budgetPlanPayDetailPrice.getPayDate();
        LocalDate endDate = cashFlowList.get(cashFlowList.size() - 1).getCashFlowDate();
        // 确定开始日期的资金占用
        long totalPay = budgetPlanPayDetailPrice.getProjectAmount();
        long firstRent = 0L;
//        long consultingFee = 0L;
        long deposit = 0L;
        long commission = 0L;
        if (Objects.nonNull(budgetPlanPayDetailPrice.getFirstRentRate())) {
            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getFirstRentRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
            firstRent = Util.mithrasLongDecimalTwo(b.longValue());
        }
//        if (Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
//            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
//            consultingFee = Util.mithrasLongDecimalTwo(b.longValue());
//        }
        if (Objects.nonNull(budgetPlanPayDetailPrice.getDepositRate())) {
            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
            deposit = Util.mithrasLongDecimalTwo(b.longValue());
        }
        if (Objects.nonNull(budgetPlanPayDetailPrice.getCommissionRate())) {
            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getCommissionRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
            commission = Util.mithrasLongDecimalTwo(b.longValue());
        }
        long cashInBegin = firstRent + deposit + commission;
        long cashOccupy = totalPay - cashInBegin;
        if (cashOccupy <= 0) {
            throw new MithrasException("初始资金占用为负数，无法计算成本");
        }
        // 按天进行FTP计息
        Map<LocalDate, BudgetPlanPayDetailCashFlow> cashFlowMap = cashFlowList.stream().collect(Collectors.toMap(BudgetPlanPayDetailCashFlow::getCashFlowDate, e -> e));
        List<BudgetPlanPayDetailFtpInterest> result = new LinkedList<>();
        LocalDate interestDate = startDate;
        BigDecimal dailyFtp = BigDecimal.valueOf(ftp).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
        while (!interestDate.isAfter(endDate)) {
            long currentDateRent = 0L;
            BudgetPlanPayDetailCashFlow cashFlow = cashFlowMap.get(interestDate);
            if (Objects.nonNull(cashFlow) && cashFlow.getCashFlowPhase() > 0) {
                currentDateRent = cashFlow.getRent();
                cashOccupy = cashOccupy - currentDateRent;
            }
            // 计算资金计息
            BigDecimal ftpInterest = BigDecimal.valueOf(cashOccupy).multiply(dailyFtp);
            // 保存数据
            BudgetPlanPayDetailFtpInterest budgetPlanPayDetailFtpInterest = new BudgetPlanPayDetailFtpInterest();
            budgetPlanPayDetailFtpInterest.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            budgetPlanPayDetailFtpInterest.setBudgetPlanPayId(budgetPlanPay.getId());
            budgetPlanPayDetailFtpInterest.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
            budgetPlanPayDetailFtpInterest.setInterestDate(interestDate);
            budgetPlanPayDetailFtpInterest.setInterestYear(interestDate.getYear());
            budgetPlanPayDetailFtpInterest.setInterestMonth(interestDate.getMonthValue());
            if (interestDate.isEqual(startDate)) {
                budgetPlanPayDetailFtpInterest.setCashOut(totalPay);
                budgetPlanPayDetailFtpInterest.setCashIn(cashInBegin);
            } else {
                budgetPlanPayDetailFtpInterest.setCashOut(0L);
                budgetPlanPayDetailFtpInterest.setCashIn(currentDateRent);
            }
            budgetPlanPayDetailFtpInterest.setCashOccupy(cashOccupy);
            budgetPlanPayDetailFtpInterest.setCashFtp(ftp);
            budgetPlanPayDetailFtpInterest.setCashInterest(Util.mithrasLongDecimalTwo(ftpInterest.longValue()));
            result.add(budgetPlanPayDetailFtpInterest);
            interestDate = interestDate.plusDays(1);
        }
        return result;
    }

    private List<BudgetPlanPayDetailExpense> buildDetailExpense(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetail budgetPlanPayDetail, BudgetPlanPayDetailPrice budgetPlanPayDetailPrice, List<BudgetPlanPayDetailCashFlow> cashFlowList, List<BudgetPlanPayDetailIncomeSharing> incomeList, List<BudgetPlanPayDetailFtpInterest> costList) {
        BigDecimal valueAddedTaxRateBD = FinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType());
        BigDecimal consultingFeeTaxRateBD = FinancialUtil.ensureConsultingTaxRate();
        // 从参数配置中获取参数
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(budgetPlanPayDetail.getFtpIndustryCategory());
        RelatedTermRange relatedTermRange = RelatedTermRange.convertFromMonthCount(budgetPlanPayDetailPrice.getTermMonth());
        Integer riskReserveConfig = SpringUtil.getBean(BudgetParameterConfigService.class).getRiskReserve(ftpIndustryCategoryEnum, relatedTermRange);
        if (Objects.isNull(riskReserveConfig)) {
            throw new MithrasException("没有找到符合条件的<参数设置-风险准备金计提比例>");
        }
        Integer expenseRateConfig = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(budgetPlanPayDetail.getBelongDeptId());
        if (Objects.isNull(expenseRateConfig)) {
            throw new MithrasException("没有找到符合条件的<参数设置-费用计提比例>");
        }
        BigDecimal riskFundRateBD = BigDecimal.valueOf(riskReserveConfig).divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP);
        BigDecimal expenseRateBD = BigDecimal.valueOf(expenseRateConfig).divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP);
        // 按月分组
        Map<String, List<BudgetPlanPayDetailIncomeSharing>> incomeMap = incomeList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getIncomeDate(), DatePattern.SIMPLE_MONTH_PATTERN)));
        Map<String, List<BudgetPlanPayDetailFtpInterest>> costMap = costList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getInterestDate(), DatePattern.SIMPLE_MONTH_PATTERN)));
        // 确定起止日期
        LocalDate startDate = budgetPlanPayDetailPrice.getPayDate();
        cashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowDate));
        LocalDate endDate = cashFlowList.get(cashFlowList.size() - 1).getCashFlowDate();
        // 按月处理数据
        List<BudgetPlanPayDetailExpense> result = new LinkedList<>();
        LocalDate expenseDate = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 1);
        long totalPrincipal = cashFlowList.stream().filter(e -> e.getCashFlowPhase() > 0).filter(e -> Objects.nonNull(e.getPrincipal())).mapToLong(BudgetPlanPayDetailCashFlow::getPrincipal).sum();
        BigDecimal lastRiskFundBalanceBD = BigDecimal.ZERO;
        boolean firstMonth = true;
        while (!expenseDate.isAfter(endDate)) {
            String dateKey = LocalDateTimeUtil.format(expenseDate, DatePattern.SIMPLE_MONTH_PATTERN);
            long income = Optional.ofNullable(incomeMap.get(dateKey)).map(e -> e.stream().mapToLong(BudgetPlanPayDetailIncomeSharing::getIncome).sum()).orElse(0L);
            long consultingFee = 0L;
            if (firstMonth && Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
                // 投放日当天需要计算咨询费
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                consultingFee = Util.mithrasLongDecimalTwo(b.longValue());
                firstMonth = false;
            }
            long cost = Optional.ofNullable(costMap.get(dateKey)).map(e -> e.stream().mapToLong(BudgetPlanPayDetailFtpInterest::getCashInterest).sum()).orElse(0L);
            // 不含税收入
            BigDecimal incomeWithoutTax = FinancialUtil.calculateAmountWithoutTax(income + consultingFee, valueAddedTaxRateBD);
            // 增值税 = (不含税收入 - 营业成本) * 税率
            BigDecimal valueAddedTaxBD = incomeWithoutTax.subtract(BigDecimal.valueOf(cost)).multiply(valueAddedTaxRateBD);
            // 附加税 = 增值税 * 12%
            BigDecimal additionalTaxBD = valueAddedTaxBD.multiply(BigDecimal.valueOf(0.12));
            // 印花税(仅在投放时点计算一次)
            BigDecimal stampTaxBD = BigDecimal.ZERO;
            if (expenseDate.getYear() == startDate.getYear() && expenseDate.getMonthValue() == startDate.getMonthValue()) {
                BigDecimal consultingFeeBD = BigDecimal.ZERO;
                if (Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
                    consultingFeeBD = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount())
                            .multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                }
                if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.hui_zu.name())) {
                    // 回租（本金+含税利息/（1+税率)+含税咨询费/(1+税率)）*5/100000
                    long totalInterest = cashFlowList.stream().filter(e -> e.getCashFlowPhase() > 0).filter(e -> Objects.nonNull(e.getInterest())).mapToLong(BudgetPlanPayDetailCashFlow::getInterest).sum();
                    stampTaxBD = BigDecimal.valueOf(totalPrincipal)
                            .add(BigDecimal.valueOf(totalInterest).divide(BigDecimal.ONE.add(valueAddedTaxRateBD), 20, RoundingMode.HALF_UP))
                            .add(consultingFeeBD.divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                            .multiply(BigDecimal.valueOf(0.00005));
                }
                if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.zhi_zu.name())) {
                    // 直租（含税租金总额/(1+税率)+含税咨询费/(1+税率)）*5/100000+投放金额*3/10000
                    long totalRent = cashFlowList.stream().filter(e -> e.getCashFlowPhase() > 0).filter(e -> Objects.nonNull(e.getRent())).mapToLong(BudgetPlanPayDetailCashFlow::getRent).sum();
                    stampTaxBD = BigDecimal.valueOf(totalRent).divide(BigDecimal.ONE.add(valueAddedTaxRateBD), 20, RoundingMode.HALF_UP)
                            .add(consultingFeeBD.divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                            .multiply(BigDecimal.valueOf(0.00005))
                            .add(BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(0.0003)));
                }
                if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.jyx_zu.name())) {
                    // 经营性租赁（含税租金总额/(1+税率)+含税咨询费/(1+税率)）*1/1000
                    long totalRent = cashFlowList.stream().filter(e -> e.getCashFlowPhase() > 0).filter(e -> Objects.nonNull(e.getRent())).mapToLong(BudgetPlanPayDetailCashFlow::getRent).sum();
                    stampTaxBD = BigDecimal.valueOf(totalRent).divide(BigDecimal.ONE.add(valueAddedTaxRateBD), 20, RoundingMode.HALF_UP)
                            .add(consultingFeeBD.divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                            .multiply(BigDecimal.valueOf(0.0001));
                }
            }
            // 毛利 = 营业收入（不含税） - 营业成本（含税） - 附加税 - 印花税
            BigDecimal grossProfitBD = incomeWithoutTax.subtract(BigDecimal.valueOf(cost)).subtract(additionalTaxBD).subtract(stampTaxBD);
            // 拨备（风险准备金） = (剩余本金 - 保证金) * 风险准备金计提比例
            LocalDate nextMonth = expenseDate.plusMonths(1);
            LocalDate targetDate = LocalDate.of(nextMonth.getYear(), nextMonth.getMonthValue(), 1);
            long deposit = 0L;
            if (Objects.nonNull(budgetPlanPayDetailPrice.getDepositRate()) && budgetPlanPayDetailPrice.getDepositRate() > 0 && targetDate.isBefore(endDate)) {
                BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                deposit = Util.mithrasLongDecimalTwo(b.longValue());
            }
            long curRemainingPrincipal = cashFlowList.stream().filter(e -> e.getCashFlowPhase() > 0).filter(e -> !e.getCashFlowDate().isBefore(targetDate)).mapToLong(BudgetPlanPayDetailCashFlow::getPrincipal).sum();
            BigDecimal riskFundBD = BigDecimal.valueOf(curRemainingPrincipal - deposit).multiply(riskFundRateBD);
            if (riskFundBD.longValue() < 0) {
                riskFundBD = BigDecimal.ZERO;
            }
            BigDecimal riskFundDiffBD = riskFundBD.subtract(lastRiskFundBalanceBD);
            lastRiskFundBalanceBD = riskFundBD;
            // 考核利润 = 毛利 - 拨备
            BigDecimal assessmentProfitBD = grossProfitBD.subtract(riskFundDiffBD);
            // 扣费后利润
            BigDecimal assessmentProfitWithoutExpenseBD = FinancialUtil.calculateProfitWithoutExpense(assessmentProfitBD, expenseRateConfig);
            // 费用 = 考核利润 - 扣费后利润
            BigDecimal expenseBD = assessmentProfitBD.subtract(assessmentProfitWithoutExpenseBD);
            // 保存数据
            BudgetPlanPayDetailExpense insert = new BudgetPlanPayDetailExpense();
            insert.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            insert.setBudgetPlanPayId(budgetPlanPay.getId());
            insert.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
            insert.setExpenseDate(expenseDate);
            insert.setExpenseDateYear(expenseDate.getYear());
            insert.setExpenseDateMonth(expenseDate.getMonthValue());
            insert.setValueAddedTax(Util.mithrasLongDecimalTwo(valueAddedTaxBD.longValue()));
            insert.setStampTax(Util.mithrasLongDecimalTwo(stampTaxBD.longValue()));
            insert.setAdditionalTax(Util.mithrasLongDecimalTwo(additionalTaxBD.longValue()));
            insert.setRiskFund(Util.mithrasLongDecimalTwo(riskFundBD.longValue()));
            insert.setRiskFundDiff(Util.mithrasLongDecimalTwo(riskFundDiffBD.longValue()));
            insert.setGrossProfit(Util.mithrasLongDecimalTwo(grossProfitBD.longValue()));
            insert.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
            insert.setAssessmentProfit(Util.mithrasLongDecimalTwo(assessmentProfitBD.longValue()));
            result.add(insert);
            expenseDate = expenseDate.plusMonths(1);
        }
        return result;
    }

    private void fillNewSingletonSpecialInfo(BudgetPlanPay budgetPlanPay, BudgetPlanPayDetail budgetPlanPayDetail) {
        // 根据预算状态确定投放计划明细确认状态
        if (Objects.equals(budgetPlanPay.getBudgetStatus(), BudgetStatusEnum.COLLECTING.name())) {
            // 需要进一步判断流程节点情况
            BudgetPlanPayProcessInfo processInfo = SpringUtil.getBean(BudgetPlanPayProcessInfoService.class).findDeptLastOne(budgetPlanPay.getId(), budgetPlanPayDetail.getBelongDeptId());
            if (Objects.nonNull(processInfo) && Objects.equals(processInfo.getIsCollectFinish(), YesOrNoNumberEnum.YES.getCode())) {
                budgetPlanPayDetail.setIsBusinessheadConfirm(YesOrNoNumberEnum.YES.getCode());
                budgetPlanPayDetail.setIsLeaderinchargeConfirm(YesOrNoNumberEnum.YES.getCode());
            } else {
                budgetPlanPayDetail.setIsBusinessheadConfirm(YesOrNoNumberEnum.NO.getCode());
                budgetPlanPayDetail.setIsLeaderinchargeConfirm(YesOrNoNumberEnum.NO.getCode());
            }
        } else {
            budgetPlanPayDetail.setIsBusinessheadConfirm(YesOrNoNumberEnum.YES.getCode());
            budgetPlanPayDetail.setIsLeaderinchargeConfirm(YesOrNoNumberEnum.YES.getCode());
            budgetPlanPayDetail.setBusinessheadConfirmDate(LocalDate.now());
        }
    }

    private BudgetPlanStatisticsBO statistics(Long budgetPlanPayId, Long belongDeptId) {
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(budgetPlanPayId);
        if (Objects.isNull(budgetPlanPay)) {
            throw new MithrasException("投放计划数据不存在");
        }
        // 找到对应的利润预算
        BudgetPlanProfit budgetPlanProfit = SpringUtil.getBean(BudgetPlanProfitService.class).getOneByBudgetPlanId(budgetPlanPay.getBudgetPlanId());
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        // 查询明细并统计
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = SpringUtil.getBean(BudgetPlanProfitDetailService.class).initDefaultQuery(budgetPlanProfit.getId());
        if (Objects.nonNull(belongDeptId)) {
            query.eq(BudgetPlanProfitDetail::getBelongDeptId, belongDeptId);
        }
        List<BudgetPlanProfitDetail> detailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(query);
        if (CollectionUtil.isEmpty(detailList)) {
            return new BudgetPlanStatisticsBO();
        }
        BudgetPlanStatisticsBO result = new BudgetPlanStatisticsBO();
        for (BudgetPlanProfitDetail profitDetail : detailList) {
            result.setEndOfLastPeriodBalanceTotal(result.getEndOfLastPeriodBalanceTotal() + Optional.ofNullable(profitDetail.getEndOfLastPeriodBalance()).orElse(0L));
            result.setEndOfThisPeriodBalanceTotal(result.getEndOfThisPeriodBalanceTotal() + Optional.ofNullable(profitDetail.getEndOfThisPeriodBalance()).orElse(0L));
            result.setIncomeWithoutTaxTotal(result.getIncomeWithoutTaxTotal() + Optional.ofNullable(profitDetail.getIncomeWithoutTax()).orElse(0L));
            result.setProfitTotal(result.getProfitTotal() + Optional.ofNullable(profitDetail.getAssessmentProfit()).orElse(0L));
            result.setProfitWithoutExpenseTotal(result.getProfitWithoutExpenseTotal() + Optional.ofNullable(profitDetail.getAssessmentProfitWithoutExpense()).orElse(0L));
        }
        // 寻找投放日在预算区间内的数据
        List<BudgetPlanProfitDetail> futureDetailList = detailList.stream().filter(e -> StrUtil.equals(e.getDataCategory(), BudgetPlanDataCategoryEnum.FUTURE.name())).collect(Collectors.toList());

        /*通过利润预算表查询到关联的 投放计划-明细*/
        Set<Long> ids = futureDetailList.stream()
                .filter(e -> Objects.nonNull(e.getBudgetPlanPayDetailId()))
                .map(e -> e.getBudgetPlanPayDetailId())
                .collect(Collectors.toSet());
        List<BudgetPlanPayDetail> budgetPlanPayDetailList = budgetPlanPayDetailService.listByIds(ids);
        // 新增投放额 = 投放计划-明细中剔除未纳入、备选状态   资金拟投放金额求和
        if (CollectionUtil.isNotEmpty(budgetPlanPayDetailList)) {
            Long newActualPayThisPeriodTotal = budgetPlanPayDetailList.stream()
                    .filter(e -> Objects.nonNull(e.getFundPlanPayAmount()) && !ListUtil.of(BudgetPlanPayFundPlanEnum.BACKUP.name(), BudgetPlanPayFundPlanEnum.NOT_BRING_INTO.name()).contains(e.getBringIntoFundPlan()) )
                    .mapToLong(BudgetPlanPayDetail::getFundPlanPayAmount).sum();
            result.setNewActualPayThisPeriodTotal( newActualPayThisPeriodTotal);
        }


        Map<Long, Long> newActualPayThisPeriodTotalMap = budgetPlanPayDetailList.stream()
                .filter(e -> Objects.nonNull(e.getFundPlanPayAmount()) && !ListUtil.of(BudgetPlanPayFundPlanEnum.BACKUP.name(), BudgetPlanPayFundPlanEnum.NOT_BRING_INTO.name()).contains(e.getBringIntoFundPlan()) )
                .collect(Collectors.toMap(
                        BudgetPlanPayDetail::getId,
                        BudgetPlanPayDetail::getFundPlanPayAmount
                ));
        // 加权平均IRR = 预算区间内按照投放金额的加权平均值和
        if (Objects.nonNull(result.getNewActualPayThisPeriodTotal())) {
            BigDecimal irrTempBD = null;
            for (BudgetPlanProfitDetail detail : futureDetailList) {
                if (Objects.isNull(newActualPayThisPeriodTotalMap.get(detail.getBudgetPlanPayDetailId())) || Objects.isNull(detail.getIrr())) {
                    continue;
                }
                if (Objects.isNull(irrTempBD)) {
                    irrTempBD = BigDecimal.ZERO;
                }
                irrTempBD = irrTempBD.add(BigDecimal.valueOf(newActualPayThisPeriodTotalMap.get(detail.getBudgetPlanPayDetailId()) * detail.getIrr()));
            }
            if (Objects.nonNull(irrTempBD) && result.getNewActualPayThisPeriodTotal() != 0) {
                BigDecimal irrAverageBD = irrTempBD.divide(BigDecimal.valueOf(result.getNewActualPayThisPeriodTotal()), 20, RoundingMode.HALF_UP);
                result.setPriorityAverageIrr(Util.mithrasIntegerDecimalTwo(irrAverageBD.intValue()));
            }
        }
        return result;
    }

    public BudgetPlanPayDetail getLastMonthBudgetPayDetailByProjectCode(String projectCode) {
        LambdaQueryWrapper<BudgetPlanPay> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPay::getBudgetType, Arrays.asList(BudgetPlanTypeEnum.MONTH.name(),BudgetPlanTypeEnum.MONTH_ADJUST.name()));
        query.eq(BudgetPlanPay::getBudgetStatus, BudgetStatusEnum.CONFIRM.name());
        query.orderByDesc(BudgetPlanPay::getId);
        query.last(StringUtil.mysqlLimitOne());
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getOne(query);
        if (Objects.isNull(budgetPlanPay)) {
            return null;
        }
        List<BudgetPlanPayDetail> budgetPlanPayDetails = this.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery().eq(BudgetPlanPayDetail::getBudgetPlanId, budgetPlanPay.getId()).eq(BudgetPlanPayDetail::getProjCode, projectCode));
        return CollectionUtil.isEmpty(budgetPlanPayDetails) ? null : budgetPlanPayDetails.get(0);
    }
}
