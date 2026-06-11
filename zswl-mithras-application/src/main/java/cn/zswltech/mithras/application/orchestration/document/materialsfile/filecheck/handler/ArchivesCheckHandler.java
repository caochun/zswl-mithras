package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.archives.mapper.ArchivesDownloadPermissionMapper;
import cn.zswltech.mithras.archives.mapper.ArchivesManagementMapper;
import cn.zswltech.mithras.archives.mapper.model.ArchivesDownloadPermission;
import cn.zswltech.mithras.archives.mapper.model.ArchivesManagement;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2023-03-09
 **/

@Component
public class ArchivesCheckHandler extends FileModuleCheck {

    @Resource
    private ArchivesDownloadPermissionMapper archivesDownloadPermissionMapper;
    @Resource
    private ArchivesManagementMapper archivesManagementMapper;
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
        ArchivesManagement management = archivesManagementMapper.selectById(mainId);
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(management.getProjId());

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
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<ArchivesDownloadPermission> permissions = archivesDownloadPermissionMapper.selectList(Wrappers.<ArchivesDownloadPermission>lambdaQuery().in(ArchivesDownloadPermission::getMaterialsId, fileIds).ge(ArchivesDownloadPermission::getExpires, LocalDateTime.now()).eq(ArchivesDownloadPermission::getUserId, loginInfo.getId()).eq(ArchivesDownloadPermission::getStatus, 1));
        if (CollectionUtil.isEmpty(permissions)){
            throw new MithrasException("无权下载文件！");
        }
        Set<Long> idSet = permissions.stream().map(ArchivesDownloadPermission::getMaterialsId).collect(Collectors.toSet());
        List<Long> noPermission = fileIds.stream().filter(o -> !idSet.contains(o)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noPermission)){
            throw new MithrasException("部分文件无权下载！");
        }
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ARCHIVES.name();
    }
}
