package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.service.client.ClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/3 10:09
 */
public class InitProvinceOfAffiliationTest extends ApplicationTest {

    @Resource
    private ClientService clientService;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;

    @Test
    public void test() {
        System.out.println("单测测试");
        List<Client> clients = clientService.list();
        clients.parallelStream().forEach(client -> {
            List<CorpAddressInfo> addressInfos;
            if (client.getNewestVersion() == null) {
                addressInfos = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
                        .eq(ClientBaseModel::getClientId, client.getId()));
            } else {
                List<CorpAddressInfoLib> corpAddressInfoLibs = corpAddressInfoLibMapper.selectList(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                        .eq(ClientBaseModel::getClientId, client.getId())
                        .eq(CorpAddressInfoLib::getVersion, client.getNewestVersion()));
                addressInfos = corpAddressInfoLibs.stream().map(corpAddressInfoLib -> {
                    CorpAddressInfo corpAddressInfo = new CorpAddressInfo();
                    BeanUtils.copyProperties(corpAddressInfoLib, corpAddressInfo);
                    return corpAddressInfo;
                }).collect(Collectors.toList());
            }
            Map<String, List<CorpAddressInfo>> addressMap = addressInfos.stream().collect(Collectors.groupingBy(CorpAddressInfo::getAddressType));
            String province = null;
            if (addressMap.containsKey(CorpAddressType.WORK_ADDRESS.name())) {
                for (CorpAddressInfo workAddress : addressMap.get(CorpAddressType.WORK_ADDRESS.name())) {
                    province = workAddress.getProvince();
                    if (province != null) {
                        break;
                    }
                }
            }
            if (province == null) {
                if (addressMap.containsKey(CorpAddressType.REGISTRY_ADDRESS.name())) {
                    CorpAddressInfo registryAddress = addressMap.get(CorpAddressType.REGISTRY_ADDRESS.name()).get(0);
                    province = registryAddress.getProvince();
                }
            }
            client.setProvinceOfAffiliation(province);
        });
        clientService.updateBatchById(clients);
    }
}
