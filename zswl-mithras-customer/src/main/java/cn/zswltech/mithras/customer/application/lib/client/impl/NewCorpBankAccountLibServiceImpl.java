package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.NewCorpBankAccountListRSP;
import cn.zswltech.mithras.customer.mapper.lib.client.NewCorpBankAccountLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.mapper.model.client.NewCorpBankAccountLib;
import cn.zswltech.mithras.customer.application.lib.client.NewCorpBankAccountLibService;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.NewCorpBankAccountLibHandlerImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpBankAccountLibServiceImpl extends ServiceImpl<NewCorpBankAccountLibMapper, NewCorpBankAccountLib> implements NewCorpBankAccountLibService {

    @Resource
    private NewCorpBankAccountLibHandlerImpl newCorpBankAccountLibHandler;
    @Override
    public List<NewCorpBankAccountLib> listBy(Long clientId, String version) {
        LambdaQueryWrapper<NewCorpBankAccountLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(NewCorpBankAccountLib::getVersion, version);
        return this.list(query);
    }

    @Override
    public PageR<NewCorpBankAccountListRSP> list(CorpBankAccountListREQ req) {
        Page<NewCorpBankAccountLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewCorpBankAccountLib>lambdaQuery().eq(NewCorpBankAccountLib::getClientId, req.getClientId())
                        .eq(NewCorpBankAccountLib::getVersion, req.getVersion())
                        .orderByDesc(NewCorpBankAccountLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(newCorpBankAccountLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
