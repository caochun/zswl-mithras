package cn.zswltech.mithras.application.orchestration.fund.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayConverter;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.OrganizationType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.*;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPayAccount;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingPayAccountService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.*;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptRepayBaseInfoLibMapper;
import cn.zswltech.mithras.fund.persistence.model.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.financing.*;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowIRRBO;
import cn.zswltech.mithras.fund.application.financing.model.ComprehensiveFinancingCostBO;
import cn.zswltech.mithras.projectprocess.application.model.DailyDiscountRateCalcResultBO;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingFeeDetailService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayEstimateService;
import cn.zswltech.mithras.fund.application.financing.*;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayListQueryDto;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptAccountService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayCashDepositService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundRepayAccountService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingCollectAccountLibService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingPayAccountLibService;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingBaseInfoLibHandler;
import cn.zswltech.mithras.fund.versioning.receiptrepay.FundReceiptRepayBaseInfoLibService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Service
@Slf4j
public class FundReceiptRepayBaseInfoService
        extends ServiceImpl<FundReceiptRepayBaseInfoMapper, FundReceiptRepayBaseInfo> {
    @Resource
    private FundReceiptRepayBaseInfoLibMapper baseLibMapper;
    @Resource
    private FundFinancingBaseInfoLibMapper fundFinancingBaseInfoLibMapper;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundReceiptRepayConverter fundReceiptRepayConverter;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundFinancingRepayActualLibMapper fundFinancingRepayActualLibMapper;
    @Resource
    private FundReceiptRepayBorrowingService fundReceiptRepayBorrowingService;
    @Resource
    private FundFinancingPlanLibMapper fundFinancingPlanLibMapper;
    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;
    @Resource
    private FundReceiptRepayCashDepositService fundReceiptRepayCashDepositService;
    @Resource
    private FundFinancingPayAccountLibService financingPayAccountLibService;
    @Resource
    private FundRepayAccountService fundRepayAccountService;
    @Resource
    private FundFinancingCollectAccountLibService financingCollectAccountLibService;
    @Resource
    private FundReceiptAccountService fundReceiptAccountService;
    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;

    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingPayAccountService directFinancingPayAccountService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private FundDirectFinancingFeeDetailService directFinancingFeeDetailService;
    @Resource
    private FundReceiptRepayBaseInfoLibService fundReceiptRepayBaseInfoLibService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoLibHandler financingBaseInfoLibHandler;
    @Resource
    private FundReceiptFlowPlanService fundReceiptFlowPlanService;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource
    private FundFinancingRepayActualService fundFinancingRepayActualService;
    @Resource
    private FundFinancingFeeDetailService financingFeeDetailService;
    @Resource
    private FundDirectFinancingFeeDetailService fundDirectFinancingFeeDetailService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingRepayEstimateService financingRepayEstimateService;

    public FundReceiptRepayBaseInfo getOneByFinancingId(Long financingId, boolean isDirect) {
        LambdaQueryWrapper<FundReceiptRepayBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayBaseInfo::getFinancingId, financingId);
        if (isDirect) {
            query.eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT");
        } else {
            query.isNull(FundReceiptRepayBaseInfo::getFinancingType);
        }
        query.orderByDesc(FundReceiptRepayBaseInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public PageR<FundReceiptRepayBaseInfoListRSP> list(FundReceiptRepayBaseInfoListREQ req) {
        FundReceiptRepayListQueryDto queryDto = fundReceiptRepayConverter.listReq2QueryDto(req);
        Page<FundReceiptRepayBaseInfo> page =
                baseMapper.advancedList(new Page<>(req.getPage(), req.getPageSize()), queryDto);
        if (page.getRecords().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Set<Long> receiptRepayIds = page.getRecords().stream()
                .map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toSet());
        Set<Long> financingIds = page.getRecords().stream()
                .map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toSet());
//        Set<Long> orgIds = page.getRecords().stream()
//                .map(FundReceiptRepayBaseInfo::getFinancingOrgId).collect(Collectors.toSet());
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByFinancingId(financingIds);
        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(refMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));

        Set<Long> userIds = page.getRecords().stream()
                .map(FundReceiptRepayBaseInfo::getCreateBy).collect(Collectors.toSet());
        // 查询融资机构名称
//        Map<Long, String> orgNames = fundOrganizationService.getNamesByIds(orgIds);
        // 查询最新版本的融资信息
//        Map<Long, Long> financingAmounts = fundFinancingBaseInfoLibMapper.selectList(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery()
//                .in(ObjectUtil.isNotEmpty(financingIds), FundFinancingBaseInfoLib::getOriginId, financingIds)
//                .eq(FundFinancingBaseInfoLib::getVersionType, 1)
//                .groupBy(FundFinancingBaseInfoLib::getOriginId)
//                .orderByDesc(FundFinancingBaseInfoLib::getVersion)).stream().collect(Collectors.toMap(FundFinancingBaseInfoLib::getOriginId, FundFinancingBaseInfo::getFinancingAmount, (k1, k2) -> k1));
        Map<Long, FundFinancingBaseInfo> financingBaseInfoMap;
        Map<Long, FundDirectFinancingBaseInfo> directFinancingBaseInfoMap;
        if (CollectionUtil.isEmpty(financingIds)) {
            financingBaseInfoMap = Collections.emptyMap();
            directFinancingBaseInfoMap = Collections.emptyMap();
        } else {
            financingBaseInfoMap = financingBaseInfoLibHandler.listLatestByOriginIds(financingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfoLib::getOriginId, Function.identity(), (k1, k2) -> k1));
            directFinancingBaseInfoMap = Optional.ofNullable(directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().in(FundDirectFinancingBaseInfo::getId, financingIds)))
                    .map(m -> m.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()))).orElse(Collections.emptyMap());
        }
        List<FundReceiptRepayBaseInfoListRSP> rspList = new ArrayList<>();
        // 查询收付款对应的现金流
        Map<Long, List<FundReceiptRepayCashFlow>> cashFlows
                = fundReceiptRepayCashFlowService.listByReceiptRepayIds(receiptRepayIds);
        // 查询创建人姓名
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        LocalDate today = LocalDateTimeUtil.parseDate(req.getRepayMonth(), DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
        LocalDate lastDayOfLastMonth = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate nextMonth = LocalDate.of(today.getYear(), today.getMonth(), 1).plusMonths(1);
        for (FundReceiptRepayBaseInfo record : page.getRecords()) {
            FundFinancingBaseInfo financingBaseInfo = financingBaseInfoMap.get(record.getFinancingId());
            FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingBaseInfoMap.get(record.getFinancingId());
            FundReceiptRepayBaseInfoListRSP rsp = new FundReceiptRepayBaseInfoListRSP();
            rsp.setId(record.getId());
            rsp.setFinancingId(record.getFinancingId());
            rsp.setReceiptRepayCode(record.getReceiptRepayCode());
//            if (record.getFinancingOrgId() != null) {
//                rsp.setFinancingOrgName(orgNames.get(record.getFinancingOrgId()));
//            } else {
//                rsp.setFinancingOrgName(record.getFinancingChannel());
//            }
//            rsp.setFinancingOrgName(record.getFinancingOrgNames());
            if ("DIRECT".equals(record.getFinancingType())) {
                rsp.setFinancingAmount(record.getFinancingAmount());
                rsp.setFinancingCode(directFinancingBaseInfo.getFinancingCode());
                rsp.setFinancingOrgName(directFinancingBaseInfo.getProductName());
                //rsp.setFinancingOrgName(record.getFinancingChannel());
            } else {
                rsp.setFinancingCode(financingBaseInfo.getFinancingCode());
                rsp.setFinancingAmount(financingBaseInfo.getFinancingAmount());
                List<FundFinancingCreditRef> refList = refMap.get(record.getFinancingId());
                Long orgId = Optional.ofNullable(refList).map(m -> m.get(0)).map(FundFinancingCreditRef::getOrganizationId).orElse(null);
                rsp.setFinancingOrgName(orgIdNameMap.get(orgId));
            }
            Long monthRepayAmount = 0L, monthRepayPrinciple = 0L, monthRepayInterest = 0L;
            for (FundReceiptRepayCashFlow cashFlow : cashFlows.getOrDefault(record.getId(), new ArrayList<>())) {
                CashFlowState state = CashFlowState.of(cashFlow.getWriteOffState());
                if (cashFlow.getPrincipleAmount() == null) {
                    cashFlow.setPrincipleAmount(0L);
                }
                if (cashFlow.getInterestAmount() == null) {
                    cashFlow.setInterestAmount(0L);
                }
                if (cashFlow.getRepayDate().isBefore(nextMonth) && cashFlow.getRepayDate().isAfter(lastDayOfLastMonth)) {
                    monthRepayPrinciple += LongUtil.null2zero(cashFlow.getPrincipleAmount());
                    monthRepayInterest += LongUtil.null2zero(cashFlow.getInterestAmount());
                    monthRepayAmount += LongUtil.null2zero(cashFlow.getPrincipleAmount());
                    monthRepayAmount += LongUtil.null2zero(cashFlow.getInterestAmount());
                }
            }
//            rsp.setRepayPrincipal(repayPrincipal);
//            rsp.setRepayInterest(repayInterest);
            rsp.setFinancingType(record.getFinancingType());
            rsp.setFinancingBizType(record.getFinancingBizType());
            rsp.setMonthRepayAmount(monthRepayAmount);
            rsp.setMonthRepayPrincipal(monthRepayPrinciple);
            rsp.setMonthRepayInterest(monthRepayInterest);
//            rsp.setOneYearPrincipal(oneYearPrincipal);
            rsp.setCreateByName(userId2Name.get(record.getCreateBy()));
            rsp.setProcessState(record.getProcessState());
            rsp.setReceiptRepayState(record.getReceiptRepayState());
            rsp.setCreateTime(record.getCreateTime());
            rsp.setUpdateTime(record.getUpdateTime());
            rspList.add(rsp);
        }
        return PageR.of(page, rspList);
    }

    /**
     * 查询收付款详情
     *
     * @param req 请求参数
     * @return 收付款详情
     */
    public FundReceiptRepayBaseInfoDetailRSP detail(FundReceiptRepayBaseInfoDetailREQ req) {
        FundReceiptRepayBaseInfo receiptRepayBaseInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(receiptRepayBaseInfo)) {
            throw new MithrasException("收付款信息不存在");
        }
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            FundReceiptRepayBaseInfoLib baseInfoLib = baseLibMapper.selectOne(
                    Wrappers.<FundReceiptRepayBaseInfoLib>lambdaQuery()
                            .eq(FundReceiptRepayBaseInfoLib::getOriginId, req.getId())
                            .eq(FundReceiptRepayBaseInfoLib::getVersion, req.getVersion())
                            .last("limit 1"));
            receiptRepayBaseInfo = fundReceiptRepayConverter.lib2Entity(baseInfoLib);
        }
        SingleFinancingIdREQ singleFinancingIdReq = new SingleFinancingIdREQ();
        singleFinancingIdReq.setFinancingId(receiptRepayBaseInfo.getFinancingId());
        singleFinancingIdReq.setVersion(receiptRepayBaseInfo.getFinancingVersion());
        FundFinancingBaseInfoDetailRSP financingBaseInfo = fundFinancingBaseInfoService.detail(singleFinancingIdReq);
        FundFinancingPlanDetailRSP financingPlan = fundFinancingPlanService.detail(singleFinancingIdReq);
        FundReceiptRepayBaseInfoDetailRSP rsp = fundReceiptRepayConverter.joinDetailRsp(receiptRepayBaseInfo, financingBaseInfo, financingPlan);
        // 查询收付款对应的现金流计算总利息
        fundReceiptRepayCashFlowService.listByReceiptRepayId(req.getId(), req.getVersion())
                .ifPresent(cashFlows -> {
                    Long totalInterest = cashFlows.stream().map(s -> LongUtil.null2zero(s.getInterestAmount()))
                            .reduce(0L, Long::sum);
                    rsp.setTotalInterest(totalInterest);
                });
        List<FundOrganization> organizationList = fundOrganizationService.getByFinancingId(financingBaseInfo.getId());
        rsp.setFinancingOrgName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
        rsp.setFactoringFee(LongUtil.null2zero(financingPlan.getServiceChargeAmount()));
        rsp.setLicenseFee(LongUtil.null2zero(financingPlan.getLicenseAmount()));
        rsp.setOtherFee(LongUtil.null2zero(financingPlan.getOtherAmount()));
        rsp.setCashDeposit(LongUtil.null2zero(financingPlan.getEarnestMoneyAmount()));
        return rsp;
    }

    public FundDirectFinancingBaseInfoDetailRSP directDetail(Long id) {
        FundReceiptRepayBaseInfo directReceipt = getById(id);

        FundDirectFinancingBaseInfoDetailRSP financingRsp =
                fundDirectFinancingBaseInfoService.detail(directReceipt.getFinancingId());
        if (ObjectUtil.isNotEmpty(financingRsp.getIssuingScale())) {
            BigDecimal bigDecimal = new BigDecimal(financingRsp.getIssuingScale()).multiply(new BigDecimal(10000));
            financingRsp.setIssuingScale(bigDecimal.longValue());
        }
        financingRsp.setRemark(directReceipt.getRemark());
        return financingRsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundReceiptRepayBaseInfoModifyREQ req) {
        FundReceiptRepayBaseInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptRepayBaseInfo info = BeanUtil.copyProperties(req, FundReceiptRepayBaseInfo.class);
        baseMapper.updateAnnotationIncludeNullById(info);
        fundReceiptRepayStateService.modifyUpdateProcessState(originalInfo.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processFinancingEffectEnd(Long financingId) {
        FundReceiptRepayBaseInfo baseInfo = baseMapper.selectOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                .last("limit 1"));
        FundFinancingBaseInfoLib financingBaseInfoLib = fundFinancingBaseInfoLibMapper.selectOne(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery()
                .eq(FundFinancingBaseInfoLib::getOriginId, financingId)
                .eq(FundFinancingBaseInfoLib::getVersionType, 1)
                .orderByDesc(FundFinancingBaseInfoLib::getVersion)
                .last("limit 1"));
        List<FundFinancingRepayActualLib> repayActualLibs = fundFinancingRepayActualLibMapper.selectList(Wrappers.<FundFinancingRepayActualLib>lambdaQuery()
                .eq(FundFinancingRepayActualLib::getFinancingId, financingId)
                .eq(FundFinancingRepayActualLib::getVersion, financingBaseInfoLib.getVersion())
                .gt(FundFinancingRepayActual::getPhase, 0)
                .orderByAsc(FundFinancingRepayActualLib::getRepayDate));
        Long receiptRepayId;
        if (ObjectUtil.isNull(baseInfo)) {
            receiptRepayId = addReceiptRepay(financingBaseInfoLib, repayActualLibs);
        } else {
            receiptRepayId = baseInfo.getId();
            upgradeRecipeRepay(baseInfo, financingBaseInfoLib, repayActualLibs);
            // 状态更新
            fundReceiptRepayStateService.handleFinancingChange(baseInfo.getId());
        }
        // 同步数据至新的统一现金流表，老表保留继续主要用于审批流历史快照及不改动老的一些业务逻辑
        if (Objects.nonNull(receiptRepayId)) {
            this.syncToFlowPlan(receiptRepayId);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syncToFlowPlan(Long receiptRepayId) {
        // 同步融资款
        fundReceiptFlowPlanService.syncFromBorrowing(receiptRepayId);
        // 同步还本付息
        fundReceiptFlowPlanService.syncFromRepay(receiptRepayId);
        // 同步保证金
        fundReceiptFlowPlanService.syncFromDeposit(receiptRepayId);
        // 同步费用项
        fundReceiptFlowPlanService.syncFromExpense(receiptRepayId);
    }

    private Long addReceiptRepay(FundFinancingBaseInfoLib financingBaseInfoLib, List<FundFinancingRepayActualLib> repayActualLibs) {
        log.info("融资起息，新增收付款信息");

        // 插入收付款基础信息
        FundReceiptRepayBaseInfo baseInfo = new FundReceiptRepayBaseInfo();
        baseInfo.setFinancingBizType(financingBaseInfoLib.getBusinessType());
        baseInfo.setReceiptRepayState(ReceiptRepayState.NO_WRITE_OFF.name());
        baseInfo.setCreateBy(financingBaseInfoLib.getCreateBy());
        baseInfo.setFinancingId(financingBaseInfoLib.getOriginId());
        baseInfo.setFundManager(financingBaseInfoLib.getFundManagerId());
        if (ObjectUtil.isNotEmpty(repayActualLibs)) {
            baseInfo.setNextRepayDate(repayActualLibs.get(0).getRepayDate());
        }
//        baseInfo.setFinancingOrgId(financingBaseInfoLib.getOrganizationIds());
//        baseInfo.setFinancingOrgNames(financingBaseInfoLib.getOrganizationNames());
        baseInfo.setReceiptRepayCode(financingBaseInfoLib.getFinancingCode() + "F");
        baseInfo.setFinancingAmount(financingBaseInfoLib.getFinancingAmount());
        baseInfo.setFinancingVersion(financingBaseInfoLib.getVersion());
        baseInfo.setProcessState(ProcessState.UN_SUBMIT.name());
        baseMapper.insert(baseInfo);
        // 生成借款流入
        FundReceiptRepayBorrowing borrowing = new FundReceiptRepayBorrowing();
        borrowing.setReceiptRepayId(baseInfo.getId());
        borrowing.setActualLoanDate(financingBaseInfoLib.getActualLoanDate());
        borrowing.setTerm(0);
        borrowing.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "-000");
        borrowing.setPrincipal(financingBaseInfoLib.getFinancingAmount());
        borrowing.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        fundReceiptRepayBorrowingService.save(borrowing);
        //生成本金与利息一览表
        List<FundReceiptRepayCashFlow> cashFlows = new ArrayList<>();
        for (FundFinancingRepayActualLib repayActualLib : repayActualLibs) {
            FundReceiptRepayCashFlow cashFlow = fundReceiptRepayConverter.repayActual2CashFlow(repayActualLib);
            cashFlow.setReceiptRepayId(baseInfo.getId());
            cashFlow.setFinancingId(baseInfo.getFinancingId());
            cashFlow.setCashFlowId(repayActualLib.getOriginId());
            cashFlow.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            cashFlows.add(cashFlow);
        }
        fundReceiptRepayCashFlowService.saveBatch(cashFlows);
        // 生成费用一览表
        FundFinancingPlanLib financingPlanLib = fundFinancingPlanLibMapper.selectOne(Wrappers.<FundFinancingPlanLib>lambdaQuery()
                .eq(FundFinancingPlanLib::getFinancingId, financingBaseInfoLib.getOriginId())
                .eq(FundFinancingPlanLib::getVersion, financingBaseInfoLib.getVersion())
                .last("limit 1"));
        List<FundReceiptRepayExpense> expenses = new ArrayList<>();
//        if (ObjectUtil.isNotEmpty(financingPlanLib.getServiceChargeAmount())) {
//            FundReceiptRepayExpense expense = new FundReceiptRepayExpense();
//            expense.setReceiptRepayId(baseInfo.getId());
//            expense.setExpenseType(ExpenseType.FACTORING_FEE.name());
//            expense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
//            expense.setTotalAmount(financingPlanLib.getServiceChargeAmount());
//            expense.setTotalPaidAmount(0L);
//            expense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A1");
//            expenses.add(expense);
//        }
        if (ObjectUtil.isNotEmpty(financingPlanLib.getLicenseAmount())) {
            FundReceiptRepayExpense expense = new FundReceiptRepayExpense();
            expense.setReceiptRepayId(baseInfo.getId());
            expense.setExpenseType(ExpenseType.OPEN_LICENSE_FEE.name());
            expense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            expense.setTotalAmount(financingPlanLib.getLicenseAmount());
            expense.setTotalPaidAmount(0L);
            expense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expenses.size() + 1));
            expenses.add(expense);
        }
        if (ObjectUtil.isNotEmpty(financingPlanLib.getOtherAmount())) {
            FundReceiptRepayExpense expense = new FundReceiptRepayExpense();
            expense.setReceiptRepayId(baseInfo.getId());
            expense.setExpenseType(ExpenseType.OTHER_FEE.name());
            expense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            expense.setTotalAmount(financingPlanLib.getOtherAmount());
            expense.setTotalPaidAmount(0L);
            expense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expenses.size() + 1));
            expenses.add(expense);
        }
        // 费用项明细
        List<FundFinancingFeeDetail> fees = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                .eq(FundFinancingFeeDetail::getFinancingId, financingBaseInfoLib.getOriginId()));
        int count = 1;
        for (FundFinancingFeeDetail fee : fees) {
            FundReceiptRepayExpense repayExpense = new FundReceiptRepayExpense();
            repayExpense.setReceiptRepayId(baseInfo.getId());
            repayExpense.setExpenseType(fee.getExpenseType());
            repayExpense.setTotalAmount(fee.getAmount());
            repayExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            repayExpense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + count);
            repayExpense.setTotalPaidAmount(0L);
            repayExpense.setFeeId(fee.getId());
            count++;
            expenses.add(repayExpense);
        }

        fundReceiptRepayExpenseService.saveBatch(expenses);
        // 生成保证金明细
        List<FundReceiptRepayCashDeposit> cashDeposits = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(financingPlanLib.getEarnestMoneyAmount())) {
            FundReceiptRepayCashDeposit depositOut = new FundReceiptRepayCashDeposit();
            depositOut.setReceiptRepayId(baseInfo.getId());
            depositOut.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expenses.size() + 1));
            depositOut.setAmount(financingPlanLib.getEarnestMoneyAmount());
            depositOut.setPaidAmount(0L);
            depositOut.setReceiptAmount(0L);
            depositOut.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            depositOut.setDepositCashFlowType(DepositCashFlowType.DEPOSIT_PAYMENT.name());
            cashDeposits.add(depositOut);
            FundReceiptRepayCashDeposit depositIn = new FundReceiptRepayCashDeposit();
            depositIn.setReceiptRepayId(baseInfo.getId());
            depositIn.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expenses.size() + 2));
            depositIn.setAmount(financingPlanLib.getEarnestMoneyAmount());
            depositIn.setPaidAmount(0L);
            depositIn.setReceiptAmount(0L);
            depositIn.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            depositIn.setDepositCashFlowType(DepositCashFlowType.DEPOSIT_RETURN.name());
            cashDeposits.add(depositIn);
        }
        fundReceiptRepayCashDepositService.saveBatch(cashDeposits);
        // 拷贝账户信息
        List<FundFinancingPayAccountLib> payAccounts = financingPayAccountLibService.getVersionList(baseInfo.getFinancingId(), baseInfo.getFinancingVersion());
        List<FundRepayAccount> repayAccounts = new ArrayList<>();
        for (FundFinancingPayAccountLib payAccount : payAccounts) {
            FundRepayAccount repayAccount = fundReceiptRepayConverter.copyPayAccount(payAccount);
            repayAccount.setId(null);
            repayAccount.setReceiptRepayId(baseInfo.getId());
            repayAccounts.add(repayAccount);
        }
        fundRepayAccountService.saveBatch(repayAccounts);

        List<FundFinancingCollectAccountLib> collectAccounts = financingCollectAccountLibService.getVersionList(baseInfo.getFinancingId(), baseInfo.getFinancingVersion());
        List<FundReceiptAccount> receiptAccounts = new ArrayList<>();
        for (FundFinancingCollectAccountLib collectAccount : collectAccounts) {
            FundReceiptAccount receiptAccount = fundReceiptRepayConverter.copyCollectAccount(collectAccount);
            receiptAccount.setId(null);
            receiptAccount.setReceiptRepayId(baseInfo.getId());
            receiptAccounts.add(receiptAccount);
        }
        fundReceiptAccountService.saveBatch(receiptAccounts);

        return baseInfo.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void upgradeRecipeRepay(FundReceiptRepayBaseInfo baseInfo,
                                   FundFinancingBaseInfoLib financingBaseInfoLib,
                                   List<FundFinancingRepayActualLib> repayActualLibs) {
        log.info("融资版本升级，同步升级收付款信息");
        // 更新基本信息
        baseInfo.setFundManager(financingBaseInfoLib.getFundManagerId());
        baseInfo.setFinancingAmount(financingBaseInfoLib.getFinancingAmount());
        baseInfo.setFinancingVersion(financingBaseInfoLib.getVersion());
        baseMapper.updateById(baseInfo);
        // 更新现金流
        Map<Long, FundReceiptRepayCashFlow> cashFlowMap = fundReceiptRepayCashFlowService.list(
                        Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                                .eq(FundReceiptRepayCashFlow::getReceiptRepayId, baseInfo.getId())).stream()
                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowId, item -> item, (k1, k2) -> k1));
        Map<Long, FundFinancingRepayActualLib> actualLibMap = repayActualLibs.stream()
                .collect(Collectors.toMap(FundFinancingRepayActualLib::getOriginId, item -> item, (k1, k2) -> k1));
        List<FundReceiptRepayCashFlow> updateList = new ArrayList<>();
        List<FundReceiptRepayCashFlow> insertList = new ArrayList<>();

        Iterator<Map.Entry<Long, FundFinancingRepayActualLib>> iterator = actualLibMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, FundFinancingRepayActualLib> entry = iterator.next();
            FundReceiptRepayCashFlow cashFlow = cashFlowMap.get(entry.getKey());
            if (cashFlow == null) {
                //新增
                cashFlow = fundReceiptRepayConverter.repayActual2CashFlow(entry.getValue());
                cashFlow.setReceiptRepayId(baseInfo.getId());
                cashFlow.setCashFlowId(entry.getKey());
                cashFlow.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                insertList.add(cashFlow);
            } else {
                BeanUtil.copyProperties(entry.getValue(), cashFlow, CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties("id", "receiptRepayId", "cashFlowId", "writeOffState"));
                updateList.add(cashFlow);
                cashFlowMap.remove(entry.getKey());
            }
        }
        //更新cash flow
        List<Long> deleteIds = new ArrayList<>(cashFlowMap.keySet());
        if (deleteIds.size() > 0) {
            fundReceiptRepayCashFlowService.remove(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                    .in(FundReceiptRepayCashFlow::getCashFlowId, deleteIds));
        }
        if (updateList.size() > 0) {
            fundReceiptRepayCashFlowService.updateBatchById(updateList);
        }
        if (insertList.size() > 0) {
            fundReceiptRepayCashFlowService.saveBatch(insertList);
        }
        // 获取到最新的融资方案
        FundFinancingPlanLib financingPlanLib = fundFinancingPlanLibMapper.selectOne(Wrappers.<FundFinancingPlanLib>lambdaQuery()
                .eq(FundFinancingPlanLib::getFinancingId, financingBaseInfoLib.getOriginId())
                .eq(FundFinancingPlanLib::getVersion, financingBaseInfoLib.getVersion())
                .last("limit 1"));

        // 更新费用
        Map<String, FundReceiptRepayExpense> dbExpenses = fundReceiptRepayExpenseService.list(
                        Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                                .eq(FundReceiptRepayExpense::getReceiptRepayId, baseInfo.getId()))
                .stream().collect(Collectors.toMap(FundReceiptRepayExpense::getExpenseType, item -> item,
                        (k1, k2) -> k1));

        List<FundReceiptRepayExpense> expensesUpdateList = new ArrayList<>();
        List<Long> expensesDeleteList = new ArrayList<>();
