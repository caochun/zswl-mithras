package cn.zswltech.mithras.application.orchestration.capital;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.dto.collection.BillManagementAddREQ;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.capital.enums.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.OrganizationType;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.enums.receiptrepay.ExpenseType;
import cn.zswltech.mithras.payment.enums.PaymentMethod;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.enums.*;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundPlanFlowQueryDTO;
import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundPlanFlowResultDTO;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayCashDepositService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.fund.mapper.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.fund.model.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.collection.mapper.model.BillManagement;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.*;
import cn.zswltech.mithras.third.financialshare.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.capital.application.excel.BusinessFlowFundCollectExporter;
import cn.zswltech.mithras.capital.application.excel.BusinessFlowFundCollectModel;
import cn.zswltech.mithras.capital.application.excel.BusinessFlowFundPaymentExporter;
import cn.zswltech.mithras.capital.application.excel.BusinessFlowFundPaymentModel;
import cn.zswltech.mithras.application.orchestration.collection.BillManagementService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.*;
import cn.zswltech.mithras.application.orchestration.third.FinanceFlowRecordService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.client.config.AppAuthConfig;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2CollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.third.financialshare.application.dto.SyncCqReqBizInfo;
import cn.zswltech.mithras.third.financialshare.enums.*;
import cn.zswltech.mithras.foundation.util.BigDecimalUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yangxiong
 * @date 2024/5/20/19:27
 * @description
 */
@Slf4j
@Service
public class BusinessFlowService {
    @Resource
    private AppAuthConfig appAuthConfig;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BusinessFlowService thisService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource
    private FundReceiptRepayBorrowingService fundReceiptRepayBorrowingService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptRepayCashDepositService fundReceiptRepayCashDepositService;
    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private FundReceiptFlowPlanService fundReceiptFlowPlanService;
    @Resource
    private BusinessFlowFundPaymentExporter flowFundPaymentExporter;
    @Resource
    private BusinessFlowFundCollectExporter flowFundCollectExporter;

