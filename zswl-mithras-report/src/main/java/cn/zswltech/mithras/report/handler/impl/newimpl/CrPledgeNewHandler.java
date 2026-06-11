package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.biz.CrClientTypeEnum;
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
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPledgeItemLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeItemLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.util.StreamUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
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
public class CrPledgeNewHandler extends CrAbstractHandler<CrPledgeDraft, CrPledge> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractPledgeLibMapper contractPledgeLibMapper;
    @Resource
    private ContractPledgeItemLibMapper contractPledgeItemLibMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CommonInfoService commonInfoService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.PLEDGE_NEW;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<ContractBaseInfoLib> contractBaseInfoLibList = commonInfoService.getContractBaseInfoLibs(dealTime, lastDealTime);
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
        Map<String, CrPledgeDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrPledgeDraft::getBusinessKey, Function.identity()));

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

        log.info("非直租征信报送-质押表处理，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
        if (CollectionUtils.isNotEmpty(needDeleteKeyList)) {
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
        if (CollUtil.isNotEmpty(needInsertList)) {
            draftService.saveOrUpdateBatch(needInsertList);
        }
    }

    public List<CrPledgeDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList) {
        List<CrPledgeDraft> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        contractBaseInfoLibList.forEach(contractBaseInfoLib -> {
            Boolean contractReportFlag = reportDataRepository.mainTenantryReport(contractBaseInfoLib);
            if (!Boolean.TRUE.equals(contractReportFlag)) {
                // 主承租人报送过滤逻辑
                return;
            }
            // 找一找该合同有没有付款核销完毕或者部分核销的付款申请数据
            List<PaymentBaseInfo> writtenOffPaymentList = commonInfoService.getPaymentBaseInfos(contractBaseInfoLib);
            if (CollUtil.isEmpty(writtenOffPaymentList)) {
                // 该合同不存在付款核销完毕或者部分核销的付款申请
                return;
            }

            List<ContractPledgeItemLib> contractPledgeItemLibList = contractPledgeItemLibMapper.selectList(Wrappers.<ContractPledgeItemLib>lambdaQuery()
                    .eq(ContractPledgeItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeItemLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeItemLib::getVersion, contractBaseInfoLib.getVersion()));
            if (CollectionUtils.isEmpty(contractPledgeItemLibList)) {
                return;
            }
            Map<Long, ContractPledgeLib> contractPledgeLibMap = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                    .eq(ContractPledgeLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractPledgeLib::getVersionType, VersionTypeConstants.NORMAL)
            ).stream().collect(Collectors.toMap(ContractPledgeLib::getOriginId, Function.identity()));

            Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(writtenOffPaymentList);
            for (PaymentBaseInfo paymentBaseInfo : writtenOffPaymentList) {
                contractPledgeItemLibList.forEach(c -> {
                    List<CrPledgeDraft> pledgeDrafts = buildCrPledgeList(paymentIdAccountListMap, paymentBaseInfo, contractPledgeLibMap.get(c.getPledgeId()), c, contractBaseInfoLib);
                    resultList.addAll(pledgeDrafts);
                });
            }
        });
        return resultList;
    }

    public List<CrPledgeDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrPledgeDraft> resultList = new ArrayList<>();
        // 查找付款模块有变动的数据
        List<PaymentBaseInfo> paymentBaseInfoList = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTableNew(dealTime, lastDealTime);
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            paymentBaseInfoList = Collections.emptyList();
        }
        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(paymentBaseInfoList);
        paymentBaseInfoList.forEach(paymentBaseInfo -> {
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
            List<ContractPledgeItemLib> contractPledgeItemLibList = contractPledgeItemLibMapper.selectList(Wrappers.<ContractPledgeItemLib>lambdaQuery()
                    .eq(ContractPledgeItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeItemLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeItemLib::getVersion, contractBaseInfoLib.getVersion()));
            if (CollectionUtils.isEmpty(contractPledgeItemLibList)) {
                return;
            }
            Map<Long, ContractPledgeLib> contractPledgeLibMap = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                    .eq(ContractPledgeLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractPledgeLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractPledgeLib::getVersion, contractBaseInfoLib.getVersion())
            ).stream().collect(Collectors.toMap(ContractPledgeLib::getOriginId, c -> c));
            contractPledgeItemLibList.forEach(c -> resultList.addAll(buildCrPledgeList(paymentIdAccountListMap, paymentBaseInfo, contractPledgeLibMap.get(c.getPledgeId()), c, contractBaseInfoLib)));
        });
        return resultList;
    }

    private List<CrPledgeDraft> buildCrPledgeList(Map<Long, List<CrAccountDraft>> paymentIdAccountListMap, PaymentBaseInfo paymentBaseInfo, ContractPledgeLib contractPledgeLib, ContractPledgeItemLib contractPledgeItemLib, ContractBaseInfoLib contractBaseInfoLib) {
        if (Objects.isNull(contractPledgeLib) || StringUtils.isBlank(contractPledgeLib.getPledgeIds())) {
            return Collections.emptyList();
        }
        List<CrPledgeDraft> result = new ArrayList<>();
        List<Long> clientIdList = JSON.parseArray(contractPledgeLib.getPledgeIds()).toJavaList(Long.class);
        if (CollectionUtils.isEmpty(clientIdList)) {
            return Collections.emptyList();
        }
        //之前的数据按借据展开，但是现在借据被拆散了，所以这里需要根据账户再次拆分
        List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
        if (CollUtil.isEmpty(accountDrafts)) {
            return Collections.emptyList();
        }
        for (CrAccountDraft accountDraft : accountDrafts) {
            CrPledgeDraft crPledge = CrPledgeDraft.builder().build();
            Long paymentAmount = Optional.ofNullable(CrFacade.AMOUNT_MAP.get(accountDraft.getPaymentApplyCode() + accountDraft.getPaymentId())).orElse(0L);
            if (paymentAmount.equals(0L)) {
                paymentAmount = accountDraft.getPaymentAmount();
            }
            crPledge.setReportState(ReportState.TO_BE_REPORT.name());
            crPledge.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
            crPledge.setProcBusinessKey(null);
            crPledge.setClientId(clientIdList.get(0));
            crPledge.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
            crPledge.setPaymentId(paymentBaseInfo.getId());
            crPledge.setApplyPaymentAmount(paymentAmount);
            crPledge.setPledgeContractCode(ReportBizUtil.contractCodeAddPaymentSeq(ReportBizUtil.removeSpecialChar(contractPledgeLib.getPledgeContractCode()), paymentBaseInfo.getPaymentCode()));
            crPledge.setMaxFlag(contractPledgeLib.getHighest());
            crPledge.setSequence(String.valueOf(contractPledgeItemLib.getSequence()));
            crPledge.setType(PledgeDataTypeEnum.convert(contractPledgeItemLib.getCategory()));
            crPledge.setAssessedValue(contractPledgeItemLib.getAssessedValue());
            crPledge.setPledgeContractCode2(ReportBizUtil.handlePledgeContractCode(crPledge.getPledgeContractCode()));
            crPledge.setBusinessKey(crPledge.genBusinessKey(contractPledgeItemLib.getOriginId()));
            crPledge.setContractId(paymentBaseInfo.getContractId());
            result.add(crPledge);
        }
        return result;
    }

    private void fillClientData(List<CrPledgeDraft> crPledgeList) {
        if (CollUtil.isEmpty(crPledgeList)) {
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
            cr.setPledgeIdType(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportClientTypeEnum.CORPORATION.getCode() : ReportClientTypeEnum.NORMAL.getCode());
            cr.setPledgeId(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportBizUtil.removeSpecialChar(client.getUscCode()) : ReportBizUtil.handleCertCode(cr.getPledgeIdType(), ReportBizUtil.removeSpecialChar(client.getCertNumber())));
        });
    }

    @Override
    public Integer sort() {
        return 11;
    }
}
