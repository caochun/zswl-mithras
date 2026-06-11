package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.mapper.corp.NewCorpRelatedEnterpriseMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.NewCorpRelatedEnterprise;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientNewDataHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luyi
 */
@Slf4j
@Service
public class NewCorpRelatedEnterpriseService extends ServiceImpl<NewCorpRelatedEnterpriseMapper, NewCorpRelatedEnterprise> implements ClientNewDataHelper<NewCorpRelatedEnterprise> {
    @Override
    public List<NewCorpRelatedEnterprise> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpRelatedEnterprise> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpRelatedEnterprise::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpRelatedEnterprise> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpRelatedEnterprise::getUserId, userId);
        this.remove(query);
    }
}
