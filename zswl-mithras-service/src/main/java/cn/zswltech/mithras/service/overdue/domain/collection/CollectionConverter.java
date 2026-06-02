package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import cn.zswltech.mithras.contract.overdue.domain.collection.Collection;

/**
 * @description: entity Map to po
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:28
 */
@Mapper(componentModel = "spring")
public interface CollectionConverter {

    @Mapping(source = "id", target = "collectionId.id")
    Collection po2Entity(OverdueCollection po);

    @Mapping(source = "collectionId.id", target = "id")
    OverdueCollection entity2Po(Collection entity);
}
