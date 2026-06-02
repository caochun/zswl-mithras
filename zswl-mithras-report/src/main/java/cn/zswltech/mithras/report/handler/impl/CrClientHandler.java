package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.model.CrClientDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrClient;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.CreditReportMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-客户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 1:29 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrClientHandler extends CrAbstractHandler<CrClientDraft, CrClient> {

    @Resource
    protected CommonVersionMapper commonVersionMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private CreditReportMapper creditReportMapper;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.CLIENT;
    }

    @Override
    public void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = new ArrayList<>();
        List<ContractBaseInfoLib> collection = stringListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(collection)) {
            contractBaseInfoLibList = collection;
        }
        // 收集数据变动
        Set<Long> reportClientIdSet = new HashSet<>();
        reportClientIdSet.addAll(collectClientChangeData(dealTime, lastDealTime));
        reportClientIdSet.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportClientIdSet.addAll(contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getClientId).collect(Collectors.toList()));

        List<CrClientDraft> reportDataList = buildCrClientListByClientIdList(reportClientIdSet);
        List<CrClientDraft> existDataList = reportDataList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrClientDraft>lambdaQuery()
                .in(CrClientDraft::getBusinessKey, reportDataList.stream().map(CrClientDraft::getBusinessKey).collect(Collectors.toList()))
        );
        Map<String, CrClientDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrClientDraft::getBusinessKey, Function.identity(), (k1, k2) -> k1));
        List<CrClientDraft> needInsertList = new ArrayList<>();
        log.info("征信报送-客户表，此次处理数量:{}, 处理businessKey列表:{}", reportDataList.size(), JSON.toJSONString((reportDataList.stream().map(CrClientDraft::getBusinessKey).collect(Collectors.toList()))));
        for (CrClientDraft reportData : reportDataList) {
            if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                // 新增
                needInsertList.add(reportData);
            } else {
                // 编辑 需要判断是否实际发生变化
                CrClientDraft existData = existReportDataMap.get(reportData.getBusinessKey());
                if (ReportCompareUtil.checkChange(reportData, existData, existData.ignoreCompareFieldNames())) {
                    reportData.setId(existData.getId());
                    draftMapper.updateById(reportData);
                }
            }
        }
        draftService.saveBatch(needInsertList);
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = "reportTransactionManager")
    @Override
    public void clearNeedDeleteContract(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> changeContractBaseInfoList = new ArrayList<>();
        List<ContractBaseInfoLib> collection = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isNotEmpty(collection)) {
            changeContractBaseInfoList = collection;
        }
        Set<Long> needCheckClientIdSet = changeContractBaseInfoList.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());
        // 这些合同有变更，看看这些合同对应的主客户需不需要删除
        List<Long> needDeleteClientIdList = new ArrayList<>();
        for (Long clientId : needCheckClientIdSet) {
            if (creditReportMapper.countReportClient(clientId) == 0) {
                needDeleteClientIdList.add(clientId);
            }
        }
        if (CollectionUtils.isNotEmpty(needDeleteClientIdList)) {
            draftMapper.delete(Wrappers.<CrClientDraft>lambdaQuery().in(CrClientDraft::getClientId, needDeleteClientIdList));
            formalMapper.delete(Wrappers.<CrClient>lambdaQuery().in(CrClient::getClientId, needDeleteClientIdList));
        }
    }

    private List<Long> collectClientChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 查找客户模块有新增版本的数据
        List<CommonVersion> commonVersionList = commonVersionMapper.selectList(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CLIENT.name())
                .gt(CommonVersion::getCreateTime, lastDealTime)
                .le(CommonVersion::getCreateTime, dealTime)
        );
        // 版本号倒序 根据主表id去重
        commonVersionList = commonVersionList.stream()
                .sorted(Comparator.comparing(CommonVersion::getVersion).reversed())
                .filter(StreamUtil.distinctByKey(CommonVersion::getMainId))
                .collect(Collectors.toList());
        return commonVersionList.stream().map(CommonVersion::getMainId).collect(Collectors.toList());
    }

    private List<Long> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        // 查找客户模块有新增版本的数据
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTable(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = new ArrayList<>();
        List<PaymentBaseInfo> collection = stringListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(collection)) {
            paymentBaseInfoList = collection;
        }
        return paymentBaseInfoList.stream().map(PaymentBaseInfo::getClientId).collect(Collectors.toList());
    }

    private List<CrClientDraft> buildCrClientListByClientIdList(Set<Long> clientIdList) {
        List<CrClientDraft> resultList = new ArrayList<>();
        for (Long clientId : clientIdList) {
            if (creditReportMapper.countReportClient(clientId) == 0) {
                // 合同校验、付款核销校验
                continue;
            }
            Client client = clientMapper.selectById(clientId);
            if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
                // 只处理法人
                continue;
            }
            // 工商信息
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                    .eq(CorpCommerceInfoLib::getClientId, client.getId())
                    .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(CorpCommerceInfoLib::getVersion, client.getNewestVersion())
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(corpCommerceInfoLib)) {
                // 兼容历史错误数据 实际上必须有工商信息，才能生成版本
                continue;
            }
            // 地址信息
            CorpAddressInfoLib corpRegisterAddress = corpAddressInfoLibMapper.selectOne(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                    .eq(CorpAddressInfoLib::getClientId, client.getId())
                    .eq(CorpAddressInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(CorpAddressInfoLib::getVersion, client.getNewestVersion())
                    .eq(CorpAddressInfoLib::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
                    .last(StringUtil.mysqlLimitOne()));
            resultList.add(buildCrClient(client, corpCommerceInfoLib, corpRegisterAddress));
        }
        return resultList;
    }

    private CrClientDraft buildCrClient(Client client, CorpCommerceInfoLib corpCommerceInfoLib, CorpAddressInfoLib corpRegisterAddress) {
        // 组装数据
        CrClientDraft crClient = CrClientDraft.builder().build();
        crClient.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setClientId(client.getId())
                .setClientCode(ReportBizUtil.removeSpecialChar(client.getClientCode()))
                .setClientName(ReportBizUtil.removeSpecialChar(client.getClientName()))
                .setZhongZhengCode(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getZhongZhengCode()))
                .setContinuousStatus(corpCommerceInfoLib.getContinuousStatus())
                .setOrgType(corpCommerceInfoLib.getOrgType())
                .setRegisterAddress(ReportBizUtil.removeSpecialChar(genFullAddress(corpRegisterAddress)))
                .setRegionCode(ReportBizUtil.removeSpecialChar(Optional.ofNullable(corpRegisterAddress).map(CorpAddressInfoLib::getRegionCode).orElse(null)))
                .setEstablishDate(corpCommerceInfoLib.getEstablishDate())
                .setBizLicenseEndDate(corpCommerceInfoLib.getBizLicenseEndDate())
                .setBizScope(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getBizScope()))
                .setIndustryType(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getIndustryType()))
                .setEconomyType(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getEconomyType()))
                .setOrgScale(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getOrgScale()))
                .setRegisterCurrencyType(corpCommerceInfoLib.getRegisterCurrencyType())
                .setRegisterCapital(corpCommerceInfoLib.getRegisterCapital())
                .setCorpRepresent(ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getCorpRepresent()))
                .setCorpCertType(corpCommerceInfoLib.getCorpCertType())
                .setCorpCertCode(ReportBizUtil.handleCertCode(corpCommerceInfoLib.getCorpCertType(), ReportBizUtil.removeSpecialChar(corpCommerceInfoLib.getCorpCertCode())))
                .setEffectDate(LocalDate.now().minusDays(1));
        crClient.setBusinessKey(crClient.genBusinessKey());
        return crClient;
    }

    private String genFullAddress(CorpAddressInfoLib addressInfo) {
        if (Objects.isNull(addressInfo)) {
            return null;
        }

        Set<String> codeSet = new HashSet<>(4);
        codeSet.add(addressInfo.getCountry());
        codeSet.add(addressInfo.getProvince());
        codeSet.add(addressInfo.getCity());
        codeSet.add(addressInfo.getDistrict());
        codeSet = codeSet.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, codeSet))
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));

        return new StringBuilder()
                .append(Optional.ofNullable(nameMap.get(addressInfo.getCountry())).orElse(""))
                .append(Optional.ofNullable(nameMap.get(addressInfo.getProvince())).orElse(""))
                .append(Optional.ofNullable(nameMap.get(addressInfo.getCity())).orElse(""))
                .append(Optional.ofNullable(nameMap.get(addressInfo.getDistrict())).orElse(""))
                .append(Optional.ofNullable(addressInfo.getDetail()).orElse(""))
                .toString();
    }


    @Override
    public void handlerSettleRecord(Collection<String> businessKeys) {
        if (businessKeys.isEmpty()) {
            return;
        }
        List<Long> clientIds = businessKeys.stream().map(Long::valueOf).collect(Collectors.toList());
        Map<Long, Long> stockRiskExposureMap = SpringUtil.getBean(ClientService.class).clientStockRiskExposureMap(clientIds);
        // 过滤
        businessKeys.removeIf(businessKey -> {
            Long clientId = Long.valueOf(businessKey);
            Long stockRiskExposure = stockRiskExposureMap.get(clientId);
            return stockRiskExposure != null && stockRiskExposure > 0;
        });
        if (businessKeys.isEmpty()) {
            return;
        }
        for (String businessKey : businessKeys) {
            // 判断是不是在租合同
            boolean existInContract = super.isExistInContract(Long.valueOf(businessKey));
            if (!existInContract) {
                // 不存在在租合同
                draftService.lambdaUpdate()
                        .eq(CrClientDraft::getBusinessKey, businessKey)
                        .set(CrClientDraft::getReportState, ReportState.REPORTED.name())
                        .update();
            }
        }
    }


    @Override
    public Integer sort() {
        return 3;
    }
}
