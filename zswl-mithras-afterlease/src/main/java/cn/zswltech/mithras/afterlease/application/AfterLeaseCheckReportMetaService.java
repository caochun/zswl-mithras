package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportMeta;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
public interface AfterLeaseCheckReportMetaService extends IService<NewAfterLeaseCheckReportMeta> {
    NewAfterLeaseCheckReportMeta getByCheckPlanClientId(Long checkPlanClientId);

    List<NewAfterLeaseCheckReportMeta> listByCheckPlanClientIds(Collection<Long> checkPlanClientIds);
}
