package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.customer.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.customer.versioning.CorpContactInfoLibService;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractTenantryLibHandler
        extends ContractLibAbstractHandler<ContractTenantryLib, ContractTenantry, ContractTenantryListRSP> {

    @Resource
    private CorpContactInfoLibService corpContactInfoLibService;

    @Override
    protected ContractTenantryLib entity2Lib(ContractTenantry f) {
        return BeanUtil.copyProperties(f, ContractTenantryLib.class);
    }

    @Override
    protected ContractTenantry lib2Entity(ContractTenantryLib t) {
        return BeanUtil.copyProperties(t, ContractTenantry.class);
    }

    @Override
    protected ContractTenantryListRSP lib2Rsp(ContractTenantryLib f) {
        return lib2RspList(ListUtil.toList(f)).get(0);
    }

    @Override
    protected List<ContractTenantryListRSP> lib2RspList(List<ContractTenantryLib> fList) {
        List<ContractTenantryListRSP> rspList = BeanUtil.copyToList(fList, ContractTenantryListRSP.class);
        /*Map<Long, Long> contactLibIdMap = rspList.stream().filter(r -> Objects.nonNull(r.getContactId())).collect(Collectors.toMap(ContractTenantryListRSP::getId, ContractTenantryListRSP::getContractId));
        if (CollectionUtils.isNotEmpty(contactLibIdMap.values())) {
            Map<Long, CorpContactInfoLib> contactLibMap = contactInfoLibMapper.selectBatchIds(new HashSet<>(contactLibIdMap.values())).stream().collect(Collectors.toMap(CorpContactInfoLib::getId, c -> c));
            for (ContractTenantryListRSP rsp : rspList) {
                rsp.setContactName(Optional.ofNullable(contactLibIdMap.get(rsp.getId())).map(contactLibId -> contactLibMap.get(contactLibId)).map(CorpContactInfoLib::getName).orElse(null));
            }
        }*/
        //这里ID为编辑区id，需找到最新的版本数据,暂时这样处理，后续看是否可批量
        for(ContractTenantryListRSP rsp : rspList){
            if (ObjectUtil.isNotNull(rsp.getContactId())){
//                rsp.setContactName(handler.queryLatestDataByLibId(rsp.getContactId()).getName());
                CorpContactInfoLib corpContactInfoLib = corpContactInfoLibService.getById(rsp.getContactId());
                if (Objects.nonNull(corpContactInfoLib)) {
                    rsp.setContactName(corpContactInfoLib.getName());
                }
            }
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setClientId(rsp.getLesseeId());
            clientInfo.setClientName(rsp.getLesseeName());
            clientInfo.setStockRiskExposure(rsp.getStockRiskExposure());
            rsp.setLesseeClient(clientInfo);
        }
        return rspList;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.TENANTRY;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
