package cn.zswltech.mithras.service.service.liquidityrisk.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.excel.exporter.LiquidityRiskInflowExcelExporter;
import cn.zswltech.mithras.service.excel.exporter.LiquidityRiskShortTermLoanExcelExporter;
import cn.zswltech.mithras.service.excel.model.LiquidityRiskInflowExcelModel;
import cn.zswltech.mithras.service.excel.model.LiquidityRiskShortTermLoanExcelModel;
import cn.zswltech.mithras.service.mapper.dto.FundFinancingRepayActualDTO;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.liquidity.infrastructure.persistence.mapper.risk.BaseAmountSettingMapper;
import cn.zswltech.mithras.liquidity.infrastructure.persistence.mapper.risk.FinancingDeliverDetailSettingMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.liquidity.infrastructure.persistence.model.risk.BaseAmountSetting;
import cn.zswltech.mithras.liquidity.infrastructure.persistence.model.risk.FinancingDeliverDetailSetting;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.liquidityrisk.CapitalInflowService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName LiquidityRiskServiceImpl
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/16 10:29 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class CapitalInflowServiceImpl implements CapitalInflowService {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private FundFinancingRepayActualLibMapper fundFinancingRepayActualLibMapper;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private BaseAmountSettingMapper baseAmountSettingMapper;
    @Resource
    private FinancingDeliverDetailSettingMapper financingDeliverDetailSettingMapper;
    @Resource
    private LiquidityRiskInflowExcelExporter liquidityRiskInflowExcelExporter;
    @Resource
    private LiquidityRiskShortTermLoanExcelExporter liquidityRiskShortTermLoanExcelExporter;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService organizationService;

    private static final String LINE = "line";

    private static final NamedThreadLocal<AssetInflowDetailRSP> assetInflowDetailRSPThreadLocal = new NamedThreadLocal<>("assetInflowDetailRSPThreadLocal");


    @Override
    public AssetInflowDetailRSP inFlowDetail(AssetInflowDetailREQ req) {
        LocalDateTime startTime = req.getTimeFrom();
        LocalDateTime endTime = req.getTimeTo();
        Page<CollectionBaseInfo> collectionBaseInfoPage;
        List<CollectionBaseInfo> collectionBaseInfos;
        Set<Long> addContractIds = new HashSet<>();
        AssetInflowDetailRSP assetInflowDetailRSP = new AssetInflowDetailRSP();
        AssetInflowDetailRSP.AssetInflowDetailBody pageSumBody = assetInflowDetailRSP.new AssetInflowDetailBody();
        AssetInflowDetailRSP.AssetInflowDetailBody sumBody = assetInflowDetailRSP.new AssetInflowDetailBody();
        assetInflowDetailRSP.setPageSum(pageSumBody);
        assetInflowDetailRSP.setSum(sumBody);
        if (!req.getNeedPage()) {
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        collectionBaseInfoPage = collectionBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .between(CollectionBaseInfo::getPlanCollectionDate, startTime, endTime)
                        .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                        .orderByAsc(CollectionBaseInfo::getContractId));
        collectionBaseInfos = collectionBaseInfoPage.getRecords();
        //计算总计
        List<CollectionBaseInfo> sumAmountList = collectionBaseInfoService.sumAmount(startTime, endTime);
        sumAmountList.forEach(base -> {
            sumBody.setTotalAmount(LongUtil.null2zero(sumBody.getTotalAmount()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
            switch (CashFlowItemEnum.of(base.getCashFlowItem())) {
                case RENT:
                    sumBody.setPrincipal(LongUtil.null2zero(sumBody.getPrincipal()) + LongUtil.null2zero(base.getPrincipal()));
                    sumBody.setInterest(LongUtil.null2zero(sumBody.getInterest()) + LongUtil.null2zero(base.getInterest()));
                    break;
                case FIRST_RENT:
                    sumBody.setDownPayment(LongUtil.null2zero(sumBody.getDownPayment()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                    break;
                case RETENTION_MONEY:
                case EARNEST_MONEY:
                    sumBody.setEarnestMoney(LongUtil.null2zero(sumBody.getEarnestMoney()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                    break;
                default:
                    sumBody.setConsultingFee(LongUtil.null2zero(sumBody.getConsultingFee()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
            }
        });
        //计算合同金额
        sumBody.setApplyCreditAmount(collectionBaseInfoService.sumContractAmount(startTime, endTime).stream().filter(base -> ObjectUtil.isNotEmpty(base.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum());
        //计算预估
        if (ObjectUtil.isNotEmpty(req.getEstimatedOverdueRate())) {
            BigDecimal subtract = new BigDecimal(1).subtract(NumberUtil.div(String.valueOf(req.getEstimatedOverdueRate()), "100"));
            sumBody.setEstimatedCashInFlowTotal(subtract.multiply(new BigDecimal(String.valueOf(sumBody.getTotalAmount()))).setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
        } else {
            sumBody.setEstimatedCashInFlowTotal(sumBody.getTotalAmount());
        }
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return assetInflowDetailRSP;
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()))
                .orderByDesc(ContractBaseInfo::getId));
        Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        Map<Long, ProjReviewBaseInfo> projReviewIdBean = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .in(ProjReviewBaseInfo::getId, contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet()))).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));
        List<AssetInflowDetailRSP.AssetInflowDetailBody> bodies = new ArrayList<>();
        //质保金需俺付款加入保证金
        Map<Long, Long> retentionMap = collectionBaseInfos.stream().filter(base -> ObjectUtil.equals(base.getCashFlowItem(),
                CashFlowItemEnum.RETENTION_MONEY.name())).collect(Collectors.toMap(CollectionBaseInfo::getPaymentId, CollectionBaseInfo::getPlanCollectionAmount));
        collectionBaseInfos.forEach(base -> {
            //排除质保金
            if (!CashFlowItemEnum.RETENTION_MONEY.name().equals(base.getCashFlowItem())) {
                AssetInflowDetailRSP.AssetInflowDetailBody assetInflowDetailBody = assetInflowDetailRSP.new AssetInflowDetailBody();
                ContractBaseInfo contractBaseInfo = contractId2Bean.getOrDefault(base.getContractId(), new ContractBaseInfo());
                assetInflowDetailBody.setProjName(contractBaseInfo.getProjName());
                ProjReviewBaseInfo projReviewBaseInfo = projReviewIdBean.getOrDefault(contractBaseInfo.getProjReviewId(), new ProjReviewBaseInfo());
                assetInflowDetailBody.setEstablishId(projReviewBaseInfo.getProjEstablishId());
                assetInflowDetailBody.setReviewId(projReviewBaseInfo.getId());
                if (ObjectUtil.isNotEmpty(projReviewBaseInfo.getProjEstablishId())) {
                    assetInflowDetailBody.setProjId(projReviewBaseInfo.getProjEstablishId());
                    //assetInflowDetailBody.setBizType(projName2Id.get(assetInflowDetailBody.getProjName()).getBizType());
                    assetInflowDetailBody.setDataType(BusinessModuleEnum.PROJ_ESTABLISH.name());
                } else {
                    assetInflowDetailBody.setProjId(projReviewBaseInfo.getId());
                    //assetInflowDetailBody.setBizType(projName2Id.get(assetInflowDetailBody.getProjName()).getBizType());
                    assetInflowDetailBody.setDataType(BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
                }
                assetInflowDetailBody.setContractCode(contractBaseInfo.getContractCode());
                assetInflowDetailBody.setContractId(contractBaseInfo.getId());
                assetInflowDetailBody.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
                assetInflowDetailBody.setFlowInDate(base.getPlanCollectionDate());
                CashFlowItemEnum of = CashFlowItemEnum.of(base.getCashFlowItem());
                if (ObjectUtil.isNotEmpty(of)) {
                    switch (of) {
                        case RENT:
                            assetInflowDetailBody.setPrincipal(base.getPrincipal());
                            assetInflowDetailBody.setInterest(base.getInterest());
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(base.getPrincipal()));
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(base.getInterest()));
                            pageSumBody.setPrincipal(LongUtil.null2zero(pageSumBody.getPrincipal()) + LongUtil.null2zero(base.getPrincipal()));
                            pageSumBody.setInterest(LongUtil.null2zero(pageSumBody.getInterest()) + LongUtil.null2zero(base.getInterest()));
                            break;
                        case FIRST_RENT:
                            assetInflowDetailBody.setDownPayment(base.getPlanCollectionAmount());
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                            pageSumBody.setDownPayment(LongUtil.null2zero(pageSumBody.getDownPayment()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                            break;
                        case RETENTION_MONEY:
                            break;
                        case EARNEST_MONEY:
                            assetInflowDetailBody.setEarnestMoney(LongUtil.null2zero(assetInflowDetailBody.getEarnestMoney()) + LongUtil.null2zero(base.getPlanCollectionAmount()) + LongUtil.null2zero(retentionMap.get(base.getPaymentId())));
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(base.getPlanCollectionAmount()) + LongUtil.null2zero(retentionMap.get(base.getPaymentId())));
                            pageSumBody.setEarnestMoney(LongUtil.null2zero(pageSumBody.getEarnestMoney()) + LongUtil.null2zero(base.getPlanCollectionAmount()) + LongUtil.null2zero(retentionMap.get(base.getPaymentId())));
                            break;
                        case OTHERAMOUNT:
                            //手续费
                            CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.lambdaQuery()
                                    .eq(CollectionBaseInfo::getContractId, base.getContractId())
                                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.COMMISSION.name())
                                    .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED).one();
                            Long planCollectionAmount = Objects.isNull(collectionBaseInfo)?base.getPlanCollectionAmount()
                                    : LongUtil.null2zero(base.getPlanCollectionAmount())+LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount());
                            assetInflowDetailBody.setConsultingFee(planCollectionAmount);
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(planCollectionAmount));
                            pageSumBody.setConsultingFee(LongUtil.null2zero(pageSumBody.getConsultingFee()) + LongUtil.null2zero(planCollectionAmount));
                        default:
                            assetInflowDetailBody.setConsultingFee(base.getPlanCollectionAmount());
                            assetInflowDetailBody.setTotalAmount(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                            pageSumBody.setConsultingFee(LongUtil.null2zero(pageSumBody.getConsultingFee()) + LongUtil.null2zero(base.getPlanCollectionAmount()));
                    }
                    //计算预估
                    if (ObjectUtil.isNotEmpty(req.getEstimatedOverdueRate())) {
                        BigDecimal subtract = new BigDecimal(1).subtract(NumberUtil.div(String.valueOf(req.getEstimatedOverdueRate()), "100"));
                        assetInflowDetailBody.setEstimatedCashInFlowTotal(subtract.multiply(new BigDecimal(String.valueOf(LongUtil.null2zero(assetInflowDetailBody.getTotalAmount())))).setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
                    } else {
                        assetInflowDetailBody.setEstimatedCashInFlowTotal(assetInflowDetailBody.getTotalAmount());
                    }
                    pageSumBody.setTotalAmount(LongUtil.null2zero(pageSumBody.getTotalAmount()) + LongUtil.null2zero(assetInflowDetailBody.getTotalAmount()));
                    pageSumBody.setEstimatedCashInFlowTotal(LongUtil.null2zero(pageSumBody.getEstimatedCashInFlowTotal()) + LongUtil.null2zero(assetInflowDetailBody.getEstimatedCashInFlowTotal()));
                    if (!addContractIds.contains(contractBaseInfo.getId())) {
                        pageSumBody.setApplyCreditAmount(LongUtil.null2zero(pageSumBody.getApplyCreditAmount()) + LongUtil.null2zero(assetInflowDetailBody.getApplyCreditAmount()));
                        addContractIds.add(contractBaseInfo.getId());
                    }
                    bodies.add(assetInflowDetailBody);
                }
            }
        });
        if (ObjectUtil.isEmpty(collectionBaseInfoPage)) {
            assetInflowDetailRSP.setDetailBodies(PageR.of(bodies, bodies.size()));
        } else {
            assetInflowDetailRSP.setDetailBodies(PageR.of(bodies, collectionBaseInfoPage.getTotal(), collectionBaseInfoPage.getCurrent(),
                    collectionBaseInfoPage.getSize()));
        }
        return assetInflowDetailRSP;
    }

    @Override
    public void inFlowDetailDownload(AssetInflowDetailREQ req, OutputStream outputStream) {
        AssetInflowDetailRSP assetInflowDetailRSP = this.inFlowDetail(req);
        if (ObjectUtil.isEmpty(assetInflowDetailRSP) || ObjectUtil.isEmpty(assetInflowDetailRSP.getDetailBodies())) {
            return;
        }
        List<LiquidityRiskInflowExcelModel> liquidityRiskInflowExcelModels = assetInflowDetailRSP.getDetailBodies().getList().stream().map(base -> {
            LiquidityRiskInflowExcelModel model = BeanUtil.copyProperties(base,
                    LiquidityRiskInflowExcelModel.class);
            model.setApplyCreditAmount(Long2BigDecimal(base.getApplyCreditAmount()));
            model.setPrincipal(Long2BigDecimal(base.getPrincipal()));
            model.setInterest(Long2BigDecimal(base.getInterest()));
            model.setDownPayment(Long2BigDecimal(base.getDownPayment()));
            model.setEarnestMoney(Long2BigDecimal(base.getEarnestMoney()));
            model.setConsultingFee(Long2BigDecimal(base.getConsultingFee()));
            model.setTotalAmount(Long2BigDecimal(base.getTotalAmount()));
            model.setEstimatedCashInFlowTotal(Long2BigDecimal(base.getEstimatedCashInFlowTotal()));
            return model;
        }).collect(Collectors.toList());
        //添加最后一行
        LiquidityRiskInflowExcelModel liquidityRiskInflowExcelModel = new LiquidityRiskInflowExcelModel();
        liquidityRiskInflowExcelModel.setProjName("总计");
        liquidityRiskInflowExcelModel.setPrincipal(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getPrincipal()));
        liquidityRiskInflowExcelModel.setApplyCreditAmount(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getApplyCreditAmount()));
        liquidityRiskInflowExcelModel.setInterest(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getInterest()));
        liquidityRiskInflowExcelModel.setDownPayment(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getDownPayment()));
        liquidityRiskInflowExcelModel.setEarnestMoney(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getEarnestMoney()));
        liquidityRiskInflowExcelModel.setConsultingFee(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getConsultingFee()));
        liquidityRiskInflowExcelModel.setTotalAmount(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getTotalAmount()));
        liquidityRiskInflowExcelModel.setEstimatedCashInFlowTotal(Long2BigDecimal(assetInflowDetailRSP.getPageSum().getEstimatedCashInFlowTotal()));
        liquidityRiskInflowExcelModels.add(liquidityRiskInflowExcelModel);
        liquidityRiskInflowExcelExporter.exportExcel(liquidityRiskInflowExcelModels, outputStream);
    }

    private String Long2BigDecimal(Long num) {
        BigDecimal bigDecimal = LongUtil.tenThousand2Dollar(LongUtil.tenThousand2Dollar(LongUtil.null2zero(num).toString()).toString());
        if (bigDecimal.compareTo(new BigDecimal("0.01")) < 0 && bigDecimal.compareTo(BigDecimal.ZERO) >= 1) {
            return "<0.01";
        } else {
            return bigDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
    }

    @Override
    public ShortTermLoanDetailRSP shortTermLoanDetail(ShortTermLoanDetailREQ req) {
        FundFinancingRepayActualDTO fundFinancingRepayActualDTO = new FundFinancingRepayActualDTO();
        fundFinancingRepayActualDTO.setFromTime(req.getFromTime().atStartOfDay());
        fundFinancingRepayActualDTO.setTimeLimitType(FundFinancingTimeLimitTypeEnum.SHORT_TERM_LOAN.name());
        fundFinancingRepayActualDTO.setToTime(req.getToTime().atTime(23, 59, 59));
        Page<FundFinancingRepayActualLib> fundFinancingRepayActualLibPage;
        List<FundFinancingRepayActualLib> financingRepayActualLibs;
        Set<Long> addFinancingIds = new HashSet<>();
        Set<Long> addAllFinancingIds = new HashSet<>();
        if (req.getNeedPage()) {
            fundFinancingRepayActualLibPage = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(req.getPage(), req.getPageSize()), fundFinancingRepayActualDTO);
        } else {
            fundFinancingRepayActualLibPage = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(1, Integer.MAX_VALUE), fundFinancingRepayActualDTO);
        }
        financingRepayActualLibs = fundFinancingRepayActualLibPage.getRecords();
        if (ObjectUtil.isEmpty(financingRepayActualLibs)) {
            return null;
        }
        Page<FundFinancingRepayActualLib> pageAll = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(1, Integer.MAX_VALUE),
                fundFinancingRepayActualDTO);
        Set<Long> financingIds = pageAll.getRecords().stream().map(FundFinancingRepayActualLib::getFinancingId).collect(Collectors.toSet());

        //查询融资基本信息
        Map<Long, FundFinancingBaseInfo> fundFinancingBaseInfoId2Bean = fundFinancingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getId, financingIds)).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e));
        //查询核销完毕期限
        Set<String> writtenOffCodes = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .in(FundReceiptRepayCashFlow::getFinancingId, financingIds)
                .eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name())).stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toSet());
        ShortTermLoanDetailRSP shortTermLoanDetailRSP = new ShortTermLoanDetailRSP();
        ShortTermLoanDetailRSP.ShortTermLoanDetailBody sumBody = shortTermLoanDetailRSP.new ShortTermLoanDetailBody();
        ShortTermLoanDetailRSP.ShortTermLoanDetailBody pageSumBody = shortTermLoanDetailRSP.new ShortTermLoanDetailBody();
        shortTermLoanDetailRSP.setSum(sumBody);
        shortTermLoanDetailRSP.setPageSum(pageSumBody);
        //计算总额

        pageAll.getRecords().forEach(base -> {
            FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoId2Bean.getOrDefault(base.getFinancingId(), new FundFinancingBaseInfo());
            sumBody.setPrincipleAmount(LongUtil.null2zero(sumBody.getPrincipleAmount()) + LongUtil.null2zero(base.getPrincipleAmount()));
            sumBody.setInterestAmount(LongUtil.null2zero(sumBody.getInterestAmount()) + LongUtil.null2zero(base.getInterestAmount()));
            sumBody.setTotalAmount(LongUtil.null2zero(sumBody.getTotalAmount()) + LongUtil.null2zero(base.getPrincipleAmount()) + LongUtil.null2zero(base.getInterestAmount()));
            if (!addAllFinancingIds.contains(base.getFinancingId())) {
                sumBody.setFinancingAmount(LongUtil.null2zero(sumBody.getFinancingAmount()) + LongUtil.null2zero(baseInfo.getFinancingAmount()));
                addAllFinancingIds.add(base.getFinancingId());
            }
        });

        List<ShortTermLoanDetailRSP.ShortTermLoanDetailBody> bodies = new ArrayList<>();
        Map<Long, List<FundFinancingCreditRef>> financingCreditRefMap = financingCreditRefService.queryBatchByFinancingId(financingRepayActualLibs.stream().map(FundFinancingRepayActualLib::getFinancingId).collect(Collectors.toList()));
        Map<Long, String> orgMap = organizationService.getNamesByIds(financingCreditRefMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));
        financingRepayActualLibs.forEach(actualLib -> {
            FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoId2Bean.getOrDefault(actualLib.getFinancingId(), new FundFinancingBaseInfo());
            List<FundFinancingCreditRef> creditRefList = financingCreditRefMap.getOrDefault(actualLib.getFinancingId(), Collections.emptyList());
            List<Long> organizationIdList = creditRefList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList());
            ShortTermLoanDetailRSP.ShortTermLoanDetailBody shortTermLoanDetailBody = shortTermLoanDetailRSP.new ShortTermLoanDetailBody();
            shortTermLoanDetailBody.setOrganizationId(organizationIdList);
            if (writtenOffCodes.contains(actualLib.getCashFlowCode())) {
                shortTermLoanDetailBody.setIsRepayment(YesOrNoNumberEnum.YES.getCode());
            } else {
                shortTermLoanDetailBody.setIsRepayment(YesOrNoNumberEnum.NO.getCode());
            }
            shortTermLoanDetailBody.setOrganizationName(organizationIdList.stream().map(orgMap::get).collect(Collectors.toList()));
            shortTermLoanDetailBody.setFinancingId(actualLib.getFinancingId());
            shortTermLoanDetailBody.setFinancingCode(baseInfo.getFinancingCode());
            shortTermLoanDetailBody.setFinancingAmount(baseInfo.getFinancingAmount());
            shortTermLoanDetailBody.setRepaymentDate(actualLib.getRepayDate());
            if (actualLib.getRepayDate().isAfter(LocalDate.now())) {
                shortTermLoanDetailBody.setRemainingDays(LocalDate.now().until(actualLib.getRepayDate(), ChronoUnit.DAYS));
            }
            shortTermLoanDetailBody.setPrincipleAmount(actualLib.getPrincipleAmount());
            shortTermLoanDetailBody.setInterestAmount(actualLib.getInterestAmount());
            shortTermLoanDetailBody.setTotalAmount(LongUtil.null2zero(shortTermLoanDetailBody.getPrincipleAmount()) + LongUtil.null2zero(shortTermLoanDetailBody.getInterestAmount()));
            pageSumBody.setPrincipleAmount(LongUtil.null2zero(pageSumBody.getPrincipleAmount()) + LongUtil.null2zero(actualLib.getPrincipleAmount()));
            pageSumBody.setInterestAmount(LongUtil.null2zero(pageSumBody.getInterestAmount()) + LongUtil.null2zero(actualLib.getInterestAmount()));
            pageSumBody.setTotalAmount(LongUtil.null2zero(pageSumBody.getTotalAmount()) + shortTermLoanDetailBody.getTotalAmount());
            if (!addFinancingIds.contains(actualLib.getFinancingId())) {
                pageSumBody.setFinancingAmount(LongUtil.null2zero(pageSumBody.getFinancingAmount()) + LongUtil.null2zero(shortTermLoanDetailBody.getFinancingAmount()));
                addFinancingIds.add(actualLib.getFinancingId());
            }
            bodies.add(shortTermLoanDetailBody);
        });
        shortTermLoanDetailRSP.setDetailBodies(PageR.of(bodies, fundFinancingRepayActualLibPage.getTotal(),
                fundFinancingRepayActualLibPage.getCurrent(), fundFinancingRepayActualLibPage.getSize()));
        return shortTermLoanDetailRSP;
    }

    @Override
    public void shortTermLoanDownload(ShortTermLoanDetailREQ req, OutputStream outputStream) {
        ShortTermLoanDetailRSP shortTermLoanDetailRSP = this.shortTermLoanDetail(req);
        if (ObjectUtil.isEmpty(shortTermLoanDetailRSP) || ObjectUtil.isEmpty(shortTermLoanDetailRSP.getDetailBodies())) {
            return;
        }
        List<LiquidityRiskShortTermLoanExcelModel> liquidityRiskShortTermLoanExcelModels = shortTermLoanDetailRSP.getDetailBodies().getList().stream().map(base -> {
            LiquidityRiskShortTermLoanExcelModel model = BeanUtil.copyProperties(base,
                    LiquidityRiskShortTermLoanExcelModel.class);
            model.setFinancingAmount(Long2BigDecimal(base.getFinancingAmount()));
            model.setPrincipleAmount(Long2BigDecimal(base.getPrincipleAmount()));
            model.setInterestAmount(Long2BigDecimal(base.getInterestAmount()));
            model.setTotalAmount(Long2BigDecimal(base.getTotalAmount()));
            return model;
        }).collect(Collectors.toList());
        //添加最后一行
        LiquidityRiskShortTermLoanExcelModel liquidityRiskInflowExcelModel = new LiquidityRiskShortTermLoanExcelModel();
        liquidityRiskInflowExcelModel.setOrganizationName("合计");
        liquidityRiskInflowExcelModel.setFinancingAmount(Long2BigDecimal(shortTermLoanDetailRSP.getPageSum().getFinancingAmount()));
        liquidityRiskInflowExcelModel.setPrincipleAmount(Long2BigDecimal(shortTermLoanDetailRSP.getPageSum().getPrincipleAmount()));
        liquidityRiskInflowExcelModel.setInterestAmount(Long2BigDecimal(shortTermLoanDetailRSP.getPageSum().getInterestAmount()));
        liquidityRiskInflowExcelModel.setTotalAmount(Long2BigDecimal(shortTermLoanDetailRSP.getPageSum().getTotalAmount()));
        liquidityRiskShortTermLoanExcelModels.add(liquidityRiskInflowExcelModel);
        liquidityRiskShortTermLoanExcelExporter.exportExcel(liquidityRiskShortTermLoanExcelModels, outputStream);
    }

    @Override
    public ChartQueryRSP expectedCashFlowInflowChart(ChartQueryREQ req) {
        ChartQueryRSP chartQueryRSP = baseInflowChart(req, false);
        chartQueryRSP.setDataType("预估现金流流入");
        chartQueryRSP.setChartType(LINE);
        return chartQueryRSP;
    }

    @Override
    public ChartQueryRSP pressureTestInflowChart(ChartQueryREQ req) {
        ChartQueryRSP chartQueryRSP = baseInflowChart(req, true);
        chartQueryRSP.setDataType("压力测试-流入");
        chartQueryRSP.setChartType(LINE);
        return chartQueryRSP;
    }

    private ChartQueryRSP baseInflowChart(ChartQueryREQ req, Boolean needPressure) {
        ChartQueryRSP chartQueryRSP = new ChartQueryRSP();
        chartQueryRSP.setDetails(new ArrayList<>());
        Map<String, Long> map = new HashMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = req.getTimeFrom().toLocalDate();
        String dateString;
        //查询数据
        AssetInflowDetailRSP assetInflowDetailRSP = getAssetInflowDetailRSP(req);
        //压力测试数据
        Map<String, Long> financingDeliverDetailSettingsMap = new HashMap<>();
        BaseAmountSetting baseAmountSetting = baseAmountSettingMapper.selectOne(Wrappers.<BaseAmountSetting>lambdaQuery()
                .orderByDesc(BaseAmountSetting::getId)
                .last(StringUtil.mysqlLimitOne()));
        //需要压力数据
        if (needPressure) {
            financingDeliverDetailSettingMapper.selectList(Wrappers.<FinancingDeliverDetailSetting>lambdaQuery()
                    .eq(FinancingDeliverDetailSetting::getType, YesOrNoNumberEnum.NO.getCode())).forEach(detailSetting -> {
                String format = detailSetting.getDate().format(dateTimeFormatter);
                financingDeliverDetailSettingsMap.put(format,
                        LongUtil.null2zero(financingDeliverDetailSettingsMap.get(format)) + LongUtil.null2zero(detailSetting.getAmount()));
            });
        }
        //流入基础值
        long baseAmount = 0L;
        if (ObjectUtil.isNotEmpty(baseAmountSetting)) {
            baseAmount += LongUtil.null2zero(baseAmountSetting.getBeginCashflowAmount());
            baseAmount += LongUtil.null2zero(baseAmountSetting.getOtherIncome());
        }
        //按照日期累加
        // 1.按照日期汇总
        // 2。按照日期递增
        if (ObjectUtil.isNotEmpty(assetInflowDetailRSP.getDetailBodies()) && ObjectUtil.isNotEmpty(assetInflowDetailRSP.getDetailBodies().getList())) {
            assetInflowDetailRSP.getDetailBodies().getList().forEach(detailBody -> {
                String key = detailBody.getFlowInDate().format(dateTimeFormatter);
                map.put(key, LongUtil.null2zero(map.get(key)) + LongUtil.null2zero(detailBody.getEstimatedCashInFlowTotal()));
            });
        }
        //
        ChartQueryRSP.ChartQueryDetail chartQueryDetail;
        for (; newDate.isBefore(req.getTimeTo().toLocalDate().plusDays(1)); ) {
            dateString = newDate.format(dateTimeFormatter);
            chartQueryDetail = new ChartQueryRSP.ChartQueryDetail();
            baseAmount += LongUtil.null2zero(map.get(dateString));
            baseAmount += LongUtil.null2zero(financingDeliverDetailSettingsMap.get(dateString));
            chartQueryDetail.setName(dateString);
            chartQueryDetail.setValue(baseAmount);
            chartQueryRSP.getDetails().add(chartQueryDetail);
            newDate = newDate.plusDays(1);
        }
        return chartQueryRSP;
    }

    private AssetInflowDetailRSP getAssetInflowDetailRSP(ChartQueryREQ req) {
        AssetInflowDetailRSP assetInflowDetailRSP = assetInflowDetailRSPThreadLocal.get();
        if (ObjectUtil.isEmpty(assetInflowDetailRSP)) {
            AssetInflowDetailREQ assetInflowDetailREQ = new AssetInflowDetailREQ();
            assetInflowDetailREQ.setTimeFrom(req.getTimeFrom());
            assetInflowDetailREQ.setTimeTo(req.getTimeTo());
            assetInflowDetailREQ.setEstimatedOverdueRate(req.getEstimatedOverdueRate());
            assetInflowDetailREQ.setNeedPage(false);
            assetInflowDetailRSP = this.inFlowDetail(assetInflowDetailREQ);
            assetInflowDetailRSPThreadLocal.set(assetInflowDetailRSP);
        }
        return assetInflowDetailRSP;
    }

    @Override
    public void threadRemove() {
        assetInflowDetailRSPThreadLocal.remove();
    }
}
