package cn.zswltech.mithras.application.orchestration.capital;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdMarginRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.BankFlowPaymentCollectionTypeEnum;
import cn.zswltech.mithras.capital.enums.BankFlowWriteOffTypeEnum;
import cn.zswltech.mithras.capital.enums.CollectionWriteOffOrderEnum;
import cn.zswltech.mithras.capital.enums.FinanceCollectionWriteOffOrderEnum;
import cn.zswltech.mithras.capital.enums.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.capital.enums.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.capital.enums.FinanceWriteOffTypeEnum;
import cn.zswltech.mithras.capital.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.capital.enums.PaymentWriteOffOrderEnum;
import cn.zswltech.mithras.capital.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.third.enums.capital.DataSourceEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.enums.*;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.financialshare.enums.CQCollectionTypeENUM;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.*;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.model.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.WarrantyBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.WarrantyRecordInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.third.financialshare.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailUnconfirmedMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.third.financialshare.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.capital.application.writeoff.bo.FinanceFlowWriteOffBO;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingFeeDetailService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.margin.service.WarrantyBaseInfoService;
import cn.zswltech.mithras.margin.service.WarrantyRecordService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentWriteOffService;
import cn.zswltech.mithras.application.orchestration.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinancialExtraService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2CollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PlanCollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.SyncCqReqBizInfo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.B;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yangxiong
 * @date 2024/5/19/16:07
 * @description 自动核销流水
 */
@Slf4j
@Component
public class FinanceFlowAutoWriteOffService {
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FinanceFlowWriteOffDetailMapper financeFlowWriteOffDetailMapper;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private FinancialService financialService;
    @Resource
    private FinancialExtraService financialExtraService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private PaymentWriteOffService paymentWriteOffService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundFinancingFeeDetailService financingFeeDetailService;
    @Resource
    private FundDirectFinancingFeeDetailService directFinancingFeeDetailService;
    @Resource
    private FundReceiptRepayExpenseService receiptRepayExpenseService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private WarrantyRecordService warrantyRecordService;
    @Resource
    private WarrantyBaseInfoService warrantyBaseInfoService;


