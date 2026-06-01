package cn.zswltech.mithras.service.service.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.dto.client.shareholder.NewCorpShareholderInfoListRSP;
import cn.zswltech.mithras.service.mapper.lib.client.CorpShareholderInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.client.NewCorpShareholderInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpShareholderInfoLib;
import cn.zswltech.mithras.service.service.lib.client.CorpShareholderInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.NewCorpShareholderInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpShareholderInfoLibHandlerImpl;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.NewCorpShareholderInfoLibHandlerImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.OrderByType.ascend;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpShareholderInfoLibServiceImpl extends ServiceImpl<NewCorpShareholderInfoLibMapper, NewCorpShareholderInfoLib> implements NewCorpShareholderInfoLibService {
    @Resource
    private NewCorpShareholderInfoLibHandlerImpl newCorpShareholderInfoLibHandler;

    @Override
    public PageR<NewCorpShareholderInfoListRSP> list(CorpShareholderInfoListREQ req, SFunction<NewCorpShareholderInfoLib, ?> orderBy, String orderType) {
        LambdaQueryWrapper<NewCorpShareholderInfoLib> w = Wrappers.<NewCorpShareholderInfoLib>lambdaQuery()
                .eq(NewCorpShareholderInfoLib::getClientId, req.getClientId()).eq(NewCorpShareholderInfoLib::getVersion, req.getVersion());
        if (ascend.name().equals(orderType)) {
            w.orderByAsc(orderBy);
        } else {
            w.orderByDesc(orderBy);
        }
        Page<NewCorpShareholderInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), w);
        return PageR.of(dataPage.getRecords().stream().map(newCorpShareholderInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

}
