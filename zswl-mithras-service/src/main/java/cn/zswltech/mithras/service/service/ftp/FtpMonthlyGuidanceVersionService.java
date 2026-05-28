package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.zswltech.mithras.dto.ftp.FtpVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.MithrasConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.ftp.FtpMonthlyGuidanceMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpMonthlyGuidance;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.ftp.handler.AbstractFtpMonthlyLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 17:10
 */
@Service
public class FtpMonthlyGuidanceVersionService extends CommonVersionService<FtpMonthlyGuidance> {
    @Resource
    private FtpMonthlyGuidanceMapper guidanceMapper;
    @Resource
    private List<AbstractFtpMonthlyLibHandler> libHandlerList;

    @Override
    public void customFlushData(FtpMonthlyGuidance guidance, String version, boolean needClearLastFlag, Integer versionType) {
        for (AbstractFtpMonthlyLibHandler libHandler : libHandlerList) {
            libHandler.flushData(version, guidance.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        FtpMonthlyGuidance guidance = guidanceMapper.selectById(mainId);
        if (Objects.isNull(guidance)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (AbstractFtpMonthlyLibHandler libHandler : libHandlerList) {
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
        return changeDTO;
    }

    @Override
    public void customReset(FtpMonthlyGuidance guidance, CommonVersion commonVersion) {
        for (AbstractFtpMonthlyLibHandler libHandler : libHandlerList) {
            libHandler.reset(guidance.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        FtpMonthlyGuidance guidance = guidanceMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(guidance)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (AbstractFtpMonthlyLibHandler libHandler : libHandlerList) {
            CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
            oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
            newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
            moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
        }
        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, FtpMonthlyGuidance baseModel, Map<Long, String> userNameMap) {
        FtpVersionListRSP rsp = BeanUtil.copyProperties(cv, FtpVersionListRSP.class);
        String guidanceName = "FTP定价测算表" + baseModel.getYear() + String.format("%02d", baseModel.getMonth());
        rsp.setGuidanceName(guidanceName);
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FTP_MONTHLY_GUIDANCE;
    }
}
