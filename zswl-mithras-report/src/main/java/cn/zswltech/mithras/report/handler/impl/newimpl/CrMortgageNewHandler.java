package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.report.enums.biz.*;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrMortgageDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrMortgage;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.MortgageItemTypeEnum;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractMortgageItemLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractMortgageLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItemLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.common.util.StreamUtil;
import cn.zswltech.mithras.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-抵押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 6:42 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrMortgageNewHandler extends CrAbstractHandler<CrMortgageDraft, CrMortgage> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractMortgageItemLibMapper contractMortgageItemLibMapper;
    @Resource
    private ContractMortgageLibMapper contractMortgageLibMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CommonInfoService commonInfoService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.MORTGAGE_NEW;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<ContractBaseInfoLib> contractBaseInfoLibList = commonInfoService.getContractBaseInfoLibs(dealTime, lastDealTime);
        List<CrMortgageDraft> reportDataList = new ArrayList<>();
        reportDataList.addAll(collectContractChangeData(contractBaseInfoLibList));
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrMortgageDraft::getBusinessKey)).collect(Collectors.toList());

        if (CollUtil.isEmpty(reportDataList)) {
            return;
        }
        fillClientData(reportDataList);
        Set<String> businessKeySet = reportDataList.stream().map(CrMortgageDraft::getBusinessKey).collect(Collectors.toSet());

        // 该次需上报的businessKey对应的列表 用于判断新增还是编辑
        List<CrMortgageDraft> existDataList = reportDataList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrMortgageDraft>lambdaQuery()
                .in(CrMortgageDraft::getBusinessKey, reportDataList.stream().map(CrMortgageDraft::getBusinessKey).collect(Collectors.toList()))
        );
        Map<String, CrMortgageDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrMortgageDraft::getBusinessKey, Function.identity()));

        // 合同已存在的数据 用于判断发生变动需要删除的数据
        Map<Long, Map<Long, Set<String>>> pbkMap = new HashMap<>();
        for (CrMortgageDraft crMortgageDraft : reportDataList) {
            Map<Long, Set<String>> paymentMap = pbkMap.computeIfAbsent(crMortgageDraft.getContractId(), k -> new HashMap<>());
            Set<String> bkSet = paymentMap.computeIfAbsent(crMortgageDraft.getPaymentId(), k -> new HashSet<>());
            bkSet.add(crMortgageDraft.getBusinessKey());
        }
        List<CrMortgageDraft> existContractDataList = contractBaseInfoLibList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrMortgageDraft>lambdaQuery()
                .in(CrMortgageDraft::getContractId, contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList()))
        );
        Set<String> needDeleteKeyList = existContractDataList.stream().filter(k -> {
            if (!pbkMap.containsKey(k.getContractId()) || !pbkMap.get(k.getContractId()).containsKey(k.getPaymentId())) {
                // 如果此次收集的数据 不含变动合同的id或不含付款id则不处理
                return false;
            }
            // 该合同下该付款曾经有数据 但此次报送没有 需删除
            return !pbkMap.get(k.getContractId()).get(k.getPaymentId()).contains(k.getBusinessKey());
        }).map(CrMortgageDraft::getBusinessKey).collect(Collectors.toSet());

        log.info("非直租征信报送-抵押表，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
        if (CollectionUtils.isNotEmpty(needDeleteKeyList)) {
            draftMapper.delete(Wrappers.<CrMortgageDraft>lambdaQuery().in(CrMortgageDraft::getBusinessKey, needDeleteKeyList));
            formalMapper.delete(Wrappers.<CrMortgage>lambdaQuery().in(CrMortgage::getBusinessKey, needDeleteKeyList));
        }
        List<CrMortgageDraft> needInsertList = new ArrayList<>();
        for (CrMortgageDraft reportData : reportDataList) {
            if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                needInsertList.add(reportData);
            } else {
                CrMortgageDraft existData = existReportDataMap.get(reportData.getBusinessKey());
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

    public List<CrMortgageDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList) {
        List<CrMortgageDraft> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        contractBaseInfoLibList.forEach(contractBaseInfoLib -> {
            Boolean contractReportFlag = reportDataRepository.mainTenantryReport(contractBaseInfoLib);
            if (!Boolean.TRUE.equals(contractReportFlag)) {
                // 主承租人报送过滤逻辑
                return;
            }
            List<PaymentBaseInfo> writtenOffPaymentList = commonInfoService.getPaymentBaseInfos(contractBaseInfoLib);
            if (CollectionUtils.isEmpty(writtenOffPaymentList)) {
                // 该合同不存在付款核销完毕或者部分核销的付款申请
                return;
            }

            List<ContractMortgageItemLib> contractMortgageItemLibList = contractMortgageItemLibMapper.selectList(Wrappers.<ContractMortgageItemLib>lambdaQuery()
                    .eq(ContractMortgageItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractMortgageItemLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractMortgageItemLib::getVersion, contractBaseInfoLib.getVersion()));
            if (CollectionUtils.isEmpty(contractMortgageItemLibList)) {
                return;
            }
            Map<Long, ContractMortgageLib> contractMortgageLibMap = contractMortgageLibMapper.selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                    .eq(ContractMortgageLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractMortgageLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractMortgageLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractMortgageLib::getMortgageItemType, MortgageItemTypeEnum.SUBSTANTIVE_ITEM.name())
            ).stream().collect(Collectors.toMap(ContractMortgageLib::getOriginId, Function.identity()));

            Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(writtenOffPaymentList);
            for (PaymentBaseInfo paymentBaseInfo : writtenOffPaymentList) {
                for (ContractMortgageItemLib itemLib : contractMortgageItemLibList) {
                    resultList.addAll(buildContractMortgage(paymentIdAccountListMap, paymentBaseInfo, contractMortgageLibMap, itemLib));
                }
            }
        });
        return resultList;
    }

    public List<CrMortgageDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrMortgageDraft> resultList = new ArrayList<>();
        // 查找付款模块有新增版本的数据
        List<PaymentBaseInfo> paymentBaseInfoList = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTableNew(dealTime, lastDealTime);
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            paymentBaseInfoList = Collections.emptyList();
        }
        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = commonInfoService.getPaymentIdAccountListMap(paymentBaseInfoList);
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
            List<ContractMortgageItemLib> contractMortgageItemLibList = contractMortgageItemLibMapper.selectList(Wrappers.<ContractMortgageItemLib>lambdaQuery()
                    .eq(ContractMortgageItemLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractMortgageItemLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractMortgageItemLib::getVersionType, VersionTypeConstants.NORMAL)
            );
            if (CollectionUtils.isEmpty(contractMortgageItemLibList)) {
                continue;
            }
            Map<Long, ContractMortgageLib> contractMortgageLibMap = contractMortgageLibMapper.selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                    .eq(ContractMortgageLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractMortgageLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractMortgageLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractMortgageLib::getMortgageItemType, MortgageItemTypeEnum.SUBSTANTIVE_ITEM.name())
            ).stream().collect(Collectors.toMap(ContractMortgageLib::getOriginId, Function.identity()));

            for (ContractMortgageItemLib itemLib : contractMortgageItemLibList) {
                resultList.addAll(buildContractMortgage(paymentIdAccountListMap, paymentBaseInfo, contractMortgageLibMap, itemLib));
            }
        }
        return resultList;
    }

    private List<CrMortgageDraft> buildContractMortgage(Map<Long, List<CrAccountDraft>> paymentIdAccountListMap, PaymentBaseInfo paymentBaseInfo,
                                                        Map<Long, ContractMortgageLib> contractMortgageLibMap, ContractMortgageItemLib c) {
        ContractMortgageLib contractMortgageLib = contractMortgageLibMap.get(c.getMortgageId());
        List<CrMortgageDraft> result = new ArrayList<>();
        if (Objects.nonNull(contractMortgageLib)) {
            List<Long> clientIdList = JSON.parseArray(contractMortgageLib.getMortgageIds()).toJavaList(Long.class);
            //之前的数据按借据展开，但是现在借据被拆散了，所以这里需要根据账户再次拆分
            List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
            if (CollUtil.isEmpty(accountDrafts)) {
                return Collections.emptyList();
            }
            for (CrAccountDraft accountDraft : accountDrafts) {
                Long paymentAmount = Optional.ofNullable(CrFacade.AMOUNT_MAP.get(accountDraft.getPaymentApplyCode() + accountDraft.getPaymentId())).orElse(0L);
                if (paymentAmount.equals(0L)) {
                    paymentAmount = accountDraft.getPaymentAmount();
                }
                CrMortgageDraft crMortgage = CrMortgageDraft.builder().build();
                crMortgage.setReportState(ReportState.TO_BE_REPORT.name());
                crMortgage.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                crMortgage.setClientId(clientIdList.get(0));
                crMortgage.setPaymentApplyCode(accountDraft.getPaymentApplyCode());
                crMortgage.setPaymentId(paymentBaseInfo.getId());
                crMortgage.setApplyPaymentAmount(paymentAmount);
                crMortgage.setMortgageContractCode(ReportBizUtil.contractCodeAddPaymentSeq(ReportBizUtil.removeSpecialChar(contractMortgageLib.getMortgageContractCode()), paymentBaseInfo.getPaymentCode()));
                crMortgage.setMaxFlag(contractMortgageLib.getHighest());
                crMortgage.setSequence(String.valueOf(c.getSequence()));
                crMortgage.setType(MortgageDataTypeEnum.convert(c.getCategory()));
                crMortgage.setModelType(MortgageModelTypeEnum.convert(c.getUniqueIdentifyCodeType()));
                crMortgage.setModel(ReportBizUtil.removeSpecialChar(c.getUniqueIdentifyCode()));
                crMortgage.setAssessedValue(c.getAssessedNetValue());
                crMortgage.setAppraisalCompanyType(MortgageAppraisalCompanyTypeEnum.convert(contractMortgageLib.getAssess()));
                crMortgage.setAssessedDate(contractMortgageLib.getAssessDate());
                crMortgage.setMortgageDescribe(ReportBizUtil.removeSpecialChar(contractMortgageLib.getMortgageDescribe()));
                crMortgage.setMortgageContractCode2(ReportBizUtil.handleMortgageContractCode(crMortgage.getMortgageContractCode()));
                crMortgage.setBusinessKey(crMortgage.genBusinessKey(c.getOriginId()));
                crMortgage.setContractId(paymentBaseInfo.getContractId());
                result.add(crMortgage);
            }
            return result;
        }
        return Collections.emptyList();
    }

    private void fillClientData(List<CrMortgageDraft> crMortgageList) {
        if (CollUtil.isEmpty(crMortgageList)) {
            return;
        }
        List<Long> clientIdList = crMortgageList.stream().map(CrMortgageDraft::getClientId).collect(Collectors.toList());
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .in(Client::getId, clientIdList)
        ).stream().collect(Collectors.toMap(Client::getId, Function.identity()));
        crMortgageList.forEach(cr -> {
            Client client = clientMap.get(cr.getClientId());
            if (Objects.isNull(client)) {
                return;
            }
            cr.setMortgageType(CrClientTypeEnum.convert(client.getClientType()));
            cr.setMortgageName(ReportBizUtil.removeSpecialChar(client.getClientName()));
            cr.setMortgageIdType(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportClientTypeEnum.CORPORATION.getCode() : ReportClientTypeEnum.NORMAL.getCode());
            cr.setMortgageId(ClientType.CORPORATION.name().equals(client.getClientType()) ? ReportBizUtil.removeSpecialChar(client.getUscCode()) : ReportBizUtil.handleCertCode(cr.getMortgageIdType(), ReportBizUtil.removeSpecialChar(client.getCertNumber())));
        });
    }

    @Override
    public Integer sort() {
        return 7;
    }
}

