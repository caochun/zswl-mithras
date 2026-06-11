package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.EclPredictBusinessConfigApplicationService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.kpi.enums.config.EclConfigEnum;
import cn.zswltech.mithras.budget.mapper.EclPredictBusinessConfigMapper;
import cn.zswltech.mithras.budget.mapper.model.EclPredictBusinessConfig;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.kpi.bo.EclBreachMappingBO;
import cn.zswltech.mithras.kpi.bo.EclLossLgdBO;
import cn.zswltech.mithras.kpi.bo.EclRatingMappingBO;
import cn.zswltech.mithras.kpi.bo.EclScenarioWeightBO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description ecl_预测业务配置表
* @author vico
* @date 2025-10-14
*/
@Service
public class EclPredictBusinessConfigService extends ServiceImpl<EclPredictBusinessConfigMapper, EclPredictBusinessConfig> implements EclPredictBusinessConfigApplicationService {

    @Resource
    private EclPredictBusinessConfigMapper eclPredictBusinessConfigMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(EclPredictBusinessConfigAddREQ req) {
        EclPredictBusinessConfig info = BeanUtil.copyProperties(req, EclPredictBusinessConfig.class);
        eclPredictBusinessConfigMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(EclPredictBusinessConfigModifyREQ req) {
        checkModify(req);
        EclPredictBusinessConfig originalInfo = eclPredictBusinessConfigMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        EclPredictBusinessConfig info = BeanUtil.copyProperties(req, EclPredictBusinessConfig.class);
        eclPredictBusinessConfigMapper.updateById(info);
    }

    private void checkModify(EclPredictBusinessConfigModifyREQ req){
        EclConfigEnum eclConfigEnum = EclConfigEnum.ofName(req.getConfigCode());
        if (ObjectUtil.isEmpty(eclConfigEnum)) {
            throw new MithrasException("暂不支持此类型");
        }
        Set<String> set = new HashSet<>();
        switch (eclConfigEnum) {
            case RATING_MAPPING:
                //
                EclRatingMappingBO eclRatingMappingBO = JSONUtil.toBean(req.getConfigValue(), EclRatingMappingBO.class);
                List<String> innerCollection = eclRatingMappingBO.getData().stream().filter(e -> ObjectUtil.isNotEmpty(e.getInnerLevel())).map(EclRatingMappingBO.RatingMappingData::getInnerLevel).collect(Collectors.toList());
                innerCollection.forEach(e -> {
                    if(set.contains(e)) {
                        throw new MithrasException("内部评级" + e + "重复");
                    }
                    set.add(e);
                });
                break;
            case BREACH_MAPPING:
                EclBreachMappingBO eclBreachMappingBO = JSONUtil.toBean(req.getConfigValue(), EclBreachMappingBO.class);
                List<String> collect = eclBreachMappingBO.getData().stream().filter(e -> ObjectUtil.isNotEmpty(e.getOuterLevel())).map(EclBreachMappingBO.BreachMappingData::getOuterLevel).collect(Collectors.toList());
                collect.forEach(e -> {
                    if(set.contains(e)) {
                        throw new MithrasException("穆迪评级" + e + "重复");
                    }
                    set.add(e);
                });
                break;
            case FORWARD_Z:
                break;
            case LOSS_LGD:
                EclLossLgdBO eclLossLgdBO = JSONUtil.toBean(req.getConfigValue(), EclLossLgdBO.class);
                List<String> lossList = eclLossLgdBO.getData().stream().filter(e -> ObjectUtil.isNotEmpty(e.getLgd())).map(EclLossLgdBO.EclLossLgdData::getLgd).collect(Collectors.toList());
                lossList.forEach(e -> {
                    if(new BigDecimal(e).compareTo(new BigDecimal("1")) > 0) {
                        throw new MithrasException("违约损失率LGD不能大于1");
                    }
                });
                break;
            case SCENARIO_WEIGHT:
                EclScenarioWeightBO eclScenarioWeightBO = JSONUtil.toBean(req.getConfigValue(), EclScenarioWeightBO.class);
                BigDecimal sum = eclScenarioWeightBO.getData().stream().filter(e -> ObjectUtil.isNotEmpty(e.getSceneWeight())).map(EclScenarioWeightBO.EclScenarioWeightData::getSceneWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sum.compareTo(new BigDecimal("1")) != 0) {
                    throw new MithrasException("权重总和不为1");
                }
                break;
        }
    }

    public Page<EclPredictBusinessConfig> list(EclPredictBusinessConfigListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclPredictBusinessConfig>lambdaQuery()
                .eq(EclPredictBusinessConfig::getExecutePredictId, req.getExecutePredictId())
                .orderByAsc(EclPredictBusinessConfig::getOrderFlag));
    }

    public PageR<EclPredictBusinessConfigListRSP> pageList(EclPredictBusinessConfigListREQ req) {
        Page<EclPredictBusinessConfig> data = this.list(req);
        List<EclPredictBusinessConfigListRSP> list = BeanUtil.copyToList(data.getRecords(), EclPredictBusinessConfigListRSP.class);
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    public EclPredictBusinessConfig detail(EclPredictBusinessConfigDetailREQ req) {
        return this.getOne(Wrappers.<EclPredictBusinessConfig>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getId()), EclPredictBusinessConfig::getId, req.getId())
                .eq(ObjectUtil.isNotEmpty(req.getExecutePredictId()), EclPredictBusinessConfig::getExecutePredictId, req.getExecutePredictId())
                .eq(ObjectUtil.isNotEmpty(req.getConfigCode()), EclPredictBusinessConfig::getConfigCode, req.getConfigCode()));
    }

    public EclPredictBusinessConfigDetailRSP detailRsp(EclPredictBusinessConfigDetailREQ req) {
        return BeanUtil.copyProperties(this.detail(req), EclPredictBusinessConfigDetailRSP.class);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void remove(EclPredictBusinessConfigRemoveREQ req) {
        EclPredictBusinessConfig originalInfo = eclPredictBusinessConfigMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<EclPredictBusinessConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(EclPredictBusinessConfig::getId, req.getId());
        updateWrapper.set(EclPredictBusinessConfig::getDeleted, YesOrNoNumberEnum.YES.getCode());
        eclPredictBusinessConfigMapper.update(null, updateWrapper);
    }

}
