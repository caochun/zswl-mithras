package cn.zswltech.mithras.customer.versioning.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpBondInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.CorpBondInfoLib;
import cn.zswltech.mithras.customer.versioning.CorpBondInfoLibService;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpBondInfoLibHandlerImpl;
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
public class CorpBondInfoLibServiceImpl extends ServiceImpl<CorpBondInfoLibMapper, CorpBondInfoLib> implements CorpBondInfoLibService {

    @Resource
    private CorpBondInfoLibHandlerImpl bondInfoLibHandler;

    @Override
    public PageR<CorpBondInfoListRSP> list(CorpBondInfoListREQ req) {
        Page<CorpBondInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpBondInfoLib>lambdaQuery().eq(CorpBondInfoLib::getClientId, req.getClientId())
                        .eq(CorpBondInfoLib::getVersion, req.getVersion())
                        .orderByDesc(CorpBondInfoLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(bondInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
