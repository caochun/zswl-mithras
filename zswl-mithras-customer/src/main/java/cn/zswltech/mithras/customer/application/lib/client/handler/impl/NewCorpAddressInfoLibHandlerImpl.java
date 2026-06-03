package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.client.addressinfo.NewCorpAddressInfoListRSP;
import cn.zswltech.mithras.customer.domain.constant.LackDataMsg;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.domain.enums.InfoModule;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.others.LackDataException;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import cn.zswltech.mithras.customer.application.validator.CorpAddressInfoValidator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class NewCorpAddressInfoLibHandlerImpl extends ClientLibAbstractHandler<NewCorpAddressInfoLib, NewCorpAddressInfo, NewCorpAddressInfoListRSP> {

    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    protected NewCorpAddressInfoLib entity2Lib(NewCorpAddressInfo newCorpAddressInfo) {
        NewCorpAddressInfoLib newCorpAddressInfoLib = BeanUtil.copyProperties(newCorpAddressInfo, NewCorpAddressInfoLib.class);
        return newCorpAddressInfoLib;
    }

    @Override
    protected NewCorpAddressInfo lib2Entity(NewCorpAddressInfoLib newCorpAddressInfoLib) {
        NewCorpAddressInfo newCorpAddressInfo = BeanUtil.copyProperties(newCorpAddressInfoLib, NewCorpAddressInfo.class);
        return newCorpAddressInfo;
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
        Long userId = AccountUtil.getLoginInfo().getId();
        List<NewCorpAddressInfo> dataList = draftMapper.selectList(Wrappers.<NewCorpAddressInfo>lambdaQuery()
                .eq(NewCorpAddressInfo::getClientId, client.getId())
                .eq(NewCorpAddressInfo::getUserId, userId));
        // 注册地址、办公必须要有数据
        Set<String> addressTypeSet = dataList.stream()
                .map(CorpAddressInfo::getAddressType)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        errLackData(!addressTypeSet.contains(CorpAddressType.REGISTRY_ADDRESS.name()), LackDataMsg.ADDRESS_REGISTRY);
        errLackData(!addressTypeSet.contains(CorpAddressType.WORK_ADDRESS.name()), LackDataMsg.ADDRESS_WORK_ADDRESS);
        dataList.forEach(CorpAddressInfoValidator::validate);
    }

    @Override
    protected NewCorpAddressInfoListRSP lib2Rsp(NewCorpAddressInfoLib f) {
        return lib2RspList(ListUtil.toList(f)).get(0);
    }

    @Override
    protected List<NewCorpAddressInfoListRSP> lib2RspList(List<NewCorpAddressInfoLib> fList) {
        List<NewCorpAddressInfoListRSP> list = BeanUtil.copyToList(fList, NewCorpAddressInfoListRSP.class);
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
    public List<NewCorpAddressInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

    private void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }

}
