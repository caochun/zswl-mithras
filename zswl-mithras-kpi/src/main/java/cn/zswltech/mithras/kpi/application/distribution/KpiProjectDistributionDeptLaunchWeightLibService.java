package cn.zswltech.mithras.kpi.application.distribution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionDeptLaunchWeightLibMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptLaunchWeightLib;
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
* @description 绩效考核-部门-项目投放分配比重版本表
* @author hspcadmin
* @date 2025-09-29
*/
@Slf4j
@Service
public class KpiProjectDistributionDeptLaunchWeightLibService extends ServiceImpl<KpiProjectDistributionDeptLaunchWeightLibMapper, KpiProjectDistributionDeptLaunchWeightLib> {


    @Resource
    private Id2NameService id2NameService;
    @Resource
    private KpiProjectDistributionMapper kpiProjectDistributionMapper;

    public List<KpiProjectDistributionDeptLaunchWeightInfo> buildDeptLaunchWeightInfoByDistributionId(Long distributionId, String version) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionMapper.selectById(distributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }

        // 如果不传版本，则默认取最新版本
        List<KpiProjectDistributionDeptLaunchWeightLib> kpiProjectDistributionDeptWeightLibs;
        if (CharSequenceUtil.isNotBlank(version)) {
            kpiProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<KpiProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getVersion, version));
        } else {
            KpiProjectDistributionDeptLaunchWeightLib selected = baseMapper.selectOne(Wrappers.<KpiProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(KpiProjectDistributionDeptLaunchWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(selected)) {
                kpiProjectDistributionDeptWeightLibs = new ArrayList<>();
            } else {
                kpiProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<KpiProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                        .eq(KpiProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionId)
                        .eq(KpiProjectDistributionDeptLaunchWeightLib::getVersion, selected.getVersion()));
            }
        }
        if (CollUtil.isEmpty(kpiProjectDistributionDeptWeightLibs)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(kpiProjectDistributionDeptWeightLibs.stream()
                .map(KpiProjectDistributionDeptLaunchWeightLib::getWeightTarget).collect(Collectors.toList()));

//        // 拿到项目主办的业务部门
//        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(distributionServiceById.getContractId());
//        if (Objects.isNull(contractBaseInfo)) {
//            throw new MithrasException("合同信息不存在");
//        }
//        OrgDO deptByUserId = sysUserService.getBizDeptByUserId(contractBaseInfo.getProjSponsorUserId());
//        if (Objects.isNull(deptByUserId)) {
//            throw new MithrasException("合同主办对应的业务部门为空");
//        }
        List<KpiProjectDistributionDeptLaunchWeightInfo> collect = kpiProjectDistributionDeptWeightLibs.stream().map(lib -> {
            KpiProjectDistributionDeptLaunchWeightInfo info = new KpiProjectDistributionDeptLaunchWeightInfo();
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
