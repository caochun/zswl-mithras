package cn.zswltech.mithras.service.service.lib.groupcreditreview.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.MithrasConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.groupcreditreview.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.GroupCreditReviewLibAbstractHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditReviewVersionServiceImpl extends CommonVersionService<GroupCreditReviewBaseInfo> {
    @Resource
    private List<GroupCreditReviewLibAbstractHandler> libHandlerList;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;

    @Override
    public void customFlushData(GroupCreditReviewBaseInfo baseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (GroupCreditReviewLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, baseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(mainId);
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
        for (GroupCreditReviewLibAbstractHandler libHandler : libHandlerList) {
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
    @Transactional(rollbackFor = Exception.class)
    public void customReset(GroupCreditReviewBaseInfo baseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (GroupCreditReviewLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(baseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (GroupCreditReviewLibAbstractHandler libHandler : libHandlerList) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, GroupCreditReviewBaseInfo baseModel, Map<Long, String> userNameMap) {
        GroupCreditReviewVersionListRSP rsp = BeanUtil.copyProperties(cv, GroupCreditReviewVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        rsp.setProjName(baseModel.getProjName());
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.GROUP_CREDIT_REVIEW;
    }
}
