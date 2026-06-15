package cn.zswltech.mithras.kpi.application.distribution;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.kpi.distribution.versioning.KpiProjectDistributionDeptLaunchWeightLibService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionDeptLaunchWeightMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionDeptLaunchWeightLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.util.UpdateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 绩效考核-部门-项目投放分配比重表
* @author hspcadmin
* @date 2025-09-29
*/
@Service
public class KpiProjectDistributionDeptLaunchWeightService extends ServiceImpl<KpiProjectDistributionDeptLaunchWeightMapper, KpiProjectDistributionDeptLaunchWeight> {

    @Resource
    private KpiProjectDistributionDeptLaunchWeightMapper kpiProjectDistributionDeptLaunchWeightMapper;
    @Resource
    private DeptNameResolver deptNameResolver;
    @Resource
    private KpiProjectDistributionMapper kpiProjectDistributionMapper;
    @Resource
    private KpiProjectDistributionDeptLaunchWeightService thisService;
    private KpiProjectDistributionDeptLaunchWeightLibService kpiProjectDistributionDeptLaunchWeightLibService;

    public List<KpiProjectDistributionDeptLaunchWeight> listByProjectDistributionIds(Collection<Long> projectDistributionIds) {
        LambdaQueryWrapper<KpiProjectDistributionDeptLaunchWeight> query = Wrappers.lambdaQuery();
        query.in(KpiProjectDistributionDeptLaunchWeight::getProjectDistributionId, projectDistributionIds);
        return this.list(query);
    }



    // 部门投放分配比相关数据校验
    public Pair<KpiProjectDistribution, List<KpiProjectDistributionDeptLaunchWeightInfo>> deptLaunchSaveValid(KpiProjectDistributionDeptWeightSaveREQ req) {
        // 首先需要检查列表数据，如果列表不正确的话就没必要查数据库
        for (KpiProjectDistributionDeptLaunchWeightInfo launchWeightInfo : req.getDeptLaunchWeightInfoList()) {
            if (Objects.isNull(launchWeightInfo.getWeightValue()) && Objects.nonNull(launchWeightInfo.getWeightTarget())) {
                throw new MithrasException("部门投放分配比和投放分配比例必须同时有值或者无值");
            }
            if (Objects.nonNull(launchWeightInfo.getWeightValue()) && Objects.isNull(launchWeightInfo.getWeightTarget())) {
                throw new MithrasException("部门投放分配比和投放分配比例必须同时有值或者无值");
            }
        }
        // 重新构建数据列表，过滤掉无效数据
        List<KpiProjectDistributionDeptLaunchWeightInfo> launchWeightInfoList = req.getDeptLaunchWeightInfoList().stream()
                .filter(item -> Objects.nonNull(item.getWeightValue())).collect(Collectors.toList());
        if (CollUtil.isEmpty(launchWeightInfoList)) {
            throw new MithrasException("投放分配比例不能为空");
        }
        boolean no = launchWeightInfoList.stream().map(KpiProjectDistributionDeptLaunchWeightInfo::getWeightValue)
                .filter(Objects::nonNull).anyMatch(e -> e <= 0);
        if (no) {
            throw new MithrasException("投放分配比例不能小于等于0");
        }
        long counted = launchWeightInfoList.stream().collect(Collectors.groupingBy(KpiProjectDistributionDeptLaunchWeightInfo::getWeightTarget))
                .values().stream().map(List::size).filter(e -> e != 1).count();
        if (counted > 0) {
            throw new MithrasException("部门投放分配比,分配比重部门不能重复");
        }
        // 还需要校验总的分配比重是否为100%
        int sum = launchWeightInfoList.stream().mapToInt(KpiProjectDistributionDeptLaunchWeightInfo::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("投放分配比例加总必须等于100%");
        }
        req.setDeptLaunchWeightInfoList(launchWeightInfoList);
        KpiProjectDistribution instance = kpiProjectDistributionMapper.selectById(req.getProjectDistributionId());
        if (Objects.isNull(instance)) {
            throw new MithrasException("项目分配信息不存在");
        }
        return Pair.of(instance, launchWeightInfoList);
    }

