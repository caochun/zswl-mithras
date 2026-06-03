package cn.zswltech.mithras.service.convert.collection;


import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface CollectionRecordInfoConverter {

    @Mapping(source = "pknumber", target = "flowId")
    CollectionRecordInfo financialRecordREQToBean(ThirdCollectionRecordREQ req);

}
