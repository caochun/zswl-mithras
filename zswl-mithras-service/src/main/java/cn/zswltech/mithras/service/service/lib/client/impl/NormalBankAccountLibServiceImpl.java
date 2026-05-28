package cn.zswltech.mithras.service.service.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListRSP;
import cn.zswltech.mithras.service.mapper.lib.client.NormalBankAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.NormalBankAccount;
import cn.zswltech.mithras.service.mapper.model.client.NormalBankAccountLib;
import cn.zswltech.mithras.service.service.lib.client.NormalBankAccountLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.NormalBankAccountLibHandlerImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NormalBankAccountLibServiceImpl extends ServiceImpl<NormalBankAccountLibMapper, NormalBankAccountLib> implements NormalBankAccountLibService {

    @Resource
    private NormalBankAccountLibHandlerImpl bankAccountLibHandler;

    @Override
    public PageR<NormalBankAccountListRSP> list(NormalBankAccountListREQ req) {
        Page<NormalBankAccountLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NormalBankAccountLib>lambdaQuery()
                        .eq(NormalBankAccountLib::getClientId, req.getClientId())
                        .eq(NormalBankAccountLib::getVersion, req.getVersion())
                        .orderByDesc(NormalBankAccountLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(bankAccountLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

}