//        FundReceiptRepayExpense serviceExpense =
//                dbExpenses.getOrDefault(ExpenseType.FACTORING_FEE.name(), new FundReceiptRepayExpense());
//        if(financingPlanLib.getServiceChargeAmount()==null || financingPlanLib.getServiceChargeAmount()==0L){
//            if(serviceExpense.getId() != null) {
//                expensesDeleteList.add(serviceExpense.getId());
//            }
//        }else{
//            serviceExpense.setReceiptRepayId(baseInfo.getId());
//            if(ObjectUtil.isEmpty(serviceExpense.getExpenseType())) {
//                serviceExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
//                serviceExpense.setExpenseType(ExpenseType.FACTORING_FEE.name());
//            }
//            serviceExpense.setTotalAmount(financingPlanLib.getServiceChargeAmount());
//            serviceExpense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A1");
//            expensesUpdateList.add(serviceExpense);
//        }
        FundReceiptRepayExpense licenseExpense =
                dbExpenses.getOrDefault(ExpenseType.OPEN_LICENSE_FEE.name(), new FundReceiptRepayExpense());
        if (financingPlanLib.getLicenseAmount() == null || financingPlanLib.getLicenseAmount() == 0L) {
            if (licenseExpense.getId() != null) {
                expensesDeleteList.add(licenseExpense.getId());
            }
        } else {
            licenseExpense.setReceiptRepayId(baseInfo.getId());
            if (ObjectUtil.isEmpty(licenseExpense.getExpenseType())) {
                licenseExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                licenseExpense.setExpenseType(ExpenseType.OPEN_LICENSE_FEE.name());
            }
            licenseExpense.setTotalAmount(financingPlanLib.getLicenseAmount());
            licenseExpense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expensesUpdateList.size() + 1));
            expensesUpdateList.add(licenseExpense);
        }
        FundReceiptRepayExpense otherExpense =
                dbExpenses.getOrDefault(ExpenseType.OTHER_FEE.name(), new FundReceiptRepayExpense());
        if (financingPlanLib.getOtherAmount() == null || financingPlanLib.getOtherAmount() == 0L) {
            if (otherExpense.getId() != null) {
                expensesDeleteList.add(otherExpense.getId());
            }
        } else {
            otherExpense.setReceiptRepayId(baseInfo.getId());
            if (ObjectUtil.isEmpty(otherExpense.getExpenseType())) {
                otherExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                otherExpense.setExpenseType(ExpenseType.OTHER_FEE.name());
            }
            otherExpense.setTotalAmount(financingPlanLib.getOtherAmount());
            otherExpense.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expensesUpdateList.size() + 1));
            expensesUpdateList.add(otherExpense);
        }


        //更新费用明细
        //1 查询收付款的费用明细
        Map<Long, FundReceiptRepayExpense> expenseMap = fundReceiptRepayExpenseService.list(
                        Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                                .eq(FundReceiptRepayExpense::getReceiptRepayId, baseInfo.getId())).stream()
                .collect(Collectors.toMap(FundReceiptRepayExpense::getFeeId, v -> v, (k1, k2) -> k1));
        //2 查询融资的费用明细
        Map<Long, FundFinancingFeeDetail> feeMap = financingFeeDetailService.list(
                        Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                                .eq(FundFinancingFeeDetail::getFinancingId, financingBaseInfoLib.getOriginId())).stream()
                .collect(Collectors.toMap(FundFinancingFeeDetail::getId, v -> v, (k1, k2) -> k1));
        //3 更新收付款的费用明细
        Iterator<Map.Entry<Long, FundFinancingFeeDetail>> feeIterator = feeMap.entrySet()
                .stream().sorted(Map.Entry.comparingByKey()).iterator();
        int count = fundReceiptRepayExpenseService.count(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getReceiptRepayId, baseInfo.getId()));
        while (feeIterator.hasNext()) {
            Map.Entry<Long, FundFinancingFeeDetail> entry = feeIterator.next();
            FundReceiptRepayExpense expense = expenseMap.get(entry.getKey());
            FundFinancingFeeDetail fee = entry.getValue();
            if (expense == null) {
                //新增
                FundReceiptRepayExpense expenseToAdd = new FundReceiptRepayExpense();
                expenseToAdd.setReceiptRepayId(baseInfo.getId());
                expenseToAdd.setExpenseType(fee.getExpenseType());
                expenseToAdd.setTotalAmount(fee.getAmount());
                expenseToAdd.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                expenseToAdd.setTotalPaidAmount(0L);
                expenseToAdd.setFeeId(fee.getId());
                expenseToAdd.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (count + 1));
                count++;
                expensesUpdateList.add(expenseToAdd);
            } else {
                BeanUtil.copyProperties(fee, expense, CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties("id", "receiptRepayId", "directFeeId", "writeOffState"));
                expense.setTotalAmount(fee.getAmount());
                expensesUpdateList.add(expense);
                expenseMap.remove(entry.getKey());
            }
        }
        expensesDeleteList.addAll(expenseMap.keySet());
        if (expensesDeleteList.size() > 0) {
            fundReceiptRepayExpenseService.removeByIds(expensesDeleteList);
        }
        if (expensesUpdateList.size() > 0) {
            fundReceiptRepayExpenseService.saveOrUpdateBatch(expensesUpdateList);
        }


        // 更新保证金
        if (financingPlanLib.getEarnestMoneyAmount() == null || financingPlanLib.getEarnestMoneyAmount() == 0L) {
            // 保证金为空或0，清空保证金
            fundReceiptRepayCashDepositService.remove(Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery()
                    .eq(FundReceiptRepayCashDeposit::getReceiptRepayId, baseInfo.getId()));
        } else {
            List<FundReceiptRepayCashDeposit> deposit =
                    fundReceiptRepayCashDepositService.list(Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery()
                            .eq(FundReceiptRepayCashDeposit::getReceiptRepayId, baseInfo.getId()));
            if (ObjectUtil.isEmpty(deposit)) {
                // 收付款保证金为空，新建
                List<FundReceiptRepayCashDeposit> cashDeposits = new ArrayList<>();
                FundReceiptRepayCashDeposit depositOut = new FundReceiptRepayCashDeposit();
                depositOut.setReceiptRepayId(baseInfo.getId());
                depositOut.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expensesUpdateList.size() + 1));
                depositOut.setAmount(financingPlanLib.getEarnestMoneyAmount());
                depositOut.setPaidAmount(0L);
                depositOut.setReceiptAmount(0L);
                depositOut.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                depositOut.setDepositCashFlowType(DepositCashFlowType.DEPOSIT_PAYMENT.name());
                cashDeposits.add(depositOut);
                FundReceiptRepayCashDeposit depositIn = new FundReceiptRepayCashDeposit();
                depositIn.setReceiptRepayId(baseInfo.getId());
                depositIn.setCashFlowCode(financingBaseInfoLib.getFinancingCode() + "A" + (expensesUpdateList.size() + 2));
                depositIn.setAmount(financingPlanLib.getEarnestMoneyAmount());
                depositIn.setPaidAmount(0L);
                depositIn.setReceiptAmount(0L);
                depositIn.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                depositIn.setDepositCashFlowType(DepositCashFlowType.DEPOSIT_RETURN.name());
                cashDeposits.add(depositIn);
                fundReceiptRepayCashDepositService.saveBatch(cashDeposits);
            } else {
                if (!deposit.get(0).getAmount().equals(financingPlanLib.getEarnestMoneyAmount())) {
                    // 保证金金额不一致，更新保证金
                    for (FundReceiptRepayCashDeposit item : deposit) {
                        item.setAmount(financingPlanLib.getEarnestMoneyAmount());
                    }
                    fundReceiptRepayCashDepositService.updateBatchById(deposit);
                }
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long addDirectFinancingReceipt(Long financingId) {
        FundDirectFinancingBaseInfo financingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        FundReceiptRepayBaseInfo receiptRepayBaseInfo = new FundReceiptRepayBaseInfo();
        receiptRepayBaseInfo.setFinancingBizType(financingBaseInfo.getDirectFinancingType());
        receiptRepayBaseInfo.setFinancingId(financingId);
        receiptRepayBaseInfo.setFinancingChannel(financingBaseInfo.getProductName());
        receiptRepayBaseInfo.setFinancingType("DIRECT");
        receiptRepayBaseInfo.setReceiptRepayCode(financingBaseInfo.getFinancingCode() + "F");
        receiptRepayBaseInfo.setRemark(financingBaseInfo.getRemark());
        receiptRepayBaseInfo.setReceiptRepayState(ReceiptRepayState.NO_WRITE_OFF.name());
        receiptRepayBaseInfo.setProcessState(ProcessState.UN_SUBMIT.name());
        receiptRepayBaseInfo.setFundManager(financingBaseInfo.getFundManagerId());
        receiptRepayBaseInfo.setFinancingAmount(financingBaseInfo.getFinancingAmount() * 10000);

        List<FundDirectFinancingRepayActual> repayActuals = directFinancingRepayActualService.list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                .eq(FundDirectFinancingRepayActual::getFinancingId, financingId)
                .ne(FundDirectFinancingRepayActual::getPhase, 0)
                .orderByAsc(FundDirectFinancingRepayActual::getPhase));
        if (ObjectUtil.isNotEmpty(repayActuals)) {
            receiptRepayBaseInfo.setNextRepayDate(repayActuals.get(0).getRepayDate());
        }
        save(receiptRepayBaseInfo);

        //生成本金与利息一览表
        LocalDate valueDate = null;
        List<FundReceiptRepayCashFlow> cashFlows = new ArrayList<>();
        for (FundDirectFinancingRepayActual repayActual : repayActuals) {
            if (repayActual.getPhase() == 1) {
                valueDate = repayActual.getRepayDate();
            }
            FundReceiptRepayCashFlow cashFlow = fundReceiptRepayConverter.repayActual2CashFlow(repayActual);
            cashFlow.setReceiptRepayId(receiptRepayBaseInfo.getId());
//            cashFlow.setFinancingId(receiptRepayBaseInfo.getFinancingId());
            cashFlow.setCashFlowId(repayActual.getId());
            cashFlow.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            cashFlows.add(cashFlow);
        }
        fundReceiptRepayCashFlowService.saveBatch(cashFlows);

        // 生成借款流入
        FundReceiptRepayBorrowing borrowing = new FundReceiptRepayBorrowing();
        borrowing.setReceiptRepayId(receiptRepayBaseInfo.getId());
        borrowing.setActualLoanDate(valueDate);
        borrowing.setTerm(0);
        borrowing.setCashFlowCode(financingBaseInfo.getFinancingCode() + "-000");
        borrowing.setPrincipal(financingBaseInfo.getFinancingAmount() * 10000L);
        borrowing.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
        fundReceiptRepayBorrowingService.save(borrowing);

        // 费用明细
        List<FundReceiptRepayExpense> expenses = new ArrayList<>();
        List<FundDirectFinancingFeeDetail> fees = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
        int count = 1;
        for (FundDirectFinancingFeeDetail fee : fees) {
            FundReceiptRepayExpense repayExpense = new FundReceiptRepayExpense();
            repayExpense.setReceiptRepayId(receiptRepayBaseInfo.getId());
            repayExpense.setExpenseType(fee.getExpenseType());
            repayExpense.setTotalAmount(fee.getAmount());
            repayExpense.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
            repayExpense.setCashFlowCode(financingBaseInfo.getFinancingCode() + "A" + count);
            repayExpense.setTotalPaidAmount(0L);
            repayExpense.setDirectFeeId(fee.getId());
            count++;
            expenses.add(repayExpense);
        }
        fundReceiptRepayExpenseService.saveBatch(expenses);

        // 拷贝账户信息
        List<FundDirectFinancingPayAccount> payAccounts = directFinancingPayAccountService
                .list(Wrappers.<FundDirectFinancingPayAccount>lambdaQuery()
                        .eq(FundDirectFinancingPayAccount::getFinancingId, financingId));
        List<FundRepayAccount> repayAccounts = new ArrayList<>();
        for (FundDirectFinancingPayAccount payAccount : payAccounts) {
            FundRepayAccount repayAccount = fundReceiptRepayConverter.copyPayAccount(payAccount);
            repayAccount.setId(null);
            repayAccount.setReceiptRepayId(receiptRepayBaseInfo.getId());
            repayAccounts.add(repayAccount);
        }
        fundRepayAccountService.saveBatch(repayAccounts);
        return receiptRepayBaseInfo.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long upgradeDirectFinancingReceipt(Long financingId) {
        FundDirectFinancingBaseInfo financingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        FundReceiptRepayBaseInfo receiptRepayBaseInfo = getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")
                .last("limit 1"));
        receiptRepayBaseInfo.setFinancingAmount(financingBaseInfo.getFinancingAmount() * 10000);
        receiptRepayBaseInfo.setFinancingBizType(financingBaseInfo.getDirectFinancingType());

        List<FundDirectFinancingRepayActual> repayActuals = directFinancingRepayActualService.list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                .eq(FundDirectFinancingRepayActual::getFinancingId, financingId)
                .orderByAsc(FundDirectFinancingRepayActual::getPhase));
        if (ObjectUtil.isNotEmpty(repayActuals)) {
            receiptRepayBaseInfo.setNextRepayDate(repayActuals.get(0).getRepayDate());
        }
        updateById(receiptRepayBaseInfo);
        //更新实际还款表
        Map<Long, FundReceiptRepayCashFlow> cashFlowMap = fundReceiptRepayCashFlowService.list(
                        Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                                .eq(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayBaseInfo.getId())).stream()
                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowId, item -> item, (k1, k2) -> k1));
        Map<Long, FundDirectFinancingRepayActual> actualMap = repayActuals.stream()
                .collect(Collectors.toMap(FundDirectFinancingRepayActual::getId, v -> v, (k1, k2) -> k1));
        List<FundReceiptRepayCashFlow> updateList = new ArrayList<>();
        List<FundReceiptRepayCashFlow> insertList = new ArrayList<>();

        Iterator<Map.Entry<Long, FundDirectFinancingRepayActual>> iterator = actualMap.entrySet()
                .stream().sorted(Map.Entry.comparingByKey()).iterator();
        LocalDate valueDate = null;
        while (iterator.hasNext()) {
            Map.Entry<Long, FundDirectFinancingRepayActual> entry = iterator.next();
            FundDirectFinancingRepayActual value = entry.getValue();
            if (value.getPhase() == 0) {
                continue;
            }
            if (value.getPhase() == 1) {
                valueDate = value.getRepayDate();
            }
            FundReceiptRepayCashFlow cashFlow = cashFlowMap.get(entry.getKey());
            if (cashFlow == null) {
                //新增
                cashFlow = fundReceiptRepayConverter.repayActual2CashFlow(value);
                cashFlow.setReceiptRepayId(receiptRepayBaseInfo.getId());
                cashFlow.setCashFlowId(entry.getKey());
                cashFlow.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                insertList.add(cashFlow);
            } else {
                BeanUtil.copyProperties(value, cashFlow, CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties("id", "receiptRepayId", "cashFlowId", "writeOffState"));
                updateList.add(cashFlow);
                cashFlowMap.remove(entry.getKey());
            }

        }
        //更新cash flow
        List<Long> deleteIds = new ArrayList<>(cashFlowMap.keySet());
        if (deleteIds.size() > 0) {
            fundReceiptRepayCashFlowService.remove(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                    .in(FundReceiptRepayCashFlow::getCashFlowId, deleteIds));
        }
        if (updateList.size() > 0) {
            fundReceiptRepayCashFlowService.updateBatchById(updateList);
        }
        if (insertList.size() > 0) {
            fundReceiptRepayCashFlowService.saveBatch(insertList);
        }

        //更新资金流入
        FundReceiptRepayBorrowing borrowing = fundReceiptRepayBorrowingService.getOne(Wrappers.<FundReceiptRepayBorrowing>lambdaQuery()
                .eq(FundReceiptRepayBorrowing::getReceiptRepayId, receiptRepayBaseInfo.getId())
                .last("limit 1"));
        borrowing.setActualLoanDate(valueDate);
        borrowing.setPrincipal(financingBaseInfo.getFinancingAmount() * 10000L);
        fundReceiptRepayBorrowingService.updateById(borrowing);
        //更新费用明细
        //1 查询收付款的费用明细
        Map<Long, FundReceiptRepayExpense> expenseMap = fundReceiptRepayExpenseService.list(
                        Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                                .eq(FundReceiptRepayExpense::getReceiptRepayId, receiptRepayBaseInfo.getId())).stream()
                .collect(Collectors.toMap(FundReceiptRepayExpense::getDirectFeeId, v -> v, (k1, k2) -> k1));
        //2 查询融资的费用明细
        Map<Long, FundDirectFinancingFeeDetail> feeMap = directFinancingFeeDetailService.list(
                        Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                                .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId)).stream()
                .collect(Collectors.toMap(FundDirectFinancingFeeDetail::getId, v -> v, (k1, k2) -> k1));
        //3 更新收付款的费用明细
        List<FundReceiptRepayExpense> feeUpdateList = new ArrayList<>();
        List<FundReceiptRepayExpense> feeInsertList = new ArrayList<>();

        Iterator<Map.Entry<Long, FundDirectFinancingFeeDetail>> feeIterator = feeMap.entrySet()
                .stream().sorted(Map.Entry.comparingByKey()).iterator();
        int count = fundReceiptRepayExpenseService.count(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                .eq(FundReceiptRepayExpense::getReceiptRepayId, receiptRepayBaseInfo.getId()));
        while (feeIterator.hasNext()) {
            Map.Entry<Long, FundDirectFinancingFeeDetail> entry = feeIterator.next();
            FundReceiptRepayExpense expense = expenseMap.get(entry.getKey());
            FundDirectFinancingFeeDetail fee = entry.getValue();
            if (expense == null) {
                //新增
                FundReceiptRepayExpense expenseToAdd = new FundReceiptRepayExpense();
                expenseToAdd.setReceiptRepayId(receiptRepayBaseInfo.getId());
                expenseToAdd.setExpenseType(fee.getExpenseType());
                expenseToAdd.setTotalAmount(fee.getAmount());
                expenseToAdd.setWriteOffState(CashFlowState.NO_WRITE_OFF.name());
                expenseToAdd.setTotalPaidAmount(0L);
                expenseToAdd.setDirectFeeId(fee.getId());
                expenseToAdd.setCashFlowCode(financingBaseInfo.getFinancingCode() + "A" + (count + 1));
                count++;
                feeInsertList.add(expenseToAdd);
            } else {
                BeanUtil.copyProperties(fee, expense, CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties("id", "receiptRepayId", "directFeeId", "writeOffState"));
                expense.setTotalAmount(fee.getAmount());
                feeUpdateList.add(expense);
                expenseMap.remove(entry.getKey());
            }
        }
        //更新
        List<Long> feeDeleteIds = new ArrayList<>(expenseMap.keySet());
        if (feeDeleteIds.size() > 0) {
            fundReceiptRepayExpenseService.remove(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                    .in(FundReceiptRepayExpense::getDirectFeeId, feeDeleteIds));
        }
        if (feeUpdateList.size() > 0) {
            fundReceiptRepayExpenseService.updateBatchById(feeUpdateList);
        }
        if (feeInsertList.size() > 0) {
            fundReceiptRepayExpenseService.saveBatch(feeInsertList);
        }
        return receiptRepayBaseInfo.getId();
    }

    public Map<String, DiffValue> directCompare(Long id) {
        FundDirectFinancingBaseInfoDetailRSP financingBaseInfoDetailRSP = directDetail(id);
        FundReceiptRepayBaseInfoLib lastestLib = fundReceiptRepayBaseInfoLibService.getLastestDirectLib(id);

        Map<String, DiffValue> diffValueMap = new HashMap<>();
        Field[] fields = ReflectUtil.getFields(FundDirectFinancingBaseInfoDetailRSP.class);
        for (Field field : fields) {
            if ("remark".equals(field.getName())) {
                if (lastestLib != null) {
                    continue;
                }
            }
            Object fieldValue = ReflectUtil.getFieldValue(financingBaseInfoDetailRSP, field);
            DiffValue diffValue = new DiffValue();
            diffValue.setValue(fieldValue);
            diffValue.setBeforeValue(fieldValue);
            diffValue.setIsChange(false);
            diffValueMap.put(field.getName(), diffValue);
        }
        if (!diffValueMap.containsKey("remark")) {
            DiffValue diffValue = new DiffValue();
            diffValue.setValue(financingBaseInfoDetailRSP.getRemark());
            diffValue.setBeforeValue(lastestLib.getRemark());
            diffValue.setIsChange(Objects.equals(financingBaseInfoDetailRSP.getRemark(), lastestLib.getRemark()));
            diffValueMap.put("remark", diffValue);
        }
        return diffValueMap;
    }


    /**
     * 获取剩余本金
     *
     * @param financingIdList
     * @param financingTypeEnum
     * @return
     */
    public Map<Long, Long> queryRemainingAmount(Collection<Long> financingIdList, FinancingTypeEnum financingTypeEnum) {
        if (CollectionUtil.isEmpty(financingIdList)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<FundReceiptRepayBaseInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(FundReceiptRepayBaseInfo::getId);
        queryWrapper.in(FundReceiptRepayBaseInfo::getFinancingId, financingIdList);
        switch (financingTypeEnum) {
            case DIRECT:
                queryWrapper.eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name());
                break;
            case INDIRECT:
                queryWrapper.isNull(FundReceiptRepayBaseInfo::getFinancingType);
                break;
            default:
                break;
        }
        // 根据financingType过滤
        List<Long> receiptRepayIdList = this.list(queryWrapper)
                .stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(receiptRepayIdList)) {
            return Collections.emptyMap();
        }

        // 还款计划提供应还本金之和，已核销金额取实际核销表
        List<FundReceiptRepayCashFlow> cashFlowList = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayIdList)
                .ne(FundReceiptRepayCashFlow::getPhase, 0));

        if (CollectionUtil.isNotEmpty(cashFlowList)) {
            // 处理实际核销部分
            Map<String, List<FundReceiptFlowDetail>> flowDetailMap = Optional.ofNullable(fundReceiptFlowDetailService.listByCashFlowCodes(
                    cashFlowList.stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList())
            )).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
            for (FundReceiptRepayCashFlow repayCashFlow : cashFlowList) {
                List<FundReceiptFlowDetail> flowDetailList = flowDetailMap.get(repayCashFlow.getCashFlowCode());
                if (CollectionUtil.isEmpty(flowDetailList)) {
                    continue;
                }
                // 调整这笔现金流的剩余本金 = 应还本金 - 已核销本金
                long alreadyWriteOffAmount = flowDetailList.stream().filter(f -> Objects.nonNull(f.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum();
                repayCashFlow.setPrincipleAmount(Optional.ofNullable(repayCashFlow.getPrincipleAmount()).orElse(0L) - alreadyWriteOffAmount);
            }
            return cashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId,
                    Collectors.summingLong(e -> Objects.isNull(e.getPrincipleAmount()) ? 0 : e.getPrincipleAmount())));
        }

        return new HashMap<>();
    }


    public Long calculateFundContractRate(ComprehensiveFinancingCostBO financingCostBO) {
        Long financingId = financingCostBO.getFinancingId();
        FundFinancingBaseInfo financingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.INDIRECT.name()) ? fundFinancingBaseInfoService.getById(financingId) : new FundFinancingBaseInfo();
        FundDirectFinancingBaseInfo directFinancingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name()) ? fundDirectFinancingBaseInfoService.getById(financingId) : new FundDirectFinancingBaseInfo();
        if (financingBaseInfo == null && directFinancingBaseInfo == null) {
            return 0L;
        }
        // 同业借款  ①规律即借款日还租日均统一，合同利率=irr  ②不规律借款日还租日不统一，合同利率=按月折现计算的irr
        if (financingBaseInfo != null && Objects.equals(financingCostBO.getOrganizationType(), OrganizationType.ZL.name())) {
            List<FundFinancingRepayActual> repayActualList = fundFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingBaseInfo.getId());
            if (CollectionUtil.isEmpty(repayActualList)) {
                // 若不存在实际租金表 寻找是否存在概算租金表
                List<FundFinancingRepayEstimate> fundFinancingRepayEstimateList = financingRepayEstimateService.listByFinancingId(financingId);
                if (CollectionUtil.isEmpty(fundFinancingRepayEstimateList)) {
                    return 0L;
                } else {
                    repayActualList = fundFinancingRepayEstimateList.stream().filter(f -> f.getPhase() != 0)
                            .map(item -> BeanUtil.copyProperties(item, FundFinancingRepayActual.class)).collect(Collectors.toList());
                }
            }
            List<CashFlowBO> cashFlowBOList = repayActualList.stream().map(this::toCashFlowBO).collect(Collectors.toList());
            FundFinancingPlan fundFinancingPlan = fundFinancingPlanService.getOneByFinancingId(financingBaseInfo.getId());
            Assert.isTrue(Objects.nonNull(fundFinancingPlan.getFinancingMonth()) && Objects.nonNull(fundFinancingPlan.getRepayFrequency()), () -> MithrasException.newException("需补充融资期限及还款频率"));
            RepayRateEnum repayRateEnum = RepayRateEnum.of(fundFinancingPlan.getRepayFrequency());
            // 补充第0期
            List<CashFlowBO> cashFlowBOListNew = new ArrayList<>();
            CashFlowBO cashFlowBOZero = new CashFlowBO();
            cashFlowBOZero.setCashFlowPhase(0);
            cashFlowBOZero.setCashFlowDate(Optional.ofNullable(financingBaseInfo.getActualLoanDate()).orElse(financingBaseInfo.getPlanLoanDate()));
            cashFlowBOZero.setRemainingPrincipal(fundFinancingPlan.getFinancingAmount());
            cashFlowBOZero.setCashFlowAmount(-1 * fundFinancingPlan.getFinancingAmount());
            cashFlowBOListNew.add(cashFlowBOZero);
            cashFlowBOListNew.addAll(cashFlowBOList);
            //添加费用项金额
            List<CashFlowBO> feeList = this.getFeeList(financingCostBO);
            if (CollectionUtil.isNotEmpty(feeList)) {
                cashFlowBOListNew.addAll(feeList);
            }
            CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(fundFinancingPlan.getFinancingMonth(), repayRateEnum, cashFlowBOListNew);
            return cashFlowIRRBO.getIrr().multiply(new BigDecimal("1000000")).longValue();
        }

        // other 合同利率 = 融资费用按还本计划分摊到每笔费用支付日后每期的折算率（费用项未核销时取计划支付日期，全部核销后取实际支付日期）
        // 综合融资成本 = 保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）+ 担保比例*担保费率
        if (Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
            List<FundDirectFinancingFeeDetail> feeDetailList = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                    .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
            List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList = directFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
            if (CollectionUtil.isEmpty(feeDetailList) || CollectionUtil.isEmpty(fundDirectFinancingRepayActualList)) {
                return Optional.ofNullable(directFinancingBaseInfo.getAverageCouponRate()).orElse(0L);
            }
            for (FundDirectFinancingFeeDetail feeDetail : feeDetailList) {
                Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
                Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
            }
            Assert.notNull(directFinancingBaseInfo.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
            Assert.notNull(directFinancingBaseInfo.getRepayFrequency(), () -> MithrasException.newException("还款频率为空"));
            Assert.notNull(directFinancingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("起息日为空"));

            if(Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(directFinancingBaseInfo.getDirectFinancingType()) && fundDirectFinancingRepayActualList.size()>1) {
                long principleSum = fundDirectFinancingRepayActualList.stream().filter(f -> f.getPrincipleAmount() != null).mapToLong(FundDirectFinancingRepayActual::getPrincipleAmount).sum();
                long principleAll = directFinancingBaseInfo.getFinancingAmount() * 10000L;
                if (principleSum != principleAll) {
                    //末期（最后一期）的本金，一定等于倒数第二期的未还本金
                    FundDirectFinancingRepayActual lastRepayActual = fundDirectFinancingRepayActualList.get(fundDirectFinancingRepayActualList.size() - 1);
                    FundDirectFinancingRepayActual lastSecondRepayActual = fundDirectFinancingRepayActualList.get(fundDirectFinancingRepayActualList.size() - 2);
                    if (!lastRepayActual.getPrincipleAmount().equals(lastSecondRepayActual.getRemainingPrincipleAmount())) {
                        lastRepayActual.setPrincipleAmount(lastSecondRepayActual.getRemainingPrincipleAmount());
                        directFinancingRepayActualService.updateById(lastRepayActual);
                    }
                }
            }
            DailyDiscountRateCalcResultBO rateCalcResult = calculateConvertDirectNew(fundDirectFinancingRepayActualList, feeDetailList, directFinancingBaseInfo.getCarryInterestTime());
            return rateCalcResult.getDailyDiscountRate().multiply(new BigDecimal("360000000")).longValue();

        } else {
            FundFinancingPlan financingPlan = fundFinancingPlanService.getOneByFinancingId(financingId);
            BigDecimal earnestFormula = BigDecimal.ZERO;
            if (financingPlan.getEarnestMoneyAmount() != null) {
                // 融资机构存款利率
                BigDecimal depositRate = BigDecimal.ZERO;
                List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
                if (CollectionUtil.isNotEmpty(organizationList)) {
                    long depositRateSum = organizationList.stream().filter(f -> f.getCurrentDepositRate() != null).mapToLong(FundOrganization::getCurrentDepositRate).sum();
                    depositRate = BigDecimal.valueOf(depositRateSum).divide(BigDecimal.valueOf(organizationList.size()), 10, RoundingMode.HALF_UP);
                }
                // 保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）
                Assert.notNull(financingPlan.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
                BigDecimal financingLimit = BigDecimal.valueOf(financingPlan.getFinancingMonth()).divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
                BigDecimal a = BigDecimal.valueOf(financingPlan.getEarnestMoneyAmount()).divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 10, RoundingMode.HALF_UP)
                        .divide(financingLimit, 10, RoundingMode.HALF_UP);
                BigDecimal b = new BigDecimal("0.015").subtract(depositRate.divide(new BigDecimal("1000000"), 10, BigDecimal.ROUND_HALF_UP));
                earnestFormula = a.multiply(b).multiply(new BigDecimal("1000000"));
            }

            List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                    .eq(FundFinancingFeeDetail::getFinancingId, financingId));
            List<FundFinancingRepayActual> fundFinancingRepayActualList = fundFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
            if (CollectionUtil.isEmpty(feeDetailList)) {
                return earnestFormula.add(Optional.ofNullable(financingPlan.getLprRatePercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO).add(Optional.ofNullable(financingPlan.getLprAddPercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO))).longValue();
            }
            Assert.notNull(financingPlan.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
            Assert.notNull(financingPlan.getRepayFrequency(), () -> MithrasException.newException("还款频率为空"));
            if (CollectionUtil.isEmpty(fundFinancingRepayActualList)) {
                // 若不存在实际租金表 寻找是否存在概算租金表
                List<FundFinancingRepayEstimate> fundFinancingRepayEstimateList = financingRepayEstimateService.listByFinancingId(financingId);
                if (CollectionUtil.isEmpty(fundFinancingRepayEstimateList)) {
                    return earnestFormula.longValue();
                } else {
                    Assert.notNull(financingBaseInfo.getPlanLoanDate(), () -> MithrasException.newException("计划贷款时间为空"));
                    fundFinancingRepayActualList = fundFinancingRepayEstimateList.stream().filter(f -> f.getPhase() != 0)
                            .map(item -> BeanUtil.copyProperties(item, FundFinancingRepayActual.class)).collect(Collectors.toList());
                }
            } else {
                Assert.notNull(financingBaseInfo.getActualLoanDate(), () -> MithrasException.newException("实际贷款日期为空"));
            }
            for (FundFinancingFeeDetail feeDetail : feeDetailList) {
                Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
                Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
            }

            DailyDiscountRateCalcResultBO rateCalcResult = calculateConvertNew(fundFinancingRepayActualList, feeDetailList, Optional.ofNullable(financingBaseInfo.getActualLoanDate()).orElse(financingBaseInfo.getPlanLoanDate()));
            return earnestFormula.add(rateCalcResult.getDailyDiscountRate().multiply(new BigDecimal("360000000"))).longValue();
        }

//    原逻辑，留存
//    public Long calculateFundContractRateOld2(ComprehensiveFinancingCostBO financingCostBO) {
//        Long financingId = financingCostBO.getFinancingId();
//        FundFinancingBaseInfo financingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.INDIRECT.name()) ? fundFinancingBaseInfoService.getById(financingId) : new FundFinancingBaseInfo();
//        FundDirectFinancingBaseInfo directFinancingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name()) ? fundDirectFinancingBaseInfoService.getById(financingId) : new FundDirectFinancingBaseInfo();
//        if (financingBaseInfo == null && directFinancingBaseInfo == null) {
//            return 0L;
//        }
//        // 同业借款  ①规律即借款日还租日均统一，合同利率=irr  ②不规律借款日还租日不统一，合同利率=按月折现计算的irr
//        if (financingBaseInfo != null && Objects.equals(financingCostBO.getOrganizationType(), OrganizationType.ZL.name())) {
//            List<FundFinancingRepayActual> repayActualList = fundFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingBaseInfo.getId());
//            if(CollectionUtil.isEmpty(repayActualList)){
//                // 若不存在实际租金表 寻找是否存在概算租金表
//                List<FundFinancingRepayEstimate> fundFinancingRepayEstimateList = financingRepayEstimateService.listByFinancingId(financingId);
//                if(CollectionUtil.isEmpty(fundFinancingRepayEstimateList)){
//                    return 0L;
//                }else{
//                    repayActualList = fundFinancingRepayEstimateList.stream().filter(f -> f.getPhase() != 0)
//                            .map(item -> BeanUtil.copyProperties(item, FundFinancingRepayActual.class)).collect(Collectors.toList());
//                }
//            }
//            List<CashFlowBO> cashFlowBOList = repayActualList.stream().map(this::toCashFlowBO).collect(Collectors.toList());
//            FundFinancingPlan fundFinancingPlan = fundFinancingPlanService.getOneByFinancingId(financingBaseInfo.getId());
//            Assert.isTrue(Objects.nonNull(fundFinancingPlan.getFinancingMonth()) && Objects.nonNull(fundFinancingPlan.getRepayFrequency()), () -> MithrasException.newException("需补充融资期限及还款频率"));
//            RepayRateEnum repayRateEnum = RepayRateEnum.of(fundFinancingPlan.getRepayFrequency());
//            // 补充第0期
//            List<CashFlowBO> cashFlowBOListNew = new ArrayList<>();
//            CashFlowBO cashFlowBOZero = new CashFlowBO();
//            cashFlowBOZero.setCashFlowPhase(0);
//            cashFlowBOZero.setCashFlowDate(financingBaseInfo.getActualLoanDate());
//            cashFlowBOZero.setRemainingPrincipal(fundFinancingPlan.getFinancingAmount());
//            cashFlowBOZero.setCashFlowAmount(-1 * fundFinancingPlan.getFinancingAmount());
//            cashFlowBOListNew.add(cashFlowBOZero);
//            cashFlowBOListNew.addAll(cashFlowBOList);
//            CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(fundFinancingPlan.getFinancingMonth(), repayRateEnum, cashFlowBOListNew);
//            return cashFlowIRRBO.getIrr().multiply(new BigDecimal("1000000")).longValue();
//        }
//
//        // other 合同利率 = 融资费用按还本计划分摊到每笔费用支付日后每期的折算率（费用项未核销时取计划支付日期，全部核销后取实际支付日期）
//        // 综合融资成本 = 保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）+ 担保比例*担保费率
//        if (Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
//            List<FundDirectFinancingFeeDetail> feeDetailList = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
//                    .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
//            List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList = directFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
//            if(CollectionUtil.isEmpty(fundDirectFinancingRepayActualList)){
//                return BigDecimal.ZERO.longValue();
//            }
//            if(CollectionUtil.isEmpty(feeDetailList)){
//                return Optional.ofNullable(directFinancingBaseInfo.getComprehensiveFinancingCost()).orElse(0L);
//            }
//            for (FundDirectFinancingFeeDetail feeDetail : feeDetailList) {
//                Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
//                Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
//            }
//            Assert.notNull(directFinancingBaseInfo.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
//            Assert.notNull(directFinancingBaseInfo.getRepayFrequency(), () -> MithrasException.newException("还款频率为空"));
//            Assert.notNull(directFinancingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("起息日为空"));
//            // 找出每笔费用项需要分摊到哪些现金流上, 取最小期项
//            Map<Long, Integer> feeCashFlowIdMap = feeDetailList.stream().collect(Collectors.toMap(FundDirectFinancingFeeDetail::getId,
//                            feePay -> fundDirectFinancingRepayActualList.stream()
//                                    .filter(item -> !feePay.getPayDate().isAfter(item.getRepayDate()))
//                                    .map(FundDirectFinancingRepayActual::getPhase).collect(Collectors.toList()))).entrySet().stream()
//                    .filter(f -> CollectionUtil.isNotEmpty(f.getValue()))
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            entry -> entry.getValue().stream().sorted().findFirst().get()
//                    ));
//
//            Map<Integer, Long> phaseAmountSumMap = calculatePhaseRateDirect(fundDirectFinancingRepayActualList);
//
//            Map<Integer, Long> newFeeCashFlow = fundDirectFinancingRepayActualList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActual::getPhase, item -> {
//                // 该期现金流需要分摊的费用项
//                if(item.getPrincipleAmount() == null || item.getPrincipleAmount() == 0L){
//                    return 0L;
//                }
//                List<FundDirectFinancingFeeDetail> feeList = feeDetailList.stream().filter(f -> !f.getPayDate().isAfter(item.getRepayDate())).collect(Collectors.toList());
//                BigDecimal feeCashFlowAmount = BigDecimal.ZERO;
//                for (FundDirectFinancingFeeDetail feeDetail : feeList) {
//                    // 最小期项
//                    Integer minPhase = feeCashFlowIdMap.get(feeDetail.getId());
//                    Long amountSum = phaseAmountSumMap.get(minPhase);
//
//                    // 获取该费用项在此现金流中需要分摊的权重
//                    BigDecimal rate = BigDecimal.valueOf(item.getPrincipleAmount()).divide(BigDecimal.valueOf(amountSum), 10, RoundingMode.HALF_UP);
//                    feeCashFlowAmount = feeCashFlowAmount.add(BigDecimal.valueOf(feeDetail.getAmount()).multiply(rate));
//                }
//                return feeCashFlowAmount.longValue();
//            }));
//
//            // 兜底逻辑：若有费用项没有摊到任何一期现金流中 自成一期现金流
//            List<Long> remainingFeeId = feeDetailList.stream().map(FundDirectFinancingFeeDetail::getId).collect(Collectors.toList());
//            remainingFeeId.removeAll(feeCashFlowIdMap.keySet());
//            Map<LocalDate, Long> remainingFeeMap = null;
//            if (CollectionUtil.isNotEmpty(remainingFeeId)) {
//                Map<Long, FundDirectFinancingFeeDetail> feeDetailMap = feeDetailList.stream().collect(Collectors.toMap(FundDirectFinancingFeeDetail::getId, Function.identity()));
//                remainingFeeMap = remainingFeeId.stream().map(feeDetailMap::get).collect(Collectors.toMap(FundDirectFinancingFeeDetail::getPayDate, FundDirectFinancingFeeDetail::getAmount));
//            }
//            DailyDiscountRateCalcResultBO rateCalcResult = calculateConvertDirect(fundDirectFinancingRepayActualList, newFeeCashFlow, remainingFeeMap,
//                    directFinancingBaseInfo.getRepayFrequency(), directFinancingBaseInfo.getFinancingMonth(),directFinancingBaseInfo.getCarryInterestTime());
//            return rateCalcResult.getDailyDiscountRate().multiply(new BigDecimal("360000000")).longValue();
//
//        } else {
//            FundFinancingPlan financingPlan = fundFinancingPlanService.getOneByFinancingId(financingId);
//            BigDecimal earnestFormula = BigDecimal.ZERO;
//            if(financingPlan.getEarnestMoneyAmount() != null){
//                // 融资机构存款利率
//                BigDecimal depositRate = BigDecimal.ZERO;
//                List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
//                if(CollectionUtil.isNotEmpty(organizationList)) {
//                    long depositRateSum = organizationList.stream().filter(f -> f.getCurrentDepositRate() != null).mapToLong(FundOrganization::getCurrentDepositRate).sum();
//                    depositRate = BigDecimal.valueOf(depositRateSum).divide(BigDecimal.valueOf(organizationList.size()), 10, RoundingMode.HALF_UP);
//                }
//                // 保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）
//                Assert.notNull(financingPlan.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
//                BigDecimal financingLimit = BigDecimal.valueOf(financingPlan.getFinancingMonth()).divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
//                BigDecimal a = BigDecimal.valueOf(financingPlan.getEarnestMoneyAmount()).divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 10, RoundingMode.HALF_UP)
//                        .divide(financingLimit, 10, RoundingMode.HALF_UP);
//                BigDecimal b = new BigDecimal("0.015").subtract(depositRate.divide(new BigDecimal("1000000"), 10 ,BigDecimal.ROUND_HALF_UP));
//                earnestFormula = a.multiply(b).multiply(new BigDecimal("1000000"));
//            }
//
//            List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
//                    .eq(FundFinancingFeeDetail::getFinancingId, financingId));
//            List<FundFinancingRepayActual> fundFinancingRepayActualList = fundFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
//            if (CollectionUtil.isEmpty(feeDetailList)) {
//                return earnestFormula.add(Optional.ofNullable(financingPlan.getLprRatePercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO).add(Optional.ofNullable(financingPlan.getLprAddPercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO))).longValue();
//            }
//            Assert.notNull(financingPlan.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
//            Assert.notNull(financingPlan.getRepayFrequency(), () -> MithrasException.newException("还款频率为空"));
//            if(CollectionUtil.isEmpty(fundFinancingRepayActualList)){
//                // 若不存在实际租金表 寻找是否存在概算租金表
//                List<FundFinancingRepayEstimate> fundFinancingRepayEstimateList = financingRepayEstimateService.listByFinancingId(financingId);
//                if(CollectionUtil.isEmpty(fundFinancingRepayEstimateList)){
//                    return BigDecimal.ZERO.longValue();
//                }else{
//                    Assert.notNull(financingBaseInfo.getPlanLoanDate(), () -> MithrasException.newException("计划贷款时间为空"));
//                    fundFinancingRepayActualList = fundFinancingRepayEstimateList.stream().filter(f -> f.getPhase() != 0)
//                            .map(item -> BeanUtil.copyProperties(item, FundFinancingRepayActual.class)).collect(Collectors.toList());
//                }
//            }else {
//                Assert.notNull(financingBaseInfo.getActualLoanDate(), () -> MithrasException.newException("实际贷款日期为空"));
//            }
//            for (FundFinancingFeeDetail feeDetail : feeDetailList) {
//                Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
//                Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
//            }
//            // 找出每笔费用项需要分摊到哪些现金流上, 取最小期项
//            List<FundFinancingRepayActual> finalFundFinancingRepayActualList = fundFinancingRepayActualList;
//            Map<Long, Integer> feeCashFlowCodeMap = feeDetailList.stream().collect(Collectors.toMap(FundFinancingFeeDetail::getId,
//                            feeDetail -> finalFundFinancingRepayActualList.stream()
//                                    .filter(item -> !feeDetail.getPayDate().isAfter(item.getRepayDate()))
//                                    .map(FundFinancingRepayActual::getPhase).collect(Collectors.toList()))).entrySet().stream()
//                    .filter(f -> CollectionUtil.isNotEmpty(f.getValue()))
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            entry -> entry.getValue().stream().sorted().findFirst().get()
//                    ));
//
//            Map<Integer, Long> phaseAmountSumMap = calculatePhaseRate(fundFinancingRepayActualList);
//
//            Map<Integer, Long> newFeeCashFlow = fundFinancingRepayActualList.stream().collect(Collectors.toMap(FundFinancingRepayActual::getPhase, item -> {
//                if(item.getPrincipleAmount() == null || item.getPrincipleAmount() == 0L){
//                    return 0L;
//                }
//                // 该期现金流需要分摊的费用项
//                List<FundFinancingFeeDetail> feeList = feeDetailList.stream().filter(f -> !f.getPayDate().isAfter(item.getRepayDate())).collect(Collectors.toList());
//                BigDecimal feeCashFlowAmount = BigDecimal.ZERO;
//                for (FundFinancingFeeDetail feeDetail : feeList) {
//                    // 最小期项
//                    Integer minPhase = feeCashFlowCodeMap.get(feeDetail.getId());
//                    Long amountSum = phaseAmountSumMap.get(minPhase);
//
//                    // 获取该费用项在此现金流中需要分摊的权重
//                    BigDecimal rate = BigDecimal.valueOf(item.getPrincipleAmount()).divide(BigDecimal.valueOf(amountSum), 10, RoundingMode.HALF_UP);
//                    feeCashFlowAmount = feeCashFlowAmount.add(BigDecimal.valueOf(feeDetail.getAmount()).multiply(rate));
//                }
//                return feeCashFlowAmount.longValue();
//            }));
//            // 兜底逻辑：若有费用项没有摊到任何一期现金流中 自成一期现金流
//            List<Long> remainingFeeId = feeDetailList.stream().map(FundFinancingFeeDetail::getId).collect(Collectors.toList());
//            remainingFeeId.removeAll(feeCashFlowCodeMap.keySet());
//            Map<LocalDate, Long> remainingFeeMap = null;
//            if (CollectionUtil.isNotEmpty(remainingFeeId)) {
//                Map<Long, FundFinancingFeeDetail> feeDetailMap = feeDetailList.stream().collect(Collectors.toMap(FundFinancingFeeDetail::getId, Function.identity()));
//                remainingFeeMap = remainingFeeId.stream().map(feeDetailMap::get).collect(Collectors.toMap(FundFinancingFeeDetail::getPayDate, FundFinancingFeeDetail::getAmount));
//            }
//            DailyDiscountRateCalcResultBO rateCalcResult = calculateConvert(fundFinancingRepayActualList, newFeeCashFlow, remainingFeeMap, financingPlan.getRepayFrequency(), financingPlan.getFinancingMonth(), Optional.ofNullable(financingBaseInfo.getActualLoanDate()).orElse(financingBaseInfo.getPlanLoanDate()));
//            return earnestFormula.add(rateCalcResult.getDailyDiscountRate().multiply(new BigDecimal("360000000"))).longValue();
//        }


        // 原逻辑，先留存
//        // 非同业借款 合同利率=票面加权平均利率（直融）/借款利率（间融）+融资费用/(1+i)ⁿ/融资金额/融资期限（年）+保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）
//        // i = 全部融资机构（机构类型=银行）的银行活期存款利率平均值 , n = 融资费用支付计划日距起息日的月份数/12
//        BigDecimal rateAverage = organizationService.currentDepositRateAverage();
//        if (Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
//            Assert.notNull(directFinancingBaseInfo.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
//            Assert.notNull(directFinancingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("起息日为空"));
//            Assert.notNull(directFinancingBaseInfo.getFinancingAmount(), () -> MithrasException.newException("融资金额为空"));
//            List<FundDirectFinancingFeeDetail> feeDetailList = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
//                    .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
//            BigDecimal averageCouponRate = Objects.nonNull(directFinancingBaseInfo.getAverageCouponRate()) ? BigDecimal.valueOf(directFinancingBaseInfo.getAverageCouponRate()) : BigDecimal.ZERO;
//            if (CollectionUtil.isEmpty(feeDetailList)) {
//                return averageCouponRate.longValue();
//            }
//            LocalDate carryInterestTime = directFinancingBaseInfo.getCarryInterestTime();
//            Long financingAmount = directFinancingBaseInfo.getFinancingAmount() * 10000;
//            BigDecimal financingLimit = BigDecimal.valueOf(directFinancingBaseInfo.getFinancingMonth()).divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
//            BigDecimal result = BigDecimal.ZERO;
//            for (FundDirectFinancingFeeDetail feeDetail : feeDetailList) {
//                Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
//                Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
//                // 融资费用支付计划日距起息日的天数/360
//                long gapDay = ChronoUnit.DAYS.between(carryInterestTime, feeDetail.getPayDate());
//                BigDecimal n = BigDecimal.valueOf(gapDay).divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);
//                // 融资费用/(1+i)ⁿ/融资金额/融资期限（年）
//                double pow = Math.pow(rateAverage.add(BigDecimal.ONE).doubleValue(), n.doubleValue());
//                BigDecimal resultSingle = BigDecimal.valueOf(feeDetail.getAmount() * 10000).divide(BigDecimal.valueOf(pow), 10, RoundingMode.HALF_UP)
//                        .divide(BigDecimal.valueOf(financingAmount), 10, RoundingMode.HALF_UP)
//                        .divide(financingLimit, 10, RoundingMode.HALF_UP);
//                result = result.add(resultSingle);
//            }
//            return averageCouponRate.add(result.multiply(new BigDecimal("1000000"))).longValue();
//

//        }else {
//            List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
//                    .eq(FundFinancingFeeDetail::getFinancingId, financingId));
//            FundFinancingPlan financingPlan = fundFinancingPlanService.getOneByFinancingId(financingId);
//            Assert.notNull(financingPlan.getLprRatePercent(), () -> MithrasException.newException("LPR利率为空"));
//            Assert.notNull(financingPlan.getFinancingMonth(), () -> MithrasException.newException("融资期限为空"));
//            Assert.notNull(financingPlan.getFinancingAmount(), () -> MithrasException.newException("融资金额为空"));
//
//            BigDecimal lpr = BigDecimal.valueOf(financingPlan.getLprRatePercent() + financingPlan.getLprAddPercent());
//            long financingAmount = financingPlan.getFinancingAmount();
//            // 融资期限（年）
//            BigDecimal financingLimit = BigDecimal.valueOf(financingPlan.getFinancingMonth()).divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
//
//            BigDecimal rateFormula = BigDecimal.ZERO;
//            if (CollectionUtil.isNotEmpty(feeDetailList)) {
//                LocalDate carryInterestTime = financingBaseInfo.getActualLoanDate();
//                BigDecimal n = BigDecimal.ZERO;
//                for (FundFinancingFeeDetail feeDetail : feeDetailList) {
//                    Assert.notNull(feeDetail.getPayDate(), () -> MithrasException.newException("费用项支付日为空"));
//                    Assert.notNull(feeDetail.getAmount(), () -> MithrasException.newException("费用项金额为空"));
//                    // 融资费用支付计划日距起息日的天数/360
//                    if(carryInterestTime != null) {
//                        long gapDay = ChronoUnit.DAYS.between(carryInterestTime, feeDetail.getPayDate());
//                        n = BigDecimal.valueOf(gapDay).divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);
//                    }
//                    // 融资费用/(1+i)ⁿ/融资金额/融资期限（年）
//                    double pow = Math.pow(rateAverage.add(BigDecimal.ONE).doubleValue(), n.doubleValue());
//                    BigDecimal result = BigDecimal.valueOf(feeDetail.getAmount()).divide(BigDecimal.valueOf(pow), 10, RoundingMode.HALF_UP)
//                            .divide(BigDecimal.valueOf(financingAmount), 10, RoundingMode.HALF_UP)
//                            .divide(financingLimit, 10, RoundingMode.HALF_UP);
//                    rateFormula = rateFormula.add(result.multiply(new BigDecimal("1000000")));
//                }
//            }
//
//            BigDecimal earnestFormula = BigDecimal.ZERO;
//            Long earnestMoneyAmount = financingPlan.getEarnestMoneyAmount();
//            if(earnestMoneyAmount != null){
//                // 融资机构存款利率
//                BigDecimal depositRate = BigDecimal.ZERO;
//                List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
//                if(CollectionUtil.isNotEmpty(organizationList)) {
//                    long depositRateSum = organizationList.stream().filter(f -> f.getCurrentDepositRate() != null).mapToLong(FundOrganization::getCurrentDepositRate).sum();
//                    depositRate = BigDecimal.valueOf(depositRateSum).divide(BigDecimal.valueOf(organizationList.size()), 10, RoundingMode.HALF_UP);
//                }
//                // 保证金/融资金额/融资期限（年）*（1.5%-融资机构存款利率）
//                BigDecimal a = BigDecimal.valueOf(earnestMoneyAmount).divide(BigDecimal.valueOf(financingAmount), 10, RoundingMode.HALF_UP)
//                        .divide(financingLimit, 10, RoundingMode.HALF_UP);
//                BigDecimal b = new BigDecimal("0.015").subtract(depositRate.divide(new BigDecimal("1000000"), 10 ,BigDecimal.ROUND_HALF_UP));
//                earnestFormula = a.multiply(b).multiply(new BigDecimal("1000000"));
//            }
//            return lpr.add(rateFormula).add(earnestFormula).longValue();
//
//        }

    }

