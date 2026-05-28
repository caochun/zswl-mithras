package cn.zswltech.mithras.service.service.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.service.mapper.lib.client.CorpRelatedEnterpriseLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpRelatedEnterpriseLib;
import cn.zswltech.mithras.service.service.lib.client.CorpRelatedEnterpriseLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpRelatedEnterpriseLibHandlerImpl;
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
public class CorpRelatedEnterpriseLibServiceImpl extends ServiceImpl<CorpRelatedEnterpriseLibMapper, CorpRelatedEnterpriseLib> implements CorpRelatedEnterpriseLibService {

    @Resource
    private CorpRelatedEnterpriseLibHandlerImpl enterpriseLibHandler;

    @Override
    public PageR<CorpRelatedEnterpriseListRSP> list(CorpRelatedEnterpriseListREQ req, SFunction<CorpRelatedEnterpriseLib, ?> orderBy, String orderType) {
        LambdaQueryWrapper<CorpRelatedEnterpriseLib> w = Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery()
                .eq(CorpRelatedEnterpriseLib::getClientId, req.getClientId())
                .eq(CorpRelatedEnterpriseLib::getVersion, req.getVersion());
        if (ascend.name().equals(orderType)) {
            w.orderByAsc(orderBy);
        } else {
            w.orderByDesc(orderBy);
        }

        Page<CorpRelatedEnterpriseLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), w);
        return PageR.of(dataPage.getRecords().stream().map(enterpriseLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
