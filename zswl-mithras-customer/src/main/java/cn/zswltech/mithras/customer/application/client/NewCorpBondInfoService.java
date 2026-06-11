package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.mapper.corp.NewCorpBondInfoMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.mapper.model.client.NewCorpBondInfo;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientNewDataHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luyi
 */
@Service
public class NewCorpBondInfoService extends ServiceImpl<NewCorpBondInfoMapper, NewCorpBondInfo> implements ClientNewDataHelper<NewCorpBondInfo> {
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
