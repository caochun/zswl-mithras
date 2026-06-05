package cn.zswltech.mithras.workbench.interfaces.metric;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchCardMetricApi;
import cn.zswltech.mithras.dto.workbench.CardMetricChooseDto;
import cn.zswltech.mithras.dto.workbench.WorkBenchRoleListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.workbench.application.WorkbenchCardMetricApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class WorkbenchCardMetricController implements WorkbenchCardMetricApi {

    @Resource
    private WorkbenchCardMetricApplicationService workbenchCardMetricApplicationService;

    @Override
    public R<WorkBenchRoleListRsp> listRole() {
        return workbenchCardMetricApplicationService.listRole();
    }

    @Override
    public R<List<WorkbenchCardMetricListRsp>> listCardMetrics(WorkbenchMetricReq req) {
        return workbenchCardMetricApplicationService.listCardMetrics(req);
    }

    @Override
    public R<List<CardMetricChooseDto>> currentUserChooseVo() {
        return workbenchCardMetricApplicationService.currentUserChooseVo();
    }

    @Override
    public R<Void> modifyChoosedCard(List<CardMetricChooseDto> req) {
        return workbenchCardMetricApplicationService.modifyChoosedCard(req);
    }
}
