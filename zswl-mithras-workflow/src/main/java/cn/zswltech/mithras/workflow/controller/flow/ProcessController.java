package cn.zswltech.mithras.workflow.controller.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.ProcessApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.execution.MockStartComplexProcessREQ;
import cn.zswltech.mithras.dto.flow.execution.MockStartProcessREQ;
import cn.zswltech.mithras.dto.flow.search.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.workflow.flow.ProcessApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController implements ProcessApi {
    @Resource
    private ProcessApplicationService processApplicationService;

    @Override
    public R<List<SelectRSP>> listTransferUser(@Valid TransferUserListREQ req) {
        return processApplicationService.listTransferUser(req);
    }

    @Override
    public R<PageR<ProcessListRSP>> list(ProcessListREQ req) {
        return processApplicationService.list(req);
    }

    @Override
    public R<ProcessTraceRSP> trace(ProcessBaseREQ req) {
        return processApplicationService.trace(req);
    }

    @Override
    public R<PageR<ProcessHistoryRSP>> history(ProcessHistoryREQ req) {
        return processApplicationService.history(req);
    }

    @Override
    public R<List<ProcessNodeRSP>> queryCanJumpNodes(ProcessBaseREQ req) {
        return processApplicationService.queryCanJumpNodes(req);
    }

    @Override
    public R<Void> start(MockStartProcessREQ req) {
        return processApplicationService.start(req);
    }

    @Override
    public R<Void> startComplex(MockStartComplexProcessREQ req) {
        return processApplicationService.startComplex(req);
    }

    @Override
    public void mockTrace(String processInstanceId) {
        processApplicationService.mockTrace(processInstanceId);
    }

    @Override
    public byte[] getProcessBpmnXml(String processInstanceId) {
        return processApplicationService.getProcessBpmnXml(processInstanceId);
    }

    @Override
    public R<ProcessPictureRSP> getProcessPictureData(String processInstanceId) {
        return processApplicationService.getProcessPictureData(processInstanceId);
    }
}
