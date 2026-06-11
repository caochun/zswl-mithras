package cn.zswltech.mithras.workflow.controller.flow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.FlowFileApi;
import cn.zswltech.mithras.dto.FlowFocusFileREQ;
import cn.zswltech.mithras.dto.FlowFocusFileRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileListREQ;
import cn.zswltech.mithras.dto.flow.file.FlowFileListRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileRemoveREQ;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.workflow.flow.FlowFileApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlowFileController implements FlowFileApi {
    @Resource
    private FlowFileApplicationService flowFileApplicationService;

    @Override
    public R<Void> uploadApproval(MultipartFile file, String materialsType,String taskId, String processInstanceId) {
        return flowFileApplicationService.uploadApproval(file, materialsType, taskId, processInstanceId);
    }

    @Override
    public R<List<FlowFileListRSP>> listApproval(FlowFileListREQ req) {
        return flowFileApplicationService.listApproval(req);
    }

    @Override
    public R<Void> removeApproval(FlowFileRemoveREQ req) {
        return flowFileApplicationService.removeApproval(req);
    }

    @Override
    public R<List<FlowFocusFileRSP>> listBizImportantFile(@Valid FlowFocusFileREQ req) {
        return flowFileApplicationService.listBizImportantFile(req);
    }

    @Override
    public R<List<FlowFocusFileRSP>> listMeetingDecisionFile(@Valid FlowFocusFileREQ req) {
        return flowFileApplicationService.listMeetingDecisionFile(req);
    }
}
