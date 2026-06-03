package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.report.enums.biz.*;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrGuarantorDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
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
public class CrGuarantorHandler extends CrAbstractHandler<CrGuarantorDraft, CrGuarantor> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private CrAccountDraftService accountDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.GUARANTOR;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            contractBaseInfoLibList = Collections.emptyList();
        }
        List<CrGuarantorDraft> reportDataList = new ArrayList<>();
        reportDataList.addAll(collectContractChangeData(contractBaseInfoLibList));
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrGuarantorDraft::getBusinessKey)).collect(Collectors.toList());
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

        log.info("直租征信报送-保证表，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
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
                    draftMapper.updateById(reportData);
                }
            }
        }
        draftService.saveBatch(needInsertList);
    }

    public List<CrGuarantorDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList) {
        List<CrGuarantorDraft> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        for (ContractBaseInfoLib contractBaseInfoLib : contractBaseInfoLibList) {
            Boolean contractReportFlag = reportDataRepository.contractReport(contractBaseInfoLib);
            if (!Boolean.TRUE.equals(contractReportFlag)) {
                // 合同报送过滤逻辑
                continue;
            }
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name())
                    .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
            );
            // 过滤一下没有实际租金表的数据
            paymentBaseInfoList = reportDataRepository.filterNeedReportPaymentListSubTable(paymentBaseInfoList);
            if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            ContractLeasePriceLib leasePriceLib = getContractLeasePriceLib(contractBaseInfoLib.getOriginId());
            List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                    .eq(ContractGuarantorLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractGuarantorLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractGuarantorLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));
            List<ContractTenantryLib> contractTenantryLibList = new ArrayList<>();
            if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) && LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType())) {
                // 联合承租人
                contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                        .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                        .eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                        .eq(ContractTenantryLib::getIsReport, 1)
                );
            }

            //这里需要修改一下，需要判断是否是分笔投放
            if (!paymentBaseInfoList.isEmpty()) {
                for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                    contractGuarantorLibList.forEach(c -> resultList.addAll(buildCrGuarantorByGuarantor(contractBaseInfoLib, paymentBaseInfo, c, leasePriceLib)));
                    contractTenantryLibList.forEach(c -> resultList.add(buildCrGuarantorByTenantry(contractBaseInfoLib, paymentBaseInfo, c)));
                }
            }
        }
        return resultList;
    }

    private ContractLeasePriceLib getContractLeasePriceLib(Long id) {
        //查找合同的报价方案
        return contractLeasePriceLibService.getOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                .eq(ContractLeasePriceLib::getContractId, id)
                .orderByDesc(ContractLeasePriceLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
    }

    public List<CrGuarantorDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrGuarantorDraft> resultList = new ArrayList<>();
        // 查找付款模块有变动的数据
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTable(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
            return resultList;
        }

        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            ContractLeasePriceLib contractLeasePriceLib = getContractLeasePriceLib(paymentBaseInfo.getContractId());
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne())
            );
            if (Objects.isNull(contractBaseInfoLib)) {
                // 兼容历史错误数据 实际上有付款申请，就会有合同版本
                continue;
            }
            if (ProjectBizType.ZR.name().equals(contractBaseInfoLib.getBizType())) {
                // 债权转让不报
                continue;
            }
            List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                    .eq(ContractGuarantorLib::getContractId, paymentBaseInfo.getContractId())
                    .eq(ContractGuarantorLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractGuarantorLib::getIsReport, YesOrNoNumberEnum.YES.getCode()));
            contractGuarantorLibList.forEach(c -> resultList.addAll(buildCrGuarantorByGuarantor(contractBaseInfoLib, paymentBaseInfo, c, contractLeasePriceLib)));

            List<ContractTenantryLib> contractTenantryLibList = new ArrayList<>();
            if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType())) {
                // 联合承租人
                contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                        .eq(ContractTenantryLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                        .eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                        .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
                );
            }
            contractTenantryLibList.forEach(c -> resultList.add(buildCrGuarantorByTenantry(contractBaseInfoLib, paymentBaseInfo, c)));
        }
        return resultList;
    }

    private CrGuarantorDraft buildCrGuarantorByTenantry(ContractBaseInfoLib contractBaseInfoLib, PaymentBaseInfo paymentBaseInfo, ContractTenantryLib contractTenantryLib) {

        CrGuarantorDraft crGuarantor = CrGuarantorDraft.builder().build();
        crGuarantor.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setClientId(contractTenantryLib.getLesseeId())
                .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                .setPaymentId(paymentBaseInfo.getId())
                .setClientClass(GuarantorClientClassEnum.JOINT_LESSEE.getValue());
        crGuarantor.setContractId(contractBaseInfoLib.getOriginId());
        crGuarantor.setBusinessKey(crGuarantor.genBusinessKey(GuaranteeTypeEnum.TENANTRY.getValue()));
        return crGuarantor;
    }

    private List<CrGuarantorDraft> buildCrGuarantorByGuarantor(ContractBaseInfoLib contractBaseInfoLib,
                                                               PaymentBaseInfo paymentBaseInfo,
                                                               ContractGuarantorLib contractGuarantorLib,
                                                               ContractLeasePriceLib leasePriceLib) {
        if (CharSequenceUtil.isBlank(contractGuarantorLib.getGuarantorIds())) {
            return Collections.emptyList();
        }
        CrAccountDraft accountDraft = accountDraftService.getOne(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getPaymentId, paymentBaseInfo.getId())
                .eq(CrAccountDraft::getContractId, contractBaseInfoLib.getOriginId())
                .last(StringUtil.mysqlLimitOne()));
        List<Long> clientIdList = JSON.parseArray(contractGuarantorLib.getGuarantorIds(), Long.class);
        List<CrGuarantorDraft> data = new ArrayList<>();
        for (int i = 1; i <= clientIdList.size(); i++) {
            CrGuarantorDraft crGuarantor = CrGuarantorDraft.builder().build();
            crGuarantor.setReportState(ReportState.TO_BE_REPORT.name())
                    .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                    .setProcBusinessKey(null)
                    .setClientId(clientIdList.get(i - 1))
                    .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                    .setPaymentId(paymentBaseInfo.getId())
                    .setClientClass(GuarantorClientClassEnum.GUARANTOR.getValue())
                    .setJointGuarantorFlag(GuarantorJointFlagEnum.convert(contractGuarantorLib.getJointGuaranteeMark()));
            crGuarantor.setContractId(contractBaseInfoLib.getOriginId());
            Long guaranteeAmountSingle = contractGuarantorLib.getGuaranteeAmountSingle();
            BigDecimal decimal = BigDecimal.valueOf(guaranteeAmountSingle);
            Long accountAmount = accountDraft.getPaymentAmount();
            BigDecimal contractAmount = BigDecimal.valueOf(leasePriceLib.getApplyCreditAmount());
            crGuarantor.setRepayLiabilityAmount(Util.mithrasLongDecimalTwo(decimal.divide(contractAmount, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(accountAmount)).longValue()));
//            crGuarantor.setRepayLiabilityAmount(guaranteeAmountSingle);
            crGuarantor.setGuaranteContractCode(ReportBizUtil.contractCodeAddPaymentSeq(contractGuarantorLib.getGuarantorContractCode(), crGuarantor.getPaymentApplyCode()));
            crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
            crGuarantor.setBusinessKey(crGuarantor.genBusinessKey(GuaranteeTypeEnum.GUARANTOR.getValue()));
            data.add(crGuarantor);
        }
        return data;
    }

    private void fillClientData(List<CrGuarantorDraft> crGuarantorList) {
        if (CollUtil.isEmpty(crGuarantorList)) {
            return;
        }
        List<Long> clientIdList = crGuarantorList.stream().map(CrGuarantorDraft::getClientId).collect(Collectors.toList());
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIdList)).stream().collect(Collectors.toMap(Client::getId, c -> c));
        crGuarantorList.forEach(cr -> {
            Client client = clientMap.get(cr.getClientId());
            if (Objects.isNull(client)) {
                return;
            }
            cr.setClientType(CrClientTypeEnum.convert(client.getClientType()));
            cr.setClientName(ReportBizUtil.removeSpecialChar(client.getClientName()));
            cr.setGuarantorIdType(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportClientTypeEnum.CORPORATION.getCode() : client.getCertType());
            cr.setGuarantorId(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportBizUtil.removeSpecialChar(client.getUscCode()) : ReportBizUtil.handleCertCode(cr.getGuarantorIdType(), ReportBizUtil.removeSpecialChar(client.getCertNumber())));
        });
    }

    @Override
    public Integer sort() {
        return 4;
    }
}
