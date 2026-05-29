package cn.zswltech.mithras.service.service.stampduty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.stampduty.*;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.fund.StampDutyBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.kpi.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.ContractBusinessModelEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.excel.exporter.StampDutyExcelExporter;
import cn.zswltech.mithras.service.excel.importer.StampDutyExcelImporter;
import cn.zswltech.mithras.service.excel.model.StampDutyExcelModel;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.stampduty.StampDutyDetail;
import cn.zswltech.mithras.service.mapper.stampduty.StampDutyMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.bo.StampDutyBO;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractReceiptLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractRentActualLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

@Service
@Slf4j
public class ReportStampDutyService  extends ServiceImpl<StampDutyMapper, StampDutyDetail> {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService organizationService;
    @Autowired
    private ContractLeasePriceService leasePriceService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private StampDutyExcelImporter stampDutyExcelImporter;
    @Resource
    private StampDutyExcelExporter stampDutyExcelExporter;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    StampDutyMapper stampDutyMapper;
    @Resource
    protected FlowVariableApiService flowVariableApiService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;

    private static final String FORMAT = "yyyy-MM-dd";
    public static final String TARGET_RECEIPT_ID = "targetReceiptId";

    /**
     * 增量更新印花税
     * @author: luyujie
     * @date: 2025/11/27
     **/
    public void refreshStampDuty(String id) {
        //获取印花税
//        List<StampDutyDetail> stampDutyDetailList = this.list(Wrappers.<StampDutyDetail>lambdaQuery());
//        if(CollUtil.isEmpty(stampDutyDetailList)){
//            refreshStampDuty(id,null);
//        } else {
            // t日
            LocalDate dateTime = LocalDate.now();
            refreshStampDuty(id,dateTime);
//        }
    }

    public void refreshStampDuty(String id,LocalDate dateTime) {
        List<StampDutyDetail> res = new ArrayList<>();
        //获取业务类型=“租赁”&流程“合同起租”、“合同起租-系统自动发起”、“合同新增借据（投放）”审批状态=审批通过&审批通过时间为T-1。（融资租赁）
        List<StampDutyDetail> rzzlhtRes = getRZZLHT(id,dateTime);
        res.addAll(rzzlhtRes);
        //获取业务类型=“流动资金贷款”、“项目贷款”、“银团”、“保理融资”       &融资状态=“起息”&起息审批流程审批通过时间为T-1的合同。（间接融资）
        List<StampDutyDetail> jjrzhtRes = getJJRZHT(id,dateTime);
        res.addAll(jjrzhtRes);
        if(CollUtil.isNotEmpty(res)){
            //保存新增印花税
            this.saveBatch(res);
        }
    }

