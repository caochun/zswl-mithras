package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckReportContentLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportContent;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportContentLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@Deprecated
@Service
public class AfterLeaseCheckReportContentLibService extends ServiceImpl<NewAfterLeaseCheckReportContentLibMapper, NewAfterLeaseCheckReportContentLib> {
    public List<NewAfterLeaseCheckReportContentLib> listByCheckClientIdAndVersion(Long checkClientId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportContentLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportContent::getCheckPlanClientId, checkClientId);
        query.eq(NewAfterLeaseCheckReportContentLib::getVersion, version);
        return this.list(query);
    }
}