//    @Deprecated
//    public Long calculateFundContractRateOld(ComprehensiveFinancingCostBO financingCostBO) {
//        Long financingId = financingCostBO.getFinancingId();
//        FundFinancingBaseInfo financingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.INDIRECT.name()) ? fundFinancingBaseInfoService.getById(financingId) : new FundFinancingBaseInfo();
//        FundDirectFinancingBaseInfo directFinancingBaseInfo = Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name()) ? fundDirectFinancingBaseInfoService.getById(financingId) : new FundDirectFinancingBaseInfo();
//
//        // 同业借款  ①规律即借款日还租日均统一，合同利率=irr  ②不规律借款日还租日不统一，合同利率=按月折现计算的irr
//        if (financingBaseInfo != null && Objects.equals(financingCostBO.getOrganizationType(), OrganizationType.ZL.name())) {
//            List<FundFinancingRepayActual> repayActualList = fundFinancingRepayActualService.listByFinancingId(financingBaseInfo.getId());
//            if (CollectionUtil.isEmpty(repayActualList)) {
//                return null;
//            }
//            List<CashFlowBO> cashFlowBOList = repayActualList.stream().map(this::toCashFlowBO).collect(Collectors.toList());
//            FundFinancingPlan fundFinancingPlan = fundFinancingPlanService.getOneByFinancingId(financingBaseInfo.getId());
//            RepayRateEnum repayRateEnum = RepayRateEnum.of(fundFinancingPlan.getRepayFrequency());
//            // 补充第0期
//            CashFlowBO cashFlowBOZero = new CashFlowBO();
//            cashFlowBOZero.setCashFlowPhase(0);
//            cashFlowBOZero.setCashFlowDate(financingBaseInfo.getActualLoanDate());
//            cashFlowBOZero.setRemainingPrincipal(fundFinancingPlan.getFinancingAmount());
//            cashFlowBOList.add(cashFlowBOZero);
//            CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(fundFinancingPlan.getFinancingMonth(), repayRateEnum, cashFlowBOList);
//            return cashFlowIRRBO.getIrr().longValue();
//        }
//
//        // ABS/ABN  合同利率=票面加权平均利率+发行费用总额/年限/融资金额
//        if (directFinancingBaseInfo != null && Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(directFinancingBaseInfo.getDirectFinancingType())) {
//            BigDecimal averageCouponRate = Objects.nonNull(directFinancingBaseInfo.getAverageCouponRate()) ? BigDecimal.valueOf(directFinancingBaseInfo.getAverageCouponRate()) : BigDecimal.ZERO;
//            List<FundDirectFinancingFeeDetail> feeDetailList = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery().eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
//            if (CollectionUtil.isEmpty(feeDetailList) || directFinancingBaseInfo.getFinancingMonth() == null) {
//                return averageCouponRate.longValue();
//            }
//            BigDecimal feeAmountSum = BigDecimal.valueOf(feeDetailList.stream().mapToLong(FundDirectFinancingFeeDetail::getAmount).sum());
//            BigDecimal yearLimit = BigDecimal.valueOf(directFinancingBaseInfo.getFinancingMonth()).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
//            BigDecimal financingAmount = BigDecimal.valueOf(directFinancingBaseInfo.getFinancingAmount());
//            return averageCouponRate.add(feeAmountSum.divide(yearLimit, 2, RoundingMode.HALF_UP).divide(financingAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("10000"))).longValue();
//        }
//
//        // other  合同利率=票面加权平均利率+融资费用按还本计划分摊到每笔费用支付日后每期的折算率（费用项未核销时取计划支付日期，全部核销后取实际支付日期）
//        if (Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
//            List<FundDirectFinancingFeeDetail> feeDetailList = directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
//                    .eq(FundDirectFinancingFeeDetail::getFinancingId, financingId));
//            BigDecimal averageCouponRate = Objects.nonNull(directFinancingBaseInfo.getAverageCouponRate()) ? BigDecimal.valueOf(directFinancingBaseInfo.getAverageCouponRate()) : BigDecimal.ZERO;
//            if (CollectionUtil.isEmpty(feeDetailList)) {
//                return averageCouponRate.longValue();
//            }
//            // 费用项未核销时取计划支付日期，全部核销后取实际支付日期
//            FundReceiptRepayBaseInfo repayBaseInfo = this.getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
//                    .eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name()));
//            List<FundReceiptRepayExpense> fundReceiptRepayExpenseList = fundReceiptRepayExpenseService.listByReceiptRepayId(repayBaseInfo.getId());
//            List<FundReceiptFlowDetail> flowDetailList = fundReceiptFlowDetailService.listByCashFlowCodes(fundReceiptRepayExpenseList.stream().map(FundReceiptRepayExpense::getCashFlowCode).collect(Collectors.toList()));
//            Map<String, LocalDate> actualPayDate = new HashMap<>();
//            if (CollectionUtil.isNotEmpty(flowDetailList)) {
//                // key: 费用项现金流编号 value : 实际支付时间
//                actualPayDate = flowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode,
//                                Collectors.maxBy(Comparator.comparing(FundReceiptFlowDetail::getCashFlowDate)))).entrySet().stream()
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                entry -> entry.getValue().map(FundReceiptFlowDetail::getCashFlowDate).orElse(null)
//                        ));
//            }
//            Map<Long, String> feeCodeByIdMap = fundReceiptRepayExpenseList.stream().collect(Collectors.toMap(FundReceiptRepayExpense::getDirectFeeId, FundReceiptRepayExpense::getCashFlowCode));
//
//            // 筛选时间
//            Map<String, LocalDate> finalActualPayDate = actualPayDate;
//            Map<String, LocalDate> feePayDateMap = feeDetailList.stream().collect(Collectors.toMap(item -> feeCodeByIdMap.get(item.getId()),
//                    item -> Optional.ofNullable(finalActualPayDate.get(feeCodeByIdMap.get(item.getId()))).orElse(item.getPayDate())));
//            List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList = directFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
//
//            Map<String, Long> feeDetailAmountMap = feeDetailList.stream().collect(Collectors.toMap(item -> feeCodeByIdMap.get(item.getId()), FundDirectFinancingFeeDetail::getAmount));
//            // 找出每笔费用项需要分摊到哪些现金流上, 取最小期项
//            Map<String, Integer> feeCashFlowCodeMap = feePayDateMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
//                            feePay -> fundDirectFinancingRepayActualList.stream()
//                                    .filter(item -> feePay.getValue().isBefore(item.getRepayDate()))
//                                    .map(FundDirectFinancingRepayActual::getPhase).collect(Collectors.toList()))).entrySet().stream()
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            entry -> entry.getValue().stream().sorted(Comparator.reverseOrder()).findFirst().get()
//                    ));
//
//            Map<Integer, Long> phaseAmountSumMap = calculatePhaseRateDirect(fundDirectFinancingRepayActualList);
//
//            Map<Integer, Long> newFeeCashFlow = fundDirectFinancingRepayActualList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActual::getPhase, item -> {
//                // 该期现金流需要分摊的费用项
//                List<String> feeCodeList = feePayDateMap.entrySet().stream().filter(f -> f.getValue().isBefore(item.getRepayDate())).map(Map.Entry::getKey).collect(Collectors.toList());
//                long feeCashFlowAmount = 0L;
//                for (String feeCode : feeCodeList) {
//                    // 最小期项
//                    Integer minPhase = feeCashFlowCodeMap.get(feeCode);
//                    Long amountSum = phaseAmountSumMap.get(minPhase);
//
//                    // 获取该费用项在此现金流中需要分摊的权重
//                    Long rate = item.getPrincipleAmount() / amountSum;
//                    Long feeAmount = feeDetailAmountMap.get(feeCode);
//                    feeCashFlowAmount += feeAmount * rate;
//                }
//                return feeCashFlowAmount;
//            }));
//            // 兜底逻辑：若有费用项没有摊到任何一期现金流中 自成一期现金流
//            Set<String> remainingFeeCode = feePayDateMap.keySet();
//            remainingFeeCode.removeAll(feeCashFlowCodeMap.keySet());
//            Map<LocalDate, Long> remainingFeeMap = null;
//            if (CollectionUtil.isNotEmpty(remainingFeeCode)) {
//                remainingFeeMap = remainingFeeCode.stream().collect(Collectors.toMap(feePayDateMap::get, feeDetailAmountMap::get));
//            }
//            CashFlowIRRBO cashFlowIrr = calculateConvertDirect(fundDirectFinancingRepayActualList, newFeeCashFlow, remainingFeeMap,
//                    directFinancingBaseInfo.getRepayFrequency(), directFinancingBaseInfo.getFinancingMonth(),directFinancingBaseInfo.getCarryInterestTime());
//            return averageCouponRate.add(cashFlowIrr.getIrr()).longValue();
//
//        } else {
//            FundFinancingPlan financingPlan = fundFinancingPlanService.getOneByFinancingId(financingId);
//            BigDecimal lpr = BigDecimal.valueOf(financingPlan.getLprRatePercent() + financingPlan.getLprAddPercent());
//
//            List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
//                    .eq(FundFinancingFeeDetail::getFinancingId, financingId));
//            if (CollectionUtil.isNotEmpty(feeDetailList)) {
//                return lpr.longValue();
//            }
//            // 费用项未核销时取计划支付日期，全部核销后取实际支付日期
//            FundReceiptRepayBaseInfo repayBaseInfo = this.getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
//                    .eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.INDIRECT.name()));
//            List<FundReceiptRepayExpense> fundReceiptRepayExpenseList = fundReceiptRepayExpenseService.listByReceiptRepayId(repayBaseInfo.getId());
//            List<FundReceiptFlowDetail> flowDetailList = fundReceiptFlowDetailService.listByCashFlowCodes(fundReceiptRepayExpenseList.stream().map(FundReceiptRepayExpense::getCashFlowCode).collect(Collectors.toList()));
//            Map<String, LocalDate> actualPayDate = new HashMap<>();
//            if (CollectionUtil.isNotEmpty(flowDetailList)) {
//                // key: 费用项现金流编号 value : 实际支付时间
//                actualPayDate = flowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode,
//                                Collectors.maxBy(Comparator.comparing(FundReceiptFlowDetail::getCashFlowDate)))).entrySet().stream()
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                entry -> entry.getValue().map(FundReceiptFlowDetail::getCashFlowDate).orElse(null)
//                        ));
//            }
//            Map<Long, String> feeCodeByIdMap = fundReceiptRepayExpenseList.stream().collect(Collectors.toMap(FundReceiptRepayExpense::getFeeId, FundReceiptRepayExpense::getCashFlowCode));
//
//            // 筛选时间
//            Map<String, LocalDate> finalActualPayDate = actualPayDate;
//            Map<String, LocalDate> feePayDateMap = feeDetailList.stream().collect(Collectors.toMap(item -> feeCodeByIdMap.get(item.getId()),
//                    item -> Optional.ofNullable(finalActualPayDate.get(feeCodeByIdMap.get(item.getId()))).orElse(item.getPayDate())));
//            List<FundFinancingRepayActual> fundFinancingRepayActualList = fundFinancingRepayActualService.listByFinancingIdExcludeZeroPhase(financingId);
//
//            Map<String, Long> feeDetailAmountMap = feeDetailList.stream().collect(Collectors.toMap(item -> feeCodeByIdMap.get(item.getId()), FundFinancingFeeDetail::getAmount));
//            // 找出每笔费用项需要分摊到哪些现金流上, 取最小期项
//            Map<String, Integer> feeCashFlowCodeMap = feePayDateMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
//                            feePay -> fundFinancingRepayActualList.stream()
//                                    .filter(item -> feePay.getValue().isBefore(item.getRepayDate()))
//                                    .map(FundFinancingRepayActual::getPhase).collect(Collectors.toList()))).entrySet().stream()
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            entry -> entry.getValue().stream().sorted(Comparator.reverseOrder()).findFirst().get()
//                    ));
//
//            Map<Integer, Long> phaseAmountSumMap = calculatePhaseRate(fundFinancingRepayActualList);
//
//            Map<Integer, Long> newFeeCashFlow = fundFinancingRepayActualList.stream().collect(Collectors.toMap(FundFinancingRepayActual::getPhase, item -> {
//                // 该期现金流需要分摊的费用项
//                List<String> feeCodeList = feePayDateMap.entrySet().stream().filter(f -> f.getValue().isBefore(item.getRepayDate())).map(Map.Entry::getKey).collect(Collectors.toList());
//                long feeCashFlowAmount = 0L;
//                for (String feeCode : feeCodeList) {
//                    // 最小期项
//                    Integer minPhase = feeCashFlowCodeMap.get(feeCode);
//                    Long amountSum = phaseAmountSumMap.get(minPhase);
//
//                    // 获取该费用项在此现金流中需要分摊的权重
//                    Long rate = item.getPrincipleAmount() / amountSum;
//                    Long feeAmount = feeDetailAmountMap.get(feeCode);
//                    feeCashFlowAmount += feeAmount * rate;
//                }
//                return feeCashFlowAmount;
//            }));
//            // 兜底逻辑：若有费用项没有摊到任何一期现金流中 自成一期现金流
//            Set<String> remainingFeeCode = feePayDateMap.keySet();
//            remainingFeeCode.removeAll(feeCashFlowCodeMap.keySet());
//            Map<LocalDate, Long> remainingFeeMap = null;
//            if (CollectionUtil.isNotEmpty(remainingFeeCode)) {
//                remainingFeeMap = remainingFeeCode.stream().collect(Collectors.toMap(feePayDateMap::get, feeDetailAmountMap::get));
//            }
//            CashFlowIRRBO cashFlowIrr = calculateConvert(fundFinancingRepayActualList, newFeeCashFlow, remainingFeeMap, financingPlan.getRepayFrequency(), financingPlan.getFinancingMonth(), financingBaseInfo.getActualLoanDate());
//            return lpr.add(cashFlowIrr.getIrr()).longValue();
//        }
//
//    }


    private DailyDiscountRateCalcResultBO calculateConvertDirectNew(List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList, List<FundDirectFinancingFeeDetail> feeDetailList, LocalDate zeroPhaseTime) {
        List<CashFlowBO> cashFlowBOList = new ArrayList<>();
        long financingAmount = fundDirectFinancingRepayActualList.stream().mapToLong(m -> Optional.ofNullable(m.getPrincipleAmount()).orElse(0L)).sum();
        CashFlowBO cashFlowBOZero = new CashFlowBO();
        cashFlowBOZero.setCashFlowPhase(0);
        cashFlowBOZero.setCashFlowDate(zeroPhaseTime);
        cashFlowBOZero.setCashFlowAmount(-1 * financingAmount);
        cashFlowBOZero.setRemainingPrincipal(financingAmount);
        cashFlowBOList.add(cashFlowBOZero);

        for (FundDirectFinancingRepayActual repayActual : fundDirectFinancingRepayActualList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            Integer phase = repayActual.getPhase();
            cashFlowBO.setCashFlowPhase(phase);
            cashFlowBO.setCashFlowDate(repayActual.getRepayDate());
            cashFlowBO.setCashFlowAmount(Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L));
            cashFlowBO.setRemainingPrincipal(repayActual.getRemainingPrincipleAmount());
            cashFlowBOList.add(cashFlowBO);
        }
        for (FundDirectFinancingFeeDetail feeDetail : feeDetailList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            cashFlowBO.setCashFlowDate(feeDetail.getPayDate());
            cashFlowBO.setCashFlowAmount(LongUtil.null2zero(feeDetail.getAmount()));
            cashFlowBOList.add(cashFlowBO);
        }
        return FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);

    }


    private DailyDiscountRateCalcResultBO calculateConvertNew(List<FundFinancingRepayActual> fundFinancingRepayActualList, List<FundFinancingFeeDetail> feeDetailList, LocalDate zeroPhaseTime) {
        List<CashFlowBO> cashFlowBOList = new ArrayList<>();
        long financingAmount = fundFinancingRepayActualList.stream().mapToLong(m -> Optional.ofNullable(m.getPrincipleAmount()).orElse(0L)).sum();
        CashFlowBO cashFlowBOZero = new CashFlowBO();
        cashFlowBOZero.setCashFlowPhase(0);
        cashFlowBOZero.setCashFlowDate(zeroPhaseTime);
        cashFlowBOZero.setCashFlowAmount(-1 * financingAmount);
        cashFlowBOZero.setRemainingPrincipal(financingAmount);
        cashFlowBOList.add(cashFlowBOZero);

        for (FundFinancingRepayActual repayActual : fundFinancingRepayActualList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            Integer phase = repayActual.getPhase();
            cashFlowBO.setCashFlowPhase(phase);
            cashFlowBO.setCashFlowDate(repayActual.getRepayDate());
            cashFlowBO.setCashFlowAmount(Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L));
            cashFlowBO.setRemainingPrincipal(repayActual.getRemainingPrincipleAmount());
            cashFlowBOList.add(cashFlowBO);
        }
        for (FundFinancingFeeDetail feeDetail : feeDetailList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            cashFlowBO.setCashFlowDate(feeDetail.getPayDate());
            cashFlowBO.setCashFlowAmount(Optional.ofNullable(feeDetail.getAmount()).orElse(0L));
            cashFlowBOList.add(cashFlowBO);
        }
        return FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);
    }


    /**
     * 直融
     *
     * @param fundDirectFinancingRepayActualList 还本计划现金流
     * @param newFeeCashFlow                     费用项需要摊在各期项现金流的金额
     * @param remainingFeeMap                    未摊在还本计划中的费用项
     * @param repayFrequency                     还款频率
     * @param financingMonth                     融资期限
     * @param zeroPhaseTime                      第0期的时间
     */
    private DailyDiscountRateCalcResultBO calculateConvertDirect(List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList, Map<Integer, Long> newFeeCashFlow,
                                                                 Map<LocalDate, Long> remainingFeeMap, String repayFrequency, Integer financingMonth, LocalDate zeroPhaseTime) {
        List<CashFlowBO> cashFlowBOList = new ArrayList<>();
        long financingAmount = fundDirectFinancingRepayActualList.stream().mapToLong(m -> Optional.ofNullable(m.getPrincipleAmount()).orElse(0L)).sum();
        CashFlowBO cashFlowBOZero = new CashFlowBO();
        cashFlowBOZero.setCashFlowPhase(0);
        cashFlowBOZero.setCashFlowDate(zeroPhaseTime);
        cashFlowBOZero.setCashFlowAmount(-1 * financingAmount);
        cashFlowBOZero.setRemainingPrincipal(financingAmount);
        cashFlowBOList.add(cashFlowBOZero);

        for (FundDirectFinancingRepayActual repayActual : fundDirectFinancingRepayActualList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            Integer phase = repayActual.getPhase();
            cashFlowBO.setCashFlowPhase(phase);
            cashFlowBO.setCashFlowDate(repayActual.getRepayDate());
            cashFlowBO.setCashFlowAmount(Optional.ofNullable(newFeeCashFlow.get(phase)).orElse(0L) + Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L));
            cashFlowBO.setRemainingPrincipal(repayActual.getRemainingPrincipleAmount());
            cashFlowBOList.add(cashFlowBO);
        }
        if (CollectionUtil.isNotEmpty(remainingFeeMap)) {
            int maxPhase = fundDirectFinancingRepayActualList.size();
            remainingFeeMap.forEach((cashFlowDate, cashFlowAmount) -> {
                CashFlowBO cashFlowBO = new CashFlowBO();
                Integer phase = maxPhase + 1;
                cashFlowBO.setCashFlowPhase(phase);
                cashFlowBO.setCashFlowDate(cashFlowDate);
                cashFlowBO.setCashFlowAmount(cashFlowAmount);
                cashFlowBOList.add(cashFlowBO);
            });

        }
