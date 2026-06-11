package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.CorpAddressInfoLibService;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpAddressInfoLibHandlerImpl;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpAddressInfoLibDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpAddressInfoLibServiceImpl extends ServiceImpl<CorpAddressInfoLibMapper, CorpAddressInfoLib> implements CorpAddressInfoLibService {


    @Resource
    private CorpAddressInfoLibHandlerImpl addressInfoLibHandler;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;


    @Override
    public PageR<CorpAddressInfoListRSP> list(CorpAddressInfoListREQ req) {
        Page<CorpAddressInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpAddressInfoLib>lambdaQuery().eq(CorpAddressInfoLib::getClientId, req.getClientId())
                        .eq(CorpAddressInfoLib::getVersion, req.getVersion())
                        .orderByDesc(CorpAddressInfoLib::getUpdateTime)
        );

        return PageR.of(addressInfoLibHandler.actualLib2RspList(dataPage.getRecords()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public List<CorpAddressInfoLib> listAddressByClientIdAndType(Set<Long> clientIds, String corpAddressType){
        return corpAddressInfoLibMapper.listAddressByClientIdAndType(clientIds, corpAddressType);
    }

    @Override
    public List<Long> getClientIdsByProvince(CorpAddressInfoLibDto dto) {
        Map<String, List<CorpAddressInfoLib>> all = baseMapper.listNewestCorpAddress(dto).stream().collect(Collectors.groupingBy(CorpAddressInfoLib::getAddressType));
        List<CorpAddressInfoLib> workAddress = all.getOrDefault(CorpAddressType.WORK_ADDRESS.name(), new ArrayList<>());
        List<CorpAddressInfoLib> registryAddress = all.getOrDefault(CorpAddressType.REGISTRY_ADDRESS.name(), new ArrayList<>());
        Map<Long, CorpAddressInfoLib> workAddressMap = workAddress.stream().collect(Collectors.toMap(ClientBaseModel::getClientId, item -> item, (a, b) -> a));
        Map<Long, CorpAddressInfoLib> registryAddressMap = registryAddress.stream().collect(Collectors.toMap(ClientBaseModel::getClientId, item -> item, (a, b) -> a));
        registryAddressMap.forEach((k, v) -> {
            if (!workAddressMap.containsKey(k)) {
                //那就要去查一下是不是该企业没有办公地址
                Integer count = baseMapper.selectCount(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                        .eq(CorpAddressInfoLib::getVersion, v.getVersion())
                        .eq(CorpAddressInfoLib::getAddressType, CorpAddressType.WORK_ADDRESS.name())
                        .eq(ClientBaseModel::getClientId, v.getClientId()));
                // 如果没有办公地址，就取注册地址
                if (count == 0) {
                    workAddressMap.put(k, v);
                }
            }
        });
        return new ArrayList<>(workAddressMap.keySet());
    }

    @Override
    public List<CorpAddressInfoLib> listNewestAddressByClientId(Set<Long> clientIds) {
        return null;
    }
}