    @Transactional(rollbackFor = Throwable.class)
    public void handleWriteOff(BankFlowProcessingCenterProjWriteOffREQ req) {
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .in(FinanceFlowRecord::getId, req.getFinanceFlowIds())
                .eq(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode()));
        if (CollUtil.isEmpty(financeFlowRecords) || financeFlowRecords.size() != req.getFinanceFlowIds().size()) {
            log.error("根据流水ID列表：【{}】存在查询不到对应流水信息的ID或者流水正在使用中！", JSONUtil.toJsonStr(req.getFinanceFlowIds()));
            throw new MithrasException("根据流水ID列表：【" + JSONUtil.toJsonStr(req.getFinanceFlowIds()) + "】存在查询不到对应流水信息的ID或者流水正在使用中！");
        }
        //修改流水状态为使用中
        financeFlowRecordService.lambdaUpdate()
                .in(FinanceFlowRecord::getId, financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList()))
                .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.NO.getCode())
                .update();

        FinanceFlowWriteOffBO bo = new FinanceFlowWriteOffBO();
        //设置剩余可核销金额
        handlerSurplusAmount(financeFlowRecords, req.getWriteOffType());
        //将流水按照业务金额进行排序，并且放到队列里面，由于业务比较特殊，金额还可能是负值
        financeFlowRecords.sort(Comparator.comparing(FinanceFlowRecord::getBiztime).thenComparing(FinanceFlowRecord::getSurplusAmount));
        bo.setHandFlowRecordQueue(new LinkedList<>(financeFlowRecords));
        //根据核销方向确定本次的核销金额
        if (BankFlowPaymentCollectionTypeEnum.PAYMENT.name().equals(req.getWriteOffType())) {
            if (BankFlowWriteOffTypeEnum.PROJ_SIDE.name().equals(req.getSideType())) {
                bo.setProjectMap(JSONUtil.toList(req.getListDataJson(), BankCenterSubTableProjectListRSP.class)
                        .stream().collect(Collectors.groupingBy(BankCenterSubTableProjectListRSP::getCashFlowItemName)));
                // 检查一下，如果存在未核销金额为0且本次核销金额大于0的情况，直接抛出异常
                bo.getProjectMap().values().stream().flatMap(Collection::stream).forEach(rsp -> {
                    if (LongUtil.null2zero(rsp.getNoPayAmount()) < LongUtil.null2zero(rsp.getThisWriteOffAmount())) {
                        throw new MithrasException("现金流未核销金额超过未核销金额，操作失败！");
                    }
                });
                //根据枚举sort顺序执行
                Arrays.stream(PaymentWriteOffOrderEnum.values()).sorted(Comparator.comparing(PaymentWriteOffOrderEnum::getSort))
                        .collect(Collectors.toList()).forEach(orderEnum -> {
                            List<BankCenterSubTableProjectListRSP> subTableProjectList = bo.getProjectMap().get(orderEnum.display());
                            if (CollUtil.isEmpty(subTableProjectList)) {
                                return;
                            }
                            //依次进行核销 时间从远至近，金额从小到大，依次排序
                            subTableProjectList.sort(Comparator.comparing(BankCenterSubTableProjectListRSP::getShouldPayTime)
                                    .thenComparing(BankCenterSubTableProjectListRSP::getThisWriteOffAmount));
                            subTableProjectList.forEach(subTableProject -> {
                                boolean flag = true;
                                while (flag) {
                                    FinanceFlowRecord flow = bo.getHandFlowRecordQueue().pollFirst();
                                    if (Objects.isNull(flow)) {
                                        return;
                                    }
                                    long flowSurplusAmount;
                                    if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() != 0) {
                                        flowSurplusAmount = flow.getSurplusAmount();
                                    } else if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() == 0) {
                                        continue;
                                    } else {
                                        flowSurplusAmount = LongUtil.other2Long(String.valueOf(flow.getDebitamount()));
                                        if (flowSurplusAmount == 0) {
                                            flowSurplusAmount = -LongUtil.other2Long(String.valueOf(flow.getCreditamount()));
                                        }
                                    }
                                    bo.getHandWriteOffAmount().set(flowSurplusAmount);
                                    if (bo.getHandWriteOffAmount().get() == 0) {
                                        return;
                                    }
                                    if (orderEnum.equals(PaymentWriteOffOrderEnum.INVESTMENTS_FUNDS)) {
                                        //核销投放
                                        getBean(FinanceFlowAutoWriteOffService.class).writeOffPayment(bo, req.getFinanceFlowIds(), subTableProject, flow);
                                    }
                                    if (orderEnum.equals(PaymentWriteOffOrderEnum.EARNEST_MONEY)) {
                                        //优先核销钱少的
                                        getBean(FinanceFlowAutoWriteOffService.class).writeOffMarginPayment(bo, req.getFinanceFlowIds(), subTableProject, flow);
                                    }
                                    if (orderEnum.equals(PaymentWriteOffOrderEnum.RETENTION_MONEY)) {
                                        //优先核销钱少的
                                        getBean(FinanceFlowAutoWriteOffService.class).writeOffWarrantyPayment(bo, req.getFinanceFlowIds(), subTableProject, flow);
                                    }
                                    flag = false;
                                }
                            });
                        });
            }
            if (BankFlowWriteOffTypeEnum.FUNDS_END.name().equals(req.getSideType())) {
                List<BankCenterSubTableFinanceListRSP> list = JSONUtil.toList(req.getListDataJson(), BankCenterSubTableFinanceListRSP.class);
                // 检查一下，如果存在未核销金额为0且本次核销金额大于0的情况，直接抛出异常
                bo.getFinanceMap().values().stream().flatMap(Collection::stream).forEach(rsp -> {
                    if (LongUtil.null2zero(rsp.getNoPayAmount()) < LongUtil.null2zero(rsp.getThisWriteOffAmount())) {
                        throw new MithrasException("现金流未核销金额超过未核销金额，操作失败！");
                    }
                });
                bo.setFinanceMap(list.stream().collect(Collectors.groupingBy(BankCenterSubTableFinanceListRSP::getCashFlowItemName)));
                //付款
                Arrays.stream(FinancePaymentWriteOffOrderEnum.values()).sorted(Comparator.comparing(FinancePaymentWriteOffOrderEnum::getSort))
                        .collect(Collectors.toList())
                        .forEach(payEnum -> {
                            List<BankCenterSubTableFinanceListRSP> subTableFinanceList = bo.getFinanceMap().get(payEnum.getDisplay());
                            if (CollUtil.isEmpty(subTableFinanceList)) {
                                return;
                            }
                            subTableFinanceList.forEach(finance -> {
                                boolean flag = true;
                                while (flag) {
                                    FinanceFlowRecord flow = bo.getHandFlowRecordQueue().pollFirst();
                                    if (Objects.isNull(flow)) {
                                        return;
                                    }
                                    long flowSurplusAmount;
                                    if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() != 0) {
                                        flowSurplusAmount = flow.getSurplusAmount();
                                    } else if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() == 0) {
                                        continue;
                                    } else {
                                        flowSurplusAmount = LongUtil.other2Long(String.valueOf(flow.getDebitamount()));
                                        if (flowSurplusAmount == 0) {
                                            flowSurplusAmount = -LongUtil.other2Long(String.valueOf(flow.getCreditamount()));
                                        }
                                    }

                                    bo.getHandWriteOffAmount().set(flowSurplusAmount);
                                    if (bo.getHandWriteOffAmount().get() == 0) {
                                        return;
                                    }
                                    //不同付款类型遍历实际核销, 本金利息需要特殊处理
                                    getBean(FinanceFlowAutoWriteOffService.class).financeCollectPayment(flow, bo, finance, payEnum.name(), true);
                                    flag = false;
                                }
                            });
                        });
            }
        }
        if (BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(req.getWriteOffType())) {
            if (BankFlowWriteOffTypeEnum.PROJ_SIDE.name().equals(req.getSideType())) {
                //项目端的手动核销
                bo.setProjectMap(JSONUtil.toList(req.getListDataJson(), BankCenterSubTableProjectListRSP.class)
                        .stream().collect(Collectors.groupingBy(BankCenterSubTableProjectListRSP::getCashFlowItemName)));
                // 检查一下，如果存在未核销金额为0且本次核销金额大于0的情况，直接抛出异常
                bo.getProjectMap().values().stream().flatMap(Collection::stream).forEach(rsp -> {
                    if (LongUtil.null2zero(rsp.getNoPayAmount()) < LongUtil.null2zero(rsp.getThisWriteOffAmount())) {
                        throw new MithrasException("现金流未核销金额超过未核销金额，操作失败！");
                    }
                });
                //根据枚举sort顺序执行
                Arrays.stream(CollectionWriteOffOrderEnum.values()).sorted(Comparator.comparing(CollectionWriteOffOrderEnum::getSort))
                        .collect(Collectors.toList())
                        .forEach(orderEnum -> {
                            List<BankCenterSubTableProjectListRSP> subTableProjectList = bo.getProjectMap().get(orderEnum.getDisplay());
                            if (CollUtil.isEmpty(subTableProjectList)) {
                                return;
                            }
                            subTableProjectList.sort(Comparator.comparing(BankCenterSubTableProjectListRSP::getShouldPayTime)
                                    .thenComparing(BankCenterSubTableProjectListRSP::getThisWriteOffAmount));
                            subTableProjectList.forEach(subTableProject -> {
                                boolean flag = true;
                                while (flag) {
                                    FinanceFlowRecord flow = bo.getHandFlowRecordQueue().pollFirst();
                                    if (Objects.isNull(flow)) {
                                        return;
                                    }
                                    long flowSurplusAmount;
                                    if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() > 0) {
                                        flowSurplusAmount = flow.getSurplusAmount();
                                    } else if (ObjectUtil.isNotEmpty(flow.getSurplusAmount()) && flow.getSurplusAmount() == 0) {
                                        // 没钱可用，直接跳过
                                        continue;
                                    } else {
                                        flowSurplusAmount = LongUtil.other2Long(String.valueOf(flow.getCreditamount()));
                                        if (flowSurplusAmount == 0) {
                                            flowSurplusAmount = -LongUtil.other2Long(String.valueOf(flow.getDebitamount()));
                                        }
                                    }
                                    bo.getHandWriteOffAmount().set(flowSurplusAmount);
                                    //其它的核销走通用核销
                                    getBean(FinanceFlowAutoWriteOffService.class).commonCollectionWriteOff(bo, req.getFinanceFlowIds(), orderEnum, subTableProject, flow);
                                    flag = false;
                                }
                            });
                        });
            }
            if (BankFlowWriteOffTypeEnum.FUNDS_END.name().equals(req.getSideType())) {
                //项目端的手动核销
                bo.setFinanceMap(JSONUtil.toList(req.getListDataJson(), BankCenterSubTableFinanceListRSP.class)
                        .stream().collect(Collectors.groupingBy(BankCenterSubTableFinanceListRSP::getCashFlowItemName)));
                // 检查一下，如果存在未核销金额为0且本次核销金额大于0的情况，直接抛出异常
                bo.getFinanceMap().values().stream().flatMap(Collection::stream).forEach(rsp -> {
                    if (LongUtil.null2zero(rsp.getNoPayAmount()) < LongUtil.null2zero(rsp.getThisWriteOffAmount())) {
                        throw new MithrasException("现金流未核销金额超过未核销金额，操作失败！");
                    }
                });
                //资金端的手动核销
                Arrays.stream(FinanceCollectionWriteOffOrderEnum.values()).sorted(Comparator.comparing(FinanceCollectionWriteOffOrderEnum::getSort))
                        .collect(Collectors.toList())
                        .forEach(collectionEnum -> {
                            //收款直接走通用核销
                            List<BankCenterSubTableFinanceListRSP> subTableFinanceList = bo.getFinanceMap().get(collectionEnum.getDisplay());
                            if (CollUtil.isEmpty(subTableFinanceList)) {
                                return;
                            }
                            subTableFinanceList.forEach(subTableFinance -> {
                                boolean flag = true;
                                while (flag) {
                                    FinanceFlowRecord flow = bo.getHandFlowRecordQueue().pollFirst();
                                    if (Objects.isNull(flow)) {
                                        return;
                                    }
                                    long flowSurplusAmount;
                                    if (ObjectUtil.isNotNull(flow.getSurplusAmount()) && flow.getSurplusAmount() > 0) {
                                        flowSurplusAmount = flow.getSurplusAmount();
                                    } else if (ObjectUtil.isNotEmpty(flow.getSurplusAmount()) && flow.getSurplusAmount() == 0) {
                                        // 没钱可用，直接跳过
                                        continue;
                                    } else {
                                        flowSurplusAmount = LongUtil.other2Long(String.valueOf(flow.getCreditamount()));
                                        if (flowSurplusAmount == 0) {
                                            flowSurplusAmount = -LongUtil.other2Long(String.valueOf(flow.getDebitamount()));
                                        }
                                    }
                                    bo.getHandWriteOffAmount().set(flowSurplusAmount);
                                    if (bo.getHandWriteOffAmount().get() == 0) {
                                        return;
                                    }
                                    getBean(FinanceFlowAutoWriteOffService.class).financeCollectPayment(flow, bo, subTableFinance, collectionEnum.name(), false);
                                    flag = false;
                                }
                            });
                        });
            }
        }
    }

    public void financeCollectPayment(FinanceFlowRecord flow, FinanceFlowWriteOffBO bo,
                                      BankCenterSubTableFinanceListRSP finance, String payEnum, Boolean isPayFlag) {
        long principal = 0;
        long interest = 0;
        //够还
        if (bo.getHandWriteOffAmount().get() > 0 && finance.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > finance.getThisWriteOffAmount()) {
            if (FinancePaymentWriteOffOrderEnum.PRINCIPAL.name().equals(payEnum)) {
                //本金核销
                principal = finance.getThisWriteOffAmount();
            }
            if (FinancePaymentWriteOffOrderEnum.INTEREST.name().equals(payEnum)) {
                //利息核销
                interest = finance.getThisWriteOffAmount();
            }
            //首先实际核销掉金额
            BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setBankDetailNo(flow.getBillno());
            String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum))
                    .map(Enum::name)
                    .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum))
                            .map(Enum::name)
                            .orElse(null));
            financeDetailSaveReq.setCashFlowItem(cashFlowItem);
            financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
            financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
            financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
            financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
            financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            financeDetailSaveReq.setInterestAmount(interest);
            financeDetailSaveReq.setPrincipalAmount(principal);
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setTotalAmount(finance.getThisWriteOffAmount());
            financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
            financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
            getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
            //修改流水状态
            getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, bo.getHandWriteOffAmount().get() - finance.getThisWriteOffAmount(),
                    FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            bo.getHandWriteOffAmount().set(bo.getHandWriteOffAmount().get() - finance.getThisWriteOffAmount());
            finance.setThisWriteOffAmount(0L);
            //这里要把剩余的钱还回队列
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }
        //不够还
        if (bo.getHandWriteOffAmount().get() > 0 && finance.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= finance.getThisWriteOffAmount()) {
            if (FinancePaymentWriteOffOrderEnum.PRINCIPAL.name().equals(payEnum)) {
                //本金核销
                principal = bo.getHandWriteOffAmount().get();
            }
            if (FinancePaymentWriteOffOrderEnum.INTEREST.name().equals(payEnum)) {
                //利息核销
                interest = bo.getHandWriteOffAmount().get();
            }
            //首先实际核销掉金额
            BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setBankDetailNo(flow.getBillno());
            String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum))
                    .map(Enum::name)
                    .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum))
                            .map(Enum::name)
                            .orElse(null));
            financeDetailSaveReq.setCashFlowItem(cashFlowItem);
            financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
            financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
            financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
            financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
            financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            financeDetailSaveReq.setInterestAmount(interest);
            financeDetailSaveReq.setPrincipalAmount(principal);
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setTotalAmount(flow.getSurplusAmount());
            financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
            financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
            getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
            //修改流水状态
            getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, 0L,
                    FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_FUNDS_END);
            finance.setThisWriteOffAmount(finance.getThisWriteOffAmount() - flow.getSurplusAmount());
            //给何老师留口饭吃
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            flow.setSurplusAmount(0L);
            bo.getHandWriteOffAmount().set(0L);
        }
        //负数流水
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (finance.getThisWriteOffAmount() == 0) {
                return;
            }
            //看看是不是负数
            if (bo.getHandWriteOffAmount().get() < 0) {
                //需要将正数流水核销负数流水
                BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
                financeDetailSaveReq.setFinanceFlowId(flow.getId());
                financeDetailSaveReq.setBankDetailNo(flow.getBillno());
                String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum))
                        .map(Enum::name)
                        .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum))
                                .map(Enum::name)
                                .orElse(null));
                financeDetailSaveReq.setCashFlowItem(cashFlowItem);
                financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
                financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
                financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
                financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
                financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
                financeDetailSaveReq.setInterestAmount(interest);
                financeDetailSaveReq.setPrincipalAmount(principal);
                financeDetailSaveReq.setFinanceFlowId(flow.getId());
                financeDetailSaveReq.setTotalAmount(flow.getSurplusAmount());
                financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
                financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
                getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
                //修改流水状态
                getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, 0L,
                        FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_FUNDS_END);
                finance.setThisWriteOffAmount(finance.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                //给何老师留口饭吃
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
                flow.setSurplusAmount(0L);
            }
            //去队列里拿到下一条流水
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (ObjectUtil.isNull(flowRecord)) {
                //银行流水使用完毕
                return;
            }
            long flowSurplusAmount;
            if (ObjectUtil.isNotNull(flowRecord.getSurplusAmount()) && flowRecord.getSurplusAmount() > 0) {
                flowSurplusAmount = flowRecord.getSurplusAmount();
            } else if (isPayFlag) {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(flowRecord.getDebitamount()));
            } else {
                flowSurplusAmount = -LongUtil.other2Long(String.valueOf(flowRecord.getCreditamount()));
            }
            flowRecord.setSurplusAmount(flowSurplusAmount);
            bo.getHandWriteOffAmount().set(flowSurplusAmount);
            //递归
            getBean(FinanceFlowAutoWriteOffService.class).financeCollectPayment(flowRecord, bo, finance, payEnum, isPayFlag);
        }
    }

    /**
     * 核销完毕通知
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void writeOffNotice(List<Long> financeFlowRecordIds) {
        log.info("FinanceFlowAutoWriteOffService writeOffNotice financeFlowRecordIds : {}", financeFlowRecordIds);
        List<FinanceFlowRecord> flowRecords = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .in(FinanceFlowRecord::getId, financeFlowRecordIds)
                .eq(FinanceFlowRecord::getSendCqFlag, YesOrNoNumberEnum.NO.getCode()));
        if (ObjectUtil.isEmpty(flowRecords)) {
            return;
        }
        Map<String, List<FinanceFlowWriteOffDetail>> writeOffDetailMap = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .in(FinanceFlowWriteOffDetail::getFinanceFlowId, financeFlowRecordIds)).stream().collect(Collectors.groupingBy(FinanceFlowWriteOffDetail::getRecordMainTable));
        //获取流水下所有记录，并拼接为CQ2PlanCollectionVO
        //List<CQ2PlanCollectionVO> planCollectionVOs = new ArrayList<>();
        List<CQ2CollectionVO> collectionVOs = new ArrayList<>();
        for (Map.Entry<String, List<FinanceFlowWriteOffDetail>> e : writeOffDetailMap.entrySet()) {
            String recordMainTable = e.getKey();
            List<FinanceFlowWriteOffDetail> writeOffDetails = e.getValue();
            FinanceFlowDetailTableEnum financeFlowDetailTableEnum = FinanceFlowDetailTableEnum.valueOf(recordMainTable);
            List<Long> mainIds = writeOffDetails.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toList());
            switch (financeFlowDetailTableEnum) {
                case COLLECTION_RECORD_INFO:
                    ///planCollectionVOs.addAll(collectionRecordInfoService.getPlanCollectionVO(mainIds));
                    List<CQ2CollectionVO> collectionVO = collectionRecordInfoService.getCollectionVO(mainIds);
                    if (ObjectUtil.isNotEmpty(collectionVO)) {
                        collectionVOs.addAll(collectionVO);
                    }
                    //这里发送应收单
                    List<CQ2PlanCollectionVO> cq2PlanCollectionVOS = collectionRecordInfoService.buildPlanCollectionReqByCollectionRecord(mainIds);
                    flowRecords.forEach(flowRecord -> {
                        this.planCollectionVoDiff(cq2PlanCollectionVOS, flowRecord);
                    });
                    if (ObjectUtil.isNotEmpty(cq2PlanCollectionVOS)) {
                        cq2PlanCollectionVOS.forEach(a -> financialManagerServiceImpl2.planCollectionExec(Collections.singletonList(a)));
                    }
                    break;
                case FUND_RECEIPT_FLOW_DETAIL:
//                    collectionVOs = fundReceiptFlowDetailService.getCollectionVO(mainIds);
                    List<CQ2CollectionVO> collectionVONew = SpringUtil.getBean(BusinessFlowService.class).getCollectionVONew(mainIds);
                    if (ObjectUtil.isNotEmpty(collectionVONew)) {
                        collectionVOs.addAll(collectionVONew);
                    }
                    //推付款给苍穹系统
                    SpringUtil.getBean(BusinessFlowService.class).doNoticeCqPayment(mainIds, flowRecords);
                    break;
                case PAYMENT_ACTUAL_DETAIL:
                    //项目端付款详情表
                    List<CQ2PaymentVO> cq2PaymentVOS = new ArrayList<>();
                    log.info("writeOffNotice PAYMENT_ACTUAL_DETAIL WRITTEN_OFF {}", mainIds);
                    mainIds.forEach(paymentActualId ->
                    {
                        CQ2PaymentVO cq2PaymentVO = paymentWriteOffService.buildPayment(paymentActualId);
                        if (ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            cq2PaymentVOS.add(cq2PaymentVO);
                        }
                    });
                    //这里需要查询是否核销完，否则需要补差额,目前只支持单银行流水
                    flowRecords.forEach(flowRecord -> {
                        this.paymentVoDiff(cq2PaymentVOS, flowRecord);
                    });
                    //付款单
                    if (!cq2PaymentVOS.isEmpty()) {
                        financialManagerServiceImpl2.cq2PaymentExec(new SyncCqReqBizInfo(), cq2PaymentVOS);
                    }
                    break;
                case WARRANTY_RECORD_INFO:
                    //项目端  质保金核销表
                    List<CQ2PaymentVO> cq2PaymentVOWarrantyS = new ArrayList<>();
                    log.info("writeOffNotice WARRANTY_RECORD_INFO WRITTEN_OFF {}", mainIds);
                    mainIds.forEach(warrantyRecordId ->
                    {
                        WarrantyRecordInfo warrantyRecordInfo = warrantyRecordService.getById(warrantyRecordId);
                        WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoService.getById(warrantyRecordInfo.getWarrantyId());
                        CQ2PaymentVO cq2PaymentVO = warrantyRecordService.buildPayment(warrantyBaseInfo, warrantyRecordInfo);
                        if (ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            cq2PaymentVOWarrantyS.add(cq2PaymentVO);
                        }
                    });
                    //付款单
                    if (!cq2PaymentVOWarrantyS.isEmpty()) {
                        financialManagerServiceImpl2.cq2PaymentExec(new SyncCqReqBizInfo(), cq2PaymentVOWarrantyS);
                    }
                    break;
                default:
            }
        }
        if (!collectionVOs.isEmpty()) {
            if (collectionVOs.size() > 1) {
                CQ2CollectionVO cq2CollectionVO = null;
                for (CQ2CollectionVO collectionVO : collectionVOs) {
                    if (cq2CollectionVO == null) {
                        cq2CollectionVO = collectionVO;
                    } else {
                        List<CQ2CollectionVO.CQ2CollectionVOBody> entry = cq2CollectionVO.getEntry();
                        if (ObjectUtil.isEmpty(entry)) {
                            entry = new ArrayList<>();
                        }
                        entry.addAll(collectionVO.getEntry());
                        cq2CollectionVO.setEntry(entry);
                        cq2CollectionVO.setLocalamt((cq2CollectionVO.getLocalamt() == null ? BigDecimal.ZERO : cq2CollectionVO.getLocalamt()).add(collectionVO.getLocalamt() == null ? BigDecimal.ZERO : collectionVO.getLocalamt()));
                        cq2CollectionVO.setActrecamt(cq2CollectionVO.getLocalamt());
                    }
                }
                collectionVOs = Collections.singletonList(cq2CollectionVO);
            }
            for (FinanceFlowRecord flowRecord : flowRecords) {
                this.collectionVoDiff(collectionVOs, flowRecord);
            }
            //这里需要查询是否核销完，否则需要补差额
            financialManagerServiceImpl2.collectionExec(collectionVOs);
            financeFlowRecordService.modifySendFlag(collectionVOs.stream().map(CQ2CollectionVO::getSourcebillnumber).collect(Collectors.toList()), YesOrNoNumberEnum.YES);
        }
    }

    private void collectionVoDiff(List<CQ2CollectionVO> collectionVOs, FinanceFlowRecord flowRecord) {
        if (LongUtil.null2zero(flowRecord.getSurplusAmount()) <= 0 || ObjectUtil.isEmpty(collectionVOs)) {
            return;
        }
        CQ2CollectionVO vo = collectionVOs.get(0);
        List<CQ2CollectionVO.CQ2CollectionVOBody> entry = vo.getEntry();
        if (ObjectUtil.isEmpty(entry)) {
            return;
        }
        CQ2CollectionVO.CQ2CollectionVOBody cq2CollectionVOBody = BeanUtil.copyProperties(entry.get(0), CQ2CollectionVO.CQ2CollectionVOBody.class);
        cq2CollectionVOBody.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(flowRecord.getSurplusAmount()))));
        cq2CollectionVOBody.setE_actamt(cq2CollectionVOBody.getE_receivableamt());
        cq2CollectionVOBody.setE_fundflowitem_number(CQCollectionTypeENUM.SCENARIO3.getChannelCode());
        cq2CollectionVOBody.setCico_purposeoffunds_number(cq2CollectionVOBody.getE_fundflowitem_number());
        entry.add(cq2CollectionVOBody);
    }

    /**
     * 付款单 补充流水未核销金额 预付款？
     */
    private void paymentVoDiff(List<CQ2PaymentVO> cq2PaymentVOS, FinanceFlowRecord flowRecord) {
        if (LongUtil.null2zero(flowRecord.getSurplusAmount()) <= 0 || ObjectUtil.isEmpty(cq2PaymentVOS)) {
            return;
        }
        Map<String, CQ2PaymentVO> billno2CQ2PaymentVO = cq2PaymentVOS.stream().collect(Collectors.toMap(CQ2PaymentVO::getCico_paynum_rby, e -> e, (a, b) -> a));

        CQ2PaymentVO vo = null;
        vo = billno2CQ2PaymentVO.get(flowRecord.getBillno());
        if (ObjectUtil.isEmpty(vo)) {
            cq2PaymentVOS.get(0);
        }
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entry = vo.getEntry();
        if (ObjectUtil.isEmpty(entry)) {
            return;
        }
        CQ2PaymentVO.CQ2PaymentVOEntry cq2CollectionVOBody = BeanUtil.copyProperties(entry.get(0), CQ2PaymentVO.CQ2PaymentVOEntry.class);
        cq2CollectionVOBody.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(flowRecord.getSurplusAmount()))));
        entry.add(cq2CollectionVOBody);
    }

    //补充未核销完毕的金额
    private void planCollectionVoDiff(List<CQ2PlanCollectionVO> collectionVOs, FinanceFlowRecord flowRecord) {
        if (LongUtil.null2zero(flowRecord.getSurplusAmount()) <= 0 || ObjectUtil.isEmpty(collectionVOs)) {
            return;
        }
        CQ2PlanCollectionVO vo = collectionVOs.get(0);
        List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> entry = vo.getEntry();
        if (ObjectUtil.isEmpty(entry)) {
            return;
        }
        CQ2PlanCollectionVO.CQ2PlanCollectionVOBody cq2CollectionVOBody = BeanUtil.copyProperties(entry.get(0), CQ2PlanCollectionVO.CQ2PlanCollectionVOBody.class);
        cq2CollectionVOBody.setE_taxunitprice(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(flowRecord.getSurplusAmount()))));
        cq2CollectionVOBody.setE_recamount(cq2CollectionVOBody.getE_taxunitprice());
        cq2CollectionVOBody.setE_unitprice(cq2CollectionVOBody.getE_taxunitprice().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(cq2CollectionVOBody.getE_taxrate()), 10, RoundingMode.HALF_UP));
        //不含税单价* 税率
        cq2CollectionVOBody.setE_tax(cq2CollectionVOBody.getE_unitprice().multiply(cq2CollectionVOBody.getE_taxrate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        entry.add(cq2CollectionVOBody);
    }

    public void commonCollectionWriteOff(FinanceFlowWriteOffBO bo, List<Long> financeIds, CollectionWriteOffOrderEnum orderEnum, BankCenterSubTableProjectListRSP rsp, FinanceFlowRecord flow) {
        //先判断本次核销金额是否大于本次应收金额
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > rsp.getThisWriteOffAmount()) {
            //调用手动核销的方法
            getBean(FinanceFlowAutoWriteOffService.class).commonCollectionActualWriteOff(orderEnum, rsp, rsp.getThisWriteOffAmount(), flow);
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, bo.getHandWriteOffAmount().get() - rsp.getThisWriteOffAmount(), FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            //更新实际需要核销的钱
            bo.getHandWriteOffAmount().set(bo.getHandWriteOffAmount().get() - rsp.getThisWriteOffAmount());
            rsp.setThisWriteOffAmount(0L);
            //全部核销掉之后，如果当前的待核销金额里面还有钱，则需要将这个钱构建一个流水放到队列尾部
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= rsp.getThisWriteOffAmount()) {
            //然后将此次的流水全部挂到对应的现金流项目
            getBean(FinanceFlowAutoWriteOffService.class).commonCollectionActualWriteOff(orderEnum, rsp, bo.getHandWriteOffAmount().get(), flow);
            //更新流水状态
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
            //不够还，说明该笔流水在这里就已经使用完毕， 需要调用何老师的接口
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            //更新实际需要核销的金额
            rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
            bo.getHandWriteOffAmount().set(0);
        }
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (rsp.getThisWriteOffAmount() == 0) {
                return;
            }
            //不够还
            if (bo.getHandWriteOffAmount().get() < 0) {
                //并且还需要额外的钱来抵消
                getBean(FinanceFlowAutoWriteOffService.class).commonCollectionActualWriteOff(orderEnum, rsp, bo.getHandWriteOffAmount().get(), flow);
                //设置当前的还款金额需要加上
                rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                //更新流水状态
                getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
                //不够还，说明该笔流水在这里就已经使用完毕， 需要调用何老师的接口
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            }

            //去队列里面找到下一条流水, 然后递归核销
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (ObjectUtil.isNull(flowRecord)) {
                log.info("流水ID列表：{}收款手动核销完毕 在【{}】模块截止", JSONUtil.toJsonStr(financeIds), orderEnum.getDisplay());
                return;
            }
            long flowSurplusAmount;
            if (ObjectUtil.isNotNull(flowRecord.getSurplusAmount()) && flowRecord.getSurplusAmount() > 0) {
                flowSurplusAmount = flowRecord.getSurplusAmount();
            } else if (flowRecord.getDebitamount() > 0) {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(-flowRecord.getDebitamount()));
            } else {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(flowRecord.getCreditamount()));
            }
            bo.getHandWriteOffAmount().set(flowSurplusAmount);
            getBean(FinanceFlowAutoWriteOffService.class).commonCollectionWriteOff(bo, financeIds, orderEnum, rsp, flowRecord);
        }
    }

    /**
     * 处理剩余金额的逻辑。
     * 对给定的财务流水记录列表，根据冲销类型（支付或收款），处理相应的金额。
     *
     * @param financeFlowRecords 财务流水记录列表，不可为null或空。
     * @param writeOffType       冲销类型，不可为null。应为BankFlowPaymentCollectionTypeEnum中的PAYMENT或COLLECTION。
     */
    private void handlerSurplusAmount(List<FinanceFlowRecord> financeFlowRecords, String writeOffType) {
        if (financeFlowRecords == null || financeFlowRecords.isEmpty() || writeOffType == null) {
            log.info("Invalid input parameters.");
            return;
        }

        financeFlowRecords.forEach(financeFlowRecord -> {
            if (ObjectUtil.isNotNull(financeFlowRecord) && ObjectUtil.isNotNull(financeFlowRecord.getSurplusAmount()) && financeFlowRecord.getSurplusAmount() > 0) {
                if (BankFlowPaymentCollectionTypeEnum.PAYMENT.name().equals(writeOffType) && ObjectUtil.isNotNull(financeFlowRecord.getCreditamount()) && financeFlowRecord.getCreditamount() != 0) {
                    financeFlowRecord.setSurplusAmount(-financeFlowRecord.getSurplusAmount());
                }
                if (BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(writeOffType) && ObjectUtil.isNotNull(financeFlowRecord.getDebitamount()) && financeFlowRecord.getDebitamount() != 0) {
                    financeFlowRecord.setSurplusAmount(-financeFlowRecord.getSurplusAmount());
                }
                return;
            } else if (ObjectUtil.isNotNull(financeFlowRecord) && ObjectUtil.isNotNull(financeFlowRecord.getSurplusAmount()) && financeFlowRecord.getSurplusAmount() == 0) {
                financeFlowRecord.setSurplusAmount(0L);
                return;
            }
            try {
                if (BankFlowPaymentCollectionTypeEnum.PAYMENT.name().equals(writeOffType)) {
                    //设置本次可核销金额
                    if (ObjectUtil.isNotNull(financeFlowRecord.getDebitamount()) && financeFlowRecord.getDebitamount() != 0) {
                        financeFlowRecord.setSurplusAmount(LongUtil.other2Long(String.valueOf(financeFlowRecord.getDebitamount())));
                    } else {
                        financeFlowRecord.setSurplusAmount(LongUtil.other2Long(String.valueOf(-financeFlowRecord.getCreditamount())));
                    }
                }
                if (BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(writeOffType)) {
                    //设置本次可核销金额
                    if (ObjectUtil.isNotNull(financeFlowRecord.getCreditamount()) && financeFlowRecord.getCreditamount() != 0) {
                        financeFlowRecord.setSurplusAmount(LongUtil.other2Long(String.valueOf(financeFlowRecord.getCreditamount())));
                    } else {
                        financeFlowRecord.setSurplusAmount(LongUtil.other2Long(String.valueOf(-financeFlowRecord.getDebitamount())));
                    }
                }
            } catch (Exception e) {
                log.info("Error processing finance flow record: {}", financeFlowRecord.toString(), e);
            }
        });
    }

    public void writeOffPayment(FinanceFlowWriteOffBO bo, List<Long> financeFlowIds, BankCenterSubTableProjectListRSP rsp, FinanceFlowRecord flow) {
        int index = rsp.getCashFlowCode().lastIndexOf("-");
        PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getPaymentCode, rsp.getCashFlowCode().substring(0, index))
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(paymentBaseInfo)) {
            log.error("现金流编号 = {}的借据对应的付款申请不存在", rsp.getCashFlowCode());
            return;
        }
        Long paymentId = paymentBaseInfo.getId();
        PaymentActualDetailUnconfirmed unconfirmed = getBean(PaymentActualDetailUnconfirmedMapper.class).selectOne(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                .eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId)
                .eq(PaymentActualDetailUnconfirmed::getId, rsp.getPaymentActualDetailId())
                .last(StringUtil.mysqlLimitOne()));
        //先判断本次核销金额是否大于本次应收金额
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).paymentWriteOffCommonMethod(unconfirmed, paymentId, rsp, flow, rsp.getThisWriteOffAmount());
            //本次核销金额大于本次应收金额，本次核销金额减去本次
            bo.getHandWriteOffAmount().set(bo.getHandWriteOffAmount().get() - rsp.getThisWriteOffAmount());
            rsp.setThisWriteOffAmount(0L);
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, bo.getHandWriteOffAmount().get(), FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            //全部核销掉之后，如果当前的待核销金额里面还有钱，则需要将这个钱构建一个流水放到队列尾部
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).paymentWriteOffCommonMethod(unconfirmed, paymentId, rsp, flow, bo.getHandWriteOffAmount().get());
            // 流水核销完毕，置0
            rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
            bo.getHandWriteOffAmount().set(0L);
            //需要更新流水状态、tab类型以及剩余可核销金额
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
            //需要给何老师发送核销完毕的业务流水
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
        }

        //需要处理收款核销付款的情况，也就是可核销金额是负数
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (rsp.getThisWriteOffAmount() == 0) {
                return;
            }
            //调用核销方法
            if (bo.getHandWriteOffAmount().get() < 0) {
                //调用核销方法
                getBean(FinanceFlowAutoWriteOffService.class).paymentWriteOffCommonMethod(unconfirmed, paymentId, rsp, flow, bo.getHandWriteOffAmount().get());
                rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
                //当前流水已经核销完毕，需要调用苍穹的核销接口
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            }
            //去队列找下一条流水
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (Objects.isNull(flowRecord)) {
                bo.getHandWriteOffAmount().set(0);
                log.info("流水ID列表：{}付款手动核销完毕 在【投放款】模块截止", JSONUtil.toJsonStr(financeFlowIds));
                return;
            }
            //继续递归核销
            long flowSurplusAmount;
            if (ObjectUtil.isNotNull(flowRecord.getSurplusAmount()) && flowRecord.getSurplusAmount() > 0) {
                flowSurplusAmount = flowRecord.getSurplusAmount();
            } else if (flowRecord.getCreditamount() > 0) {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(-flowRecord.getCreditamount()));
            } else {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(flowRecord.getDebitamount()));
            }
            bo.getHandWriteOffAmount().set(flowSurplusAmount);
            getBean(FinanceFlowAutoWriteOffService.class).writeOffPayment(bo, financeFlowIds, rsp, flowRecord);
        }
    }

    public void writeOffMarginPayment(FinanceFlowWriteOffBO bo, List<Long> financeFlowIds, BankCenterSubTableProjectListRSP rsp, FinanceFlowRecord flow) {
        //先判断本次核销金额是否大于本次应收金额
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).writeOffMargin(rsp.getCashFlowCode(), rsp.getThisWriteOffAmount(), flow, RecordTypeEnum.REFUND_MARGIN);
            //本次核销金额大于本次应收金额，本次核销金额减去本次
            bo.getHandWriteOffAmount().addAndGet(-rsp.getThisWriteOffAmount());
            rsp.setThisWriteOffAmount(0L);
            //可以核销完，进入下一次循环
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, bo.getHandWriteOffAmount().get() - rsp.getThisWriteOffAmount(),
                    FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            //全部核销掉之后，如果当前的待核销金额里面还有钱，则需要将这个钱构建一个流水放到队列尾部
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).writeOffMargin(rsp.getCashFlowCode(), bo.getHandWriteOffAmount().get(), flow, RecordTypeEnum.REFUND_MARGIN);
            //本次核销金额小于本次应收金额，本次核销金额加上本次
            rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
            bo.getHandWriteOffAmount().set(0);
            //需要更新流水状态、tab类型以及剩余可核销金额
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(),
                    BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
            //需要给何老师发送核销完毕的业务流水
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
        }
        //还需要处理收款核销付款的情况，也就是可核销金额是负数
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (rsp.getThisWriteOffAmount() == 0) {
                return;
            }
            if (bo.getHandWriteOffAmount().get() < 0) {
                getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(),
                        BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
                //调用核销方法
                getBean(FinanceFlowAutoWriteOffService.class).writeOffMargin(rsp.getCashFlowCode(), bo.getHandWriteOffAmount().get(), flow, RecordTypeEnum.REFUND_MARGIN);
                rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                //当前流水已经核销完毕，需要调用苍穹的核销接口
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            }
            //去队列找下一条流水
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (Objects.isNull(flowRecord)) {
                bo.getHandWriteOffAmount().set(0);
                log.info("流水ID列表：{}手动核销完毕 在【保证金】模块截止", JSONUtil.toJsonStr(financeFlowIds));
                return;
            }
            //继续递归核销
            long flowSurplusAmount;
            if (ObjectUtil.isNotNull(flowRecord.getSurplusAmount()) && flowRecord.getSurplusAmount() > 0) {
                flowSurplusAmount = flowRecord.getSurplusAmount();
            } else if (flow.getCreditamount() > 0) {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(-flowRecord.getCreditamount()));
            } else {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(flowRecord.getDebitamount()));
            }
            bo.getHandWriteOffAmount().set(flowSurplusAmount);
            getBean(FinanceFlowAutoWriteOffService.class).writeOffMarginPayment(bo, financeFlowIds, rsp, flowRecord);
        }
    }

    public void writeOffWarrantyPayment(FinanceFlowWriteOffBO bo, List<Long> financeFlowIds, BankCenterSubTableProjectListRSP rsp, FinanceFlowRecord flow) {
        //先判断本次核销金额是否大于本次应收金额
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).writeOffWarranty(rsp.getCashFlowCode(), rsp.getThisWriteOffAmount(), flow, RecordTypeEnum.REFUND_WARRANTY);
            //本次核销金额大于本次应收金额，本次核销金额减去本次
            bo.getHandWriteOffAmount().addAndGet(-rsp.getThisWriteOffAmount());
            rsp.setThisWriteOffAmount(0L);
            //可以核销完，进入下一次循环
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, bo.getHandWriteOffAmount().get() - rsp.getThisWriteOffAmount(),
                    FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            //全部核销掉之后，如果当前的待核销金额里面还有钱，则需要将这个钱构建一个流水放到队列尾部
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }
        if (bo.getHandWriteOffAmount().get() > 0 && rsp.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= rsp.getThisWriteOffAmount()) {
            //调用核销方法
            getBean(FinanceFlowAutoWriteOffService.class).writeOffWarranty(rsp.getCashFlowCode(), bo.getHandWriteOffAmount().get(), flow, RecordTypeEnum.REFUND_WARRANTY);
            //本次核销金额小于本次应收金额，本次核销金额加上本次
            rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
            bo.getHandWriteOffAmount().set(0);
            //需要更新流水状态、tab类型以及剩余可核销金额
            getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(),
                    BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
            //需要给何老师发送核销完毕的业务流水
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
        }
        //还需要处理收款核销付款的情况，也就是可核销金额是负数
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (rsp.getThisWriteOffAmount() == 0) {
                return;
            }
            if (bo.getHandWriteOffAmount().get() < 0) {
                getBean(FinanceFlowAutoWriteOffService.class).updateFinanceFlowRecord(flow, 0L, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(),
                        BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE);
                //调用核销方法
                getBean(FinanceFlowAutoWriteOffService.class).writeOffWarranty(rsp.getCashFlowCode(), bo.getHandWriteOffAmount().get(), flow, RecordTypeEnum.REFUND_WARRANTY);
                rsp.setThisWriteOffAmount(rsp.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                //当前流水已经核销完毕，需要调用苍穹的核销接口
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(flow.getId()));
            }
            //去队列找下一条流水
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (Objects.isNull(flowRecord)) {
                bo.getHandWriteOffAmount().set(0);
                log.info("流水ID列表：{}手动核销完毕 在【质保金】模块截止", JSONUtil.toJsonStr(financeFlowIds));
                return;
            }
            //继续递归核销
            long flowSurplusAmount;
            if (ObjectUtil.isNotNull(flowRecord.getSurplusAmount()) && flowRecord.getSurplusAmount() > 0) {
                flowSurplusAmount = flowRecord.getSurplusAmount();
            } else if (flow.getCreditamount() > 0) {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(-flowRecord.getCreditamount()));
            } else {
                flowSurplusAmount = LongUtil.other2Long(String.valueOf(flowRecord.getDebitamount()));
            }
            bo.getHandWriteOffAmount().set(flowSurplusAmount);
            getBean(FinanceFlowAutoWriteOffService.class).writeOffWarrantyPayment(bo, financeFlowIds, rsp, flowRecord);
        }
    }

    public void updateFinanceFlowRecord(FinanceFlowRecord flow, Long surplusAmount, String writeOffStatus, BankFlowCenterTypeEnum writeOffType) {
        FinanceFlowRecord flowRecord = new FinanceFlowRecord();
        flowRecord.setId(flow.getId());
        flowRecord.setFinancingFlowType(writeOffType.name());
        flowRecord.setFinancingProjectType(BankFlowWriteOffTypeEnum.PROJ_SIDE.name());
        flowRecord.setSurplusAmount(surplusAmount);
        flowRecord.setWriteOffStatus(writeOffStatus);
        Integer sort = Optional.ofNullable(FinancingFlowWriteOffStatusEnum.of(writeOffStatus))
                .map(FinancingFlowWriteOffStatusEnum::getSort).orElse(3);
        flowRecord.setSort(sort);
        flowRecord.setWriteOffType(FinanceWriteOffTypeEnum.HAND_WRITE_OFF.name());
        //之前存在部分核销
        if (Objects.nonNull(flow.getWriteOffType()) && CharSequenceUtil.equals(flow.getWriteOffType(), FinanceWriteOffTypeEnum.AUTO_WRITE_OFF.name())) {
            flowRecord.setWriteOffStatus(FinanceWriteOffTypeEnum.AUTO_HAND_WRITE_OFF.name());
        }
        financeFlowRecordService.updateById(flowRecord);
    }

    public void updateFlowRecordFinance(FinanceFlowRecord flow, Long surplusAmount, String writeOffStatus, BankFlowCenterTypeEnum writeOffType) {
        FinanceFlowRecord flowRecord = new FinanceFlowRecord();
        flowRecord.setId(flow.getId());
        flowRecord.setFinancingFlowType(writeOffType.name());
        flowRecord.setFinancingProjectType(BankFlowWriteOffTypeEnum.FUNDS_END.name());
        flowRecord.setSurplusAmount(surplusAmount);
        flowRecord.setWriteOffStatus(writeOffStatus);
        Integer sort = Optional.ofNullable(FinancingFlowWriteOffStatusEnum.of(writeOffStatus))
                .map(FinancingFlowWriteOffStatusEnum::getSort).orElse(3);
        flowRecord.setSort(sort);
        flowRecord.setWriteOffType(FinanceWriteOffTypeEnum.HAND_WRITE_OFF.name());
        //之前存在部分核销
        if (Objects.nonNull(flow.getWriteOffType()) && CharSequenceUtil.equals(flow.getWriteOffType(), FinanceWriteOffTypeEnum.AUTO_WRITE_OFF.name())) {
            flowRecord.setWriteOffStatus(FinanceWriteOffTypeEnum.AUTO_HAND_WRITE_OFF.name());
        }
        financeFlowRecordService.updateById(flowRecord);
    }


    public void writeOffMargin(String marginCode, Long amount, FinanceFlowRecord flow, RecordTypeEnum collectionType) {
        ThirdMarginRecordREQ thirdMarginRecordReq = new ThirdMarginRecordREQ();
        thirdMarginRecordReq.setCollectionType(collectionType.name());
        thirdMarginRecordReq.setCollectionCode(marginCode);
        thirdMarginRecordReq.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(amount)));
        thirdMarginRecordReq.setCollectionDate(flow.getBiztime().toLocalDate());
        thirdMarginRecordReq.setDataSource(Optional.ofNullable(DataSourceEnum.of(flow.getDatasource())).map(DataSourceEnum::getDisplay).orElse(""));
        thirdMarginRecordReq.setOppositeAccountBank(flow.getOppbank());
        thirdMarginRecordReq.setOppositeAccountName(flow.getOppunit());
        thirdMarginRecordReq.setOppositeAccountNumber(flow.getOppbanknumber());
        thirdMarginRecordReq.setOurAccountBank(flow.getAccountbankAcctname());
        thirdMarginRecordReq.setOurAccountName(flow.getAccountbankName());
        thirdMarginRecordReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
        thirdMarginRecordReq.setPknumber(UUID.randomUUID().toString());
        thirdMarginRecordReq.setFinanceFlowId(flow.getId());
        thirdMarginRecordReq.setBankDetailNo(flow.getDetailid());
        R<String> collectionRecode = financialService.backRecord(thirdMarginRecordReq);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("自动核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
    }

    public void writeOffWarranty(String warrantyCode, Long amount, FinanceFlowRecord flow, RecordTypeEnum collectionType) {
        ThirdMarginRecordREQ thirdMarginRecordReq = new ThirdMarginRecordREQ();
        thirdMarginRecordReq.setCollectionType(collectionType.name());
        thirdMarginRecordReq.setCollectionCode(warrantyCode);
        thirdMarginRecordReq.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(amount)));
        thirdMarginRecordReq.setCollectionDate(flow.getBiztime().toLocalDate());
        thirdMarginRecordReq.setDataSource(Optional.ofNullable(DataSourceEnum.of(flow.getDatasource())).map(DataSourceEnum::getDisplay).orElse(""));
        thirdMarginRecordReq.setOppositeAccountBank(flow.getOppbank());
        thirdMarginRecordReq.setOppositeAccountName(flow.getOppunit());
        thirdMarginRecordReq.setOppositeAccountNumber(flow.getOppbanknumber());
        thirdMarginRecordReq.setOurAccountBank(flow.getAccountbankAcctname());
        thirdMarginRecordReq.setOurAccountName(flow.getAccountbankName());
        thirdMarginRecordReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
        thirdMarginRecordReq.setPknumber(UUID.randomUUID().toString());
        thirdMarginRecordReq.setFinanceFlowId(flow.getId());
        thirdMarginRecordReq.setBankDetailNo(flow.getDetailid());
        R<String> collectionRecode = financialService.backRecordWarranty(thirdMarginRecordReq);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("自动核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
    }

    public void paymentWriteOffCommonMethod(PaymentActualDetailUnconfirmed unconfirmed, Long paymentId, BankCenterSubTableProjectListRSP rsp, FinanceFlowRecord flow, Long writeOffAmount) {
        ThirdPaymentDetailREQ thirdPaymentDetailReq = getThirdPaymentDetailReq(unconfirmed, paymentId, rsp.getCashFlowItemName(), flow, writeOffAmount);
        thirdPaymentDetailReq.setInfoSource("手工核销");
        R<String> recodeExtra = financialExtraService.paymentRecodeExtra(thirdPaymentDetailReq);
        if (ResultCode.FAILURE.getCode() == recodeExtra.getCode()) {
            log.error("自动核销失败，原因：{}", recodeExtra.getMsg());
            throw new MithrasException(recodeExtra.getMsg());
        }
    }

    public ThirdPaymentDetailREQ getThirdPaymentDetailReq(PaymentActualDetailUnconfirmed unconfirmed, Long paymentId, String cashFlowItemName, FinanceFlowRecord flow, Long writeOffAmount) {
        ThirdPaymentDetailREQ thirdPaymentDetailReq = new ThirdPaymentDetailREQ();
        PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectById(paymentId);
        thirdPaymentDetailReq.setCollectionCode(paymentBaseInfo.getPaymentCode());
        thirdPaymentDetailReq.setCashFlowItem(cashFlowItemName);
        thirdPaymentDetailReq.setPaymentMethod(unconfirmed.getPaymentMethod());
        thirdPaymentDetailReq.setPaidInAmount(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
        thirdPaymentDetailReq.setPaidInDate(flow.getBiztime().toLocalDate());
        thirdPaymentDetailReq.setOppositeAccountBank(flow.getOppbank());
        thirdPaymentDetailReq.setOppositeAccountName(flow.getOppunit());
        thirdPaymentDetailReq.setOppositeAccountNumber(flow.getOppbanknumber());
        thirdPaymentDetailReq.setOurAccountBank(flow.getBankName());
        thirdPaymentDetailReq.setOurAccountName(flow.getAccountbankAcctname());
        thirdPaymentDetailReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
        thirdPaymentDetailReq.setPknumber(UUID.fastUUID().toString());
        thirdPaymentDetailReq.setFinanceFlowId(flow.getId());
        thirdPaymentDetailReq.setReqFromCq(false);
        thirdPaymentDetailReq.setBankDetailNo(flow.getBillno());
        thirdPaymentDetailReq.setUnConfirmedId(unconfirmed.getId());
        thirdPaymentDetailReq.setCapitalSource(unconfirmed.getCapitalSource());
        thirdPaymentDetailReq.setFinancingCode(unconfirmed.getFinancingCode());
        return thirdPaymentDetailReq;
    }

    public void commonCollectionActualWriteOff(CollectionWriteOffOrderEnum orderEnum, BankCenterSubTableProjectListRSP rsp, Long writeOffAmount, FinanceFlowRecord flow) {
        ThirdCollectionRecordREQ collectionRecordReq = new ThirdCollectionRecordREQ();
        collectionRecordReq.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
        String cashFlowItem = orderEnum.name();
        if (CharSequenceUtil.equalsAny(orderEnum.name(), CollectionWriteOffOrderEnum.PENALTY_INTEREST.name(),
                CollectionWriteOffOrderEnum.INTEREST.name(), CollectionWriteOffOrderEnum.PRINCIPAL.name(), CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
            //这里需要处理各自的核销金额
            if (CharSequenceUtil.equals(orderEnum.name(), CollectionWriteOffOrderEnum.PENALTY_INTEREST.name())) {
                collectionRecordReq.setPenaltyInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(orderEnum.name(), CollectionWriteOffOrderEnum.PRINCIPAL.name())) {
                collectionRecordReq.setPrincipal(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(orderEnum.name(), CollectionWriteOffOrderEnum.INTEREST.name())) {
                collectionRecordReq.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(orderEnum.name(), CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
                collectionRecordReq.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            cashFlowItem = CashFlowItemEnum.RENT.name();
        }
        collectionRecordReq.setCollectionDate(flow.getBiztime().toLocalDate());
        collectionRecordReq.setCollectionType(PaymentMethod.WY.display);
        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCode, rsp.getCashFlowCode())
                .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem)
                .last(StringUtil.mysqlLimitOne()));
        collectionRecordReq.setCollectionCode(collectionBaseInfo.getCode());
        collectionRecordReq.setCashFlowItem(cashFlowItem);
        collectionRecordReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
        collectionRecordReq.setInvoiceFlag(0);
        collectionRecordReq.setOurAccountBank(flow.getAccountbankAcctname());
        collectionRecordReq.setOurAccountName(flow.getAccountbankName());
        collectionRecordReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
        collectionRecordReq.setPknumber(UUID.fastUUID().toString());
        collectionRecordReq.setFinanceFlowId(flow.getId());
        collectionRecordReq.setBankDetailNo(flow.getBillno());
        R<String> collectionRecode = financialService.collectionRecode(collectionRecordReq);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("自动核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
    }

    /**
     * 这个方法是专门给资金端付款核销使用的！！！
     */
    @Transactional(rollbackFor = Throwable.class)
    public void financingPaymentWriteOff(FinancePaymentWriteOffREQ req) {
        //找到对应的流水信息
        List<FinanceFlowRecord> financeFlowRecords = getBean(FinanceFlowRecordMapper.class).selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .in(FinanceFlowRecord::getId, req.getFinanceFlowIds())
                .eq(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .ne(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name()));
        if (CollectionUtils.isEmpty(financeFlowRecords)) {
            throw new MithrasException("没有找到对应且未核销的流水信息");
        }
        try {
            //修改流水的展示状态，因为并发不大，暂时只做修改，不加锁，后续可修复
            financeFlowRecordService.lambdaUpdate()
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.NO.getCode())
                    .in(FinanceFlowRecord::getId, financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList()))
                    .update();
            //根据是否自动核销，初始化对应的现金流列表
            List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> toBeWriteOffList = JSONUtil.toList(req.getListDataJson(), BankFlowProcessingCenterFinancePaymentCashFlowRSP.class);
            if (CollectionUtils.isEmpty(toBeWriteOffList)) {
                throw new MithrasException("对应的核销列表不能为空");
            }
            toBeWriteOffList.forEach(e -> e.setOriginalWriteOffAmount(e.getThisWriteOffAmount()));
            Map<Long, FundDirectFinancingBaseInfo> directFinancingIdMap = getNeedHandleDirectFinancingId(toBeWriteOffList.stream().map(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getReceiptRepayBaseId).collect(Collectors.toList()));
            Map<Long, Long> financingIdMap = getNeedHandleFinancing(toBeWriteOffList.stream().map(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getReceiptRepayBaseId).collect(Collectors.toList()));
            if (Objects.equals(req.getIsAuto(), YesOrNoNumberEnum.YES.getCode())) {
                toBeWriteOffList.forEach(obj -> obj.setThisWriteOffAmount(obj.getNoPayAmount()));
            } else {
                toBeWriteOffList.forEach(obj -> obj.setThisWriteOffAmount(LongUtil.null2zero(obj.getThisWriteOffAmount())));
            }
            //处理流水的剩余金额，处理完金额后直接使用剩余金额即可，已经转化为毫厘
            handlerSurplusAmount(financeFlowRecords, BankFlowPaymentCollectionTypeEnum.PAYMENT.name());
            FinanceFlowWriteOffBO bo = new FinanceFlowWriteOffBO();
            this.handlerSurplusAmount(financeFlowRecords, BankFlowPaymentCollectionTypeEnum.PAYMENT.name());
            financeFlowRecords.sort(Comparator.comparing(FinanceFlowRecord::getSurplusAmount));
            bo.getHandFlowRecordQueue().addAll(financeFlowRecords);
            //根据规定的顺序核销对应的金额
            toBeWriteOffList = toBeWriteOffList.stream().sorted(Comparator.comparing(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getShouldPayTime)
                    .thenComparing(obj -> FinancePaymentWriteOffOrderEnum.valueOf(obj.getCashFlowItem()).getSort())
                    .thenComparing(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getThisWriteOffAmount)
                    .thenComparing(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getIdKey)).collect(Collectors.toList());

            // 开始遍历核销
            toBeWriteOffList.forEach(obj -> {
                // 如果是自动核销需要跳过其他费用项(除开本金和利息)
                if (obj.getThisWriteOffAmount() <= 0 ||
                        (YesOrNoNumberEnum.YES.getCode().equals(req.getIsAuto()) && !CharSequenceUtil.equalsAny(obj.getCashFlowItem(),
                                FinancePaymentWriteOffOrderEnum.INTEREST.name(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name()))) {
                    return;
                }
                FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
                if (flowRecord == null) {
                    return;
                }
                bo.getHandWriteOffAmount().set(flowRecord.getSurplusAmount());
                if(!CharSequenceUtil.equalsAny(obj.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())) {
                    if (!Objects.equals(obj.getShouldPayAmount(), obj.getThisWriteOffAmount())) {
                        throw new MithrasException("费用金额与银行流水不匹配，请修改费用明细后核销");
                    }
                }
                //开始核销实际核销金额
                getBean(FinanceFlowAutoWriteOffService.class).financingPay(bo, flowRecord, obj, FinancePaymentWriteOffOrderEnum.valueOf(obj.getCashFlowItem()));

                getBean(FinanceFlowAutoWriteOffService.class).financingPayAfter(financingIdMap, directFinancingIdMap, flowRecord, obj, FinancePaymentWriteOffOrderEnum.valueOf(obj.getCashFlowItem()));
            });
            // 如果有拆分项，最后处理拆分项
            if (CollectionUtil.isNotEmpty(req.getRepaySplitInfoList())) {
                boolean isAuto = Objects.nonNull(req.getIsAuto()) && Objects.equals(req.getIsAuto(), YesOrNoNumberEnum.YES.getCode());
                SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordService.class).writeOffRepayActualSplit(isAuto, req.getRepaySplitInfoList(), toBeWriteOffList);
            }
            // 通知苍穹
            if (CollUtil.isNotEmpty(bo.getFinanceFlowRecordIds())) {
                getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(bo.getFinanceFlowRecordIds());
            }
        } finally {
            //归还流水
            financeFlowRecordService.lambdaUpdate()
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                    .in(FinanceFlowRecord::getId, financeFlowRecords.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList()))
                    .update();
        }
    }

    public void financingPayAfter(Map<Long, Long> financingIdMap, Map<Long, FundDirectFinancingBaseInfo> directFinancingIdMap, FinanceFlowRecord flowRecord,
                                   BankFlowProcessingCenterFinancePaymentCashFlowRSP obj, FinancePaymentWriteOffOrderEnum writeOffOrderEnum) {
        FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingIdMap.get(obj.getReceiptRepayBaseId());
        Boolean isAbsOrAbn = null;
        if(directFinancingBaseInfo != null) {
            isAbsOrAbn = Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(directFinancingBaseInfo.getDirectFinancingType());
        }
        // 区分直融间融
        Long financingId = financingIdMap.getOrDefault(obj.getReceiptRepayBaseId(), directFinancingIdMap.getOrDefault(obj.getReceiptRepayBaseId(), new FundDirectFinancingBaseInfo()).getId());
        FinancingTypeEnum financingTypeEnum = financingIdMap.containsKey(obj.getReceiptRepayBaseId()) ? FinancingTypeEnum.INDIRECT : FinancingTypeEnum.DIRECT;
        if(financingId == null){
            throw new MithrasException("数据异常，未找到待处理的融资");
        }
        switch (writeOffOrderEnum){
            case PRINCIPAL:
                if(Objects.equals(FinancingTypeEnum.INDIRECT, financingTypeEnum)){
                    // 间融的本金核销完成需要释放授信额度
                    SpringContextHolder.getBean(FundCreditService.class).release(financingId, obj.getActualDetailAmount());
                    // 结清校验
                    Map<Long, Long> indirectRemainingMap = receiptRepayBaseInfoService.queryRemainingAmount(Collections.singletonList(financingId), FinancingTypeEnum.INDIRECT);
                    if(indirectRemainingMap.get(financingId) == null || indirectRemainingMap.get(financingId).equals(0L)){
                        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
                        toUpdate.setId(financingId);
                        toUpdate.setFinancingStatus(FundFinancingStatusEnum.SETTLE.name());
                        financingBaseInfoService.updateById(toUpdate);
                    }
                }

                if(Objects.equals(FinancingTypeEnum.DIRECT, financingTypeEnum)) {
                    if(isAbsOrAbn) {
                        // 直融ABS/ABN 核销完成后需要更新ABS/ABN的还款计划表
                        directFinancingRepayActualService.updateNextPhase(financingId, obj.getCashFlowCode());
                    }
                    // 结清校验
                    Map<Long, Long> directRemainingMap = receiptRepayBaseInfoService.queryRemainingAmount(Collections.singletonList(financingId), FinancingTypeEnum.DIRECT);
                    if(directRemainingMap.get(financingId) == null || directRemainingMap.get(financingId).equals(0L)){
                        FundDirectFinancingBaseInfo toUpdate = new FundDirectFinancingBaseInfo();
                        toUpdate.setId(financingId);
                        fundDirectFinancingPledgeInfoService.unLockContract(financingId);
                        toUpdate.setFinancingStatus(FundFinancingStatusEnum.SETTLE.name());
                        directFinancingBaseInfoService.updateById(toUpdate);
                    }
                }
                break;

            case INTEREST:
                // 直融利息核销完成后需要更新ABS/ABN的还款计划表
                if (Objects.equals(FinancingTypeEnum.DIRECT, financingTypeEnum) && isAbsOrAbn) {
                    directFinancingRepayActualService.updateNextPhase(financingId, obj.getCashFlowCode());
                }
                break;

            default:
                // 费用项核销后重新计算实际综合成本,并更新费用项支付时间为实际核销时间
                FundReceiptRepayExpense repayExpense = receiptRepayExpenseService.getOne(Wrappers.<FundReceiptRepayExpense>lambdaQuery()
                        .eq(FundReceiptRepayExpense::getCashFlowCode, obj.getCashFlowCode()).last(StringUtil.mysqlLimitOne()));

                if(Objects.equals(FinancingTypeEnum.DIRECT, financingTypeEnum)){
                    if(repayExpense != null) {
                        directFinancingFeeDetailService.update(Wrappers.<FundDirectFinancingFeeDetail>lambdaUpdate()
                                .eq(FundDirectFinancingFeeDetail::getId, repayExpense.getDirectFeeId())
                                .set(FundDirectFinancingFeeDetail::getPayDate, flowRecord.getBiztime()));
                    }
                    directFinancingBaseInfoService.updateFinancingCost(financingId);
                }
                if(Objects.equals(FinancingTypeEnum.INDIRECT, financingTypeEnum)){
                    if(repayExpense != null) {
                        financingFeeDetailService.update(Wrappers.<FundFinancingFeeDetail>lambdaUpdate()
                                .eq(FundFinancingFeeDetail::getId, repayExpense.getFeeId())
                                .set(FundFinancingFeeDetail::getPayDate, flowRecord.getBiztime()));
                    }
                    fundFinancingPlanService.updateFinancingCost(financingId);
                }
        }
    }

    private Map<Long, FundDirectFinancingBaseInfo> getNeedHandleDirectFinancingId(List<Long> receiptRepayIdList) {
        Map<Long, FundDirectFinancingBaseInfo> resultMap = new HashMap<>();
        if(CollectionUtil.isEmpty(receiptRepayIdList)){
            return resultMap;
        }
        // 先拿到直融
        Map<Long, Long> directFinancingIdMap = receiptRepayBaseInfoService.listByIds(receiptRepayIdList)
                .stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name())).collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));

        // 再筛选类型
        if(CollectionUtil.isNotEmpty(directFinancingIdMap)) {
            Map<Long, FundDirectFinancingBaseInfo> directFinancingBaseInfoMap = Optional.ofNullable(directFinancingBaseInfoService
                    .listByIds(directFinancingIdMap.values())).map(m -> m.stream()
                    .collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()))).orElse(Collections.emptyMap());
            directFinancingIdMap.forEach((k, v) -> {
                resultMap.put(k, directFinancingBaseInfoMap.get(v));
            });
        }
        return resultMap;
    }

    private Map<Long, Long> getNeedHandleFinancing(List<Long> receiptRepayIdList) {
        Map<Long, Long> resultMap = new HashMap<>();
        if(CollectionUtil.isEmpty(receiptRepayIdList)){
            return resultMap;
        }
        resultMap = receiptRepayBaseInfoService.listByIds(receiptRepayIdList)
                .stream().filter(f -> Objects.isNull(f.getFinancingType())).collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
        return resultMap;
    }

    public void financingPay(FinanceFlowWriteOffBO bo, FinanceFlowRecord flow, BankFlowProcessingCenterFinancePaymentCashFlowRSP finance,
                             FinancePaymentWriteOffOrderEnum payEnum) {
        long principal = 0;
        long interest = 0;
        //够还
        if (bo.getHandWriteOffAmount().get() > 0 && finance.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() > finance.getThisWriteOffAmount()) {
            if (FinancePaymentWriteOffOrderEnum.PRINCIPAL.equals(payEnum)) {
                //本金核销
                principal = finance.getThisWriteOffAmount();
            }
            if (FinancePaymentWriteOffOrderEnum.INTEREST.equals(payEnum)) {
                //利息核销
                interest = finance.getThisWriteOffAmount();
            }
            //首先实际核销掉金额
            BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setBankDetailNo(flow.getBillno());
            String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum.name()))
                    .map(Enum::name)
                    .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum.name()))
                            .map(Enum::name)
                            .orElse(null));
            financeDetailSaveReq.setCashFlowItem(cashFlowItem);
            financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
            financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
            financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
            financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
            financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            financeDetailSaveReq.setInterestAmount(interest);
            financeDetailSaveReq.setPrincipalAmount(principal);
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setTotalAmount(finance.getThisWriteOffAmount());
            financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
            financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
            getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
            finance.setActualDetailAmount(finance.getActualDetailAmount() + finance.getThisWriteOffAmount());
            //修改流水状态
            getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, bo.getHandWriteOffAmount().get() - finance.getThisWriteOffAmount(),
                    FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSING_CENTER);
            bo.getHandWriteOffAmount().set(bo.getHandWriteOffAmount().get() - finance.getThisWriteOffAmount());
            finance.setThisWriteOffAmount(0L);
            //这里要把剩余的钱还回队列
            if (bo.getHandWriteOffAmount().get() > 0) {
                flow.setSurplusAmount(bo.getHandWriteOffAmount().get());
                bo.getHandWriteOffAmount().set(bo.getHandWriteOffAmount().get());
                bo.getHandFlowRecordQueue().offerFirst(flow);
            }
            return;
        }

        //不够还款
        if (bo.getHandWriteOffAmount().get() > 0 && finance.getThisWriteOffAmount() > 0 && bo.getHandWriteOffAmount().get() <= finance.getThisWriteOffAmount()) {
            if (FinancePaymentWriteOffOrderEnum.PRINCIPAL.equals(payEnum)) {
                //本金核销
                principal = bo.getHandWriteOffAmount().get();
            }
            if (FinancePaymentWriteOffOrderEnum.INTEREST.equals(payEnum)) {
                //利息核销
                interest = bo.getHandWriteOffAmount().get();
            }
            //首先实际核销掉金额
            BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setBankDetailNo(flow.getBillno());
            String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum.name()))
                    .map(Enum::name)
                    .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum.name()))
                            .map(Enum::name)
                            .orElse(null));
            financeDetailSaveReq.setCashFlowItem(cashFlowItem);
            financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
            financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
            financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
            financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
            financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            financeDetailSaveReq.setInterestAmount(interest);
            financeDetailSaveReq.setPrincipalAmount(principal);
            financeDetailSaveReq.setFinanceFlowId(flow.getId());
            financeDetailSaveReq.setTotalAmount(flow.getSurplusAmount());
            financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
            financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
            getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
            finance.setActualDetailAmount(finance.getActualDetailAmount() + flow.getSurplusAmount());
            //修改流水状态
            getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, 0L,
                    FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_FUNDS_END);
            //给何老师留口饭吃
            bo.getFinanceFlowRecordIds().add(flow.getId());
            flow.setSurplusAmount(0L);
            finance.setThisWriteOffAmount(finance.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
            bo.getHandWriteOffAmount().set(0L);
        }
        //负数流水
        if (bo.getHandWriteOffAmount().get() <= 0) {
            if (finance.getThisWriteOffAmount() == 0) {
                return;
            }
            //看看是不是负数
            if (bo.getHandWriteOffAmount().get() < 0) {
                //需要将正数流水核销负数流水
                BusinessFlowFinanceDetailSaveREQ financeDetailSaveReq = new BusinessFlowFinanceDetailSaveREQ();
                financeDetailSaveReq.setFinanceFlowId(flow.getId());
                financeDetailSaveReq.setBankDetailNo(flow.getBillno());
                String cashFlowItem = Optional.ofNullable(FinanceCollectionWriteOffOrderEnum.transform(payEnum.name()))
                        .map(Enum::name)
                        .orElse(Optional.ofNullable(FinancePaymentWriteOffOrderEnum.transform(payEnum.name()))
                                .map(Enum::name)
                                .orElse(null));
                financeDetailSaveReq.setCashFlowItem(cashFlowItem);
                financeDetailSaveReq.setCashFlowDate(flow.getBiztime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                financeDetailSaveReq.setCashFlowCode(finance.getCashFlowCode());
                financeDetailSaveReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
                financeDetailSaveReq.setOurAccountName(flow.getAccountbankAcctname());
                financeDetailSaveReq.setOurAccountBank(flow.getAccountbankName());
                financeDetailSaveReq.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
                if (FinancePaymentWriteOffOrderEnum.PRINCIPAL.equals(payEnum)) {
                    //本金核销
                    principal = bo.getHandWriteOffAmount().get();
                }
                if (FinancePaymentWriteOffOrderEnum.INTEREST.equals(payEnum)) {
                    //利息核销
                    interest = bo.getHandWriteOffAmount().get();
                }
                financeDetailSaveReq.setInterestAmount(interest);
                financeDetailSaveReq.setPrincipalAmount(principal);
                financeDetailSaveReq.setFinanceFlowId(flow.getId());
                financeDetailSaveReq.setTotalAmount(bo.getHandWriteOffAmount().get());
                financeDetailSaveReq.setReceiptRepayId(finance.getReceiptRepayBaseId());
                financeDetailSaveReq.setSettleMethod(PaymentMethod.WY.display);
                getBean(BusinessFlowService.class).detailSave(financeDetailSaveReq);
                finance.setActualDetailAmount(finance.getActualDetailAmount() + bo.getHandWriteOffAmount().get());
                //修改流水状态
                getBean(FinanceFlowAutoWriteOffService.class).updateFlowRecordFinance(flow, 0L,
                        FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), BankFlowCenterTypeEnum.PROCESSED_FUNDS_END);
                finance.setThisWriteOffAmount(finance.getThisWriteOffAmount() - bo.getHandWriteOffAmount().get());
                //给何老师留口饭吃
                bo.getFinanceFlowRecordIds().add(flow.getId());
                flow.setSurplusAmount(0L);
            }
            //去队列里拿到下一条流水
            FinanceFlowRecord flowRecord = bo.getHandFlowRecordQueue().pollFirst();
            if (ObjectUtil.isNull(flowRecord)) {
                //银行流水使用完毕
                return;
            }
            bo.getHandWriteOffAmount().set(flowRecord.getSurplusAmount());
            //递归
            getBean(FinanceFlowAutoWriteOffService.class).financingPay(bo, flowRecord, finance, payEnum);
        }
    }

    public void check(BankFlowProcessingCenterProjWriteOffREQ req) {
        log.info("此次核销提交参数: {}", JSON.toJSONString(req));
        // 如核销的是最后一期的本金，则判断判断核销后的剩余本金是否为0，
        // 不为0，则提示“本次核销后剩余本金不为0，请核对”，并不允许核销成功
        if (BankFlowWriteOffTypeEnum.FUNDS_END.name().equals(req.getSideType())) {
            List<BankCenterSubTableFinanceListRSP> list = JSONUtil.toList(req.getListDataJson(), BankCenterSubTableFinanceListRSP.class);
            if (CollectionUtil.isNotEmpty(list)) {
                // 根据receiptRepayBaseId对list进行分组
                Map<Long, List<BankCenterSubTableFinanceListRSP>> groupedMap = list.stream()
                        .collect(Collectors.groupingBy(BankCenterSubTableFinanceListRSP::getReceiptRepayBaseId));
                // key融资id，value具体提交的明细
                Map<Long, List<BankCenterSubTableFinanceListRSP>> map = new HashMap<>();
                Map<Long, FinancingTypeEnum> receiptTypeMap = new HashMap<>();
                // 获取所有的receiptRepayBaseId
                final Set<Long> collect = list.stream().map(BankCenterSubTableFinanceListRSP::getReceiptRepayBaseId).collect(Collectors.toSet());
                final List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = receiptRepayBaseInfoService.listByIds(collect);
                if (CollectionUtil.isNotEmpty(receiptRepayBaseInfos)) {
                    Map<Long, List<FundReceiptRepayBaseInfo>> baseMap = receiptRepayBaseInfos.stream()
                            .collect(Collectors.groupingBy(FundReceiptRepayBaseInfo::getFinancingId));
                    baseMap.forEach((k,v) -> {
                        List<BankCenterSubTableFinanceListRSP> bankAllList = map.get(k);
                        if (CollectionUtil.isEmpty(bankAllList)) {
                            bankAllList = new ArrayList<>();
                        }
                        for (FundReceiptRepayBaseInfo item : v) {
                            final List<BankCenterSubTableFinanceListRSP> bankList = groupedMap.get(item.getId());
                            if (CollectionUtil.isNotEmpty(bankList)) {
                                bankAllList.addAll(bankList);
                            }
                        }
                        map.put(k, bankAllList);
                        final String financingType = v.get(0).getFinancingType();
                        if (StringUtils.isEmpty(financingType)) {
                            receiptTypeMap.put(k, FinancingTypeEnum.INDIRECT);
                        } else {
                            receiptTypeMap.put(k, FinancingTypeEnum.DIRECT);
                        }
                    });
                    // 根据融资id获取该融资最后的还款日期
                    map.forEach((k,v) -> {
                        List<FundReceiptRepayCashFlow> cashFlowList = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                                .eq(FundReceiptRepayCashFlow::getFinancingId, k));
                        if (CollectionUtil.isNotEmpty(cashFlowList)) {
                            // 获取还款期限最大那条
                            final FundReceiptRepayCashFlow fundReceiptRepayCashFlow = cashFlowList.stream().max(Comparator.comparingInt(FundReceiptRepayCashFlow::getPhase)).get();
                            Boolean isLastFlag = false;
                            for (BankCenterSubTableFinanceListRSP item : v) {
                                // 如果提交的数据包含了最后一期，则需要去验证是否剩余本金为0
                                if (item.getCashFlowCode().equals(fundReceiptRepayCashFlow.getCashFlowCode()) && item.getReceiptRepayBaseId().equals(fundReceiptRepayCashFlow.getReceiptRepayId())) {
                                    log.info("融资id为: {},cashFlowCode: {} , receiptRepayBaseId: {}",k,item.getCashFlowCode(),item.getReceiptRepayBaseId());
                                    isLastFlag = true;
                                }
                            }
                            if (isLastFlag) {
                                final Map<Long, Long> remainingAmountMap = receiptRepayBaseInfoService.queryRemainingAmount(Collections.singleton(k), receiptTypeMap.get(k));
                                final Long remainingAmount = remainingAmountMap.get(k);
                                log.info("融资id为: {} 的 剩余本金为: {}",k,remainingAmount);
                                // 将同一个融资项目的核算金额加起来
                                Long totalThisWriteOffAmount = v.stream()
                                        .map(BankCenterSubTableFinanceListRSP::getThisWriteOffAmount)
                                        .filter(Objects::nonNull)
                                        .reduce(0L, Long::sum);
                                // 如果剩余本金大于本次提交的核销总额，则提示
                                if (remainingAmount > totalThisWriteOffAmount) {
                                    throw new MithrasException("本次核销后剩余本金不为0，请核对！");
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void checkFinancingPaymentWriteOff(FinancePaymentWriteOffREQ req) {
        // 判断是不是最后一笔
        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> list = JSONUtil.toList(req.getListDataJson(), BankFlowProcessingCenterFinancePaymentCashFlowRSP.class);
        if (CollectionUtils.isEmpty(list)) {
            throw new MithrasException("对应的核销列表不能为空");
        }
        // 对过来的数据进行分组，只管本金
        list = list.stream().filter((e -> FinancePaymentWriteOffOrderEnum.PRINCIPAL.name().equals(e.getCashFlowItem()))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            // 将提交过来的数据分组
            Map<Long, List<BankFlowProcessingCenterFinancePaymentCashFlowRSP>> groupedMap = list.stream()
                    .collect(Collectors.groupingBy(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getFinancingId));
            // 校验每一笔数据
            groupedMap.forEach((k,v) -> {
                List<FundReceiptRepayCashFlow> cashFlowList = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .eq(FundReceiptRepayCashFlow::getFinancingId, k));
                if (CollectionUtil.isNotEmpty(cashFlowList)) {
                    // 获取还款期限最大那条
                    final FundReceiptRepayCashFlow fundReceiptRepayCashFlow = cashFlowList.stream().max(Comparator.comparingInt(FundReceiptRepayCashFlow::getPhase)).get();
                    boolean isLastFlag = false;
                    for (BankFlowProcessingCenterFinancePaymentCashFlowRSP item : v) {
                        // 如果提交的数据包含了最后一期，则需要去验证是否剩余本金为0
                        if (item.getCashFlowCode().equals(fundReceiptRepayCashFlow.getCashFlowCode())) {
                            isLastFlag = true;
                        }
                    }
                    if (isLastFlag) {
                        FinancingTypeEnum financingTypeEnum = v.get(0).getFinancingType().equals(FinancingTypeEnum.INDIRECT.name()) ? FinancingTypeEnum.INDIRECT : FinancingTypeEnum.DIRECT;
                        final Map<Long, Long> remainingAmountMap = receiptRepayBaseInfoService.queryRemainingAmount(Collections.singleton(k),financingTypeEnum);
                        final Long remainingAmount = remainingAmountMap.get(k);
                        // 将同一个融资项目的核算金额加起来
                        Long totalThisWriteOffAmount = v.stream()
                                .map(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getThisWriteOffAmount)
                                .filter(Objects::nonNull)
                                .reduce(0L, Long::sum);
                        log.info("融资id为: {} 的 剩余本金为: {}，本次核销本金总额为: {}",k,remainingAmount,totalThisWriteOffAmount);
                        // 如果剩余本金大于本次提交的核销总额，则提示
                        if (remainingAmount.compareTo(totalThisWriteOffAmount) != 0) {
                            throw new MithrasException("本次核销后剩余本金不为0，请核对！");
                        }
                    }
                }
            });
        }
    }
}
