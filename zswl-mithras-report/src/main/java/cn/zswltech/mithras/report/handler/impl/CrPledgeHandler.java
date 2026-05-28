package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.biz.CrClientTypeEnum;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.PledgeDataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.ReportClientTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrPledgeDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrPledge;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractPledgeItemLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeItemLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-质押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 6:42 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrPledgeHandler extends CrAbstractHandler<CrPledgeDraft, CrPledge> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractPledgeLibMapper contractPledgeLibMapper;
    @Resource
    private ContractPledgeItemLibMapper contractPledgeItemLibMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CrAccountDraftService crAccountDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.PLEDGE;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            contractBaseInfoLibList = Collections.emptyList();
        }
        List<CrPledgeDraft> reportDataList = new ArrayList<>();
        reportDataList.addAll(collectContractChangeData(contractBaseInfoLibList));
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrPledgeDraft::getBusinessKey)).collect(Collectors.toList());
        fillClientData(reportDataList);
        Set<String> businessKeySet = reportDataList.stream().map(CrPledgeDraft::getBusinessKey).collect(Collectors.toSet());

        // 该次需上报的businessKey对应的列表 用于判断新增还是编辑
        List<CrPledgeDraft> existDataList = reportDataList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrPledgeDraft>lambdaQuery()
                .in(CrPledgeDraft::getBusinessKey, reportDataList.stream().map(CrPledgeDraft::getBusinessKey).collect(Collectors.toList()))
        );
        Map<String, CrPledgeDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrPledgeDraft::getBusinessKey, cr -> cr));

        // 合同已存在的数据 用于判断发生变动需要删除的数据
        Map<Long, Map<Long, Set<String>>> pbkMap = new HashMap<>();
        for (CrPledgeDraft crPledgeDraft : reportDataList) {
            Map<Long, Set<String>> paymentMap = pbkMap.computeIfAbsent(crPledgeDraft.getContractId(), k -> new HashMap<>());
            Set<String> bkSet = paymentMap.computeIfAbsent(crPledgeDraft.getPaymentId(), k -> new HashSet<>());
            bkSet.add(crPledgeDraft.getBusinessKey());
        }
        List<CrPledgeDraft> existContractDataList = contractBaseInfoLibList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrPledgeDraft>lambdaQuery()
                .in(CrPledgeDraft::getContractId, contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList()))
        );
        Set<String> needDeleteKeyList = existContractDataList.stream().filter(k -> {
            if (!pbkMap.containsKey(k.getContractId()) || !pbkMap.get(k.getContractId()).containsKey(k.getPaymentId())) {
                // 如果此次收集的数据 不含变动合同的id或不含付款id则不处理
                return false;
            }
            // 该合同下该付款曾经有数据 但此次报送没有 需删除
            return !pbkMap.get(k.getContractId()).get(k.getPaymentId()).contains(k.getBusinessKey());
        }).map(CrPledgeDraft::getBusinessKey).collect(Collectors.toSet());

        log.info("直租征信报送-质押表处理，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
        if (CollUtil.isNotEmpty(needDeleteKeyList)) {
            draftMapper.delete(Wrappers.<CrPledgeDraft>lambdaQuery().in(CrPledgeDraft::getBusinessKey, needDeleteKeyList));
            formalMapper.delete(Wrappers.<CrPledge>lambdaQuery().in(CrPledge::getBusinessKey, needDeleteKeyList));
        }
        List<CrPledgeDraft> needInsertList = new ArrayList<>();
        for (CrPledgeDraft reportData : reportDataList) {
            if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                needInsertList.add(reportData);
            } else {
                CrPledgeDraft existData = existReportDataMap.get(reportData.getBusinessKey());
                if (ReportCompareUtil.checkChange(reportData, existData, existData.ignoreCompareFieldNames())) {
                    reportData.setId(existData.getId());
                    draftMapper.updateById(reportData);
                }
            }
        }
        draftService.saveBatch(needInsertList);
    }

    public List<CrPledgeDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList) {
        List<CrPledgeDraft> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        for (ContractBaseInfoLib contractBaseInfoLib : contractBaseInfoLibList) {
            Boolean contractReportFlag = reportDataRepository.mainTenantryReport(contractBaseInfoLib);
            if (!Boolean.TRUE.equals(contractReportFlag)) {
                // 主承租人报送过滤逻辑
                continue;
            }
            // 找一找该合同有没有付款核销完毕的付款申请数据
            List<PaymentBaseInfo> writtenOffPaymentList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(PaymentBaseInfo::getPaymentStatus, RecordStatus.TAKE_EFFECT.name())
                    .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
            );
            // 过滤一下没有实际租金表的数据
            writtenOffPaymentList = reportDataRepository.filterNeedReportPaymentListSubTable(writtenOffPaymentList);
            if (CollectionUtils.isEmpty(writtenOffPaymentList)) {
                // 该合同不存在付款核销完毕的付款申请
                continue;
            }

            List<ContractPledgeItemLib> contractPledgeItemLibList = contractPledgeItemLibMapper.selectList(Wrappers.<ContractPledgeItemLib>lambdaQuery()
                    .eq(ContractPledgeItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeItemLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeItemLib::getVersion, contractBaseInfoLib.getVersion()));
            if (CollectionUtils.isEmpty(contractPledgeItemLibList)) {
                continue;
            }
            Map<Long, ContractPledgeLib> contractPledgeLibMap = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                    .eq(ContractPledgeLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeLib::getVersion, contractBaseInfoLib.getVersion())
            ).stream().collect(Collectors.toMap(ContractPledgeLib::getOriginId, Function.identity()));

            for (PaymentBaseInfo paymentBaseInfo : writtenOffPaymentList) {
                contractPledgeItemLibList.forEach(c -> resultList.addAll(buildCrPledgeList(paymentBaseInfo, contractPledgeLibMap.get(c.getPledgeId()), c, contractBaseInfoLib)));
            }
        }
        return resultList;
    }

    public List<CrPledgeDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrPledgeDraft> resultList = new ArrayList<>();
        // 查找付款模块有变动的数据
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTable(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            paymentBaseInfoList = Collections.emptyList();
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
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
            List<ContractPledgeItemLib> contractPledgeItemLibList = contractPledgeItemLibMapper.selectList(Wrappers.<ContractPledgeItemLib>lambdaQuery()
                    .eq(ContractPledgeItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeItemLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeItemLib::getVersion, contractBaseInfoLib.getVersion()));
            if (CollectionUtils.isEmpty(contractPledgeItemLibList)) {
                continue;
            }
            Map<Long, ContractPledgeLib> contractPledgeLibMap = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                    .eq(ContractPledgeLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeLib::getVersion, contractBaseInfoLib.getVersion())
            ).stream().collect(Collectors.toMap(ContractPledgeLib::getOriginId, Function.identity()));
            contractPledgeItemLibList.forEach(c -> resultList.addAll(buildCrPledgeList(paymentBaseInfo, contractPledgeLibMap.get(c.getPledgeId()), c, contractBaseInfoLib)));
        }
        return resultList;
    }

    private List<CrPledgeDraft> buildCrPledgeList(PaymentBaseInfo paymentBaseInfo, ContractPledgeLib contractPledgeLib, ContractPledgeItemLib contractPledgeItemLib, ContractBaseInfoLib contractBaseInfoLib) {
        if (Objects.isNull(contractPledgeLib) || StringUtils.isBlank(contractPledgeLib.getPledgeIds())) {
            return Collections.emptyList();
        }
        List<Long> clientIdList = JSON.parseArray(contractPledgeLib.getPledgeIds()).toJavaList(Long.class);
        if (CollectionUtils.isEmpty(clientIdList)) {
            return Collections.emptyList();
        }
        CrAccountDraft draft = crAccountDraftService.getOne(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getContractId, contractBaseInfoLib.getOriginId())
                .eq(CrAccountDraft::getPaymentId, paymentBaseInfo.getId()).last(StringUtil.mysqlLimitOne()));
        CrPledgeDraft crPledge = CrPledgeDraft.builder().build();
        Long paymentAmount = Optional.ofNullable(CrFacade.AMOUNT_MAP.get(draft.getPaymentApplyCode() + draft.getPaymentId())).orElse(0L);
        if (paymentAmount.equals(0L)) {
            paymentAmount = draft.getPaymentAmount();
        }
        crPledge.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setClientId(clientIdList.get(0))
                .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                .setPaymentId(paymentBaseInfo.getId())
                .setApplyPaymentAmount(paymentAmount)
                .setMaxFlag(contractPledgeLib.getHighest())
                .setSequence(String.valueOf(contractPledgeItemLib.getSequence()))
                .setType(PledgeDataTypeEnum.convert(contractPledgeItemLib.getCategory()))
                .setAssessedValue(contractPledgeItemLib.getAssessedValue());
        crPledge.setBusinessKey(crPledge.genBusinessKey(contractPledgeItemLib.getOriginId()));
        crPledge.setPledgeContractCode(ReportBizUtil.contractCodeAddPaymentSeq(ReportBizUtil.removeSpecialChar(contractPledgeLib.getPledgeContractCode()), crPledge.getPaymentApplyCode()));
        crPledge.setPledgeContractCode2(ReportBizUtil.handlePledgeContractCode(crPledge.getPledgeContractCode()));
        crPledge.setContractId(paymentBaseInfo.getContractId());
        return Collections.singletonList(crPledge);
    }

    private void fillClientData(List<CrPledgeDraft> crPledgeList) {
        if (CollectionUtils.isEmpty(crPledgeList)) {
            return;
        }
        List<Long> clientIdList = crPledgeList.stream().map(CrPledgeDraft::getClientId).collect(Collectors.toList());
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .in(Client::getId, clientIdList)
        ).stream().collect(Collectors.toMap(Client::getId, Function.identity()));
        crPledgeList.forEach(cr -> {
            Client client = clientMap.get(cr.getClientId());
            if (Objects.isNull(client)) {
                return;
            }
            cr.setPledgeType(CrClientTypeEnum.convert(client.getClientType()));
            cr.setPledgeName(ReportBizUtil.removeSpecialChar(client.getClientName()));
            cr.setPledgeIdType(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportClientTypeEnum.CORPORATION.getCode() : client.getCertType());
            cr.setPledgeId(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportBizUtil.removeSpecialChar(client.getUscCode()) : ReportBizUtil.handleCertCode(cr.getPledgeIdType(), ReportBizUtil.removeSpecialChar(client.getCertNumber())));
        });
    }

    @Override
    public Integer sort() {
        return 10;
    }
}
