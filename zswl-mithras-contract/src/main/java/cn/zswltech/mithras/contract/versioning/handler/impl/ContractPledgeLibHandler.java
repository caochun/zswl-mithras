package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.contract.convert.contract.ContractPledgeConverter;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.foundation.port.ClientInfoResolver;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractPledgeLibHandler
        extends ContractLibAbstractHandler<ContractPledgeLib, ContractPledge, ContractPledgeListRSP> {

    private static final String PLEDGE_ITEM_FILE_TYPE = "PLEDGE";

    @Autowired
    private ContractPledgeConverter converter;

    @Autowired
    private ClientInfoResolver clientInfoResolver;

    @Resource
    private MaterialsListQueryService materialsListQueryService;

    @Override
    protected ContractPledgeLib entity2Lib(ContractPledge f) {
        return BeanUtil.copyProperties(f, ContractPledgeLib.class);
    }

    @Override
    protected ContractPledge lib2Entity(ContractPledgeLib t) {
        return BeanUtil.copyProperties(t, ContractPledge.class);
    }

    @Override
    protected ContractPledgeListRSP lib2Rsp(ContractPledgeLib f) {
        return lib2RspList(ListUtil.toList(f)).get(0);
    }

    @Override
    protected List<ContractPledgeListRSP> lib2RspList(List<ContractPledgeLib> fList) {
        List<ContractPledgeListRSP> rspList = fList.stream().map(f -> converter.entityToRSP(f)).collect(Collectors.toList());
        Map<Long, Long> originIdMap = fList.stream().collect(Collectors.toMap(ContractPledgeLib::getId, ContractPledgeLib::getOriginId));
        Map<Long, ClientInfo> clientInfoMap = clientInfoResolver.clientId2Client(rspList.stream().map(ContractPledgeListRSP::getPledgeIds).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet()));
        Map<Long, List<MaterialsList>> materialsListMap = materialsListQueryService.list("CONTRACT", Collections.singletonList(PLEDGE_ITEM_FILE_TYPE), fList.stream().map(ContractPledgeLib::getOriginId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(MaterialsList::getMainId));
        for (ContractPledgeListRSP rsp : rspList) {
            if (rsp.getPledgeIds() != null && !rsp.getPledgeIds().isEmpty()) {
                rsp.setPledgeInfo(rsp.getPledgeIds().stream().map(pId -> clientInfoMap.get(pId)).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            List<MaterialsList> materialsListList = Optional.ofNullable(materialsListMap.get(originIdMap.get(rsp.getId()))).orElse(new ArrayList<>());
            if (CollectionUtil.isNotEmpty(materialsListList)) {
                MaterialsList file = materialsListList.get(0);
                rsp.setFileName(file.getFilename());
                rsp.setFileId(file.getId());
            }
        }
        return rspList;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.PLEDGE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
