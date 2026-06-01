package cn.zswltech.mithras.service.service.lib.association.impl;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportApply;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @since
 */
@Service
public class AssociationReportVersionServiceServiceImpl extends CommonVersionService<AssociationReportApply> {
    @Resource
    private List<AssociationReportLibAbstractHandler> libHandlerList;


    @Override
    public void customFlushData(AssociationReportApply associationReportApply, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AssociationReportLibAbstractHandler libHandler : libHandlerList) {
            String reportInstanceIds  = associationReportApply.getReportInstanceIds();
            List<String> reportInstanceIdList = Arrays.asList(reportInstanceIds.split(","));
            reportInstanceIdList.forEach(reportInstanceId -> {
                libHandler.flushData(version, Long.valueOf(reportInstanceId), needClearLastFlag, versionType);
            });
        }
    }

    @Override
    public void customReset(AssociationReportApply associationReportApply, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AssociationReportLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(associationReportApply.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, AssociationReportApply baseModel, Map<Long, String> userNameMap) {
        return null;
    }

    @Override
    protected String getBusinessModuleName() {
        return "ASSOCIATION_REPORT_APPLY";
    }

}
