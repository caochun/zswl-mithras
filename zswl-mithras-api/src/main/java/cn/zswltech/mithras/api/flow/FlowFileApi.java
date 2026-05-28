package cn.zswltech.mithras.api.flow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.FlowFocusFileREQ;
import cn.zswltech.mithras.dto.FlowFocusFileRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileListREQ;
import cn.zswltech.mithras.dto.flow.file.FlowFileListRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 审批流里
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:45 AM
 */
@Api(tags = "审批流里-文件相关接口")
@RequestMapping("/flow/file")
public interface FlowFileApi {

    @ApiOperation("上传报告（审批时）")
    @PostMapping("/uploadApproval")
    R<Void> uploadApproval(@RequestParam("file") MultipartFile file,
                           @RequestParam("materialsType") String materialsType,
                           @RequestParam("taskId") String taskId,
                           @RequestParam("processInstanceId") String processInstanceId);

    @ApiOperation("获取报告清单列表（审批时）")
    @PostMapping("/listApproval")
    R<List<FlowFileListRSP>> listApproval(@RequestBody @Valid FlowFileListREQ req);


    @ApiOperation("删除报告（审批时）")
    @PostMapping("/removeApproval")
    R<Void> removeApproval(@RequestBody @Valid FlowFileRemoveREQ req);

    @ApiOperation("获取审批流业务重要文件")
    @PostMapping("/important/list")
    R<List<FlowFocusFileRSP>> listBizImportantFile(@RequestBody @Valid FlowFocusFileREQ req);

    @ApiOperation("获取审批流会议决议文件")
    @PostMapping("/decision/list")
    R<List<FlowFocusFileRSP>> listMeetingDecisionFile(@RequestBody @Valid FlowFocusFileREQ req);
}
