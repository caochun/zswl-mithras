package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigDetailREQ;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigDetailRSP;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigListREQ;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigModifyREQ;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.kpi.config.EclConfigEnum;
import cn.zswltech.mithras.service.mapper.kpi.EclBusinessConfigMapper;
import cn.zswltech.mithras.service.mapper.model.kpi.EclBusinessConfig;
import cn.zswltech.mithras.service.mapper.model.kpi.EclBusinessConfigLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.EclBreachMappingBO;
import cn.zswltech.mithras.service.service.bo.EclLossLgdBO;
import cn.zswltech.mithras.service.service.bo.EclRatingMappingBO;
import cn.zswltech.mithras.service.service.bo.EclScenarioWeightBO;
import cn.zswltech.mithras.common.util.StringUtil;
import cn.zswltech.mithras.service.util.VersionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.kpi.config.EclConfigEnum.RATING_MAPPING;

/**
* @description ecl_业务配置表
* @author vico
* @date 2025-09-24
*/
@Service
public class EclBusinessConfigService extends ServiceImpl<EclBusinessConfigMapper, EclBusinessConfig> {

    @Resource
    private EclBusinessConfigMapper eclBusinessConfigMapper;
    @Resource
    private EclBusinessConfigLibService eclBusinessConfigLibService;

    @Transactional(rollbackFor = Throwable.class)
    public void modify(EclBusinessConfigModifyREQ req) {
        EclBusinessConfig originalInfo = eclBusinessConfigMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkModify(req);
        EclBusinessConfigLib lastLib = eclBusinessConfigLibService.getOne(Wrappers.<EclBusinessConfigLib>lambdaQuery().gt(EclBusinessConfigLib::getCreateTime, LocalDate.now())
                .orderByDesc(EclBusinessConfigLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));

        //转存版本表
        EclBusinessConfigLib eclBusinessConfigLib = BeanUtil.copyProperties(originalInfo, EclBusinessConfigLib.class, "id", "createTime", "createBy", "updateTime", "updateBy");
        eclBusinessConfigLib.setOriginId(originalInfo.getId());
        eclBusinessConfigLib.setDataCreateBy(originalInfo.getCreateBy());
        eclBusinessConfigLib.setDataCreateTime(originalInfo.getCreateTime());
        eclBusinessConfigLib.setDataUpdateBy(originalInfo.getUpdateBy());
        eclBusinessConfigLib.setDataUpdateTime(originalInfo.getUpdateTime());
        eclBusinessConfigLib.setVersion(VersionUtil.generateVersion(lastLib == null ? null : lastLib.getVersion()));
        eclBusinessConfigLibService.save(eclBusinessConfigLib);
        EclBusinessConfig info = BeanUtil.copyProperties(req, EclBusinessConfig.class);
        info.setConfigVersion(IdUtil.getSnowflakeNextIdStr());
        eclBusinessConfigMapper.updateById(info);
    }

    private void checkModify(EclBusinessConfigModifyREQ req){
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

    public Page<EclBusinessConfig> list(EclBusinessConfigListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclBusinessConfig>lambdaQuery()
        .orderByAsc(EclBusinessConfig::getOrderFlag));
    }

    public EclBusinessConfigDetailRSP detail(EclBusinessConfigDetailREQ req) {
        return BeanUtil.copyProperties(this.getOne(Wrappers.<EclBusinessConfig>lambdaQuery().eq(ObjectUtil.isNotEmpty(req.getId()), EclBusinessConfig::getId, req.getId())
                .eq(ObjectUtil.isNotEmpty(req.getConfigCode()), EclBusinessConfig::getConfigCode, req.getConfigCode())), EclBusinessConfigDetailRSP.class);
    }

    public EclBusinessConfig getByCode(String configCode) {
        return this.getOne(Wrappers.<EclBusinessConfig>lambdaQuery()
        .eq(EclBusinessConfig::getConfigCode, configCode));
    }

    public Map<String, String> innerLeave2PD() {
        //获取国内转md
        EclRatingMappingBO eclRatingMappingBO = JSONUtil.toBean(getByCode(RATING_MAPPING.name()).getConfigValue(), EclRatingMappingBO.class);
        EclBreachMappingBO eclBreachMappingBO = JSONUtil.toBean(getByCode(EclConfigEnum.BREACH_MAPPING.name()).getConfigValue(), EclBreachMappingBO.class);
        Map<String, String> eclRatingMappingMap = eclRatingMappingBO.getData().stream().collect(Collectors.toMap(EclRatingMappingBO.RatingMappingData::getInnerLevel, EclRatingMappingBO.RatingMappingData::getOuterLevel, (a, b) -> a));
        Map<String, String> eclBreachMappingMap = eclBreachMappingBO.getData().stream().collect(Collectors.toMap(EclBreachMappingBO.BreachMappingData::getOuterLevel, EclBreachMappingBO.BreachMappingData::getOuterPd, (a, b) -> a));
        Map<String, String> leave2PDMap = new HashMap<>();
        eclRatingMappingMap.keySet().forEach(e -> {
            leave2PDMap.put(e, eclBreachMappingMap.get(eclRatingMappingMap.get(e)));
        });
        return leave2PDMap;
    }

    public Map<String, String> innerLeave2OutLeave() {
        //获取国内转md
        EclRatingMappingBO eclRatingMappingBO = JSONUtil.toBean(getByCode(RATING_MAPPING.name()).getConfigValue(), EclRatingMappingBO.class);
        return eclRatingMappingBO.getData().stream().collect(Collectors.toMap(EclRatingMappingBO.RatingMappingData::getInnerLevel, EclRatingMappingBO.RatingMappingData::getOuterLevel, (a, b) -> a));
    }


}