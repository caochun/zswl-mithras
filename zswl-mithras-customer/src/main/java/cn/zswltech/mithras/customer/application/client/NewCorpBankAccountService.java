package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.mapper.corp.NewCorpBankAccountMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.NewCorpBankAccount;
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
public class NewCorpBankAccountService extends ServiceImpl<NewCorpBankAccountMapper, NewCorpBankAccount> implements ClientNewDataHelper<NewCorpBankAccount> {
    @Override
    public List<NewCorpBankAccount> findByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpBankAccount> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpBankAccount::getUserId, userId);
        return this.list(query);
    }

    @Override
    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<NewCorpBankAccount> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpBankAccount::getUserId, userId);
        this.remove(query);
    }
}
