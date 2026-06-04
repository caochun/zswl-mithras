package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
public class FtpQuarterlyGuidanceHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        super.checkRemove(moduleKey, fileId);
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        super.checkRemove(moduleKey, fileIds);
    }

    @Override
    public void checkList(String moduleKey, Long mainId) {
        super.checkList(moduleKey, mainId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        super.checkDownload(moduleKey, mainId, fileId);
    }

    @Override
    public void checkTemplateDownload(String moduleKey, String templateId) {
        if(!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()){
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
    }
}
