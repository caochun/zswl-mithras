package cn.zswltech.mithras.service.service.client;

import cn.zswltech.mithras.service.mapper.corp.NewCorpCommerceInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpCommerceInfo;
import cn.zswltech.mithras.service.service.client.copyhandler.ClientNewDataHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author junke
 * 工商信息service
 */
@Slf4j
@Service
public class NewCorpCommerceInfoService extends ServiceImpl<NewCorpCommerceInfoMapper, NewCorpCommerceInfo> implements ClientDataSaveCheckInterface<NewCorpCommerceInfo>, ClientNewDataHelper<NewCorpCommerceInfo> {
    public List<NewCorpCommerceInfo> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpCommerceInfo::getUserId, userId);
        return this.list(query);
    }

    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpCommerceInfo::getUserId, userId);
        this.remove(query);
    }
}
