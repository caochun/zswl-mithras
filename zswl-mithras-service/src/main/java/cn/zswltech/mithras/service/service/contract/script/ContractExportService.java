package cn.zswltech.mithras.service.service.contract.script;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.price.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.contract.convert.contract.ContractBaseInfoConverter;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.service.excel.exporter.ContractExcelExporter;
import cn.zswltech.mithras.service.excel.exporter.ContractExcelExporter2;
import cn.zswltech.mithras.service.excel.model.ContractExcelModel;
import cn.zswltech.mithras.service.excel.model.ContractExcelModel2;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeaseItemLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus.WRITTEN_OFF;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/4 19:54
 */
@Service
public class ContractExportService {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ContractLeaseItemLibService contractLeaseItemLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractBaseInfoConverter contractBaseInfoConverter;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private ContractExcelExporter exporter;
    @Resource
    private ContractExcelExporter2 exporter2;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void exportContracts(OutputStream outputStream) {
        List<ContractExcelModel> exportRsps = new ArrayList<>();
        contractBaseInfoService.getBaseMapper()
                .selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .notIn(ContractBaseInfo::getContractStatus, Arrays.asList("NEW", "CLOSED", "INVALID")))
                .stream().map(ContractBaseInfo::getId).forEach(aLong -> {
                    ContractBaseInfoLib lastEffectLib = contractBaseInfoLibService.getOne(
                            Wrappers.<ContractBaseInfoLib>lambdaQuery()
                                    .eq(ContractBaseInfoLib::getOriginId, aLong)
                                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                                    .orderByDesc(ContractBaseInfoLib::getVersion)
                                    .last("LIMIT 1"));
                    if (ObjectUtil.isEmpty(lastEffectLib)) {
                        return;
                    }
                    // 处理基本信息
                    ContractExcelModel rsp = contractBaseInfoConverter.libEntity2exportRsp(lastEffectLib);
                    Map<Long, String> clientId2Name =
                            id2NameService.clientId2Name(Collections.singletonList(rsp.getClientId()));
                    rsp.setClientName(clientId2Name.get(rsp.getClientId()));
                    rsp.setProjSponsorUserName(id2NameService.sysUserId2NameSingle(rsp.getProjSponsorUserId()));
                    Map<Long, String> deptId2Name =
                            id2NameService.deptId2Name(Collections.singletonList(rsp.getBizDeptId()));
                    rsp.setBizDeptName(deptId2Name.get(rsp.getBizDeptId()));
                    rsp.setBizTypeDisplay(ProjectBizType.valueOf(rsp.getBizType()).display);
                    if (ObjectUtil.isNotEmpty(rsp.getProjectType())) {
                        rsp.setProjectTypeDisplay(ProjectType.valueOf(rsp.getProjectType()).display);
                    }
                    // 处理担保人信息
                    List<Long> guarantorIds = contractGuarantorLibService.getBaseMapper()
                            .selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                                    .eq(ContractGuarantor::getContractId, aLong)
                                    .eq(ContractGuarantorLib::getVersion, lastEffectLib.getVersion()))
                            .stream().map(ContractGuarantor::getGuarantorIds)
                            .flatMap((Function<String, Stream<Long>>) s -> JSON.parseArray(s, Long.class).stream())
                            .collect(Collectors.toList());
                    Map<Long, String> guarantorNames = id2NameService.clientId2Name(guarantorIds);
                    if (ObjectUtil.isNotEmpty(guarantorNames)) {
                        rsp.setGuarantors(String.join(",", guarantorNames.values()));
                    }
                    // 处理报价信息
                    ContractPriceDetailREQ priceReq = new ContractPriceDetailREQ(aLong);
                    ContractPriceDetailRSP contractPrice = contractPriceService.editionDetail(priceReq);
                    if (ObjectUtil.isNotEmpty(contractPrice)) {
                        ProjectBizType projectBizType = ProjectBizType.valueOf(rsp.getBizType());
                        switch (projectBizType) {
                            case ZL:
                            case ZZ:
                                ContractLeasePriceDetailRSP leasePrice = contractPrice.getLeasePriceModifyRSP();
                                if (ObjectUtil.isNotEmpty(leasePrice)) {
                                    rsp.setIrrPercent(minusWan(leasePrice.getIrrPercent()));
                                    if (ObjectUtil.isNotEmpty(leasePrice.getLprPercent())
                                            && ObjectUtil.isNotEmpty(leasePrice.getLprAddPercent())) {
                                        rsp.setRatePercent(minusWan(leasePrice.getLprPercent()
                                                + leasePrice.getLprAddPercent()));
                                    }
                                    rsp.setRateType(RateType.valueOf(leasePrice.getRateType()).display);
                                    rsp.setCreditTerm(leasePrice.getLeaseMonthCount());
                                    rsp.setEarnestMoney(toWan(leasePrice.getEarnestMoney()));
                                }
                                break;
                            case BL:
                                ContractFactoringPriceDetailRSP price = contractPrice.getFactoringPriceRSP();
                                if (ObjectUtil.isNotEmpty(price)) {
                                    rsp.setIrrPercent(minusWan(price.getIrrPercent()));
                                    if (ObjectUtil.isNotEmpty(price.getLprPercent())
                                            && ObjectUtil.isNotEmpty(price.getLprAddPercent())) {
                                        rsp.setRatePercent(minusWan(price.getLprPercent()
                                                + price.getLprAddPercent()));
                                    }
                                    rsp.setRateType(RateType.valueOf(price.getRateType()).display);
                                    rsp.setCreditTerm(price.getFactoringCreditTerm());
                                    rsp.setEarnestMoney(toWan(price.getEarnestMoney()));
                                }
                                break;
                            case ZR:
                                ContractAocPriceDetailRSP aocPrice = contractPrice.getAocPriceRSP();
                                if (ObjectUtil.isNotEmpty(aocPrice)) {
                                    rsp.setIrrPercent(minusWan(aocPrice.getIrrPercent()));
                                    if (ObjectUtil.isNotEmpty(aocPrice.getLprPercent())
                                            && ObjectUtil.isNotEmpty(aocPrice.getLprAddPercent())) {
                                        rsp.setRatePercent(minusWan(aocPrice.getLprPercent()
                                                + aocPrice.getLprAddPercent()));
                                    }
                                    rsp.setRateType(RateType.valueOf(aocPrice.getRateType()).display);
                                    rsp.setCreditTerm(aocPrice.getAocCreditTerm());
                                    rsp.setEarnestMoney(toWan(aocPrice.getEarnestMoney()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    // 处理租赁物清单
                    LeaseItemValues leaseItemValues = contractLeaseItemLibService.getBaseMapper().selectList(
                                    Wrappers.<ContractLeaseItemLib>lambdaQuery().eq(ContractLeaseItem::getContractId, aLong)
                                            .eq(ContractLeaseItemLib::getVersion, lastEffectLib.getVersion()))
                            .stream().collect(new LeaseItemValueCollector());
                    if (leaseItemValues.getOriginalBookValueSum() > 0) {
                        rsp.setOriginalBookValue(toYuan(leaseItemValues.getOriginalBookValueSum()));
                    }
                    if (leaseItemValues.getOriginalBookNetSum() > 0) {
                        rsp.setOriginalBookNetValue(toYuan(leaseItemValues.getOriginalBookNetSum()));
                    }
                    if (leaseItemValues.getAssessedSum() > 0) {
                        rsp.setAssessedValue(toYuan(leaseItemValues.getAssessedSum()));
                    }
                    if (leaseItemValues.getAssessedNetSum() > 0) {
                        rsp.setAssessedNetValue(toYuan(leaseItemValues.getAssessedNetSum()));
                    }
                    // 处理投放信息
                    List<Long> paymentIds = paymentBaseInfoService.getBaseMapper().selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                                    .eq(PaymentBaseInfo::getContractId, aLong)
                                    .eq(PaymentBaseInfo::getPaymentProcessStatus, ProcessStatus.APPROVAL_PASS.name()))
                            .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(paymentIds)) {
                        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.getBaseMapper()
                                .selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                                        .in(PaymentActualDetail::getPaymentId, paymentIds)
                                        .eq(PaymentActualDetail::getWriteOffStatus, WRITTEN_OFF.name()));
                        if (ObjectUtil.isNotEmpty(paymentActualDetails)) {
                            LocalDate paidInDate = LocalDate.now();
                            Long paidAmountSum = 0L;
                            for (PaymentActualDetail paymentActualDetail : paymentActualDetails) {
                                if (paymentActualDetail.getPaidInDate().isBefore(paidInDate)) {
                                    paidInDate = paymentActualDetail.getPaidInDate();
                                }
                                if (ObjectUtil.isNotEmpty(paymentActualDetail.getPaidInAmount())) {
                                    paidAmountSum += paymentActualDetail.getPaidInAmount();
                                }
                            }
                            rsp.setPaidAmount(toWan(paidAmountSum));
                            rsp.setFirstPaidDate(paidInDate.format(df));
                        }
                    }
                    // 剩余本金 及 剩余利息
                    List<CollectionBaseInfo> baseInfos = collectionBaseInfoMapper.selectList(
                            Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, aLong));
                    long receivedPrincipal = 0, principal = 0, receivedInterest = 0, interest = 0;
                    for (CollectionBaseInfo info : baseInfos) {
                        receivedPrincipal += LongUtil.null2zero(info.getCollectionPrincipal());
                        principal += LongUtil.null2zero(info.getPrincipal());
                        receivedInterest += LongUtil.null2zero(info.getCollectionInterest());
                        interest += LongUtil.null2zero(info.getInterest());
                    }
                    rsp.setRemainInterest(toYuan(interest - receivedInterest));
                    rsp.setRemainPrincipal(toYuan(principal - receivedPrincipal));

                    // 到期日
                    ContractRentActualLib contractRentActualLib = contractRentActualLibMapper
                            .selectOne(Wrappers.<ContractRentActualLib>lambdaQuery()
                                    .eq(ContractRentActual::getContractId, aLong)
                                    .eq(ContractRentActualLib::getVersion, lastEffectLib.getVersion())
                                    .orderByDesc(ContractRentActual::getCashFlowDate)
                                    .last("LIMIT 1"));
                    if (ObjectUtil.isNotEmpty(contractRentActualLib)
                            && ObjectUtil.isNotEmpty(contractRentActualLib.getCashFlowDate())) {
                        rsp.setDueDate(contractRentActualLib.getCashFlowDate().format(df));
                    }
                    rsp.setId(aLong);
                    exportRsps.add(rsp);
                });
        exporter.exportExcel(exportRsps, outputStream);
    }

    /**
     * 拉一份合同清单，字段包含：项目名称、合同编号、承租人、担保人、主办、风控经理、风控行业分类
     */
    public void export2(OutputStream outputStream) {
        List<ContractExcelModel2> exportRsps = new ArrayList<>();

        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractStatus, "START_RENT", "TAKE_EFFECT", "SETTLE"));
        List<Long> userIds = new ArrayList<>();
        List<Long> clientIds = new ArrayList<>();
        contracts.forEach(contractBaseInfo -> {
            userIds.add(contractBaseInfo.getProjSponsorUserId());
            if (Objects.nonNull(contractBaseInfo.getRiskControlManagerId())) {
                userIds.add(contractBaseInfo.getRiskControlManagerId());
            }
            clientIds.add(contractBaseInfo.getClientId());
        });
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        for (ContractBaseInfo contract : contracts) {
            ContractExcelModel2 rsp = new ContractExcelModel2();
            rsp.setContractCode(contract.getContractCode());
            rsp.setProjName(contract.getProjName());
            rsp.setClientName(clientId2Name.get(contract.getClientId()));
            rsp.setProjSponsorUserName(userId2Name.get(contract.getProjSponsorUserId()));
            rsp.setRiskControlManagerName(userId2Name.get(contract.getRiskControlManagerId()));
//            rsp.setRiskControlIndustryClassify(RiskControlIndustryClassify.valueOf(contract.getRiskControlIndustryClassify()).display());
            // 处理担保人信息
            List<Long> guarantorIds = contractGuarantorLibService.getBaseMapper()
                    .selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                            .eq(ContractGuarantor::getContractId, contract.getId()))
                    .stream().map(ContractGuarantor::getGuarantorIds)
                    .flatMap((Function<String, Stream<Long>>) s -> JSON.parseArray(s, Long.class).stream())
                    .collect(Collectors.toList());
            Map<Long, String> guarantorNames = id2NameService.clientId2Name(guarantorIds);
            if (ObjectUtil.isNotEmpty(guarantorNames)) {
                rsp.setGuarantors(String.join(",", guarantorNames.values()));
            }
        }
        exporter2.exportExcel(exportRsps, outputStream);
    }

    protected String minusWan(Integer dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(),
                String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.####", bigDecimal);
    }

    protected String toYuan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(),
                String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.####", bigDecimal);
    }

    protected String toWan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(),
                String.valueOf(10000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.########", bigDecimal);
    }
}

