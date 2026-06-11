package cn.zswltech.mithras.collection.mapper;

import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueHistory;
import cn.zswltech.mithras.contract.overdue.mapper.model.OverdueCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
* @description 逾期历史表
* @author vico
* @date 2023-05-31
*/
public interface CollectionOverdueHistoryMapper extends BaseMapper<CollectionOverdueHistory> {

    List<CollectionOverdueHistory> getHistoryByDay(Long collectionId);

    List<OverdueCollection> getOverdueCollection(@Param("clientIds") Collection<Long> clientIds);


}