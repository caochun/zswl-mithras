package cn.zswltech.mithras.customer.versioning.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpBankAccountLibMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpBankAccountLib;
import cn.zswltech.mithras.customer.versioning.CorpBankAccountLibService;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpBankAccountLibHandlerImpl;
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
public class CorpBankAccountLibServiceImpl extends ServiceImpl<CorpBankAccountLibMapper, CorpBankAccountLib> implements CorpBankAccountLibService {

    @Resource
    private CorpBankAccountLibHandlerImpl bankAccountLibHandler;

    @Override
    public List<CorpBankAccountLib> listBy(Long clientId, String version) {
        LambdaQueryWrapper<CorpBankAccountLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        query.eq(CorpBankAccountLib::getVersion, version);
        return this.list(query);
    }

    @Override
    public PageR<CorpBankAccountListRSP> list(CorpBankAccountListREQ req) {
        Page<CorpBankAccountLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpBankAccountLib>lambdaQuery().eq(CorpBankAccountLib::getClientId, req.getClientId())
                        .eq(CorpBankAccountLib::getVersion, req.getVersion())
                        .orderByDesc(CorpBankAccountLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(bankAccountLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
