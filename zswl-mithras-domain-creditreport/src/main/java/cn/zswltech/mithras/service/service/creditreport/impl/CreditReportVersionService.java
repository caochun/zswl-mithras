
package cn.zswltech.mithras.service.service.creditreport.impl;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportBaseInfo;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.creditreport.CreditReportLibAbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CreditReportVersionService extends CommonVersionService<CreditReportBaseInfo> {

    @Autowired
    private List<CreditReportLibAbstractHandler> libHandlerList;

    @Override
    public void customFlushData(CreditReportBaseInfo creditReport, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (CreditReportLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, creditReport.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        return null;
    }


    @Override
    public void customReset(CreditReportBaseInfo creditReport, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (CreditReportLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(creditReport.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        return null;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, CreditReportBaseInfo baseModel, Map<Long, String> userNameMap) {
        return null;
    }

    @Override
    protected String getBusinessModuleName() {
        return "CREDIT_REPORT_SELECT";
    }
}

