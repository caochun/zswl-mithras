package cn.zswltech.mithras.application.orchestration.fund.receiptrepay;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.enums.receiptrepay.DepositCashFlowType;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.fund.enums.receiptrepay.ReceiptRepayState;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayCashDepositService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.third.financialshare.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayStateCronService;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayAccountMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundRepayAccountMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.*;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description fund_receipt_repay_state
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayStateService implements FundReceiptRepayStateCronService {

    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;
    @Resource
    private FundReceiptRepayCashFlowMapper fundReceiptRepayCashFlowMapper;
    @Resource
    private FundReceiptRepayVersionService fundReceiptRepayVersionService;
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private FundReceiptRepayAccountMapper fundReceiptRepayAccountMapper;
    @Resource
    private FundRepayAccountMapper fundRepayAccountMapper;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    /**
     * 提交审批后处理状态
     * 1、付款：未核销 -> 核销中
     * 2、还款：未核销 && 还款时间 < 当月底 -> 核销中
     *
     * @param receiptIdList
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleSubmitState(List<Long> receiptIdList) {
        if (CollectionUtils.isEmpty(receiptIdList)) {
            return;
        }
        // 付款状态处理
        LambdaUpdateWrapper<FundReceiptRepayBaseInfo> receiptUpdateWrapper = new LambdaUpdateWrapper<>();
        receiptUpdateWrapper.eq(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.NO_WRITE_OFF.name());
        receiptUpdateWrapper.in(FundReceiptRepayBaseInfo::getId, receiptIdList);
        receiptUpdateWrapper.set(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.WRITE_OFF_ING.name());
        fundReceiptRepayBaseInfoMapper.update(null, receiptUpdateWrapper);
        // 还款状态处理 本月底前的未核销 都处理成核销中
        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LambdaUpdateWrapper<FundReceiptRepayCashFlow> repayUpdateWrapper = new LambdaUpdateWrapper<>();
        repayUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
        repayUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptIdList);
        repayUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay);
        repayUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
        fundReceiptRepayCashFlowMapper.update(null, repayUpdateWrapper);
    }

    /**
     * 审批结束处理状态
     *
     * @param receiptIdList
     * @param processPass
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleProcessEndState(List<Long> receiptIdList, boolean processPass) {
        if (CollectionUtils.isEmpty(receiptIdList)) {
            return;
        }
        if (processPass) {
            // 先更新还款 今天及之前的核销中都处理成核销完毕
//            LambdaUpdateWrapper<FundReceiptRepayCashFlow> repayUpdateWrapper = new LambdaUpdateWrapper<>();
//            repayUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
//            repayUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptIdList);
//            repayUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now());
//            repayUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name());
//            fundReceiptRepayCashFlowMapper.update(null, repayUpdateWrapper);
        } else {
            // 还款 核销中 处理成未核销 approvalPassDate为null的才处理
            LambdaUpdateWrapper<FundReceiptRepayCashFlow> repayUpdateWrapper = new LambdaUpdateWrapper<>();
            repayUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
            repayUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptIdList);
            repayUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
            repayUpdateWrapper.isNull(FundReceiptRepayCashFlow::getApprovalPassDate);
            fundReceiptRepayCashFlowMapper.update(null, repayUpdateWrapper);
        }

        calReceiptState(receiptIdList);
    }

    /**
     * 记录某期还款的审批通过时间点
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordApprovalPassDate(List<Long> receiptIdList) {
        LambdaUpdateWrapper<FundReceiptRepayCashFlow> approvalPassDateUpdateWrapper = new LambdaUpdateWrapper<>();
        approvalPassDateUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
        approvalPassDateUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptIdList);
        // 曾经有审批通过日期的数据不覆盖
        approvalPassDateUpdateWrapper.isNull(FundReceiptRepayCashFlow::getApprovalPassDate);
        approvalPassDateUpdateWrapper.set(FundReceiptRepayCashFlow::getApprovalPassDate, LocalDate.now());
        fundReceiptRepayCashFlowMapper.update(null, approvalPassDateUpdateWrapper);
    }

    /**
     * 审批结束处理状态
     * 1、付款 核销中数据变更 当月最新一次审批通过 && 当前时间 >= 当月最后一期还款时间 -> 核销完毕 ? 结清
     * 2、还款 核销中数据变更 当月最新一次审批通过 && 当前时间 >= 计划还款时间 -> 核销完毕
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleCronState() {
        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        Set<Long> recordSet = new HashSet<>();
        // 全量处理
        List<Long> receiptIdList = fundReceiptRepayBaseInfoMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().select(FundReceiptRepayBaseInfo::getId))
                .stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
        //先查询需要变更的数据
        List<FundReceiptRepayCashFlow> repayCashFlows = fundReceiptRepayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name())
                .le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now())
                .isNotNull(FundReceiptRepayCashFlow::getApprovalPassDate));
        if(repayCashFlows != null){
            recordSet.addAll(repayCashFlows.stream().map(FundReceiptRepayCashFlow::getReceiptRepayId).collect(Collectors.toSet()));
        }
        // 先更新还款 今天及之前审批通过 的核销中都处理成核销完毕
//        LambdaUpdateWrapper<FundReceiptRepayCashFlow> repayUpdateWrapper = new LambdaUpdateWrapper<>();
//        repayUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
//        repayUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now());
//        repayUpdateWrapper.isNotNull(FundReceiptRepayCashFlow::getApprovalPassDate);
//        repayUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name());
//        fundReceiptRepayCashFlowMapper.update(null, repayUpdateWrapper);

        // 再更新还款 付款在流程中的 本月底之前的变成核销中
        List<Long> inProcessReceiptIdList = receiptIdList.stream().filter(receiptId -> Objects.nonNull(fundReceiptRepayVersionService.findRelatedProcess(receiptId))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(inProcessReceiptIdList)) {
            recordSet.addAll(inProcessReceiptIdList);
            LambdaUpdateWrapper<FundReceiptRepayCashFlow> writeOffIngUpdateWrapper = new LambdaUpdateWrapper<>();
            writeOffIngUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
            writeOffIngUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, inProcessReceiptIdList);
            writeOffIngUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay);
            writeOffIngUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
            fundReceiptRepayCashFlowMapper.update(null, writeOffIngUpdateWrapper);
        }
        // 进行一次付款汇算
        calReceiptState(receiptIdList);
        //这里生成一个版本，记录最新信息
        recordSet.forEach(mainId -> {
            fundReceiptRepayVersionService.recordVersion(mainId, VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
        });
//        //同步财务系统
//        noticeCq(repayCashFlows);
    }

    /**
     * 融资变更 通知 状态变更
     *
     * @param receiptId
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleFinancingChange(Long receiptId) {
        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<FundReceiptRepayCashFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
        queryWrapper.le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now());
        queryWrapper.isNotNull(FundReceiptRepayCashFlow::getApprovalPassDate);
        List<FundReceiptRepayCashFlow> repayCashFlows = fundReceiptRepayCashFlowMapper.selectList(queryWrapper);

        // 先更新还款 今天及之前审批通过 的核销中都处理成核销完毕（可能更新了日期）
//        LambdaUpdateWrapper<FundReceiptRepayCashFlow> repayUpdateWrapper = new LambdaUpdateWrapper<>();
//        repayUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
//        repayUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now());
//        repayUpdateWrapper.isNotNull(FundReceiptRepayCashFlow::getApprovalPassDate);
//        repayUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name());
//        fundReceiptRepayCashFlowMapper.update(null, repayUpdateWrapper);

        // 今天之后的核销完毕 处理成 核销中
//        LambdaUpdateWrapper<FundReceiptRepayCashFlow> curMonthWriteOffIngUpdateWrapper = new LambdaUpdateWrapper<>();
//        curMonthWriteOffIngUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, ListUtil.toList(receiptId));
//        curMonthWriteOffIngUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name());
//        curMonthWriteOffIngUpdateWrapper.gt(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now());
//        curMonthWriteOffIngUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay);
//        curMonthWriteOffIngUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
//        fundReceiptRepayCashFlowMapper.update(null, curMonthWriteOffIngUpdateWrapper);

        if (Objects.nonNull(fundReceiptRepayVersionService.findRelatedProcess(receiptId))) {
            // 在审批流里 当月底之前的还款直接纳入审批
            LambdaUpdateWrapper<FundReceiptRepayCashFlow> writeOffIngUpdateWrapper = new LambdaUpdateWrapper<>();
            writeOffIngUpdateWrapper.eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
            writeOffIngUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, ListUtil.toList(receiptId));
            writeOffIngUpdateWrapper.le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay);
            writeOffIngUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITE_OFF_ING.name());
            fundReceiptRepayCashFlowMapper.update(null, writeOffIngUpdateWrapper);
        }
        // 超过当月底的还款置为未付款 （从流程中剔除？）
        LambdaUpdateWrapper<FundReceiptRepayCashFlow> noWriteOffUpdateWrapper = new LambdaUpdateWrapper<>();
        noWriteOffUpdateWrapper.in(FundReceiptRepayCashFlow::getReceiptRepayId, ListUtil.toList(receiptId));
        noWriteOffUpdateWrapper.gt(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay);
        noWriteOffUpdateWrapper.set(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
        noWriteOffUpdateWrapper.set(FundReceiptRepayCashFlow::getApprovalPassDate, null);
        fundReceiptRepayCashFlowMapper.update(null, noWriteOffUpdateWrapper);

        // 再进行一次付款汇算
        calReceiptState(ListUtil.toList(receiptId));
//        //同步财务系统
//        noticeCq(repayCashFlows);
    }

    /*private void noticeCq(List<FundReceiptRepayCashFlow> repayCashFlows){
        if (ObjectUtil.isNotEmpty(repayCashFlows)) {
            List<CQ2PaymentVO> vos = new ArrayList<>();
            List<Long> receiptRepayIds = repayCashFlows.stream().map(FundReceiptRepayCashFlow::getReceiptRepayId).collect(Collectors.toList());
            //对方账户
            Map<Long, FundReceiptRepayAccount> receiptRepayId2Account = fundReceiptRepayAccountMapper.selectList(Wrappers.<FundReceiptRepayAccount>lambdaQuery()
                    .in(FundReceiptRepayAccount::getReceiptRepayId, receiptRepayIds)).stream().collect(Collectors.toMap(FundReceiptRepayAccount::getReceiptRepayId, e -> e, (a, b) -> a));
            List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos = fundReceiptRepayBaseInfoMapper.selectBatchIds(receiptRepayIds);
            if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfos)) {
                return;
            }
            Map<Long, FundReceiptRepayBaseInfo> repayBaseInfoMap = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e, (a, b) -> a));
            //
            //我方账户
            Map<Long, FundRepayAccount> ourMap = fundRepayAccountMapper.selectList(Wrappers.<FundRepayAccount>lambdaQuery()
                    .in(FundRepayAccount::getReceiptRepayId, receiptRepayIds)).stream().collect(Collectors.toMap(FundRepayAccount::getReceiptRepayId, e -> e, (a, b) -> a));
            List<FundReceiptRepayBaseInfo> directBaseInfo = fundReceiptRepayBaseInfos.stream().filter(e -> "DIRECT".equals(e.getFinancingType())).collect(Collectors.toList());
            List<FundReceiptRepayBaseInfo> baseInfo = fundReceiptRepayBaseInfos.stream().filter(e -> "DIRECT".equals(e.getFinancingType())).collect(Collectors.toList());
            //直租的
            Map<Long, FundDirectFinancingBaseInfo> id2Direct = SpringContextHolder.getBean(FundDirectFinancingBaseInfoMapper.class).selectBatchIds(directBaseInfo.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            Map<Long, FundFinancingBaseInfo> id2Base = SpringContextHolder.getBean(FundFinancingBaseInfoMapper.class).selectBatchIds(baseInfo.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            repayCashFlows.forEach(repayCashFlow -> {
                FundReceiptRepayBaseInfo receiptRepayBaseInfo = repayBaseInfoMap.get(repayCashFlow.getReceiptRepayId());
                String asstactName;
                OrgDO orgDO;
                if (ObjectUtil.isNotEmpty(receiptRepayBaseInfo)) {
                    if ("DIRECT".equals(receiptRepayBaseInfo.getFinancingType())) {
                        FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = id2Direct.get(receiptRepayBaseInfo.getFinancingId());
                        if (ObjectUtil.isEmpty(fundDirectFinancingBaseInfo)) {
                            return;
                        } else {
                            asstactName = fundDirectFinancingBaseInfo.getProductName();
                            orgDO = SpringContextHolder.getBean(OrgDOMapper.class).selectByPrimaryKey(fundDirectFinancingBaseInfo.getDeptId());
                        }
                    } else {
                        FundFinancingBaseInfo baseInfo1 = id2Base.get(receiptRepayBaseInfo.getFinancingId());
                        if (ObjectUtil.isEmpty(baseInfo1)) {
                            return;
                        } else {
                            asstactName = baseInfo1.getOrganizationName();
                            orgDO = SpringContextHolder.getBean(OrgDOMapper.class).selectByPrimaryKey(baseInfo1.getDeptId());
                        }
                    }
                    vos.add(buildPayment(repayCashFlow, receiptRepayId2Account, ourMap, asstactName, orgDO));
                }
            });
            SyncCqReqBizInfo bizInfo = new SyncCqReqBizInfo();
            //推付款申请单
            financialManagerServiceImpl2.cq2PaymentExec(bizInfo, vos);
        }
    }*/


    private CQ2PaymentVO buildPayment(FundReceiptRepayCashFlow repayCashFlow, Map<Long, FundReceiptRepayAccount> receiptRepayId2Account,  Map<Long, FundRepayAccount> ourMap, String asstactName, OrgDO orgDO) {
        FundReceiptRepayAccount fundReceiptRepayAccount = receiptRepayId2Account.get(repayCashFlow.getReceiptRepayId());
        //付款
        FundRepayAccount fundRepayAccount = ourMap.get(repayCashFlow.getReceiptRepayId());
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setApplydate(repayCashFlow.getRepayDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
        vo.setCico_dept_number(orgDO == null ? null : String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_srcbillno(String.join("-", repayCashFlow.getCashFlowCode(), UUIDUtil.genUuid()));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_JR001.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(repayCashFlow.getRepayAmount()))));
        //
        if(ObjectUtil.isNotEmpty(fundRepayAccount)){
            vo.setCico_payzh_number(fundRepayAccount.getAccountNumber());
            //vo.setPayzh_bank_name(fundRepayAccount.getAccountBank());
            entry.setCico_pay_bank_number_number(fundRepayAccount.getAccountNumber());
            entry.setCico_pay_bank_name_name(fundRepayAccount.getAccountBank());
        }
        entry.setE_asstact_name(fundReceiptRepayAccount.getBankNo());
        //todo 待补充流水号
        entry.setCico_uniquecode(UUIDUtil.genUuid());
        vo.setEntry(CollectionUtil.toList(entry));
        //todo 待补充流水号
        vo.setCico_paynum_rby(UUIDUtil.genUuid());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        vo.setApplycause(String.format("%s:%s,%s", orgDO == null ? null : orgDO.getName(), "融资", ""));
        return vo;
    }

    /**
     * 更新流程状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessState(List<Long> receiptIdList, ProcessState processState) {
        if (CollectionUtils.isEmpty(receiptIdList)) {
            return;
        }
        LambdaUpdateWrapper<FundReceiptRepayBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FundReceiptRepayBaseInfo::getId, receiptIdList);
        updateWrapper.set(FundReceiptRepayBaseInfo::getProcessState, processState.name());
        fundReceiptRepayBaseInfoMapper.update(null, updateWrapper);
    }

    /**
     * 数据更新，看看是否需要把流程状态更新为未提交
     * 理论上除了审批中的状态 都需要更新成未提交
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyUpdateProcessState(Long receiptId) {
        LambdaUpdateWrapper<FundReceiptRepayBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FundReceiptRepayBaseInfo::getId, receiptId);
        updateWrapper.ne(FundReceiptRepayBaseInfo::getProcessState, ProcessState.COMMIT.name());
        updateWrapper.set(FundReceiptRepayBaseInfo::getProcessState, ProcessState.UN_SUBMIT.name());
    }


    /**
     * 付款状态清算
     *
     * @param receiptIdList
     */
    @Transactional(rollbackFor = Exception.class)
    public void calReceiptState(List<Long> receiptIdList) {
        if (CollectionUtils.isEmpty(receiptIdList)) {
            return;
        }
        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<Long> writtenOffReceiptIdList = new ArrayList<>(), settleReceiptIdList = new ArrayList<>(), noWriteOffReceiptIdList = new ArrayList<>(), writeOffIngReceiptIdList = new ArrayList<>();
        Map<Long, List<FundReceiptRepayCashFlow>> cashFlowMap = fundReceiptRepayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptIdList)
                .le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay)
        ).stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId));
        for (Long receiptId : receiptIdList) {
            List<FundReceiptRepayCashFlow> cashFlowList = cashFlowMap.getOrDefault(receiptId, new ArrayList<>());
            if (CollectionUtils.isEmpty(cashFlowList)) {
                // 本月底前没有还款 置为未核销
                noWriteOffReceiptIdList.add(receiptId);
                continue;
            }
            int noWriteOffCount = 0, writeOffIngCount = 0, writtenOffCount = 0;
            for (FundReceiptRepayCashFlow flow : cashFlowList) {
                if (CashFlowState.NO_WRITE_OFF.name().equals(flow.getWriteOffState())) {
                    noWriteOffCount++;
                } else if (CashFlowState.WRITE_OFF_ING.name().equals(flow.getWriteOffState())) {
                    writeOffIngCount++;
                } else {
                    writtenOffCount++;
                }
            }
            // @see cn.zswltech.mithras.fund.enums.receiptrepay.ReceiptRepayState
            if (noWriteOffCount > 0 && writeOffIngCount > 0 && writtenOffCount > 0) {
                // 未核销 + 核销中 + 核销完毕 = 未核销
                noWriteOffReceiptIdList.add(receiptId);
            } else if (noWriteOffCount > 0 && writeOffIngCount > 0) {
                // 未核销 + 核销中 = 核销中
                writeOffIngReceiptIdList.add(receiptId);
            } else if (writeOffIngCount > 0 && writtenOffCount > 0) {
                // 核销中 + 核销完毕 = 核销中
                writeOffIngReceiptIdList.add(receiptId);
            } else if (noWriteOffCount > 0 && writtenOffCount > 0) {
                // 未核销 + 核销完毕 = 未核销
                noWriteOffReceiptIdList.add(receiptId);
            } else if (noWriteOffCount > 0 && writeOffIngCount == 0 && writtenOffCount == 0) {
                // 只有未付款
                noWriteOffReceiptIdList.add(receiptId);
            } else if (noWriteOffCount == 0 && writeOffIngCount > 0 && writtenOffCount == 0) {
                // 只有核销中
                writeOffIngReceiptIdList.add(receiptId);
            } else if (noWriteOffCount == 0 && writeOffIngCount == 0 && writtenOffCount > 0) {
                // 只有核销完毕 看看是不是最后一期核销完毕了 是的话就置为结清
                int notWrittenOffDbCount = fundReceiptRepayCashFlowMapper.selectCount(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .eq(FundReceiptRepayCashFlow::getReceiptRepayId, receiptId)
                        .ne(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name())
                );
                if (notWrittenOffDbCount == 0) {
                    settleReceiptIdList.add(receiptId);
                } else {
                    writtenOffReceiptIdList.add(receiptId);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(noWriteOffReceiptIdList)) {
            // 未核销
            LambdaUpdateWrapper<FundReceiptRepayBaseInfo> writtenOffUpdateWrapper = new LambdaUpdateWrapper<>();
            writtenOffUpdateWrapper.in(FundReceiptRepayBaseInfo::getId, noWriteOffReceiptIdList);
            writtenOffUpdateWrapper.set(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.NO_WRITE_OFF.name());
            fundReceiptRepayBaseInfoMapper.update(null, writtenOffUpdateWrapper);
        }
        if (CollectionUtils.isNotEmpty(writeOffIngReceiptIdList)) {
            // 核销中
            LambdaUpdateWrapper<FundReceiptRepayBaseInfo> writtenOffUpdateWrapper = new LambdaUpdateWrapper<>();
            writtenOffUpdateWrapper.in(FundReceiptRepayBaseInfo::getId, writeOffIngReceiptIdList);
            writtenOffUpdateWrapper.set(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.WRITE_OFF_ING.name());
            fundReceiptRepayBaseInfoMapper.update(null, writtenOffUpdateWrapper);
        }
//        if (CollectionUtils.isNotEmpty(writtenOffReceiptIdList)) {
//            // 核销完毕
//            LambdaUpdateWrapper<FundReceiptRepayBaseInfo> writtenOffUpdateWrapper = new LambdaUpdateWrapper<>();
//            writtenOffUpdateWrapper.in(FundReceiptRepayBaseInfo::getId, writtenOffReceiptIdList);
//            writtenOffUpdateWrapper.set(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.WRITTEN_OFF.name());
//            fundReceiptRepayBaseInfoMapper.update(null, writtenOffUpdateWrapper);
//        }
        if (CollectionUtils.isNotEmpty(settleReceiptIdList)) {
            // 结清
            LambdaUpdateWrapper<FundReceiptRepayBaseInfo> settleUpdateWrapper = new LambdaUpdateWrapper<>();
            settleUpdateWrapper.in(FundReceiptRepayBaseInfo::getId, settleReceiptIdList);
            settleUpdateWrapper.set(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.SETTLE.name());
            fundReceiptRepayBaseInfoMapper.update(null, settleUpdateWrapper);
            // 通知资金模块结清
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfoList = fundReceiptRepayBaseInfoMapper.selectBatchIds(settleReceiptIdList);
            if (CollectionUtil.isNotEmpty(receiptRepayBaseInfoList)) {
                for (FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo : receiptRepayBaseInfoList) {
                    fundFinancingService.notifyFinancingSettle(fundReceiptRepayBaseInfo.getFinancingId(), Objects.equals(fundReceiptRepayBaseInfo.getFinancingType(), "DIRECT"));
                }
            }
//            List<Long> settleFinancingIdList = fundReceiptRepayBaseInfoMapper.selectBatchIds(settleReceiptIdList).stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
//            for (Long financingId : settleFinancingIdList) {
//                fundFinancingService.notifyFinancingSettle(financingId);
//
//            }
        }
    }

    @Resource
    private FundReceiptRepayCashDepositService depositService;

    /**
     * 流程结束更新其他费率
     *
     * @param receiptIdList
     */
    @Transactional(rollbackFor = Exception.class)

    public void processEndUpdateExpense(List<Long> receiptIdList, boolean processPass) {
        if (CollectionUtils.isEmpty(receiptIdList)) {
            return;
        }
        // 更新保证金
        List<FundReceiptRepayCashDeposit> deposits = depositService.list(Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery().in(FundReceiptRepayCashDeposit::getReceiptRepayId, receiptIdList)).stream()
                .map(deposit -> {
                    FundReceiptRepayCashDeposit update = new FundReceiptRepayCashDeposit();
                    update.setId(deposit.getId());
                    if (processPass) {
                        if (deposit.getDepositCashFlowType().equals(DepositCashFlowType.DEPOSIT_PAYMENT.name()) && deposit.getPaidAmount() != null && deposit.getPaidAmount() > 0 && CashFlowState.NO_WRITE_OFF.name().equals(deposit.getWriteOffState())) {
                            update.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
                        } else if (deposit.getDepositCashFlowType().equals(DepositCashFlowType.DEPOSIT_RETURN.name()) && deposit.getReceiptAmount() != null && deposit.getReceiptAmount() > 0 && CashFlowState.NO_WRITE_OFF.name().equals(deposit.getWriteOffState())) {
                            update.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
                        }
                    }
                    return update;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deposits)) {
            depositService.updateBatchById(deposits);
        }
        // 更新其他费用
        List<FundReceiptRepayExpense> toBeUpdateExpense = fundReceiptRepayExpenseService.list(
                        Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                                .in(FundReceiptRepayExpense::getReceiptRepayId, receiptIdList)).stream()
                .map(expense -> {
                    FundReceiptRepayExpense update = new FundReceiptRepayExpense();
                    update.setId(expense.getId());
                    if (processPass) {
                        update.setTotalPaidAmount(LongUtil.null2zero(expense.getTotalPaidAmount()) +
                                LongUtil.null2zero(expense.getPayAmount()));
                        if (update.getTotalPaidAmount() >= expense.getTotalAmount()) {
                            update.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
                        }
                    }
                    update.setPayAmount(0L);
                    return update;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toBeUpdateExpense)) {
            fundReceiptRepayExpenseService.updateBatchById(toBeUpdateExpense);
        }
    }

}
