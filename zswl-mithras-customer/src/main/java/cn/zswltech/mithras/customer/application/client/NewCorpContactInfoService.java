package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.NewCorpContactInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpContactInfo;
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
public class NewCorpContactInfoService extends ServiceImpl<NewCorpContactInfoMapper, NewCorpContactInfo> implements ClientNewDataHelper<NewCorpContactInfo> {
    @Override
    public List<NewCorpContactInfo> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpContactInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpContactInfo::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpContactInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpContactInfo::getUserId, userId);
        this.remove(query);
    }
}
