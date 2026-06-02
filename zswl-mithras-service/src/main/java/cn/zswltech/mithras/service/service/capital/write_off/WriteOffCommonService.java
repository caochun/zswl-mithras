package cn.zswltech.mithras.service.service.capital.write_off;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.capital.write_off.bo.FundPledgeSupervisedBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/9/6/10:11
 * @description
 */
@Slf4j
@Service
public class WriteOffCommonService {

    @Resource
    private ClientService clientService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundFinancingPledgeInfoMapper financingPledgeInfoMapper;

    /**
     * 项目端-获取租金往来方
     */
    public Map<Long, ContractTenantry> getcontractTeantryMap(List<FinanceFlowRecord> financeFlowRecords) {
        if (financeFlowRecords.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ContractTenantry> contractTenantryList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .in(ContractTenantry::getRentConcatAccountName, financeFlowRecords.stream().map(FinanceFlowRecord::getOppunit).collect(Collectors.toList()))
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name()));
        if (contractTenantryList.isEmpty()) {
            return Collections.emptyMap();
        }
        return contractTenantryList.stream().collect(Collectors.toMap(ContractTenantry::getContractId, Function.identity(), (a, b) -> a));
    }

    /**
     * 根据合同编号找到合同编号与租金往来方的映射
     */
    public Map<String, String> getContractCode2RentAccountNameMap(Collection<String> contractCodeCollection) {
        if (CollUtil.isEmpty(contractCodeCollection)) {
            return Collections.emptyMap();
        }
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractCode, contractCodeCollection));
        Map<String, String> resultMap = new HashMap<>();
        Map<String, Long> stringLongMap = contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, ContractBaseInfo::getId));
        Map<Long, String> longStringMap = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .in(ContractTenantry::getContractId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(ContractTenantry::getContractId, ContractTenantry::getRentConcatAccountName));
        stringLongMap.forEach((k, v) -> resultMap.put(longStringMap.get(v), k));
        return resultMap;
    }

    /**
     * 通用方法-通过合同列表找到融资的监管情况
     */
    public Map<String, FundPledgeSupervisedBO> getFundSupervisedBoMap(List<ContractBaseInfo> contractBaseInfoList) {
        if (CollUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyMap();
        }

        List<FundPledgeSupervisedBO> fundPledgeSupervisedBos = financingPledgeInfoMapper.getFundSupervisedBo(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        if (CollUtil.isEmpty(fundPledgeSupervisedBos)) {
            return Collections.emptyMap();
        }
        // 产品说的一个合同只会被一个融资监管
        return fundPledgeSupervisedBos.stream().collect(Collectors.toMap(FundPledgeSupervisedBO::getAccountNumber, Function.identity(), (a, b) -> a));
    }

    /**
     * 项目端-收款通用核销
     */
    public void projectCollectionWriteOff() {
    }

}
