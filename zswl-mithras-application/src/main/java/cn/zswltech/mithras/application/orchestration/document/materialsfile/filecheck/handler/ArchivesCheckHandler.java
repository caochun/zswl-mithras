package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.archives.application.ArchivesManageService;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @create: 2023-03-09
 **/

@Component
public class ArchivesCheckHandler extends FileModuleCheck {

    @Resource
    private ArchivesManageService archivesManageService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;


    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType) {
        if (Objects.isNull(mainId)) {
            throw new AuthCheckException("id不能为空");
        }
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        Long projId = archivesManageService.getProjectId(mainId);
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(projId);

        List<Long> cosponsorList = StringUtils.isBlank(baseInfo.getProjCosponsorUserIds())
                ? new ArrayList<>() : JSONArray.parseArray(baseInfo.getProjCosponsorUserIds(), Long.class);

        cosponsorList.add(baseInfo.getProjSponsorUserId());
        if (!cosponsorList.contains(AccountUtil.getLoginInfo().getId())) {
            throw new AuthCheckException("非数据主办或协办，不支持该种操作");
        }
        dataAuthProcessRule.check(moduleEnum, mainId);

    }



    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
        archivesManageService.checkDownloadPermission(fileIds);
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ARCHIVES.name();
    }
}
