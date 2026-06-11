package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.mapper.lib.NewAfterLeaseCheckReportBaseLibMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportBaseLib;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@Service
public class AfterLeaseCheckReportBaseLibService extends ServiceImpl<NewAfterLeaseCheckReportBaseLibMapper, NewAfterLeaseCheckReportBaseLib> {
    public NewAfterLeaseCheckReportBaseLib getByCheckProjectIdAndVersion(Long checkClientId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportBaseLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportBase::getCheckPlanClientId, checkClientId);
        query.eq(NewAfterLeaseCheckReportBaseLib::getVersion, version);
        query.orderByDesc(NewAfterLeaseCheckReportBase::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }
}
