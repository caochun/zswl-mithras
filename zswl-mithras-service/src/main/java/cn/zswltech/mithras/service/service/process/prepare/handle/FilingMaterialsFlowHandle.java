package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsFactory;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
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
    private FilingMaterialsService filingMaterialsService;
    @Resource
    private FundFilingMaterialsService fundFilingMaterialsService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equalsAny(processType, ProcessModelTypeEnum.FilingMaterialsApplyFlow.name(),ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        return FilingMaterialsFactory.getProcessor(prepare.getProcessType()).startProcess(new FilingBaseREQ(Long.parseLong(prepare.getBusinessId())));
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        if (StrUtil.isNotBlank(prepare.getBusinessId())) {
            FilingMaterialsFactory.getProcessor(prepare.getProcessType()).close(new FilingBaseREQ(Long.parseLong(prepare.getBusinessId())));
        }
    }

}
