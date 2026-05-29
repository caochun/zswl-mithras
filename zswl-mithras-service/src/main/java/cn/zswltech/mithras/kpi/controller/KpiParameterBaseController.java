package cn.zswltech.mithras.kpi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiParameterBaseApi;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterBase;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.service.KpiParameterBaseService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.kpi.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 绩效考核-参数设置基本表
* @author vico
* @date 2024-09-21
*/
@RestController
@Slf4j
public class KpiParameterBaseController implements KpiParameterBaseApi {

    @Resource
    private KpiParameterBaseService kpiParameterBaseService;

    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<Long> add(KpiParameterBaseAddREQ req) {
        return R.ok(kpiParameterBaseService.add(req));
    }

    @Override
    public R<Void> effect(KpiParameterBaseCommonREQ req){
        kpiParameterBaseService.effect(req);
        return R.ok();
    }

    @Override
    public R<Void> close(@Valid KpiParameterBaseCommonREQ req) {
        kpiParameterBaseService.close(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiParameterBaseListRSP>> list(KpiParameterBaseListREQ req){
        Page<KpiParameterBase> data = kpiParameterBaseService.list(req);
        List<KpiParameterBaseListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiParameterBaseListRSP.class);
        if(ObjectUtil.isNotEmpty(list)) {
            Map<Long, String> longStringMap = id2NameService.sysUserId2Name(list.stream().map(KpiParameterBaseListRSP::getCreateBy).collect(Collectors.toList()));
            list.forEach( e -> {
                e.setCreateByName(longStringMap.get(e.getCreateBy()));
            });
        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiParameterBaseCommonREQ req){
        kpiParameterBaseService.remove(req);
        return R.ok();
    }

    @Override
    public R<Long> copy(@Valid KpiParameterBaseCopyREQ req) {
        //检查
        if (kpiParameterBaseService.count(Wrappers.<KpiParameterBase>lambdaQuery()
                .eq(KpiParameterBase::getEffectMonth, req.getEffectMonth())
                .eq(KpiParameterBase::getParameterStatus, RecordStatus.TAKE_EFFECT.name())) > 0) {
            throw new MithrasException("已存在该生效月份数据");
        }
        KpiParameterBaseAddREQ baseAddREQ = new KpiParameterBaseAddREQ();
        baseAddREQ.setEffectMonth(req.getEffectMonth());
        baseAddREQ.setParameterStatus(RecordStatus.TAKE_EFFECT.name());
        Long addId = kpiParameterBaseService.add(baseAddREQ);
        List<KpiParameterConfig> oldList = kpiParameterConfigService.list(Wrappers.<KpiParameterConfig>lambdaQuery()
                .eq(KpiParameterConfig::getParameterBaseId, req.getId()));
        if (ObjectUtil.isEmpty(oldList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        oldList.forEach(old -> {
            old.setId(null);
            old.setParameterBaseId(addId);
            old.setCreateTime(null);
            old.setCreateBy(null);
            old.setUpdateTime(null);
            old.setUpdateBy(null);
        });
        Map<String, KpiParameterConfig> oldParameterMap = oldList.stream().collect(Collectors.toMap(KpiParameterConfig::getConfigCode, e -> e, (a, b) -> a));
        List<KpiParameterConfigCodeEnum> kpiEnums = KpiParameterConfigCodeEnum.getKpiEnum();
        List<KpiParameterConfig> parameterConfigs = new ArrayList<>(kpiEnums.size());
        kpiEnums.forEach(kpiEnum -> {
            KpiParameterConfig config = oldParameterMap.get(kpiEnum.name());
            if (ObjectUtil.isEmpty(config)) {
                config = new KpiParameterConfig();
                config.setConfigCode(kpiEnum.name());
                config.setConfigDesc(kpiEnum.getDesc());
                config.setParameterBaseId(addId);
                try {
                    config.setConfigValue(JSONUtil.toJsonStr(kpiEnum.getMainMapperClass().newInstance()));
                } catch (Exception e) {
                    log.warn("参数类型创建失败");
                    config.setConfigValue("[]");
                }
            }
            parameterConfigs.add(config);
        });
        kpiParameterConfigService.saveBatch(parameterConfigs);
        return R.ok(addId);
    }

}