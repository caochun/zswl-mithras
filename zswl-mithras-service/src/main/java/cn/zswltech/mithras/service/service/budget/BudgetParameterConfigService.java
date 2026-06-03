package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListREQ;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.budget.domain.enums.BudgetConfigTypeEnum;
import cn.zswltech.mithras.ftp.newftp.enums.RelatedTermRange;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetParameterConfigMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetParameterConfig;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-参数设置
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetParameterConfigService extends ServiceImpl<BudgetParameterConfigMapper, BudgetParameterConfig> {

    @Resource
    private BudgetParameterConfigMapper budgetParameterConfigMapper;
    @Resource
    private Id2NameService id2NameService;

    @Transactional(rollbackFor = Throwable.class)
    public void modify(BudgetParameterConfigModifyREQ req) {
        BudgetParameterConfig originalInfo = budgetParameterConfigMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BudgetParameterConfig info = BeanUtil.copyProperties(req, BudgetParameterConfig.class);
        budgetParameterConfigMapper.updateById(info);
    }

    public List<BudgetParameterConfigListRSP> list(BudgetParameterConfigListREQ req) {
        List<BudgetParameterConfig> parameterConfigs = this.list();
        List<BudgetParameterConfigListRSP> rsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(parameterConfigs)) {
            rsps = BeanUtil.copyToList(parameterConfigs, BudgetParameterConfigListRSP.class);
            rsps.forEach(BudgetParameterConfigListRSP::setConfigValue);
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(rsps.stream().map(BudgetParameterConfigListRSP::getUpdateBy).collect(Collectors.toList()));
            //
            List<Long> deptIds = new ArrayList<>();
            rsps.forEach(e -> {
                if (ObjectUtil.isNotEmpty(e.getExpenseRatioConfigValue())) {
                    deptIds.addAll(e.getExpenseRatioConfigValue().stream().map(BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO::getDeptId).collect(Collectors.toList()));
                }
            });
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);
            rsps.forEach(e -> {
                e.setUpdateByName(userId2Name.get(e.getUpdateBy()));
                if (ObjectUtil.isNotEmpty(e.getExpenseRatioConfigValue())) {
                    e.getExpenseRatioConfigValue().forEach(x -> x.setDeptName(deptId2Name.get(x.getDeptId())));
                }
            });
        }
        return rsps;
    }

    public Integer getRiskReserve(FtpIndustryCategoryEnum ftpIndustryCategoryEnum, RelatedTermRange relatedTermRange) {
        if (Objects.isNull(ftpIndustryCategoryEnum) || Objects.isNull(relatedTermRange)) {
            return 0;
        }
        BudgetParameterConfig budgetParameterConfig = this.getOneByConfigType(BudgetConfigTypeEnum.RISK_RATIO);
        if (StrUtil.isBlank(budgetParameterConfig.getConfigValue())) {
            return 0;
        }
        List<BudgetParameterConfigListRSP.BudgetParameterRiskBO> list = JSONUtil.toList(budgetParameterConfig.getConfigValue(), BudgetParameterConfigListRSP.BudgetParameterRiskBO.class);
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        Optional<BudgetParameterConfigListRSP.BudgetParameterRiskBO> config = list.stream().filter(e -> StrUtil.equals(ftpIndustryCategoryEnum.name(), e.getFtpIndustryCategory()) && StrUtil.equals(relatedTermRange.name(), e.getTermRange())).findFirst();
        if (config.isPresent()) {
            return config.get().getRiskReserve();
        } else {
            return 0;
        }
    }

    public Integer getExpenseRatioByDeptId(Long bizDeptId) {
        BudgetParameterConfig budgetParameterConfig = this.getOneByConfigType(BudgetConfigTypeEnum.EXPENSE_RATIO);
        if (StrUtil.isBlank(budgetParameterConfig.getConfigValue())) {
            return null;
        }
        List<BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO> list = JSONUtil.toList(budgetParameterConfig.getConfigValue(), BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO.class);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(e -> Objects.equals(e.getDeptId(), bizDeptId)).findFirst().map(BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO::getExpenseRatio).orElse(null);
    }

    public Integer getFtpByFtpIndustryCategory(FtpIndustryCategoryEnum ftpIndustryCategoryEnum, Integer termMonth) {
        BudgetParameterConfig budgetParameterConfig = this.getOneByConfigType(BudgetConfigTypeEnum.FTP_PRICE);
        if (StrUtil.isBlank(budgetParameterConfig.getConfigValue())) {
            return null;
        }
        List<BudgetParameterConfigListRSP.BudgetParameterFtpBO> list = JSONUtil.toList(budgetParameterConfig.getConfigValue(), BudgetParameterConfigListRSP.BudgetParameterFtpBO.class);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        BudgetParameterConfigListRSP.BudgetParameterFtpBO bo = list.stream().filter(e -> Objects.equals(e.getFtpIndustryClassification(), ftpIndustryCategoryEnum.name())).findFirst().orElse(null);
        if (Objects.isNull(bo)) {
            return null;
        }
        if (termMonth > 36) {
            return bo.getMoreThanThreeYears();
        } else if (termMonth > 12) {
            return bo.getOneToThreeYearTerm();
        } else {
            return bo.getOneYearTerm();
        }
    }

    private BudgetParameterConfig getOneByConfigType(BudgetConfigTypeEnum budgetConfigTypeEnum) {
        LambdaQueryWrapper<BudgetParameterConfig> query = Wrappers.lambdaQuery();
        query.eq(BudgetParameterConfig::getConfigKey, budgetConfigTypeEnum);
        query.orderByDesc(BudgetParameterConfig::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}