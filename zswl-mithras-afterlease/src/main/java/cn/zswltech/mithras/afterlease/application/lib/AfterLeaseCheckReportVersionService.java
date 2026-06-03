package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description 重要！！！AfterLeaseCheckPlanProject虽然在此处使用，但仅仅是因为所有报告表关联了该表主键，实际该表的版本和报告表的版本没有半毛钱关系
 */
@Component
public class AfterLeaseCheckReportVersionService extends CommonVersionService<NewAfterLeaseCheckPlanClient> {
    @Autowired
    private List<AfterLeaseCheckReportLibAbstractHandler> libHandlerList;

    @Override
    public void customFlushData(NewAfterLeaseCheckPlanClient checkPlanProject, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AfterLeaseCheckReportLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, checkPlanProject.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public void customReset(NewAfterLeaseCheckPlanClient checkPlanProject, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AfterLeaseCheckReportLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(checkPlanProject.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, NewAfterLeaseCheckPlanClient baseModel, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected String getBusinessModuleName() {
        return "AFTER_LEASE_CHECK_PROJECT";
    }
}
