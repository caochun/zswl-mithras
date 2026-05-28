package cn.zswltech.mithras.service.service.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseListRSP;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.mapper.model.client.NormalSpouse;
import cn.zswltech.mithras.service.mapper.model.client.NormalSpouseLib;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NormalSpouseLibHandlerImpl extends ClientLibAbstractHandler<NormalSpouseLib, NormalSpouse, NormalSpouseListRSP> {

    @Resource
    private ClientService clientService;

    @Override
    protected NormalSpouseLib entity2Lib(NormalSpouse normalSpouse) {
        NormalSpouseLib normalSpouseLib = BeanUtil.copyProperties(normalSpouse, NormalSpouseLib.class);
        return normalSpouseLib;
    }

    @Override
    protected NormalSpouse lib2Entity(NormalSpouseLib normalSpouseLib) {
        NormalSpouse normalSpouse = BeanUtil.copyProperties(normalSpouseLib, NormalSpouse.class);
        return normalSpouse;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.NORMAL.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.NORMAL_SPOUSE;
    }

    @Override
    protected NormalSpouseListRSP lib2Rsp(NormalSpouseLib f) {
        NormalSpouseListRSP rsp = BeanUtil.copyProperties(f, NormalSpouseListRSP.class);
        rsp.setId(f.getOriginId());
        Long spouseClientId = clientService.getNormalByCertList(Arrays.asList(f.getCertNumber())).stream()
                .findFirst()
                .map(Client::getId)
                .orElse(null);
        rsp.setSpouseClientId(spouseClientId);
        return rsp;
    }

    @Override
    public List<NormalSpouse> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
