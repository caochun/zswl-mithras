package cn.zswltech.mithras.afterlease.application.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.afterlease.mapper.lib.NewAfterLeaseCheckPlanBaseLibMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBaseLib;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckPlanBaseLibHandler;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Service
public class AfterLeaseCheckPlanBaseLibService extends ServiceImpl<NewAfterLeaseCheckPlanBaseLibMapper, NewAfterLeaseCheckPlanBaseLib> {
    @Resource
    private AfterLeaseCheckPlanBaseLibHandler checkPlanBaseLibHandler;

    public NewAfterLeaseCheckPlanBase getByOriginIdAndVersion(Long originId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanBaseLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanBaseLib::getOriginId, originId);
        query.eq(NewAfterLeaseCheckPlanBaseLib::getVersion, version);
        query.orderByDesc(NewAfterLeaseCheckPlanBase::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        NewAfterLeaseCheckPlanBaseLib one = this.getOne(query);
        return ObjectUtil.isNull(one) ? null : checkPlanBaseLibHandler.actualLib2Entity(one);
    }
}
