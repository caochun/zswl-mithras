package cn.zswltech.mithras.service.service.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.dto.client.bondinfo.NewCorpBondInfoListRSP;
import cn.zswltech.mithras.service.mapper.lib.client.CorpBondInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.client.NewCorpBondInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpBondInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpBondInfoLib;
import cn.zswltech.mithras.service.service.lib.client.CorpBondInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.NewCorpBondInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpBondInfoLibHandlerImpl;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.NewCorpBondInfoLibHandlerImpl;
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
public class NewCorpBondInfoLibServiceImpl extends ServiceImpl<NewCorpBondInfoLibMapper, NewCorpBondInfoLib> implements NewCorpBondInfoLibService {

    @Resource
    private NewCorpBondInfoLibHandlerImpl newCorpBondInfoLibHandler;
    @Override
    public PageR<NewCorpBondInfoListRSP> list(CorpBondInfoListREQ req) {
        Page<NewCorpBondInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewCorpBondInfoLib>lambdaQuery().eq(NewCorpBondInfoLib::getClientId, req.getClientId())
                        .eq(NewCorpBondInfoLib::getVersion, req.getVersion())
                        .orderByDesc(NewCorpBondInfoLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(newCorpBondInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
