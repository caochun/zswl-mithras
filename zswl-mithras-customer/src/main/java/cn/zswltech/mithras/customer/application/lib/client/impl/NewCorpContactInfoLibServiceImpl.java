package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.NewCorpContactInfoListRSP;
import cn.zswltech.mithras.customer.mapper.lib.client.NewCorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.NewCorpContactInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.NewCorpContactInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpContactInfoLibServiceImpl extends ServiceImpl<NewCorpContactInfoLibMapper, NewCorpContactInfoLib> implements NewCorpContactInfoLibService {

    @Override
    public PageR<NewCorpContactInfoListRSP> list(CorpContactInfoListREQ req) {
        Page<NewCorpContactInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NewCorpContactInfoLib>lambdaQuery().eq(NewCorpContactInfoLib::getClientId, req.getClientId())
                        .eq(NewCorpContactInfoLib::getVersion, req.getVersion())
                        .orderByDesc(NewCorpContactInfoLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(this::lib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    private NewCorpContactInfoListRSP lib2Rsp(NewCorpContactInfoLib lib) {
        NewCorpContactInfoListRSP rsp = BeanUtil.copyProperties(lib, NewCorpContactInfoListRSP.class);
        rsp.setId(lib.getOriginId());
        rsp.setCreateTime(lib.getDataCreateTime());
        return rsp;
    }
}
