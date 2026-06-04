package cn.zswltech.mithras.service.service.financeprofitdistribution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionDeptWeightMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionDeptWeight;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lllin
 * @date 2025-12-19
 * @description
 */
@Slf4j
@Service
public class FinanceProjectDistributionDeptWeightService extends ServiceImpl<FinanceProjectDistributionDeptWeightMapper, FinanceProjectDistributionDeptWeight> {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;
    @Resource
    private FinanceProjectDistributionDeptWeightService thisService;
    @Resource
    private FinanceProjectDistributionDeptLaunchWeightService financeProjectDistributionDeptLaunchWeightService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FinanceProjectDistributionDeptWeightLibService financeProjectDistributionDeptWeightLibService;
    @Resource
    private FinanceProjectDistributionDeptLaunchWeightLibService financeProjectDistributionDeptLaunchWeightLibService;

    public List<FinanceProjectDistributionDeptWeight> listByProjectDistributionIds(Collection<Long> projectDistributionIds) {
        LambdaQueryWrapper<FinanceProjectDistributionDeptWeight> query = Wrappers.lambdaQuery();
        query.in(FinanceProjectDistributionDeptWeight::getProjectDistributionId, projectDistributionIds);
        return this.list(query);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void saveDept(FinanceProjectDistributionDeptWeightSaveREQ req) {
        // 部门分润比-数据校验
        Pair<FinanceProjectDistribution, List<FinanceProjectDistributionDeptWeightInfo>> pair = thisService.deptSaveValid(req);
        // 部门投放分配比-数据校验
        Pair<FinanceProjectDistribution, List<FinanceProjectDistributionDeptLaunchWeightInfo>> launchPair = financeProjectDistributionDeptLaunchWeightService.deptLaunchSaveValid(req);
        FinanceProjectDistribution projectDistribution = pair.getKey();
        List<FinanceProjectDistributionDeptWeightInfo> weightInfoList = pair.getValue();

        // 保存或者更新数据, 还要考虑多余的数据需要删除
        // 需要将现在的草稿表的数据全部拿出来
        List<Long> list = weightInfoList.stream().map(FinanceProjectDistributionDeptWeightInfo::getId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
        List<FinanceProjectDistributionDeptWeight> existList = thisService.list(Wrappers.<FinanceProjectDistributionDeptWeight>lambdaQuery()
                .eq(FinanceProjectDistributionDeptWeight::getProjectDistributionId, projectDistribution.getId()));
        List<Long> needDeleteList = existList.stream().map(FinanceProjectDistributionDeptWeight::getId)
                .filter(Objects::nonNull)
                .filter(id -> !list.contains(id)).collect(Collectors.toList());
        Map<Long, FinanceProjectDistributionDeptWeight> existMap = new HashMap<>();
        List<FinanceProjectDistributionDeptWeight> insertList = new ArrayList<>();
        List<FinanceProjectDistributionDeptWeight> updatetList = new ArrayList<>();
        List<Long> ids = weightInfoList.stream().map(FinanceProjectDistributionDeptWeightInfo::getId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)) {
            existMap = listByIds(ids).stream().collect(Collectors.toMap(FinanceProjectDistributionDeptWeight::getId, Function.identity(), (a, b) -> a));
        }
        for (FinanceProjectDistributionDeptWeightInfo weightInfo : weightInfoList) {
            if (Objects.isNull(weightInfo.getId())) {
                FinanceProjectDistributionDeptWeight financeProjectDistributionDeptWeight = new FinanceProjectDistributionDeptWeight();
                financeProjectDistributionDeptWeight.setProjectDistributionId(projectDistribution.getId());
                // weightType 字段暂时用不上，后续可扩展
                financeProjectDistributionDeptWeight.setWeightTarget(weightInfo.getWeightTarget());
                financeProjectDistributionDeptWeight.setWeightValue(weightInfo.getWeightValue());
                insertList.add(financeProjectDistributionDeptWeight);
            } else {
                FinanceProjectDistributionDeptWeight financeProjectDistributionDeptWeight = existMap.get(weightInfo.getId());
                if (Objects.nonNull(financeProjectDistributionDeptWeight)) {
                    financeProjectDistributionDeptWeight.setWeightTarget(weightInfo.getWeightTarget());
                    financeProjectDistributionDeptWeight.setWeightValue(weightInfo.getWeightValue());
                    updatetList.add(financeProjectDistributionDeptWeight);
                }
            }
        }

        // 操作更新
        if (CollUtil.isNotEmpty(insertList)) {
            thisService.saveBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updatetList)) {
            thisService.updateBatchById(updatetList);
        }
        if (CollUtil.isNotEmpty(needDeleteList)) {
            thisService.removeByIds(needDeleteList);
        }
        // 部门投放分配比-数据保存
        financeProjectDistributionDeptLaunchWeightService.savaBatch(launchPair);
    }

