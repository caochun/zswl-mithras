package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.service.convert.contract.ContractMortgageConverter;
import cn.zswltech.mithras.service.convert.contract.ContractPledgeConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
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
    private Id2NameService id2NameService;

    @Resource
    private MaterialsListService materialsListService;

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
        Map<Long, ClientInfo> clientInfoMap = id2NameService.clientId2CLient(rspList.stream().map(ContractPledgeListRSP::getPledgeIds).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet()));
        Map<Long, List<MaterialsList>> materialsListMap = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(PLEDGE_ITEM_FILE_TYPE), fList.stream().map(ContractPledgeLib::getOriginId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(MaterialsList::getMainId));
        for (ContractPledgeListRSP rsp : rspList) {
            if (CollectionUtils.isNotEmpty(rsp.getPledgeIds())) {
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
