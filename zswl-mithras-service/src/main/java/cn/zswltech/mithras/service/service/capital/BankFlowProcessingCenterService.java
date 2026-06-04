package cn.zswltech.mithras.service.service.capital;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.domain.enums.BankFlowPaymentCollectionTypeEnum;
import cn.zswltech.mithras.capital.domain.enums.BankFlowWriteOffTypeEnum;
import cn.zswltech.mithras.capital.domain.enums.CollectionWriteOffOrderEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.capital.domain.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.capital.domain.enums.PaymentWriteOffOrderEnum;
import cn.zswltech.mithras.service.enums.capital.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.third.enums.capital.DataSourceEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundFinancingCollectRecordMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.margin.mapper.WarrantyBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.WarrantyRecordInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundFinancingCollectRecord;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.margin.mapper.model.WarrantyBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.WarrantyRecordInfo;
import cn.zswltech.mithras.finance.mapper.model.nettingRefund.NettingRefund;
import cn.zswltech.mithras.finance.service.capital.NettingRefundService;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailUnconfirmedMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.third.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PlanCollectionVO;
import cn.zswltech.mithras.third.financialshare.application.dto.SyncCqReqBizInfo;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yangxiong
 * @date 2024/5/18/16:49
 * @description
 */
@Slf4j
@Service
public class BankFlowProcessingCenterService {

    public static final String PATTERN = "yyyy/MM/dd";
    public static final String WRITE_OFFED = "%s已核销%s元";

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FinanceFlowWriteOffDetailMapper financeFlowWriteOffDetailMapper;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private FinanceFlowRecordMapper financeFlowRecordMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private NettingRefundService nettingRefundService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;


