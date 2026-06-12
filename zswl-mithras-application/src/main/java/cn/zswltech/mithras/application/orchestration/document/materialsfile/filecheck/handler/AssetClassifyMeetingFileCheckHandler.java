package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyMaterialsEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyMeetingFileMaterialsEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * 五级分类会议文件上传
 *
 * @author: jackerhe
 * @date: 2023/7/14 10:08 上午
 **/
@Component
public class AssetClassifyMeetingFileCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private MaterialsListService materialsListService;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ASSET_CLASSIFY.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 资产管理岗可发起
        if (checkJob(JobEnum.assetmanagement)) {
            return;
        }
        if (materialsType.equals(AssetClassifyMeetingFileMaterialsEnum.ASSET_CLASSIFY_RISK_MEETING.name())) {
            if (checkJob(JobEnum.risksecretary)) {
                return;
            }
        } else if (materialsType.equals(AssetClassifyMeetingFileMaterialsEnum.ASSET_CLASSIFY_REVIEW_MEETING.name())) {
            if (checkJob(JobEnum.secretaryjury)) {
                return;
            }
        }
        throw new MithrasException("无权限操作");
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList byId = materialsListService.getById(fileId);
        if (Objects.nonNull(byId) && Objects.isNull(byId.getCreateBy())) {
            // 没有创建人就算了
            return;
        }
        if (!Objects.equals(byId.getCreateBy(), AccountUtil.getLoginInfo().getId())) {
            throw new MithrasException("上传人才可以删除");
        }
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        List<MaterialsList> byIds = materialsListService.getByIds(fileIds);
        for (MaterialsList byId : byIds) {
            if (Objects.nonNull(byId) && Objects.isNull(byId.getCreateBy())) {
                // 没有创建人就算了
                continue;
            }
            if (!Objects.equals(byId.getCreateBy(), AccountUtil.getLoginInfo().getId())) {
                throw new MithrasException("上传人才可以删除");
            }
        }
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

    private boolean checkJob(JobEnum job) {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        // 资产管理岗可发起
        for (String jobCode : jobList) {
            if (Objects.equals(job.name(), jobCode)) {
                return true;
            }
        }
        return false;
    }
}
