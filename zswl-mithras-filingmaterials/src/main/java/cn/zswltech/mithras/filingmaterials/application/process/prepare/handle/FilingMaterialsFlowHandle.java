package cn.zswltech.mithras.filingmaterials.application.process.prepare.handle;

import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.filingmaterials.application.process.prepare.FilingMaterialsProcessPreparePort;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lllin
 */
@Slf4j
@Component
public class FilingMaterialsFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FilingMaterialsProcessPreparePort filingMaterialsProcessPreparePort;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equalsAny(processType, ProcessModelTypeEnum.FilingMaterialsApplyFlow.name(),ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        return filingMaterialsProcessPreparePort.startProcess(prepare.getProcessType(), Long.parseLong(prepare.getBusinessId()));
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        if (StrUtil.isNotBlank(prepare.getBusinessId())) {
            filingMaterialsProcessPreparePort.close(prepare.getProcessType(), Long.parseLong(prepare.getBusinessId()));
        }
    }
}
