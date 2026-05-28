package cn.zswltech.mithras.service.service.client;

import cn.zswltech.mithras.service.mapper.corp.NewCorpAddressInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpAddressInfo;
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
public class NewCorpAddressInfoService extends ServiceImpl<NewCorpAddressInfoMapper, NewCorpAddressInfo> implements ClientDataSaveCheckInterface<NewCorpAddressInfo>, ClientNewDataHelper<NewCorpAddressInfo> {
    @Override
    public List<NewCorpAddressInfo> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpAddressInfo::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpAddressInfo::getUserId, userId);
        this.remove(query);
    }
}
