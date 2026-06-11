package cn.zswltech.mithras.contract.overdue.application.assembler;

import cn.zswltech.mithras.contract.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.domain.collection.Collection;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionAction;
import cn.zswltech.mithras.contract.overdue.model.OverdueCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 14:03
 */
@Mapper(componentModel = "spring")
public interface CollectionAssembler {

    @Mapping(source = "id", target = "collectionId.id")
    Collection detailToAggregate(CollectionDetailDto dto);

    @Mapping(source = "collectionId.id", target = "id")
    CollectionDetailDto toDetailDto(Collection collection);

    @Mapping(source = "id", target = "actionId.id")
    @Mapping(source = "code", target = "actionCode.code")
    @Mapping(source = "ocId", target = "collectionId.id")
    CollectionAction actionListDto2Entity(CollectionActionDto dto);

    List<CollectionAction> actionListDto2Entity(List<CollectionActionDto> dto);

    @Mapping(source = "actionId.id", target = "id")
    @Mapping(source = "collectionId.id", target = "ocId")
    @Mapping(source = "actionCode.code", target = "code")
    CollectionActionDto entity2ActionListDto(CollectionAction action);

    List<CollectionActionDto> entity2ActionListDto(List<CollectionAction> action);

    CollectionListDto po2ListDto(OverdueCollection collection);
    List<CollectionListDto> po2ListDto(List<OverdueCollection> collection);
}
