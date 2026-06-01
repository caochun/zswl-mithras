package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportMeta;
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
