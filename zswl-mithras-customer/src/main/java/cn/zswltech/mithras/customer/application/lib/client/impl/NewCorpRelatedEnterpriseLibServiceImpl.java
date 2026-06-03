package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.dto.client.relatedenterprise.NewCorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpRelatedEnterpriseLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.NewCorpRelatedEnterpriseLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpRelatedEnterpriseLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpRelatedEnterpriseLib;
import cn.zswltech.mithras.customer.application.lib.client.CorpRelatedEnterpriseLibService;
import cn.zswltech.mithras.customer.application.lib.client.NewCorpRelatedEnterpriseLibService;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpRelatedEnterpriseLibHandlerImpl;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.NewCorpRelatedEnterpriseLibHandlerImpl;
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
public class NewCorpRelatedEnterpriseLibServiceImpl extends ServiceImpl<NewCorpRelatedEnterpriseLibMapper, NewCorpRelatedEnterpriseLib> implements NewCorpRelatedEnterpriseLibService {

    @Resource
    private NewCorpRelatedEnterpriseLibHandlerImpl newCorpRelatedEnterpriseLibHandler;
    @Override
    public PageR<NewCorpRelatedEnterpriseListRSP> list(CorpRelatedEnterpriseListREQ req, SFunction<NewCorpRelatedEnterpriseLib, ?> orderBy, String orderType) {
        LambdaQueryWrapper<NewCorpRelatedEnterpriseLib> w = Wrappers.<NewCorpRelatedEnterpriseLib>lambdaQuery()
                .eq(NewCorpRelatedEnterpriseLib::getClientId, req.getClientId())
                .eq(NewCorpRelatedEnterpriseLib::getVersion, req.getVersion());
        if (ascend.name().equals(orderType)) {
            w.orderByAsc(orderBy);
        } else {
            w.orderByDesc(orderBy);
        }

        Page<NewCorpRelatedEnterpriseLib> dataPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), w);
        return PageR.of(dataPage.getRecords().stream().map(newCorpRelatedEnterpriseLibHandler::actualLib2Rsp).collect(Collectors.toList()),
                dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }
}
