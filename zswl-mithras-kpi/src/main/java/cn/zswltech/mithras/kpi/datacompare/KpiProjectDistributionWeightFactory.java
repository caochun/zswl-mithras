package cn.zswltech.mithras.kpi.datacompare;

import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfo;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.kpi.mapper.lib.KpiProjectDistributionWeightLibMapper;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.kpi.distribution.versioning.handler.impl.KpiProjectDistributionWeightLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-26
 **/
@Service("kpiProjectDistributionWeight")
public class KpiProjectDistributionWeightFactory implements EditdataCompareFactory {
    @Resource
    private KpiProjectDistributionWeightLibMapper libMapper;
    @Resource
    private KpiProjectDistributionWeightLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<KpiProjectDistributionWeight, KpiProjectDistributionWeightLib, KpiProjectDistributionWeightInfo>(rsps, libMapper, handler, commonVersionMapper, "KPI_PROJECT_DISTRIBUTION", version);
    }
}
