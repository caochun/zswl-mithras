package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.mapper.lib.NewAfterLeaseCheckExternalQueryLibMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQueryLib;
import cn.zswltech.mithras.foundation.util.StringUtil;
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
