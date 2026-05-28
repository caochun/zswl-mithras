package cn.zswltech.mithras.service.service.client;

import cn.zswltech.mithras.service.mapper.corp.NewCorpBondInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpBondInfo;
import cn.zswltech.mithras.service.service.client.copyhandler.ClientNewDataHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luyi
 */
@Service
public class NewCorpBondInfoService extends ServiceImpl<NewCorpBondInfoMapper, NewCorpBondInfo> implements ClientDataSaveCheckInterface<NewCorpBondInfo>, ClientNewDataHelper<NewCorpBondInfo> {
    @Override
    public List<NewCorpBondInfo> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpBondInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpBondInfo::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpBondInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpBondInfo::getUserId, userId);
        this.remove(query);
    }
}