    @Transactional(rollbackFor = Throwable.class)
    public void manualPushRepay(Collection<String> cashFlowCodeList) {
        LambdaQueryWrapper<FundReceiptRepayCashFlow> query = Wrappers.lambdaQuery();
        query.in(FundReceiptRepayCashFlow::getCashFlowCode, cashFlowCodeList);
        List<FundReceiptRepayCashFlow> receiptRepayCashFlowList = fundReceiptRepayCashFlowService.list(query);
        if (CollectionUtil.isEmpty(receiptRepayCashFlowList)) {
            throw new MithrasException("没有找到任何还本付息数据");
        }
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = receiptRepayCashFlowList.stream().collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowCode, e -> e));
        boolean check = true;
        List<String> messageList = new LinkedList<>();
        for (String cashFlowCode : cashFlowCodeList) {
            FundReceiptRepayCashFlow cashFlow = cashFlowMap.get(cashFlowCode);
            if (Objects.isNull(cashFlow)) {
                check = false;
                messageList.add(String.format("<%s>的还本付息数据不存在，请检查", cashFlowCode));
                continue;
            }
            if (!Objects.equals(cashFlow.getWriteOffState(), CashFlowState.PART_WRITE_OFF.name())) {
                check = false;
                messageList.add(String.format("<%s>的还本付息核销状态不是<部分核销>，请检查", cashFlowCode));
            }
        }
        if (!check) {
            throw new MithrasException("<span>" + CharSequenceUtil.join("<br/>", messageList) + "</span>");
        }
        // 批量变更状态
        for (FundReceiptRepayCashFlow fundReceiptRepayCashFlow : receiptRepayCashFlowList) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        fundReceiptRepayCashFlowService.updateBatchById(receiptRepayCashFlowList);
        // 推送单据给苍穹
        for (FundReceiptRepayCashFlow fundReceiptRepayCashFlow : receiptRepayCashFlowList) {
            ThreadPoolUtil.getCommonPool().execute(() -> {
                try {
                    List<CQ2PaymentVO> vos = new LinkedList<>();
                    //1
                    vos.add(buildFromRepay(fundReceiptRepayCashFlow, SpringUtil.getBean(FundReceiptFlowDetailService.class).listByCashFlowCodes(Collections.singletonList(fundReceiptRepayCashFlow.getCashFlowCode()))));
                    SpringUtil.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(new SyncCqReqBizInfo(), vos);
                } catch (Exception e) {
                    log.error("资金端-部分核销<还本付息>-手工确认通知苍穹发生异常[{}]", JSONUtil.toJsonStr(fundReceiptRepayCashFlow), e);
                }
            });
        }
    }

    /**
     * @description: 用于统一发送资金端付款申请
     * @param: ids  资金端核销记录id列表
     * @param: flowRecords 流水
     * @return:
     */
    public void doNoticeCqPayment(List<Long> ids, List<FinanceFlowRecord> flowRecords) {
        log.info("BusinessFlowService doNoticeCqPayment financeFlowRecordId : {}, flowRecord : {}", ids, flowRecords);
        if (CollectionUtil.isEmpty(ids) || ObjectUtil.isEmpty(flowRecords)) {
            log.warn("BusinessFlowService doNoticeCqPayment has null financeFlowRecordId : {}, flowRecord : {}", ids, flowRecords);
            return;
        }
        LambdaQueryWrapper<FundReceiptFlowDetail> detailQuery = Wrappers.lambdaQuery();
        detailQuery.in(FundReceiptFlowDetail::getId, ids);
        detailQuery.in(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.getPayment());
        detailQuery.isNotNull(FundReceiptFlowDetail::getBankFlowNo);
        List<FundReceiptFlowDetail> fundReceiptFlowDetailList = fundReceiptFlowDetailService.list(detailQuery);
        if (ObjectUtil.isEmpty(fundReceiptFlowDetailList)) {
            return;
        }
        //根据现金流分组
        Map<String, List<FundReceiptFlowDetail>> listMap = fundReceiptFlowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowItem));
        List<CQ2PaymentVO> paymentVOS = new ArrayList<>();
        listMap.forEach((cashFlowItem, flowDetailList) -> {
            //根据现金流编号分组
            Map<String, List<FundReceiptFlowDetail>> code2ListMap = flowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
            // TODO 可用设计模式优化，赶时间，下次再说吧
            // 还本付息
            if (Objects.equals(cashFlowItem, FundPlanFlowResultDTO.CashFlowItem.REPAY.name())) {
                try {
                    Map<String, FundReceiptRepayCashFlow> repayCashFlowCode2Bean = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                            .in(FundReceiptRepayCashFlow::getCashFlowCode, code2ListMap.keySet()))
                            .stream().collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowCode, e -> e, (a, b) -> b));
                    code2ListMap.forEach((code, detailList) -> {
                        CQ2PaymentVO cq2PaymentVO = buildFromRepay(repayCashFlowCode2Bean.get(code), detailList);
                        if(ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            paymentVOS.add(cq2PaymentVO);
                        }
                    });
                } catch (Exception e) {
                    log.error("资金端-核销<还本付息>-通知苍穹发生异常[{}]", flowDetailList, e);
                }
            }
            // 保证金付款/退款
            if (CharSequenceUtil.equalsAny(cashFlowItem, FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_PAYMENT.name(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name())) {
                try {
                    Map<String, FundReceiptRepayCashDeposit> repayCashFlowCode2Bean = fundReceiptRepayCashDepositService.list(Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery()
                            .in(FundReceiptRepayCashDeposit::getCashFlowCode, code2ListMap.keySet())).stream().collect(Collectors.toMap(FundReceiptRepayCashDeposit::getCashFlowCode, e -> e, (a, b) -> b));
                    code2ListMap.forEach((code, detailList) -> {
                        CQ2PaymentVO cq2PaymentVO = buildFromDepositPay(repayCashFlowCode2Bean.get(code), detailList);
                        if(ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            paymentVOS.add(cq2PaymentVO);
                        }
                    });
                } catch (Exception e) {
                    log.error("资金端-核销<保证金付款>-通知苍穹发生异常[{}]", flowDetailList, e);
                }
            }
            // 剩下的应该都是费用项，按照费用进行处理
            ExpenseType expenseType = ExpenseType.find(cashFlowItem);
            if (ObjectUtil.isNotEmpty(expenseType)) {
                try {
                    Map<String, FundReceiptRepayExpense> repayCashFlowCode2Bean = fundReceiptRepayExpenseService.list(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                            .in(FundReceiptRepayExpense::getCashFlowCode, code2ListMap.keySet())).stream().collect(Collectors.toMap(FundReceiptRepayExpense::getCashFlowCode, e -> e, (a, b) -> b));
                    code2ListMap.forEach((code, detailList) -> {
                        CQ2PaymentVO cq2PaymentVO = buildFromExpense(repayCashFlowCode2Bean.get(code), detailList);
                        if (ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            paymentVOS.add(cq2PaymentVO);
                        }
                    });
                } catch (Exception e) {
                    log.error("资金端-核销<费用项>-通知苍穹发生异常[{}]", flowDetailList, e);
                }
            }
        });
        //发送请求
        if(!paymentVOS.isEmpty()) {
            //钆差未核销数据
            flowRecords.forEach(flowRecord -> {
                this.paymentVoDiff(paymentVOS, flowRecord);
            });
            //分开发送
            paymentVOS.forEach(paymentVO -> {
                SpringUtil.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(new SyncCqReqBizInfo(), Collections.singletonList(paymentVO));
            });
        }
    }

    private void paymentVoDiff(List<CQ2PaymentVO> paymentVOS, FinanceFlowRecord flowRecord) {
        if (LongUtil.null2zero(flowRecord.getSurplusAmount()) <= 0 || ObjectUtil.isEmpty(paymentVOS)) {
            return;
        }
        CQ2PaymentVO vo = paymentVOS.get(0);
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entry = vo.getEntry();
        if (ObjectUtil.isEmpty(entry)) {
            return;
        }
        CQ2PaymentVO.CQ2PaymentVOEntry voEntry = BeanUtil.copyProperties(entry.get(0), CQ2PaymentVO.CQ2PaymentVOEntry.class);
        voEntry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_999.getCode());
        voEntry.setE_applyamount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(flowRecord.getSurplusAmount()).toString()));
        entry.add(voEntry);
    }
    public void detailSave(BusinessFlowFinanceDetailSaveREQ req) {
        if (Objects.equals(req.getSettleMethod(), PaymentMethod.PJ.name())) {
            // 如果是票据，校验票据信息
            if (Objects.isNull(req.getBillManagementAddREQ())) {
                throw new MithrasException("票据信息不能为空");
            }
        }
        if (!Objects.equals(req.getCashFlowItem(), FinanceCashFlowItemEnum.REPAY.name())) {
            if (Objects.isNull(req.getTotalAmount())) {
                throw new MithrasException("核销金额不能为空");
            }
        }
        // TODO 可用设计模式优化，赶时间，下次再说吧
        if (Objects.equals(req.getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.FINANCE_FUND.name())) {
            // 融资款
            this.doBorrow(req);
            return;
        }
        if (Objects.equals(req.getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.REPAY.name())) {
            // 还本付息
            this.doRepay(req);
            return;
        }
        if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_PAYMENT.name(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name())) {
            // 保证金
            this.doDeposit(req);
            return;
        }
        // 剩下的应该都是费用项，按照费用进行处理
        ExpenseType expenseType = ExpenseType.find(req.getCashFlowItem());
        if (Objects.isNull(expenseType)) {
            throw new MithrasException("未知的现金流类型");
        }
        this.doExpense(req);
    }

    public List<BusinessFlowFinanceDetailListRSP> detailList(BusinessFlowFinanceDetailListREQ req) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowDetail::getCashFlowCode, req.getCashFlowCode());
        List<FundReceiptFlowDetail> list = fundReceiptFlowDetailService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Collection<Long> userIds = list.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userNameMap;
        if (CollectionUtil.isNotEmpty(userIds)) {
            userNameMap = id2NameService.sysUserId2Name(userIds);
        } else {
            userNameMap = Collections.emptyMap();
        }
        return list.stream().map(e -> {
            BusinessFlowFinanceDetailListRSP rsp = new BusinessFlowFinanceDetailListRSP();
            rsp.setId(e.getId());
            rsp.setDataSource(e.getDataSource());
            rsp.setCashFlowDate(LocalDateTimeUtil.format(e.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setTotalAmount(e.getTotalAmount());
            rsp.setPrincipalAmount(e.getPrincipalAmount());
            rsp.setInterestAmount(e.getInterestAmount());
            rsp.setBankFlowNo(e.getBankFlowNo());
            rsp.setSettleMethod(Optional.ofNullable(PaymentMethod.findByDisplay(e.getSettleMethod())).map(Enum::name).orElse(e.getSettleMethod()));
            rsp.setOperatorName(userNameMap.get(e.getCreateBy()));
            rsp.setOperateDate(Optional.ofNullable(e.getCreateTime()).map(ct -> LocalDateTimeUtil.format(ct, DatePattern.NORM_DATE_PATTERN)).orElse(""));
            // 如果是票据则查询一下票据信息，因为票据场景比较少，直接循环中查询数据库，后续如果有问题此处需改造
            if (Objects.equals(e.getSettleMethod(), PaymentMethod.PJ.display())) {
                List<BillManagement> billManagementList = billManagementService.list(Wrappers.<BillManagement>lambdaQuery().eq(BillManagement::getMainId, e.getId()));
                if (CollectionUtil.isNotEmpty(billManagementList)) {
                    // 理论上只有一条数据
                    BillManagement billManagement = billManagementList.get(0);
                    rsp.setBillCode(billManagement.getBillCode());
                    rsp.setBillAmount(billManagement.getBillAmount());
                    rsp.setBillBuyRate(billManagement.getBillBuyRate());
                    rsp.setBillExpireDate(billManagement.getBillExpireDate());
                }
            }
            return rsp;
        }).collect(Collectors.toList());
    }

    public PageR<BusinessFlowFinanceListRSP> selectList(BusinessFlowFinanceListREQ req) {
        if (Objects.equals(req.getFlowType(), BusinessFlowFinanceListREQ.FLOW_TYPE_PAY)) {
            Page<FundReceiptRepayBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            FundPlanFlowQueryDTO conditionQuery = this.buildQuery(req);
            return this.buildFundFlowResult(fundReceiptRepayBaseInfoMapper.paymentPageList(pageQuery, conditionQuery));
        } else if (Objects.equals(req.getFlowType(), BusinessFlowFinanceListREQ.FLOW_TYPE_COLLECT)) {
            // 查询收款
            Page<FundReceiptRepayBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            FundPlanFlowQueryDTO conditionQuery = this.buildQuery(req);
            return this.buildFundFlowResult(fundReceiptRepayBaseInfoMapper.collectPageList(pageQuery, conditionQuery));
        } else {
            throw new MithrasException("未定义的业务流水类型");
        }
    }

    public List<CQ2CollectionVO> getCollectionVONew(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<FundReceiptFlowDetail> detailQuery = Wrappers.lambdaQuery();
        detailQuery.in(FundReceiptFlowDetail::getId, ids);
        detailQuery.isNotNull(FundReceiptFlowDetail::getBankFlowNo);
        List<FundReceiptFlowDetail> fundReceiptFlowDetailList = fundReceiptFlowDetailService.list(detailQuery);
        if (CollectionUtil.isEmpty(fundReceiptFlowDetailList)) {
            return Collections.emptyList();
        }
        // 过滤收款
        fundReceiptFlowDetailList.removeIf(e -> !CharSequenceUtil.equalsAny(e.getCashFlowItem(), FinanceCashFlowItemEnum.FINANCE_FUND.name(), FinanceCashFlowItemEnum.DEPOSIT_RETURN.name()));
        if (CollectionUtil.isEmpty(fundReceiptFlowDetailList)) {
            return Collections.emptyList();
        }
        // 根据银行流水分组
        Map<String, List<FundReceiptFlowDetail>> fundReceiptFlowDetailMap = fundReceiptFlowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getBankFlowNo));
        List<CQ2CollectionVO> result = new LinkedList<>();
        for (Map.Entry<String, List<FundReceiptFlowDetail>> entry : fundReceiptFlowDetailMap.entrySet()) {
            result.add(this.buildCQ2CollectionVO(entry.getKey(), entry.getValue()));
        }
        // 分装苍穹请求参数
        return result;
    }

    private FundPlanFlowQueryDTO buildQuery(BusinessFlowFinanceListREQ req) {
        FundPlanFlowQueryDTO query = new FundPlanFlowQueryDTO();
        if (CollectionUtil.isNotEmpty(req.getWriteOffStatusList())) {
            query.setWriteOffStateList(req.getWriteOffStatusList());
        }
        if (StrUtil.isNotBlank(req.getDateFrom())) {
            query.setPlanCashFlowDateFrom(req.getDateFrom());
        }
        if (StrUtil.isNotBlank(req.getDateTo())) {
            query.setPlanCashFlowDateTo(req.getDateTo());
        }
        if (StrUtil.isNotBlank(req.getFinancingRoute())) {
            query.setFundChannel(req.getFinancingRoute());
        }
        if (StrUtil.isNotBlank(req.getCashFlowItem())) {
            query.setCashFlowItem(req.getCashFlowItem());
        }
        if (StrUtil.isNotBlank(req.getFinancingCode())) {
            query.setFinancingCode(req.getFinancingCode());
        }
        return query;
    }

    private PageR<BusinessFlowFinanceListRSP> buildFundFlowResult(Page<FundPlanFlowResultDTO> pageResult) {
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(pageResult.getCurrent(), pageResult.getSize());
        }
        // 查询实际核销记录
        Set<String> cashFlowCodes = pageResult.getRecords().stream().map(FundPlanFlowResultDTO::getCashFlowCode).collect(Collectors.toSet());
        List<FundReceiptFlowDetail> detailList = fundReceiptFlowDetailService.listByCashFlowCodes(cashFlowCodes);
        Map<String, List<FundReceiptFlowDetail>> actualAmountMap = detailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        List<BusinessFlowFinanceListRSP> list = pageResult.getRecords().stream().map(e -> {
            BusinessFlowFinanceListRSP rsp = new BusinessFlowFinanceListRSP();
            rsp.setIdKey(e.getIdentifier());
            rsp.setReceiptRepayId(e.getReceiptRepayBaseId());
            rsp.setBusinessType(this.transformBizType(e.getIsDirect(), e.getBizType()));
            rsp.setCashFlowItem(e.getCashFlowItem());
            rsp.setCashFlowItemDisplay(this.transformCashFlowItem(e.getCashFlowItem()));
            rsp.setSerialNo(e.getCashFlowCode());
            if (Objects.nonNull(e.getPlanCashFlowDate())) {
                rsp.setDate(LocalDateTimeUtil.format(e.getPlanCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setFinancingCode(e.getFinancingCode());
            rsp.setFinancingRoute(e.getFundChannel());
            rsp.setAmount(e.getPlanCashFlowAmount());
            rsp.setPrincipalAmount(e.getPlanPrincipalAmount());
            rsp.setInterestAmount(e.getPlanInterestAmount());
            List<FundReceiptFlowDetail> actualDetailList = actualAmountMap.get(e.getCashFlowCode());
            if (CollectionUtil.isEmpty(actualDetailList)) {
                rsp.setActualVerifyAmount(0L);
                rsp.setActualVerifyPrincipalAmount(0L);
                rsp.setActualVerifyInterestAmount(0L);
            } else {
                rsp.setCashFlowDate(actualDetailList.stream().map(FundReceiptFlowDetail::getCashFlowDate).max(LocalDate::compareTo).get());
                rsp.setActualVerifyAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getTotalAmount())).mapToLong(FundReceiptFlowDetail::getTotalAmount).sum());
                rsp.setActualVerifyPrincipalAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum());
                rsp.setActualVerifyInterestAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum());
            }
            rsp.setWriteOffStatus(e.getWriteOffState());
            rsp.setWriteOffStatusDisplay(Optional.ofNullable(CashFlowState.of(e.getWriteOffState())).map(CashFlowState::display).orElse("未知"));
            rsp.setFinancingAmount(e.getFinancingAmount());
            rsp.setFinancingId(e.getFinancingId());
            rsp.setPhase(e.getPhase());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(list, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    private String transformBizType(Integer isDirect, String bizType) {
        if (Objects.equals(isDirect, YesOrNoNumberEnum.YES.getCode())) {
            return Optional.ofNullable(DirectFinancingType.findByName(bizType)).map(DirectFinancingType::display).orElse("未知");
        } else if (Objects.equals(isDirect, YesOrNoNumberEnum.NO.getCode())) {
            return Optional.ofNullable(FundFinancingBizTypeEnum.finaByName(bizType)).map(FundFinancingBizTypeEnum::display).orElse("未知");
        } else {
            return "未知";
        }
    }

    private String transformCashFlowItem(String name) {
        FundPlanFlowResultDTO.CashFlowItem customCashFlowItem = FundPlanFlowResultDTO.CashFlowItem.find(name);
        if (Objects.nonNull(customCashFlowItem)) {
            return customCashFlowItem.getDisplay();
        }
        // 尝试从费用枚举中寻找
        ExpenseType expenseType = ExpenseType.find(name);
        if (Objects.nonNull(expenseType)) {
            return expenseType.display();
        }
        return "未知";
    }

    private void doBorrow(BusinessFlowFinanceDetailSaveREQ req) {
        LambdaQueryWrapper<FundReceiptRepayBorrowing> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayBorrowing::getReceiptRepayId, req.getReceiptRepayId());
        query.eq(FundReceiptRepayBorrowing::getCashFlowCode, req.getCashFlowCode());
        FundReceiptRepayBorrowing fundReceiptRepayBorrowing = fundReceiptRepayBorrowingService.getOne(query);
        long hasVerifyAmount = fundReceiptFlowDetailService.sum(fundReceiptRepayBorrowing.getReceiptRepayId(), fundReceiptRepayBorrowing.getCashFlowCode());
        long remainingAmount = fundReceiptRepayBorrowing.getPrincipal() - hasVerifyAmount;
        long currentRemainingAmount = remainingAmount - req.getTotalAmount();
        if (currentRemainingAmount < 0) {
            throw new MithrasException("操作失败，" + req.getCashFlowCode() + "剩余可核销金额" + Util.toYuan(remainingAmount, true) + "元");
        } else if (currentRemainingAmount != 0) {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayBorrowing.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                doSaveDetail(req);
                fundReceiptRepayBorrowingService.updateById(fundReceiptRepayBorrowing);
                fundReceiptFlowPlanService.writeOff(fundReceiptRepayBorrowing.getCashFlowCode(), fundReceiptRepayBorrowing.getWriteOffState());
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("资金端-核销融资款发生异常[{}]", JSONUtil.toJsonStr(req), e);
                throw new MithrasException("未知异常");
            }
        });
    }

    private void doRepay(BusinessFlowFinanceDetailSaveREQ req) {
        long principal = Optional.ofNullable(req.getPrincipalAmount()).orElse(0L);
        long interest = Optional.ofNullable(req.getInterestAmount()).orElse(0L);
        req.setTotalAmount(principal + interest);
        LambdaQueryWrapper<FundReceiptRepayCashFlow> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayCashFlow::getReceiptRepayId, req.getReceiptRepayId());
        query.eq(FundReceiptRepayCashFlow::getCashFlowCode, req.getCashFlowCode());
        FundReceiptRepayCashFlow fundReceiptRepayCashFlow = fundReceiptRepayCashFlowService.getOne(query);
        long hasVerifyAmount = fundReceiptFlowDetailService.sum(fundReceiptRepayCashFlow.getReceiptRepayId(), fundReceiptRepayCashFlow.getCashFlowCode());
        long remainingAmount = Optional.ofNullable(fundReceiptRepayCashFlow.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(fundReceiptRepayCashFlow.getInterestAmount()).orElse(0L) - hasVerifyAmount;
        long currentRemainingAmount = remainingAmount - req.getTotalAmount();
        // TODO 还本付息临时方案，不设置金额校验
        if (currentRemainingAmount < 0) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.BEYOND_WRITTEN_OFF.name());
        } else if (currentRemainingAmount > 0) {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayCashFlow.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        Boolean success = transactionTemplate.execute(transactionStatus -> {
            try {
                thisService.doSaveDetail(req);
                fundReceiptRepayCashFlowService.updateById(fundReceiptRepayCashFlow);
                fundReceiptFlowPlanService.writeOff(fundReceiptRepayCashFlow.getCashFlowCode(), fundReceiptRepayCashFlow.getWriteOffState());
                return Boolean.TRUE;
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("资金端-核销<还本付息>发生异常[{}]", JSONUtil.toJsonStr(req), e);
                return Boolean.FALSE;
            }
        });
        if (Objects.nonNull(success) && !success) {
            throw new MithrasException("操作失败，发生未知异常");
        }
        /*if (Objects.equals(fundReceiptRepayCashFlow.getWriteOffState(), CashFlowState.WRITTEN_OFF.name()) || Objects.equals(fundReceiptRepayCashFlow.getWriteOffState(), CashFlowState.BEYOND_WRITTEN_OFF.name())) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 异步调用苍穹接口
                    ThreadPoolUtil.getCommonPool().execute(() -> {
                        try {
                            List<CQ2PaymentVO> vos = new LinkedList<>();
                            //2
                            vos.add(buildFromRepay(fundReceiptRepayCashFlow, SpringUtil.getBean(FundReceiptFlowDetailService.class).listByCashFlowCodes(Collections.singletonList(fundReceiptRepayCashFlow.getCashFlowCode()))));
                            SpringUtil.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(new SyncCqReqBizInfo(), vos);
                        } catch (Exception e) {
                            log.error("资金端-核销<还本付息>-通知苍穹发生异常[{}]", JSONUtil.toJsonStr(req), e);
                        }
                    });
                }
            });
        }*/
    }

    private void doDeposit(BusinessFlowFinanceDetailSaveREQ req) {
        LambdaQueryWrapper<FundReceiptRepayCashDeposit> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayCashDeposit::getReceiptRepayId, req.getReceiptRepayId());
        query.eq(FundReceiptRepayCashDeposit::getCashFlowCode, req.getCashFlowCode());
        FundReceiptRepayCashDeposit fundReceiptRepayCashDeposit = fundReceiptRepayCashDepositService.getOne(query);
        long hasVerifyAmount = fundReceiptFlowDetailService.sum(fundReceiptRepayCashDeposit.getReceiptRepayId(), fundReceiptRepayCashDeposit.getCashFlowCode());
        long remainingAmount = fundReceiptRepayCashDeposit.getAmount() - hasVerifyAmount;
        long currentRemainingAmount = remainingAmount - req.getTotalAmount();
        if (currentRemainingAmount < 0) {
            throw new MithrasException("操作失败，" + req.getCashFlowCode() + "剩余可核销金额" + Util.toYuan(remainingAmount, true) + "元");
        } else if (currentRemainingAmount != 0) {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayCashDeposit.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        Boolean success = transactionTemplate.execute(transactionStatus -> {
            try {
                doSaveDetail(req);
                fundReceiptRepayCashDepositService.updateById(fundReceiptRepayCashDeposit);
                fundReceiptFlowPlanService.writeOff(fundReceiptRepayCashDeposit.getCashFlowCode(), fundReceiptRepayCashDeposit.getWriteOffState());
                return Boolean.TRUE;
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("资金端-核销<保证金>发生异常[{}]", JSONUtil.toJsonStr(req), e);
                return Boolean.FALSE;
            }
        });
        if (Objects.nonNull(success) && !success) {
            throw new MithrasException("操作失败，发生未知异常");
        }
       /* if (Objects.equals(fundReceiptRepayCashDeposit.getWriteOffState(), CashFlowState.WRITTEN_OFF.name())) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 异步调用苍穹接口
                    ThreadPoolUtil.getCommonPool().execute(() -> {
                        if (Objects.equals(fundReceiptRepayCashDeposit.getDepositCashFlowType(), DepositCashFlowType.DEPOSIT_PAYMENT.name())) {
                            try {
                                List<CQ2PaymentVO> vos = new LinkedList<>();
                                //3
                                vos.add(buildFromDepositPay(fundReceiptRepayCashDeposit));
                                SpringUtil.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(new SyncCqReqBizInfo(), vos);
                            } catch (Exception e) {
                                log.error("资金端-核销<保证金付款>-通知苍穹发生异常[{}]", JSONUtil.toJsonStr(req), e);
                            }
                        }
//                        if (Objects.equals(fundReceiptRepayCashDeposit.getDepositCashFlowType(), DepositCashFlowType.DEPOSIT_RETURN.name())) {
//                            try {
//
//                            } catch (Exception e) {
//                                log.error("资金端-核销<保证金退款>-通知苍穹发生异常[{}]", JSONUtil.toJsonStr(req), e);
//                            }
//                        }
                    });
                }
            });
        }*/
    }

    private void doExpense(BusinessFlowFinanceDetailSaveREQ req) {
        LambdaQueryWrapper<FundReceiptRepayExpense> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayExpense::getReceiptRepayId, req.getReceiptRepayId());
        query.eq(FundReceiptRepayExpense::getCashFlowCode, req.getCashFlowCode());
        FundReceiptRepayExpense fundReceiptRepayExpense = fundReceiptRepayExpenseService.getOne(query);
        long hasVerifyAmount = fundReceiptFlowDetailService.sum(fundReceiptRepayExpense.getReceiptRepayId(), fundReceiptRepayExpense.getCashFlowCode());
        long remainingAmount = fundReceiptRepayExpense.getTotalAmount() - hasVerifyAmount;
        long currentRemainingAmount = remainingAmount - req.getTotalAmount();
        if (currentRemainingAmount < 0) {
            throw new MithrasException("操作失败，" + req.getCashFlowCode() + "剩余可核销金额" + Util.toYuan(remainingAmount, true) + "元");
        } else if (currentRemainingAmount != 0) {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.PART_WRITE_OFF.name());
        } else {
            fundReceiptRepayExpense.setWriteOffState(CashFlowState.WRITTEN_OFF.name());
        }
        // 变更已付总费用
        long totalPayAmount = Optional.ofNullable(fundReceiptRepayExpense.getTotalPaidAmount()).orElse(0L) + req.getTotalAmount();
        fundReceiptRepayExpense.setTotalPaidAmount(totalPayAmount);
        Boolean success = transactionTemplate.execute(transactionStatus -> {
            try {
                doSaveDetail(req);
                fundReceiptRepayExpenseService.updateById(fundReceiptRepayExpense);
                fundReceiptFlowPlanService.writeOff(fundReceiptRepayExpense.getCashFlowCode(), fundReceiptRepayExpense.getWriteOffState());
                return Boolean.TRUE;
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("资金端-核销<费用项>发生异常[{}]", JSONUtil.toJsonStr(req), e);
                return Boolean.FALSE;
            }
        });
        if (Objects.nonNull(success) && !success) {
            throw new MithrasException("操作失败，发生未知异常");
        }
        /*if (Objects.equals(fundReceiptRepayExpense.getWriteOffState(), CashFlowState.WRITTEN_OFF.name())) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 异步调用苍穹接口
                    ThreadPoolUtil.getCommonPool().execute(() -> {
                        try {
                            List<CQ2PaymentVO> vos = new LinkedList<>();
                            //4
                            vos.add(buildFromExpense(fundReceiptRepayExpense));
                            SpringUtil.getBean(FinancialManagerServiceImpl2.class).cq2PaymentExec(new SyncCqReqBizInfo(), vos);
                        } catch (Exception e) {
                            log.error("资金端-核销<费用项>-通知苍穹发生异常[{}]", JSONUtil.toJsonStr(req), e);
                        }
                    });
                }
            });
        }*/
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doSaveDetail(BusinessFlowFinanceDetailSaveREQ req) {
        FundReceiptFlowDetail detail = new FundReceiptFlowDetail();
        detail.setReceiptRepayId(req.getReceiptRepayId());
        detail.setCashFlowItem(req.getCashFlowItem());
        detail.setCashFlowDate(LocalDateTimeUtil.parseDate(req.getCashFlowDate().substring(0, 10), DatePattern.NORM_DATE_PATTERN));
        detail.setCashFlowCode(req.getCashFlowCode());
        detail.setTotalAmount(req.getTotalAmount());
        detail.setPrincipalAmount(req.getPrincipalAmount());
        detail.setInterestAmount(req.getInterestAmount());
        detail.setBankFlowNo(req.getBankDetailNo());
        detail.setFinanceFlowId(req.getFinanceFlowId());
        detail.setOurAccountName(req.getOurAccountName());
        detail.setOurAccountNumber(req.getOurAccountNumber());
        detail.setOurAccountBank(req.getOurAccountBank());
        if (StrUtil.isNotBlank(req.getDataSource())) {
            detail.setDataSource(req.getDataSource());
        }
        PaymentMethod paymentMethod = PaymentMethod.findByName(req.getSettleMethod());
        if (Objects.isNull(paymentMethod)) {
            // 如果找不到的话默认使用电汇吧
            detail.setSettleMethod(PaymentMethod.WY.getDisplay());
        } else {
            detail.setSettleMethod(paymentMethod.getDisplay());
        }
        fundReceiptFlowDetailService.save(detail);
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setBankDetailNo(req.getBankDetailNo());
        financeFlowWriteOffDetail.setFinanceFlowId(req.getFinanceFlowId());
        financeFlowWriteOffDetail.setMainId(detail.getId());
        financeFlowWriteOffDetail.setRecordMainTable(FinanceFlowDetailTableEnum.FUND_RECEIPT_FLOW_DETAIL.name());
        getBean(FinanceFlowWriteOffDetailMapper.class).insert(financeFlowWriteOffDetail);
        if (Objects.equals(req.getSettleMethod(), PaymentMethod.PJ.name())) {
            BillManagementAddREQ billReq = req.getBillManagementAddREQ();
            // 如果是票据，保存票据信息
            BillManagement billManagement = new BillManagement();
            billManagement.setMainId(detail.getId());
            if (CharSequenceUtil.equalsAny(detail.getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.FINANCE_FUND.name(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name())) {
                billManagement.setBillType(BillTypeEnum.FINANCE_COLLECTION.name());
            } else {
                billManagement.setBillType(BillTypeEnum.FINANCE_PAYMENT.name());
            }
            billManagement.setBillCode(billReq.getBillCode());
            billManagement.setBillAmount(billReq.getBillAmount());
            billManagement.setBillBuyRate(billReq.getBillBuyRate());
            billManagement.setBillExpireDate(billReq.getBillExpireDate());
            billManagementService.save(billManagement);
        }
    }
    /**
     * 构建资金端还本付息的付款单参数
     *
     */
    private CQ2PaymentVO buildFromRepay(FundReceiptRepayCashFlow fundReceiptRepayCashFlow, List<FundReceiptFlowDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList) || ObjectUtil.isEmpty(detailList)) {
            throw new MithrasException("没有找到核销明细");
        }
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getById(fundReceiptRepayCashFlow.getReceiptRepayId());
        String orgName = this.ensureOrgName(fundReceiptRepayBaseInfo);
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = this.getBussnessType(fundReceiptRepayBaseInfo);
        CQ2PaymentVO cq2PaymentVO = new CQ2PaymentVO();
        cq2PaymentVO.setCico_srcbillno(fundReceiptRepayCashFlow.getCashFlowCode() + "-" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        cq2PaymentVO.setSettleorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setApplyorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_dept_number(FinancialConstants.RZZL_ZJGLB_CODE);
        cq2PaymentVO.setPayorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_payzh_number(detailList.get(0).getOurAccountNumber());
//        cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(fundReceiptRepayCashFlow.getRepayDate(), DatePattern.NORM_DATE_PATTERN));
        cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(this.ensureTargetDate(detailList), DatePattern.NORM_DATE_PATTERN));
        cq2PaymentVO.setCreator_number(appAuthConfig.getClientNo());
        cq2PaymentVO.setCico_ishavecontr(false);
        cq2PaymentVO.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        cq2PaymentVO.setApplycause("资金端-还本付息：" + orgName + Util.toWanYuanWithoutSplit(fundReceiptRepayBaseInfo.getFinancingAmount()) + "万元");
        cq2PaymentVO.setCico_srcsystem(FinancialConstants.RZY);
        cq2PaymentVO.setCico_paynum_rby(detailList.stream().filter(e -> StrUtil.isNotBlank(e.getBankFlowNo())).map(FundReceiptFlowDetail::getBankFlowNo).collect(Collectors.joining(",")));
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entryList = detailList.stream().map(e -> {
            CQ2PaymentVO.CQ2PaymentVOEntry entry = cq2PaymentVO.new CQ2PaymentVOEntry();
            if (Objects.nonNull(e.getPrincipalAmount()) && e.getPrincipalAmount() != 0) {
                entry.setE_paymenttype_number(ensureCQPaymentType(fundReceiptRepayBaseInfo, true).getCode());
                entry.setE_applyamount(BigDecimal.valueOf(e.getPrincipalAmount()).divide(BigDecimal.valueOf(10000), 4, RoundingMode.HALF_UP));
            } else {
                entry.setE_paymenttype_number(ensureCQPaymentType(fundReceiptRepayBaseInfo, false).getCode());
                entry.setE_applyamount(BigDecimal.valueOf(e.getInterestAmount()).divide(BigDecimal.valueOf(10000), 4, RoundingMode.HALF_UP));
            }
            entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
            entry.setE_asstact_name(FinancialConstants.RZZL_NAME);
            entry.setCico_pay_bank_number_number(e.getOurAccountNumber());
            entry.setCico_pay_bank_name_name(e.getOurAccountBank());
            entry.setE_settlementtype_number(CQPaymentMethodENUM.JSFS16.getDisplay());
            entry.setCico_settemenorg_number(FinancialConstants.RZZL_CODE);
            entry.setE_asstact(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
            entry.setCico_uniquecode(e.getBankFlowNo());
            //设置业务类型
            entry.setCico_businesstype_number(leaseTypeCode);
            return entry;
        }).collect(Collectors.toList());
        cq2PaymentVO.setEntry(entryList);

        //这里是资金端还本付息付款
        cq2PaymentVO.setSource(ExceptionSourceENUM.FINANCE_SIDE.name());
        cq2PaymentVO.setBusinessKey(fundReceiptRepayCashFlow.getCashFlowCode());
        cq2PaymentVO.setBusinessTitle(fundReceiptRepayCashFlow.getCashFlowCode());
        return cq2PaymentVO;
    }

    private CQ2PaymentVO buildFromExpense(FundReceiptRepayExpense fundReceiptRepayExpense, List<FundReceiptFlowDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            throw new MithrasException("没有找到核销明细");
        }
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getById(fundReceiptRepayExpense.getReceiptRepayId());
        String orgName = this.ensureOrgName(fundReceiptRepayBaseInfo);
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = this.getBussnessType(fundReceiptRepayBaseInfo);
//        List<FundReceiptRepayCashFlow> cashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).listByReceiptRepayId(fundReceiptRepayExpense.getReceiptRepayId(), null).orElse(Collections.emptyList());
        CQ2PaymentVO cq2PaymentVO = new CQ2PaymentVO();
        cq2PaymentVO.setCico_srcbillno(fundReceiptRepayExpense.getCashFlowCode() + "-" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        cq2PaymentVO.setSettleorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setApplyorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_dept_number(FinancialConstants.RZZL_ZJGLB_CODE);
        cq2PaymentVO.setPayorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_payzh_number(detailList.get(0).getOurAccountNumber());
//        if (CollectionUtil.isNotEmpty(cashFlowList)) {
//            cashFlowList.sort(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate));
//            cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(cashFlowList.get(0).getRepayDate(), DatePattern.NORM_DATE_PATTERN));
//        }
        cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(this.ensureTargetDate(detailList), DatePattern.NORM_DATE_PATTERN));
        cq2PaymentVO.setCreator_number(appAuthConfig.getClientNo());
        cq2PaymentVO.setCico_ishavecontr(false);
        cq2PaymentVO.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        ExpenseType expenseType = ExpenseType.find(fundReceiptRepayExpense.getExpenseType());
        cq2PaymentVO.setApplycause("资金端-" + Optional.ofNullable(expenseType).map(ExpenseType::display).orElse("未知费用") + "：" + orgName + Util.toWanYuanWithoutSplit(fundReceiptRepayBaseInfo.getFinancingAmount()) + "万元");
        cq2PaymentVO.setCico_srcsystem(FinancialConstants.RZY);
        cq2PaymentVO.setCico_paynum_rby(detailList.stream().filter(e -> StrUtil.isNotBlank(e.getBankFlowNo())).map(FundReceiptFlowDetail::getBankFlowNo).collect(Collectors.joining(",")));
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entryList = detailList.stream().map(e -> {
            CQ2PaymentVO.CQ2PaymentVOEntry entry = cq2PaymentVO.new CQ2PaymentVOEntry();
            entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_999.getCode());
            entry.setE_applyamount(BigDecimal.valueOf(e.getTotalAmount()).divide(BigDecimal.valueOf(10000), 4, RoundingMode.HALF_UP));
            entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
            entry.setE_asstact_name(orgName);
            entry.setCico_pay_bank_number_number(e.getOurAccountNumber());
            entry.setE_settlementtype_number(CQPaymentMethodENUM.JSFS16.name());
            entry.setCico_settemenorg_number(FinancialConstants.RZZL_CODE);
            entry.setE_asstact(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
            entry.setCico_uniquecode(e.getBankFlowNo());
            // 业务类型
            entry.setCico_businesstype_number(leaseTypeCode);
            return entry;
        }).collect(Collectors.toList());
        cq2PaymentVO.setEntry(entryList);
        //这里是资金端保证金明细
        cq2PaymentVO.setSource(ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name());
        cq2PaymentVO.setBusinessKey(fundReceiptRepayExpense.getCashFlowCode());
        cq2PaymentVO.setBusinessTitle(fundReceiptRepayExpense.getCashFlowCode());
        return cq2PaymentVO;
    }

    /**
     * 构建保证金支付的付款单参数
     *
     */
    private CQ2PaymentVO buildFromDepositPay(FundReceiptRepayCashDeposit fundReceiptRepayCashDeposit, List<FundReceiptFlowDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList) || ObjectUtil.isEmpty(detailList)) {
            throw new MithrasException("没有找到核销明细");
        }
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getById(fundReceiptRepayCashDeposit.getReceiptRepayId());
        String orgName = this.ensureOrgName(fundReceiptRepayBaseInfo);
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = this.getBussnessType(fundReceiptRepayBaseInfo);
        List<FundReceiptRepayCashFlow> cashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).listByReceiptRepayId(fundReceiptRepayCashDeposit.getReceiptRepayId(), null).orElse(Collections.emptyList());
        CQ2PaymentVO cq2PaymentVO = new CQ2PaymentVO();
        cq2PaymentVO.setCico_srcbillno(fundReceiptRepayCashDeposit.getCashFlowCode() + "-" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        cq2PaymentVO.setSettleorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setApplyorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_dept_number(FinancialConstants.RZZL_ZJGLB_CODE);
        cq2PaymentVO.setPayorg_number(FinancialConstants.RZZL_CODE);
        cq2PaymentVO.setCico_payzh_number(detailList.get(0).getOurAccountNumber());
//        if (CollectionUtil.isNotEmpty(cashFlowList)) {
//            cashFlowList.sort(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate));
//            cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(cashFlowList.get(0).getRepayDate(), DatePattern.NORM_DATE_PATTERN));
//        }
        cq2PaymentVO.setApplydate(LocalDateTimeUtil.format(this.ensureTargetDate(detailList), DatePattern.NORM_DATE_PATTERN));
        cq2PaymentVO.setCreator_number(appAuthConfig.getClientNo());
        cq2PaymentVO.setCico_ishavecontr(false);
        cq2PaymentVO.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        cq2PaymentVO.setApplycause("资金端-保证金付款：" + orgName + Util.toWanYuanWithoutSplit(fundReceiptRepayBaseInfo.getFinancingAmount()) + "万元");
        cq2PaymentVO.setCico_srcsystem(FinancialConstants.RZY);
        cq2PaymentVO.setCico_paynum_rby(detailList.stream().filter(e -> StrUtil.isNotBlank(e.getBankFlowNo())).map(FundReceiptFlowDetail::getBankFlowNo).collect(Collectors.joining(",")));
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entryList = detailList.stream().map(e -> {
            CQ2PaymentVO.CQ2PaymentVOEntry entry = cq2PaymentVO.new CQ2PaymentVOEntry();
            entry.setE_paymenttype_number(CQPaymentTypeENUM.FK04_007.getCode());
            entry.setE_applyamount(BigDecimal.valueOf(e.getTotalAmount()).divide(BigDecimal.valueOf(10000), 4, RoundingMode.HALF_UP));
            entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
            entry.setE_asstact_name(orgName);
            entry.setCico_pay_bank_number_number(e.getOurAccountNumber());
            entry.setE_settlementtype_number(CQPaymentMethodENUM.JSFS16.name());
            entry.setCico_settemenorg_number(FinancialConstants.RZZL_CODE);
            entry.setE_asstact(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
            entry.setCico_uniquecode(e.getBankFlowNo());
            // 业务类型
            entry.setCico_businesstype_number(leaseTypeCode);
            return entry;
        }).collect(Collectors.toList());
        cq2PaymentVO.setEntry(entryList);
        //这里是资金端保证金明细
        cq2PaymentVO.setSource(ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name());
        cq2PaymentVO.setBusinessKey(fundReceiptRepayCashDeposit.getCashFlowCode());
        cq2PaymentVO.setBusinessTitle(fundReceiptRepayCashDeposit.getCashFlowCode());
        return cq2PaymentVO;
    }

    private CQ2CollectionVO buildCQ2CollectionVO(String bankFlowNo, List<FundReceiptFlowDetail> detailList) {
        Set<Long> fundReceiptRepayIds = detailList.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toSet());
        List<FundReceiptRepayBaseInfo> repayBaseInfoList = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).listByIds(fundReceiptRepayIds);
        Map<Long, FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoMap = repayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e));
        // 理论上有可能会存在一条流水核销两种收款的情况，和产品沟通就随便取一个类型吧
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = fundReceiptRepayBaseInfoMap.get(detailList.get(0).getReceiptRepayId());
        CQ2CollectionVO vo = new CQ2CollectionVO();
        vo.setSourcebillnumber(bankFlowNo);
        vo.setCico_srcbillno(fundReceiptRepayBaseInfo.getReceiptRepayCode() + "-" + UUIDUtil.genUuid());
