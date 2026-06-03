package cn.zswltech.mithras.service.service.financeprofitdistribution;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionDeptLaunchWeightMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.util.UpdateUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 财务-部门-项目投放分配比重表
 * @author lllin
 * @date 2025-12-19
*/
@Service
public class FinanceProjectDistributionDeptLaunchWeightService extends ServiceImpl<FinanceProjectDistributionDeptLaunchWeightMapper, FinanceProjectDistributionDeptLaunchWeight> {

    @Resource
    private FinanceProjectDistributionDeptLaunchWeightMapper financeProjectDistributionDeptLaunchWeightMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;
    @Resource
    private FinanceProjectDistributionDeptLaunchWeightService thisService;


    // 部门投放分配比相关数据校验
    public Pair<FinanceProjectDistribution, List<FinanceProjectDistributionDeptLaunchWeightInfo>> deptLaunchSaveValid(FinanceProjectDistributionDeptWeightSaveREQ req) {
        // 首先需要检查列表数据，如果列表不正确的话就没必要查数据库
        for (FinanceProjectDistributionDeptLaunchWeightInfo launchWeightInfo : req.getDeptLaunchWeightInfoList()) {
            if (Objects.isNull(launchWeightInfo.getWeightValue()) && Objects.nonNull(launchWeightInfo.getWeightTarget())) {
                throw new MithrasException("部门投放分配比和投放分配比例必须同时有值或者无值");
            }
            if (Objects.nonNull(launchWeightInfo.getWeightValue()) && Objects.isNull(launchWeightInfo.getWeightTarget())) {
                throw new MithrasException("部门投放分配比和投放分配比例必须同时有值或者无值");
            }
        }
        // 重新构建数据列表，过滤掉无效数据
        List<FinanceProjectDistributionDeptLaunchWeightInfo> launchWeightInfoList = req.getDeptLaunchWeightInfoList().stream()
                .filter(item -> Objects.nonNull(item.getWeightValue())).collect(Collectors.toList());
        if (CollUtil.isEmpty(launchWeightInfoList)) {
            throw new MithrasException("投放分配比例不能为空");
        }
        boolean no = launchWeightInfoList.stream().map(FinanceProjectDistributionDeptLaunchWeightInfo::getWeightValue)
                .filter(Objects::nonNull).anyMatch(e -> e <= 0);
        if (no) {
            throw new MithrasException("投放分配比例不能小于等于0");
        }
        long counted = launchWeightInfoList.stream().collect(Collectors.groupingBy(FinanceProjectDistributionDeptLaunchWeightInfo::getWeightTarget))
                .values().stream().map(List::size).filter(e -> e != 1).count();
        if (counted > 0) {
            throw new MithrasException("部门投放分配比,分配比重部门不能重复");
        }
        // 还需要校验总的分配比重是否为100%
        int sum = launchWeightInfoList.stream().mapToInt(FinanceProjectDistributionDeptLaunchWeightInfo::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("投放分配比例加总必须等于100%");
        }
        req.setDeptLaunchWeightInfoList(launchWeightInfoList);
        FinanceProjectDistribution instance = financeProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(instance)) {
            throw new MithrasException("项目分配信息不存在");
        }
        return Pair.of(instance, launchWeightInfoList);
    }

    public List<FinanceProjectDistributionDeptLaunchWeightInfo> queryList(Long projectDistributionId) {
        FinanceProjectDistribution distributionServiceById = financeProjectDistributionService.getById(projectDistributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        List<FinanceProjectDistributionDeptLaunchWeight> list = thisService.list(Wrappers.<FinanceProjectDistributionDeptLaunchWeight>lambdaQuery()
                .eq(FinanceProjectDistributionDeptLaunchWeight::getProjectDistributionId, distributionServiceById.getId()));

        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 组装数据
        Map<Long, String> map = id2NameService.deptId2Name(list.stream().map(FinanceProjectDistributionDeptLaunchWeight::getWeightTarget).collect(Collectors.toSet()));
        List<FinanceProjectDistributionDeptLaunchWeightInfo> collect = list.stream().map(item -> {
            FinanceProjectDistributionDeptLaunchWeightInfo info = new FinanceProjectDistributionDeptLaunchWeightInfo();
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
    public void savaBatch( Pair<FinanceProjectDistribution, List<FinanceProjectDistributionDeptLaunchWeightInfo>> launchPair){
        FinanceProjectDistribution projectDistribution = launchPair.getKey();
        List<FinanceProjectDistributionDeptLaunchWeightInfo> launchWeightInfoList = launchPair.getValue();
        //数据库存在的数据
        List<FinanceProjectDistributionDeptLaunchWeight> oldDbResult = this.list(Wrappers.<FinanceProjectDistributionDeptLaunchWeight>lambdaQuery()
                .eq(FinanceProjectDistributionDeptLaunchWeight::getProjectDistributionId, projectDistribution.getId()));

        List<FinanceProjectDistributionDeptLaunchWeight> newValList = launchWeightInfoList.stream().map(item -> {
            FinanceProjectDistributionDeptLaunchWeight newVal = BeanUtil.copyProperties(item, FinanceProjectDistributionDeptLaunchWeight.class);
            newVal.setProjectDistributionId(projectDistribution.getId());
            // weightType 字段暂时用不上，后续可扩展
            newVal.setWeightTarget(item.getWeightTarget());
            newVal.setWeightValue(item.getWeightValue());
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, FinanceProjectDistributionDeptLaunchWeight::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(FinanceProjectDistributionDeptLaunchWeight::getId).collect(Collectors.toList())),
                addList -> {
                    addList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.saveBatch(addList);
                }
        );

    }



    @Transactional(rollbackFor = Throwable.class)
    public void add(FinanceProjectDistributionDeptLaunchWeightAddREQ req) {
        FinanceProjectDistributionDeptLaunchWeight info = BeanUtil.copyProperties(req, FinanceProjectDistributionDeptLaunchWeight.class);
        financeProjectDistributionDeptLaunchWeightMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FinanceProjectDistributionDeptLaunchWeightModifyREQ req) {
        FinanceProjectDistributionDeptLaunchWeight originalInfo = financeProjectDistributionDeptLaunchWeightMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FinanceProjectDistributionDeptLaunchWeight info = BeanUtil.copyProperties(req, FinanceProjectDistributionDeptLaunchWeight.class);
        financeProjectDistributionDeptLaunchWeightMapper.updateById(info);
    }

    public Page<FinanceProjectDistributionDeptLaunchWeight> list(FinanceProjectDistributionDeptLaunchWeightListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FinanceProjectDistributionDeptLaunchWeightRemoveREQ req) {
        FinanceProjectDistributionDeptLaunchWeight originalInfo = financeProjectDistributionDeptLaunchWeightMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        financeProjectDistributionDeptLaunchWeightMapper.deleteById(req.getId());
    }


}