package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.SpecialTradeTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrSpecialTradeDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrSpecialTrade;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.formal.CrSpecialTradeService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.report.service.draft.CrSpecialTradeDraftService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractSpecialTradeMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSpecialTrade;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * 征信报送-特定交易表
 *
 * @author wangchuanhao
 * @date 2022/10/9 2:49 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrSpecialTradeHandler extends CrAbstractHandler<CrSpecialTradeDraft, CrSpecialTrade> {

    @Resource
    private ContractSpecialTradeMapper contractSpecialTradeMapper;
    @Resource
    private CrAccountDraftService accountDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.SPECIAL_TRADE;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<ContractSpecialTrade> contractSpecialTradeList = contractSpecialTradeMapper.selectList(Wrappers.<ContractSpecialTrade>lambdaQuery()
                .gt(ContractSpecialTrade::getCreateTime, lastDealTime)
                .le(ContractSpecialTrade::getCreateTime, dealTime)
        );
        Set<Long> contractIdSet = contractSpecialTradeList.stream().map(ContractSpecialTrade::getContractId).collect(Collectors.toSet());
        Map<Long, Boolean> contractReportMap = reportDataRepository.contractReportMap(contractIdSet);
        Map<Long, Boolean> receiptMultiPaymentNeedReportSubTableMap = reportDataRepository.receiptMultiPaymentNeedReportSubTableMap(contractSpecialTradeList.stream().map(ContractSpecialTrade::getPaymentId).collect(Collectors.toList()));

        contractSpecialTradeList = contractSpecialTradeList.stream()
                // 过滤掉展期月数为0的数据
                .filter(c -> Objects.nonNull(c.getChangeMonthCount()) && c.getChangeMonthCount() > 0)
                // 过滤掉合同主承租人不报送的数据
                .filter(c -> Boolean.TRUE.equals(contractReportMap.get(c.getContractId())))
                // 不用过滤掉没关联到解决的数据 展期或者提前结清一定是有租金表的
                // 过滤掉单借据多付款不报送的借据
                .filter(c -> Boolean.TRUE.equals(receiptMultiPaymentNeedReportSubTableMap.get(c.getPaymentId()))).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(contractSpecialTradeList)) {
            List<CrSpecialTradeDraft> specialTradeDraftList = new ArrayList<>();
            for (ContractSpecialTrade c : contractSpecialTradeList) {
                if (SpecialTradeTypeEnum.SETTLE_IN_ADVANCE.name().equals(c.getType())) {
                    //这里提前结清的特定交易暂时不处理
                    continue;
                }
                //之前只有一个账户，现在账户可能拆分了，需要按照账户拆分
                List<CrAccountDraft> list = getBean(CrAccountDraftService.class).list(Wrappers.<CrAccountDraft>lambdaQuery()
                        .eq(CrAccountDraft::getContractId, c.getContractId())
                        .eq(CrAccountDraft::getPaymentId, c.getPaymentId()));
                BigDecimal sum = BigDecimal.valueOf(list.stream().mapToLong(CrAccountDraft::getPaymentAmount).sum());
                BigDecimal total = BigDecimal.valueOf(c.getTradeAmount());
                for (CrAccountDraft accountDraft : list) {
                    BigDecimal divisor = BigDecimal.valueOf(accountDraft.getPaymentAmount());
                    CrSpecialTradeDraft crSpecialTrade = CrSpecialTradeDraft.builder().build();
                    crSpecialTrade.setReportFlag(YesOrNoNumberEnum.YES.getCode())
                            .setReportState(ReportState.TO_BE_REPORT.name())
                            .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                            .setProcBusinessKey(null)
                            .setPaymentApplyCode(accountDraft.getPaymentApplyCode())
                            .setPaymentId(c.getPaymentId())
                            .setTradeType(SpecialTradeTypeEnum.convert(c.getType()))
                            .setTradeDate(c.getTradeDate())
                            .setTradeAmount(Util.mithrasLongDecimalTwo(divisor.divide(sum, 20, RoundingMode.HALF_UP).multiply(total).longValue()))
                            .setChangeMonthCount(c.getChangeMonthCount());

                    crSpecialTrade.setBusinessKey(crSpecialTrade.genBusinessKey(c.getId()));
                    crSpecialTrade.setContractId(c.getContractId());
                    //用于判断特殊交易的日期不能再真实结清日期之后
                    LocalDate closedDate = accountDraft.getClosedDate();
                    if (null != closedDate && closedDate.isBefore(crSpecialTrade.getTradeDate())) {
                        crSpecialTrade.setTradeDate(closedDate);
                    }
                    specialTradeDraftList.add(crSpecialTrade);
                }
            }
            draftService.saveBatch(specialTradeDraftList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //处理提前结清的特殊交易
        List<CrAccountDraft> accountDrafts = accountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .ge(CrAccountDraft::getUpdateTime, lastDealTime)
                .isNotNull(CrAccountDraft::getClosedDate));
        if (CollUtil.isEmpty(accountDrafts)) {
            return;
        }
        List<ContractSpecialTrade> contractSpecialTrades = contractSpecialTradeMapper.selectList(Wrappers.<ContractSpecialTrade>lambdaQuery()
                .in(ContractSpecialTrade::getContractId, accountDrafts.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()))
                        .eq(ContractSpecialTrade::getType, SpecialTradeTypeEnum.SETTLE_IN_ADVANCE.name())
                .gt(ContractSpecialTrade::getChangeMonthCount, 0));

        //如果不为空，说明需要报送特殊交易
        if (CollUtil.isNotEmpty(contractSpecialTrades)) {
            List<CrSpecialTradeDraft> needInsertList = new ArrayList<>(accountDrafts.size());
            Map<Long, List<CrAccountDraft>> listMap = accountDrafts.stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));
            Map<Long, ContractSpecialTrade> tradeMap = contractSpecialTrades.stream().collect(Collectors.toMap(ContractSpecialTrade::getContractId, Function.identity(), (k1, k2) -> k1));
            accountDrafts.forEach(account -> {
                ContractSpecialTrade specialTrade = tradeMap.get(account.getContractId());
                if (Objects.nonNull(specialTrade)) {
                    List<CrAccountDraft> accountDraftList = listMap.get(account.getPaymentId());
                    if (CollUtil.isEmpty(accountDraftList)) {
                        return;
                    }
                    BigDecimal sum = BigDecimal.valueOf(accountDraftList.stream().mapToLong(CrAccountDraft::getPaymentAmount).sum());
                    BigDecimal divisor = BigDecimal.valueOf(account.getPaymentAmount());
                    CrSpecialTradeDraft crSpecialTrade = new CrSpecialTradeDraft();
                    crSpecialTrade.setReportFlag(YesOrNoNumberEnum.YES.getCode())
                            .setReportState(ReportState.TO_BE_REPORT.name())
                            .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                            .setPaymentApplyCode(account.getPaymentApplyCode())
                            .setPaymentId(specialTrade.getPaymentId())
                            .setTradeType(SpecialTradeTypeEnum.convert(specialTrade.getType()))
                            .setTradeDate(account.getClosedDate())
                            .setTradeAmount(Util.mithrasLongDecimalTwo(divisor.divide(sum, 20, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(specialTrade.getTradeAmount())).longValue()))
                            .setChangeMonthCount(specialTrade.getChangeMonthCount());
                    crSpecialTrade.setBusinessKey(crSpecialTrade.genBusinessKey(specialTrade.getId()));
                    crSpecialTrade.setContractId(specialTrade.getContractId());
                    needInsertList.add(crSpecialTrade);
                }
            });

            //入库
            if (CollUtil.isNotEmpty(needInsertList)) {
                //过滤一下已经报送过的数据
                List<CrSpecialTrade> list = SpringContextHolder.getBean(CrSpecialTradeService.class).list(Wrappers.<CrSpecialTrade>lambdaQuery()
                        .in(CrSpecialTrade::getPaymentApplyCode, needInsertList.stream().map(CrSpecialTradeDraft::getPaymentApplyCode).collect(Collectors.toList()))
                        .eq(CrSpecialTrade::getTradeType, SpecialTradeTypeEnum.SETTLE_IN_ADVANCE.getValue()));
                List<CrSpecialTradeDraft> draftList = SpringContextHolder.getBean(CrSpecialTradeDraftService.class).list(Wrappers.<CrSpecialTradeDraft>lambdaQuery()
                        .in(CrSpecialTradeDraft::getPaymentApplyCode, needInsertList.stream().map(CrSpecialTradeDraft::getPaymentApplyCode).collect(Collectors.toList()))
                        .eq(CrSpecialTradeDraft::getTradeType, SpecialTradeTypeEnum.SETTLE_IN_ADVANCE.getValue()));
                if (CollUtil.isEmpty(list) && CollUtil.isEmpty(draftList)) {
                    SpringContextHolder.getBean(CrSpecialTradeDraftService.class).saveBatch(needInsertList);
                } else {
                    List<String> paymentApplyCode = list.stream().map(CrSpecialTrade::getPaymentApplyCode).collect(Collectors.toList());
                    List<String> paymentApplyCodeDraft = draftList.stream().map(CrSpecialTradeDraft::getPaymentApplyCode).collect(Collectors.toList());
                    List<CrSpecialTradeDraft> crSpecialTradeDrafts = needInsertList.stream()
                            .filter(a -> !paymentApplyCode.contains(a.getPaymentApplyCode()))
                            .filter(a -> !paymentApplyCodeDraft.contains(a.getPaymentApplyCode()))
                            .collect(Collectors.toList());
                    SpringContextHolder.getBean(CrSpecialTradeDraftService.class).saveBatch(crSpecialTradeDrafts);
                }
            }
        }
    }

    @Override
    public Integer sort() {
        return 213;
    }
}
