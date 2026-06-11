package cn.zswltech.mithras.contract.application.auth;


import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthSponsorUserGuard;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/12/19
 * @description
 */
@Component
public class ContractRentActualAuthChecker implements IDataAuthChecker {
    @Resource
    private ProcessService processService;
    @Resource
    private DataAuthSponsorUserGuard dataAuthSponsorUserGuard;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        dataAuthSponsorUserGuard.check(businessModule, keyId);
        ProcessResp zhiZuProcess = processService.findRelatedProcess(keyId.toString(), Arrays.asList(ProcessModelTypeEnum.ContractStartRentFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name()));
        ProcessResp feiZhiZuProcess = processService.findRelatedProcess(keyId.toString(), Arrays.asList(ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name()));
        if (Objects.nonNull(feiZhiZuProcess)) {
            boolean isStartUser = FlowUtil.isStartUserNode(feiZhiZuProcess);
            boolean isProjectSponsor = FlowUtil.isSpecificNode(feiZhiZuProcess, "userTask_projectSponsor");
            if (!isStartUser && !isProjectSponsor) {
                throw new AuthCheckException("审批流程中，仅发起人节点和项目经理节点可操作");
            }
        }
        if (Objects.nonNull(zhiZuProcess)) {
            boolean isStartUser = FlowUtil.isStartUserNode(zhiZuProcess);
            if (!isStartUser) {
                throw new AuthCheckException("审批流程中，仅发起人节点可操作");
            }
        }
        return true;
    }
}
