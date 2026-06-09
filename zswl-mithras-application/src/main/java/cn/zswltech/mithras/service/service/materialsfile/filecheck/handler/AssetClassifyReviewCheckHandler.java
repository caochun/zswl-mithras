package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * 五级分类上传
 *
 * @author: jackerhe
 * @date: 2023/7/14 10:08 上午
 **/
@Component
public class AssetClassifyReviewCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ASSET_CLASSIFY_REVIEW.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 资产管理岗可发起
        checkJob();
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        checkJob();
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        checkJob();
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mainId, fileId);
    }

    private void checkJob() {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        // 资产管理岗可发起
        for (String jobCode : jobList) {
            if (Objects.equals(JobEnum.assetmanagement.name(), jobCode)) {
                return;
            }
        }
        throw new MithrasException("仅资产管理岗可操作");
    }
}
