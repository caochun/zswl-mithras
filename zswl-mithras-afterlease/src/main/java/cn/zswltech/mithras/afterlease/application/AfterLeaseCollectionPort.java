package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AfterLeaseCollectionPort {
    Page<CollectionBaseInfo> page(Page<CollectionBaseInfo> page, Wrapper<CollectionBaseInfo> queryWrapper);

    List<CollectionBaseInfo> list(Wrapper<CollectionBaseInfo> queryWrapper);

    CollectionBaseInfo getOne(Wrapper<CollectionBaseInfo> queryWrapper);

    CollectionBaseInfo getById(Long collectionId);

    void updateBatchById(List<CollectionBaseInfo> collectionBaseInfos);

    void updateEmailNoticeCount(Long collectionId, Integer emailNoticeCount);
}
