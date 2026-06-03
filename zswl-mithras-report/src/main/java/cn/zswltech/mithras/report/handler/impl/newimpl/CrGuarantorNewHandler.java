package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.biz.*;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrGuarantorDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-保证表
 *
 * @author wangchuanhao
 * @date 2022/10/8 6:42 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrGuarantorNewHandler extends CrAbstractHandler<CrGuarantorDraft, CrGuarantor> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.GUARANTOR_NEW;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<ContractBaseInfoLib> contractBaseInfoLibList = commonInfoService.getContractBaseInfoLibs(dealTime, lastDealTime);
        List<CrGuarantorDraft> reportDataList = new LinkedList<>();
        reportDataList.addAll(collectContractChangeData(contractBaseInfoLibList, dealTime));
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrGuarantorDraft::getBusinessKey)).collect(Collectors.toList());

        if (CollUtil.isEmpty(reportDataList)) {
            return;
        }

        fillClientData(reportDataList);
        Set<String> businessKeySet = reportDataList.stream().map(CrGuarantorDraft::getBusinessKey).collect(Collectors.toSet());

        // 该次需上报的businessKey对应的列表 用于判断新增还是编辑
        List<CrGuarantorDraft> existDataList = reportDataList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrGuarantorDraft>lambdaQuery()
                .in(CrGuarantorDraft::getBusinessKey, reportDataList.stream().map(CrGuarantorDraft::getBusinessKey).collect(Collectors.toList()))
        );
        Map<String, CrGuarantorDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrGuarantorDraft::getBusinessKey, Function.identity()));

        // 合同已存在的数据 用于判断发生变动需要删除的数据
        Map<Long, Map<Long, Set<String>>> pbkMap = new HashMap<>();
        for (CrGuarantorDraft crGuarantorDraft : reportDataList) {
            Map<Long, Set<String>> paymentMap = pbkMap.computeIfAbsent(crGuarantorDraft.getContractId(), k -> new HashMap<>());
            Set<String> bkSet = paymentMap.computeIfAbsent(crGuarantorDraft.getPaymentId(), k -> new HashSet<>());
            bkSet.add(crGuarantorDraft.getBusinessKey());
        }
        List<CrGuarantorDraft> existContractDataList = contractBaseInfoLibList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrGuarantorDraft>lambdaQuery()
                .in(CrGuarantorDraft::getContractId, contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList()))
        );
        Set<String> needDeleteKeyList = existContractDataList.stream().filter(k -> {
            if (!pbkMap.containsKey(k.getContractId()) || !pbkMap.get(k.getContractId()).containsKey(k.getPaymentId())) {
                // 如果不含合同id或不含付款id则不处理
                return false;
            }
            // 该合同下该付款曾经有数据 但此次报送没有 需删除
            return !pbkMap.get(k.getContractId()).get(k.getPaymentId()).contains(k.getBusinessKey());
        }).map(CrGuarantorDraft::getBusinessKey).collect(Collectors.toSet());

        log.info("非直租征信报送-保证表，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
        if (CollectionUtils.isNotEmpty(needDeleteKeyList)) {
            draftMapper.delete(Wrappers.<CrGuarantorDraft>lambdaQuery().in(CrGuarantorDraft::getBusinessKey, needDeleteKeyList));
            formalMapper.delete(Wrappers.<CrGuarantor>lambdaQuery().in(CrGuarantor::getBusinessKey, needDeleteKeyList));
        }
        List<CrGuarantorDraft> needInsertList = new ArrayList<>();
        for (CrGuarantorDraft reportData : reportDataList) {
            if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                needInsertList.add(reportData);
            } else {
                CrGuarantorDraft existData = existReportDataMap.get(reportData.getBusinessKey());
                if (ReportCompareUtil.checkChange(reportData, existData, existData.ignoreCompareFieldNames())) {
                    reportData.setId(existData.getId());
                    needInsertList.add(reportData);
                }
            }
        }
        if (CollUtil.isNotEmpty(needInsertList)) {
            draftService.saveOrUpdateBatch(needInsertList);
        }
    }

    public List<CrGuarantorDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList, LocalDateTime dealTime) {
        List<CrGuarantorDraft> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        log.info("非直租：{}变更合同ID列表：{}", dealTime.toLocalDate(), contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList()));
        contractBaseInfoLibList.forEach(contractBaseInfoLib -> {
            boolean contractReportFlag = reportDataRepository.contractReport(contractBaseInfoLib);
            if (!contractReportFlag) {
                // 合同报送过滤逻辑
                return;
            }
            //如果需要报送，才查询报价方案
            ContractLeasePriceLib leasePriceLib = getContractLeasePriceLib(contractBaseInfoLib.getOriginId());
            ContractFactoringPriceLib factoringPriceLib = getContractFactoringPriceLib(contractBaseInfoLib.getOriginId());

            List<PaymentBaseInfo> paymentBaseInfoList = commonInfoService.getPaymentBaseInfos(contractBaseInfoLib);
            if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
                return;
            }

            List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                    .eq(ContractGuarantorLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractGuarantorLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractGuarantorLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));
            if (CollUtil.isNotEmpty(contractGuarantorLibList)) {
                //这里是为了防止脏数据做的健壮性增强
                contractGuarantorLibList = contractGuarantorLibList.stream().filter(a -> Objects.nonNull(a.getJointGuaranteeMark())).collect(Collectors.toList());
            }
            List<ContractTenantryLib> contractTenantryLibList = commonInfoService.getContractTenantryLibs(contractBaseInfoLib);

            if (CollUtil.isNotEmpty(paymentBaseInfoList)) {
                Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(paymentBaseInfoList);
                for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                    //担保人
                    contractGuarantorLibList.forEach(guarantor -> {
                        Object priceLib = Objects.isNull(leasePriceLib) ? factoringPriceLib : leasePriceLib;
                        resultList.addAll(buildCrGuarantorByGuarantor(paymentIdAccountListMap, contractBaseInfoLib, paymentBaseInfo, guarantor, priceLib));
                    });
                    //共同借款人（联合承租人 / 除账户外的债权人）
                    contractTenantryLibList.forEach(tenantry -> {
                        List<CrGuarantorDraft> crGuarantorDrafts = buildCrGuarantorByTenantry(paymentIdAccountListMap, contractBaseInfoLib, paymentBaseInfo, tenantry);
                        resultList.addAll(crGuarantorDrafts);
                    });
                }
            }
        });
        return resultList;
    }

    public List<CrGuarantorDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrGuarantorDraft> resultList = new ArrayList<>();
        // 查找付款模块有变动的数据
        List<PaymentBaseInfo> paymentBaseInfoList = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTableNew(dealTime, lastDealTime);
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return resultList;
        }
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, Function.identity(), (a, b) -> a));
        Map<Long, Map<LocalDate, List<PaymentActualDetail>>> groupMap = new LinkedHashMap<>();
        Map<Long, List<PaymentActualDetail>> dateListMap = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()))
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
        ).stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));

        if (CollUtil.isNotEmpty(dateListMap)) {
            dateListMap.forEach((k, v) -> groupMap.put(k, v.stream().collect(Collectors.groupingBy(PaymentActualDetail::getOperationDate))));
        }

        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(paymentBaseInfoList);
        groupMap.forEach((paymentId, map) -> {
            map.forEach((operationDate, detailList) -> {
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMap.get(paymentId);
                ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                        .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                        .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(ContractBaseInfoLib::getVersion)
                        .last(StringUtil.mysqlLimitOne())
                );
                if (Objects.isNull(contractBaseInfoLib)) {
                    // 兼容历史错误数据 实际上有付款申请，就会有合同版本
                    return;
                }
                if (ProjectBizType.ZR.name().equals(contractBaseInfoLib.getBizType())) {
                    // 债权转让不报
                    return;
                }
                List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                        .eq(ContractGuarantorLib::getContractId, paymentBaseInfo.getContractId())
                        .eq(ContractGuarantorLib::getVersion, contractBaseInfoLib.getVersion())
                        .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractGuarantorLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));

                if (CollUtil.isEmpty(contractGuarantorLibList)) {
                    return;
                }
                //有数据才去查询报价方案
                ContractLeasePriceLib leasePriceLib = getContractLeasePriceLib(paymentBaseInfo.getContractId());
                ContractFactoringPriceLib factoringPriceLib = getContractFactoringPriceLib(paymentBaseInfo.getContractId());
                //担保人
                contractGuarantorLibList.forEach(c -> {
                    Object priceLib = Objects.isNull(leasePriceLib) ? factoringPriceLib : leasePriceLib;
                    resultList.addAll(buildCrGuarantorByGuarantor(paymentIdAccountListMap, contractBaseInfoLib, paymentBaseInfo, c, priceLib));
                });
                //共同借款人（联合承租人 / 除账户外的债权人）
                List<ContractTenantryLib> contractTenantryLibList = commonInfoService.getContractTenantryLibs(contractBaseInfoLib);
                contractTenantryLibList.forEach(c -> resultList.addAll(buildCrGuarantorByTenantry(paymentIdAccountListMap, contractBaseInfoLib, paymentBaseInfo, c)));
            });
        });
        return resultList;
    }

    private List<CrGuarantorDraft> buildCrGuarantorByTenantry(Map<Long, List<CrAccountDraft>> paymentIdAccountListMap, ContractBaseInfoLib contractBaseInfoLib,
                                                              PaymentBaseInfo paymentBaseInfo, ContractTenantryLib contractTenantryLib) {
        //之前的数据按借据展开，但是现在借据被拆散了，所以这里需要根据账户再次拆分
        List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
        if (CollUtil.isEmpty(accountDrafts)) {
            return Collections.emptyList();
        }
        List<CrGuarantorDraft> result = new LinkedList<>();
        for (CrAccountDraft accountDraft : accountDrafts) {
            CrGuarantorDraft crGuarantor = new CrGuarantorDraft();
            crGuarantor.setReportState(ReportState.TO_BE_REPORT.name());
            crGuarantor.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
            crGuarantor.setClientId(contractTenantryLib.getLesseeId());
            crGuarantor.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
            crGuarantor.setPaymentId(paymentBaseInfo.getId());
            crGuarantor.setClientClass(GuarantorClientClassEnum.JOINT_LESSEE.getValue());
            crGuarantor.setContractId(contractBaseInfoLib.getOriginId());
            crGuarantor.setBusinessKey(crGuarantor.genBusinessKey(GuaranteeTypeEnum.TENANTRY.getValue()));
            result.add(crGuarantor);
        }
        return result;
    }

    private List<CrGuarantorDraft> buildCrGuarantorByGuarantor(Map<Long, List<CrAccountDraft>> paymentIdAccountListMap, ContractBaseInfoLib contractBaseInfoLib,
                                                               PaymentBaseInfo paymentBaseInfo, ContractGuarantorLib contractGuarantorLib, Object leasePriceLib) {
        if (StringUtils.isBlank(contractGuarantorLib.getGuarantorIds())) {
            return Collections.emptyList();
        }
        List<Long> clientIdList = JSON.parseArray(contractGuarantorLib.getGuarantorIds(), Long.class);
        List<CrGuarantorDraft> data = new ArrayList<>();
        //之前的数据按借据展开，但是现在借据被拆散了，所以这里需要根据账户再次拆分
        List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
        if (CollUtil.isEmpty(accountDrafts)) {
            return Collections.emptyList();
        }
        for (CrAccountDraft accountDraft : accountDrafts) {
            for (int i = 1; i <= clientIdList.size(); i++) {
                CrGuarantorDraft crGuarantor = new CrGuarantorDraft();
                crGuarantor.setReportState(ReportState.TO_BE_REPORT.name());
                crGuarantor.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                crGuarantor.setClientId(clientIdList.get(i - 1));
                crGuarantor.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
                crGuarantor.setPaymentId(paymentBaseInfo.getId());
                crGuarantor.setClientClass(GuarantorClientClassEnum.GUARANTOR.getValue());
                String guarantorContractCode = ReportBizUtil.contractCodeAddPaymentSeq(contractGuarantorLib.getGuarantorContractCode(), paymentBaseInfo.getPaymentCode());
                crGuarantor.setGuaranteContractCode(guarantorContractCode);
                crGuarantor.setJointGuarantorFlag(GuarantorJointFlagEnum.convert(contractGuarantorLib.getJointGuaranteeMark()));
                crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
                crGuarantor.setContractId(contractBaseInfoLib.getOriginId());
                // crGuarantor.setRepayLiabilityAmount(contractGuarantorLib.getGuaranteeAmountSingle());
                setGuarantorAmount(contractBaseInfoLib, contractGuarantorLib, leasePriceLib, crGuarantor, accountDraft);
                crGuarantor.setBusinessKey(crGuarantor.genBusinessKey(GuaranteeTypeEnum.GUARANTOR.getValue()));
                data.add(crGuarantor);
            }
        }
        return data;
    }

    private void setGuarantorAmount(ContractBaseInfoLib contractBaseInfoLib, ContractGuarantorLib contractGuarantorLib,
                                    Object leasePriceLib, CrGuarantorDraft crGuarantor, CrAccountDraft accountDraft) {
        Long guaranteeAmountSingle = contractGuarantorLib.getGuaranteeAmountSingle();
        if (Objects.isNull(guaranteeAmountSingle)) {
            return;
        }
        BigDecimal decimal = BigDecimal.valueOf(Optional.of(guaranteeAmountSingle).orElse(0L));
        Long accountAmount = accountDraft.getPaymentAmount();
        if (!ProjectBizType.BL.name().equals(contractBaseInfoLib.getBizType())) {
            ContractLeasePriceLib price = (ContractLeasePriceLib) leasePriceLib;
            BigDecimal contractAmount = BigDecimal.valueOf(price.getApplyCreditAmount());
            crGuarantor.setRepayLiabilityAmount(Util.mithrasLongDecimalTwo(
                    decimal.multiply(BigDecimal.valueOf(accountAmount))
                            .divide(contractAmount, 10, RoundingMode.HALF_UP).longValue()));
        } else {
            ContractFactoringPriceLib price = (ContractFactoringPriceLib) leasePriceLib;
            BigDecimal contractAmount = BigDecimal.valueOf(price.getContractAmount());
            crGuarantor.setRepayLiabilityAmount(Util.mithrasLongDecimalTwo(
                    decimal.multiply(BigDecimal.valueOf(accountAmount))
                            .divide(contractAmount, 10, RoundingMode.HALF_UP).longValue()));
        }
    }

    private ContractLeasePriceLib getContractLeasePriceLib(Long id) {
        //查找合同的报价方案
        return contractLeasePriceLibService.getOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                .eq(ContractLeasePriceLib::getContractId, id)
                .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractLeasePriceLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
    }

    private ContractFactoringPriceLib getContractFactoringPriceLib(Long id) {
        //查找合同的报价方案
        return contractFactoringPriceLibService.getOne(Wrappers.<ContractFactoringPriceLib>lambdaQuery()
                .eq(ContractFactoringPriceLib::getContractId, id)
                .eq(ContractFactoringPriceLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractFactoringPriceLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
    }

    private void fillClientData(List<CrGuarantorDraft> crGuarantorList) {
        List<Long> clientIdList = crGuarantorList.stream().map(CrGuarantorDraft::getClientId).collect(Collectors.toList());
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIdList)).stream().collect(Collectors.toMap(Client::getId, c -> c));
        crGuarantorList.forEach(cr -> {
            Client client = clientMap.get(cr.getClientId());
            if (Objects.isNull(client)) {
                return;
            }
            cr.setClientType(CrClientTypeEnum.convert(client.getClientType()));
            cr.setClientName(ReportBizUtil.removeSpecialChar(client.getClientName()));
            String guarantorIdType = ReportClientTypeEnum.CORPORATION.name().equals(client.getClientType()) ?
                    ReportClientTypeEnum.CORPORATION.getCode() : client.getCertType();
            cr.setGuarantorIdType(guarantorIdType);
            cr.setGuarantorId(ClientType.CORPORATION.name().equals(client.getClientType()) ?
                    ReportBizUtil.removeSpecialChar(client.getUscCode()) : ReportBizUtil.handleCertCode(cr.getGuarantorIdType(), ReportBizUtil.removeSpecialChar(client.getCertNumber())));
        });
    }

    @Override
    public Integer sort() {
        return 5;
    }
}
