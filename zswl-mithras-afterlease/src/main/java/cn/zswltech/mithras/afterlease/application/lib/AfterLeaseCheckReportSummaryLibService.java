package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.lib.NewAfterLeaseCheckReportSummaryLibMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportSummary;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportSummaryLib;
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
public class AfterLeaseCheckReportSummaryLibService extends ServiceImpl<NewAfterLeaseCheckReportSummaryLibMapper, NewAfterLeaseCheckReportSummaryLib> {
    public List<NewAfterLeaseCheckReportSummaryLib> listByCheckClientId(Long checkClientId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportSummaryLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportSummary::getCheckPlanClientId, checkClientId);
        query.eq(NewAfterLeaseCheckReportSummaryLib::getVersion, version);
        return this.list(query);
    }
}
