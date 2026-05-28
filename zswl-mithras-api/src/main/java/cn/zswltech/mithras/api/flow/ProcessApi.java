package cn.zswltech.mithras.api.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.execution.MockStartComplexProcessREQ;
import cn.zswltech.mithras.dto.flow.execution.MockStartProcessREQ;
import cn.zswltech.mithras.dto.flow.search.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 流程
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:08 PM
 */
@Api(tags = "流程相关-流程-接口")
@RequestMapping("/flow/process")
public interface ProcessApi {
    @ApiOperation("查询可转办人员")
    @PostMapping("/queryCanTransferUser")
    R<List<SelectRSP>> listTransferUser(@RequestBody @Valid TransferUserListREQ req);

    @ApiOperation("流程查询")
    @PostMapping("/list")
    R<PageR<ProcessListRSP>> list(@RequestBody @Valid ProcessListREQ req);

    @ApiOperation("流程追踪")
    @PostMapping("/trace")
    R<ProcessTraceRSP> trace(@RequestBody @Valid ProcessBaseREQ req);

    @ApiOperation("流程历史操作")
    @PostMapping("/history")
    R<PageR<ProcessHistoryRSP>> history(@RequestBody @Valid ProcessHistoryREQ req);

    @ApiOperation("查询可跳转节点")
    @PostMapping("/queryCanJumpNodes")
    R<List<ProcessNodeRSP>> queryCanJumpNodes(@RequestBody @Valid ProcessBaseREQ req);

    @ApiOperation("mock发起测试流程")
    @PostMapping("/mock/start")
    R<Void> start(@RequestBody @Valid MockStartProcessREQ req);

    @ApiOperation("mock发起符合测试流程")
    @PostMapping("/mock/startComplex")
    R<Void> startComplex(@RequestBody @Valid MockStartComplexProcessREQ req);

    @ApiOperation("mock流程图追踪")
    @GetMapping("/mock/trace")
    void mockTrace(@RequestParam("processInstanceId") String processInstanceId);

    @ApiOperation("获取流程实例对应的流程图")
    @GetMapping(value = "/getProcessBpmnXml", produces = MediaType.TEXT_XML_VALUE)
    byte[] getProcessBpmnXml(@RequestParam("processInstanceId") String processInstanceId);

    @ApiOperation("获取流程实例对应的流程图中需高亮的数据")
    @GetMapping("/getProcessPictureData")
    R<ProcessPictureRSP> getProcessPictureData(@RequestParam("processInstanceId") String processInstanceId);

}
