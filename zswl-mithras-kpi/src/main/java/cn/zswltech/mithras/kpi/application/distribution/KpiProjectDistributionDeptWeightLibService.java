package cn.zswltech.mithras.kpi.application.distribution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionDeptWeightLibMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/9 14:35
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionDeptWeightLibService extends ServiceImpl<KpiProjectDistributionDeptWeightLibMapper, KpiProjectDistributionDeptWeightLib> {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private KpiProjectDistributionMapper kpiProjectDistributionMapper;

    public List<KpiProjectDistributionDeptWeightInfo> buildDeptWeightInfoByDistributionId(Long distributionId, String version) {
        KpiProjectDistribution distributionServiceById = kpiProjectDistributionMapper.selectById(distributionId);
        if (Objects.isNull(distributionServiceById)) {
            throw new MithrasException("项目分配信息不存在");
        }

        // 如果不传版本，则默认取最新版本
        List<KpiProjectDistributionDeptWeightLib> kpiProjectDistributionDeptWeightLibs;
        if (CharSequenceUtil.isNotBlank(version)) {
            kpiProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                    .eq(KpiProjectDistributionDeptWeightLib::getVersion, version));
        } else {
            KpiProjectDistributionDeptWeightLib selected = baseMapper.selectOne(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                    .eq(KpiProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(selected)) {
                kpiProjectDistributionDeptWeightLibs = new ArrayList<>();
            } else {
                kpiProjectDistributionDeptWeightLibs = baseMapper.selectList(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                        .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, distributionId)
                        .eq(KpiProjectDistributionDeptWeightLib::getVersion, selected.getVersion()));
            }
        }
        if (CollUtil.isEmpty(kpiProjectDistributionDeptWeightLibs)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(kpiProjectDistributionDeptWeightLibs.stream()
                .map(KpiProjectDistributionDeptWeightLib::getWeightTarget).collect(Collectors.toList()));

//        // 拿到项目主办的业务部门
//        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(distributionServiceById.getContractId());
//        if (Objects.isNull(contractBaseInfo)) {
//            throw new MithrasException("合同信息不存在");
//        }
//        OrgDO deptByUserId = sysUserService.getBizDeptByUserId(contractBaseInfo.getProjSponsorUserId());
//        if (Objects.isNull(deptByUserId)) {
//            throw new MithrasException("合同主办对应的业务部门为空");
//        }
        List<KpiProjectDistributionDeptWeightInfo> collect = kpiProjectDistributionDeptWeightLibs.stream().map(lib -> {
            KpiProjectDistributionDeptWeightInfo info = new KpiProjectDistributionDeptWeightInfo();
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
