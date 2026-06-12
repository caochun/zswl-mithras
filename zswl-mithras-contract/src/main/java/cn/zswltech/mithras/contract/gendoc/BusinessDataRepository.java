package cn.zswltech.mithras.contract.gendoc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.third.externaldata.environmentpenalty.mapper.EnvironmentPenaltyMapper;
import cn.zswltech.mithras.third.externaldata.environmentpenalty.model.EnvironmentPenalty;
import cn.zswltech.mithras.third.externaldata.tianyancha.mapper.*;
import cn.zswltech.mithras.third.externaldata.tianyancha.model.*;
import cn.zswltech.mithras.third.externaldata.zhongdeng.persistence.mapper.ZhongdengInfoMapper;
import cn.zswltech.mithras.third.externaldata.zhongdeng.persistence.model.ZhongdengInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.*;
import cn.zswltech.mithras.customer.mapper.corp.*;
import cn.zswltech.mithras.customer.mapper.lib.client.*;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.basedata.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/3
 * @description 业务数据仓库，用于复用数据获取接口
 */
@Slf4j
@Component
public class BusinessDataRepository implements InitializingBean {
    // 字典key
    private static final String DICTIONARY_KEY_ECONOMY_TYPE = "economyType";
    private static final String DICTIONARY_KEY_CURRENCY_TYPE = "currencyType";
    private static final String DICTIONARY_KEY_CERT_TYPE = "certType";
    private static final String DICTIONARY_KEY_CONTINUOUS_TYPE = "continuousType";
    private static final String DICTIONARY_KEY_ORG_TYPE = "orgType";
    // 通用字典
    private static final Map<String, List<GeneralDictionary>> GENERAL_DICTIONARY_CACHE_MAP = new HashMap<>(256);
    // 地址
    private static final Map<String, String> ADDRESS_CACHE_MAP = new HashMap<>();
    // 行业
    private static final Map<String, String> INDUSTRY_TYPE_CACHE_MAP = new HashMap<>();

    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpContactInfoLibMapper corpContactInfoLibMapper;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private CorpShareholderInfoLibMapper corpShareholderInfoLibMapper;
    @Resource
    private CorpRelatedEnterpriseLibMapper corpRelatedEnterpriseLibMapper;
    @Resource
    private TycLawSuitMapper tycLawSuitMapper;
    @Resource
    private TycMortgageInfoMapper tycMortgageInfoMapper;
    @Resource
    private TycEquityInfoMapper tycEquityInfoMapper;
    @Resource
    private TycPunishmentInfoMapper tycPunishmentInfoMapper;
    @Resource
    private EnvironmentPenaltyMapper environmentPenaltyMapper;
    @Resource
    private TycAbnormalMapper tycAbnormalMapper;
    @Resource
    private TycJudicialMapper tycJudicialMapper;
    @Resource
    private TycConsumptionRestrictionMapper tycConsumptionRestrictionMapper;
    @Resource
    private TycZhixingInfoMapper tycZhixingInfoMapper;
    @Resource
    private TycDishonestMapper tycDishonestMapper;
    @Resource
    private ZhongdengInfoMapper zhongdengInfoMapper;
    @Resource
    private ContractTenantryMapper contractTenantryMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractFactoringPriceMapper contractFactoringPriceMapper;
    @Resource
    private ContractAccountMapper contractAccountMapper;
    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractRentEstimateMapper contractRentEstimateMapper;
    @Resource
    private ContractLeaseItemMapper contractLeaseItemMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private NormalBaseInfoLibMapper normalBaseInfoLibMapper;
    @Resource
    private ContractMortgageItemMapper contractMortgageItemMapper;

    public Client getClient(Long clientId) {
        return clientMapper.selectById(clientId);
    }

    public Client getClientNotNull(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        return client != null ? client : new Client();
    }


