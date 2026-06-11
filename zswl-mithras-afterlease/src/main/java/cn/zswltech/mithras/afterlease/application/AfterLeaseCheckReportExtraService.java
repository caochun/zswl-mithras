package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraRSP;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportExtra;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
public interface AfterLeaseCheckReportExtraService extends IService<NewAfterLeaseCheckReportExtra> {
    void saveNonPublicExtra(AfterLeaseCheckReportNonPublicExtraREQ req);

    List<AfterLeaseCheckReportNonPublicExtraRSP> listNonPublicExtraRSP(Long checkPlanClientId, String version);

    void removeByCheckPlanClientId(Long checkPlanClientId);

    List<NewAfterLeaseCheckReportExtra> listNonPublicExtra(Long checkPlanClientId);
}
