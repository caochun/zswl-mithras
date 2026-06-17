package cn.zswltech.mithras.collection.mapper;

import cn.zswltech.mithras.collection.model.CollectionOverdueHistory;
import cn.zswltech.mithras.collection.overdue.history.CollectionOverdueHistorySnapshot;
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

    List<CollectionOverdueHistorySnapshot> getOverdueCollection(@Param("clientIds") Collection<Long> clientIds);


}
