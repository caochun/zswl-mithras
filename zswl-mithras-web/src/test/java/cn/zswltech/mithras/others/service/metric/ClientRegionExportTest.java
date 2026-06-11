package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpAddressInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/2 14:48
 */
public class ClientRegionExportTest extends ApplicationTest {

    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;

    @Test
    public void test() {
        Map<String, List<CorpAddressInfoLib>> all = corpAddressInfoLibMapper.listNewestCorpAddress(new CorpAddressInfoLibDto()).stream().collect(Collectors.groupingBy(CorpAddressInfoLib::getAddressType));
        List<CorpAddressInfoLib> workAddress = all.getOrDefault(CorpAddressType.WORK_ADDRESS.name(), new ArrayList<>());
        List<CorpAddressInfoLib> registryAddress = all.getOrDefault(CorpAddressType.REGISTRY_ADDRESS.name(), new ArrayList<>());
        Map<Long, CorpAddressInfoLib> workAddressMap = workAddress.stream().collect(Collectors.toMap(ClientBaseModel::getClientId, item -> item, (a, b) -> a));
        Map<Long, CorpAddressInfoLib> registryAddressMap = registryAddress.stream().collect(Collectors.toMap(ClientBaseModel::getClientId, item -> item, (a, b) -> a));
        registryAddressMap.forEach((k, v) -> {
            if (!workAddressMap.containsKey(k)) {
                //那就要去查一下是不是该企业没有办公地址
                Integer count = corpAddressInfoLibMapper.selectCount(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                        .eq(CorpAddressInfoLib::getVersion, v.getVersion())
                        .eq(CorpAddressInfoLib::getAddressType, CorpAddressType.WORK_ADDRESS.name())
                        .eq(ClientBaseModel::getClientId, v.getClientId()));
                // 如果没有办公地址，就取注册地址
                if (count == 0) {
                    workAddressMap.put(k, v);
                }
            }
        });


        RemainingPrincipalQueryDto remainingPrincipalQueryDto = new RemainingPrincipalQueryDto();
        remainingPrincipalQueryDto.setClientIds(workAddressMap.keySet());
        remainingPrincipalQueryDto.setEndDate(LocalDate.of(2023, 4, 30));
        Map<Long, Long> remaining = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(remainingPrincipalQueryDto);


        Map<Long, String> clientId2Name = id2NameService.clientId2Name(workAddressMap.keySet());

        List<ClientRegion> clientRegions = new ArrayList<>();
        for (Long clientId : workAddressMap.keySet()) {
            ClientRegion clientRegion = new ClientRegion();
            clientRegion.setClientName(clientId2Name.get(clientId));
            clientRegion.setRegion(workAddressMap.getOrDefault(clientId, new CorpAddressInfoLib()).getProvince());
            String remainingPrin = new BigDecimal(remaining.getOrDefault(clientId, 0L)).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString();
            clientRegion.setRemainingPrincipal(remainingPrin);
            clientRegions.add(clientRegion);
        }
        String s = JSON.toJSONString(clientRegions);
        System.out.println(s);
    }

    @Data
    @Accessors
    public static class ClientRegion {
        private String clientName;
        private String region;
        private String remainingPrincipal;
    }

}
