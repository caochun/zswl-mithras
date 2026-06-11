package cn.zswltech.mithras.fund.application.lib.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.fund.application.lib.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Service
public class FundFinancingLibVersionService extends CommonVersionService<FundFinancingBaseInfo> {
    private static final String DEFAULT_USER_NAME = "未知用户";

    @Autowired
    private List<FundFinancingAbstractLibHandler> libHandlerList;

    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;

    @Override
    public void customFlushData(FundFinancingBaseInfo financingBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        for (FundFinancingAbstractLibHandler libHandler : libHandlerList) {
            libHandler.flushData(version, financingBaseInfo.getMainId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoMapper.selectById(mainId);
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
        for (FundFinancingAbstractLibHandler libHandler : libHandlerList) {
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
    public void customReset(FundFinancingBaseInfo financingBaseInfo, CommonVersion commonVersion) {
        for (FundFinancingAbstractLibHandler libHandler : libHandlerList) {
            libHandler.reset(financingBaseInfo.getMainId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (FundFinancingAbstractLibHandler libHandler : libHandlerList) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, FundFinancingBaseInfo baseModel, Map<Long, String> userNameMap) {
        FundFinancingVersionListRSP rsp = BeanUtil.copyProperties(cv, FundFinancingVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(DEFAULT_USER_NAME));
        rsp.setFinancingCode(baseModel.getFinancingCode());
        return rsp;
    }

    @Override
    protected String getBusinessModuleName() {
        return "FUND_FINANCING";
    }
}
