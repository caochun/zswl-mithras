package cn.zswltech.mithras.application.orchestration.adapter.dashboard;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dashboard.application.port.DashboardAfterLeasePreparePort;
import cn.zswltech.mithras.dashboard.application.port.DashboardAfterLeasePrepareSnapshot;
import cn.zswltech.mithras.dashboard.enums.DashboardProcessModel;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardAfterLeasePreparePortAdapter implements DashboardAfterLeasePreparePort {

    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
    public List<DashboardAfterLeasePrepareSnapshot> listUnsubmitted(String startUserId, String businessId) {
        ProcessPrepareListREQ prepareReq = new ProcessPrepareListREQ();
        prepareReq.setPage(1);
        prepareReq.setPageSize(5000);
        prepareReq.setStartUserId(startUserId);
        prepareReq.setProcessTypeList(Collections.singletonList(DashboardProcessModel.NewAfterLeaseCheckReportCommonlyFlow.name()));
        prepareReq.setBusinessId(businessId);

        Page<CommonProcessPrepare> processPreparePage = commonProcessPrepareService.list(prepareReq);
        if (ObjectUtil.isEmpty(processPreparePage) || ObjectUtil.isEmpty(processPreparePage.getRecords())) {
            return ListUtil.empty();
        }
        return processPreparePage.getRecords().stream()
                .filter(prepare -> ObjectUtil.isEmpty(prepare.getBusinessData()))
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private DashboardAfterLeasePrepareSnapshot toSnapshot(CommonProcessPrepare prepare) {
        DashboardAfterLeasePrepareSnapshot snapshot = new DashboardAfterLeasePrepareSnapshot();
        snapshot.setId(prepare.getId());
        snapshot.setBusinessId(prepare.getBusinessId());
        snapshot.setFormName(prepare.getFormName());
        snapshot.setProcessStatusCode(ProcessState.UN_SUBMIT.name());
        snapshot.setProcessStatusDisplay(ProcessState.UN_SUBMIT.display());
        snapshot.setCreateTime(prepare.getCreateTime());
        snapshot.setUpdateTime(prepare.getUpdateTime());
        return snapshot;
    }
}
