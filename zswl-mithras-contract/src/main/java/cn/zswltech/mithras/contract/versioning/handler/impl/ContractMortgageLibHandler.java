package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.contract.convert.contract.ContractMortgageConverter;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.foundation.port.ClientInfoResolver;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
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
    private ClientInfoResolver clientInfoResolver;

    @Autowired
    private MaterialsListQueryService materialsListQueryService;

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
        MaterialsList byId = materialsListQueryService.getById(rsp.getFileId());
        if(ObjectUtil.isNotEmpty(byId)){
            rsp.setFileName(byId.getFilename());
            rsp.setFilePath(byId.getFilePath());
        }
        rsp.setMortgageInfo(clientInfoResolver.clientId2Client(rsp.getMortgageIds()).values().stream().collect(Collectors.toList()));
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    protected List<ContractMortgageListRSP> lib2RspList(List<ContractMortgageLib> fList) {
        List<ContractMortgageListRSP> rspList = fList.stream().map(f -> converter.entityToRSP(f)).collect(Collectors.toList());
        Map<Long, ClientInfo> clientInfoMap = clientInfoResolver.clientId2Client(rspList.stream().map(ContractMortgageListRSP::getMortgageIds).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet()));
        List<Long> materialsIdList = fList.stream().map(ContractMortgage::getFileId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, MaterialsList> materialsListMap = materialsIdList == null || materialsIdList.isEmpty() ? new HashMap<>() : materialsListQueryService.getByIds(materialsIdList).stream().collect(Collectors.toMap(MaterialsList::getId, m -> m));
        for (ContractMortgageListRSP rsp : rspList) {
            if (rsp.getMortgageIds() != null && !rsp.getMortgageIds().isEmpty()) {
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
