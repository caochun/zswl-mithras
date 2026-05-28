package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.service.mapper.lib.afterlease.NewAfterLeaseCheckReportMetaLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportMetaLib;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
@Service
public class AfterLeaseCheckReportMetaLibService extends ServiceImpl<NewAfterLeaseCheckReportMetaLibMapper, NewAfterLeaseCheckReportMetaLib> {
    public NewAfterLeaseCheckReportMetaLib getByCheckProjectIdAndVersion(Long checkClientId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportMetaLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportMeta::getCheckPlanClientId, checkClientId);
        query.eq(NewAfterLeaseCheckReportMetaLib::getVersion, version);
        query.orderByDesc(NewAfterLeaseCheckReportMeta::getId);
        query.last(StringUtil.mysqlLimit(0, 1));
        return this.getOne(query);
    }
}
