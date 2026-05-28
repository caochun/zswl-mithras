package cn.zswltech.mithras.service.service.lib.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.finance.FinanceOverdueReportBaseMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description
 * @since
 */
@Service
public class FinanceOverdueVersionManagerService extends CommonVersionService<FinanceOverdueReportBase> {
    @Autowired
    private List<FinanceOverdueAbstractHandler> libHandlerList;
    @Resource
    private FinanceOverdueReportBaseMapper financeOverdueReportBaseMapper;

    private static final String RECEIPT_ID = "receiptId";

    private static final String ESTIMATED_LEASE_DATE = "estimatedLeaseDate";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customFlushData(FinanceOverdueReportBase baseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (FinanceOverdueAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, baseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        FinanceOverdueReportBase baseInfo = financeOverdueReportBaseMapper.selectById(mainId);
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
        for (FinanceOverdueAbstractHandler libHandler : libHandlerList) {
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
    public void customReset(FinanceOverdueReportBase baseInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (FinanceOverdueAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(baseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        FinanceOverdueReportBase base = financeOverdueReportBaseMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(base)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (FinanceOverdueAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(base.getId())) {
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
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, FinanceOverdueReportBase baseModel, Map<Long, String> userNameMap) {
        return BeanUtil.copyProperties(cv, CommonVersionListRSP.class);
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FINANCE_OVERDUE;
    }

}
