package cn.zswltech.mithras.service.service.client;

import cn.zswltech.mithras.service.mapper.corp.NewCorpShareholderInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpShareholderInfo;
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
public class NewCorpShareHolderInfoService extends ServiceImpl<NewCorpShareholderInfoMapper, NewCorpShareholderInfo> implements ClientDataSaveCheckInterface<NewCorpShareholderInfo>, ClientNewDataHelper<NewCorpShareholderInfo> {
    @Override
    public List<NewCorpShareholderInfo> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpShareholderInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpShareholderInfo::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpShareholderInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpShareholderInfo::getUserId, userId);
        this.remove(query);
    }
}