//        return FinancialUtil.calculateIRR(financingMonth, RepayRateEnum.valueOf(repayFrequency), cashFlowBOList);
        return FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);

    }

    /**
     * 间融
     *
     * @param fundFinancingRepayActualList 还本计划现金流
     * @param newFeeCashFlow               费用项需要摊在各期项现金流的金额
     * @param remainingFeeMap              未摊在还本计划中的费用项
     * @param repayFrequency               还款频率
     * @param financingMonth               融资期限
     * @param zeroPhaseTime                第0期的时间
     * @return
     */
    private DailyDiscountRateCalcResultBO calculateConvert(List<FundFinancingRepayActual> fundFinancingRepayActualList, Map<Integer, Long> newFeeCashFlow,
                                                           Map<LocalDate, Long> remainingFeeMap, String repayFrequency, Integer financingMonth, LocalDate zeroPhaseTime) {
        List<CashFlowBO> cashFlowBOList = new ArrayList<>();
        long financingAmount = fundFinancingRepayActualList.stream().mapToLong(m -> Optional.ofNullable(m.getPrincipleAmount()).orElse(0L)).sum();
        CashFlowBO cashFlowBOZero = new CashFlowBO();
        cashFlowBOZero.setCashFlowPhase(0);
        cashFlowBOZero.setCashFlowDate(zeroPhaseTime);
        cashFlowBOZero.setCashFlowAmount(-1 * financingAmount);
        cashFlowBOZero.setRemainingPrincipal(financingAmount);
        cashFlowBOList.add(cashFlowBOZero);

        for (FundFinancingRepayActual repayActual : fundFinancingRepayActualList) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            Integer phase = repayActual.getPhase();
            cashFlowBO.setCashFlowPhase(phase);
            cashFlowBO.setCashFlowDate(repayActual.getRepayDate());
            cashFlowBO.setCashFlowAmount(Optional.ofNullable(newFeeCashFlow.get(phase)).orElse(0L) + Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L));
            cashFlowBO.setRemainingPrincipal(repayActual.getRemainingPrincipleAmount());
            cashFlowBOList.add(cashFlowBO);
        }
        if (CollectionUtil.isNotEmpty(remainingFeeMap)) {
            int maxPhase = fundFinancingRepayActualList.size();
            remainingFeeMap.forEach((cashFlowDate, cashFlowAmount) -> {
                CashFlowBO cashFlowBO = new CashFlowBO();
                Integer phase = maxPhase + 1;
                cashFlowBO.setCashFlowPhase(phase);
                cashFlowBO.setCashFlowDate(cashFlowDate);
                cashFlowBO.setCashFlowAmount(cashFlowAmount);
                cashFlowBOList.add(cashFlowBO);
            });

        }
