package cn.zswltech.mithras.kpi.distribution.versioning;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.kpi.mapper.lib.KpiProjectDistributionWeightLibMapper;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeightLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionWeightLibService extends ServiceImpl<KpiProjectDistributionWeightLibMapper, KpiProjectDistributionWeightLib> {
    public List<KpiProjectDistributionWeightLib> listEffectByProjectDistributionId(Long projectDistributionId) {
        LambdaQueryWrapper<KpiProjectDistributionWeightLib> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionWeight::getProjectDistributionId, projectDistributionId);
        query.eq(KpiProjectDistributionWeightLib::getVersionType, VersionTypeConstants.NORMAL);
        return this.list(query);
    }

    public List<KpiProjectDistributionWeightLib> listByMainIdAndVersion(Long projectDistributionId, String version) {
        LambdaQueryWrapper<KpiProjectDistributionWeightLib> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionWeight::getProjectDistributionId, projectDistributionId);
        query.eq(KpiProjectDistributionWeightLib::getVersion, version);
        return this.list(query);
    }

    public List<KpiProjectDistributionWeightLib> listByKpiProjectDistributionBaseInfoLibs(List<KpiProjectDistributionBaseInfoLib> distributionListRSP) {
        if (ObjectUtil.isEmpty(distributionListRSP)) {
            return ListUtil.empty();
        }
        LambdaQueryWrapper<KpiProjectDistributionWeightLib> query = Wrappers.lambdaQuery();
        distributionListRSP.forEach(e -> {
            query.or(true, innerQuery -> {
                innerQuery.eq(KpiProjectDistributionWeightLib::getProjectDistributionId, e.getProjectDistributionId());
                innerQuery.eq(KpiProjectDistributionWeightLib::getVersion, e.getVersion());
            });
        });
        return this.list(query);
    }
}
