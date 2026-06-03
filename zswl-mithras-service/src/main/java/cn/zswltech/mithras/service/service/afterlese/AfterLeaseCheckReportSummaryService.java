package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportSummary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Deprecated
public interface AfterLeaseCheckReportSummaryService extends IService<NewAfterLeaseCheckReportSummary> {
    void removeByCheckPlanClientId(Long checkPlanClientId);

    void saveReportSummary(AfterLeaseCheckReportCSREQ req);

    List<NewAfterLeaseCheckReportSummary> listByCheckPlanClientId(Long checkPlanClientId);

    List<AfterLeaseCheckReportCSRSP> listReportSummary(Long checkPlanClientId, String version);
}
