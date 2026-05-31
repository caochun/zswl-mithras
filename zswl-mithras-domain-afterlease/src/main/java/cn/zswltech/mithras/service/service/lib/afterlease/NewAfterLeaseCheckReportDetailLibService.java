package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckReportDetailLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetailLib;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2023/11/15
 * @description
 */
@Service
public class NewAfterLeaseCheckReportDetailLibService extends ServiceImpl<NewAfterLeaseCheckReportDetailLibMapper, NewAfterLeaseCheckReportDetailLib> {
    public NewAfterLeaseCheckReportDetailLib getOneByMainIdVersion(Long mainId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportDetailLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportDetail::getCheckPlanClientId, mainId);
        query.eq(NewAfterLeaseCheckReportDetailLib::getVersion, version);
        query.orderByDesc(NewAfterLeaseCheckReportDetail::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
