package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceSaveREQ;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportFinance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
public interface AfterLeaseCheckReportFinanceService extends IService<NewAfterLeaseCheckReportFinance> {
    void removeByCheckPlanClientId(Long checkPlanClientId);

    void saveSnapshot(AfterLeaseCheckReportFinanceSaveREQ req);

    NewAfterLeaseCheckReportFinance getSpecificSnapshot(AfterLeaseCheckReportFinanceREQ req);

    List<NewAfterLeaseCheckReportFinance>  listByCheckPlanClientId(Long checkPlanClientId);
}