//        return FinancialUtil.calculateIRR(financingMonth, RepayRateEnum.valueOf(repayFrequency), cashFlowBOList);
        return FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);
    }

    /**
     * 直融
     * 获取现金流期项金额的总和 ，使用该方法需要保证集合根据期项排序
     *
     * @param fundDirectFinancingRepayActualList
     * @return Map<期项, 金额>
     */
    private Map<Integer, Long> calculatePhaseRateDirect(List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList) {
        if (CollectionUtil.isEmpty(fundDirectFinancingRepayActualList)) {
            return Collections.emptyMap();
        }
        List<FundDirectFinancingRepayActual> collect = new ArrayList<>(fundDirectFinancingRepayActualList);
        Collections.reverse(collect);
        // 先获取各期项范围的金额总和
        Map<Integer, Long> phaseAmountSumMap = new HashMap<>();
        Long phaseAmountSum = 0L;
        for (FundDirectFinancingRepayActual repayActual : collect) {
            phaseAmountSum += Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L);
            phaseAmountSumMap.put(repayActual.getPhase(), phaseAmountSum);
        }
        return phaseAmountSumMap;

    }

    /**
     * 间融
     * 获取现金流期项金额的总和 ，使用该方法需要保证集合根据期项排序
     *
     * @param fundFinancingRepayActualList
     * @return Map<期项, 金额>
     */
    private Map<Integer, Long> calculatePhaseRate(List<FundFinancingRepayActual> fundFinancingRepayActualList) {
        if (CollectionUtil.isEmpty(fundFinancingRepayActualList)) {
            return Collections.emptyMap();
        }
        List<FundFinancingRepayActual> collect = new ArrayList<>(fundFinancingRepayActualList);
        Collections.reverse(collect);
        // 先获取各期项范围的金额总和
        Map<Integer, Long> phaseAmountSumMap = new HashMap<>();
        Long phaseAmountSum = 0L;
        for (FundFinancingRepayActual repayActual : collect) {
            phaseAmountSum += repayActual.getPrincipleAmount();
            phaseAmountSumMap.put(repayActual.getPhase(), phaseAmountSum);
        }
        return phaseAmountSumMap;

    }

    public FundReceiptRepayBaseInfoListSumRSP buildResult(PageR<FundReceiptRepayBaseInfoListRSP> data, List<FundReceiptRepayBaseInfoListRSP> list) {
        FundReceiptRepayBaseInfoListSumRSP resultRsp = new FundReceiptRepayBaseInfoListSumRSP();
        Long financingAmount = 0L, repayPrincipal = 0L, repayInterest = 0L, oneYearPrincipal = 0L, monthRepayAmount = 0L, monthRepayPrincipal = 0L, monthRepayInterest = 0L;
        for (FundReceiptRepayBaseInfoListRSP listRsp : list) {
            financingAmount += listRsp.getFinancingAmount() != null ? listRsp.getFinancingAmount() : 0L;
//            repayPrincipal += listRsp.getRepayPrincipal() != null ? listRsp.getRepayPrincipal() : 0L;
//            repayInterest += listRsp.getRepayInterest() != null ? listRsp.getRepayInterest() : 0L;
//            oneYearPrincipal += listRsp.getOneYearPrincipal() != null ? listRsp.getOneYearPrincipal() : 0L;
            monthRepayAmount += listRsp.getMonthRepayAmount() != null ? listRsp.getMonthRepayAmount() : 0L;
            monthRepayPrincipal += listRsp.getMonthRepayPrincipal() != null ? listRsp.getMonthRepayPrincipal() : 0L;
            monthRepayInterest += listRsp.getMonthRepayInterest() != null ? listRsp.getMonthRepayInterest() : 0L;
        }
        resultRsp.setFinancingAmount(financingAmount);
//        resultRsp.setRepayPrincipal(repayPrincipal);
//        resultRsp.setRepayInterest(repayInterest);
//        resultRsp.setOneYearPrincipal(oneYearPrincipal);
        resultRsp.setMonthRepayAmount(monthRepayAmount);
        resultRsp.setMonthRepayPrincipal(monthRepayPrincipal);
        resultRsp.setMonthRepayInterest(monthRepayInterest);
        resultRsp.setList(data);
        return resultRsp;
    }

    private List<CashFlowBO> getFeeList(ComprehensiveFinancingCostBO financingCostBO) {
        List<CashFlowBO> cashFlowBOS = null;
        if (Objects.equals(financingCostBO.getFinancingType(), FinancingTypeEnum.INDIRECT.name())) {
            // 费用项明细
            List<FundFinancingFeeDetail> fees = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                    .eq(FundFinancingFeeDetail::getFinancingId, financingCostBO.getFinancingId()));
            if (ObjectUtil.isNotEmpty(fees)) {
                cashFlowBOS = new ArrayList<>();
                for (FundFinancingFeeDetail e : fees) {
                    if (ObjectUtil.isEmpty(e.getPayDate()) || ObjectUtil.isEmpty(e.getAmount())) {
                        continue;
                    }
                    CashFlowBO cashFlowBO = new CashFlowBO();
                    cashFlowBO.setCashFlowPhase(0);
                    cashFlowBO.setCashFlowDate(e.getPayDate());
                    cashFlowBO.setCashFlowAmount(e.getAmount());
                    cashFlowBOS.add(cashFlowBO);
                }
            }
        } else {
            List<FundDirectFinancingFeeDetail> fees = fundDirectFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                    .eq(FundDirectFinancingFeeDetail::getFinancingId, financingCostBO.getFinancingId()));
            if (ObjectUtil.isNotEmpty(fees)) {
                cashFlowBOS = new ArrayList<>();
                for (FundDirectFinancingFeeDetail e : fees) {
                    if (ObjectUtil.isEmpty(e.getPayDate()) || ObjectUtil.isEmpty(e.getAmount())) {
                        continue;
                    }
                    CashFlowBO cashFlowBO = new CashFlowBO();
                    cashFlowBO.setCashFlowPhase(0);
                    cashFlowBO.setCashFlowDate(e.getPayDate());
                    cashFlowBO.setCashFlowAmount(e.getAmount());
                    cashFlowBOS.add(cashFlowBO);
                }
            }
        }
        return cashFlowBOS;
    }

    public List<FundReceiptFlowDetail> getFinancingRepayDetail(Long financingId, String financingType) {
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = this.baseMapper.selectOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                .eq(ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType, financingType)
                .isNull(!ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfo)) {
            return ListUtil.empty();
        }
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId());
        query.eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name());
        return fundReceiptFlowDetailService.list(query);
    }

    public Map<Long, List<FundReceiptFlowDetail>> getBatchFinancingRepayDetail (Set<Long> financingIds, String financingType, String cashFlowItem) {
        if (CollectionUtil.isEmpty(financingIds)) {
            return MapUtil.empty();
        }
        List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos = this.baseMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                .eq(ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType, financingType)
                .isNull(!ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType));
        if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> financingId2RepayId = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getFinancingId, FundReceiptRepayBaseInfo::getId, (a, b) -> b));
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList()));
        query.eq(FundReceiptFlowDetail::getCashFlowItem, cashFlowItem);
        Map<Long, List<FundReceiptFlowDetail>> repayId2Detail = fundReceiptFlowDetailService.list(query).stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getReceiptRepayId));
        Map<Long, List<FundReceiptFlowDetail>> map = new HashMap<>();
        financingIds.forEach(financingId -> {
            map.put(financingId, repayId2Detail.get(financingId2RepayId.get(financingId)));
        });
        return map;
    }

    private CashFlowBO toCashFlowBO(FundFinancingRepayActual repayActual) {
        CashFlowBO cashFlowBO = new CashFlowBO();
        cashFlowBO.setCashFlowDate(repayActual.getRepayDate());
        cashFlowBO.setCashFlowPhase(repayActual.getPhase());
        cashFlowBO.setCashFlowAmount(repayActual.getRepayAmount());
        cashFlowBO.setRent(repayActual.getPrincipleAmount());
        cashFlowBO.setInterest(repayActual.getInterestAmount());
        cashFlowBO.setPrincipal(repayActual.getRepayAmount());
        cashFlowBO.setRemainingPrincipal(repayActual.getRemainingPrincipleAmount());
        return cashFlowBO;
    }

    public List<FundReceiptFlowPlan> getFinancingRepayFlowPlan(Long financingId, String financingType) {
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = this.baseMapper.selectOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                .eq(ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType, financingType)
                .ne(!ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name()), FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfo)) {
            return ListUtil.empty();
        }
        return fundReceiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery()
                .eq(FundReceiptFlowPlan::getReceiptRepayId, fundReceiptRepayBaseInfo.getId()));
    }

    //获取区间内起息数据
    public List<FundReceiptRepayBaseInfo> listIntervalInterest(LocalDate paidInDateFrom, LocalDate paidInDateTo) {
        //直融
        List<FundDirectFinancingBaseInfo> directBaseInfoList = fundDirectFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .ge(ObjectUtil.isNotEmpty(paidInDateFrom), FundDirectFinancingBaseInfo::getCarryInterestTime, paidInDateFrom)
                .le(ObjectUtil.isNotEmpty(paidInDateTo), FundDirectFinancingBaseInfo::getCarryInterestTime, paidInDateTo));
        //间融
        List<FundFinancingBaseInfo> financingBaseInfoList = fundFinancingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .ge(ObjectUtil.isNotEmpty(paidInDateFrom), FundFinancingBaseInfo::getActualLoanDate, paidInDateFrom)
                .le(ObjectUtil.isNotEmpty(paidInDateTo), FundFinancingBaseInfo::getActualLoanDate, paidInDateTo));
        List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(directBaseInfoList)) {
            receiptRepayBaseInfos.addAll(this.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, directBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList()))
                    .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")));
        }

        if (ObjectUtil.isNotEmpty(financingBaseInfoList)) {
            receiptRepayBaseInfos.addAll(this.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList()))
                    .ne(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")));
        }
        return receiptRepayBaseInfos;
    }
}
