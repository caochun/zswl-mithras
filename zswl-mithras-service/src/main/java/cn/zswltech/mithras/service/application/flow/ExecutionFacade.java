package cn.zswltech.mithras.service.application.flow;

import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.workflow.application.flow.api.ExecutionApplicationService;
import cn.zswltech.mithras.dto.flow.execution.*;
import cn.zswltech.mithras.service.auth.aop.AdminAuthCheck;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 流程相关 操作
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:45 PM
 */
@Slf4j
@Service
public class ExecutionFacade implements ExecutionApplicationService {
    @Resource
    private ExecutionService executionService;

    @Override
    public R<Void> batchPass(@Valid ExecutionBatchPassREQ req) {
//        List<String> failTaskIds = new LinkedList<>();
        for (String taskId : req.getTaskIdList()) {
            try {
                ExecutionPassREQ executionPassREQ = new ExecutionPassREQ();
                executionPassREQ.setTaskId(taskId);
                executionPassREQ.setButtonKey(ApprovalButtonTypeEnum.AGREE.name());
                executionService.pass(executionPassREQ);
            } catch (Exception e) {
                log.error("一键批量通过过程中有流程处理发生异常[taskId:{}]", taskId, e);
//                failTaskIds.add(taskId);
            }
        }
//        if (failTaskIds.size() == 0) {
//            return R.ok();
//        } else {
//            return R.fail("批量通过中有流程发生问题，未能完成审批");
//        }
        return R.ok();
    }

    @Override
    public R<Void> pass(ExecutionPassREQ req) {
        executionService.pass(req);
        return R.ok();
    }

    @Override
    public R<Void> reject(ExecutionTaskBaseREQ req) {
        executionService.reject(req);
        return R.ok();
    }

    @Override
    public R<Void> backToStartUser(ExecutionBackToStartUserREQ req) {
        executionService.backToStartUser(req);
        return R.ok();
    }

    @Override
    public R<Void> backToStep(ExecutionBackToStepREQ req) {
        executionService.backToStep(req);
        return R.ok();
    }

    @Override
    public R<Void> collaborate(ExecutionCollaborateREQ req) {
        executionService.collaborate(req);
        return R.ok();
    }

    @Override
    public R<Void> cc(ExecutionProcessBaseREQ req) {
        executionService.cc(req);
        return R.ok();
    }

    @Override
    public R<Void> cancelProcess(ExecutionProcessBaseREQ req) {
        executionService.cancelProcess(req);
        return R.ok();
    }

    @Override
    public R<Void> withdrawToStartUser(ExecutionProcessBaseREQ req) {
        executionService.withdrawToStartUser(req);
        return R.ok();
    }

    @Override
    public R<Void> withdrawTask(ExecutionTaskBaseREQ req) {
        executionService.withdrawTask(req);
        return R.ok();
    }

    @Override
//    @AdminAuthCheck
    public R<Void> transfer(ExecutionTransferREQ req) {
        executionService.transfer(req);
        return R.ok();
    }

    @Override
    @AdminAuthCheck
    public R<Void> jump(ExecutionJumpREQ req) {
        executionService.jump(req);
        return R.ok();
    }

    @Override
    @AdminAuthCheck
    public R<Void> passAll(ExecutionProcessBaseREQ req) {
        executionService.passAll(req);
        return R.ok();
    }

    @Override
    @AdminAuthCheck
    public R<Void> rejectAll(ExecutionProcessBaseREQ req) {
        executionService.rejectAll(req);
        return R.ok();
    }

    @Override
    public R<Void> randomReturn(@Valid ExecutionRandomReturnREQ req) {
        executionService.randomReturn(req);
        return R.ok();
    }
}
