package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class CorpAddressInfoLibHandlerImpl extends ClientLibAbstractHandler<CorpAddressInfoLib, CorpAddressInfo, CorpAddressInfoListRSP> {

    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    protected CorpAddressInfoLib entity2Lib(CorpAddressInfo corpAddressInfo) {
        CorpAddressInfoLib corpAddressInfoLib = BeanUtil.copyProperties(corpAddressInfo, CorpAddressInfoLib.class);
        return corpAddressInfoLib;
    }

    @Override
    protected CorpAddressInfo lib2Entity(CorpAddressInfoLib corpAddressInfoLib) {
        CorpAddressInfo corpAddressInfo = BeanUtil.copyProperties(corpAddressInfoLib, CorpAddressInfo.class);
        return corpAddressInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_ADDRESS;
    }

    @Override
    public void validateData(Client client) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
    }

    @Override
    protected CorpAddressInfoListRSP lib2Rsp(CorpAddressInfoLib f) {
        return lib2RspList(ListUtil.toList(f)).get(0);
    }

    @Override
    protected List<CorpAddressInfoListRSP> lib2RspList(List<CorpAddressInfoLib> fList) {
        List<CorpAddressInfoListRSP> list = BeanUtil.copyToList(fList, CorpAddressInfoListRSP.class);
        //code to name
        Set<String> codeSet = new HashSet<>(list.size() * 4);
        list.forEach(e -> {
            codeSet.add(e.getCountry());
            codeSet.add(e.getProvince());
            codeSet.add(e.getCity());
            codeSet.add(e.getDistrict());
        });
        if (!codeSet.isEmpty()) {
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, codeSet))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
            list.forEach(e -> {
                e.setCountryName(nameMap.get(e.getCountry()));
                e.setProvinceName(nameMap.get(e.getProvince()));
                e.setCityName(nameMap.get(e.getCity()));
                e.setDistrictName(nameMap.get(e.getDistrict()));
            });
        }
        return list;
    }

    @Override
    public List<CorpAddressInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
