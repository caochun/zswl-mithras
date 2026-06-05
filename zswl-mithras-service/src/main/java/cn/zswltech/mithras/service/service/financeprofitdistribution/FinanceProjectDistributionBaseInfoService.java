package cn.zswltech.mithras.service.service.financeprofitdistribution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.contract.convert.contract.ContractBaseInfoConverter;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionBaseInfo;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lllin
 * @date2025/12/19
 * @description
 */
@Slf4j
@Service
public class FinanceProjectDistributionBaseInfoService extends ServiceImpl<FinanceProjectDistributionBaseInfoMapper, FinanceProjectDistributionBaseInfo> {

    @Resource
    FinanceProjectDistributionService financeProjectDistributionService;

    @Resource
    private Id2NameService id2NameService;
    @Resource
    ContractBaseInfoMapper contractBaseInfoMapper;

    public FinanceProjectDistributionBaseInfo getOneByProjectDistributionId(Long projectDistributionId) {
        LambdaQueryWrapper<FinanceProjectDistributionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public FinanceProjectDistributionBaseInfoRSP detail(FinanceProjectDistributionBaseREQ req) {
        FinanceProjectDistribution projectDistribution = financeProjectDistributionService.getById(req.getProjectDistributionId());
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目利润分配信息不存在");
        }
        FinanceProjectDistributionBaseInfo baseInfo = this.getOneByProjectDistributionId(req.getProjectDistributionId());
        FinanceProjectDistributionBaseInfoRSP baseInfoRSP = BeanCopyUtils.generatorObject(baseInfo, FinanceProjectDistributionBaseInfoRSP.class);
        entity2RSP(baseInfoRSP,projectDistribution);
        return baseInfoRSP;
    }

    public void entity2RSP(FinanceProjectDistributionBaseInfoRSP rsp,FinanceProjectDistribution projectDistribution) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(projectDistribution.getContractId());
        //fill name
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(contractBaseInfo.getProjSponsorUserId());
        List<Long> projCosponsorUserIds = Optional.ofNullable(contractBaseInfo.getProjCosponsorUserIds()).map(e -> JSONUtil.toList(e, Long.class)).orElse(null);
        if (CollUtil.isNotEmpty(projCosponsorUserIds)) {
            sysUserIds.addAll(projCosponsorUserIds);
        }
        sysUserIds.add(contractBaseInfo.getBizDeptLeaderId());
        sysUserIds.add(contractBaseInfo.getBizDivisionLeaderId());
        deptIds.add(contractBaseInfo.getBizDeptId());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        rsp.setBizDeptName(deptMap.get(contractBaseInfo.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(contractBaseInfo.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(projCosponsorUserIds)) {
            rsp.setProjCosponsorUserNames(projCosponsorUserIds.stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(contractBaseInfo.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(contractBaseInfo.getBizDivisionLeaderId()));
    }

}
