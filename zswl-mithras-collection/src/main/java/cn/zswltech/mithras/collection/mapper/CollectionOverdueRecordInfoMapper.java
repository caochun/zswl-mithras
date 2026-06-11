package cn.zswltech.mithras.collection.mapper;

import cn.zswltech.mithras.collection.model.CollectionOverdueRecordInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create: 2022-08-20
 **/
public interface CollectionOverdueRecordInfoMapper extends CustomBaseMapper<CollectionOverdueRecordInfo> {

    List<CollectionOverdueRecordInfo> getLastRecordByCollectionId(@Param("ids") List<Long> ids);

}
