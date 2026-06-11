package cn.zswltech.mithras.projectprocess.versioning.projestablish.impl;

import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionListRSP;
import cn.zswltech.mithras.dto.version.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.projectprocess.versioning.projestablish.handler.ProjEstablishLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishVersionServiceImpl extends CommonVersionService<ProjEstablishBaseInfo> {
    private static final String DEFAULT_USER_NAME = "未知用户";

    @Resource
    private List<ProjEstablishLibAbstractHandler> libHandlerList;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;

    @Override
    public void customFlushData(ProjEstablishBaseInfo establishBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (ProjEstablishLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, establishBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (ProjEstablishLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getNeedApprovalChangeFlag())) {
                    // 快速返回
                    changeDTO.setChangeFlag(true);
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                }
            }
        }
        return changeDTO;
    }


    @Override
    public void customReset(ProjEstablishBaseInfo baseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (ProjEstablishLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(baseInfo.getId(), commonVersion.getVersion());
        }
    }


    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (ProjEstablishLibAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getId())) {
                CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
                oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
                newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
                moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
            }
        }
        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, ProjEstablishBaseInfo baseModel, Map<Long, String> userNameMap) {
        ProjEstablishVersionListRSP rsp = new ProjEstablishVersionListRSP();
        rsp.setId(cv.getId());
        rsp.setMainId(cv.getMainId());
        rsp.setVersion(cv.getVersion());
        rsp.setType(cv.getType());
        rsp.setModule(cv.getModule());
        rsp.setCreateTime(cv.getCreateTime());
        rsp.setCreateBy(cv.getCreateBy());
        rsp.setUpdateTime(cv.getUpdateTime());
        rsp.setUpdateBy(cv.getUpdateBy());
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(DEFAULT_USER_NAME));
        rsp.setProjName(baseModel.getProjName());
        return rsp;
    }

    @Override
    protected String getBusinessModuleName() {
        return "PROJ_ESTABLISH";
    }
}
