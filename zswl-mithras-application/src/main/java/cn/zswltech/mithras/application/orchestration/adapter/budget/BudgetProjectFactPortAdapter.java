package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.budget.application.port.BudgetProjectFactPort;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BudgetProjectFactPortAdapter implements BudgetProjectFactPort {

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;

    @Override
    public Map<Long, Long> countEffectiveProjectsByDept(LocalDateTime startTime, LocalDateTime endTime) {
        List<Pair<Long, Long>> deptProjectCountList = projEstablishBaseInfoMapper.countEffectProjectGroupByDept(startTime, endTime);
        return deptProjectCountList.stream()
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> a));
    }

    @Override
    public Map<Long, String> mapFtpIndustryCategoryByProjReviewIds(Collection<Long> projReviewIds) {
        if (CollectionUtil.isEmpty(projReviewIds)) {
            return Collections.emptyMap();
        }
        return projPricingBaseInfoMapper.selectList(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                        .in(ProjPricingBaseInfo::getProjReviewId, projReviewIds))
                .stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getProjCode()) && ObjectUtil.isNotEmpty(e.getFtpIndustryCategory()))
                .collect(Collectors.toMap(ProjPricingBaseInfo::getProjReviewId, ProjPricingBaseInfo::getFtpIndustryCategory, (a, b) -> a));
    }
}
