package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.service.convert.contract.ContractMortgageConverter;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.contract.archive.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractMortgageLibHandler
        extends ContractLibAbstractHandler<ContractMortgageLib, ContractMortgage, ContractMortgageListRSP> {

    @Autowired
    private ContractMortgageConverter converter;

    @Autowired
    private Id2NameService id2NameService;

    @Autowired
    private MaterialsListService materialsListService;

    @Override
    protected ContractMortgageLib entity2Lib(ContractMortgage f) {
        return BeanUtil.copyProperties(f, ContractMortgageLib.class);
    }

    @Override
    protected ContractMortgage lib2Entity(ContractMortgageLib t) {
        return BeanUtil.copyProperties(t, ContractMortgage.class);
    }

    @Override
    protected ContractMortgageListRSP lib2Rsp(ContractMortgageLib f) {
        ContractMortgageListRSP rsp = converter.entityToRSP(f);
        MaterialsList byId = materialsListService.getById(rsp.getFileId());
        if(ObjectUtil.isNotEmpty(byId)){
            rsp.setFileName(byId.getFilename());
            rsp.setFilePath(byId.getFilePath());
        }
        rsp.setMortgageInfo(id2NameService.clientId2CLient(rsp.getMortgageIds()).values().stream().collect(Collectors.toList()));
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    protected List<ContractMortgageListRSP> lib2RspList(List<ContractMortgageLib> fList) {
        List<ContractMortgageListRSP> rspList = fList.stream().map(f -> converter.entityToRSP(f)).collect(Collectors.toList());
        Map<Long, ClientInfo> clientInfoMap = id2NameService.clientId2CLient(rspList.stream().map(ContractMortgageListRSP::getMortgageIds).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet()));
        List<Long> materialsIdList = fList.stream().map(ContractMortgage::getFileId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, MaterialsList> materialsListMap = CollectionUtils.isEmpty(materialsIdList) ? new HashMap<>() : materialsListService.getByIds(materialsIdList).stream().collect(Collectors.toMap(MaterialsList::getId, m -> m));
        for (ContractMortgageListRSP rsp : rspList) {
            if (CollectionUtils.isNotEmpty(rsp.getMortgageIds())) {
                rsp.setMortgageInfo(rsp.getMortgageIds().stream().map(pId -> clientInfoMap.get(pId)).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            if (ObjectUtil.isNotEmpty(rsp.getFileId())) {
                MaterialsList byId = materialsListMap.get(rsp.getFileId());
                if (ObjectUtil.isNotEmpty(byId)) {
                    rsp.setFileName(byId.getFilename());
                    rsp.setFilePath(byId.getFilePath());
                }
            }
        }
        return rspList;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.MORTGAGE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
