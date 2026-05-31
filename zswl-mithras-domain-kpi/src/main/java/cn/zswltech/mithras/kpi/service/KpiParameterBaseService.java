package cn.zswltech.mithras.kpi.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseCommonREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseListREQ;
import cn.zswltech.mithras.kpi.mapper.KpiParameterBaseMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterBase;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.kpi.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
* @description 绩效考核-参数设置基本表
* @author vico
* @date 2024-09-21
*/
@Service
public class KpiParameterBaseService extends ServiceImpl<KpiParameterBaseMapper, KpiParameterBase> {

    @Resource
    private KpiParameterBaseMapper kpiParameterBaseMapper;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(KpiParameterBaseAddREQ req) {
        KpiParameterBase info = BeanUtil.copyProperties(req, KpiParameterBase.class);
        info.setSource(KpiParameterConfigCodeEnum.PROJECT_RADIO.name());
        kpiParameterBaseMapper.insert(info);
       /* //保存kpi具体参数
        List<KpiParameterConfigCodeEnum> kpiEnums = KpiParameterConfigCodeEnum.getKpiEnum();
        List<KpiParameterConfig> parameterConfigs = new ArrayList<>(kpiEnums.size());
        kpiEnums.forEach(kpiEnum -> {
            KpiParameterConfig config = new KpiParameterConfig();
            config.setConfigCode(kpiEnum.name());
            config.setConfigDesc(kpiEnum.getDesc());
            config.setParameterBaseId(info.getId());
            parameterConfigs.add(config);
        });
        kpiParameterConfigService.saveBatch(parameterConfigs);*/
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect(KpiParameterBaseCommonREQ req) {
        KpiParameterBase originalInfo = kpiParameterBaseMapper.selectById(req.getId());
        this.checkKpiParameterBase(originalInfo);
        originalInfo.setParameterStatus(RecordStatus.TAKE_EFFECT.name());
        kpiParameterBaseMapper.updateById(originalInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void close(KpiParameterBaseCommonREQ req) {
        KpiParameterBase originalInfo = kpiParameterBaseMapper.selectById(req.getId());
        this.checkKpiParameterBase(originalInfo);
        originalInfo.setParameterStatus(RecordStatus.CLOSED.name());
        kpiParameterBaseMapper.updateById(originalInfo);
    }

    public Page<KpiParameterBase> list(KpiParameterBaseListREQ req) {
        return kpiParameterBaseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<KpiParameterBase>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getParameterStatus()), KpiParameterBase::getParameterStatus, req.getParameterStatus())
        .eq(ObjectUtil.isNotEmpty(req.getEffectMonth()), KpiParameterBase::getEffectMonth, req.getEffectMonth())
        .orderByDesc(KpiParameterBase::getEffectMonth));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiParameterBaseCommonREQ req) {
        if (baseMapper.selectCount(Wrappers.<KpiParameterBase>lambdaQuery()
                .eq(KpiParameterBase::getParameterStatus, RecordStatus.TAKE_EFFECT.name())) == 1) {
            throw new MithrasException("至少保留一份生效数据");
        }
        KpiParameterBase originalInfo = kpiParameterBaseMapper.selectById(req.getId());
        this.checkKpiParameterBase(originalInfo);
        kpiParameterBaseMapper.deleteById(req.getId());
        kpiParameterConfigService.remove(Wrappers.<KpiParameterConfig>lambdaQuery()
        .eq(KpiParameterConfig::getParameterBaseId, originalInfo.getId()));
    }

    private void checkKpiParameterBase(KpiParameterBase kpiParameterBase){
        if (ObjectUtil.isNull(kpiParameterBase)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equals(kpiParameterBase.getParameterStatus(), RecordStatus.CLOSED.name())){
            throw new MithrasException(ResultMsg.PROJ_CLOSED);
        }
    }

    //获取最新生效的kpi提奖比例
    public List<KpiParameterConfig> getNewKpiParameters(LocalDate date) {
        if(ObjectUtil.isEmpty(date)) {
            date = LocalDate.now();
        }
        date = date.with(TemporalAdjusters.lastDayOfMonth());
        KpiParameterBase kpiParameterBase = kpiParameterBaseMapper.selectOne(Wrappers.<KpiParameterBase>lambdaQuery()
                .eq(KpiParameterBase::getParameterStatus, RecordStatus.TAKE_EFFECT.name())
                .eq(KpiParameterBase::getSource, KpiParameterConfigCodeEnum.PROJECT_RADIO.name())
                .le(KpiParameterBase::getEffectMonth, date)
                .orderByDesc(KpiParameterBase::getEffectMonth)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(kpiParameterBase)) {
            return null;
        }
        return kpiParameterConfigService.list(Wrappers.<KpiParameterConfig>lambdaQuery()
                .eq(KpiParameterConfig::getParameterBaseId, kpiParameterBase.getId()));
    }

}