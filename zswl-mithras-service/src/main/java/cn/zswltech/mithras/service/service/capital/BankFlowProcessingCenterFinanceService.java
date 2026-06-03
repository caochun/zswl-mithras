package cn.zswltech.mithras.service.service.capital;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceCollectionWriteOffOrderEnum;
import cn.zswltech.mithras.capital.domain.enums.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceWriteOffTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.DepositCashFlowType;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingFeeDetailService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.dto.FundPlanFlowResultDTO;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.*;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingFeeDetailService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.*;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/5/19/18:45
 * @description
 */
@Slf4j
@Service
public class BankFlowProcessingCenterFinanceService {

    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundFinancingFeeDetailService financingFeeDetailService;
    @Resource
    private FundDirectFinancingFeeDetailService directFinancingFeeDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    public List<BankFlowProcessingCenterFinanceOrgRSP> listOrg(BankFlowProcessingCenterFinanceOrgREQ req) {
        LambdaQueryWrapper<FundOrganization> orgQuery = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(req.getName())) {
            orgQuery.like(FundOrganization::getOrganizationName, req.getName());
        }
        List<FundOrganization> orgList = SpringUtil.getBean(FundOrganizationService.class).getBaseMapper().selectList(orgQuery);
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> directQuery = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(req.getName())) {
            directQuery.like(FundDirectFinancingBaseInfo::getProductName, req.getName());
        }
        List<FundDirectFinancingBaseInfo> directList = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getBaseMapper().selectList(directQuery);
        List<BankFlowProcessingCenterFinanceOrgRSP> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(orgList)) {
            List<BankFlowProcessingCenterFinanceOrgRSP> list = orgList.stream().map(e -> {
                BankFlowProcessingCenterFinanceOrgRSP rsp = new BankFlowProcessingCenterFinanceOrgRSP();
                rsp.setId(e.getId());
                rsp.setName(e.getOrganizationName());
                rsp.setIsDirect(YesOrNoNumberEnum.NO.getCode());
                return rsp;
            }).collect(Collectors.toList());
            result.addAll(list);
        }
        if (CollectionUtil.isNotEmpty(directList)) {
            List<BankFlowProcessingCenterFinanceOrgRSP> list = directList.stream().map(e -> {
                BankFlowProcessingCenterFinanceOrgRSP rsp = new BankFlowProcessingCenterFinanceOrgRSP();
                rsp.setId(e.getId());
                rsp.setName(e.getProductName());
                rsp.setIsDirect(YesOrNoNumberEnum.YES.getCode());
                return rsp;
            }).collect(Collectors.toList());
            result.addAll(list);
        }
        result.sort(Comparator.comparing(BankFlowProcessingCenterFinanceOrgRSP::getIsDirect));
        return result;
    }

    public List<BankFlowProcessingCenterFinanceInfoRSP> listFinanceInfo(BankFlowProcessingCenterFinanceInfoREQ req) {
        // 直融和间融分开
        if (Objects.equals(req.getIsDirect(), YesOrNoNumberEnum.YES.getCode())) {
            // id为产品id
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(req.getId());
            if (Objects.isNull(fundDirectFinancingBaseInfo)) {
                return Collections.emptyList();
            }
            FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getOne(
                    Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .eq(FundReceiptRepayBaseInfo::getFinancingId, fundDirectFinancingBaseInfo.getId())
                            .eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name())
                            .last(StringUtil.mysqlLimitOne())
            );
            if (Objects.isNull(fundReceiptRepayBaseInfo)) {
                return Collections.emptyList();
            }
            BankFlowProcessingCenterFinanceInfoRSP rsp = new BankFlowProcessingCenterFinanceInfoRSP();
            rsp.setReceiptRepayBaseId(fundReceiptRepayBaseInfo.getId());
            rsp.setFinancingAmount(fundReceiptRepayBaseInfo.getFinancingAmount());
            if (Objects.nonNull(fundDirectFinancingBaseInfo.getDurationFrom())) {
                rsp.setActualLoanDate(LocalDateTimeUtil.format(fundDirectFinancingBaseInfo.getDurationFrom(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
            rsp.setBusinessType(fundDirectFinancingBaseInfo.getDirectFinancingType());
            return Collections.singletonList(rsp);
        } else {
            // id为机构id
            List<FundFinancingCreditRef> refList = financingCreditRefService.queryByOrgId(req.getId());
            List<Long> financingIdList = refList.stream().filter(Objects::nonNull).map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList());
            List<FundFinancingBaseInfo> fundFinancingBaseInfoList = SpringUtil.getBean(FundFinancingBaseInfoService.class).list(
                    Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                            .in(FundFinancingBaseInfo::getId, CollectionUtil.isNotEmpty(financingIdList) ? financingIdList : Collections.singleton(-1))
                            .notIn(FundFinancingBaseInfo::getFinancingStatus, Arrays.asList(FundFinancingStatusEnum.CLOSE.name(), FundFinancingStatusEnum.NEW.name()))
            );
            if (CollectionUtil.isEmpty(fundFinancingBaseInfoList)) {
                return Collections.emptyList();
            }
            Map<Long, FundFinancingBaseInfo> map = fundFinancingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e));
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfoList = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).list(
                    Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, map.keySet()).isNull(FundReceiptRepayBaseInfo::getFinancingType)
            );
            if (CollectionUtil.isEmpty(receiptRepayBaseInfoList)) {
                return Collections.emptyList();
            }
            List<BankFlowProcessingCenterFinanceInfoRSP> result = new LinkedList<>();
            for (FundReceiptRepayBaseInfo receiptRepayBaseInfo : receiptRepayBaseInfoList) {
                FundFinancingBaseInfo financingBaseInfo = map.get(receiptRepayBaseInfo.getFinancingId());
                if (Objects.isNull(financingBaseInfo)) {
                    continue;
                }
                BankFlowProcessingCenterFinanceInfoRSP rsp = new BankFlowProcessingCenterFinanceInfoRSP();
                rsp.setReceiptRepayBaseId(receiptRepayBaseInfo.getId());
                rsp.setFinancingCode(financingBaseInfo.getFinancingCode());
                rsp.setFinancingAmount(financingBaseInfo.getFinancingAmount());
                if (Objects.nonNull(financingBaseInfo.getActualLoanDate())) {
                    rsp.setActualLoanDate(LocalDateTimeUtil.format(financingBaseInfo.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
                }
                rsp.setBusinessType(financingBaseInfo.getBusinessType());
                result.add(rsp);
            }
            return result;
        }
    }

    public List<BankFlowProcessingCenterFinanceCashFlowRSP> listCashFlow(BankFlowProcessingCenterFinanceCashFlowREQ req) {
        LocalDate[] duration = this.duration(req.getReceiptRepayBaseId());
        final LocalDate from;
        final LocalDate to;
        if (Objects.nonNull(duration) && duration.length > 0) {
            from = duration[0];
        } else {
            from = null;
        }
        if (Objects.nonNull(duration) && duration.length > 1) {
            to = duration[1];
        } else {
            to = null;
        }
        List<BankFlowProcessingCenterFinanceCashFlowRSP> result;
        if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name(), FinancePaymentWriteOffOrderEnum.INTEREST.name())) {
            // 查实际还款计划
            List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).listByReceiptRepayId(req.getReceiptRepayBaseId(), null).orElse(Collections.emptyList());
            result = fundReceiptRepayCashFlowList.stream()
                    .filter(e -> !Objects.equals(e.getWriteOffState(), CashFlowState.WRITTEN_OFF.name()))
                    .filter(e -> !Objects.equals(e.getWriteOffState(), CashFlowState.BEYOND_WRITTEN_OFF.name()))
                    .map(e -> {
                        BankFlowProcessingCenterFinanceCashFlowRSP rsp = new BankFlowProcessingCenterFinanceCashFlowRSP();
                        rsp.setReceiptRepayBaseId(e.getReceiptRepayId());
                        rsp.setCashFlowItem(req.getCashFlowItem());
                        rsp.setCashFlowCode(e.getCashFlowCode());
                        if (Objects.nonNull(e.getRepayDate())) {
                            rsp.setShouldPayTime(LocalDateTimeUtil.format(e.getRepayDate(), DatePattern.NORM_DATE_PATTERN));
                        }
                        if (Objects.equals(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())) {
                            rsp.setShouldPayAmount(Optional.ofNullable(e.getPrincipleAmount()).orElse(0L));
                        } else {
                            rsp.setShouldPayAmount(Optional.ofNullable(e.getInterestAmount()).orElse(0L));
                        }
                        return rsp;
                    }).collect(Collectors.toList());
        } else if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.DEPOSIT_PAY.name(), FinanceCollectionWriteOffOrderEnum.DEPOSIT_REFUND.name())) {
            // 查保证金
            LambdaQueryWrapper<FundReceiptRepayCashDeposit> query = Wrappers.lambdaQuery();
            query.eq(FundReceiptRepayCashDeposit::getReceiptRepayId, req.getReceiptRepayBaseId());
            if (Objects.equals(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.DEPOSIT_PAY.name())) {
                query.eq(FundReceiptRepayCashDeposit::getDepositCashFlowType, DepositCashFlowType.DEPOSIT_PAYMENT.name());
            } else {
                query.eq(FundReceiptRepayCashDeposit::getDepositCashFlowType, DepositCashFlowType.DEPOSIT_RETURN.name());
            }
            List<FundReceiptRepayCashDeposit> fundReceiptRepayCashDepositList = SpringUtil.getBean(FundReceiptRepayCashDepositService.class).getBaseMapper().selectList(query);
            result = fundReceiptRepayCashDepositList.stream().map(e -> {
                BankFlowProcessingCenterFinanceCashFlowRSP rsp = new BankFlowProcessingCenterFinanceCashFlowRSP();
                rsp.setReceiptRepayBaseId(req.getReceiptRepayBaseId());
                rsp.setCashFlowCode(e.getCashFlowCode());
                rsp.setCashFlowItem(req.getCashFlowItem());
                if (Objects.equals(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.DEPOSIT_PAY.name())) {
                    if (Objects.nonNull(from)) {
                        rsp.setShouldPayTime(LocalDateTimeUtil.format(from, DatePattern.NORM_DATE_PATTERN));
                    }
                } else {
                    if (Objects.nonNull(to)) {
                        rsp.setShouldPayTime(LocalDateTimeUtil.format(to, DatePattern.NORM_DATE_PATTERN));
                    }
                }
                rsp.setShouldPayAmount(e.getAmount());
                return rsp;
            }).collect(Collectors.toList());
        } else if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), FinanceCollectionWriteOffOrderEnum.FINANCE_FUND.name())) {
            // 查融资款
            LambdaQueryWrapper<FundReceiptRepayBorrowing> query = Wrappers.lambdaQuery();
            query.eq(FundReceiptRepayBorrowing::getReceiptRepayId, req.getReceiptRepayBaseId());
            List<FundReceiptRepayBorrowing> fundReceiptRepayBorrowingList = SpringUtil.getBean(FundReceiptRepayBorrowingService.class).list(
                    Wrappers.<FundReceiptRepayBorrowing>lambdaQuery().eq(FundReceiptRepayBorrowing::getReceiptRepayId, req.getReceiptRepayBaseId())
            );
            result = fundReceiptRepayBorrowingList.stream().map(e -> {
                BankFlowProcessingCenterFinanceCashFlowRSP rsp = new BankFlowProcessingCenterFinanceCashFlowRSP();
                rsp.setReceiptRepayBaseId(req.getReceiptRepayBaseId());
//                if (Objects.nonNull(from)) {
//                    rsp.setShouldPayTime(LocalDateTimeUtil.format(from, DatePattern.NORM_DATE_PATTERN));
//                }
                if (Objects.nonNull(e.getActualLoanDate())) {
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(e.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
                }
                rsp.setShouldPayAmount(e.getPrincipal());
                rsp.setCashFlowItem(req.getCashFlowItem());
                rsp.setCashFlowCode(e.getCashFlowCode());
                return rsp;
            }).collect(Collectors.toList());
        } else {
            // 查费用项
            LambdaQueryWrapper<FundReceiptRepayExpense> query = Wrappers.lambdaQuery();
            query.eq(FundReceiptRepayExpense::getReceiptRepayId, req.getReceiptRepayBaseId());
            FinanceCashFlowItemEnum financeCashFlowItemEnum = FinancePaymentWriteOffOrderEnum.transform(req.getCashFlowItem());
            if (Objects.isNull(financeCashFlowItemEnum)) {
                throw new MithrasException("未知的现金流类型");
            }
            query.eq(FundReceiptRepayExpense::getExpenseType, financeCashFlowItemEnum.name());
            List<FundReceiptRepayExpense> fundReceiptRepayExpenseList = SpringUtil.getBean(FundReceiptRepayExpenseService.class).list(query);
            result = fundReceiptRepayExpenseList.stream().map(e -> {
                BankFlowProcessingCenterFinanceCashFlowRSP rsp = new BankFlowProcessingCenterFinanceCashFlowRSP();
                rsp.setReceiptRepayBaseId(req.getReceiptRepayBaseId());
                rsp.setCashFlowCode(e.getCashFlowCode());
                rsp.setCashFlowItem(req.getCashFlowItem());
                rsp.setShouldPayAmount(e.getTotalAmount());
                if (Objects.nonNull(from)) {
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(from, DatePattern.NORM_DATE_PATTERN));
                }
                return rsp;
            }).collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        // 填充已核销金额
        Set<String> cashFlowCodes = result.stream().map(BankFlowProcessingCenterFinanceCashFlowRSP::getCashFlowCode).collect(Collectors.toSet());
        List<FundReceiptFlowDetail> detailList = SpringUtil.getBean(FundReceiptFlowDetailService.class).listByCashFlowCodesNotOnlyRepay(cashFlowCodes);
        Map<String, List<FundReceiptFlowDetail>> actualMap;
        if (CollectionUtil.isEmpty(detailList)) {
            actualMap = Collections.emptyMap();
        } else {
            actualMap = detailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        }
        result.forEach(e -> {
            long actualAmount = 0L;
            List<FundReceiptFlowDetail> list = actualMap.get(e.getCashFlowCode());
            if (CollectionUtil.isNotEmpty(list)) {
                if (Objects.equals(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())) {
                    actualAmount = list.stream().filter(item -> Objects.nonNull(item.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum();
                } else if (Objects.equals(req.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())) {
                    actualAmount = list.stream().filter(item -> Objects.nonNull(item.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum();
                } else {
                    actualAmount = list.stream().filter(item -> Objects.nonNull(item.getTotalAmount())).mapToLong(FundReceiptFlowDetail::getTotalAmount).sum();
                }
            }
            long planAmount = Optional.ofNullable(e.getShouldPayAmount()).orElse(0L);
            e.setNoPayAmount(planAmount - actualAmount);
        });
        return result;
    }

    public List<BankCenterSubTableFinanceListRSP> subList(BankCenterSubTableFinanceListREQ req) {
        List<FundReceiptFlowDetail> flowDetailList = SpringUtil.getBean(FundReceiptFlowDetailService.class).listByBankFlowIds(req.getBankFlowIds());
        if (CollectionUtil.isEmpty(flowDetailList)) {
            return Collections.emptyList();
        }
        // 根据现金流类型分类
        Map<String, List<FundReceiptFlowDetail>> detailMap = flowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowItem));
        // 找到主表数据
        List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoList = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).listByIds(flowDetailList.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toList()));
        // 辅助数据
        Map<Long, FinancingExtraInfo> extraInfoMap = this.getExtraInfoMap(fundReceiptRepayBaseInfoList);
        List<BankCenterSubTableFinanceListRSP> result = new LinkedList<>();
        // 融资款、保证金、还本付息、费用项需要从不同的表中获取应收信息
        List<BankCenterSubTableFinanceListRSP> borrow = this.borrow(detailMap, extraInfoMap);
        if (Objects.nonNull(borrow) && CollectionUtil.isNotEmpty(borrow)) {
            result.addAll(borrow);
        }
        List<BankCenterSubTableFinanceListRSP> deposit = this.deposit(detailMap, extraInfoMap);
        if (Objects.nonNull(deposit) && CollectionUtil.isNotEmpty(deposit)) {
            result.addAll(deposit);
        }
        List<BankCenterSubTableFinanceListRSP> repay = this.repay(detailMap, extraInfoMap);
        if (Objects.nonNull(repay) && CollectionUtil.isNotEmpty(repay)) {
            result.addAll(repay);
        }
        List<BankCenterSubTableFinanceListRSP> expense = this.expense(detailMap, extraInfoMap);
        if (Objects.nonNull(expense) && CollectionUtil.isNotEmpty(expense)) {
            result.addAll(expense);
        }
        return result;
    }

    private List<BankCenterSubTableFinanceListRSP> borrow(Map<String, List<FundReceiptFlowDetail>> map, Map<Long, FinancingExtraInfo> extraInfoMap) {
        List<FundReceiptFlowDetail> list = map.get(FundPlanFlowResultDTO.CashFlowItem.FINANCE_FUND.name());
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> ids = list.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toSet());
        List<FundReceiptRepayBorrowing> fundReceiptRepayBorrowingList = SpringUtil.getBean(FundReceiptRepayBorrowingService.class).listByReceiptRepayIds(ids);
        Map<String, FundReceiptRepayBorrowing> receiptRepayBorrowingMap = fundReceiptRepayBorrowingList.stream().collect(Collectors.toMap(FundReceiptRepayBorrowing::getCashFlowCode, e -> e));
        return list.stream().map(e -> {
            FinancingExtraInfo extraInfo = extraInfoMap.get(e.getReceiptRepayId());
            FundReceiptRepayBorrowing receiptRepayBorrowing = receiptRepayBorrowingMap.get(e.getCashFlowCode());
            BankCenterSubTableFinanceListRSP rsp = new BankCenterSubTableFinanceListRSP();
            this.fillExtraInfo(rsp, extraInfo);
            if (Objects.nonNull(extraInfo.getDurationFrom())) {
                rsp.setShouldPayTime(LocalDateTimeUtil.format(extraInfo.getDurationFrom(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setReceiptRepayBaseId(e.getReceiptRepayId());
            rsp.setCashFlowCode(e.getCashFlowCode());
            FinanceCollectionWriteOffOrderEnum item = FinanceCashFlowItemEnum.transformCollection(e.getCashFlowItem());
            rsp.setCashFlowItem(Optional.ofNullable(item).map(Enum::name).orElse(null));
            rsp.setCashFlowItemName(Optional.ofNullable(item).map(FinanceCollectionWriteOffOrderEnum::display).orElse("未知"));
            rsp.setShouldPayAmount(receiptRepayBorrowing.getPrincipal());
            rsp.setThisWriteOffAmount(e.getTotalAmount());
            long actualAmount = SpringUtil.getBean(FundReceiptFlowDetailService.class).sum(e.getReceiptRepayId(), e.getCashFlowCode());
            rsp.setNoPayAmount(Optional.ofNullable(rsp.getShouldPayAmount()).orElse(0L) - actualAmount);
            rsp.setId(e.getId());
            rsp.setSourceId(e.getId());
            rsp.setPlatform(PlatformApiEnum.CQ2_COLLECTION.name());
            rsp.setSource(ExceptionSourceENUM.FINANCE_SIDE.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<BankCenterSubTableFinanceListRSP> deposit(Map<String, List<FundReceiptFlowDetail>> map, Map<Long, FinancingExtraInfo> extraInfoMap) {
        List<FundReceiptFlowDetail> list1 = map.get(FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_PAYMENT.name());
        List<FundReceiptFlowDetail> list2 = map.get(FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name());
        List<FundReceiptFlowDetail> list = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(list1)) {
            list.addAll(list1);
        }
        if (CollectionUtil.isNotEmpty(list2)) {
            list.addAll(list2);
        }
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> ids = list.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toSet());
        List<FundReceiptRepayCashDeposit> fundReceiptRepayCashDepositList = SpringUtil.getBean(FundReceiptRepayCashDepositService.class).listByReceiptRepayIds(ids);
        Map<String, FundReceiptRepayCashDeposit> receiptRepayCashDepositMap = fundReceiptRepayCashDepositList.stream().collect(Collectors.toMap(FundReceiptRepayCashDeposit::getCashFlowCode, e -> e));
        return list.stream().map(e -> {
            FinancingExtraInfo extraInfo = extraInfoMap.get(e.getReceiptRepayId());
            FundReceiptRepayCashDeposit receiptRepayCashDeposit = receiptRepayCashDepositMap.get(e.getCashFlowCode());
            BankCenterSubTableFinanceListRSP rsp = new BankCenterSubTableFinanceListRSP();
            this.fillExtraInfo(rsp, extraInfo);
            rsp.setReceiptRepayBaseId(e.getReceiptRepayId());
            rsp.setCashFlowCode(e.getCashFlowCode());
            if (Objects.equals(DepositCashFlowType.DEPOSIT_PAYMENT.name(), receiptRepayCashDeposit.getDepositCashFlowType())) {
                FinancePaymentWriteOffOrderEnum item = FinanceCashFlowItemEnum.transformPayment(e.getCashFlowItem());
                rsp.setCashFlowItem(Optional.ofNullable(item).map(Enum::name).orElse(null));
                rsp.setCashFlowItemName(Optional.ofNullable(item).map(FinancePaymentWriteOffOrderEnum::display).orElse("未知"));
                // 付款取融资开始日期
                if (Objects.nonNull(extraInfo.getDurationFrom())) {
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(extraInfo.getDurationFrom(), DatePattern.NORM_DATE_PATTERN));
                }
            }
            if (Objects.equals(DepositCashFlowType.DEPOSIT_RETURN.name(), receiptRepayCashDeposit.getDepositCashFlowType())) {
                FinanceCollectionWriteOffOrderEnum item = FinanceCashFlowItemEnum.transformCollection(e.getCashFlowItem());
                rsp.setCashFlowItem(Optional.ofNullable(item).map(Enum::name).orElse(null));
                rsp.setCashFlowItemName(Optional.ofNullable(item).map(FinanceCollectionWriteOffOrderEnum::display).orElse("未知"));
                // 退款取融资结束日期
                if (Objects.nonNull(extraInfo.getDurationTo())) {
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(extraInfo.getDurationFrom(), DatePattern.NORM_DATE_PATTERN));
                }
            }
            rsp.setShouldPayAmount(receiptRepayCashDeposit.getAmount());
            rsp.setThisWriteOffAmount(e.getTotalAmount());
            long actualAmount = SpringUtil.getBean(FundReceiptFlowDetailService.class).sum(e.getReceiptRepayId(), e.getCashFlowCode());
            rsp.setNoPayAmount(Optional.ofNullable(rsp.getShouldPayAmount()).orElse(0L) - actualAmount);
            rsp.setId(e.getId());
            rsp.setSourceId(e.getId());
            rsp.setPlatform(PlatformApiEnum.CQ2_PAYMENT.name());
            rsp.setSource(ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<BankCenterSubTableFinanceListRSP> repay(Map<String, List<FundReceiptFlowDetail>> map, Map<Long, FinancingExtraInfo> extraInfoMap) {
        List<FundReceiptFlowDetail> list = map.get(FundPlanFlowResultDTO.CashFlowItem.REPAY.name());
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> ids = list.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toSet());
        List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery().in(FundReceiptRepayCashFlow::getReceiptRepayId, ids));
        Map<String, FundReceiptRepayCashFlow> receiptRepayCashFlowMap = fundReceiptRepayCashFlowList.stream().collect(Collectors.toMap(FundReceiptRepayCashFlow::getCashFlowCode, e -> e));
        return list.stream().map(e -> {
            FinancingExtraInfo extraInfo = extraInfoMap.get(e.getReceiptRepayId());
            FundReceiptRepayCashFlow receiptRepayCashFlow = receiptRepayCashFlowMap.get(e.getCashFlowCode());
            BankCenterSubTableFinanceListRSP rsp = new BankCenterSubTableFinanceListRSP();
            this.fillExtraInfo(rsp, extraInfo);
            if (Objects.nonNull(e.getCashFlowDate())) {
                rsp.setShouldPayTime(LocalDateTimeUtil.format(e.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setReceiptRepayBaseId(e.getReceiptRepayId());
            rsp.setCashFlowCode(e.getCashFlowCode());
            if (Objects.nonNull(e.getPrincipalAmount()) && e.getPrincipalAmount() > 0) {
                // 本金
                rsp.setCashFlowItem(FinancePaymentWriteOffOrderEnum.PRINCIPAL.name());
                rsp.setCashFlowItemName(FinancePaymentWriteOffOrderEnum.PRINCIPAL.display());
                rsp.setShouldPayAmount(Optional.ofNullable(receiptRepayCashFlow.getPrincipleAmount()).orElse(0L));
                rsp.setThisWriteOffAmount(e.getTotalAmount());
                LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
                query.eq(FundReceiptFlowDetail::getReceiptRepayId, e.getReceiptRepayId());
                query.eq(FundReceiptFlowDetail::getCashFlowCode, e.getCashFlowCode());
                query.gt(FundReceiptFlowDetail::getPrincipalAmount, 0);
                List<FundReceiptFlowDetail> principalList = SpringUtil.getBean(FundReceiptFlowDetailService.class).list(query);
                long actualAmount = Optional.ofNullable(principalList).map(item -> item.stream().mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum()).orElse(0L);
                rsp.setNoPayAmount(Optional.ofNullable(rsp.getShouldPayAmount()).orElse(0L) - actualAmount);
            } else {
                // 利息
                rsp.setCashFlowItem(FinancePaymentWriteOffOrderEnum.INTEREST.name());
                rsp.setCashFlowItemName(FinancePaymentWriteOffOrderEnum.INTEREST.display());
                rsp.setShouldPayAmount(Optional.ofNullable(receiptRepayCashFlow.getInterestAmount()).orElse(0L));
                rsp.setThisWriteOffAmount(e.getTotalAmount());
                LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
                query.eq(FundReceiptFlowDetail::getReceiptRepayId, e.getReceiptRepayId());
                query.eq(FundReceiptFlowDetail::getCashFlowCode, e.getCashFlowCode());
                query.gt(FundReceiptFlowDetail::getInterestAmount, 0);
                List<FundReceiptFlowDetail> interestList = SpringUtil.getBean(FundReceiptFlowDetailService.class).list(query);
                long actualAmount = Optional.ofNullable(interestList).map(item -> item.stream().mapToLong(FundReceiptFlowDetail::getInterestAmount).sum()).orElse(0L);
                rsp.setNoPayAmount(Optional.ofNullable(rsp.getShouldPayAmount()).orElse(0L) - actualAmount);
            }
            rsp.setId(e.getId());
            rsp.setSourceId(e.getId());
            rsp.setPlatform(PlatformApiEnum.CQ2_PAYMENT.name());
            rsp.setSource(ExceptionSourceENUM.FINANCE_SIDE.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<BankCenterSubTableFinanceListRSP> expense(Map<String, List<FundReceiptFlowDetail>> map, Map<Long, FinancingExtraInfo> extraInfoMap) {
        List<FundReceiptFlowDetail> list = new LinkedList<>();
        for (Map.Entry<String, List<FundReceiptFlowDetail>> entry : map.entrySet()) {
            if (CharSequenceUtil.equalsAny(entry.getKey(),
                    FundPlanFlowResultDTO.CashFlowItem.FINANCE_FUND.name(),
                    FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_PAYMENT.name(),
                    FundPlanFlowResultDTO.CashFlowItem.DEPOSIT_RETURN.name(),
                    FundPlanFlowResultDTO.CashFlowItem.REPAY.name())) {
                continue;
            }
            list.addAll(entry.getValue());
        }
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> ids = list.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toSet());
        List<FundReceiptRepayExpense> fundReceiptRepayExpenseList = SpringUtil.getBean(FundReceiptRepayExpenseService.class).listByReceiptRepayIds(ids);
        Map<String, FundReceiptRepayExpense> receiptRepayExpenseMap = fundReceiptRepayExpenseList.stream().collect(Collectors.toMap(FundReceiptRepayExpense::getCashFlowCode, e -> e));
        Map<Long, FundDirectFinancingFeeDetail> directFeeDetailMap = directFinancingFeeDetailService.listByIds(fundReceiptRepayExpenseList.stream().map(FundReceiptRepayExpense::getDirectFeeId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FundDirectFinancingFeeDetail::getId, Function.identity()));
        Map<Long, FundFinancingFeeDetail> feeDetailMap = financingFeeDetailService.listByIds(fundReceiptRepayExpenseList.stream().map(FundReceiptRepayExpense::getFeeId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FundFinancingFeeDetail::getId, Function.identity()));

        return list.stream().map(e -> {
            FinancingExtraInfo extraInfo = extraInfoMap.get(e.getReceiptRepayId());
            FundReceiptRepayExpense receiptRepayExpense = receiptRepayExpenseMap.get(e.getCashFlowCode());
            LocalDate feePayDate = null;
            if(Objects.equals(YesOrNoNumberEnum.YES.getCode(), extraInfo.getIsDirect())){
                FundDirectFinancingFeeDetail directFeeDetail = directFeeDetailMap.get(receiptRepayExpense.getDirectFeeId());
                feePayDate = Optional.ofNullable(directFeeDetail).map(FundDirectFinancingFeeDetail::getPayDate).orElse(null);
            }else{
                FundFinancingFeeDetail feeDetail = feeDetailMap.get(receiptRepayExpense.getFeeId());
                feePayDate = Optional.ofNullable(feeDetail).map(FundFinancingFeeDetail::getPayDate).orElse(null);
            }
            BankCenterSubTableFinanceListRSP rsp = new BankCenterSubTableFinanceListRSP();
            this.fillExtraInfo(rsp, extraInfo);

            if (Objects.nonNull(extraInfo.getDurationFrom()) || Objects.nonNull(feePayDate)) {
                rsp.setShouldPayTime(LocalDateTimeUtil.format(Optional.ofNullable(feePayDate).orElse(extraInfo.getDurationFrom()), DatePattern.NORM_DATE_PATTERN));
            }
            FinancePaymentWriteOffOrderEnum item = FinanceCashFlowItemEnum.transformPayment(e.getCashFlowItem());
            rsp.setCashFlowItem(Optional.ofNullable(item).map(Enum::name).orElse(null));
            rsp.setCashFlowItemName(Optional.ofNullable(item).map(FinancePaymentWriteOffOrderEnum::display).orElse("未知"));
            rsp.setReceiptRepayBaseId(e.getReceiptRepayId());
            rsp.setCashFlowCode(e.getCashFlowCode());
            rsp.setShouldPayAmount(receiptRepayExpense.getTotalAmount());
            rsp.setThisWriteOffAmount(e.getTotalAmount());
            long actualAmount = SpringUtil.getBean(FundReceiptFlowDetailService.class).sum(e.getReceiptRepayId(), e.getCashFlowCode());
            rsp.setNoPayAmount(Optional.ofNullable(rsp.getShouldPayAmount()).orElse(0L) - actualAmount);
            rsp.setId(e.getId());
            rsp.setSourceId(e.getId());
            rsp.setPlatform(PlatformApiEnum.CQ2_PAYMENT.name());
            rsp.setSource(ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private void fillExtraInfo(BankCenterSubTableFinanceListRSP rsp, FinancingExtraInfo extraInfo) {
        rsp.setOrgIds(extraInfo.getOrgIds());
        rsp.setOrgNames(extraInfo.getOrgNames());
        rsp.setReceiptRepayBaseCode(extraInfo.getFinancingCode());
        rsp.setIsDirect(extraInfo.getIsDirect());
        rsp.setReceiptRepayBaseCode(extraInfo.getFinancingCode());
        rsp.setStatus(FinanceWriteOffTypeEnum.HAND_WRITE_OFF.name());
        rsp.setBusinessType(extraInfo.getBusinessType());
    }

    private Map<Long, FinancingExtraInfo> getExtraInfoMap(List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoList) {
        // FIXME 不要循环查询数据库
        Map<Long, FinancingExtraInfo> result = new HashMap<>();
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(fundReceiptRepayBaseInfoList.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList()));
        for (FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo : fundReceiptRepayBaseInfoList) {
            FinancingExtraInfo financingExtraInfo = new FinancingExtraInfo();
            if (StrUtil.isBlank(fundReceiptRepayBaseInfo.getFinancingType())) {
                // 间融
                List<FundOrganization> organizationList = orgMap.get(fundReceiptRepayBaseInfo.getFinancingId());
                FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
                List<FundFinancingRepayActual> fundFinancingRepayActualList = SpringUtil.getBean(FundFinancingRepayActualService.class).listByFinancingId(fundReceiptRepayBaseInfo.getFinancingId());
                financingExtraInfo.setFinancingCode(financingBaseInfo.getFinancingCode());
                financingExtraInfo.setOrgIds(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
                financingExtraInfo.setOrgNames(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                if (CollectionUtil.isNotEmpty(fundFinancingRepayActualList)) {
                    financingExtraInfo.setDurationFrom(fundFinancingRepayActualList.get(0).getRepayDate());
                    financingExtraInfo.setDurationTo(fundFinancingRepayActualList.get(fundFinancingRepayActualList.size() - 1).getRepayDate());
                }
                financingExtraInfo.setIsDirect(YesOrNoNumberEnum.NO.getCode());
                financingExtraInfo.setBusinessType(financingBaseInfo.getBusinessType());
            } else {
                // 直融
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
                List<FundDirectFinancingRepayActual> fundDirectFinancingRepayActualList = SpringUtil.getBean(FundDirectFinancingRepayActualService.class).listByFinancingId(fundReceiptRepayBaseInfo.getFinancingId());
                financingExtraInfo.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                financingExtraInfo.setOrgIds(Collections.singletonList(fundDirectFinancingBaseInfo.getId()));
                financingExtraInfo.setOrgNames(Collections.singletonList(fundDirectFinancingBaseInfo.getProductName()));
                if (CollectionUtil.isNotEmpty(fundDirectFinancingRepayActualList)) {
                    financingExtraInfo.setDurationFrom(fundDirectFinancingRepayActualList.get(0).getRepayDate());
                    financingExtraInfo.setDurationTo(fundDirectFinancingRepayActualList.get(fundDirectFinancingRepayActualList.size() - 1).getRepayDate());
                }
                financingExtraInfo.setIsDirect(YesOrNoNumberEnum.YES.getCode());
                financingExtraInfo.setBusinessType(fundDirectFinancingBaseInfo.getDirectFinancingType());
            }
            result.put(fundReceiptRepayBaseInfo.getId(), financingExtraInfo);
        }
        return result;
    }

    private LocalDate[] duration(Long receiptRepayBaseId) {
        List<FundReceiptRepayCashFlow> cashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).listByReceiptRepayId(receiptRepayBaseId, null).orElse(Collections.emptyList());
        if (CollectionUtil.isEmpty(cashFlowList)) {
            return null;
        }
        return new LocalDate[]{cashFlowList.get(0).getRepayDate(), cashFlowList.get(cashFlowList.size() - 1).getRepayDate()};
    }

    private Map<Long, LocalDate[]> durationGroup(List<Long> receiptRepayBaseIdList) {
        List<FundReceiptRepayCashFlow> cashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class)
                .list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery().in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayBaseIdList));
        if (CollectionUtils.isEmpty(cashFlowList)) {
            return Collections.emptyMap();
        }
        Map<Long, LocalDate[]> map = new HashMap<>();
        cashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId))
                .forEach((receiptRepayId, list) -> {
                    list.sort(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate));
                    map.put(receiptRepayId, new LocalDate[]{list.get(0).getRepayDate(), list.get(list.size() - 1).getRepayDate()});
                });
        return map;
    }

    public BankFlowProcessingCenterFinancePaymentCashFlowSumRSP listPaymentCashFlow(BankFlowProcessingCenterFinancePaymentCashFlowREQ req) {
        BankFlowProcessingCenterFinancePaymentCashFlowSumRSP resultRSP = new BankFlowProcessingCenterFinancePaymentCashFlowSumRSP();
        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> result = new ArrayList<>();
        List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).listByIds(req.getReceiptRepayBaseIdList());
        //查找实际还款计划
        List<FundReceiptRepayCashFlow> repayCashFlowList = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .ge(FundReceiptRepayCashFlow::getRepayDate, LocalDate.parse(req.getActualLoanDateFrom().replace("/", ""), DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)))
                .le(FundReceiptRepayCashFlow::getRepayDate, LocalDate.parse(req.getActualLoanDateTo().replace("/", ""), DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)))
                .in(FundReceiptRepayCashFlow::getReceiptRepayId, req.getReceiptRepayBaseIdList()));

        if (!CollectionUtils.isEmpty(repayCashFlowList) && !CollectionUtils.isEmpty(fundReceiptRepayBaseInfos)) {
            //构建响应体
            result.addAll(buildPlanRsp(repayCashFlowList, fundReceiptRepayBaseInfos));
        }

        //保证金
        List<FundReceiptRepayCashDeposit> fundReceiptRepayCashDeposits = SpringUtil.getBean(FundReceiptRepayCashDepositService.class).list(Wrappers.<FundReceiptRepayCashDeposit>lambdaQuery()
                .in(FundReceiptRepayCashDeposit::getReceiptRepayId, req.getReceiptRepayBaseIdList())
                .in(FundReceiptRepayCashDeposit::getWriteOffState, CashFlowState.PART_WRITE_OFF.name(), CashFlowState.NO_WRITE_OFF.name())
                .eq(FundReceiptRepayCashDeposit::getDepositCashFlowType, DepositCashFlowType.DEPOSIT_PAYMENT.name()));
        if (!CollectionUtils.isEmpty(fundReceiptRepayCashDeposits) && !CollectionUtils.isEmpty(fundReceiptRepayBaseInfos)) {
            //构建响应体
            result.addAll(buildDepositRsp(fundReceiptRepayCashDeposits, fundReceiptRepayBaseInfos));
        }
        //其它费用项
        result.addAll(buildFeeRsp(req, fundReceiptRepayBaseInfos));
        // 流水金额合计
        if(CollectionUtil.isNotEmpty(req.getFinanceFlowIdList())) {
            List<FinanceFlowRecord> financeFlowRecords = SpringContextHolder.getBean(FinanceFlowRecordService.class).listByIds(req.getFinanceFlowIdList());
            if (CollectionUtil.isNotEmpty(financeFlowRecords)) {
                double sum = financeFlowRecords.stream().mapToDouble(m -> m.getDebitamount() + m.getCreditamount()).sum();
                BigDecimal cashFlowAmount = new BigDecimal(Double.toString(sum)).multiply(BigDecimal.valueOf(10000).divide(BigDecimal.ONE, 0, RoundingMode.UP));
                resultRSP.setCashFlowAmountSum(cashFlowAmount.longValue());
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            resultRSP.setList(Collections.emptyList());
        }else {
            result = Optional.of(result.stream().filter(obj -> LongUtil.null2zero(obj.getNoPayAmount()) > 0)
                    .collect(Collectors.toList())).orElse(Collections.emptyList());
            long shouldPayAmountSum = result.stream().mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getShouldPayAmount).sum();
            long noPayAmountSum = result.stream().mapToLong(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getNoPayAmount).sum();

            Map<Long, Long> directRemainingMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(result.stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name()))
                    .map(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getFinancingId).collect(Collectors.toList()), FinancingTypeEnum.DIRECT);
            Map<Long, Long> indirectRemainingMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(result.stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.INDIRECT.name()))
                    .map(BankFlowProcessingCenterFinancePaymentCashFlowRSP::getFinancingId).collect(Collectors.toList()), FinancingTypeEnum.INDIRECT);
            resultRSP.setRemainingDetailList(buildRemainingRsp(directRemainingMap, indirectRemainingMap));
            resultRSP.setShouldPayAmountSum(shouldPayAmountSum);
            resultRSP.setNoPayAmountSum(noPayAmountSum);
            resultRSP.setList(result);
        }
        return resultRSP;
    }

    private List<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail> buildRemainingRsp(Map<Long, Long> directRemainingMap, Map<Long, Long> indirectRemainingMap) {
        List<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail> rspList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(directRemainingMap)) {
            List<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail> directList = directRemainingMap.entrySet().stream().map(entry -> {
                BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail direct = new BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail();
                direct.setFinancingId(entry.getKey());
                direct.setFinancingType(FinancingTypeEnum.DIRECT.name());
                direct.setRemainingAmount(entry.getValue());
                return direct;
            }).collect(Collectors.toList());
            rspList.addAll(directList);
        }

        if(CollectionUtil.isNotEmpty(indirectRemainingMap)) {
            List<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail> directList = indirectRemainingMap.entrySet().stream().map(entry -> {
                BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail direct = new BankFlowProcessingCenterFinancePaymentCashFlowSumRSP.RemainingDetail();
                direct.setFinancingId(entry.getKey());
                direct.setFinancingType(FinancingTypeEnum.INDIRECT.name());
                direct.setRemainingAmount(entry.getValue());
                return direct;
            }).collect(Collectors.toList());
            rspList.addAll(directList);
        }
        return rspList;
    }

    private List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> buildFeeRsp(BankFlowProcessingCenterFinancePaymentCashFlowREQ req, List<FundReceiptRepayBaseInfo> baseInfos) {
        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> result = new ArrayList<>();
        Map<Long, FundReceiptRepayBaseInfo> baseInfoMap = baseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        List<Long> financingIds = baseInfos.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        Map<Long, FundFinancingBaseInfo> financingMap = new HashMap<>();
        Map<Long, FundFinancingFeeDetail> feeDetailMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(financingIds)) {
            List<FundFinancingBaseInfo> financingBaseInfos = SpringUtil.getBean(FundFinancingBaseInfoService.class).listByIds(financingIds);
            if (!CollectionUtils.isEmpty(financingBaseInfos)) {
                financingMap = Optional.of(financingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1)))
                        .orElse(new HashMap<>());
                feeDetailMap = Optional.ofNullable(financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery().in(FundFinancingFeeDetail::getFinancingId, financingIds)))
                        .map(m -> m.stream().collect(Collectors.toMap(FundFinancingFeeDetail::getId, Function.identity()))).orElse(Collections.emptyMap());
            }
        }

        Map<Long, FundDirectFinancingBaseInfo> directMap = new HashMap<>();
        Map<Long, FundDirectFinancingFeeDetail> directFeeDetailMap = new HashMap<>();
        List<FundReceiptRepayBaseInfo> directList = baseInfos.stream().filter(o -> Objects.equals(FinancingTypeEnum.DIRECT.name(), o.getFinancingType()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(directList)) {
            List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfos = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                    .in(FundDirectFinancingBaseInfo::getId, directList.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())));
            if (!CollectionUtils.isEmpty(directList)) {
                directMap = fundDirectFinancingBaseInfos.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity(), (a, b) -> a));
                directFeeDetailMap = Optional.ofNullable(directFinancingFeeDetailService.list(Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery().in(FundDirectFinancingFeeDetail::getFinancingId, financingIds)))
                        .map(m -> m.stream().collect(Collectors.toMap(FundDirectFinancingFeeDetail::getId, Function.identity()))).orElse(Collections.emptyMap());
            }
        }

        Map<Long, String> financingTypeMap = baseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId,
                m -> Objects.equals(m.getFinancingType(), FinancingTypeEnum.DIRECT.name()) ? FinancingTypeEnum.DIRECT.name() : FinancingTypeEnum.INDIRECT.name()));

        LambdaQueryWrapper<FundReceiptRepayExpense> query = Wrappers.lambdaQuery();
        query.in(FundReceiptRepayExpense::getReceiptRepayId, req.getReceiptRepayBaseIdList());
        query.in(FundReceiptRepayExpense::getWriteOffState, CashFlowState.PART_WRITE_OFF.name(), CashFlowState.NO_WRITE_OFF.name());
        List<FundReceiptRepayExpense> fundReceiptRepayExpenseList = SpringUtil.getBean(FundReceiptRepayExpenseService.class).list(query);
        if (!CollectionUtils.isEmpty(fundReceiptRepayExpenseList)) {
            Map<Long, LocalDate[]> durationMap = this.durationGroup(req.getReceiptRepayBaseIdList());
            Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(financingMap.keySet());
            LocalDate dateForm = LocalDate.parse(req.getActualLoanDateFrom().replace("/", ""), DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
            LocalDate dateTo = LocalDate.parse(req.getActualLoanDateTo().replace("/", ""), DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
            for (FundReceiptRepayExpense expense : fundReceiptRepayExpenseList) {
                LocalDate[] duration = durationMap.get(expense.getReceiptRepayId());
                final LocalDate from;
                if (Objects.nonNull(duration) && duration.length > 0) {
                    from = duration[0];
                } else {
                    from = null;
                }
                BankFlowProcessingCenterFinancePaymentCashFlowRSP rsp = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
                FundReceiptRepayBaseInfo obj = baseInfoMap.get(expense.getReceiptRepayId());
                if (Objects.nonNull(obj)) {
                    LocalDate shouldPayTime = null;
                    rsp.setFinancingId(obj.getFinancingId());
                    if(Objects.equals(financingTypeMap.get(obj.getId()), FinancingTypeEnum.DIRECT.name())){
                        rsp.setFinancingCode(Optional.ofNullable(directMap.get(obj.getFinancingId())).map(FundDirectFinancingBaseInfo::getFinancingCode).orElse(null));
                        rsp.setFinancingType(FinancingTypeEnum.DIRECT.name());
                        FundDirectFinancingFeeDetail directFeeDetail = directFeeDetailMap.getOrDefault(expense.getDirectFeeId(), new FundDirectFinancingFeeDetail());
                        shouldPayTime = Optional.ofNullable(directFeeDetail.getPayDate()).orElse(from);
                    }else if(Objects.equals(financingTypeMap.get(obj.getId()), FinancingTypeEnum.INDIRECT.name())){
                        rsp.setFinancingCode(Optional.ofNullable(financingMap.get(obj.getFinancingId())).map(FundFinancingBaseInfo::getFinancingCode).orElse(null));
                        rsp.setFinancingType(FinancingTypeEnum.INDIRECT.name());
                        FundFinancingFeeDetail feeDetail = feeDetailMap.getOrDefault(expense.getFeeId(), new FundFinancingFeeDetail());
                        shouldPayTime = Optional.ofNullable(feeDetail.getPayDate()).orElse(from);
                    }
                    if(shouldPayTime.isBefore(dateForm) || shouldPayTime.isAfter(dateTo)){
                        continue;
                    }
                    rsp.setShouldPayTime(shouldPayTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                    FundFinancingBaseInfo financingBaseInfo = financingMap.get(obj.getFinancingId());
                    List<FundOrganization> organizationList = orgMap.getOrDefault(obj.getFinancingId(), Collections.emptyList());
                    if(CollectionUtil.isNotEmpty(organizationList)) {
                        rsp.setOrgName(organizationList.get(0).getOrganizationName());
                    }else{
                        rsp.setOrgName(Optional.ofNullable(directMap.get(expense.getFeeId())).map(FundDirectFinancingBaseInfo::getProductName).orElse(null));
                    }
                    rsp.setBeginInterestDate(Optional.ofNullable(financingBaseInfo)
                            .map(o -> o.getPlanLoanDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                            .orElse(""));
                    FundDirectFinancingBaseInfo directFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getOne(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                            .eq(FundDirectFinancingBaseInfo::getId, obj.getFinancingId())
                            .last(StringUtil.mysqlLimitOne()));
                    if (Objects.isNull(rsp.getOrgName()) && Objects.nonNull(directFinancingBaseInfo)) {
                        // 直接融资，取产品名称
                        rsp.setOrgName(directFinancingBaseInfo.getProductName());
                    }
                    if (CharSequenceUtil.isBlank(rsp.getBeginInterestDate()) && Objects.nonNull(directFinancingBaseInfo)) {
                        // 直接融资，取存续时间开始
                        rsp.setBeginInterestDate(directFinancingBaseInfo.getDurationFrom().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                    }
                    rsp.setFinancingAmount(obj.getFinancingAmount());
                    rsp.setReceiptRepayBaseCode(obj.getReceiptRepayCode());
                }
                rsp.setReceiptRepayBaseId(expense.getReceiptRepayId());
                rsp.setCashFlowCode(expense.getCashFlowCode());
                rsp.setCashFlowItem(expense.getExpenseType());
                rsp.setShouldPayAmount(expense.getTotalAmount());
                rsp.setIdKey(expense.getId() + "-OTHER_FEE");
                rsp.setNoPayAmount(LongUtil.null2zero(expense.getTotalAmount()) - LongUtil.null2zero(expense.getTotalPaidAmount()));
//                if (Objects.nonNull(from)) {
//                    rsp.setShouldPayTime(from.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
//                }
                result.add(rsp);
            }
        }
        return result;
    }

    private List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> buildDepositRsp(List<FundReceiptRepayCashDeposit> depositList, List<FundReceiptRepayBaseInfo> baseInfos) {
        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> result = new ArrayList<>();
        Map<Long, FundReceiptRepayBaseInfo> baseInfoMap = baseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        List<Long> financingIds = baseInfos.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(financingIds);
        Map<Long, FundFinancingBaseInfo> financingMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(financingIds)) {
            List<FundFinancingBaseInfo> financingBaseInfos = SpringUtil.getBean(FundFinancingBaseInfoService.class).listByIds(financingIds);
            if (!CollectionUtils.isEmpty(financingBaseInfos)) {
                financingMap = Optional.of(financingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1)))
                        .orElse(new HashMap<>());
            }
        }
        Map<Long, LocalDate[]> durationMap = this.durationGroup(baseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList()));
        for (FundReceiptRepayCashDeposit deposit : depositList) {
            LocalDate[] duration = durationMap.get(deposit.getReceiptRepayId());
            final LocalDate from;
            if (Objects.nonNull(duration) && duration.length > 0) {
                from = duration[0];
            } else {
                from = null;
            }

            BankFlowProcessingCenterFinancePaymentCashFlowRSP cashFlowRsp = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
            FundReceiptRepayBaseInfo obj = baseInfoMap.get(deposit.getReceiptRepayId());
            if (Objects.nonNull(obj)) {
                FundFinancingBaseInfo financingBaseInfo = financingMap.get(obj.getFinancingId());
                List<FundOrganization> organizationList = orgMap.get(obj.getFinancingId());
                if(CollectionUtil.isNotEmpty(organizationList)) {
                    cashFlowRsp.setOrgName(organizationList.get(0).getOrganizationName());
                }
                cashFlowRsp.setBeginInterestDate(Optional.ofNullable(financingBaseInfo)
                        .map(o -> o.getPlanLoanDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                        .orElse(""));
                cashFlowRsp.setFinancingCode(financingBaseInfo.getFinancingCode());
                cashFlowRsp.setFinancingType(FinancingTypeEnum.INDIRECT.name());
                cashFlowRsp.setFinancingId(financingBaseInfo.getId());
                cashFlowRsp.setFinancingAmount(obj.getFinancingAmount());
                cashFlowRsp.setReceiptRepayBaseId(obj.getId());
                cashFlowRsp.setReceiptRepayBaseCode(obj.getReceiptRepayCode());
            }
            cashFlowRsp.setCashFlowCode(deposit.getCashFlowCode());
            cashFlowRsp.setCashFlowItem(FinancePaymentWriteOffOrderEnum.DEPOSIT_PAY.name());
            cashFlowRsp.setShouldPayAmount(deposit.getAmount());
            cashFlowRsp.setShouldPayTime(Optional.ofNullable(from)
                    .map(o -> o.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                    .orElse(""));
            cashFlowRsp.setNoPayAmount(deposit.getAmount() - deposit.getPaidAmount());
            cashFlowRsp.setIdKey(deposit.getId() + "-DEPOSIT");
            result.add(cashFlowRsp);
        }
        return result;
    }

    private List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> buildPlanRsp(List<FundReceiptRepayCashFlow> repayCashFlowList, List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos) {
        //找到融资基本信息
        Map<Long, FundFinancingBaseInfo> inDirectMap = new HashMap<>();
        Map<Long ,List<FundOrganization>> orgMap = new HashMap<>();
        List<FundReceiptRepayBaseInfo> inDirectList = fundReceiptRepayBaseInfos.stream().filter(o -> !Objects.equals(FinancingTypeEnum.DIRECT.name(), o.getFinancingType()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(inDirectList)) {
            List<FundFinancingBaseInfo> financingBaseInfos = SpringUtil.getBean(FundFinancingBaseInfoService.class).list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                    .in(FundFinancingBaseInfo::getId, inDirectList.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())));
            if (!CollectionUtils.isEmpty(financingBaseInfos)) {
                inDirectMap = financingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (a, b) -> a));
                orgMap = organizationService.getBatchByFinancingId(financingBaseInfos.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList()));
            }
        }

        Map<Long, FundDirectFinancingBaseInfo> directMap = new HashMap<>();
        List<FundReceiptRepayBaseInfo> directList = fundReceiptRepayBaseInfos.stream().filter(o -> Objects.equals(FinancingTypeEnum.DIRECT.name(), o.getFinancingType()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(directList)) {
            List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfos = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                    .in(FundDirectFinancingBaseInfo::getId, directList.stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())));
            if (!CollectionUtils.isEmpty(directList)) {
                directMap = fundDirectFinancingBaseInfos.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity(), (a, b) -> a));
            }
        }

        Map<Long, String> financingTypeMap = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId,
                m -> Objects.equals(m.getFinancingType(), FinancingTypeEnum.DIRECT.name()) ? FinancingTypeEnum.DIRECT.name() : FinancingTypeEnum.INDIRECT.name()));

        //查询借据基本信息
        Map<Long, FundReceiptRepayBaseInfo> repayBaseInfoMap = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity(), (a, b) -> a));

        List<BankFlowProcessingCenterFinancePaymentCashFlowRSP> rspList = new ArrayList<>();
        //找到实际的核销记录
        Map<String, List<FundReceiptFlowDetail>> writeOffDetailMap = new HashMap<>();
        List<FundReceiptFlowDetail> receiptFlowDetails = SpringUtil.getBean(FundReceiptFlowDetailService.class).list(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                .in(FundReceiptFlowDetail::getCashFlowCode, repayCashFlowList.stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList())));
        if (!CollectionUtils.isEmpty(receiptFlowDetails)) {
            writeOffDetailMap = receiptFlowDetails.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        }
        for (FundReceiptRepayCashFlow cashFlow : repayCashFlowList) {
            List<FundOrganization> organizationList = orgMap.getOrDefault(cashFlow.getFinancingId(), Collections.emptyList());
            BankFlowProcessingCenterFinancePaymentCashFlowRSP dto = new BankFlowProcessingCenterFinancePaymentCashFlowRSP();
            String orgName = Optional.ofNullable(CollectionUtil.isNotEmpty(organizationList) ? organizationList.get(0) : null)
                    .map(FundOrganization::getOrganizationName)
                    .orElse(Optional.ofNullable(directMap.get(cashFlow.getFinancingId()))
                            .map(FundDirectFinancingBaseInfo::getProductName)
                            .orElse(null));
            dto.setOrgName(orgName);
            dto.setReceiptRepayBaseId(cashFlow.getReceiptRepayId());
            dto.setReceiptRepayBaseCode(Optional.ofNullable(repayBaseInfoMap.get(cashFlow.getReceiptRepayId()))
                    .map(FundReceiptRepayBaseInfo::getReceiptRepayCode).orElse(null));
            LocalDate beginDate = Optional.ofNullable(inDirectMap.get(cashFlow.getFinancingId()))
                    .map(FundFinancingBaseInfo::getActualLoanDate)
                    .orElse(Optional.ofNullable(directMap.get(cashFlow.getFinancingId()))
                            .map(FundDirectFinancingBaseInfo::getDurationFrom)
                            .orElse(null));
            if (Objects.nonNull(beginDate)) {
                dto.setBeginInterestDate(beginDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            }
            dto.setFinancingAmount(Optional.ofNullable(repayBaseInfoMap.get(cashFlow.getReceiptRepayId()))
                    .map(FundReceiptRepayBaseInfo::getFinancingAmount).orElse(null));

            dto.setCashFlowCode(cashFlow.getCashFlowCode());
            if (Objects.nonNull(cashFlow.getRepayDate())) {
                dto.setShouldPayTime(cashFlow.getRepayDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            }
            List<FundReceiptFlowDetail> fundReceiptFlowDetails = writeOffDetailMap.get(cashFlow.getCashFlowCode());
            //分别算出本金和利息的收款
            long writeOffPrincipal = 0;
            long writeOffInterest = 0;
            if (!CollectionUtils.isEmpty(fundReceiptFlowDetails)) {
                writeOffPrincipal = fundReceiptFlowDetails.stream()
                        .filter(o -> Objects.nonNull(o.getPrincipalAmount()))
                        .mapToLong(FundReceiptFlowDetail::getPrincipalAmount).summaryStatistics().getSum();
                writeOffInterest = fundReceiptFlowDetails.stream()
                        .filter(o -> Objects.nonNull(o.getInterestAmount()))
                        .mapToLong(FundReceiptFlowDetail::getInterestAmount).summaryStatistics().getSum();
            }
            String financingCode = null;
            String financingType = null;
            String businessType = null;
            if(Objects.equals(financingTypeMap.get(cashFlow.getReceiptRepayId()), FinancingTypeEnum.DIRECT.name())){
                financingCode = Optional.ofNullable(directMap.get(cashFlow.getFinancingId())).map(FundDirectFinancingBaseInfo::getFinancingCode).orElse(null);
                financingType = FinancingTypeEnum.DIRECT.name();
                businessType = Optional.ofNullable(directMap.get(cashFlow.getFinancingId())).map(FundDirectFinancingBaseInfo::getDirectFinancingType).orElse(null);
            }else if(Objects.equals(financingTypeMap.get(cashFlow.getReceiptRepayId()), FinancingTypeEnum.INDIRECT.name())){
                financingCode = Optional.ofNullable(inDirectMap.get(cashFlow.getFinancingId())).map(FundFinancingBaseInfo::getFinancingCode).orElse(null);
                financingType = FinancingTypeEnum.INDIRECT.name();
                businessType = Optional.ofNullable(inDirectMap.get(cashFlow.getFinancingId())).map(FundFinancingBaseInfo::getBusinessType).orElse(null);
            }

            if (writeOffPrincipal == 0 || writeOffPrincipal < cashFlow.getPrincipleAmount()) {
                BankFlowProcessingCenterFinancePaymentCashFlowRSP principalDto = BeanUtil.copyProperties(dto, BankFlowProcessingCenterFinancePaymentCashFlowRSP.class);
                principalDto.setShouldPayAmount(cashFlow.getPrincipleAmount());
                principalDto.setNoPayAmount(LongUtil.null2zero(cashFlow.getPrincipleAmount()) - writeOffPrincipal);
                principalDto.setCashFlowItem(FinancePaymentWriteOffOrderEnum.PRINCIPAL.name());
                principalDto.setIdKey(cashFlow.getCashFlowCode() + "_" + FinancePaymentWriteOffOrderEnum.valueOf(FinancePaymentWriteOffOrderEnum.PRINCIPAL.name()).getSort());
                principalDto.setPhase(cashFlow.getPhase());
                principalDto.setFinancingCode(financingCode);
                principalDto.setFinancingId(cashFlow.getFinancingId());
                principalDto.setFinancingType(financingType);
                principalDto.setBusinessType(businessType);
                rspList.add(principalDto);
            }
            if (writeOffInterest == 0 || writeOffInterest < cashFlow.getInterestAmount()) {
                BankFlowProcessingCenterFinancePaymentCashFlowRSP interestDto = BeanUtil.copyProperties(dto, BankFlowProcessingCenterFinancePaymentCashFlowRSP.class);
                interestDto.setShouldPayAmount(cashFlow.getInterestAmount());
                interestDto.setNoPayAmount(LongUtil.null2zero(cashFlow.getInterestAmount()) - writeOffInterest);
                interestDto.setCashFlowItem(FinancePaymentWriteOffOrderEnum.INTEREST.name());
                interestDto.setIdKey(cashFlow.getCashFlowCode() + "_" + FinancePaymentWriteOffOrderEnum.valueOf(FinancePaymentWriteOffOrderEnum.INTEREST.name()).getSort());
                interestDto.setPhase(cashFlow.getPhase());
                interestDto.setFinancingCode(financingCode);
                interestDto.setFinancingId(cashFlow.getFinancingId());
                interestDto.setFinancingType(financingType);
                interestDto.setBusinessType(businessType);
                rspList.add(interestDto);
            }
        }
        return rspList;
    }

    public List<BankFlowProcessingCenterListRSP> listFlow(BankFlowQueryREQ req) {
        BankFlowProcessingCenterListREQ listREQ = new BankFlowProcessingCenterListREQ();
        listREQ.setPage(1);
        listREQ.setPageSize(Integer.MAX_VALUE);
        listREQ.setFinancingFlowIdList(req.getBankFlowIds());
        return SpringUtil.getBean(BankFlowProcessingCenterService.class).tabList(listREQ).getList();
    }

    @Data
    private static class FinancingExtraInfo {
        private List<Long> orgIds;
        private List<String> orgNames;
        private LocalDate durationFrom;
        private LocalDate durationTo;
        private String financingCode;
        private Integer isDirect;
        private String businessType;
    }
}
