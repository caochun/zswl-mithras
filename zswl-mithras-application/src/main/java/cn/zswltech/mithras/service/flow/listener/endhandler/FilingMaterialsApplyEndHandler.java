package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsFactory;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.filingmaterials.FundFilingMaterialsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.FilingMaterialsApplyFlow;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.FundFilingMaterialsApplyFlow;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.AfterFilingMaterialsApplyFlow;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.OtherFilingMaterialsApplyFlow;

/**
 * 资料归档流程结束处理handler
 * @create: 2025-12-03
 * @author lllin
 **/
@Component
public class FilingMaterialsApplyEndHandler extends AbstractProcessEndHandler {

    @Resource
    private FilingMaterialsService filingMaterialsService;
    @Resource
    private FundFilingMaterialsService fundFilingMaterialsService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), FilingMaterialsApplyFlow.name(),FundFilingMaterialsApplyFlow.name(),
                AfterFilingMaterialsApplyFlow.name(),OtherFilingMaterialsApplyFlow.name());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(ProcessEndContext endContext) {
        FilingMaterialsFactory.getProcessor(endContext.getModelKey()).processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), endContext.getProcessInstanceId());
    }
}
