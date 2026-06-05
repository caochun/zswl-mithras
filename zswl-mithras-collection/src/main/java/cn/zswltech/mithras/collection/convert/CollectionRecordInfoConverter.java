package cn.zswltech.mithras.collection.convert;


import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollectionRecordInfoConverter {

    @Mapping(source = "pknumber", target = "flowId")
    CollectionRecordInfo financialRecordREQToBean(ThirdCollectionRecordREQ req);

}