    public PageR<BankFlowProcessingCenterListRSP> tabList(BankFlowProcessingCenterListREQ req) {
        String query = BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(req.getCollectionPaymentType()) ?
                "SELECT id FROM finance_flow_record WHERE creditamount > 0" : "SELECT id FROM finance_flow_record WHERE debitamount > 0";
        LocalDateTime localDateTime = null;
        if (ObjectUtil.isNotNull(req.getTransactionDateTo())) {
            localDateTime = LocalDateTimeUtil.parse(req.getTransactionDateTo(), DatePattern.NORM_DATE_PATTERN).plusDays(1);
        }
        Page<FinanceFlowRecord> recordPage = financeFlowRecordService.page(new Page<FinanceFlowRecord>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<FinanceFlowRecord>().eq(FinanceFlowRecord::getFinancingFlowType, req.getTabType())
                        .eq(CharSequenceUtil.isNotBlank(req.getTabType()), FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                        .in(!CollectionUtils.isEmpty(req.getFinancingFlowIdList()), FinanceFlowRecord::getId, req.getFinancingFlowIdList())
                        .like(CharSequenceUtil.isNotBlank(req.getOtherName()), FinanceFlowRecord::getOppunit, req.getOtherName())
                        .ge(CharSequenceUtil.isNotBlank(req.getTransactionDateFrom()), FinanceFlowRecord::getBiztime, LocalDateTimeUtil.parse(req.getTransactionDateFrom(), DatePattern.NORM_DATE_PATTERN))
                        .lt(ObjectUtil.isNotNull(localDateTime), FinanceFlowRecord::getBiztime, localDateTime)
                        .like(CharSequenceUtil.isNotBlank(req.getBankName()), FinanceFlowRecord::getBankName, req.getBankName())
                        .like(CharSequenceUtil.isNotBlank(req.getBankAccount()), FinanceFlowRecord::getAccountbankBankaccountnumber, req.getBankAccount())
                        .like(CharSequenceUtil.isNotBlank(req.getOtherBankName()), FinanceFlowRecord::getOppbank, req.getOtherBankName())
                        .like(CharSequenceUtil.isNotBlank(req.getOtherBankAccount()), FinanceFlowRecord::getOppbanknumber, req.getOtherBankAccount())
                        .like(CharSequenceUtil.isNotBlank(req.getTransactionDetailsNumber()), FinanceFlowRecord::getBillno, req.getTransactionDetailsNumber())
                        .like(CharSequenceUtil.isNotBlank(req.getMainInfo()), FinanceFlowRecord::getDescription, req.getMainInfo())
                        .inSql(CharSequenceUtil.isNotBlank(req.getCollectionPaymentType()), FinanceFlowRecord::getId, query)
                        .orderByAsc(FinanceFlowRecord::getSort)
                        .orderByDesc(FinanceFlowRecord::getLastmodifytime));

        if (CollUtil.isEmpty(recordPage.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<BankFlowProcessingCenterListRSP> bankFlowProcessingCenterList = new ArrayList<>(recordPage.getRecords().size());
        fillDataHandler(bankFlowProcessingCenterList, recordPage.getRecords());
        return PageR.of(bankFlowProcessingCenterList, recordPage.getTotal(), recordPage.getCurrent(), recordPage.getSize());
    }

    private void fillDataHandler(List<BankFlowProcessingCenterListRSP> bankFlowProcessingCenterList,
                                 List<FinanceFlowRecord> financeFlowRecords) {
        financeFlowRecords.sort(Comparator.comparing(a -> Optional.ofNullable(FinancingFlowWriteOffStatusEnum.of(a.getWriteOffStatus()))
                .map(FinancingFlowWriteOffStatusEnum::getSort).orElse(99)));
        financeFlowRecords.forEach(dto -> {
            BankFlowProcessingCenterListRSP rsp = new BankFlowProcessingCenterListRSP();
            rsp.setId(dto.getId());
            rsp.setTransactionDetailsNumber(dto.getBillno());
            rsp.setFinancialOrganization(dto.getCompanyName());
            rsp.setBankAccount(dto.getAccountbankBankaccountnumber());
            rsp.setBankName(dto.getBankName());
            rsp.setCurrency(dto.getCurrencyName());
            rsp.setTransactionDate(LocalDateTimeUtil.format(dto.getBiztime(), DatePattern.NORM_DATETIME_PATTERN));
            rsp.setMainInfo(dto.getDescription());
            rsp.setCollectionAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getCreditamount()).orElse(0.00))));
            rsp.setPaymentAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getDebitamount()).orElse(0.00))));
            rsp.setDepositAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getTransbalance()).orElse(0.00))));
            rsp.setHandingFees(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getTransfercharge()).orElse(0.00))));
            rsp.setOtherName(dto.getOppunit());
            rsp.setOtherBankAccount(dto.getOppbanknumber());
            rsp.setOtherBankName(dto.getOppbank());
            rsp.setDetailSerialNumber(dto.getDetailid());
            rsp.setDataSource(Optional.ofNullable(DataSourceEnum.of(dto.getDatasource())).map(DataSourceEnum::getDisplay).orElse(null));
            if (Objects.nonNull(dto.getLastmodifytime())) {
                rsp.setUpdateTime(dto.getLastmodifytime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            }
            rsp.setPromptLabel(String.valueOf(dto.getLogicDeleteFlag()));
            setWriteOffedAmountList(rsp, dto);
            rsp.setFlowStatus(dto.getWriteOffStatus());
            rsp.setCicoBruid(dto.getCicoBruid());
            setWriteOffByTab(dto, rsp);
            bankFlowProcessingCenterList.add(rsp);
        });
    }

    private void setWriteOffByTab(FinanceFlowRecord dto, BankFlowProcessingCenterListRSP rsp) {
        if (dto.getFinancingFlowType().equals(BankFlowCenterTypeEnum.PROCESSING_CENTER.name())) {
            rsp.setFlowStatus(dto.getWriteOffStatus());
        } else {
            if (ObjectUtil.isNull(dto.getWriteOffStatus())) {
                rsp.setFlowStatus(dto.getWriteOffType());
            }
            if (ObjectUtil.isNotNull(dto.getWriteOffStatus()) && dto.getWriteOffStatus().equals(FinancingFlowWriteOffStatusEnum.NO_WRITE_OFF.name())) {
                rsp.setFlowStatus(dto.getWriteOffStatus());
            }
            if (!CharSequenceUtil.equals(dto.getFinancingFlowType(), BankFlowCenterTypeEnum.PROCESSING_CENTER.name())) {
                rsp.setFlowStatus(dto.getWriteOffType());
            }
        }

    }

    private void setWriteOffedAmountList(BankFlowProcessingCenterListRSP rsp, FinanceFlowRecord dto) {
        if (ObjectUtil.isNull(dto.getFinancingProjectType())) {
            rsp.setWriteOffedAmountList(Collections.emptyList());
            return;
        }
        List<BankFlowCenterListDTO> results = new ArrayList<>();
        if (BankFlowWriteOffTypeEnum.FUNDS_END.name().equals(dto.getFinancingProjectType())) {
            FundReceiptFlowDetailMapper fundReceiptFlowDetailMapper = getBean(FundReceiptFlowDetailMapper.class);
            //付款
            List<FundReceiptFlowDetail> fundReceiptFlowDetails = fundReceiptFlowDetailMapper.selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                    .eq(FundReceiptFlowDetail::getFinanceFlowId, dto.getId()));
            if (CollUtil.isNotEmpty(fundReceiptFlowDetails)) {
                fundReceiptFlowDetails.forEach(detail -> {
                    BankFlowCenterListDTO bankFlowCenterListDto = new BankFlowCenterListDTO();
                    bankFlowCenterListDto.setWriteOffAmount(detail.getTotalAmount());
                    bankFlowCenterListDto.setWriteOffTime(detail.getCashFlowDate().format(DateTimeFormatter.ofPattern(PATTERN)));
                    results.add(bankFlowCenterListDto);
                });
            }
        }

        if (BankFlowWriteOffTypeEnum.PROJ_SIDE.name().equals(dto.getFinancingProjectType())) {
            //查询项目端流水使用情况, 分为付款和收款
            if (ObjectUtil.isNotNull(dto.getDebitamount()) && dto.getDebitamount().doubleValue() != 0) {
                //付款
                List<PaymentActualDetail> paymentActualDetails = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getFinanceFlowId, dto.getId()));
                if (CollUtil.isNotEmpty(paymentActualDetails)) {
                    paymentActualDetails.forEach(d -> results.add(new BankFlowCenterListDTO(d.getPaidInAmount(), d.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN)))));
                }
                List<MarginRecordInfo> marginRecordInfos = getBean(MarginRecordInfoMapper.class).selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                        .eq(MarginRecordInfo::getFinanceFlowId, dto.getId()));
                if (CollUtil.isNotEmpty(marginRecordInfos)) {
                    marginRecordInfos.forEach(d -> results.add(new BankFlowCenterListDTO(d.getCollectionAmount(), d.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN)))));
                }
            }
            if (ObjectUtil.isNotNull(dto.getCreditamount()) && dto.getCreditamount().doubleValue() != 0) {
                //收款
                List<CollectionRecordInfo> collectionRecordInfos = getBean(CollectionRecordInfoMapper.class).selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                        .eq(CollectionRecordInfo::getFinanceFlowId, dto.getId()));
                if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                    collectionRecordInfos.forEach(d -> {
                        if (ObjectUtil.isNotNull(d.getPenaltyInterest()) && d.getPenaltyInterest() > 0) {
                            results.add(new BankFlowCenterListDTO(d.getPenaltyInterest(), d.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN))));
                        } else {
                            results.add(new BankFlowCenterListDTO(d.getCollectionAmount(), d.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN))));
                        }
                    });
                }
                List<FundFinancingCollectRecord> fundFinancingCollectRecords = getBean(FundFinancingCollectRecordMapper.class).selectList(Wrappers.<FundFinancingCollectRecord>lambdaQuery()
                        .eq(FundFinancingCollectRecord::getFinanceFlowId, dto.getId()));
                if (CollUtil.isNotEmpty(fundFinancingCollectRecords)) {
                    fundFinancingCollectRecords.forEach(d -> results.add(new BankFlowCenterListDTO(d.getCollectionAmount(), d.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN)))));
                }
            }
        }
        rsp.setWriteOffedAmountList(results);
    }

    public void noProcessingRequire(NoProcessingRequireREQ req) {
        financeFlowRecordService.lambdaUpdate()
                .in(FinanceFlowRecord::getId, req.getFinanceFlowIds())
                .set(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.NO_PROCESSING_REQUIRE.name())
                .update();
    }

    public BankFlowProcessingCenterProjDetailRSP projAmountDetail(ProjAmountDetailREQ req) {
        BankFlowProcessingCenterProjDetailRSP rsp = new BankFlowProcessingCenterProjDetailRSP();
        if (BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(req.getWriteOffType())) {
            //收款
            handlerCollection(req, rsp);
        }

        if (BankFlowPaymentCollectionTypeEnum.PAYMENT.name().equals(req.getWriteOffType())) {
            //付款
            handlerPayment(req, rsp);
        }
        return rsp;
    }

    private void handlerPayment(ProjAmountDetailREQ req, BankFlowProcessingCenterProjDetailRSP rsp) {
        String cashFlowItem = req.getCashFlowItem();
        //付款分为授信款和保证金，需要分开处理
        if (PaymentWriteOffOrderEnum.INVESTMENTS_FUNDS.name().equals(cashFlowItem)) {
            //授信款
            int index = req.getCashFlowCode().lastIndexOf("-");
            PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectOne(
                    Wrappers.<PaymentBaseInfo>lambdaQuery()
                            .eq(PaymentBaseInfo::getPaymentCode, req.getCashFlowCode().substring(0, index))
                            .eq(PaymentBaseInfo::getContractId, req.getContractId())
                            .last(StringUtil.mysqlLimitOne()));
            if (paymentBaseInfo == null) {
                throw new MithrasException("付款信息不存在");
            }
            //计划核销明细
            String seq = req.getCashFlowCode().substring(index + 1);
            List<PaymentActualDetailUnconfirmed> detailUnConfirmedList = getBean(PaymentActualDetailUnconfirmedMapper.class)
                    .selectList(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                            .eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentBaseInfo.getId())
                            .eq(PaymentActualDetailUnconfirmed::getSeqCode, seq)
                            .in(PaymentActualDetailUnconfirmed::getWriteOffStatus, Arrays.asList(WriteOffStatus.CONFIRM.name(), WriteOffStatus.COMMIT.name(), WriteOffStatus.TO_BE_WRITE_OFF.name())));
            if (CollUtil.isEmpty(detailUnConfirmedList)) {
                throw new MithrasException("该投放款不存在流程中或者已确认的投放款");
            }

            //以已确认的纬度返回需要核销的金额
            detailUnConfirmedList.forEach(unconfirmed -> {
                //实际核销明细
                List<PaymentActualDetail> paymentActualDetails = getBean(PaymentActualDetailMapper.class)
                        .selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                                .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId())
                                .eq(PaymentActualDetail::getUnConfirmedId, unconfirmed.getId()));

                rsp.setModelId(unconfirmed.getId());
                if (CollUtil.isEmpty(paymentActualDetails)) {
                    rsp.setShouldPayAmount(unconfirmed.getPaidInAmount());
                    rsp.setWriteOffedAmount(Collections.emptyList());
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(unconfirmed.getPaidInDate(), DatePattern.NORM_DATE_PATTERN));
                    rsp.setNoPayAmount(LongUtil.null2zero(unconfirmed.getPaidInAmount()));
                    return;
                }

                List<String> paidAmountList = new ArrayList<>();
                for (PaymentActualDetail paymentActualDetail : paymentActualDetails) {
                    //设置未还金额
                    String format = String.format(WRITE_OFFED, LocalDateTimeUtil.format(paymentActualDetail.getPaidInDate(), PATTERN),
                            LongUtil.tenThousand2Dollar(String.valueOf(paymentActualDetail.getPaidInAmount())).setScale(2, RoundingMode.HALF_UP));
                    paidAmountList.add(format);
                }
                rsp.setWriteOffedAmount(paidAmountList);
                rsp.setShouldPayAmount(unconfirmed.getPaidInAmount());
                long noPayAmount = LongUtil.null2zero(unconfirmed.getPaidInAmount())
                        - LongUtil.null2zero(paymentActualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum());
                rsp.setNoPayAmount(noPayAmount < 0 ? unconfirmed.getPaidInAmount() : noPayAmount);
                rsp.setShouldPayTime(LocalDateTimeUtil.format(unconfirmed.getPaidInDate(), DatePattern.NORM_DATE_PATTERN));
            });
        } else if (PaymentWriteOffOrderEnum.EARNEST_MONEY.name().equals(cashFlowItem)) {
            //保证金
            MarginBaseInfo marginBaseInfo = getBean(MarginBaseInfoMapper.class).selectOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                    .eq(MarginBaseInfo::getContractId, req.getContractId())
                    .eq(MarginBaseInfo::getMarginCode, req.getCashFlowCode())
                    .last(StringUtil.mysqlLimitOne()));
            if (marginBaseInfo == null) {
                throw new MithrasException("保证金信息不存在");
            }
            List<MarginRecordInfo> marginRecordInfos = getBean(MarginRecordInfoMapper.class)
                    .selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                            .eq(MarginRecordInfo::getMarginId, marginBaseInfo.getId()));

            rsp.setModelId(marginBaseInfo.getId());
            rsp.setShouldPayAmount(LongUtil.null2zero(marginBaseInfo.getCollectionAmount()));
            //查询实际租金表的最后一期
            ContractRentActual contractRentActual = getBean(ContractRentActualMapper.class).selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, req.getContractId())
                    .orderByDesc(ContractRentActual::getCashFlowPhase)
                    .last(StringUtil.mysqlLimitOne()));
            if (contractRentActual == null) {
                throw new MithrasException("实际租金表不存在");
            }
            rsp.setShouldPayTime(LocalDateTimeUtil.format(contractRentActual.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setNoPayAmount(LongUtil.null2zero(marginBaseInfo.getCollectionAmount()));
            List<String> writeOffAmountList = new ArrayList<>();
            if (CollUtil.isNotEmpty(marginRecordInfos)) {
                for (MarginRecordInfo marginRecordInfo : marginRecordInfos) {
                    String format = String.format(WRITE_OFFED, LocalDateTimeUtil.format(marginRecordInfo.getCollectionDate(), PATTERN),
                            LongUtil.tenThousand2Dollar(String.valueOf(marginRecordInfo.getCollectionAmount())).setScale(2, RoundingMode.HALF_UP));
                    writeOffAmountList.add(format);
                }
            }
            rsp.setWriteOffedAmount(writeOffAmountList);
        } else if (PaymentWriteOffOrderEnum.RETENTION_MONEY.name().equals(cashFlowItem)) {
            //质保金
            WarrantyBaseInfo warrantyBaseInfo = getBean(WarrantyBaseInfoMapper.class).selectOne(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                    .eq(WarrantyBaseInfo::getContractId, req.getContractId())
                    .eq(WarrantyBaseInfo::getWarrantyCode, req.getCashFlowCode())
                    .last(StringUtil.mysqlLimitOne()));
            if (warrantyBaseInfo == null) {
                throw new MithrasException("质保金信息不存在");
            }
            List<WarrantyRecordInfo> warrantyRecordInfos = getBean(WarrantyRecordInfoMapper.class)
                    .selectList(Wrappers.<WarrantyRecordInfo>lambdaQuery()
                            .eq(WarrantyRecordInfo::getWarrantyId, warrantyBaseInfo.getId()));

            rsp.setModelId(warrantyBaseInfo.getId());
            rsp.setShouldPayAmount(LongUtil.null2zero(warrantyBaseInfo.getCollectionAmount()));
            //查询实际租金表的最后一期
            ContractRentActual contractRentActual = getBean(ContractRentActualMapper.class).selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, req.getContractId())
                    .orderByDesc(ContractRentActual::getCashFlowPhase)
                    .last(StringUtil.mysqlLimitOne()));
            if (contractRentActual == null) {
                throw new MithrasException("实际租金表不存在");
            }
            rsp.setShouldPayTime(LocalDateTimeUtil.format(contractRentActual.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setNoPayAmount(LongUtil.null2zero(warrantyBaseInfo.getCollectionAmount()));
            List<String> writeOffAmountList = new ArrayList<>();
            if (CollUtil.isNotEmpty(warrantyRecordInfos)) {
                for (WarrantyRecordInfo warrantyRecordInfo : warrantyRecordInfos) {
                    String format = String.format(WRITE_OFFED, LocalDateTimeUtil.format(warrantyRecordInfo.getCollectionDate(), PATTERN),
                            LongUtil.tenThousand2Dollar(String.valueOf(warrantyRecordInfo.getCollectionAmount())).setScale(2, RoundingMode.HALF_UP));
                    writeOffAmountList.add(format);
                }
            }
            rsp.setWriteOffedAmount(writeOffAmountList);
        }
    }

    private static void handlerCollection(ProjAmountDetailREQ req, BankFlowProcessingCenterProjDetailRSP rsp) {
        String cashFlowItem = req.getCashFlowItem();
        if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), CollectionWriteOffOrderEnum.PENALTY_INTEREST.name(),
                CollectionWriteOffOrderEnum.INTEREST.name(), CollectionWriteOffOrderEnum.PRINCIPAL.name(),
                CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
            req.setCashFlowItem(CashFlowItemEnum.RENT.name());
        }
        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, req.getCashFlowItem())
                .eq(CollectionBaseInfo::getContractId, req.getContractId())
                .eq(CollectionBaseInfo::getCode, req.getCashFlowCode())
                .last(StringUtil.mysqlLimitOne()));

        if (collectionBaseInfo == null) {
            throw new MithrasException("找不到该期项的收款记录");
        }
        if("1".equals(collectionBaseInfo.getRetreatLock())){
            throw new MithrasException("该租金期次处于保证金退抵流程中，请等待结束后操作！");
        }

        /*质保金核销  付款申请中如果质保金为内扣则不允许手工核销 */
        if(CollectionWriteOffOrderEnum.RETENTION_MONEY.name().equals(req.getCashFlowItem())) {
            PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, req.getContractId())
                    .eq(PaymentBaseInfo::getPaymentCode, req.getCashFlowCode().replace("-zbj", ""))
                    .last(StringUtil.mysqlLimitOne()));
            if (paymentBaseInfo.getRetentionMoneyType() == 0) {
                throw new MithrasException("质保金为自动内扣收款，无需手工核销！");
            }
        }

        List<CollectionRecordInfo> collectionRecordInfos = getBean(CollectionRecordInfoMapper.class).selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, collectionBaseInfo.getId()));
        //设置已经核销的钱
        if (CollUtil.isEmpty(collectionRecordInfos)) {
            rsp.setWriteOffedAmount(Collections.emptyList());
        } else {
            List<String> collectedAmounts = new ArrayList<>();
            for (CollectionRecordInfo collectionRecordInfo : collectionRecordInfos) {
                String format = String.format(WRITE_OFFED, LocalDateTimeUtil.format(collectionRecordInfo.getCollectionDate(), PATTERN),
                        LongUtil.tenThousand2Dollar(String.valueOf(collectionRecordInfo.getCollectionAmount())).setScale(2, RoundingMode.HALF_UP));
                collectedAmounts.add(format);
            }
            rsp.setWriteOffedAmount(collectedAmounts);
        }
        rsp.setModelId(collectionBaseInfo.getId());
        //设置应收时间
        rsp.setShouldPayTime(LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), DatePattern.NORM_DATETIME_PATTERN));
        if (Objects.equals(req.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
            //根据不同的类型做不同的处理
            if (CollectionWriteOffOrderEnum.PRINCIPAL.name().equals(cashFlowItem)) {
                //本金
                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPrincipal()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal()));
                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPrincipal()));
            }
            if (CollectionWriteOffOrderEnum.INTEREST.name().equals(cashFlowItem)) {
                //利息
                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionInterest()));
                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()));
            }
            if (CollectionWriteOffOrderEnum.PENALTY_INTEREST.name().equals(cashFlowItem)) {
                //罚息
                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()));
                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
            }
            if (CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name().equals(cashFlowItem)) {
                //首期利息
                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
            }
        } else {
            //可能存在else穿透，待确认业务
            rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
            rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
        }
    }

    public List<BankCenterSubTableProjectListRSP> subListByCashFlowId(List<Long> financingFlowIdList) {
        List<BankCenterSubTableProjectListRSP> rspList = new ArrayList<>();
        financingFlowIdList.forEach(financingFlowId -> {
            FinanceFlowRecord financeFlowRecord = getBean(FinanceFlowRecordMapper.class).selectById(financingFlowId);
            if (financeFlowRecord == null) {
                return;
            }
            handlerPayment(financingFlowId, financeFlowRecord, rspList);
            handlerCollection(financingFlowId, financeFlowRecord, rspList);
        });
        return rspList;
    }

    private static void handlerCollection(Long financingFlowId, FinanceFlowRecord financeFlowRecord, List<BankCenterSubTableProjectListRSP> rspList) {
        if (Boolean.TRUE.equals(Objects.nonNull(financeFlowRecord.getCreditamount()) && financeFlowRecord.getCreditamount() != 0)
                && BankFlowWriteOffTypeEnum.PROJ_SIDE.name().equals(financeFlowRecord.getFinancingProjectType())) {
            //收钱
            List<CollectionRecordInfo> collectionRecordInfos = getBean(CollectionRecordInfoMapper.class).selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                    .eq(CollectionRecordInfo::getFinanceFlowId, financingFlowId));

            if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                Map<Long, List<CollectionRecordInfo>> listMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
                listMap.forEach((collectionId, collectionRecordInfoList) -> {
                    for (CollectionRecordInfo recordInfo : collectionRecordInfoList) {
                        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectById(collectionId);
                        BankCenterSubTableProjectListRSP rsp = new BankCenterSubTableProjectListRSP();
                        rsp.setCashFlowItemName(CashFlowItemEnum.of(collectionBaseInfo.getCashFlowItem()).display);
                        rsp.setClientId(collectionBaseInfo.getClientId());
                        rsp.setCashFlowCode(collectionBaseInfo.getCode());
                        rsp.setClientName(getBean(Id2NameService.class).clientId2NameSingle(collectionBaseInfo.getClientId()));
                        rsp.setContractCode(collectionBaseInfo.getContractCode());
                        rsp.setThisWriteOffAmount(LongUtil.null2zero(recordInfo.getCollectionAmount()));
                        rsp.setShouldPayTime(LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), PATTERN));
                        //根据不同的类型做不同的处理
                        if (CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
                            if (ObjectUtil.isNotNull(recordInfo.getPrincipal()) && recordInfo.getPrincipal() > 0) {
                                //本金
                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPrincipal()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal()));
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPrincipal()));
                                rsp.setThisWriteOffAmount(LongUtil.null2zero(recordInfo.getPrincipal()));
                                rsp.setCashFlowItemName(CollectionWriteOffOrderEnum.PRINCIPAL.display());
                            }
                            if (ObjectUtil.isNotNull(recordInfo.getInterest()) && recordInfo.getInterest() > 0) {
                                //利息
                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionInterest()));
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()));
                                rsp.setThisWriteOffAmount(LongUtil.null2zero(recordInfo.getInterest()));
                                rsp.setCashFlowItemName(CollectionWriteOffOrderEnum.INTEREST.display());
                            }
                            if (ObjectUtil.isNotNull(recordInfo.getPenaltyInterest()) && recordInfo.getPenaltyInterest() > 0) {
                                //罚息
                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()));
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                                rsp.setThisWriteOffAmount(LongUtil.null2zero(recordInfo.getPenaltyInterest()));
                                rsp.setCashFlowItemName(CollectionWriteOffOrderEnum.PENALTY_INTEREST.display());
                            }
                            if (collectionBaseInfo.getPhase() == 0) {
                                //利息
                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
                                rsp.setThisWriteOffAmount(LongUtil.null2zero(recordInfo.getCollectionAmount()));
                                rsp.setCashFlowItemName(CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.display());
                            }
                        } else {
                            if (CharSequenceUtil.equalsAny(collectionBaseInfo.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name(), CashFlowItemEnum.COMMISSION.name(),
                                    CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name(), CashFlowItemEnum.OTHERAMOUNT.name(),
                                    CashFlowItemEnum.RETENTION_MONEY.name(), CashFlowItemEnum.EARLY_STOP_COMPENSATION.name(), CashFlowItemEnum.NOMINAL_PRICE.name())) {

                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
                            } else {
                                //可能存在else穿透，罚息
                                rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
                                rsp.setNoPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
                            }
                        }
                        rsp.setStatus(financeFlowRecord.getWriteOffType());
                        rsp.setPlatform(PlatformApiEnum.CQ2_PLAN_COLLECTION.name());
                        rsp.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
                        rsp.setId(recordInfo.getId());
                        rspList.add(rsp);
                    }

                });
            }
        }
    }

    //项目端付款核销记录
    private static void handlerPayment(Long financingFlowId, FinanceFlowRecord financeFlowRecord, List<BankCenterSubTableProjectListRSP> rspList) {
        if (Boolean.TRUE.equals(Objects.nonNull(financeFlowRecord.getDebitamount()) && financeFlowRecord.getDebitamount() != 0)
                && BankFlowWriteOffTypeEnum.PROJ_SIDE.name().equals(financeFlowRecord.getFinancingProjectType())) {
            //付款
            List<PaymentActualDetail> paymentActualDetails = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getFinanceFlowId, financingFlowId));

            if (CollUtil.isNotEmpty(paymentActualDetails)) {
                for (PaymentActualDetail detail : paymentActualDetails) {
                    PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectById(detail.getPaymentId());
                    List<PaymentActualDetailUnconfirmed> confirmedList = SpringUtil.getBean(PaymentActualDetailUnconfirmedService.class).listConfirmedByPaymentId(detail.getPaymentId());
                    long planPayAmount = confirmedList.stream().mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount).sum();
                    //构建手工核销页面的列表数据
                    BankCenterSubTableProjectListRSP rsp = new BankCenterSubTableProjectListRSP();
                    rsp.setCashFlowItemName(PaymentWriteOffOrderEnum.INVESTMENTS_FUNDS.display());
                    rsp.setClientName(Optional.ofNullable(getBean(ClientService.class).getById(paymentBaseInfo.getClientId())).map(Client::getClientName).orElse(null));
                    rsp.setContractCode(paymentBaseInfo.getContractCode());
                    rsp.setCashFlowCode(paymentBaseInfo.getPaymentCode());
//                    rsp.setShouldPayAmount(paymentBaseInfo.getApplyPaymentAmount());
                    rsp.setShouldPayAmount(planPayAmount);
                    rsp.setShouldPayTime(LocalDateTimeUtil.format(paymentBaseInfo.getApplyPaymentDate(), DatePattern.NORM_DATETIME_PATTERN));
                    //这里算未还金额需要根据付款申请反查付款详情
                    List<PaymentActualDetail> currentDetailList = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId())
                            .eq(PaymentActualDetail::getFinanceFlowId, financingFlowId));
                    Long allWriteOffAmount = LongUtil.null2zero(currentDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
//                    long unCollectPaymentAmount = LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount()) - allWriteOffAmount;
                    long unCollectPaymentAmount = planPayAmount - allWriteOffAmount;
                    rsp.setNoPayAmount(unCollectPaymentAmount);
                    rsp.setThisWriteOffAmount(detail.getPaidInAmount());
                    rsp.setStatus(ObjectUtil.isNull(financeFlowRecord.getWriteOffType()) ? financeFlowRecord.getWriteOffStatus() : financeFlowRecord.getWriteOffType());
                    rsp.setPlatform(PlatformApiEnum.CQ2_PAYMENT.name());
                    rsp.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
                    rsp.setId(detail.getId());
                    rsp.setPaymentActualDetailId(detail.getId());
                    rspList.add(rsp);
                }
            }

            //保证金
            List<MarginRecordInfo> marginRecordInfos = getBean(MarginRecordInfoMapper.class).selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                    .eq(MarginRecordInfo::getCollectionType, RecordTypeEnum.REFUND.name())
                    .eq(MarginRecordInfo::getFinanceFlowId, financingFlowId)
                    .eq(MarginRecordInfo::getRecordType, RecordTypeEnum.REFUND_MARGIN.name()));

            if (CollUtil.isNotEmpty(marginRecordInfos)) {
                MarginBaseInfo marginBaseInfo = getBean(MarginBaseInfoMapper.class).selectById(marginRecordInfos.get(0).getMarginId());
                if (marginBaseInfo == null) {
                    return;
                }
                //根据合同ID查询最后一期租金
                CollectionBaseInfo lastRentPlan = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId())
                        .orderByDesc(CollectionBaseInfo::getPhase)
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNotNull(lastRentPlan)) {
                    marginRecordInfos.forEach(recordInfo -> {
                        //构建手工核销页面的列表数据
                        BankCenterSubTableProjectListRSP rsp = new BankCenterSubTableProjectListRSP();
                        rsp.setCashFlowItemName(PaymentWriteOffOrderEnum.INVESTMENTS_FUNDS.display());
                        rsp.setClientName(Optional.ofNullable(getBean(ClientService.class).getById(marginBaseInfo.getClientId())).map(Client::getClientName).orElse(null));
                        rsp.setContractCode(marginBaseInfo.getContractCode());
                        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .eq(CollectionBaseInfo::getCode, marginBaseInfo.getMarginCode())
                                .last(StringUtil.mysqlLimitOne()));
                        PaymentBaseInfo paymentBaseInfo = null;
                        if (Objects.nonNull(collectionBaseInfo)) {
                            paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectById(collectionBaseInfo.getPaymentId());
                            if (Objects.nonNull(paymentBaseInfo)) {
                                rsp.setCashFlowCode(collectionBaseInfo.getCode());
                            }
                        }
                        rsp.setShouldPayAmount(marginBaseInfo.getCollectionAmount());
                        Long allWriteOffAmount = LongUtil.null2zero(marginRecordInfos.stream().mapToLong(MarginRecordInfo::getCollectionAmount).sum());
                        long unCollectPaymentAmount = LongUtil.null2zero(marginBaseInfo.getPlanMarginAmount()) - allWriteOffAmount;
                        rsp.setNoPayAmount(unCollectPaymentAmount);
                        rsp.setShouldPayTime(lastRentPlan.getPlanCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                        rsp.setThisWriteOffAmount(recordInfo.getCollectionAmount());
                        rsp.setStatus(ObjectUtil.isNull(financeFlowRecord.getWriteOffType()) ? financeFlowRecord.getWriteOffStatus() : financeFlowRecord.getWriteOffType());
                        rsp.setPlatform(PlatformApiEnum.CQ2_PAYMENT.name());
                        rsp.setSource(ExceptionSourceENUM.BUSINESS_MARGIN.name());
                        rsp.setId(recordInfo.getId());
                        rspList.add(rsp);
                    });
                }

            }
        }
    }

    public void restore(Collection<Long> ids) {
        List<FinanceFlowRecord> financeFlowRecordList = financeFlowRecordService.listByIds(ids);
        if (CollectionUtil.isEmpty(financeFlowRecordList)) {
            return;
        }
        for (FinanceFlowRecord item : financeFlowRecordList) {
            if (!Objects.equals(item.getFinancingFlowType(), BankFlowCenterTypeEnum.NO_PROCESSING_REQUIRE.name())) {
                throw new MithrasException("交易明细编号为[" + item.getBillno() + "]的银行流水不允许进行还原操作");
            }
        }
        List<FinanceFlowRecord> updateList = financeFlowRecordList.stream().map(e -> {
            FinanceFlowRecord toUpdate = new FinanceFlowRecord();
            toUpdate.setId(e.getId());
            toUpdate.setFinancingFlowType(BankFlowCenterTypeEnum.PROCESSING_CENTER.name());
            return toUpdate;
        }).collect(Collectors.toList());
        financeFlowRecordService.updateBatchById(updateList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(FinanceFlowDeleteREQ req) {
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordService.listByIds(req.getFinanceFlowIds());
        if (CollUtil.isEmpty(financeFlowRecords)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (financeFlowRecords.size() < req.getFinanceFlowIds().size()) {
            throw new MithrasException("欲删除数据与预期不符，操作被禁止");
        }
        //看看是不是都是可以逻辑删除的
        financeFlowRecords.removeIf(a -> !a.getWriteOffStatus().equals(FinancingFlowWriteOffStatusEnum.NO_WRITE_OFF.name()));
        if (financeFlowRecords.size() < req.getFinanceFlowIds().size()) {
            throw new MithrasException("存在部分核销或已核销的数据，操作被禁止");
        }
        financeFlowRecordService.removeByIds(req.getFinanceFlowIds());
    }

    public List<String> projectCashFlowCodeList(CashFlowCodeListREQ req) {
        log.info("projectCashFlowCodeList req:{}", req);
        ContractBaseInfo contractBaseInfo = getBean(ContractBaseInfoMapper.class).selectById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (CharSequenceUtil.equalsAny(contractBaseInfo.getContractStatus(), ContractStatus.CLOSED.name(),
                // 去除结清核销的限制
                ContractStatus.INVALID.name(), ContractStatus.NEW.name())) {
            throw new MithrasException(String.format("合同「%s」暂时不支持做核销", contractBaseInfo.getContractCode()));
        }
        //先判断是收款还是付款
        if (BankFlowPaymentCollectionTypeEnum.COLLECTION.name().equals(req.getWriteOffType())) {
            String cashFlowItem = req.getCashFlowItem();
            boolean isFirstInterest = CharSequenceUtil.equals(req.getCashFlowItem(), CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name());
            //收款比较特殊，需要手动处理一下cash_flow_item
            if (CharSequenceUtil.equalsAny(req.getCashFlowItem(), CollectionWriteOffOrderEnum.PRINCIPAL.name(),
                    CollectionWriteOffOrderEnum.INTEREST.name(), CollectionWriteOffOrderEnum.PENALTY_INTEREST.name(),
                    CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
                req.setCashFlowItem(CashFlowItemEnum.RENT.name());
            }
            //收款的话只需要操作collection_base_info即可
            List<CollectionBaseInfo> collectionBaseInfos = getBean(CollectionBaseInfoMapper.class).selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, req.getCashFlowItem())
                    .eq(isFirstInterest, CollectionBaseInfo::getPhase, 0)
                    .eq(CollectionBaseInfo::getContractId, req.getContractId()));
            if (CollUtil.isEmpty(collectionBaseInfos)) {
                throw new MithrasException(String.format("不存在现金流为「%s」的应收款", Optional.ofNullable(CollectionWriteOffOrderEnum.of(cashFlowItem)).map(CollectionWriteOffOrderEnum::getDisplay).orElse("")));
            }
            return collectionBaseInfos.stream().map(CollectionBaseInfo::getCode).collect(Collectors.toList());
        }
        if (BankFlowPaymentCollectionTypeEnum.PAYMENT.name().equals(req.getWriteOffType())) {
            //付款分为投放款和保证金退款，需要分开处理
            if (PaymentWriteOffOrderEnum.INVESTMENTS_FUNDS.name().equals(req.getCashFlowItem())) {
                List<PaymentBaseInfo> paymentBaseInfos = getBean(PaymentBaseInfoMapper.class).selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .eq(PaymentBaseInfo::getContractId, req.getContractId())
                        .notIn(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name(), PaymentStatusEnum.FINISHED.name()));
                if (CollUtil.isEmpty(paymentBaseInfos)) {
                    throw new MithrasException("不存在未核销的付款申请");
                }
                List<String> res = new ArrayList<>();
                paymentBaseInfos.forEach(paymentBaseInfo -> {
                    //查询实际已经确认的金额
                    List<PaymentActualDetailUnconfirmed> detailUnConfirmeds = getBean(PaymentActualDetailUnconfirmedMapper.class).selectList(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                            .eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentBaseInfo.getId())
                            .in(PaymentActualDetailUnconfirmed::getWriteOffStatus, Arrays.asList(WriteOffStatus.TO_BE_WRITE_OFF.name(),WriteOffStatus.COMMIT.name(), WriteOffStatus.CONFIRM.name())));
                    if (CollUtil.isEmpty(detailUnConfirmeds)) {
                        return;
                    }
                    res.addAll(detailUnConfirmeds.stream().map(o -> paymentBaseInfo.getPaymentCode() + "-" + o.getSeqCode()).collect(Collectors.toList()));
                });
                return res;
            }
            if (PaymentWriteOffOrderEnum.EARNEST_MONEY.name().equals(req.getCashFlowItem())) {
                //保证金退款
                List<MarginBaseInfo> marginBaseInfos = getBean(MarginBaseInfoMapper.class).selectList(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .eq(MarginBaseInfo::getContractId, req.getContractId())
                        .gt(MarginBaseInfo::getCollectionAmount, 0));
                if (CollUtil.isEmpty(marginBaseInfos)) {
                    throw new MithrasException("不存在可以退款的保证金");
                }
                return marginBaseInfos.stream().map(MarginBaseInfo::getMarginCode).collect(Collectors.toList());
            }
            if (PaymentWriteOffOrderEnum.RETENTION_MONEY.name().equals(req.getCashFlowItem())) {
                //质保金退款
                List<WarrantyBaseInfo> warrantyBaseInfos = getBean(WarrantyBaseInfoMapper.class).selectList(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                        .eq(WarrantyBaseInfo::getContractId, req.getContractId())
                        .gt(WarrantyBaseInfo::getCollectionAmount, 0));
                if (CollUtil.isEmpty(warrantyBaseInfos)) {
                    throw new MithrasException("不存在可以退款的质保金");
                }
                return warrantyBaseInfos.stream().map(WarrantyBaseInfo::getWarrantyCode).collect(Collectors.toList());
            }
        }
        throw new MithrasException("参数「核销方式」不存在对应的值");
    }

    @Transactional(rollbackFor = Throwable.class)
    public void nettingRefund(BankFlowNettingRefundREQ req) {
        if (req.getFinanceFlowIds().size() != 2) {
            throw new MithrasException("付款和收款的选择总条数不为2条");
        }
        List<Long> financeFlowIds = req.getFinanceFlowIds();
        List<FinanceFlowRecord> financeFlowRecordList = financeFlowRecordMapper
                .selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                        .in(FinanceFlowRecord::getId, financeFlowIds));
        if (financeFlowRecordList.size() != 2) {
            throw new MithrasException("收款和付款的数据集不为2");
        }
        FinanceFlowRecord collectionFinanceFlowRecord = null;
        FinanceFlowRecord paymentFinanceFlowRecord = null;
        for (FinanceFlowRecord financeFlowRecord : financeFlowRecordList) {
            if (financeFlowRecord.getDebitamount() > 0) {
                paymentFinanceFlowRecord = financeFlowRecord;
            } else if (financeFlowRecord.getCreditamount() > 0) {
                collectionFinanceFlowRecord = financeFlowRecord;
            }
        }
        if (collectionFinanceFlowRecord == null || paymentFinanceFlowRecord == null) {
            throw new MithrasException("请选择一条收款和一条付款流水，进行轧差");
        }
        //轧差退款金额
        Long refundAmount = Long.valueOf(req.getRefundAmount());
        //核销收款金额和
        Long collectionAmount = LongUtil.other2Long(String.valueOf(collectionFinanceFlowRecord.getCreditamount()));
        Long collectionSurplusAmount = ObjectUtil.isNull(collectionFinanceFlowRecord.getSurplusAmount()) ? 0L : collectionFinanceFlowRecord.getSurplusAmount();
        List<FinanceFlowWriteOffDetail> collectionFinanceDetailList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, collectionFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.COLLECTION_RECORD_INFO.name()));
        List<FinanceFlowWriteOffDetail> collectionNettingRefundList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, collectionFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.NETTING_REFUND.name()));
        long totalCollectionFee = 0L;
        long totalCollectionNettingRefund = 0L;
        if (!collectionFinanceDetailList.isEmpty()) {
            List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByIds(collectionFinanceDetailList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalCollectionFee = collectionRecordInfoList.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
        }
        if (!collectionNettingRefundList.isEmpty()) {
            List<NettingRefund> nettingRefundList = nettingRefundService.listByIds(collectionNettingRefundList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalCollectionNettingRefund = nettingRefundList.stream().mapToLong(NettingRefund::getNettingAmount).sum();
        }

        //核销付款金额和
        Long paymentAmount = LongUtil.other2Long(String.valueOf(paymentFinanceFlowRecord.getDebitamount()));
        Long paymentSurplusAmount = ObjectUtil.isNull(paymentFinanceFlowRecord.getSurplusAmount()) ? 0L : paymentFinanceFlowRecord.getSurplusAmount();
        List<FinanceFlowWriteOffDetail> paymentFinanceDetailList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, paymentFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.PAYMENT_ACTUAL_DETAIL.name()));
        List<FinanceFlowWriteOffDetail> paymentNettingRefundList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, paymentFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.NETTING_REFUND.name()));
        long totalPaymentFee = 0L;
        long totalPaymentNettingRefund = 0L;
        if (!paymentFinanceDetailList.isEmpty()) {
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByIds(paymentFinanceDetailList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalPaymentFee = paymentActualDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
        }
        if (!paymentNettingRefundList.isEmpty()) {
            List<NettingRefund> nettingRefundList = nettingRefundService.listByIds(paymentNettingRefundList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalPaymentNettingRefund = nettingRefundList.stream().mapToLong(NettingRefund::getNettingAmount).sum();
        }

        Long collectionRemain = 0L;
        Long paymentRemain = 0L;
        if (ObjectUtil.isNotNull(collectionFinanceFlowRecord.getSurplusAmount()) &&
                (collectionSurplusAmount - refundAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(collectionSurplusAmount)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("收款流水的未核销金额-用户本次输入的退款金额小于0, 允许轧差退款最大金额%s元", maxRefundAmount));
        } else if (ObjectUtil.isNull(collectionFinanceFlowRecord.getSurplusAmount()) && (collectionAmount - totalCollectionFee - totalCollectionNettingRefund - refundAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(collectionAmount - totalCollectionFee - totalCollectionNettingRefund)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("收款流水的未核销金额-用户本次输入的退款金额小于0, 允许轧差退款最大金额%s元", maxRefundAmount));
        }
        if (ObjectUtil.isNotNull(paymentFinanceFlowRecord.getSurplusAmount()) &&
                (paymentSurplusAmount - refundAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(paymentSurplusAmount)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("付款流水的未核销金额-用户本次输入的退款金额小于0, 允许轧差退款最大金额%s元", maxRefundAmount));
        } else if (ObjectUtil.isNull(paymentFinanceFlowRecord.getSurplusAmount()) && (paymentAmount - totalPaymentFee - totalPaymentNettingRefund - refundAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(paymentAmount - totalPaymentFee - totalPaymentNettingRefund)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("付款流水的未核销金额-用户本次输入的退款金额小于0, 允许轧差退款最大金额%s元", maxRefundAmount));
        }

        paymentRemain = paymentAmount - totalPaymentFee - totalPaymentNettingRefund - refundAmount;
        collectionRemain = collectionAmount - totalCollectionFee - totalCollectionNettingRefund - refundAmount;

        paymentFinanceFlowRecord.setSurplusAmount(paymentRemain);
        collectionFinanceFlowRecord.setSurplusAmount(collectionRemain);
        if (paymentRemain == 0) {
            paymentFinanceFlowRecord.setFinancingFlowType(BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE.name());
            paymentFinanceFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name());
        }
        if (collectionRemain == 0) {
            collectionFinanceFlowRecord.setFinancingFlowType(BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE.name());
            collectionFinanceFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name());
        }
        saveFinanceFlowRecord(collectionFinanceFlowRecord);
        saveFinanceFlowRecord(paymentFinanceFlowRecord);
        NettingRefund collectionNettingRefund = saveNettingRefund(collectionFinanceFlowRecord, refundAmount);
        NettingRefund paymentNettingRefund = saveNettingRefund(paymentFinanceFlowRecord, refundAmount);
        saveFinanceFlowWriteOffDetail(collectionNettingRefund);
        saveFinanceFlowWriteOffDetail(paymentNettingRefund);
    }

    private void saveFinanceFlowRecord(FinanceFlowRecord paymentFinanceFlowRecord) {
        LambdaUpdateWrapper<FinanceFlowRecord> paymentUpdateWrapper = new LambdaUpdateWrapper<>();
        paymentUpdateWrapper.eq(FinanceFlowRecord::getId, paymentFinanceFlowRecord.getId());
        paymentUpdateWrapper.set(FinanceFlowRecord::getSurplusAmount, paymentFinanceFlowRecord.getSurplusAmount());
        paymentUpdateWrapper.set(FinanceFlowRecord::getWriteOffStatus, paymentFinanceFlowRecord.getWriteOffStatus());
        paymentUpdateWrapper.set(FinanceFlowRecord::getFinancingFlowType, paymentFinanceFlowRecord.getFinancingFlowType());
        financeFlowRecordService.update(paymentUpdateWrapper);
    }

    private NettingRefund saveNettingRefund(FinanceFlowRecord financeFlowRecord, Long refundAmount) {
        NettingRefund nettingRefund = new NettingRefund();
        nettingRefund.setNettingAmount(refundAmount);
        nettingRefund.setFinanceFlowId(financeFlowRecord.getId());
        nettingRefund.setBankDetailNo(financeFlowRecord.getBillno());
        nettingRefund.setCreateBy(AccountUtil.getLoginInfo().getId());
        nettingRefund.setUpdateBy(AccountUtil.getLoginInfo().getId());
        nettingRefundService.save(nettingRefund);
        return nettingRefund;
    }

    private void saveFinanceFlowWriteOffDetail(NettingRefund nettingRefund) {
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setBankDetailNo(nettingRefund.getBankDetailNo());
        financeFlowWriteOffDetail.setFinanceFlowId(nettingRefund.getFinanceFlowId());
        financeFlowWriteOffDetail.setMainId(nettingRefund.getId());
        financeFlowWriteOffDetail.setRecordMainTable(FinanceFlowDetailTableEnum.NETTING_REFUND.name());
        financeFlowWriteOffDetail.setCreateBy(AccountUtil.getLoginInfo().getId());
        financeFlowWriteOffDetail.setUpdateBy(AccountUtil.getLoginInfo().getId());
        financeFlowWriteOffDetailMapper.insert(financeFlowWriteOffDetail);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void confirmIncome(BankFlowConfirmIncomeREQ req) {
        List<FinanceFlowRecord> financeFlowRecordList = financeFlowRecordMapper
                .selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                        .in(FinanceFlowRecord::getId, req.getFinanceFlowId()));

        FinanceFlowRecord collectionFinanceFlowRecord = financeFlowRecordList.get(0);
        if (collectionFinanceFlowRecord == null || collectionFinanceFlowRecord.getCreditamount() <= 0) {
            throw new MithrasException("收款金额不大于0");
        }
        //核销金额
        Long writeOffAmount = Long.valueOf(req.getWriteOffAmount());
        if (writeOffAmount <= 0) {
            throw new MithrasException("本次核销金额不大于0");
        }
        //核销收款金额和
        Long collectionAmount = LongUtil.other2Long(String.valueOf(collectionFinanceFlowRecord.getCreditamount()));
        //剩余可用金额
        Long collectionSurplusAmount = ObjectUtil.isNull(collectionFinanceFlowRecord.getSurplusAmount()) ? 0L : collectionFinanceFlowRecord.getSurplusAmount();
        List<FinanceFlowWriteOffDetail> collectionFinanceDetailList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, collectionFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.COLLECTION_RECORD_INFO.name()));
        List<FinanceFlowWriteOffDetail> collectionNettingRefundList = financeFlowWriteOffDetailMapper.selectList(Wrappers.<FinanceFlowWriteOffDetail>lambdaQuery()
                .eq(FinanceFlowWriteOffDetail::getFinanceFlowId, collectionFinanceFlowRecord.getId())
                .eq(FinanceFlowWriteOffDetail::getRecordMainTable, FinanceFlowDetailTableEnum.NETTING_REFUND.name()));
        long totalCollectionFee = 0L;
        long totalCollectionNettingRefund = 0L;
        if (!collectionFinanceDetailList.isEmpty()) {
            List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByIds(collectionFinanceDetailList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalCollectionFee = collectionRecordInfoList.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
        }
        if (!collectionNettingRefundList.isEmpty()) {
            List<NettingRefund> nettingRefundList = nettingRefundService.listByIds(collectionNettingRefundList.stream().map(FinanceFlowWriteOffDetail::getMainId).collect(Collectors.toSet()));
            totalCollectionNettingRefund = nettingRefundList.stream().mapToLong(NettingRefund::getNettingAmount).sum();
        }

        if (ObjectUtil.isNotNull(collectionFinanceFlowRecord.getSurplusAmount()) && (collectionSurplusAmount - writeOffAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(collectionSurplusAmount)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("收款流水的未核销金额-用户本次输入的核销金额小于0, 允许最大核销金额%s元", maxRefundAmount));
        } else if (ObjectUtil.isNull(collectionFinanceFlowRecord.getSurplusAmount()) && (collectionAmount - totalCollectionFee - totalCollectionNettingRefund - writeOffAmount < 0)) {
            BigDecimal maxRefundAmount = LongUtil.tenThousand2Dollar(String.valueOf(collectionAmount - totalCollectionFee - totalCollectionNettingRefund)).setScale(2, RoundingMode.HALF_UP);
            throw new MithrasException(String.format("收款流水的未核销金额-用户本次输入的核销金额小于0, 允许最大核销金额%s元", maxRefundAmount));
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            throw new MithrasException("所选合同为空");
        }
        ContractReceipt contractReceipt = contractReceiptMapper.selectById(req.getReceiptId());
        if (ObjectUtil.isNull(contractReceipt)) {
            throw new MithrasException("合同明细借据为空");
        }
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getReceiptIdFinal, contractReceipt.getId()));
        if (ObjectUtil.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请为空");
        }

        //更新剩余核销金额
        Long collectionRemain = collectionAmount - totalCollectionFee - totalCollectionNettingRefund - writeOffAmount;
        collectionFinanceFlowRecord.setSurplusAmount(collectionRemain);
        if (collectionRemain == 0) {
            collectionFinanceFlowRecord.setWriteOffStatus(FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name());
        }
        saveFinanceFlowRecord(collectionFinanceFlowRecord);

        //应收
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, req.getContractId())
                .eq(ObjectUtil.isNotNull(contractBaseInfo.getContractCode()), CollectionBaseInfo::getContractCode, contractBaseInfo.getContractCode())
                .eq(CollectionBaseInfo::getReceiptId, contractReceipt.getId())
                .eq(CollectionBaseInfo::getReceiptCode, contractReceipt.getReceiptCode())
                .eq(CollectionBaseInfo::getClientId, req.getClientId())
                .eq(CollectionBaseInfo::getCashFlowItem, req.getCashFlowItem())
                .orderByDesc(CollectionBaseInfo::getCreateTime));
        String code = null;
        if (!collectionBaseInfoList.isEmpty()) {
            String previousCode = collectionBaseInfoList.get(0).getCode();
            if (ObjectUtil.isNotNull(previousCode)) {
                code = updateCodeVersion(previousCode);
            }
        } else {
            String newCode = collectionBaseInfoService.getCode(req.getCashFlowItem(), contractReceipt.getReceiptCode(), 0, contractBaseInfo.getContractCode());
            code = generateNewCode(newCode);
        }

        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setContractId(req.getContractId());
        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
        collectionBaseInfo.setClientId(req.getClientId());
        collectionBaseInfo.setCode(code);
        collectionBaseInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        collectionBaseInfo.setCollectionDate(LocalDate.parse(collectionFinanceFlowRecord.getBizdate(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        collectionBaseInfo.setCollectionAmount(writeOffAmount);
        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());
        collectionBaseInfo.setPhase(0);
        collectionBaseInfo.setPlanCollectionAmount(writeOffAmount);
        collectionBaseInfo.setPlanCollectionDate(LocalDate.parse(collectionFinanceFlowRecord.getBizdate(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        if (CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name().equalsIgnoreCase(req.getCashFlowItem())) {
            collectionBaseInfo.setCashFlowItem(CashFlowItemEnum.RENT.name());
        } else {
            collectionBaseInfo.setCashFlowItem(req.getCashFlowItem());
        }
        collectionBaseInfo.setReceiptId(contractReceipt.getId());
        collectionBaseInfo.setReceiptCode(contractReceipt.getReceiptCode());
        collectionBaseInfoService.save(collectionBaseInfo);

        //实收
        CollectionRecordInfo collectionRecordInfo = new CollectionRecordInfo();
        collectionRecordInfo.setCollectionId(collectionBaseInfo.getId());
        collectionRecordInfo.setCollectionType(RecordTypeEnum.COLLECTION.name());
        collectionRecordInfo.setCollectionDate(LocalDate.parse(collectionFinanceFlowRecord.getBizdate(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        collectionRecordInfo.setCollectionAmount(writeOffAmount);
        collectionRecordInfo.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
        collectionRecordInfo.setWriteOffType(WriteOffTypeEnum.MANUAL_RECORD.name());
        collectionRecordInfo.setBankDetailNo(collectionFinanceFlowRecord.getBillno());
        collectionRecordInfo.setCreateBy(AccountUtil.getLoginInfo().getId());
        collectionRecordInfoService.save(collectionRecordInfo);

        //资金流水核销详情关联
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setBankDetailNo(collectionFinanceFlowRecord.getBillno());
        financeFlowWriteOffDetail.setFinanceFlowId(collectionFinanceFlowRecord.getId());
        financeFlowWriteOffDetail.setMainId(collectionRecordInfo.getId());
        financeFlowWriteOffDetail.setRecordMainTable(FinanceFlowDetailTableEnum.COLLECTION_RECORD_INFO.name());
        financeFlowWriteOffDetail.setCreateBy(AccountUtil.getLoginInfo().getId());
        financeFlowWriteOffDetail.setUpdateBy(AccountUtil.getLoginInfo().getId());
        financeFlowWriteOffDetailMapper.insert(financeFlowWriteOffDetail);

        //折现率的重算
        LambdaUpdateWrapper<ContractReceipt> contractReceiptWrapper = new LambdaUpdateWrapper<>();
        contractReceiptWrapper.eq(ContractReceipt::getId, contractReceipt.getId());
        contractReceiptWrapper.set(ContractReceipt::getIncomeSharingFlag, 0);
        contractReceiptWrapper.set(ContractReceipt::getChangeDate, LocalDate.now());
        contractReceiptService.update(contractReceiptWrapper);
        contractIncomeSharingService.calculateIncomeSharing(contractBaseInfo.getId());


        //推送应收单和
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 发送指标速算事件
                MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
                metricComputeEventBus.post(metricComputeEvent);
                //给苍穹推送应收单，收款单
                if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())) {
                    List<CollectionRecordInfo> allCollectionRecordInfos = new ArrayList<>();
                    allCollectionRecordInfos.add(collectionRecordInfo);
                    if (ObjectUtil.isNotEmpty(allCollectionRecordInfos)) {
                        //换聚合维度为收款
                        StringBuilder sb = new StringBuilder();
                        CollectionRecordInfo collectionRecordInfo;
                        Set<String> flowSet = new HashSet<>();
                        for (int i = 0; i < allCollectionRecordInfos.size(); i++) {
                            collectionRecordInfo = allCollectionRecordInfos.get(i);
                            if (ObjectUtil.isEmpty(collectionRecordInfo.getBankDetailNo()) || flowSet.contains(collectionRecordInfo.getBankDetailNo())) {
                                continue;
                            }
                            flowSet.add(collectionRecordInfo.getBankDetailNo());
                            sb.append(collectionRecordInfo.getBankDetailNo());
                            if (i < allCollectionRecordInfos.size() - 1) {
                                sb.append(",");
                            }
                        }
                        SyncCqReqBizInfo bizInfo = SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(collectionBaseInfo.getContractId());
                        //应收单中的 客户编码，需关联【合同管理】模块中的 租金往来方，取资金往来方的客户编码。
                        ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                                .eq(ContractTenantry::getContractId, collectionBaseInfo.getContractId())
                                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                                .last(StringUtil.mysqlLimitOne()));
                        if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
                            Client client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
                            if (ObjectUtil.isNotEmpty(client)) {
                                bizInfo.setCustomer(client.getClientCode());
                                bizInfo.setCustomerName(client.getClientName());
                            }
                        }
                        CQ2PlanCollectionVO cq2PlanCollectionVO = collectionRecordInfoService.buildPlanCollectionReq(collectionBaseInfo, allCollectionRecordInfos, bizInfo, sb.toString());
                        //罚息单独拆分
                        CQ2PlanCollectionVO PenaltyInterestPlanCollectionVO = collectionRecordInfoService.buildPenaltyInterestPlanCollectionReq(cq2PlanCollectionVO, collectionBaseInfo, allCollectionRecordInfos, bizInfo);
                        List<CQ2PlanCollectionVO> collectionVOS = new ArrayList<>();
                        collectionVOS.add(cq2PlanCollectionVO);
                        if (ObjectUtil.isNotEmpty(PenaltyInterestPlanCollectionVO)) {
                            collectionVOS.add(PenaltyInterestPlanCollectionVO);
                        }
                        //20251218名义价款需要把 保证金额抵扣部分拆分出来
                        CQ2PlanCollectionVO nominalPriceCollectionVo = collectionRecordInfoService.buildNomnalPricePlanCollectionReq(cq2PlanCollectionVO, collectionBaseInfo, allCollectionRecordInfos, bizInfo);
                        if (ObjectUtil.isNotEmpty(nominalPriceCollectionVo)) {
                            collectionVOS.add(nominalPriceCollectionVo);
                        }
                        financialManagerServiceImpl2.planCollectionExec(collectionVOS);
                    }
                }
            }
        });

        //判断推送收款单
        if (YesOrNoNumberEnum.NO.getCode().equals(collectionFinanceFlowRecord.getSendCqFlag())) {
            getBean(FinanceFlowAutoWriteOffService.class).writeOffNotice(Collections.singletonList(collectionFinanceFlowRecord.getId()));
        }
    }

    public List<BankFlowContractReceiptListRSP> getReceiptCode(BankFlowContractReceiptREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            throw new MithrasException("所选合同为空");
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper
                .selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .eq(PaymentBaseInfo::getContractId, req.getContractId())
                        .isNotNull(PaymentBaseInfo::getReceiptIdFinal));
        if (paymentBaseInfoList.isEmpty()) {
            throw new MithrasException("付款申请明细基础为空");
        }
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet()));
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e, (k1, k2) -> k1));
        Map<Long, List<PaymentActualDetail>> paymentActualDetailMap = paymentActualDetailService.getMapByPaymentIds(paymentBaseInfoMap.keySet());
        List<BankFlowContractReceiptListRSP> res = new ArrayList<>();
        for (Map.Entry<Long, PaymentBaseInfo> entry : paymentBaseInfoMap.entrySet()) {
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMap.get(entry.getKey());
            PaymentBaseInfo paymentBaseInfo = entry.getValue();
            if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                continue;
            }
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            long amount = paymentActualDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            for (ContractReceipt contractReceipt : contractReceiptList) {
                if (paymentBaseInfo != null && paymentBaseInfo.getReceiptIdFinal().equals(contractReceipt.getId())) {
                    BankFlowContractReceiptListRSP bankFlowContractReceiptListRSP = new BankFlowContractReceiptListRSP();
                    bankFlowContractReceiptListRSP.setReceiptCode(contractReceipt.getReceiptCode());
                    bankFlowContractReceiptListRSP.setReceiptId(contractReceipt.getId());
                    bankFlowContractReceiptListRSP.setContractId(contractReceipt.getContractId());
                    bankFlowContractReceiptListRSP.setPaidInDate(paymentBaseInfo.getPaidInDate());
                    bankFlowContractReceiptListRSP.setPaidInAmount(amount);
                    res.add(bankFlowContractReceiptListRSP);
                }
            }
        }
        return res;
    }

    // 2022B0007-06-01-bzj
    private String updateCodeVersion(String prevCode) {
        if (ObjectUtil.isNull(prevCode)) {
            return null;
        }
        String[] temp = prevCode.split("-");
        StringBuilder sBuilder = new StringBuilder();
        int length = temp.length;
        for (int i = 0; i < length; i++) {
            if (i == length - 2) {
                String cur = temp[i];
                if (Integer.valueOf(cur) + 1 < 10) {
                    sBuilder.append("0").append(Integer.valueOf(cur) + 1);
                } else {
                    sBuilder.append(Integer.valueOf(cur) + 1);
                }
                sBuilder.append("-");
            } else {
                sBuilder.append(temp[i]);
                if (i != length - 1) {
                    sBuilder.append("-");
                }
            }
        }
        return sBuilder.toString();
    }

    // 2022B0007-06-bzj
    private String generateNewCode(String prevCode) {
        if (ObjectUtil.isNull(prevCode)) {
            return null;
        }
        String[] temp = prevCode.split("-");
        StringBuilder sBuilder = new StringBuilder();
        int length = temp.length;
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                sBuilder.append("01").append("-");
                sBuilder.append(temp[i]);
                if (i != length - 1) {
                    sBuilder.append("-");
                }
            } else {
                sBuilder.append(temp[i]);
                if (i != length - 1) {
                    sBuilder.append("-");
                }
            }
        }
        return sBuilder.toString();
    }


}
