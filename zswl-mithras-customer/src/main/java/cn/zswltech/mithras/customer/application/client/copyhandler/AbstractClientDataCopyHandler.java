package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.ClientUserRef;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.ClientCreateRecordService;
import cn.zswltech.mithras.customer.application.client.ClientUserRefService;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
public abstract class AbstractClientDataCopyHandler<OldModel extends ClientBaseModel, NewModel extends OldModel, OldService extends ClientOldDataHelper<OldModel>, NewService extends ClientNewDataHelper<NewModel>> implements ClientDataCopyHandler, InitializingBean {
    @Resource
    protected ClientMapper clientMapper;
    @Resource
    protected ClientUserRefService clientUserRefService;
    @Resource
    protected ClientCreateRecordService clientCreateRecordService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void copyFromNewToOld(ClientCopyInfoBO clientCopyInfoBO) {
        Long clientId = clientCopyInfoBO.getClientId();
        Long userId = clientCopyInfoBO.getCurrentUserId();
        List<NewModel> newModelList = this.getNewService().findByClientUser(clientId, userId);
        this.getOldService().removeByClientId(clientId);
        if (CollectionUtil.isNotEmpty(newModelList)) {
            List<OldModel> oldModelList = newModelList.stream().map(e -> {
                OldModel oldModel = copyOldModel(e, clientCopyInfoBO);
                oldModel.setId(null);
                return oldModel;
            }).collect(Collectors.toList());
            this.getOldService().saveBatch(oldModelList);
        }
        // 更新关联表数据
        clientUserRefService.updateLastOperateTime(clientId, userId, ClientUserRef.OperateTypeEnum.WRITE);
        // 如果没有创建记录就补一条
        if (!clientCreateRecordService.hasRecord(clientId, userId)) {
            clientCreateRecordService.create(clientId, userId);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void copyFromOldToNew(ClientCopyInfoBO clientCopyInfoBO) {
        Long clientId = clientCopyInfoBO.getClientId();
        Long userId = clientCopyInfoBO.getCurrentUserId();
        List<OldModel> oldModelList = this.getOldService().findByClientId(clientId);
        this.getNewService().removeByClientUser(clientId, userId);
        if (CollectionUtil.isNotEmpty(oldModelList)) {
            Client client = clientMapper.selectById(clientId);
            clientCopyInfoBO.setClient(client);
            List<NewModel> newModelList = oldModelList.stream().map(e -> {
                NewModel newModel = copyNewModel(e, clientCopyInfoBO);
                newModel.setId(null);
                return newModel;
            }).collect(Collectors.toList());
            this.getNewService().saveBatch(newModelList);
        }
        // 更新关联表数据
        clientUserRefService.updateLastOperateTime(clientId, userId, ClientUserRef.OperateTypeEnum.WRITE);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void copyFromNewToNew(ClientCopyInfoBO clientCopyInfoBO) {
        Long clientId = clientCopyInfoBO.getClientId();
        Long dbUserId = clientCopyInfoBO.getDbUserId();
        Long currentUserId = clientCopyInfoBO.getCurrentUserId();
        List<NewModel> dbList = this.getNewService().findByClientUser(clientId, dbUserId);
        this.getNewService().removeByClientUser(clientId, currentUserId);
        if (CollectionUtil.isNotEmpty(dbList)) {
            Client client = clientMapper.selectById(clientId);
            clientCopyInfoBO.setClient(client);
            List<NewModel> list = dbList.stream().map(e -> {
                NewModel newModel = copyNewModelFromNewModel(e, clientCopyInfoBO);
                newModel.setId(null);
                return newModel;
            }).collect(Collectors.toList());
            this.getNewService().saveBatch(list);
        }
        // 更新关联表数据
        clientUserRefService.updateLastOperateTime(clientId, clientCopyInfoBO.getCurrentUserId(), ClientUserRef.OperateTypeEnum.READ);
    }

    @Override
    public void afterPropertiesSet() {
        ClientDataCopyHandlerFactory.register(this);
    }

    protected  <T extends BaseModel, S extends BaseModel> T copyIgnoreBaseField(S source, Class<T> targetClz) {
        T target = BeanUtil.copyProperties(source, targetClz);
        target.setCreateBy(null);
        target.setCreateTime(null);
        target.setUpdateBy(null);
        target.setUpdateTime(null);
        return target;
    }

    protected abstract OldService getOldService();

    protected abstract NewService getNewService();

    protected abstract NewModel copyNewModel(OldModel oldModel, ClientCopyInfoBO clientCopyInfoBO);

    protected abstract OldModel copyOldModel(NewModel newModel, ClientCopyInfoBO clientCopyInfoBO);

    protected abstract NewModel copyNewModelFromNewModel(NewModel newModel, ClientCopyInfoBO clientCopyInfoBO);
}
