package cn.zswltech.mithras.afterlease.application.impl;

import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckReportMetaMapper;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
@Service
public class AfterLeaseCheckReportMetaServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportMetaMapper, NewAfterLeaseCheckReportMeta> implements AfterLeaseCheckReportMetaService {
    @Override
    public NewAfterLeaseCheckReportMeta getByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportMeta> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, checkPlanClientId);
        query.orderByDesc(NewAfterLeaseCheckReportMeta::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }

    @Override
    public List<NewAfterLeaseCheckReportMeta> listByCheckPlanClientIds(Collection<Long> checkPlanClientIds) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportMeta> query = Wrappers.lambdaQuery();
        query.in(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, checkPlanClientIds);
        return this.list(query);
    }
}
