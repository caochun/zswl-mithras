package cn.zswltech.mithras.workflow.interfaces.flow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.flow.execution.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.workflow.application.flow.api.ExecutionApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutionController implements ExecutionApi {
    @Resource
    private ExecutionApplicationService executionApplicationService;

    @Override
    public R<Void> batchPass(@Valid ExecutionBatchPassREQ req) {
        return executionApplicationService.batchPass(req);
    }

    @Override
    public R<Void> pass(ExecutionPassREQ req) {
        return executionApplicationService.pass(req);
    }

    @Override
    public R<Void> reject(ExecutionTaskBaseREQ req) {
        return executionApplicationService.reject(req);
    }

    @Override
    public R<Void> backToStartUser(ExecutionBackToStartUserREQ req) {
        return executionApplicationService.backToStartUser(req);
    }

    @Override
    public R<Void> backToStep(ExecutionBackToStepREQ req) {
        return executionApplicationService.backToStep(req);
    }

    @Override
    public R<Void> collaborate(ExecutionCollaborateREQ req) {
        return executionApplicationService.collaborate(req);
    }

    @Override
    public R<Void> cc(ExecutionProcessBaseREQ req) {
        return executionApplicationService.cc(req);
    }

    @Override
    public R<Void> cancelProcess(ExecutionProcessBaseREQ req) {
        return executionApplicationService.cancelProcess(req);
    }

    @Override
    public R<Void> withdrawToStartUser(ExecutionProcessBaseREQ req) {
        return executionApplicationService.withdrawToStartUser(req);
    }

    @Override
    public R<Void> withdrawTask(ExecutionTaskBaseREQ req) {
        return executionApplicationService.withdrawTask(req);
    }

    @Override
//    @AdminAuthCheck
    public R<Void> transfer(ExecutionTransferREQ req) {
        return executionApplicationService.transfer(req);
    }

    @Override
    public R<Void> jump(ExecutionJumpREQ req) {
        return executionApplicationService.jump(req);
    }

    @Override
    public R<Void> passAll(ExecutionProcessBaseREQ req) {
        return executionApplicationService.passAll(req);
    }

    @Override
    public R<Void> rejectAll(ExecutionProcessBaseREQ req) {
        return executionApplicationService.rejectAll(req);
    }

    @Override
    public R<Void> randomReturn(@Valid ExecutionRandomReturnREQ req) {
        return executionApplicationService.randomReturn(req);
    }
}
