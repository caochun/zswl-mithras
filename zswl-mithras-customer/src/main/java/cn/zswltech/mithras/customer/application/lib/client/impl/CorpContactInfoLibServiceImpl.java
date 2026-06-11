package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.CorpContactInfoLibService;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpContactInfoLibHandlerImpl;
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
public class CorpContactInfoLibServiceImpl extends ServiceImpl<CorpContactInfoLibMapper, CorpContactInfoLib> implements CorpContactInfoLibService {

    @Resource
    private CorpContactInfoLibHandlerImpl contactInfoLibHandler;

    @Override
    public PageR<CorpContactInfoListRSP> list(CorpContactInfoListREQ req) {
        Page<CorpContactInfoLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpContactInfoLib>lambdaQuery().eq(CorpContactInfoLib::getClientId, req.getClientId())
                        .eq(CorpContactInfoLib::getVersion, req.getVersion())
                        .orderByDesc(CorpContactInfoLib::getUpdateTime)
        );
        return PageR.of(dataPage.getRecords().stream().map(contactInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
