package cn.zswltech.mithras.afterlease.adapter;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCollectionPort;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AfterLeaseCollectionPortAdapter implements AfterLeaseCollectionPort {
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public Page<CollectionBaseInfo> page(Page<CollectionBaseInfo> page, Wrapper<CollectionBaseInfo> queryWrapper) {
        return collectionBaseInfoService.page(page, queryWrapper);
    }

    @Override
    public List<CollectionBaseInfo> list(Wrapper<CollectionBaseInfo> queryWrapper) {
        return collectionBaseInfoService.list(queryWrapper);
    }

    @Override
    public CollectionBaseInfo getOne(Wrapper<CollectionBaseInfo> queryWrapper) {
        return collectionBaseInfoService.getOne(queryWrapper);
    }

    @Override
    public CollectionBaseInfo getById(Long collectionId) {
        return collectionBaseInfoService.getById(collectionId);
    }

    @Override
    public void updateBatchById(List<CollectionBaseInfo> collectionBaseInfos) {
        collectionBaseInfoService.updateBatchById(collectionBaseInfos);
    }

    @Override
    public void updateEmailNoticeCount(Long collectionId, Integer emailNoticeCount) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setId(collectionId);
        collectionBaseInfo.setEmailNoticeCount(emailNoticeCount);
        collectionBaseInfoService.updateById(collectionBaseInfo);
    }
}
