package cn.zswltech.mithras.service.service.lib.kpi;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.kpi.handler.KpiProjectDistributionAbstractLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Service
public class KpiProjectDistributionLibVersionService extends CommonVersionService<KpiProjectDistribution> {
    @Resource
    private List<KpiProjectDistributionAbstractLibHandler> libHandlerList;

    @Override
    public void customFlushData(KpiProjectDistribution kpiProjectDistribution, String version, boolean needClearLastFlag, Integer versionType) {
        for (KpiProjectDistributionAbstractLibHandler libHandler : libHandlerList) {
            libHandler.flushData(version, kpiProjectDistribution.getMainId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public void customReset(KpiProjectDistribution kpiProjectDistribution, CommonVersion commonVersion) {
        for (KpiProjectDistributionAbstractLibHandler libHandler : libHandlerList) {
            libHandler.reset(kpiProjectDistribution.getMainId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, KpiProjectDistribution kpiProjectDistribution, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION;
    }
}
