package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportContent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/17
 * @description
 */
@Deprecated
public interface AfterLeaseCheckReportContentService extends IService<NewAfterLeaseCheckReportContent> {
    void removeByCheckPlanClientId(Long checkPlanClientId);

    void saveReportContent(AfterLeaseCheckReportCSREQ req);

    List<NewAfterLeaseCheckReportContent> listByCheckPlanClientId(Long checkPlanClientId);

    List<AfterLeaseCheckReportCSRSP> listReportContent(Long checkPlanClientId, String version);
}
