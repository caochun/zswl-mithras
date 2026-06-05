package cn.zswltech.mithras.service.application.workbench;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.CardMetricChooseDto;
import cn.zswltech.mithras.dto.workbench.WorkBenchRoleListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.service.service.workbench.WorkbenchCardMetricService;
import cn.zswltech.mithras.workbench.application.WorkbenchCardMetricApplicationService;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchCardMetricFacade implements WorkbenchCardMetricApplicationService {

    @Resource
    private WorkbenchCardMetricService workbenchCardMetricService;


    @Override
    public R<WorkBenchRoleListRsp> listRole() {
        return R.ok(workbenchCardMetricService.listRole());
    }

    @Override
    public R<List<WorkbenchCardMetricListRsp>> listCardMetrics(WorkbenchMetricReq req) {
        if (req.getCurrentRoleCode().equals(WorkbenchMetricRole.XMJL.name())) {
            return R.ok(workbenchCardMetricService.listBizPersonMetrics());
        }
        return R.ok(workbenchCardMetricService.list(req));
    }

    @Override
    public R<List<CardMetricChooseDto>> currentUserChooseVo() {
        return R.ok(workbenchCardMetricService.currentUserChooseVo());
    }

    @Override
    public R<Void> modifyChoosedCard(List<CardMetricChooseDto> req) {
        workbenchCardMetricService.modifyChoosedCard(req);
        return R.ok();
    }

}