    public Map<Long, NormalBaseInfoLib> getNormalBaseInfoMap(Collection<Long> clientIds) {
        Map<Long, Client> clientMap = this.getClientMap(clientIds);
        LambdaQueryWrapper<NormalBaseInfoLib> query = Wrappers.lambdaQuery();
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = Assert.notNull(entry.getValue(), () -> MithrasException.newException("客户不存在[" + entry.getKey() + "]"));
            String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
            query.or(true, innerQuery -> innerQuery.eq(ClientBaseModel::getClientId, client.getId()).eq(NormalBaseInfoLib::getVersion, newestVersion));
        }
        List<NormalBaseInfoLib> list = normalBaseInfoLibMapper.selectList(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, NormalBaseInfoLib> map = new LinkedHashMap<>();
        for (NormalBaseInfoLib normalBaseInfo : list) {
            map.put(normalBaseInfo.getClientId(), normalBaseInfo);
        }
        return map;
    }

    public NormalBaseInfoLib getNormalBaseInfo(Long clientId) {
        Client client = Assert.notNull(clientMapper.selectById(clientId), () -> MithrasException.newException("客户不存在[" + clientId + "]"));
        String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
        LambdaQueryWrapper<NormalBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NormalBaseInfoLib::getVersion, newestVersion);
        return normalBaseInfoLibMapper.selectOne(query);
    }

    public List<ContractGuarantor> listContractGuarantor(Long contractId) {
        LambdaQueryWrapper<ContractGuarantor> query = new LambdaQueryWrapper<>();
        query.eq(ContractGuarantor::getContractId, contractId);
        return contractGuarantorMapper.selectList(query);
    }

    public List<ContractAccount> listContractAccount(Long contractId, ContractAccountUseEnum accountUse) {
        LambdaQueryWrapper<ContractAccount> query = new LambdaQueryWrapper<>();
        query.eq(ContractAccount::getContractId, contractId);
        query.eq(ContractAccount::getAccountUse, accountUse.name());
        return contractAccountMapper.selectList(query);
    }

    public List<ContractRentEstimate> listContractRentEstimate(Long contractId) {
        LambdaQueryWrapper<ContractRentEstimate> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentEstimate::getContractId, contractId);
        return contractRentEstimateMapper.selectList(query);
    }

    public List<ContractRentActual> listContractRentActual(Long contractId) {
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getContractId, contractId);
        return contractRentActualMapper.selectList(query);
    }

    public List<ContractLeaseItem> listContractLeaseItem(Long contractId) {
        LambdaQueryWrapper<ContractLeaseItem> query = new LambdaQueryWrapper<>();
        query.eq(ContractLeaseItem::getContractId, contractId);
        return contractLeaseItemMapper.selectList(query);
    }

    public List<ContractMortgageItem> listContractMortgageItem(Long mortgageId) {
        LambdaQueryWrapper<ContractMortgageItem> query = new LambdaQueryWrapper<>();
        query.eq(ContractMortgageItem::getMortgageId, mortgageId);
        return contractMortgageItemMapper.selectList(query);
    }

    /**
     * 获取合同实际租金表的数据
     * 获取第一张借据对应的数据
     *
     * @param contractId
     * @return
     */
    public List<ContractRentActual> listFirstContractRentActual(Long contractId) {
        ContractReceipt firstContractReceipt = contractReceiptMapper.selectOne(Wrappers.<ContractReceipt>lambdaQuery()
                .eq(ContractReceipt::getContractId, contractId)
                .orderByAsc(ContractReceipt::getCreateTime)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(firstContractReceipt)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getContractId, contractId);
        query.eq(ContractRentActual::getReceiptId, firstContractReceipt.getId());
        return contractRentActualMapper.selectList(query);
    }

    public ContractLeasePrice getContractLeasePrice(Long contractId) {
        LambdaQueryWrapper<ContractLeasePrice> query = new LambdaQueryWrapper<>();
        query.eq(ContractLeasePrice::getContractId, contractId);
        return contractLeasePriceMapper.selectOne(query);
    }

    public ContractFactoringPrice getContractFactoringPrice(Long contractId) {
        LambdaQueryWrapper<ContractFactoringPrice> query = new LambdaQueryWrapper<>();
        query.eq(ContractFactoringPrice::getContractId, contractId);
        return contractFactoringPriceMapper.selectOne(query);
    }

    public Map<Long, ContractTenantry> getContractTenantryMap(Long contractId) {
        List<ContractTenantry> contractTenantryList = contractTenantryMapper.selectList(Wrappers.<ContractTenantry>lambdaQuery().eq(ContractTenantry::getContractId, contractId));
        if (CollectionUtils.isEmpty(contractTenantryList)) {
            return Collections.emptyMap();
        }
        Map<Long, ContractTenantry> map = new LinkedHashMap<>();
        for (ContractTenantry contractTenantry : contractTenantryList) {
            map.put(contractTenantry.getLesseeId(), contractTenantry);
        }
        return map;
    }

    public Map<Long, List<ZhongdengInfo>> getZhongdengInfoMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<ZhongdengInfo> list = zhongdengInfoMapper.selectList(Wrappers.<ZhongdengInfo>lambdaQuery().in(ZhongdengInfo::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ZhongdengInfo::getClientId));
    }

    public Map<Long, List<TycDishonest>> getTycDishonestMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycDishonest> list = tycDishonestMapper.selectList(Wrappers.<TycDishonest>lambdaQuery().in(TycDishonest::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycDishonest::getClientId));
    }

    public Map<Long, List<TycZhixingInfo>> getTycZhixingInfoMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycZhixingInfo> list = tycZhixingInfoMapper.selectList(Wrappers.<TycZhixingInfo>lambdaQuery().in(TycZhixingInfo::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycZhixingInfo::getClientId));
    }

    public Map<Long, List<TycConsumptionRestriction>> getTycConsumptionRestrictionMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycConsumptionRestriction> list = tycConsumptionRestrictionMapper.selectList(Wrappers.<TycConsumptionRestriction>lambdaQuery().in(TycConsumptionRestriction::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycConsumptionRestriction::getClientId));
    }

    public Map<Long, List<TycJudicial>> getTycJudicialMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycJudicial> list = tycJudicialMapper.selectList(Wrappers.<TycJudicial>lambdaQuery().in(TycJudicial::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycJudicial::getClientId));
    }

    public Map<Long, List<TycAbnormal>> getTycAbnormalMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycAbnormal> list = tycAbnormalMapper.selectList(Wrappers.<TycAbnormal>lambdaQuery().in(TycAbnormal::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycAbnormal::getClientId));
    }

    public Map<Long, List<EnvironmentPenalty>> getEnvironmentPenaltyMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<EnvironmentPenalty> list = environmentPenaltyMapper.selectList(Wrappers.<EnvironmentPenalty>lambdaQuery().in(EnvironmentPenalty::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(EnvironmentPenalty::getClientId));
    }

    public Map<Long, List<TycPunishmentInfo>> geTycPunishmentMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycPunishmentInfo> list = tycPunishmentInfoMapper.selectList(Wrappers.<TycPunishmentInfo>lambdaQuery().in(TycPunishmentInfo::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycPunishmentInfo::getClientId));
    }

    public Map<Long, List<TycEquityInfo>> getTycEquityMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycEquityInfo> list = tycEquityInfoMapper.selectList(Wrappers.<TycEquityInfo>lambdaQuery().in(TycEquityInfo::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycEquityInfo::getClientId));
    }

    public Map<Long, List<TycMortgageInfo>> getTycMortgageMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycMortgageInfo> list = tycMortgageInfoMapper.selectList(Wrappers.<TycMortgageInfo>lambdaQuery().in(TycMortgageInfo::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TycMortgageInfo::getClientId));
    }

    public Map<Long, Integer> getTycLawSuitCountMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<TycLawSuit> list = tycLawSuitMapper.selectList(Wrappers.<TycLawSuit>lambdaQuery().in(TycLawSuit::getClientId, clientIds));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> map = new HashMap<>();
        for (TycLawSuit tycLawSuit : list) {
            Integer count = map.get(tycLawSuit.getClientId());
            if (Objects.isNull(count)) {
                count = 1;
                map.put(tycLawSuit.getClientId(), count);
            }
            map.put(tycLawSuit.getClientId(), count + 1);
        }
        return map;
    }

    public Map<Long, List<CorpRelatedEnterpriseLib>> getCorpRelatedEnterpriseMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Client> clientMap = this.getClientMap(clientIds);
        LambdaQueryWrapper<CorpRelatedEnterpriseLib> query = Wrappers.lambdaQuery();
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = Assert.notNull(entry.getValue(), () -> MithrasException.newException("客户不存在[" + entry.getKey() + "]"));
            String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
            query.or(true, innerQuery -> innerQuery.eq(ClientBaseModel::getClientId, client.getId()).eq(CorpRelatedEnterpriseLib::getVersion, newestVersion));
        }
        List<CorpRelatedEnterpriseLib> list = corpRelatedEnterpriseLibMapper.selectList(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
    }

    public Map<Long, List<CorpShareholderInfoLib>> getCorpShareholderMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Client> clientMap = this.getClientMap(clientIds);
        LambdaQueryWrapper<CorpShareholderInfoLib> query = Wrappers.lambdaQuery();
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = Assert.notNull(entry.getValue(), () -> MithrasException.newException("客户不存在[" + entry.getKey() + "]"));
            String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
            query.or(true, innerQuery -> innerQuery.eq(ClientBaseModel::getClientId, client.getId()).eq(CorpShareholderInfoLib::getVersion, newestVersion));
        }
        List<CorpShareholderInfoLib> list = corpShareholderInfoLibMapper.selectList(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
    }

    public CorpCommerceInfoLib getCorpCommerceInfo(Long clientId) {
        Client client = Assert.notNull(clientMapper.selectById(clientId), () -> MithrasException.newException("客户不存在[" + clientId + "]"));
        String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
        LambdaQueryWrapper<CorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(CorpCommerceInfoLib::getVersion, newestVersion);
        return corpCommerceInfoLibMapper.selectOne(query);
    }

    public Map<Long, CorpCommerceInfoLib> getCorpCommerceMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Client> clientMap = this.getClientMap(clientIds);
        LambdaQueryWrapper<CorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = Assert.notNull(entry.getValue(), () -> MithrasException.newException("客户不存在[" + entry.getKey() + "]"));
            String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
            query.or(true, innerQuery -> innerQuery.eq(ClientBaseModel::getClientId, client.getId()).eq(CorpCommerceInfoLib::getVersion, newestVersion));
        }
        List<CorpCommerceInfoLib> list = corpCommerceInfoLibMapper.selectList(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, CorpCommerceInfoLib> map = new HashMap<>();
        for (CorpCommerceInfoLib corpCommerceInfo : list) {
            map.put(corpCommerceInfo.getClientId(), corpCommerceInfo);
        }
        return map;
    }

    public UserDO getUser(Long userId) {
        return userDOMapper.selectByPrimaryKey(userId);
    }

    public Map<Long, UserDO> getUserMap(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<UserDO> list = userDOMapper.selectByIds(new ArrayList<>(ids));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, UserDO> map = new HashMap<>();
        for (UserDO userDO : list) {
            map.put(userDO.getId(), userDO);
        }
        return map;
    }

    public String getIndustryTypeNameFromLocalCache(String code) {
        return INDUSTRY_TYPE_CACHE_MAP.get(code);
    }

    public String getAddressNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        return ADDRESS_CACHE_MAP.get(code);
    }

    public String getEconomyTypeNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        GeneralDictionary generalDictionary = this.findSpecificOne(code, GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_ECONOMY_TYPE));
        return Optional.ofNullable(generalDictionary).map(GeneralDictionary::getDisplay).orElse("");
    }

    public List<GeneralDictionary> getEconomyTypeFromLocalCache() {
        List<GeneralDictionary> list = new ArrayList<>(GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_ECONOMY_TYPE));
        list.sort(Comparator.comparingInt(GeneralDictionary::getSort));
        return list;
    }

    public String getCurrencyTypeNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        GeneralDictionary generalDictionary = this.findSpecificOne(code, GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CURRENCY_TYPE));
        return Optional.ofNullable(generalDictionary).map(GeneralDictionary::getDisplay).orElse("");
    }

    public List<GeneralDictionary> getCurrencyTypeFromLocalCache() {
        List<GeneralDictionary> list = new ArrayList<>(GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CURRENCY_TYPE));
        list.sort(Comparator.comparingInt(GeneralDictionary::getSort));
        return list;
    }

    public String getCertTypeNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        GeneralDictionary generalDictionary = this.findSpecificOne(code, GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CERT_TYPE));
        return Optional.ofNullable(generalDictionary).map(GeneralDictionary::getDisplay).orElse("");
    }

    public List<GeneralDictionary> getCertTypeFromLocalCache() {
        List<GeneralDictionary> list = new ArrayList<>(GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CERT_TYPE));
        list.sort(Comparator.comparingInt(GeneralDictionary::getSort));
        return list;
    }

    public String getContinuousTypeNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        GeneralDictionary generalDictionary = this.findSpecificOne(code, GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CONTINUOUS_TYPE));
        return Optional.ofNullable(generalDictionary).map(GeneralDictionary::getDisplay).orElse("");
    }

    public List<GeneralDictionary> getContinuousTypeFromLocalCache() {
        List<GeneralDictionary> list = new ArrayList<>(GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_CONTINUOUS_TYPE));
        list.sort(Comparator.comparingInt(GeneralDictionary::getSort));
        return list;
    }

    public String getOrgTypeNameFromLocalCache(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        GeneralDictionary generalDictionary = this.findSpecificOne(code, GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_ORG_TYPE));
        return Optional.ofNullable(generalDictionary).map(GeneralDictionary::getDisplay).orElse("");
    }

    public List<GeneralDictionary> getOrgTypeFromLocalCache() {
        List<GeneralDictionary> list = new ArrayList<>(GENERAL_DICTIONARY_CACHE_MAP.get(DICTIONARY_KEY_ORG_TYPE));
        list.sort(Comparator.comparingInt(GeneralDictionary::getSort));
        return list;
    }

    public Map<Long, Client> getClientMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<Client> list = clientMapper.selectBatchIds(clientIds);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, Client> map = new HashMap<>();
        for (Client client : list) {
            map.put(client.getId(), client);
        }
        return map;
    }

    public List<Client> getClientList(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return new ArrayList<>();
        }
        List<Client> list = clientMapper.selectBatchIds(clientIds);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }


    public List<CorpAddressInfoLib> getCorpAddressInfo(Long clientId) {
        Client client = Assert.notNull(clientMapper.selectById(clientId), () -> MithrasException.newException("客户不存在[" + clientId + "]"));
        String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
        LambdaQueryWrapper<CorpAddressInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(CorpAddressInfoLib::getVersion, newestVersion);
        return corpAddressInfoLibMapper.selectList(query);
    }

    public Map<Long, List<CorpAddressInfoLib>> getCorpAddressMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Client> clientMap = this.getClientMap(clientIds);
        LambdaQueryWrapper<CorpAddressInfoLib> query = Wrappers.lambdaQuery();
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = Assert.notNull(entry.getValue(), () -> MithrasException.newException("客户不存在[" + entry.getKey() + "]"));
            String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
            query.or(true, innerQuery -> innerQuery.eq(ClientBaseModel::getClientId, client.getId()).eq(CorpAddressInfoLib::getVersion, newestVersion));
        }
        List<CorpAddressInfoLib> list = corpAddressInfoLibMapper.selectList(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
    }

    public List<CorpContactInfoLib> getCorpContactInfo(Long clientId) {
        Client client = Assert.notNull(clientMapper.selectById(clientId), () -> MithrasException.newException("客户不存在[" + clientId + "]"));
        String newestVersion = Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("没有找到最新版本的客户数据"));
        LambdaQueryWrapper<CorpContactInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(CorpContactInfoLib::getVersion, newestVersion);
        return corpContactInfoLibMapper.selectList(query);
    }

    public Map<Long, CorpContactInfoLib> getCorpContactInfoMap(Collection<Long> contactIds) {
        if (CollectionUtils.isEmpty(contactIds)) {
            return Collections.emptyMap();
        }
        Map<Long, CorpContactInfoLib> map = new HashMap<>();
        for (Long contactId : contactIds) {
            LambdaQueryWrapper<CorpContactInfoLib> query = Wrappers.lambdaQuery();
            query.eq(CorpContactInfoLib::getOriginId, contactId);
            query.eq(CorpContactInfoLib::getVersionType, VersionTypeConstants.NORMAL);
            query.orderByDesc(ClientBaseModel::getId);
            query.last(StringUtil.mysqlLimitOne());
            CorpContactInfoLib corpContactInfoLib = corpContactInfoLibMapper.selectOne(query);
            if (Objects.nonNull(corpContactInfoLib)) {
                map.put(contactId, corpContactInfoLib);
            }
        }
        return map;
    }

    public OrgDO getOrgById(Long orgId) {
        return orgDOMapper.queryByPrimaryKey(orgId);
    }

    public Map<Long, OrgDO> getOrgMap(Collection<Long> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return Collections.emptyMap();
        }
        List<OrgDO> list = orgDOMapper.selectByIds(new ArrayList<>(orgIds), null);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, OrgDO> map = new HashMap<>();
        for (OrgDO orgDO : list) {
            map.put(orgDO.getId(), orgDO);
        }
        return map;
    }

    @Override
    public void afterPropertiesSet() {
        this.refreshAddressLocalCache();
        this.refreshGeneralDictionaryLocalCache();
        this.refreshIndustryLocalCache();
    }

    public void refreshIndustryLocalCache() {
        INDUSTRY_TYPE_CACHE_MAP.clear();
        List<IndustryType> all = industryTypeMapper.selectList(Wrappers.lambdaQuery());
        for (IndustryType industryType : all) {
            INDUSTRY_TYPE_CACHE_MAP.put(industryType.getCode(), industryType.getDisplay());
        }
        log.info("init industry type local cache success. count: {}", INDUSTRY_TYPE_CACHE_MAP.size());
    }

    public void refreshAddressLocalCache() {
        ADDRESS_CACHE_MAP.clear();
        List<AddressDictionary> all = addressDictionaryMapper.selectList(Wrappers.lambdaQuery());
        for (AddressDictionary addressDictionary : all) {
            ADDRESS_CACHE_MAP.put(addressDictionary.getCode(), addressDictionary.getDisplay());
        }
        log.info("init address local cache success. count: {}", ADDRESS_CACHE_MAP.size());
    }

    public void refreshGeneralDictionaryLocalCache() {
        GENERAL_DICTIONARY_CACHE_MAP.clear();
        List<GeneralDictionary> all = generalDictionaryMapper.selectList(Wrappers.lambdaQuery());
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        Map<String, List<GeneralDictionary>> newMap = all.stream().collect(Collectors.groupingBy(GeneralDictionary::getDictKey));
        GENERAL_DICTIONARY_CACHE_MAP.putAll(newMap);
        log.info("init general dictionary local cache success. count: {}", GENERAL_DICTIONARY_CACHE_MAP.size());
    }

    private GeneralDictionary findSpecificOne(String code, List<GeneralDictionary> candidateList) {
        if (CollectionUtil.isEmpty(candidateList)) {
            return null;
        }
        for (GeneralDictionary generalDictionary : candidateList) {
            if (Objects.equals(code, generalDictionary.getCode())) {
                return generalDictionary;
            }
        }
        return null;
    }
}
