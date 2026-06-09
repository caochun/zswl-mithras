package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.archives.domain.enums.ArchivesFlowStatusEnum;
import cn.zswltech.mithras.archives.domain.enums.ArchivesStatusEnum;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.ArchivesManagementMapper;
import cn.zswltech.mithras.archives.infrastructure.persistence.model.ArchivesManagement;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.ArchivesFlow;

/**
 * @create: 2023-03-20
 **/
@Component
public class ArchivesEndHandler extends AbstractProcessEndHandler {

    @Resource
    private ArchivesManagementMapper archivesManagementMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ArchivesFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        ArchivesManagement management1 = archivesManagementMapper.selectById(endContext.getBusinessKey());
        ArchivesManagement management = new ArchivesManagement();
        if (processPass) {
            management.setFlowStatus(management1.getType() == 1 ? ArchivesFlowStatusEnum.APPROVAL_PASS.name() : ArchivesFlowStatusEnum.SYS_APPROVAL_PASS.name());
            management.setStatus(ArchivesStatusEnum.OVER.name());
        }else if(ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType())){
            management.setFlowStatus(management1.getType() == 1 ? ArchivesFlowStatusEnum.APPROVAL_CLOSE.name() : ArchivesFlowStatusEnum.SYS_APPROVAL_CLOSE.name());
        } else {
            management.setFlowStatus(management1.getType() == 1 ? ArchivesFlowStatusEnum.REJECT.name() : ArchivesFlowStatusEnum.SYS_REJECT.name());
        }
        archivesManagementMapper.update(management, Wrappers.<ArchivesManagement>lambdaUpdate().eq(ArchivesManagement::getId,endContext.getBusinessKey()));
    }
}
