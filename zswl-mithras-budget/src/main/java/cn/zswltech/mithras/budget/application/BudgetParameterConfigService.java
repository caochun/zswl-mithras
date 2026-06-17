package cn.zswltech.mithras.budget.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListREQ;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.budget.enums.BudgetConfigTypeEnum;
import cn.zswltech.mithras.budget.enums.BudgetFtpIndustryCategory;
import cn.zswltech.mithras.budget.mapper.BudgetParameterConfigMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetParameterConfig;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.foundation.util.StringUtil;
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
public class BudgetParameterConfigService extends ServiceImpl<BudgetParameterConfigMapper, BudgetParameterConfig> implements BudgetParameterConfigApplicationService {

    @Resource
    private BudgetParameterConfigMapper budgetParameterConfigMapper;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private DeptNameResolver deptNameResolver;

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
            Map<Long, String> userId2Name = userNameResolver.sysUserId2Name(rsps.stream().map(BudgetParameterConfigListRSP::getUpdateBy).collect(Collectors.toList()));
            //
            List<Long> deptIds = new ArrayList<>();
            rsps.forEach(e -> {
                if (ObjectUtil.isNotEmpty(e.getExpenseRatioConfigValue())) {
                    deptIds.addAll(e.getExpenseRatioConfigValue().stream().map(BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO::getDeptId).collect(Collectors.toList()));
                }
            });
            Map<Long, String> deptId2Name = deptNameResolver.deptId2Name(deptIds);
            rsps.forEach(e -> {
                e.setUpdateByName(userId2Name.get(e.getUpdateBy()));
                if (ObjectUtil.isNotEmpty(e.getExpenseRatioConfigValue())) {
                    e.getExpenseRatioConfigValue().forEach(x -> x.setDeptName(deptId2Name.get(x.getDeptId())));
                }
            });
        }
        return rsps;
    }

    public Integer getRiskReserve(String ftpIndustryCategory, String relatedTermRangeCode) {
        if (StrUtil.isBlank(ftpIndustryCategory) || StrUtil.isBlank(relatedTermRangeCode)) {
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
        Optional<BudgetParameterConfigListRSP.BudgetParameterRiskBO> config = list.stream().filter(e -> StrUtil.equals(ftpIndustryCategory, e.getFtpIndustryCategory()) && StrUtil.equals(relatedTermRangeCode, e.getTermRange())).findFirst();
        if (config.isPresent()) {
            return config.get().getRiskReserve();
        } else {
            return 0;
        }
    }

    public Integer getRiskReserve(BudgetFtpIndustryCategory ftpIndustryCategory, String relatedTermRangeCode) {
        return this.getRiskReserve(Objects.isNull(ftpIndustryCategory) ? null : ftpIndustryCategory.name(), relatedTermRangeCode);
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

    public Integer getFtpByFtpIndustryCategory(String ftpIndustryCategory, Integer termMonth) {
        if (StrUtil.isBlank(ftpIndustryCategory) || Objects.isNull(termMonth)) {
            return null;
        }
        BudgetParameterConfig budgetParameterConfig = this.getOneByConfigType(BudgetConfigTypeEnum.FTP_PRICE);
        if (StrUtil.isBlank(budgetParameterConfig.getConfigValue())) {
            return null;
        }
        List<BudgetParameterConfigListRSP.BudgetParameterFtpBO> list = JSONUtil.toList(budgetParameterConfig.getConfigValue(), BudgetParameterConfigListRSP.BudgetParameterFtpBO.class);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        BudgetParameterConfigListRSP.BudgetParameterFtpBO bo = list.stream().filter(e -> Objects.equals(e.getFtpIndustryClassification(), ftpIndustryCategory)).findFirst().orElse(null);
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

    public Integer getFtpByFtpIndustryCategory(BudgetFtpIndustryCategory ftpIndustryCategory, Integer termMonth) {
        return this.getFtpByFtpIndustryCategory(Objects.isNull(ftpIndustryCategory) ? null : ftpIndustryCategory.name(), termMonth);
    }

    private BudgetParameterConfig getOneByConfigType(BudgetConfigTypeEnum budgetConfigTypeEnum) {
        LambdaQueryWrapper<BudgetParameterConfig> query = Wrappers.lambdaQuery();
        query.eq(BudgetParameterConfig::getConfigKey, budgetConfigTypeEnum);
        query.orderByDesc(BudgetParameterConfig::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