    public Pair<FinanceProjectDistribution, List<FinanceProjectDistributionDeptWeightInfo>> deptSaveValid(FinanceProjectDistributionDeptWeightSaveREQ req) {
        // 首先需要检查列表数据，如果列表不正确的话就没必要查数据库
        for (FinanceProjectDistributionDeptWeightInfo weightInfo : req.getDeptWeightInfoList()) {
            if (Objects.isNull(weightInfo.getWeightValue()) && Objects.nonNull(weightInfo.getWeightTarget())) {
                throw new MithrasException("分配比重部门和分配比重必须同时有值或者无值");
            }
            if (Objects.nonNull(weightInfo.getWeightValue()) && Objects.isNull(weightInfo.getWeightTarget())) {
                throw new MithrasException("分配比重部门和分配比重必须同时有值或者无值");
            }
        }
        // 重新构建数据列表，过滤掉无效数据
        List<FinanceProjectDistributionDeptWeightInfo> weightInfoList = req.getDeptWeightInfoList().stream()
                .filter(item -> Objects.nonNull(item.getWeightValue())).collect(Collectors.toList());
        if (CollUtil.isEmpty(weightInfoList)) {
            throw new MithrasException("分配比重有效信息不能为空");
        }
        boolean no = weightInfoList.stream().map(FinanceProjectDistributionDeptWeightInfo::getWeightValue)
                .filter(Objects::nonNull).anyMatch(e -> e <= 0);
        if (no) {
            throw new MithrasException("分配比重占比不能小于等于0");
        }
        long counted = weightInfoList.stream().collect(Collectors.groupingBy(FinanceProjectDistributionDeptWeightInfo::getWeightTarget))
                .values().stream().map(List::size).filter(e -> e != 1).count();
        if (counted > 0) {
            throw new MithrasException("分配比重部门不能重复");
        }
        // 还需要校验总的分配比重是否为100%
        int sum = weightInfoList.stream().mapToInt(FinanceProjectDistributionDeptWeightInfo::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("分配比重加总必须等于100%");
        }
        req.setDeptWeightInfoList(weightInfoList);
        FinanceProjectDistribution instance = financeProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(instance)) {
            throw new MithrasException("项目分配信息不存在");
        }

        return Pair.of(instance, weightInfoList);
    }


    public List<FinanceProjectDistributionDeptWeightInfo> queryList(Long projectDistributionId) {
        FinanceProjectDistribution distributionServiceById = financeProjectDistributionService.getById(projectDistributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }
        List<FinanceProjectDistributionDeptWeight> list = thisService.list(Wrappers.<FinanceProjectDistributionDeptWeight>lambdaQuery()
                .eq(FinanceProjectDistributionDeptWeight::getProjectDistributionId, distributionServiceById.getId()));

        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 组装数据
        Map<Long, String> map = id2NameService.deptId2Name(list.stream().map(FinanceProjectDistributionDeptWeight::getWeightTarget).collect(Collectors.toSet()));
        List<FinanceProjectDistributionDeptWeightInfo> collect = list.stream().map(item -> {
            FinanceProjectDistributionDeptWeightInfo info = new FinanceProjectDistributionDeptWeightInfo();
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

    public FinanceProjectDistributionWeightRSP detail(FinanceProjectDistributionWeightREQ req) {
        boolean isHistory = StrUtil.isNotBlank(req.getVersion());
        FinanceProjectDistribution projectDistribution = financeProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        List<FinanceProjectDistributionDeptWeightInfo> deptWeightInfoList;
        List<FinanceProjectDistributionDeptLaunchWeightInfo> deptLaunchWeightInfoList;
        if (isHistory) {
            // 构建部门的比重信息
            deptWeightInfoList = financeProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(projectDistribution.getId(), req.getVersion());
            //项目投放分配比
            deptLaunchWeightInfoList = financeProjectDistributionDeptLaunchWeightLibService.buildDeptLaunchWeightInfoByDistributionId(projectDistribution.getId(), req.getVersion());
        } else {
            deptWeightInfoList = thisService.queryList(projectDistribution.getId());
            //项目投放分配比
            deptLaunchWeightInfoList = financeProjectDistributionDeptLaunchWeightService.queryList(projectDistribution.getId());
        }
        FinanceProjectDistributionWeightRSP rsp = new FinanceProjectDistributionWeightRSP();
        rsp.setProjectDistributionId(projectDistribution.getId());

        rsp.setDeptWeightInfoList(deptWeightInfoList);
        rsp.setDeptLaunchWeightInfoList(deptLaunchWeightInfoList);
        return rsp;
    }
}
