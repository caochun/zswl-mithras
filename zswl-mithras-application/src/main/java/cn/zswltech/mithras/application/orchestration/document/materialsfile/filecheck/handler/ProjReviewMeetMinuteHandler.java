package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * @ClassName AfterLeaseCollectionCheck
 * 项目评审文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:51 上午
 * @Version 1.0
 **/
@Component
public class ProjReviewMeetMinuteHandler extends FileModuleCheck {


    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private SysUserService sysUserService;
    @Resource
    protected FileConvert fileConvert;


    @Override
    public String getModuleKey() {
        return "PROJ_REVIEW_MEET_MINUTE";
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 确定是否为项目经理或评审会秘书
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name(), JobEnum.secretaryjury.name())) {
         throw new MithrasException("仅评审会秘书或项目经理可上传");
        }
    }


    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), materialsList.getCreateBy())) {
            // 上传尽调报告 或 其他文件
            throw new AuthCheckException("仅上传用户可删除");
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        //下载权限同查看暂不限制
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        List<MaterialsList> byIds = materialsListService.getByIds(fileIds);
        if (ObjectUtil.isNotEmpty(byIds)) {
            for(MaterialsList list : byIds) {
                if (!ObjectUtil.equals(list.getCreateBy(), AccountUtil.getLoginInfo().getId())) {
                    throw new MithrasException("非文件上传人，不可删除");
                }
            }
        }
    }


    protected int getGroupFileSort(FileListRSP rsp) {
        return 0;
    }

}
