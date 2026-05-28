package cn.zswltech.mithras.service.controller.flow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.FlowFileApi;
import cn.zswltech.mithras.dto.FlowFocusFileREQ;
import cn.zswltech.mithras.dto.FlowFocusFileRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileListREQ;
import cn.zswltech.mithras.dto.flow.file.FlowFileListRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileRemoveREQ;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.flow.file.FlowFocusFileSelectorFactory;
import cn.zswltech.mithras.service.service.bo.FileBO;
import cn.zswltech.mithras.service.service.flow.FlowFileService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 审批流里 动态表单 上传文件
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:52 AM
 */
@RestController
public class FlowFileController implements FlowFileApi {

    @Resource
    private FlowFileService flowFileService;
    @Resource
    private FlowFocusFileSelectorFactory flowFocusFileSelectorFactory;

    @Override
    public R<Void> uploadApproval(MultipartFile file, String materialsType,String taskId, String processInstanceId) {
        flowFileService.uploadApproval(file, materialsType, processInstanceId, taskId);
        return R.ok();
    }

    @Override
    public R<List<FlowFileListRSP>> listApproval(FlowFileListREQ req) {
        return R.ok(flowFileService.listApproval(req));
    }

    @Override
    public R<Void> removeApproval(FlowFileRemoveREQ req) {
        flowFileService.removeApproval(req);
        return R.ok();
    }

    @Override
    public R<List<FlowFocusFileRSP>> listBizImportantFile(@Valid FlowFocusFileREQ req) {
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(req.getProcessModuleType());
        if (Objects.isNull(processModelTypeEnum)) {
            return R.fail("未定义的流程模型类型");
        }
        List<FileBO> fileBOList = flowFocusFileSelectorFactory.getInstance(processModelTypeEnum).listBizImportantFile(req.getBusinessKey(), req.getVersion());
        List<FlowFocusFileRSP> result = fileBOList.stream().map(e -> new FlowFocusFileRSP(e.getFileId(), e.getFileName(), e.getIdType())).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<List<FlowFocusFileRSP>> listMeetingDecisionFile(@Valid FlowFocusFileREQ req) {
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(req.getProcessModuleType());
        if (Objects.isNull(processModelTypeEnum)) {
            return R.fail("未定义的流程模型类型");
        }
        List<FileBO> fileBOList = flowFocusFileSelectorFactory.getInstance(processModelTypeEnum).listMeetingDecisionFile(req.getBusinessKey(), req.getVersion());
        List<FlowFocusFileRSP> result = fileBOList.stream().map(e -> new FlowFocusFileRSP(e.getFileId(), e.getFileName(), e.getIdType())).collect(Collectors.toList());
        return R.ok(result);
    }

}
