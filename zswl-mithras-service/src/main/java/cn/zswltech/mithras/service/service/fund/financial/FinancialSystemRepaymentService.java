package cn.zswltech.mithras.service.service.fund.financial;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.fund.domain.enums.financial.FundFinancialSystemEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.fund.application.financial.dto.FinancialSystemSubmitQuery;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zswl
 */
@Slf4j
@Service
public class FinancialSystemRepaymentService extends FinancialSystemService{

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;

    private List<String> errorInfoList = new ArrayList<>();


    @Override
    protected FundFinancialSystemEnum getModule() {
        return FundFinancialSystemEnum.REPAYMENT;
    }

    @Override
    protected FinancialSystemSubmitQuery buildRequest() {
        errorInfoList = new ArrayList<>();
        FinancialSystemSubmitQuery result = new FinancialSystemSubmitQuery();
        List<FinancialSystemSubmitQuery.RepayBody> bodyList = new ArrayList<>();
        result.setCwgsHead(getMessageHead());
        result.setCwgsApiAppUser(getAppHead());

        // 处理间融
//        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
//                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
//                .notIn(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name()));
        // 调整取数逻辑，当月结清的也要取出来
        LocalDate now = LocalDate.now();
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.listFinancialSystemTodoList(LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth()));
        if(CollectionUtil.isEmpty(financingBaseInfoList)){
            throw new MithrasException("不存在需要推送的现金流维度数据");
        }
        Map<Long, FundFinancingBaseInfo> financingBaseInfoMap = financingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));
        Map<Long, String> payAccountMap = financingPayAccountService.queryRepayPrincipal(financingBaseInfoMap.keySet());
        Map<Long, List<FundReceiptRepayCashFlow>> cashFlowMap = fundReceiptRepayCashFlowService.queryByFinancingIds(financingBaseInfoMap.keySet(), FinancingTypeEnum.INDIRECT);
        if(CollectionUtil.isEmpty(cashFlowMap)){
            throw new MithrasException("不存在需要推送的现金流维度数据");
        }
        Map<String, List<FundReceiptFlowDetail>> flowDetailMap = fundReceiptFlowDetailService.listByReceiptRepayIds(cashFlowMap.values()
                .stream().flatMap(Collection::stream).map(FundReceiptRepayCashFlow::getReceiptRepayId).collect(Collectors.toList()));

        for (Map.Entry<Long, List<FundReceiptRepayCashFlow>> entry : cashFlowMap.entrySet()) {
            Long financingId = entry.getKey();
            FundFinancingBaseInfo financingBaseInfo = financingBaseInfoMap.get(financingId);
            List<FundReceiptRepayCashFlow> cashFlowList = entry.getValue();
            List<String> errorInfo = new ArrayList<>();
            String payAccount = payAccountMap.get(financingId);
            if(payAccount == null){
                errorInfo.add("未找到融资对应的还本账户");
            }
            if(CollectionUtil.isNotEmpty(errorInfo)) {
                String error = String.format("融资编号:%s -> %s", financingBaseInfo.getFinancingCode(), String.join(",", errorInfo));
                log.warn(error);
                errorInfoList.add(error);
                continue;
            }
            for (FundReceiptRepayCashFlow repayCashFlow : cashFlowList) {
                FinancialSystemSubmitQuery.RepayBody body = new FinancialSystemSubmitQuery.RepayBody();
                List<FundReceiptFlowDetail> flowDetailList = flowDetailMap.get(repayCashFlow.getCashFlowCode());
                long repayTotalAmount = 0L;
                long repayInterestAmount = 0L;
                LocalDate repayDate = null;
                if(CollectionUtil.isNotEmpty(flowDetailList)){
                    repayTotalAmount = flowDetailList.stream().mapToLong(m -> Optional.ofNullable(m.getTotalAmount()).orElse(0L)).sum();
                    repayInterestAmount = flowDetailList.stream().mapToLong(m -> Optional.ofNullable(m.getInterestAmount()).orElse(0L)).sum();
                    for(FundReceiptFlowDetail detail : flowDetailList) {
                        if (ObjectUtil.isNotEmpty(detail) && ObjectUtil.isNotEmpty(detail.getCashFlowDate())) {
                            repayDate = detail.getCashFlowDate();
                        }
                    }
                }
                String JHHKJE;
                String JHHKLX;

                if (ObjectUtil.isNotEmpty(repayTotalAmount) && repayTotalAmount > 0 || (ObjectUtil.isNotEmpty(repayInterestAmount) && repayInterestAmount > 0)) {
                    JHHKJE = Util.toYuanWithoutSplit(repayTotalAmount);
                } else {
                    JHHKJE = Optional.ofNullable(repayCashFlow.getRepayAmount()).map(Util::toYuanWithoutSplit).orElse(null);
                }
                if ((ObjectUtil.isNotEmpty(repayInterestAmount) && repayInterestAmount > 0) || (ObjectUtil.isNotEmpty(repayTotalAmount) && repayTotalAmount > 0)) {
                    JHHKLX = Util.toYuanWithoutSplit(repayInterestAmount);
                } else {
                    JHHKLX = Optional.ofNullable(repayCashFlow.getInterestAmount()).map(Util::toYuanWithoutSplit).orElse(null);
                }

                if (ObjectUtil.isEmpty(repayDate)) {
                    repayDate = repayCashFlow.getRepayDate();
                }
                body.setLAIYUAXT("浙商租赁")
                        .setJIAOYIRQ(Optional.ofNullable(repayDate).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                        .setHETONGBH(financingBaseInfo.getFinancingCode())
                        .setDKJIEJUH(financingBaseInfo.getFinancingCode())
                        .setHUANKZHH(payAccount)
                        .setFKJE(Optional.ofNullable(financingBaseInfo.getFinancingAmount()).map(Util::toYuanWithoutSplit).orElse(null))
                        .setJHHKJE(JHHKJE)
                        .setHKJE(Util.toYuanWithoutSplit(repayTotalAmount))
                        .setJHHKLX(JHHKLX)
                        .setYHLX(Util.toYuanWithoutSplit(repayInterestAmount));
                bodyList.add(body);
            }
        }

        FinancialSystemSubmitQuery.Body body = new FinancialSystemSubmitQuery.Body();
        body.setList(bodyList);
        result.setBody(body);
        return result;

    }

    @Override
    protected String errorInfo() {
        return JSON.toJSONString(errorInfoList);
    }
}
