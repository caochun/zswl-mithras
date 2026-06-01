package cn.zswltech.mithras.service.service.lib.groupcreditestablish.service.impl;

import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.groupcreditestablish.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler.GroupCreditEstablishLibAbstractHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditEstablishVersionServiceImpl extends CommonVersionService<GroupCreditEstablishBaseInfo> {
    private static final String DEFAULT_USER_NAME = "未知用户";

    @Resource
    private List<GroupCreditEstablishLibAbstractHandler> libHandlerList;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void customFlushData(GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (GroupCreditEstablishLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, groupCreditEstablishBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(mainId);
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
        for (GroupCreditEstablishLibAbstractHandler libHandler : libHandlerList) {
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
    @Transactional(rollbackFor = Throwable.class)
    public void customReset(GroupCreditEstablishBaseInfo baseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (GroupCreditEstablishLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(baseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public void validateData(Long mainId) {

    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (GroupCreditEstablishLibAbstractHandler libHandler : libHandlerList) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, GroupCreditEstablishBaseInfo baseModel, Map<Long, String> userNameMap) {
        GroupCreditEstablishVersionListRSP rsp = new GroupCreditEstablishVersionListRSP();
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
        return "GROUP_CREDIT_ESTABLISH";
    }
}
