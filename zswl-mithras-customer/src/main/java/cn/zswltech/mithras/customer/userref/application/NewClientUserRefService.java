package cn.zswltech.mithras.customer.userref.application;

import cn.zswltech.mithras.customer.userref.mapper.NewClientUserRefMapper;
import cn.zswltech.mithras.customer.userref.model.NewClientUserRef;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@Service
public class NewClientUserRefService extends ServiceImpl<NewClientUserRefMapper, NewClientUserRef> {
    public void create(Long clientId, Long userId) {
        NewClientUserRef newClientUserRef = new NewClientUserRef();
        newClientUserRef.setClientId(clientId);
        newClientUserRef.setUserId(userId);
        this.save(newClientUserRef);
    }

    public int countByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewClientUserRef> query = Wrappers.lambdaQuery();
        query.eq(NewClientUserRef::getClientId, clientId);
        query.eq(NewClientUserRef::getUserId, userId);
        return this.count(query);
    }
}