    public List<StampDutyDetail> getRZZLHT(String id,LocalDate dateTime) {
        List<StampDutyDetail> rzzlhtRes = new ArrayList<>();
        //获取业务类型=“租赁”&流程“合同起租”、“合同起租-系统自动发起”、“合同新增借据（投放）”审批状态=审批通过&审批通过时间为T-1。（融资租赁）
        List<StampDutyBO> stampDutyBOList = stampDutyMapper.queryPreApprovedProcess(dateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)),
                ListUtil.of(ProcessModelTypeEnum.ContractStartRentFlow.name(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name()));
        if (CollUtil.isEmpty(stampDutyBOList)) {
            return rzzlhtRes;
        }
        List<String> contractIds = stampDutyBOList.stream().map(StampDutyBO::getBusinessKey).collect(Collectors.toList());
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
        query.eq(ContractBaseInfo::getBizType, ProjectBizType.ZL.name());
        query.in(ContractBaseInfo::getId,contractIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        if(StringUtils.isNotEmpty(id)){
            if (!contractIds.contains(id)) {
                return rzzlhtRes;
            }
            query.eq(ContractBaseInfo::getId, id);
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(query);
        if(CollUtil.isEmpty(contractBaseInfos)){
            return rzzlhtRes;
        }

        Set<Long> deptIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        contractBaseInfos.stream().forEach(contract -> {
            deptIds.add(Long.valueOf(contract.getBizDeptId()));
            clientIds.add(Long.valueOf(contract.getClientId()));
        });

        Map<Long, List<ContractBaseInfo>> contractGroupByIdMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getId));
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);

        LambdaQueryWrapper<CollectionBaseInfo> collectionBaseInfoLambdaQuery = Wrappers.lambdaQuery();
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getContractId, contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.OTHERAMOUNT.name(),CashFlowItemEnum.COMMISSION.name()));
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getWriteOffStatus,  ListUtil.toList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(collectionBaseInfoLambdaQuery);

        for(StampDutyBO stampDutyBO : stampDutyBOList){
            Long contractId = Long.valueOf(stampDutyBO.getBusinessKey());
            if (!contractGroupByIdMap.containsKey(contractId)) {
                continue;
            }
            ContractBaseInfo contractBaseInfo = contractGroupByIdMap.get(contractId).get(0);
            //是否起租流程
            boolean rentProcessFlag = CharSequenceUtil.equalsAny(stampDutyBO.getModelKey(), ProcessModelTypeEnum.ContractStartRentFlow.name(),
                    ProcessModelTypeEnum.ContractStartRentAutoFlow.name());
            //直租合同只存在一笔借据，故只需在合同起租时生成印花税
            if (ObjectUtil.equals(ContractBusinessModelEnum.zhi_zu.name(), contractBaseInfo.getLeaseType())
                    && !rentProcessFlag) {
                continue;
            }
            //获取流程参数中流程审批时关联的借据
            Map<String, Object> varMap = flowVariableApiService.getVariables(stampDutyBO.getProcInstId(),
                    Collections.singletonList(TARGET_RECEIPT_ID));
            if (Objects.isNull(varMap) || !varMap.containsKey(TARGET_RECEIPT_ID)) {
                continue;
            }
            //新增借据投放流程关联的借据为空
            Object targetReceiptId = varMap.get(TARGET_RECEIPT_ID);
            if (Objects.isNull(targetReceiptId) && !rentProcessFlag) {
                continue;
            }
            log.info("进入方法合同[contractId:{}]", contractId);
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(contractId);
            if (Objects.isNull(contractBaseInfoLib)) {
                log.info("没有找到最新的合同生效版本数据[contractId:{}]", contractId);
                continue;
            }
            List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibService.listByContractIdVersion(contractId, contractBaseInfoLib.getVersion());
            if (CollectionUtil.isEmpty(contractReceiptLibList)) {
                log.info("没有找到最新的借据生效版本数据[contractId:{}]", contractId);
                continue;
            }
//            List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibService.listLibByContractVersion(contractId, contractBaseInfoLib.getVersion());
//            if (CollectionUtil.isEmpty(contractRentActualLibList)) {
//                log.info("没有找到最新的实际租金表生效版本数据[contractId:{}]", contractId);
//                continue;
//            }
            Long bizdeptId = contractBaseInfo.getBizDeptId();
            Long clientId = contractBaseInfo.getClientId();
            for(ContractReceiptLib contractReceiptLib : contractReceiptLibList){
                LocalDate receiptStartDate = contractReceiptLib.getReceiptStartDate();
                Long receiptId= contractReceiptLib.getOriginId();
                //起租流程关联首笔借据
                boolean rentReceiptFlag = rentProcessFlag && !Objects.equals(contractReceiptLib.getIsFirstReceipt(), YesOrNoNumberEnum.YES.getCode());
                //新增借据流程关联新增借据流程审批通过时放入流程参数中的借据
                boolean addReceiptFlag = !rentProcessFlag && !Objects.equals(contractReceiptLib.getOriginId(), targetReceiptId);
                if (rentReceiptFlag || addReceiptFlag) {
                    continue;
                }
                //是否已生成
                List<StampDutyDetail> stampDutyDetails = stampDutyMapper.selectList(Wrappers.<StampDutyDetail>lambdaQuery()
                        .eq(StampDutyDetail::getReceiptId, receiptId)
                        .eq(StampDutyDetail::getIsDelete, "0"));
                if(CollUtil.isNotEmpty(stampDutyDetails)){
                    continue;
                }

                //以借据为维度
                List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getReceiptId, contractReceiptLib.getOriginId()));
                List<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());

                List<CollectionBaseInfo> collectBaseInfoList = collectionBaseInfoList.stream().filter(e -> (CollUtil.isNotEmpty(paymentIds) && Objects.nonNull(e.getPaymentId())
                        && paymentIds.contains(e.getPaymentId()))
                        || (Objects.nonNull(e.getReceiptId()) && e.getReceiptId().equals(receiptId))).collect(Collectors.toList());
                long consultingFee = collectBaseInfoList.stream().filter(e -> CashFlowItemEnum.OTHERAMOUNT.name().equals(e.getCashFlowItem())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                long commission = collectBaseInfoList.stream().filter(e -> CashFlowItemEnum.COMMISSION.name().equals(e.getCashFlowItem())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                try {
                    BigDecimal commissionTaxRate = getCommissionTaxRate(contractBaseInfoLib.getLeaseType(),contractReceiptLib.getTaxRate(),receiptId);
                    BigDecimal consultingFeeTaxRate = getConsultingFeeTaxRate(contractBaseInfoLib.getLeaseType());
                    consultingFee = BigDecimal.valueOf(consultingFee).divide(BigDecimal.ONE.add(consultingFeeTaxRate),2, RoundingMode.HALF_UP).longValue();
                    commission = BigDecimal.valueOf(commission).divide(BigDecimal.ONE.add(commissionTaxRate),2, RoundingMode.HALF_UP).longValue();
                } catch (Exception e){
                    log.error("手续费/咨询费税率获取失败！");
                    continue;
                }


                StampDutyDetail stampDutyDetail = new StampDutyDetail();
                stampDutyDetail.setBelongId(contractId);
                String stampTaxRate;
                if(ObjectUtil.equals(ContractBusinessModelEnum.jyx_zu.name(), contractBaseInfoLib.getLeaseType())){
                    stampDutyDetail.setName(StampDutyBizTypeEnum.ZLHT.name());
                    stampTaxRate = "0.100";
                }else{
                    stampDutyDetail.setName(StampDutyBizTypeEnum.RZZLHT.name());
                    stampTaxRate = "0.005";
                }
                stampDutyDetail.setBelongOrgId(bizdeptId);
                stampDutyDetail.setBelongOrgName(deptMap.get(bizdeptId));
                stampDutyDetail.setClientId(clientId);
                stampDutyDetail.setClientName(clientMap.get(clientId));
                stampDutyDetail.setBelongCode(contractBaseInfo.getContractCode());
                stampDutyDetail.setReceiptId(receiptId);
                stampDutyDetail.setReceiptCode(contractReceiptLib.getReceiptCode());
                stampDutyDetail.setStartDate(receiptStartDate);
                //3.不含税租金： sum（租金表中的租金） + 首期利息 + 首期租金 - 税额
                Long rentExcludingTax = contractReceiptLib.getRentExcludingTax();
                if(Objects.isNull(rentExcludingTax)){
                    //查询实际租金列表数据
                    List<ContractRentActual> rentActualList = getContractRentActuals(receiptId);
                    if (CollectionUtils.isEmpty(rentActualList)) {
                        continue;
                    }
                    //2.税率：直租税率：13%    非直租税率：6%
                    BigDecimal taxRate = LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType()) ? GlobalConstants.TAX_RATE_ZHI_ZU : GlobalConstants.TAX_RATE_FEI_ZHI_ZU;

                    //租金总额
                    Long rentSum = rentActualList.stream().map(ContractRentActual::getRent).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
                    //租金表中将第0期剔除掉
                    rentActualList = rentActualList.stream().filter(rentActual -> rentActual.getCashFlowPhase() != 0).collect(Collectors.toList());

                    //本金总额
                    Long capitalSum = rentActualList.stream().map(ContractRentActual::getPrincipal).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
                    //利息总额
                    Long interestSum = rentActualList.stream().map(ContractRentActual::getInterest).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);

                    BigDecimal tax; // 税额
                    BigDecimal stampDuty; //印花税
                    Long firstRent = 0L; // 首期租金
                    Long firstInstallmentInterest = 0L; // 首期利息
                    List<CollectionBaseInfo> collectionBaseInfos = new ArrayList<>();
                    List<Long> paymentBaseInfoIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(paymentBaseInfoIds)) {
                        collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .in(CollectionBaseInfo::getPaymentId, paymentBaseInfoIds)
                                .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()));
                    }
                    //合同状态为起租的合同状态取借据维度已核销的首期租金、首期利息实际付款金额
                    if (CollectionUtils.isNotEmpty(collectionBaseInfos)) {
                        firstRent = collectionBaseInfos.stream()
                                .filter(collectionBaseInfo -> CashFlowItemEnum.FIRST_RENT.name().equals(collectionBaseInfo.getCashFlowItem()))
                                .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
                        firstInstallmentInterest = collectionBaseInfos.stream()
                                .filter(collectionBaseInfo -> CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && collectionBaseInfo.getPhase() == 0)
                                .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
                    }
                    //1.不含税利息的计算公式：【sum（租金表中的利息）+ 首期利息】/（1+税率），保留2位小数。
                    BigDecimal excludingInterestTax = NumberUtil.div(interestSum + firstInstallmentInterest, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP);

                    //2.税额计算公式：
                    if (LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
                        //直租：sum（租金表中的利息）+ 首期利息 - 不含税利息 + （sum（租金表中的 本金+ 首期租金）- sum（租金表中的 本金+ 首期租金）/（1+税率））
                        tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax)
                                .add(BigDecimal.valueOf(capitalSum + firstRent).subtract(NumberUtil.div(capitalSum + firstRent, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP)));
                    } else {
                        //其他：sum（租金表中的利息）+ 首期利息 - 不含税利息
                        tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax);
                    }
                    //不含税租金
                    rentExcludingTax = Util.mithrasLongDecimalTwo(NumberUtil.sub(BigDecimal.valueOf(rentSum + firstInstallmentInterest + firstRent), tax).longValue());
                }
                stampDutyDetail.setRent(rentExcludingTax);
                stampDutyDetail.setCommission(commission);//不含税手续费
                stampDutyDetail.setConsultingFee(consultingFee);
                Long amount = rentExcludingTax + commission + consultingFee;
                stampDutyDetail.setAmount(amount);
                stampDutyDetail.setTaxRate(stampTaxRate);
                BigDecimal bd = new BigDecimal(amount);
                BigDecimal bd1 = new BigDecimal(stampTaxRate).divide(new BigDecimal("100"));
                BigDecimal result1 = bd.multiply(bd1);
                result1 = result1.setScale(2, RoundingMode.HALF_UP);
                stampDutyDetail.setStampDuty(result1.toString());
                stampDutyDetail.setScoure("System");
                stampDutyDetail.setIsDelete("0");
                rzzlhtRes.add(stampDutyDetail);
                if(ObjectUtil.equals(ContractBusinessModelEnum.zhi_zu.name(), contractBaseInfoLib.getLeaseType())){
                    //买卖合同的不含税租金取借据的实际投放款
                    List<Long> paymentBaseInfoIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                    Long finishedSum = actualDetailService.calculatePaidAmount(paymentBaseInfoIds);
                    Long zhizuTaxRate = contractReceiptLib.getTaxRate();
                    if(Objects.isNull(zhizuTaxRate)){
                        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
                        if (Objects.nonNull(contractReceipt) && Objects.nonNull(contractReceipt.getTaxRate())) {
                            zhizuTaxRate = contractReceipt.getTaxRate();
                        } else {
                            zhizuTaxRate = Util.toMithrasUnit(GlobalConstants.TAX_RATE_ZHI_ZU.multiply(BigDecimal.valueOf(100)));
                        }
                    }
                    BigDecimal add = BigDecimal.ONE.add(BigDecimal.valueOf(zhizuTaxRate).divide(BigDecimal.valueOf(1000000)));
                    String taxRate2 = "0.030";
                    bd = new BigDecimal(finishedSum);
                    BigDecimal bd2 = new BigDecimal(taxRate2).divide(new BigDecimal("100"));
                    BigDecimal finishedRent = bd.divide(add, 2, RoundingMode.HALF_UP);
                    BigDecimal result2 = finishedRent.multiply(bd2);
                    result2 = result2.setScale(2, RoundingMode.HALF_UP);
                    StampDutyDetail stampDutyDetail2 = BeanUtil.copyProperties(stampDutyDetail, StampDutyDetail.class);
                    stampDutyDetail2.setRent(finishedRent.longValue());
                    stampDutyDetail2.setCommission(0L);
                    stampDutyDetail2.setConsultingFee(0L);
                    stampDutyDetail2.setAmount(finishedRent.longValue());
                    stampDutyDetail2.setName(StampDutyBizTypeEnum.MMHT.name());
                    stampDutyDetail2.setTaxRate(taxRate2);
                    stampDutyDetail2.setStampDuty(result2.toString());
                    rzzlhtRes.add(stampDutyDetail2);
                }
            }
        }
        return rzzlhtRes;
    }

    /**
     * 手续费
     * @return
     */
    private BigDecimal getCommissionTaxRate(String leaseType,Long taxRate,Long receiptId){
        if(ObjectUtil.equals(ContractBusinessModelEnum.zhi_zu.name(),leaseType)){
            return kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_ZZ);
        } else if(ObjectUtil.equals(ContractBusinessModelEnum.hui_zu.name(),leaseType)){
            return kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
        } else if(ObjectUtil.equals(ContractBusinessModelEnum.jyx_zu.name(),leaseType)){
            Long resTaxRate = taxRate;
            if(Objects.isNull(taxRate)){
                ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
                if (Objects.nonNull(contractReceipt) && Objects.nonNull(contractReceipt.getTaxRate())) {
                    resTaxRate = contractReceipt.getTaxRate();
                } else {
                    resTaxRate = Util.toMithrasUnit(GlobalConstants.TAX_RATE_FEI_ZHI_ZU.multiply(BigDecimal.valueOf(100)));
                }
            }
            return BigDecimal.valueOf(resTaxRate).divide(new BigDecimal("1000000"));
        } else {
            throw new MithrasException("手续费税率获取失败！");
        }
    }

    private BigDecimal getConsultingFeeTaxRate(String leaseType){
        return kpiParameterConfigService.getTaxRate(TaxRateEnum.ZXS_ZL_JYX);
    }

    private List<ContractRentActual> getContractRentActuals(Long contractReceiptId) {
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getReceiptId, contractReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return contractRentActualService.list(query);
    }

    public List<StampDutyDetail> getJJRZHT(String id,LocalDate dateTime) {
        //获取业务类型=“流动资金贷款”、“项目贷款”、“银团”、“保理融资”  &融资状态=“起息”&起息审批流程审批通过时间为t-1的合同。（间接融资）
        List<StampDutyDetail> jjrzhtRes = new ArrayList<>();
        List<StampDutyBO> stampDutyBOList = stampDutyMapper.queryPreApprovedProcess(dateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)),
                Collections.singletonList(ProcessModelTypeEnum.IndirectFinancingCarryInterestFlow.name()));
        if(CollUtil.isEmpty(stampDutyBOList)){
            return jjrzhtRes;
        }
        List<String> financingIdList = stampDutyBOList.stream().map(StampDutyBO::getBusinessKey).collect(Collectors.toList());
        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
            query.in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name());
            query.in(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name(), FundFinancingBizTypeEnum.SYNDICATIONS.name(), FundFinancingBizTypeEnum.FACTORING_FINANCING.name());
            query.in(FundFinancingBaseInfo::getId,financingIdList.stream().map(Long::valueOf).collect(Collectors.toList()));
        if (StringUtils.isNotEmpty(id)) {
            if (!financingIdList.contains(id)) {
                return jjrzhtRes;
            }
            query.eq(FundFinancingBaseInfo::getId, id);
        }
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.list(query);
        if(CollUtil.isEmpty(fundFinancingBaseInfos)){
            return jjrzhtRes;
        }
        Set<Long> deptIds = new HashSet<>();
        Set<Long> financingIds = new HashSet<>();
        //Set<Long> createUserIds = new HashSet<>();
        fundFinancingBaseInfos.stream().forEach(baseInfo -> {
            deptIds.add(Long.valueOf(baseInfo.getDeptId()));
            financingIds.add(baseInfo.getId());
        });
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        // 批量查询融资机构
        Map<Long, List<FundFinancingCreditRef>> orgMap = financingCreditRefService.queryBatchByFinancingId(financingIds);
        Set<Long> orgIds = orgMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
        for(FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfos){
            LocalDate actualLoanDate = fundFinancingBaseInfo.getActualLoanDate();
            //判断前一天是否为贷款日
            if(ObjectUtil.isEmpty(actualLoanDate)){
                continue;
            }
            Long financingId = fundFinancingBaseInfo.getId();
            //是否已生成
            List<StampDutyDetail> stampDutyDetails = stampDutyMapper.selectList(Wrappers.<StampDutyDetail>lambdaQuery()
                    .eq(StampDutyDetail::getBelongId, financingId)
                    .eq(StampDutyDetail::getBelongCode, fundFinancingBaseInfo.getFinancingCode())
                    .eq(StampDutyDetail::getName, StampDutyBizTypeEnum.JKHT.name())
                    .eq(StampDutyDetail::getIsDelete, "0"));
            if (CollUtil.isNotEmpty(stampDutyDetails)) {
                continue;
            }

            log.info("进入方法间融[financingId:{}]", financingId);
            Long deptId = fundFinancingBaseInfo.getDeptId();
            StampDutyDetail stampDutyDetail = new StampDutyDetail();
            stampDutyDetail.setBelongId(financingId);
            stampDutyDetail.setName(StampDutyBizTypeEnum.JKHT.name());//借款合同
            stampDutyDetail.setBelongOrgId(deptId);
            stampDutyDetail.setBelongOrgName(deptMap.get(deptId));
            if(ObjectUtil.isNotEmpty(orgMap.get(financingId))){
                Long organizationId = orgMap.get(financingId).get(0).getOrganizationId();
                stampDutyDetail.setClientId(organizationId);
                stampDutyDetail.setClientName(orgIdNameMap.get(organizationId));
            }
            stampDutyDetail.setBelongCode(fundFinancingBaseInfo.getFinancingCode());
            stampDutyDetail.setReceiptId(null);
            stampDutyDetail.setReceiptCode(null);
            stampDutyDetail.setStartDate(actualLoanDate);
//            Long amount = fundFinancingBaseInfo.getFinancingAmount();
            Long amount = getActualVerifyAmount(fundFinancingBaseInfo.getFinancingCode());
            stampDutyDetail.setRent(amount);
            stampDutyDetail.setCommission(0L);//不含税手续费
            stampDutyDetail.setConsultingFee(0L);
            stampDutyDetail.setAmount(amount);
            String taxRate = "0.005";
            stampDutyDetail.setTaxRate(taxRate);
            BigDecimal bd = new BigDecimal(amount);
            BigDecimal bd1 = new BigDecimal(taxRate).divide(new BigDecimal("100"));
            BigDecimal result1 = bd.multiply(bd1);
            result1 = result1.setScale(2, RoundingMode.HALF_UP);
            stampDutyDetail.setStampDuty(result1.toString());
            stampDutyDetail.setScoure("System");
            stampDutyDetail.setIsDelete("0");
            jjrzhtRes.add(stampDutyDetail);
        }
        return jjrzhtRes;
    }

    /*获取间接融资 收款已核销金额*/
    private Long getActualVerifyAmount(String cashFlowCode){
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowDetail::getCashFlowCode, cashFlowCode + "-000");
        query.eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.FINANCE_FUND.name());
        List<FundReceiptFlowDetail> actualDetailList = fundReceiptFlowDetailService.list(query);
        if (CollectionUtil.isEmpty(actualDetailList)) {
            return 0L;
        }else {
            return actualDetailList.stream().filter(item -> Objects.nonNull(item.getTotalAmount())).mapToLong(FundReceiptFlowDetail::getTotalAmount).sum();
        }
    }

    public StampDutyListRSP listPage(StampDutyListREQ req) {
        StampDutyListRSP listrsp = new StampDutyListRSP();
        Page<StampDutyDetail> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<StampDutyDetail> query = this.buildQuery(req);
        Page<StampDutyDetail> dbResult = this.page(pageQuery, query);
        //Page<StampDutyDetail> dbResultAll = this.page(new Page<>(1, Integer.MAX_VALUE), query);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            listrsp.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return listrsp;
        }
        List<StampDutyDetail> dbList = dbResult.getRecords();
        //List<StampDutyDetail> dbListAll = dbResultAll.getRecords();

        List<StampDutyListRSP.StampDutyList> rspList = dbList.stream().map(item -> {
            StampDutyListRSP.StampDutyList stampDutyRSP = new StampDutyListRSP.StampDutyList();
            stampDutyRSP.setId(item.getId());
            stampDutyRSP.setClientId(item.getClientId());//客户id、机构id
            stampDutyRSP.setBelongId(item.getBelongId());//关联id（合同id、融资id）
            stampDutyRSP.setName(item.getName());//申报税目名称
            stampDutyRSP.setBelongOrgId(item.getBelongOrgId());//业务部门id
            stampDutyRSP.setBelongOrgName(item.getBelongOrgName());//业务部门名称
            stampDutyRSP.setClientName(item.getClientName());//客户名称/融资机构
            stampDutyRSP.setBelongCode(item.getBelongCode());//合同编号/融资编号
            stampDutyRSP.setReceiptId(item.getReceiptId());//借据id
            stampDutyRSP.setReceiptCode(item.getReceiptCode());//借据编号
            stampDutyRSP.setStartDate(item.getStartDate());//实际起租日
            stampDutyRSP.setRent(item.getRent());//不含税租金
            stampDutyRSP.setCommission(item.getCommission());//不含税手续费
            stampDutyRSP.setConsultingFee(item.getConsultingFee());//不含税咨询费
            stampDutyRSP.setAmount(item.getAmount());//金额
            stampDutyRSP.setTaxRate(item.getTaxRate());//印花税率
            stampDutyRSP.setStampDuty(item.getStampDuty());//印花税
            stampDutyRSP.setScoure(item.getScoure());//来源类型
            stampDutyRSP.setIsDelete(item.getIsDelete());//是否删除(1是、0否)
            stampDutyRSP.setCreateBy(item.getCreateBy());
            stampDutyRSP.setCreateTime(item.getCreateTime());
            stampDutyRSP.setUpdateBy(item.getUpdateBy());
            stampDutyRSP.setUpdateTime(item.getUpdateTime());
            stampDutyRSP.setRemark(item.getRemark());
            return stampDutyRSP;
        }).collect(Collectors.toList());
        listrsp.setRecords(PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize()));
        return listrsp;
    }

    private LambdaQueryWrapper<StampDutyDetail> buildQuery(StampDutyListREQ req) {
        LambdaQueryWrapper<StampDutyDetail> query = new LambdaQueryWrapper<>();
        query.eq(StampDutyDetail::getIsDelete, "0");
        query.eq(Objects.nonNull(req.getName()), StampDutyDetail::getName, req.getName());
        if(Objects.nonNull(req.getBelongOrgName())){
            OrgDO orgDo= orgDOMapper.queryByPrimaryKey(Long.valueOf(req.getBelongOrgName()));
            if(Objects.nonNull(orgDo)&&Objects.nonNull(orgDo.getName())){
                query.eq(StampDutyDetail::getBelongOrgName, orgDo.getName());
            }
        }
        if(Objects.nonNull(req.getClientName())){
            String clientName = id2NameService.clientId2NameSingle(Long.valueOf(req.getClientName()));
            if(Objects.nonNull(clientName)){
                query.eq(StampDutyDetail::getClientName, clientName);
            }
        }
        // 批量查询融资机构
        if(Objects.nonNull(req.getOrganizationName())){
            FundOrganization org = organizationService.getBaseMapper().selectById(req.getOrganizationName());
            if(Objects.nonNull(org)&&Objects.nonNull(org.getOrganizationName())){
                query.eq(StampDutyDetail::getClientName, org.getOrganizationName());
            }
        }
        query.like(StrUtil.isNotBlank(req.getContractCode()), StampDutyDetail::getBelongCode, req.getContractCode());
        query.like(StrUtil.isNotBlank(req.getFinancingCode()), StampDutyDetail::getBelongCode, req.getFinancingCode());
        query.like(StrUtil.isNotBlank(req.getReceiptCode()), StampDutyDetail::getReceiptCode, req.getReceiptCode());
        if (StrUtil.isNotBlank(req.getStartDateFrom())) {
            query.ge(StampDutyDetail::getStartDate, LocalDateTimeUtil.parseDate(req.getStartDateFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getStartDateTo())) {
            query.le(StampDutyDetail::getStartDate, LocalDateTimeUtil.parseDate(req.getStartDateTo(), DatePattern.NORM_DATE_PATTERN));
        }
        if(CollUtil.isNotEmpty(req.getIds())){
            query.in(StampDutyDetail::getId,req.getIds());
        }
        query.orderByDesc(StampDutyDetail::getStartDate);
        return query;
    }


    @Transactional(rollbackFor = Throwable.class)
    public StampDutyDetailREQ addStamp(StampDutyDetailREQ req) {
        StampDutyDetail stampDutyDetail = copyProperties(req, StampDutyDetail.class);
        stampDutyDetail.setScoure("Add");
        stampDutyDetail.setIsDelete("0");
        this.save(stampDutyDetail);
        StampDutyDetailREQ addREQ = new StampDutyDetailREQ();
        addREQ.setId(stampDutyDetail.getId());
        return addREQ;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream) {
        // 读取文件处理
//        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
//        // 总行数
//        int total = excelReader.getRowCount();
//        if (total > 15000) {
//            throw new MithrasException("最多支持导入15000条数据");
//        }
//        // 读取表头
//        List<Object> firstRow = excelReader.readRow(0);
//        if (CollectionUtil.isEmpty(firstRow)) {
//            throw new MithrasException("没有从文件中获取到表头，请检查导入文件");
//        }
        // 解析Excel
        List<StampDutyExcelModel> excelModelList = stampDutyExcelImporter.parse(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中解析出需要导入的数据"));
        List<StampDutyDetail> res = new ArrayList<>();
        //必输要求校验/格式要求校验
        Set<String> errorMsgs = new HashSet<>();
        //查询前线业务部门与中后台部门
        Example example = new Example(OrgDO.class);
        //去除浙江浙商融资租赁有限公司、领导层、风险管理委员会、项目评审委员会、管理员、董事会、定价委员会、浙商金控、公共利润中心
        example.createCriteria().andNotIn("code", Arrays.asList("ZSZL", "LDC", "FXGLWYH", "XMPSWYH", "ADMIN", "DSH", "DJWYH", "ZSJK", "GGLRZX"));
        List<OrgDO> orgList = orgDOMapper.selectByExample(example);
        Map<String, OrgDO> orgMap = orgList.stream().filter(e -> Objects.nonNull(e.getName())).collect(Collectors.toMap(e -> e.getName(), e -> e, (a, b) -> b));
        //BigDecimal
        for(StampDutyExcelModel excelModel : excelModelList){
            StampDutyDetail stampDutyDetail = new StampDutyDetail();
            stampDutyDetail.setIsDelete("0");
            stampDutyDetail.setScoure("import");
            //申报税目名称
            String modelName = excelModel.getName();
            String name = "";
            //是否印花税类型
            boolean isStampDutyType = false;
            if(StringUtils.isBlank(modelName)){
                errorMsgs.add("申报税目名称");
            }else{
                for (StampDutyBizTypeEnum value : StampDutyBizTypeEnum.values()) {
                    if (value.display().equals(modelName.trim())) {
                        isStampDutyType = true;
                        name = value.name();
                    }
                }
            }
            if(!isStampDutyType){
                errorMsgs.add("申报税目名称");
            }else{
                stampDutyDetail.setName(name);
            }
            //合同编号/融资编号
            String modelBelongCode = excelModel.getBelongCode();
            if(StringUtils.isBlank(modelBelongCode)){
                errorMsgs.add("合同编号/融资编号");
            }else{
                stampDutyDetail.setBelongCode(modelBelongCode.trim());
            }
            //客户名称/融资机构
            String modelClientName = excelModel.getClientName();
            if(StringUtils.isBlank(modelClientName)){
                errorMsgs.add("客户名称/融资机构");
            }else{
                stampDutyDetail.setClientName(modelClientName.trim());
            }
            //业务部门
            String modelBelongOrgName = excelModel.getBelongOrgName();
            if(StringUtils.isBlank(modelBelongOrgName)){
                errorMsgs.add("业务部门");
            }else{
                //是否当前组织架构内业务部门
                Long belongOrgId = null;
                if(Objects.isNull(orgMap.get(modelBelongOrgName.trim()))){
                    errorMsgs.add("业务部门");
                }else{
                    belongOrgId = orgMap.get(modelBelongOrgName.trim()).getId();
                    stampDutyDetail.setBelongOrgId(belongOrgId);
                    stampDutyDetail.setBelongOrgName(modelBelongOrgName);
                }
            }
            //借据编号
            //“申报税目名称”选择为“融资租赁合同/买卖合同/租赁合同”，且“合同编号/融资编号”选择完成时，
            // 该字段格式为下拉单选框，枚举值为该合同项下所有借据编号，必输
            String modelReceiptCode = excelModel.getReceiptCode();
            if(StringUtils.isBlank(modelReceiptCode)&&StrUtil.equalsAny(name, StampDutyBizTypeEnum.RZZLHT.name(), StampDutyBizTypeEnum.MMHT.name(),  StampDutyBizTypeEnum.ZLHT.name())){
                errorMsgs.add("借据编号");
            }else if(StringUtils.isNotBlank(modelReceiptCode)){
                stampDutyDetail.setReceiptCode(modelReceiptCode.trim());
            }
            //实际起租日yyyy-mm-dd
            LocalDate modelStartDate = excelModel.getStartDate();
            if(Objects.isNull(modelStartDate)){
                errorMsgs.add("实际起租日");
            }else{
                stampDutyDetail.setStartDate(modelStartDate);
            }
            //不含税租金
            BigDecimal modelRent = excelModel.getRent();
            if(ObjectUtil.isEmpty(modelRent)){
                errorMsgs.add("不含税租金");
            }else{
                //*10000方便后续统一处理
                stampDutyDetail.setRent(modelRent.multiply(new BigDecimal("10000")).longValue());
            }
            //不含税手续费
            BigDecimal modelCommission = excelModel.getCommission();
            if(ObjectUtil.isNotEmpty(modelCommission)){
                //*10000方便后续统一处理
                stampDutyDetail.setCommission(modelCommission.multiply(new BigDecimal("10000")).longValue());
            }
            //不含税咨询费
            BigDecimal modelConsultingFee = excelModel.getConsultingFee();
            if(ObjectUtil.isNotEmpty(modelConsultingFee)){
                //*10000方便后续统一处理
                stampDutyDetail.setConsultingFee(modelConsultingFee.multiply(new BigDecimal("10000")).longValue());
            }
            //金额
            BigDecimal modelAmount = excelModel.getAmount();
            if(ObjectUtil.isEmpty(modelAmount)){
                errorMsgs.add("金额");
            }else{
                //*10000方便后续统一处理
                stampDutyDetail.setAmount(modelAmount.multiply(new BigDecimal("10000")).longValue());
            }
            //印花税率（%）
            String modelTaxRate = excelModel.getTaxRate();
            if(StringUtils.isBlank(modelTaxRate)){
                errorMsgs.add("印花税率（%）");
            }else{
                BigDecimal taxRate = canConvertToBigDecimal(modelTaxRate);
                if(Objects.isNull(taxRate)){
                    errorMsgs.add("印花税率（%）");
                }else{
                    stampDutyDetail.setTaxRate(modelTaxRate.trim());
                }
            }
            //印花税
            BigDecimal modelStampDuty = excelModel.getStampDuty();
            if(ObjectUtil.isEmpty(modelStampDuty)){
                errorMsgs.add("印花税");
            }else{
                //*10000方便后续统一处理
                stampDutyDetail.setStampDuty(modelStampDuty.multiply(new BigDecimal("10000")).toString());
            }
            res.add(stampDutyDetail);
        }
        if(CollUtil.isNotEmpty(errorMsgs)){
            List<String> list = new ArrayList<>(errorMsgs);
            String msg = "";
            for(int i = 0; i < list.size(); i++){
                if(i == 0){
                    msg = msg + list.get(i);
                }else{
                    msg = msg + "、" + list.get(i);
                }
            }
            //导入失败，【对应列名称（若存在多个，则“、”拼接）】为必输信息，请录入！
            throw new MithrasException("导入失败，【"+msg+"】的信息为必输信息/录入不规范，请修改！");
        }
        if(!res.isEmpty()){
            this.saveBatch(res);
        }
    }

    /**
     * 判断字符串是否可以转换为BigDecimal
     * @param str 待检测的字符串
     * @return 如果可以转换返回BigDecimal，否则返回null
     */
    public static BigDecimal canConvertToBigDecimal(String str) {
        // 检查空字符串或null
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            // 去除首尾空格后尝试转换
            BigDecimal bd = new BigDecimal(str.trim());
            return bd;
        } catch (NumberFormatException e) {
            // 如果转换失败，说明不能转换为BigDecimal
            return null;
        }
    }

    /**
     * 判断字符串是否可以转换为yyyy-MM-dd格式的日期
     * @param str 待检测的字符串
     * @return 如果可以转换返回日期，否则返回null
     */
    public static LocalDate canConvertToDate(String str) {
        // 检查空字符串或null
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            // 使用指定格式解析日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate localDate = LocalDate.parse(str.trim(), formatter);
            return localDate;
        } catch (DateTimeParseException e) {
            // 如果解析失败，说明不能转换为指定格式的日期
            return null;
        }
    }


    public StampDutyListRSP listContract(StampDutyContractREQ req) {
        StampDutyListRSP listrsp = new StampDutyListRSP();
        String code = req.getCode();
        String belongId = req.getBelongId();
        String type = req.getType();
        if(("ht".equals(type) || "mmht".equals(type))&&(StringUtils.isEmpty(req.getBelongId()))){
            //获取业务类型=“租赁”&合同状态=“起租/结清”的所有合同
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.START_RENT.name(),ContractStatus.SETTLE.name()));
            query.eq(ContractBaseInfo::getBizType, ProjectBizType.ZL.name());
            if(StringUtils.isNotEmpty(code)){
                query.like(ContractBaseInfo::getContractCode, code);
            }
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(query);
            if(CollUtil.isEmpty(contractBaseInfos)){
                return listrsp;
            }
            List<StampDutyListRSP.ContractList> list = BeanUtil.copyToList(contractBaseInfos, StampDutyListRSP.ContractList.class);
            listrsp.setContracts(PageR.of(list, list.size(), req.getPage(), req.getPageSize()));
            return listrsp;
        }else if("rzht".equals(type)&&(StringUtils.isEmpty(req.getBelongId()))){
            //获取业务类型=“流动资金贷款”、“项目贷款”、“银团”、“保理融资”  &融资状态=“起息/结清”的所有融资合同。（间接融资）
            LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
            query.in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name());
            query.in(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name(), FundFinancingBizTypeEnum.SYNDICATIONS.name(), FundFinancingBizTypeEnum.FACTORING_FINANCING.name());
            if(StringUtils.isNotEmpty(code)){
                query.like(FundFinancingBaseInfo::getFinancingCode, code);
            }
            List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.list(query);
            if(CollUtil.isEmpty(fundFinancingBaseInfos)){
                return listrsp;
            }
            List<StampDutyListRSP.FundContractList> list = BeanUtil.copyToList(fundFinancingBaseInfos, StampDutyListRSP.FundContractList.class);
            listrsp.setFundContracts(PageR.of(list, list.size(), req.getPage(), req.getPageSize()));
            return listrsp;
        }
        List<StampDutyDetail> dbList = null;
        if("ht".equals(type) || "mmht".equals(type)){
            dbList = getRZZLHT2(belongId,type);
        } else if("rzht".equals(type)){
            dbList = getJJRZHT2(belongId);
        }else{
            return listrsp;
        }
        List<StampDutyListRSP.StampDutyList> rspList = dbList.stream().map(item -> {
            StampDutyListRSP.StampDutyList stampDutyRSP = new StampDutyListRSP.StampDutyList();
            stampDutyRSP.setClientId(item.getClientId());//客户id、机构id
            stampDutyRSP.setBelongId(item.getBelongId());//关联id（合同id、融资id）
            stampDutyRSP.setName(item.getName());//申报税目名称
            stampDutyRSP.setBelongOrgId(item.getBelongOrgId());//业务部门id
            stampDutyRSP.setBelongOrgName(item.getBelongOrgName());//业务部门名称
            stampDutyRSP.setClientName(item.getClientName());//客户名称/融资机构
            stampDutyRSP.setBelongCode(item.getBelongCode());//合同编号/融资编号
            stampDutyRSP.setReceiptId(item.getReceiptId());//借据id
            stampDutyRSP.setReceiptCode(item.getReceiptCode());//借据编号
            stampDutyRSP.setStartDate(item.getStartDate());//实际起租日
            stampDutyRSP.setRent(item.getRent());//不含税租金
            stampDutyRSP.setCommission(item.getCommission());//不含税手续费
            stampDutyRSP.setConsultingFee(item.getConsultingFee());//不含税咨询费
            stampDutyRSP.setAmount(item.getAmount());//金额
            stampDutyRSP.setTaxRate(item.getTaxRate());//印花税率
            stampDutyRSP.setStampDuty(item.getStampDuty());//印花税
            return stampDutyRSP;
        }).collect(Collectors.toList());
        listrsp.setRecords(PageR.of(rspList, rspList.size(), req.getPage(), req.getPageSize()));
        return listrsp;
    }


    public List<StampDutyDetail> getRZZLHT2(String id,String type) {
        //获取业务类型=“租赁”&合同状态=“起租/结清”的所有合同
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.START_RENT.name(),ContractStatus.SETTLE.name()));
        query.eq(ContractBaseInfo::getBizType, ProjectBizType.ZL.name());
        if(StringUtils.isNotEmpty(id)){
            query.eq(ContractBaseInfo::getId, id);
        }
        List<StampDutyDetail> rzzlhtRes = new ArrayList<>();
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(query);
        if(CollUtil.isEmpty(contractBaseInfos)){
            return rzzlhtRes;
        }
        Set<Long> deptIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        contractBaseInfos.stream().forEach(contract -> {
            deptIds.add(Long.valueOf(contract.getBizDeptId()));
            clientIds.add(Long.valueOf(contract.getClientId()));
        });
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);

        LambdaQueryWrapper<CollectionBaseInfo> collectionBaseInfoLambdaQuery = Wrappers.lambdaQuery();
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getContractId, contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.OTHERAMOUNT.name(),CashFlowItemEnum.COMMISSION.name()));
        collectionBaseInfoLambdaQuery.in(CollectionBaseInfo::getWriteOffStatus,  ListUtil.toList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(collectionBaseInfoLambdaQuery);


        for(ContractBaseInfo contractBaseInfo : contractBaseInfos){
            Long contractId = contractBaseInfo.getId();
            log.info("进入方法合同[contractId:{}]", contractId);
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(contractId);
            if (Objects.isNull(contractBaseInfoLib)) {
                log.info("没有找到最新的合同生效版本数据[contractId:{}]", contractId);
                continue;
            }
            Long bizdeptId = contractBaseInfo.getBizDeptId();
            Long clientId = contractBaseInfo.getClientId();
            StampDutyDetail stampDutyDetail = new StampDutyDetail();
            stampDutyDetail.setBelongId(contractId);
            stampDutyDetail.setBelongOrgId(bizdeptId);
            stampDutyDetail.setBelongOrgName(deptMap.get(bizdeptId));
            stampDutyDetail.setClientId(clientId);
            stampDutyDetail.setClientName(clientMap.get(clientId));
            stampDutyDetail.setBelongCode(contractBaseInfo.getContractCode());
            if(StringUtils.isNotEmpty(id)){
                //租赁合同
                List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibService.listByContractIdVersion(contractId, contractBaseInfoLib.getVersion());
                if (CollectionUtil.isEmpty(contractReceiptLibList)) {
                    log.info("没有找到最新的借据生效版本数据[contractId:{}]", contractId);
                    continue;
                }
                for(ContractReceiptLib contractReceiptLib : contractReceiptLibList){
                    StampDutyDetail stampDutyDetail2 = BeanUtil.copyProperties(stampDutyDetail, StampDutyDetail.class);
                    LocalDate receiptStartDate = contractReceiptLib.getReceiptStartDate();
                    Long receiptId= contractReceiptLib.getOriginId();
                    stampDutyDetail2.setReceiptId(receiptId);
                    stampDutyDetail2.setReceiptCode(contractReceiptLib.getReceiptCode());
                    stampDutyDetail2.setStartDate(receiptStartDate);
                    //以借据为维度
                    List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getReceiptId, receiptId));
                    List<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());

                    if("ht".equals(type)){
                        List<CollectionBaseInfo> collectBaseInfoList = collectionBaseInfoList.stream().filter(e -> (CollUtil.isNotEmpty(paymentIds) && Objects.nonNull(e.getPaymentId())
                                && paymentIds.contains(e.getPaymentId()))
                                || (Objects.nonNull(e.getReceiptId()) && e.getReceiptId().equals(receiptId))).collect(Collectors.toList());
                        long consultingFee = collectBaseInfoList.stream().filter(e -> CashFlowItemEnum.OTHERAMOUNT.name().equals(e.getCashFlowItem())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                        long commission = collectBaseInfoList.stream().filter(e -> CashFlowItemEnum.COMMISSION.name().equals(e.getCashFlowItem())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();

                        try {
                            BigDecimal commissionTaxRate = getCommissionTaxRate(contractBaseInfoLib.getLeaseType(),contractReceiptLib.getTaxRate(),receiptId);
                            BigDecimal consultingFeeTaxRate = getConsultingFeeTaxRate(contractBaseInfoLib.getLeaseType());
                            consultingFee = BigDecimal.valueOf(consultingFee).divide(BigDecimal.ONE.add(consultingFeeTaxRate),2, RoundingMode.HALF_UP).longValue();
                            commission = BigDecimal.valueOf(commission).divide(BigDecimal.ONE.add(commissionTaxRate),2, RoundingMode.HALF_UP).longValue();
                        } catch (Exception e){
                            log.error("服务费/咨询费税率获取失败！");
                            continue;
                        }
                        //3.不含税租金： sum（租金表中的租金） + 首期利息 + 首期租金 - 税额
                        Long rentExcludingTax = contractReceiptLib.getRentExcludingTax();
                        if(Objects.isNull(rentExcludingTax)){
                            //查询实际租金列表数据
                            List<ContractRentActual> rentActualList = getContractRentActuals(receiptId);
                            if (CollectionUtils.isEmpty(rentActualList)) {
                                rentExcludingTax = 0L;
                            }else{
                                //2.税率：直租税率：13%    非直租税率：6%
                                BigDecimal taxRate = LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType()) ? GlobalConstants.TAX_RATE_ZHI_ZU : GlobalConstants.TAX_RATE_FEI_ZHI_ZU;

                                //租金总额
                                Long rentSum = rentActualList.stream().map(ContractRentActual::getRent).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
                                //租金表中将第0期剔除掉
                                rentActualList = rentActualList.stream().filter(rentActual -> rentActual.getCashFlowPhase() != 0).collect(Collectors.toList());

                                //本金总额
                                Long capitalSum = rentActualList.stream().map(ContractRentActual::getPrincipal).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
                                //利息总额
                                Long interestSum = rentActualList.stream().map(ContractRentActual::getInterest).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);

                                BigDecimal tax; // 税额
                                BigDecimal stampDuty; //印花税
                                Long firstRent = 0L; // 首期租金
                                Long firstInstallmentInterest = 0L; // 首期利息
                                List<Long> paymentBaseInfoIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                                List<CollectionBaseInfo> collectionBaseInfos = new ArrayList<>();
                                if (CollectionUtils.isNotEmpty(paymentBaseInfoIds)) {
                                    collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                            .in(CollectionBaseInfo::getPaymentId, paymentBaseInfoIds)
                                            .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()));
                                }
                                //合同状态为起租的合同状态取借据维度已核销的首期租金、首期利息实际付款金额
                                if (CollectionUtils.isNotEmpty(collectionBaseInfos)) {
                                    firstRent = collectionBaseInfos.stream()
                                            .filter(collectionBaseInfo -> CashFlowItemEnum.FIRST_RENT.name().equals(collectionBaseInfo.getCashFlowItem()))
                                            .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
                                    firstInstallmentInterest = collectionBaseInfos.stream()
                                            .filter(collectionBaseInfo -> CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && collectionBaseInfo.getPhase() == 0)
                                            .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
                                }
                                //1.不含税利息的计算公式：【sum（租金表中的利息）+ 首期利息】/（1+税率），保留2位小数。
                                BigDecimal excludingInterestTax = NumberUtil.div(interestSum + firstInstallmentInterest, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP);

                                //2.税额计算公式：
                                if (LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
                                    //直租：sum（租金表中的利息）+ 首期利息 - 不含税利息 + （sum（租金表中的 本金+ 首期租金）- sum（租金表中的 本金+ 首期租金）/（1+税率））
                                    tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax)
                                            .add(BigDecimal.valueOf(capitalSum + firstRent).subtract(NumberUtil.div(capitalSum + firstRent, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP)));
                                } else {
                                    //其他：sum（租金表中的利息）+ 首期利息 - 不含税利息
                                    tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax);
                                }
                                //不含税租金
                                rentExcludingTax = Util.mithrasLongDecimalTwo(NumberUtil.sub(BigDecimal.valueOf(rentSum + firstInstallmentInterest + firstRent), tax).longValue());
                            }
                        }

                        stampDutyDetail2.setConsultingFee(consultingFee);
                        stampDutyDetail2.setCommission(commission);
                        stampDutyDetail2.setRent(rentExcludingTax);
                        Long amount = rentExcludingTax + commission + consultingFee;
                        stampDutyDetail2.setAmount(amount);
                        BigDecimal bd = new BigDecimal(amount);
                        String taxRate = "0.005";
                        stampDutyDetail2.setTaxRate(taxRate);
                        BigDecimal bd1 = new BigDecimal(taxRate);
                        BigDecimal result1 = bd.multiply(bd1);
                        result1 = result1.setScale(2, RoundingMode.HALF_UP);
                        stampDutyDetail2.setStampDuty(result1.toString());
                    } else {
                        //买卖合同（仅直租）
                        if (ObjectUtil.equals(ContractBusinessModelEnum.zhi_zu.name(), contractBaseInfoLib.getLeaseType())) {
                            //买卖合同的不含税租金取借据的实际投放款
                            List<Long> paymentBaseInfoIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                            Long finishedSum = actualDetailService.calculatePaidAmount(paymentBaseInfoIds);
                            Long zhizuTaxRate = contractReceiptLib.getTaxRate();
                            if(Objects.isNull(zhizuTaxRate)){
                                ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
                                if (Objects.nonNull(contractReceipt) && Objects.nonNull(contractReceipt.getTaxRate())) {
                                    zhizuTaxRate = contractReceipt.getTaxRate();
                                } else {
                                    zhizuTaxRate = Util.toMithrasUnit(GlobalConstants.TAX_RATE_ZHI_ZU.multiply(BigDecimal.valueOf(100)));
                                }
                            }
                            BigDecimal add = BigDecimal.ONE.add(BigDecimal.valueOf(zhizuTaxRate).divide(BigDecimal.valueOf(1000000)));
                            String taxRate2 = "0.030";
                            BigDecimal bd = new BigDecimal(finishedSum);
                            BigDecimal bd2 = new BigDecimal(taxRate2).divide(BigDecimal.valueOf(100));
                            BigDecimal finishedRent = bd.divide(add, 2, RoundingMode.HALF_UP);
                            BigDecimal result2 = finishedRent.multiply(bd2);
                            result2 = result2.setScale(2, RoundingMode.HALF_UP);
                            stampDutyDetail2.setRent(finishedRent.longValue());
                            stampDutyDetail2.setCommission(0L);
                            stampDutyDetail2.setConsultingFee(0L);
                            stampDutyDetail2.setAmount(finishedRent.longValue());
                            stampDutyDetail2.setTaxRate(taxRate2);
                            stampDutyDetail2.setStampDuty(result2.toString());
                        }
                    }
                    rzzlhtRes.add(stampDutyDetail2);
                }
            }else{
                rzzlhtRes.add(stampDutyDetail);
            }
        }
        return rzzlhtRes;
    }

    public List<StampDutyDetail> getJJRZHT2(String belongId) {
        //获取业务类型=“流动资金贷款”、“项目贷款”、“银团”、“保理融资”  &融资状态=“起息/结清”的所有融资合同。（间接融资）
        List<StampDutyDetail> jjrzhtRes = new ArrayList<>();
        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name());
        query.in(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name(), FundFinancingBizTypeEnum.SYNDICATIONS.name(), FundFinancingBizTypeEnum.FACTORING_FINANCING.name());
        if(StringUtils.isNotEmpty(belongId)){
            query.eq(FundFinancingBaseInfo::getId, belongId);
        }
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.list(query);
        if(CollUtil.isEmpty(fundFinancingBaseInfos)){
            return jjrzhtRes;
        }
        Set<Long> deptIds = new HashSet<>();
        Set<Long> financingIds = new HashSet<>();
        //Set<Long> createUserIds = new HashSet<>();
        fundFinancingBaseInfos.stream().forEach(baseInfo -> {
            deptIds.add(Long.valueOf(baseInfo.getDeptId()));
            financingIds.add(baseInfo.getId());
        });
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        // 批量查询融资机构
        Map<Long, List<FundFinancingCreditRef>> orgMap = financingCreditRefService.queryBatchByFinancingId(financingIds);
        Set<Long> orgIds = orgMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
        for(FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfos){
            LocalDate actualLoanDate = fundFinancingBaseInfo.getActualLoanDate();
            Long financingId = fundFinancingBaseInfo.getId();
            log.info("进入方法间融[financingId:{}]", financingId);
            Long deptId = fundFinancingBaseInfo.getDeptId();
            StampDutyDetail stampDutyDetail = new StampDutyDetail();
            stampDutyDetail.setBelongId(financingId);
            stampDutyDetail.setBelongOrgId(deptId);
            stampDutyDetail.setBelongOrgName(deptMap.get(deptId));
            if(ObjectUtil.isNotEmpty(orgMap.get(financingId))){
                Long organizationId = orgMap.get(financingId).get(0).getOrganizationId();
                stampDutyDetail.setClientId(organizationId);
                stampDutyDetail.setClientName(orgIdNameMap.get(organizationId));
            }
            stampDutyDetail.setBelongCode(fundFinancingBaseInfo.getFinancingCode());
            stampDutyDetail.setReceiptId(null);
            stampDutyDetail.setReceiptCode(null);
            stampDutyDetail.setStartDate(actualLoanDate);
//            Long amount = fundFinancingBaseInfo.getFinancingAmount();
            Long amount = getActualVerifyAmount(fundFinancingBaseInfo.getFinancingCode());
            stampDutyDetail.setRent(amount);
            stampDutyDetail.setCommission(0L);//不含税手续费
            stampDutyDetail.setConsultingFee(0L);
            stampDutyDetail.setAmount(amount);
            String taxRate = "0.005";
            stampDutyDetail.setTaxRate(taxRate);
            BigDecimal bd = new BigDecimal(amount);
            BigDecimal bd1 = new BigDecimal(taxRate);
            BigDecimal result1 = bd.multiply(bd1);
            result1 = result1.setScale(2, RoundingMode.HALF_UP);
            stampDutyDetail.setStampDuty(result1.toString());
            jjrzhtRes.add(stampDutyDetail);
        }
        return jjrzhtRes;
    }

    public void exportExcel(ServletOutputStream outputStream, List<Long> ids) {
        StampDutyListREQ pageListREQ = new StampDutyListREQ();
        pageListREQ.setIds(ids);
        pageListREQ.setPage(1);
        pageListREQ.setPageSize(5000);
        List<StampDutyListRSP.StampDutyList> list = this.listPage(pageListREQ).getRecords().getList();
        if (CollUtil.isEmpty(list)) {
            throw new MithrasException("不存在数据，导出失败");
        }

        List<StampDutyExcelModel> excelModels = this.entityExcelModel(list);

        stampDutyExcelExporter.exportExcel(excelModels, outputStream);
    }

    public List<StampDutyExcelModel> entityExcelModel(List<StampDutyListRSP.StampDutyList> list) {
        if ( list == null ) {
            return null;
        }
        List<StampDutyExcelModel> list1 = new ArrayList<StampDutyExcelModel>( list.size() );
        for (StampDutyListRSP.StampDutyList detail : list ) {
            StampDutyExcelModel stampDutyExcelModel = new StampDutyExcelModel();
            stampDutyExcelModel.setName(StampDutyBizTypeEnum.valueOf(detail.getName()).display());
            stampDutyExcelModel.setBelongOrgName(detail.getBelongOrgName());
            stampDutyExcelModel.setClientName(detail.getClientName());
            stampDutyExcelModel.setBelongCode(detail.getBelongCode());
            stampDutyExcelModel.setReceiptCode(detail.getReceiptCode());
            stampDutyExcelModel.setStartDate(detail.getStartDate());
            stampDutyExcelModel.setRent(getMoneyFormat(detail.getRent()));
            stampDutyExcelModel.setCommission(getMoneyFormat(detail.getCommission()));
            stampDutyExcelModel.setConsultingFee(getMoneyFormat(detail.getConsultingFee()));
            stampDutyExcelModel.setAmount(getMoneyFormat(detail.getAmount()));
            stampDutyExcelModel.setTaxRate(detail.getTaxRate());
            stampDutyExcelModel.setStampDuty(getMoneyFormat(detail.getStampDuty()));

            list1.add(stampDutyExcelModel);
        }

        return list1;
    }


    private BigDecimal getMoneyFormat(String money){
        BigDecimal res = new BigDecimal(0L);
        BigDecimal bd = canConvertToBigDecimal(money);
        if(ObjectUtil.isNotEmpty(bd)){
            BigDecimal result = bd.divide(new BigDecimal("10000"));
            res = result.setScale(2, RoundingMode.HALF_UP);
        }
        return res;
    }

    private BigDecimal getMoneyFormat(Long money){
        BigDecimal res = new BigDecimal(0L);
        if(ObjectUtil.isNotEmpty(money)){
            BigDecimal result = new BigDecimal(money).divide(new BigDecimal("10000"));
            res = result.setScale(2, RoundingMode.HALF_UP);
        }
        return res;
    }

}
