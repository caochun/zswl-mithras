package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientRiskFactorModifyREQ;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientRiskFactorRSP;
import cn.zswltech.mithras.dto.assetclassify.RiskFactorWrapper;
import cn.zswltech.mithras.dto.assetclassify.WithdrawalRatioWrapper;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyClientRiskFactorTemplateService;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyClientRiskFactorEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientRiskFactorTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 18:43
 */
@Service
public class AssetClassifyClientRiskFactorService {

    @Resource
    private AssetClassifyClientRiskFactorTemplateService assetClassifyClientRiskFactorTemplateService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    private static final String configKey = "express_clients";

    public List<AssetClassifyClientRiskFactorRSP> listRiskFactor(SinglePkREQ req) {
        AssetClassifyClient classifyClient = assetClassifyClientService.getById(req.getId());
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("记录不存在！");
        }
        String riskFactor = classifyClient.getRiskFactor();
        List<RiskFactorWrapper> riskFactorWrappers = JSON.parseArray(riskFactor, RiskFactorWrapper.class);
        if (CollectionUtil.isEmpty(riskFactorWrappers)) {
            // 兼容老数据
            return Collections.emptyList();
        }
        LambdaQueryWrapper<AssetClassifyClientRiskFactorTemplate> query = Wrappers.lambdaQuery();
        List<AssetClassifyClientRiskFactorRSP> rspList = new ArrayList<>();

        // if riskFactorWrappers.size()>28 (历史数据总共28条) 则是新版因子，否则是历史因子
        if (riskFactorWrappers.size()>28) {
            // 资产五级分类优化：根据不同租赁类型与所属部门，展示不同的风险因子

            // 获取合同租赁类型
            String provisions = classifyClient.getProvisions();
            if (ObjectUtil.isEmpty(provisions)) {
                return Lists.emptyList();
            }
            List<WithdrawalRatioWrapper> wrappers = JSON.parseArray(provisions, WithdrawalRatioWrapper.class);
            Set<Long> contractIds = wrappers.stream()
                    .map(WithdrawalRatioWrapper::getContractId).collect(Collectors.toSet());
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractIds);

            // 1. 判断是否是特殊客户(即非航运部门但是有航运业务)
            //List<String> specialCompanies = Arrays.asList(companyString.split(","));
            Set<String> specialCompanies = getClientsConfig(configKey);
            if (specialCompanies.contains(classifyClient.getClientName())){
                // 1.1 判断租赁类型是否为经营性租赁
                if (contractBaseInfos.get(0).getLeaseType().equals(LeaseType.jyx_zu.name())){
                    query.eq(AssetClassifyClientRiskFactorTemplate::getRiskFactorType, AssetClassifyClientRiskFactorEnum.OPERATION_LEASE.name());
                }else {
                    // 1.2 按照航运部门,展示为空
                    return Lists.emptyList();
                }
            }else {
                // 2. 非特殊用户判断租赁类型是否为经营性租赁
                if (contractBaseInfos.get(0).getLeaseType().equals(LeaseType.jyx_zu.name())){
                    query.eq(AssetClassifyClientRiskFactorTemplate::getRiskFactorType, AssetClassifyClientRiskFactorEnum.OPERATION_LEASE.name());
                }else {
                    // 3. 判断所属部门是否为航运
                    Map<Long, String> deptMap = id2NameService.deptId2Name(Collections.singletonList(classifyClient.getBelongDeptId()));
                    if (deptMap.get(classifyClient.getBelongDeptId()).equals("航运业务部")){
                        return Lists.emptyList();
                    }
                    query.eq(AssetClassifyClientRiskFactorTemplate::getRiskFactorType, AssetClassifyClientRiskFactorEnum.NON_SHIPPING.name());
                }
            }
        }else {
            query.eq(AssetClassifyClientRiskFactorTemplate::getRiskFactorType, AssetClassifyClientRiskFactorEnum.HISTORY.name());
        }

        query.orderByAsc(AssetClassifyClientRiskFactorTemplate::getId);
        List<AssetClassifyClientRiskFactorTemplate> RiskFactorList = assetClassifyClientRiskFactorTemplateService.list(query);
        // 构建返回体
        Map<Long, RiskFactorWrapper> factorMap = riskFactorWrappers.stream().collect(Collectors.toMap(RiskFactorWrapper::getTemplateId, e -> e, (a, b) -> a));
        for (AssetClassifyClientRiskFactorTemplate template : RiskFactorList) {
            AssetClassifyClientRiskFactorRSP rsp = new AssetClassifyClientRiskFactorRSP();
            buildFactorRSP(rsp,factorMap,template);
            rspList.add(rsp);
        }
        return rspList;
    }

    public void modifyRiskFactorBatch(AssetClassifyClientRiskFactorModifyREQ req) {
        AssetClassifyClient classifyClient = assetClassifyClientService.getById(req.getId());
        if (ObjectUtil.isEmpty(classifyClient)) {
            throw new MithrasException("记录不存在！");
        }
        classifyClient.setRiskFactor(JSON.toJSONString(req.getRiskFactors()));
        assetClassifyClientService.updateById(classifyClient);
    }

    /**
     * 构建返回值
     */
    private void buildFactorRSP(AssetClassifyClientRiskFactorRSP rsp,
                                Map<Long, RiskFactorWrapper> factorMap,
                                AssetClassifyClientRiskFactorTemplate template){
        // riskFactorWrappers转成map
        if (factorMap.containsKey(template.getId())){
            rsp.setTemplateId(factorMap.get(template.getId()).getTemplateId());
            rsp.setHasRisk(factorMap.get(template.getId()).getHasRisk());
            rsp.setRemark(factorMap.get(template.getId()).getRemark());
        }
        rsp.setType(template.getType());
        rsp.setRiskFactor(template.getRiskFactor());
    }

    /**
     * 获取配置的特殊客户
     */
    private Set<String> getClientsConfig(String configKey){
        Set<String> clients = new HashSet<>();
        SystemConfig config = getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, configKey)
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        String configValue = config.getConfigValue();
        if (Objects.nonNull(configValue) && !configValue.trim().isEmpty()) {
            clients = new HashSet<>(JSON.parseArray(configValue, String.class));
        }
        return clients;
    }
}


