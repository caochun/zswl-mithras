package cn.zswltech.mithras.financeprojectdistribution.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionDeptLaunchWeightLibMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionDeptLaunchWeightLib;
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
* @description 财务-部门-项目投放分配比重版本表
 * @author lllin
 * @date2025/12/19
*/
@Slf4j
@Service
public class FinanceProjectDistributionDeptLaunchWeightLibService extends ServiceImpl<FinanceProjectDistributionDeptLaunchWeightLibMapper, FinanceProjectDistributionDeptLaunchWeightLib> {


    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    public List<FinanceProjectDistributionDeptLaunchWeightInfo> buildDeptLaunchWeightInfoByDistributionId(Long distributionId, String version) {
        FinanceProjectDistribution distributionServiceById = financeProjectDistributionService.getById(distributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }

        // 如果不传版本，则默认取最新版本
        List<FinanceProjectDistributionDeptLaunchWeightLib> financeProjectDistributionDeptWeightLibs;
        if (CharSequenceUtil.isNotBlank(version)) {
            financeProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<FinanceProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(FinanceProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                    .eq(FinanceProjectDistributionDeptLaunchWeightLib::getVersion, version));
        } else {
            FinanceProjectDistributionDeptLaunchWeightLib selected = baseMapper.selectOne(Wrappers.<FinanceProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(FinanceProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                    .eq(FinanceProjectDistributionDeptLaunchWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(FinanceProjectDistributionDeptLaunchWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(selected)) {
                financeProjectDistributionDeptWeightLibs = new ArrayList<>();
            } else {
                financeProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<FinanceProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                        .eq(FinanceProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                        .eq(FinanceProjectDistributionDeptLaunchWeightLib::getVersion, selected.getVersion()));
            }
        }
        if (CollUtil.isEmpty(financeProjectDistributionDeptWeightLibs)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(financeProjectDistributionDeptWeightLibs.stream()
                .map(FinanceProjectDistributionDeptLaunchWeightLib::getWeightTarget).collect(Collectors.toList()));

        List<FinanceProjectDistributionDeptLaunchWeightInfo> collect = financeProjectDistributionDeptWeightLibs.stream().map(lib -> {
            FinanceProjectDistributionDeptLaunchWeightInfo info = new FinanceProjectDistributionDeptLaunchWeightInfo();
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
