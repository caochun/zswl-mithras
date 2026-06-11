package cn.zswltech.mithras.financeprojectdistribution.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionDeptWeightLibMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lllin
 * @date 2025-12-19
 * @description
 */
@Slf4j
@Service
public class FinanceProjectDistributionDeptWeightLibService extends ServiceImpl<FinanceProjectDistributionDeptWeightLibMapper, FinanceProjectDistributionDeptWeightLib> {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    public List<FinanceProjectDistributionDeptWeightInfo> buildDeptWeightInfoByDistributionId(Long distributionId, String version) {
        FinanceProjectDistribution distributionServiceById = financeProjectDistributionService.getById(distributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }

        // 如果不传版本，则默认取最新版本
        List<FinanceProjectDistributionDeptWeightLib> financeProjectDistributionDeptWeightLibs;
        if (CharSequenceUtil.isNotBlank(version)) {
            financeProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<FinanceProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(FinanceProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                    .eq(FinanceProjectDistributionDeptWeightLib::getVersion, version));
        } else {
            FinanceProjectDistributionDeptWeightLib selected = baseMapper.selectOne(Wrappers.<FinanceProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(FinanceProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                    .eq(FinanceProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(FinanceProjectDistributionDeptWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(selected)) {
                financeProjectDistributionDeptWeightLibs = new ArrayList<>();
            } else {
                financeProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<FinanceProjectDistributionDeptWeightLib>lambdaQuery()
                        .eq(FinanceProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                        .eq(FinanceProjectDistributionDeptWeightLib::getVersion, selected.getVersion()));
            }
        }
        if (CollUtil.isEmpty(financeProjectDistributionDeptWeightLibs)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(financeProjectDistributionDeptWeightLibs.stream()
                .map(FinanceProjectDistributionDeptWeightLib::getWeightTarget).collect(Collectors.toList()));

        List<FinanceProjectDistributionDeptWeightInfo> collect = financeProjectDistributionDeptWeightLibs.stream().map(lib -> {
            FinanceProjectDistributionDeptWeightInfo info = new FinanceProjectDistributionDeptWeightInfo();
            info.setIsBusinessDept(false);
            info.setId(lib.getId());
            info.setWeightValue(lib.getWeightValue());
            info.setWeightTarget(lib.getWeightTarget());
            info.setWeightTargetName(deptId2NameMap.get(lib.getWeightTarget()));
            return info;
        }).collect(Collectors.toList());
        collect.get(0).setIsBusinessDept(true);
        return collect;
    }
}