//        vo.setBizdate(detailList.get(0).getCashFlowDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        vo.setBizdate(LocalDateTimeUtil.format(this.ensureTargetDate(Collections.singletonList(bankFlowNo)), DatePattern.NORM_DATE_PATTERN));
        vo.setPayertype(FinancialConstants.BD_SUPPLIER);
        //vo.setItempayertype(vo.getPayertype());
        vo.setPayername(FinancialConstants.RZZL_NAME);
        vo.setPayernumber(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
        vo.setCico_srcsystem(FinancialConstants.RZY);
        vo.setTxt_description("资金端-收款：" + ensureOrgName(fundReceiptRepayBaseInfo) + Util.toWanYuanWithoutSplit(fundReceiptRepayBaseInfo.getFinancingAmount()) + "万元");
        vo.setCico_relateddepartments_number(FinancialConstants.RZZL_ZJGLB_CODE);
        List<CQ2CollectionVO.CQ2CollectionVOBody> bodyList = detailList.stream().map(detail -> {
            // 实际场景detail不会很多，如果有查询DB就先循环查询DB，有问题再改吧
            CQ2CollectionVO.CQ2CollectionVOBody body = vo.new CQ2CollectionVOBody();
            body.setE_receivableamt(BigDecimal.valueOf(Optional.ofNullable(detail.getTotalAmount()).orElse(0L)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP));
            body.setE_receivablelocamt(body.getE_receivableamt());
            body.setE_settleorg_number(FinancialConstants.RZZL_CODE);
            body.setCico_customerfield_number(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
            CQCollectionTypeENUM cqCollectionTypeENUM = null;
            if (Objects.equals(detail.getCashFlowItem(), FinanceCashFlowItemEnum.FINANCE_FUND.name())) {
                // 融资款
                cqCollectionTypeENUM = ensureCollectionType(fundReceiptRepayBaseInfoMap.get(detail.getReceiptRepayId()));
                body.setE_fundflowitem_number(cqCollectionTypeENUM.getChannelCode());
            } else if (Objects.equals(detail.getCashFlowItem(), FinanceCashFlowItemEnum.DEPOSIT_RETURN.name())) {
                // 保证金退款
                cqCollectionTypeENUM = CQCollectionTypeENUM.SCENARIO9;
                body.setE_fundflowitem_number(cqCollectionTypeENUM.getChannelCode());
            } else {
                log.error("未定义的资金端收款现金流类型[{}]", JSONUtil.toJsonStr(detail));
            }
            if(ObjectUtil.isEmpty(vo.getReceivingtype_number())) {
                vo.setReceivingtype_number(cqCollectionTypeENUM.getCode());
            }
            return body;
        }).collect(Collectors.toList());
        // 取第一条明细的类型吧
        vo.setEntry(bodyList);
        //这里是融资端收款，存放流水信息，可管理改收款下所有信息
        if (Objects.equals(detailList.get(0).getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.REPAY.name()) || Objects.equals(detailList.get(0).getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.FINANCE_FUND.name())) {
            vo.setSource(ExceptionSourceENUM.FINANCE_SIDE.name());
        } else if (Objects.equals(detailList.get(0).getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_PAYMENT.name()) || Objects.equals(detailList.get(0).getCashFlowItem(), FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name())) {
            vo.setSource(ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name());
        } else {
            vo.setSource(ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name());
        }
        vo.setBusinessKey(bankFlowNo);
        vo.setBusinessTitle(bankFlowNo);
        //补充信息
        vo.setAccountbank_number(detailList.get(0).getOurAccountNumber());
        vo.setSettletype_number(Optional.ofNullable(CQPaymentMethodENUM.ofDisplay(detailList.get(0).getSettleMethod())).map(CQPaymentMethodENUM::name).orElse(detailList.get(0).getSettleMethod()));
        return vo;
    }

    private CQCollectionTypeENUM ensureCollectionType(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo) {
        if (StrUtil.isBlank(fundReceiptRepayBaseInfo.getFinancingType())) {
            FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            FundFinancingTimeLimitTypeEnum fundFinancingTimeLimitTypeEnum = FundFinancingTimeLimitTypeEnum.find(financingBaseInfo.getTimeLimitType());
            if (Objects.isNull(fundFinancingTimeLimitTypeEnum)) {
                throw new MithrasException("未定义的期限类型[间融]");
            }
            List<FundOrganization> fundOrganizationList = SpringUtil.getBean(FundOrganizationService.class).getByFinancingId(financingBaseInfo.getId());
            FundOrganization fundOrganization = fundOrganizationList.get(0);
            if (fundFinancingTimeLimitTypeEnum == FundFinancingTimeLimitTypeEnum.LONG_TERM_LOAN) {
                if (Objects.equals(fundOrganization.getOrganizationType(), OrganizationType.BANK.name())) {
                    return CQCollectionTypeENUM.SCENARIO7;
                } else {
                    return CQCollectionTypeENUM.SCENARIO8;
                }
            }
            if (Objects.equals(fundOrganization.getOrganizationType(), OrganizationType.BANK.name())) {
                return CQCollectionTypeENUM.SCENARIO5;
            } else {
                return CQCollectionTypeENUM.SCENARIO6;
            }
        } else {
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            DirectFinancingType directFinancingType = DirectFinancingType.findByName(fundDirectFinancingBaseInfo.getDirectFinancingType());
            if (Objects.isNull(directFinancingType)) {
                throw new MithrasException("未定义的业务类型[直融]");
            }
            switch (directFinancingType) {
                case CP:
                case SCP: {
                    return CQCollectionTypeENUM.SCENARI14;
                }
                case ABS: {
                    return CQCollectionTypeENUM.SCENARI11;
                }
                case MTN: {
                    return CQCollectionTypeENUM.SCENARI12;
                }
                case PRIVATE_BOND: {
                    return CQCollectionTypeENUM.SCENARI13;
                }
                default: {
                    return CQCollectionTypeENUM.SCENARIO7;
                }
            }
        }
    }
    /**
     * 根据收付款  判断业务类型
     * 20251224调整逻辑 融租 对应的关联合同明细，
     * 不为空且均为直租合同 传输048-融资租赁；
     * 否则传输051-售后回租。
     * @param fundReceiptRepayBaseInfo 收付款
     */
    private String getBussnessType(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo) {
        //融资Id
        Long financingId = fundReceiptRepayBaseInfo.getFinancingId();
        //空就是间融
        if (StrUtil.isBlank(fundReceiptRepayBaseInfo.getFinancingType())) {
           // FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(financingId);
            //查询关联合同明细
            List<FundFinancingPledgeInfo> pledgeInfoList = SpringContextHolder.getBean(FundFinancingPledgeInfoService.class)
                    .list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                            .eq(FundFinancingPledgeInfo::getFinancingId, financingId));
            if (CollectionUtil.isEmpty(pledgeInfoList)) {
                return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
            }
            List<Long> contractIdList = pledgeInfoList.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList());
            List<ContractBaseInfo> contractBaseInfos = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getId, contractIdList));
            if (CollectionUtil.isEmpty(contractBaseInfos)) {
                return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
            }
            //筛选出 直租合同
            List<ContractBaseInfo> reList = contractBaseInfos.stream().filter(x -> ProjectBizType.ZL.name().equals(x.getBizType())
                    && Objects.equals(LeaseType.zhi_zu.name(),x.getLeaseType())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(reList) || reList.size()!= contractBaseInfos.size()){
                return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
            }
            return CQBusinessTypeENUM.FINANCE_LEASING.getCode();
        }
        //直融处理
        //FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(financingId);
        //查询关联合同明细
        List<FundDirectFinancingPledgeInfo> pledgeInfoList = SpringContextHolder.getBean(FundDirectFinancingPledgeInfoService.class)
                .list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .eq(FundDirectFinancingPledgeInfo::getFinancingId, financingId));
        if (CollectionUtil.isEmpty(pledgeInfoList)) {
            return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
        }
        List<Long> contractIdList = pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList());
        List<ContractBaseInfo> contractBaseInfos = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractIdList));
        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
        }
        //筛选出 直租合同
        List<ContractBaseInfo> reList = contractBaseInfos.stream().filter(x -> ProjectBizType.ZL.name().equals(x.getBizType())
                && Objects.equals(LeaseType.zhi_zu.name(),x.getLeaseType())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(reList) || reList.size()!= contractBaseInfos.size()){
            return CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
        }
        return CQBusinessTypeENUM.FINANCE_LEASING.getCode();
    }

    private LocalDateTime ensureTargetDate(List<FundReceiptFlowDetail> detailList) {
        Set<String> bankFlowNos = detailList.stream().map(FundReceiptFlowDetail::getBankFlowNo).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(bankFlowNos)) {
            return null;
        }
        return this.ensureTargetDate(bankFlowNos);
    }

    private LocalDateTime ensureTargetDate(Collection<String> billNoList) {
        List<FinanceFlowRecord> financeFlowRecordList = SpringUtil.getBean(FinanceFlowRecordService.class)
                .list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                        .in(FinanceFlowRecord::getBillno, billNoList)
                        .eq(FinanceFlowRecord::getLogicDeleteFlag, YesOrNoNumberEnum.NO.getCode())
                );
        if (CollectionUtil.isEmpty(financeFlowRecordList)) {
            return null;
        }
        financeFlowRecordList.sort(Comparator.comparing(FinanceFlowRecord::getBiztime));
        return financeFlowRecordList.get(0).getBiztime();
    }

    private String ensureOrgName(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo) {
        if (StrUtil.isBlank(fundReceiptRepayBaseInfo.getFinancingType())) {
            List<FundOrganization> fundOrganizationList = SpringUtil.getBean(FundOrganizationService.class).getByFinancingId(fundReceiptRepayBaseInfo.getFinancingId());
            return Optional.ofNullable(fundOrganizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null);
        } else {
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            return fundDirectFinancingBaseInfo.getProductName();
        }
    }

    private CQPaymentTypeENUM ensureCQPaymentType(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo, boolean isPrincipal) {
        if (StrUtil.isBlank(fundReceiptRepayBaseInfo.getFinancingType())) {
            FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            FundFinancingTimeLimitTypeEnum fundFinancingTimeLimitTypeEnum = FundFinancingTimeLimitTypeEnum.find(financingBaseInfo.getTimeLimitType());
            if (Objects.isNull(fundFinancingTimeLimitTypeEnum)) {
                throw new MithrasException("未定义的期限类型[间融]");
            }
            if (isPrincipal) {
                switch (fundFinancingTimeLimitTypeEnum) {
                    case SHORT_TERM_LOAN: {
                        return CQPaymentTypeENUM.FK09_001;
                    }
                    case LONG_TERM_LOAN: {
                        return CQPaymentTypeENUM.FK09_007;
                    }
                    default: {
                        throw new MithrasException("非法的期限类型");
                    }
                }
            } else {
                switch (fundFinancingTimeLimitTypeEnum) {
                    case SHORT_TERM_LOAN: {
                        return CQPaymentTypeENUM.FK10_001;
                    }
                    case LONG_TERM_LOAN: {
                        return CQPaymentTypeENUM.FK10_004;
                    }
                    default: {
                        throw new MithrasException("无法处理的期限类型");
                    }
                }
            }
        } else {
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            DirectFinancingType directFinancingType = DirectFinancingType.findByName(fundDirectFinancingBaseInfo.getDirectFinancingType());
            if (Objects.isNull(directFinancingType)) {
                throw new MithrasException("未定义的业务类型[直融]");
            }
            if (isPrincipal) {
                switch (directFinancingType) {
                    case PRIVATE_BOND: {
                        return CQPaymentTypeENUM.FK09_012;
                    }
                    case MTN: {
                        return CQPaymentTypeENUM.FK09_014;
                    }
                    case CP:
                    case SCP: {
                        return CQPaymentTypeENUM.FK09_001;
                    }
                    case ABS:
                    case ABN: {
                        return CQPaymentTypeENUM.FK09_007;
                    }
                    default: {
                        throw new MithrasException("无法处理的业务类型");
                    }
                }
            } else {
                switch (directFinancingType) {
                    case PRIVATE_BOND: {
                        return CQPaymentTypeENUM.FK10_009;
                    }
                    case MTN: {
                        return CQPaymentTypeENUM.FK10_011;
                    }
                    case CP:
                    case SCP: {
                        return CQPaymentTypeENUM.FK10_001;
                    }
                    case ABS:
                    case ABN: {
                        return CQPaymentTypeENUM.FK10_004;
                    }
                    default: {
                        throw new MithrasException("无法处理的业务类型");
                    }
                }
            }
        }
    }

    /**
     * 列表导出
     */
    public void exportExcel(ServletOutputStream outputStream, BusinessFlowFinanceListREQ req) {
        req.setPageSize(99999);
        Page<FundPlanFlowResultDTO> pageResult = new Page<>();
        if (Objects.equals(req.getFlowType(), BusinessFlowFinanceListREQ.FLOW_TYPE_PAY)) {
            Page<FundReceiptRepayBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            FundPlanFlowQueryDTO conditionQuery = this.buildQuery(req);
            pageResult = fundReceiptRepayBaseInfoMapper.paymentPageList(pageQuery, conditionQuery);
            if (CollectionUtil.isEmpty(pageResult.getRecords())) {
                flowFundPaymentExporter.exportExcel(Collections.emptyList(), outputStream);
            }
        } else if (Objects.equals(req.getFlowType(), BusinessFlowFinanceListREQ.FLOW_TYPE_COLLECT)) {
            // 查询收款
            Page<FundReceiptRepayBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
            FundPlanFlowQueryDTO conditionQuery = this.buildQuery(req);
            pageResult = fundReceiptRepayBaseInfoMapper.collectPageList(pageQuery, conditionQuery);
            if (CollectionUtil.isEmpty(pageResult.getRecords())) {
                flowFundCollectExporter.exportExcel(Collections.emptyList(), outputStream);
            }
        } else {
            throw new MithrasException("未定义的业务流水类型");
        }

        List<BusinessFlowFinanceListRSP> rspList = getRspList(pageResult);
        if (Objects.equals(req.getFlowType(), BusinessFlowFinanceListREQ.FLOW_TYPE_PAY)) {
            //组装数据
            List<BusinessFlowFundPaymentModel> paymentModels = buildExcelPaymentModels(rspList);
            flowFundPaymentExporter.exportExcel(paymentModels, outputStream);
        } else {
            List<BusinessFlowFundCollectModel> collectModels = buildExcelCollectModels(rspList);
            flowFundCollectExporter.exportExcel(collectModels, outputStream);
        }
    }

    // 同页面查询逻辑 但是不分页
    private  List<BusinessFlowFinanceListRSP> getRspList(Page<FundPlanFlowResultDTO> pageResult){
        // 查询实际核销记录
        Set<String> cashFlowCodes = pageResult.getRecords().stream().map(FundPlanFlowResultDTO::getCashFlowCode).collect(Collectors.toSet());
        List<FundReceiptFlowDetail> detailList = fundReceiptFlowDetailService.listByCashFlowCodes(cashFlowCodes);
        Map<String, List<FundReceiptFlowDetail>> actualAmountMap = detailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        List<BusinessFlowFinanceListRSP> resultList = pageResult.getRecords().stream().map(e -> {
            BusinessFlowFinanceListRSP rsp = new BusinessFlowFinanceListRSP();
            rsp.setIdKey(e.getIdentifier());
            rsp.setReceiptRepayId(e.getReceiptRepayBaseId());
            rsp.setBusinessType(this.transformBizType(e.getIsDirect(), e.getBizType()));
            rsp.setCashFlowItem(e.getCashFlowItem());
            rsp.setCashFlowItemDisplay(this.transformCashFlowItem(e.getCashFlowItem()));
            rsp.setSerialNo(e.getCashFlowCode());
            if (Objects.nonNull(e.getPlanCashFlowDate())) {
                rsp.setDate(LocalDateTimeUtil.format(e.getPlanCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setFinancingCode(e.getFinancingCode());
            rsp.setFinancingRoute(e.getFundChannel());
            rsp.setAmount(e.getPlanCashFlowAmount());
            rsp.setPrincipalAmount(e.getPlanPrincipalAmount());
            rsp.setInterestAmount(e.getPlanInterestAmount());
            List<FundReceiptFlowDetail> actualDetailList = actualAmountMap.get(e.getCashFlowCode());
            if (CollectionUtil.isEmpty(actualDetailList)) {
                rsp.setActualVerifyAmount(0L);
                rsp.setActualVerifyPrincipalAmount(0L);
                rsp.setActualVerifyInterestAmount(0L);
            } else {
                rsp.setCashFlowDate(actualDetailList.stream().map(FundReceiptFlowDetail::getCashFlowDate).max(LocalDate::compareTo).get());
                rsp.setActualVerifyAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getTotalAmount())).mapToLong(FundReceiptFlowDetail::getTotalAmount).sum());
                rsp.setActualVerifyPrincipalAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum());
                rsp.setActualVerifyInterestAmount(actualDetailList.stream().filter(item -> Objects.nonNull(item.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum());
            }
            rsp.setWriteOffStatus(e.getWriteOffState());
            rsp.setWriteOffStatusDisplay(Optional.ofNullable(CashFlowState.of(e.getWriteOffState())).map(CashFlowState::display).orElse("未知"));
            rsp.setFinancingAmount(e.getFinancingAmount());
            rsp.setFinancingId(e.getFinancingId());
            rsp.setPhase(e.getPhase());
            return rsp;
        }).collect(Collectors.toList());
        return resultList;
    }

    //付款导出-数据转换
    private  List<BusinessFlowFundPaymentModel> buildExcelPaymentModels(List<BusinessFlowFinanceListRSP> list ){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        List<BusinessFlowFundPaymentModel> resultList = new ArrayList<>(list.size());
        for (BusinessFlowFinanceListRSP rsp : list) {
            BusinessFlowFundPaymentModel paymentModel = new BusinessFlowFundPaymentModel();
            paymentModel.setWriteOffStatusDisplay(rsp.getWriteOffStatusDisplay());
            paymentModel.setFinancingRoute(rsp.getFinancingRoute());
            paymentModel.setFinancingCode(rsp.getFinancingCode());
            paymentModel.setPhase(rsp.getPhase());
            paymentModel.setCashFlowItemDisplay(rsp.getCashFlowItemDisplay());
            paymentModel.setDate(rsp.getDate());
            if (Objects.nonNull(rsp.getAmount())) {
                //金额装换
                paymentModel.setAmount(BigDecimalUtil.li2Yuan(rsp.getAmount()));
            }
            if (Objects.nonNull(rsp.getPrincipalAmount())) {
                paymentModel.setPrincipalAmount(BigDecimalUtil.li2Yuan(rsp.getPrincipalAmount()));
            }
            if (Objects.nonNull(rsp.getInterestAmount())) {
                paymentModel.setInterestAmount(BigDecimalUtil.li2Yuan(rsp.getInterestAmount()));
            }

            if (Objects.nonNull(rsp.getActualVerifyAmount())) {
                paymentModel.setActualVerifyAmount(BigDecimalUtil.li2Yuan(rsp.getActualVerifyAmount()));
            }

            if (Objects.nonNull(rsp.getActualVerifyPrincipalAmount())) {
                paymentModel.setActualVerifyPrincipalAmount(BigDecimalUtil.li2Yuan(rsp.getActualVerifyPrincipalAmount()));
            }
            if (Objects.nonNull(rsp.getActualVerifyPrincipalAmount())) {
                paymentModel.setActualVerifyInterestAmount(BigDecimalUtil.li2Yuan(rsp.getActualVerifyInterestAmount()));
            }
            paymentModel.setCashFlowDate(rsp.getCashFlowDate());
            resultList.add(paymentModel);
        }



        return resultList;
    }

    //收款导出 数据转换
    private  List<BusinessFlowFundCollectModel> buildExcelCollectModels(List<BusinessFlowFinanceListRSP> list ){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        List<BusinessFlowFundCollectModel> resultList = new ArrayList<>(list.size());
        for (BusinessFlowFinanceListRSP rsp : list) {
            BusinessFlowFundCollectModel collectModel = new BusinessFlowFundCollectModel();
            collectModel.setWriteOffStatusDisplay(rsp.getWriteOffStatusDisplay());
            collectModel.setFinancingRoute(rsp.getFinancingRoute());
            collectModel.setFinancingCode(rsp.getFinancingCode());
            collectModel.setCashFlowItemDisplay(rsp.getCashFlowItemDisplay());
            collectModel.setDate(rsp.getDate());
            if (Objects.nonNull(rsp.getActualVerifyAmount())) {
                collectModel.setActualVerifyAmount(BigDecimalUtil.li2Yuan(rsp.getActualVerifyAmount()));
            }
            collectModel.setCashFlowDate(rsp.getCashFlowDate());
            resultList.add(collectModel);
        }
        return resultList;
    }

}