@Data
class LeaseItemValues {
    private Long originalBookValueSum;
    private Long originalBookNetSum;
    private Long assessedSum;
    private Long assessedNetSum;
}

@Data
class LeaseItemValueIncrease {
    private AtomicLong originalBookValue;
    private AtomicLong originalBookNetValue;
    private AtomicLong assessedValue;
    private AtomicLong assessedNetValue;

    LeaseItemValueIncrease() {
        originalBookValue = new AtomicLong(0);
        originalBookNetValue = new AtomicLong(0);
        assessedValue = new AtomicLong(0);
        assessedNetValue = new AtomicLong(0);
    }
}

class LeaseItemValueCollector implements Collector<ContractLeaseItemLib, LeaseItemValueIncrease, LeaseItemValues> {
    @Override
    public Supplier<LeaseItemValueIncrease> supplier() {
        return LeaseItemValueIncrease::new;
    }

    @Override
    public BiConsumer<LeaseItemValueIncrease, ContractLeaseItemLib> accumulator() {
        return (leaseItemValueIncrease, t) -> {
            if (ObjectUtil.isNotEmpty(t.getOriginalBookValue())) {
                leaseItemValueIncrease.getOriginalBookValue().addAndGet(t.getOriginalBookValue());
            }
            if (ObjectUtil.isNotEmpty(t.getOriginalBookNetValue())) {
                leaseItemValueIncrease.getOriginalBookNetValue().addAndGet(t.getOriginalBookNetValue());
            }
            if (ObjectUtil.isNotEmpty(t.getAssessedValue())) {
                leaseItemValueIncrease.getAssessedValue().addAndGet(t.getAssessedValue());
            }
            if (ObjectUtil.isNotEmpty(t.getAssessedNetValue())) {
                leaseItemValueIncrease.getAssessedNetValue().addAndGet(t.getAssessedNetValue());
            }
        };
    }

