package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareCollectionInfo;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareCollectionPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProcessPrepareCollectionPortAdapter implements ProcessPrepareCollectionPort {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Override
    public ProcessPrepareCollectionInfo getById(Long collectionId) {
        CollectionBaseInfo collection = collectionBaseInfoMapper.selectById(collectionId);
        if (collection == null) {
            return null;
        }
        ProcessPrepareCollectionInfo info = new ProcessPrepareCollectionInfo();
        info.setContractId(collection.getContractId());
        info.setPlanCollectionAmount(collection.getPlanCollectionAmount());
        info.setPrincipal(collection.getPrincipal());
        info.setPlanCollectionDate(collection.getPlanCollectionDate());
        info.setInterest(collection.getInterest());
        return info;
    }
}
