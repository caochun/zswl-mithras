package cn.zswltech.mithras.service.job.data_init;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClientLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanClientLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description 租后检查初始化任务-初始化担保人信息
 * @since 2025/9/21 16:25
 **/
@Slf4j
@Component
public class AfterLeaseCheckInitJob {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckPlanClientLibService libService;

    @XxlJob("afterLeaseCheckInitGuarantorJob")
    public void afterLeaseCheckInitGuarantorJob() {
        List<NewAfterLeaseCheckPlanClient> list = afterLeaseCheckPlanClientService.list();
        Set<Long> collect = list.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toSet());
        // 获取所有合同信息, 根据lesseeId 查找所有关联的contractIds
        Map<Long, Set<Long>> map = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .in(ContractTenantry::getLesseeId, collect)
                        .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name()))
                .stream().collect(Collectors.groupingBy(ContractTenantry::getLesseeId, Collectors.mapping(ContractTenantry::getContractId, Collectors.toSet())));
        // 获取所有担保人信息
        Map<Long, List<ContractGuarantor>> refMap = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                .in(ContractGuarantor::getContractId, map.values().stream().flatMap(Set::stream).collect(Collectors.toSet())))
                .stream().collect(Collectors.groupingBy(ContractGuarantor::getContractId));

        // 开始更新数据
        for (NewAfterLeaseCheckPlanClient planClient : list) {
            Set<Long> contractIds = map.get(planClient.getClientId());
            if (contractIds != null) {
                for (Long contractId : contractIds) {
                    List<ContractGuarantor> contractGuarantors = refMap.get(contractId);
                    if (contractGuarantors != null) {
                        Set<Long> guarantorIds = new HashSet<>();
                        for (ContractGuarantor contractGuarantor : contractGuarantors) {
                            // 获取所有关联的担保人信息
                            List<Long> longs = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
                            guarantorIds.addAll(longs);
                        }
                        Map<Long, String> clientedId2Name = id2NameService.clientId2Name(guarantorIds);
                        NewAfterLeaseCheckPlanClient checkPlanClient = new NewAfterLeaseCheckPlanClient();
                        checkPlanClient.setGuaranteeIds(JSONUtil.toJsonStr(guarantorIds));
                        checkPlanClient.setGuaranteeNames(JSONUtil.toJsonStr(new HashSet<>(clientedId2Name.values())));
                        checkPlanClient.setId(planClient.getId());
                        afterLeaseCheckPlanClientService.updateById(checkPlanClient);

                        // 更新版本表
                        libService.lambdaUpdate()
                                .eq(NewAfterLeaseCheckPlanClientLib::getOriginId, planClient.getId())
                                .set(NewAfterLeaseCheckPlanClientLib::getGuaranteeIds, JSONUtil.toJsonStr(guarantorIds))
                                .set(NewAfterLeaseCheckPlanClientLib::getGuaranteeNames, JSONUtil.toJsonPrettyStr(new HashSet<>(clientedId2Name.values())))
                                .update();
                    }
                }
            }
        }
    }
}
