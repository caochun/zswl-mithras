package cn.zswltech.mithras.archives.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.archives.mapper.ArchivesDownloadPermissionMapper;
import cn.zswltech.mithras.archives.mapper.model.ArchivesDownloadPermission;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ArchivesDownloadFlow;

/**
 * @create: 2023-03-20
 **/
@Component
public class ArchivesDownloadEndHandler extends AbstractProcessEndHandler {

    @Resource
    private ArchivesDownloadPermissionMapper archivesDownloadPermissionMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ArchivesDownloadFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        if(ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType())){
            archivesDownloadPermissionMapper.delete(Wrappers.<ArchivesDownloadPermission>lambdaUpdate().eq(ArchivesDownloadPermission::getBatch,endContext.getBusinessKey()));
            return;
        }
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        ArchivesDownloadPermission permission = new ArchivesDownloadPermission();
        permission.setStatus(2);
        if (processPass) {
            permission.setStatus(1);
        }
        archivesDownloadPermissionMapper.update(permission, Wrappers.<ArchivesDownloadPermission>lambdaUpdate().eq(ArchivesDownloadPermission::getBatch,endContext.getBusinessKey()));
    }
}