    @Override
    public BinaryOperator<LeaseItemValueIncrease> combiner() {
        return (leaseItemValueIncrease, leaseItemValueIncrease2) -> {
            leaseItemValueIncrease.getOriginalBookValue()
                    .addAndGet(leaseItemValueIncrease2.getOriginalBookValue().get());
            leaseItemValueIncrease.getOriginalBookNetValue()
                    .addAndGet(leaseItemValueIncrease2.getOriginalBookNetValue().get());
            leaseItemValueIncrease.getAssessedValue()
                    .addAndGet(leaseItemValueIncrease2.getAssessedValue().get());
            leaseItemValueIncrease.getAssessedNetValue()
                    .addAndGet(leaseItemValueIncrease2.getAssessedNetValue().get());
            return leaseItemValueIncrease;
        };
    }

    @Override
    public Function<LeaseItemValueIncrease, LeaseItemValues> finisher() {
        return leaseItemValueIncrease -> {
            LeaseItemValues res = new LeaseItemValues();
            res.setOriginalBookValueSum(leaseItemValueIncrease.getOriginalBookValue().get());
            res.setOriginalBookNetSum(leaseItemValueIncrease.getOriginalBookNetValue().get());
            res.setAssessedSum(leaseItemValueIncrease.getAssessedValue().get());
            res.setAssessedNetSum(leaseItemValueIncrease.getAssessedNetValue().get());
            return res;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        Set<Characteristics> characteristics = new HashSet<>();
        // 指定该收集器支持并发处理（前面也发现我们采用了线程安全的AtomicInteger方式）
        characteristics.add(Characteristics.CONCURRENT);
        // 声明元素数据处理的先后顺序不影响最终收集的结果
        characteristics.add(Characteristics.UNORDERED);
        // 注意:这里没有添加下面这句，因为finisher方法对结果进行了处理，非恒等转换
        // characteristics.add(Characteristics.IDENTITY_FINISH);
        return characteristics;
    }
}


