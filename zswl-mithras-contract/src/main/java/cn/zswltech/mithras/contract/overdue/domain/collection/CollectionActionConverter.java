package cn.zswltech.mithras.contract.overdue.domain.collection;

import cn.zswltech.mithras.contract.overdue.common.OverdueTypeConversionWorker;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollectionAction;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollectionActionLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionAction;

/**
 * @description: entity Map to po
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:28
 */
@Mapper(componentModel = "spring", uses = OverdueTypeConversionWorker.class)
public interface CollectionActionConverter {

    @Mapping(source = "id", target = "actionId.id")
    @Mapping(source = "ocId", target = "collectionId.id")
    @Mapping(source = "code", target = "actionCode.code")
    @Mapping(source = "contractIds", target = "contractIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "contractCodes", target = "contractCodes", qualifiedByName = "jsonStringToStringList")
    CollectionAction po2Entity(OverdueCollectionAction po);

    List<CollectionAction> po2Entity(List<OverdueCollectionAction> pos);

    @Mapping(source = "actionId.id", target = "id")
    @Mapping(source = "collectionId.id", target = "ocId")
    @Mapping(source = "actionCode.code", target = "code")
    @Mapping(source = "contractIds", target = "contractIds", qualifiedByName = "toJsonString")
    @Mapping(source = "contractCodes", target = "contractCodes", qualifiedByName = "toJsonString")
    OverdueCollectionAction entity2Po(CollectionAction entity);

    @Mapping(source = "originId", target = "actionId.id")
    @Mapping(source = "ocId", target = "collectionId.id")
    @Mapping(source = "code", target = "actionCode.code")
    @Mapping(source = "contractIds", target = "contractIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "contractCodes", target = "contractCodes", qualifiedByName = "jsonStringToStringList")
    CollectionAction libPo2Entity(OverdueCollectionActionLib oneLib);
}