    public List<KpiProjectDistributionDeptLaunchWeightInfo> queryList(Long projectDistributionId) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionMapper.selectById(projectDistributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        List<KpiProjectDistributionDeptLaunchWeight> list = thisService.list(Wrappers.<KpiProjectDistributionDeptLaunchWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptLaunchWeight::getProjectDistributionId, distributionServiceById.getId()));

        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 组装数据
        Map<Long, String> map = deptNameResolver.deptId2Name(list.stream().map(KpiProjectDistributionDeptLaunchWeight::getWeightTarget).collect(Collectors.toSet()));
        List<KpiProjectDistributionDeptLaunchWeightInfo> collect = list.stream().map(item -> {
            KpiProjectDistributionDeptLaunchWeightInfo info = new KpiProjectDistributionDeptLaunchWeightInfo();
            info.setId(item.getId());
            info.setIsBusinessDept(false);
            info.setWeightValue(item.getWeightValue());
            info.setWeightTarget(item.getWeightTarget());
            info.setWeightTargetName(map.get(item.getWeightTarget()));
            return info;
        }).collect(Collectors.toList());
        collect.get(0).setIsBusinessDept(true);
        return collect;
    }

    //批量保存
    @Transactional(rollbackFor = Throwable.class)
    public void savaBatch( Pair<KpiProjectDistribution, List<KpiProjectDistributionDeptLaunchWeightInfo>> launchPair){
        KpiProjectDistribution projectDistribution = launchPair.getKey();
        List<KpiProjectDistributionDeptLaunchWeightInfo> launchWeightInfoList = launchPair.getValue();
        //数据库存在的数据
        List<KpiProjectDistributionDeptLaunchWeight> oldDbResult = this.list(Wrappers.<KpiProjectDistributionDeptLaunchWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptLaunchWeight::getProjectDistributionId, projectDistribution.getId()));

        List<KpiProjectDistributionDeptLaunchWeight> newValList = launchWeightInfoList.stream().map(item -> {
            KpiProjectDistributionDeptLaunchWeight newVal = BeanUtil.copyProperties(item, KpiProjectDistributionDeptLaunchWeight.class);
            newVal.setProjectDistributionId(projectDistribution.getId());
            // weightType 字段暂时用不上，后续可扩展
            newVal.setWeightTarget(item.getWeightTarget());
            newVal.setWeightValue(item.getWeightValue());
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, KpiProjectDistributionDeptLaunchWeight::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(KpiProjectDistributionDeptLaunchWeight::getId).collect(Collectors.toList())),
                addList -> {
                    addList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.saveBatch(addList);
                }
        );

    }

    public List<KpiProjectDistributionDeptLaunchWeightInfo> prev(KpiProjectDistributionPrevREQ req) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionMapper.selectById(req.getProjectDistributionId());
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        // 找到上个版本
        KpiProjectDistributionDeptLaunchWeightLib selected;
        if (CharSequenceUtil.isNotBlank(req.getVersion())) {
            selected = kpiProjectDistributionDeptLaunchWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionServiceById.getId())
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .lt(KpiProjectDistributionDeptLaunchWeightLib::getVersion, req.getVersion())
                    .orderByDesc(KpiProjectDistributionDeptLaunchWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
        } else {
            selected = kpiProjectDistributionDeptLaunchWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptLaunchWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getProjectDistributionId, distributionServiceById.getId())
                    .eq(KpiProjectDistributionDeptLaunchWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(KpiProjectDistributionDeptLaunchWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
        }
        if (Objects.isNull(selected)) {
            return Collections.emptyList();
        }
        return kpiProjectDistributionDeptLaunchWeightLibService.buildDeptLaunchWeightInfoByDistributionId(req.getProjectDistributionId(), selected.getVersion());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(KpiProjectDistributionDeptLaunchWeightAddREQ req) {
        KpiProjectDistributionDeptLaunchWeight info = BeanUtil.copyProperties(req, KpiProjectDistributionDeptLaunchWeight.class);
        kpiProjectDistributionDeptLaunchWeightMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiProjectDistributionDeptLaunchWeightModifyREQ req) {
        KpiProjectDistributionDeptLaunchWeight originalInfo = kpiProjectDistributionDeptLaunchWeightMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiProjectDistributionDeptLaunchWeight info = BeanUtil.copyProperties(req, KpiProjectDistributionDeptLaunchWeight.class);
        kpiProjectDistributionDeptLaunchWeightMapper.updateById(info);
    }

    public Page<KpiProjectDistributionDeptLaunchWeight> list(KpiProjectDistributionDeptLaunchWeightListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiProjectDistributionDeptLaunchWeightRemoveREQ req) {
        KpiProjectDistributionDeptLaunchWeight originalInfo = kpiProjectDistributionDeptLaunchWeightMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiProjectDistributionDeptLaunchWeightMapper.deleteById(req.getId());
    }


}
