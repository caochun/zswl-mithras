package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.service.mapper.lib.afterlease.NewAfterLeaseCheckExternalQueryLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQueryLib;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Service
public class AfterLeaseCheckExternalQueryLibService extends ServiceImpl<NewAfterLeaseCheckExternalQueryLibMapper, NewAfterLeaseCheckExternalQueryLib> {
    public NewAfterLeaseCheckExternalQueryLib getByOriginIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckExternalQueryLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckExternalQueryLib::getOriginId, originId);
        query.eq(NewAfterLeaseCheckExternalQueryLib::getVersion, version);
        query.orderByDesc(NewAfterLeaseCheckExternalQuery::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }
}
