package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.service.mapper.lib.afterlease.NewAfterLeaseCheckReportSummaryLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportSummary;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportSummaryLib;
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
