package cn.zswltech.mithras.kpi.application.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import cn.zswltech.mithras.kpi.bo.KpiParameterConfigBo;
import cn.zswltech.mithras.kpi.mapper.KpiParameterConfigRecordMapper;
import cn.zswltech.mithras.kpi.model.KpiParameterConfigRecord;
import cn.zswltech.mithras.kpi.convert.KpiParameterConfigConvert;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KpiParameterConfigRecordService extends ServiceImpl<KpiParameterConfigRecordMapper, KpiParameterConfigRecord> {

    @SneakyThrows
    public KpiParameterConfigBo lastByYearAndMonth(LocalDate date) {
        List<String> enumList = KpiParameterConfigCodeEnum.getKpiEnum().stream().map(KpiParameterConfigCodeEnum::name).collect(Collectors.toList());
        KpiParameterConfigRecord kpiParameterConfigRecord = getOne(Wrappers.<KpiParameterConfigRecord>lambdaQuery()
                .eq(KpiParameterConfigRecord::getCalculateDate, date)
                .in(KpiParameterConfigRecord::getConfigCode, enumList)
                .orderByDesc(KpiParameterConfigRecord::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(kpiParameterConfigRecord)) {
            return null;
        }
        List<KpiParameterConfigRecord> configRecords = list(Wrappers.<KpiParameterConfigRecord>lambdaQuery()
                .eq(KpiParameterConfigRecord::getCalculateDate, date)
                .in(KpiParameterConfigRecord::getConfigCode, enumList)
                .eq(KpiParameterConfigRecord::getBatchNumber, kpiParameterConfigRecord.getBatchNumber()));
        if(ObjectUtil.isNotEmpty(configRecords)) {
            return null;
        }
        KpiParameterConfigBo bo = new KpiParameterConfigBo();
        Map<String, KpiParameterConfigRecord> configRecordMap = configRecords.stream().collect(Collectors.toMap(KpiParameterConfigRecord::getConfigCode, e -> e, (a, b) -> a));
        //转化为各配置
        //项目提奖-基础提奖比例
        KpiParameterConfigRecord projectRadioRecord = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO.name());
        if(ObjectUtil.isNotEmpty(projectRadioRecord)) {
            ProjectRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBaseRecord(projectRadioRecord, ProjectRadioConfig.class);
            projectRadioConfig.setParameterBaseId(projectRadioRecord.getParameterBaseId());
            projectRadioConfig.setConfigValue(JSONUtil.toList(projectRadioRecord.getConfigValue(), ProjectRadioConfig.Data.class));
            bo.setBatchNumber(projectRadioRecord.getBatchNumber());
            bo.setProjectRadioConfig(projectRadioConfig);
        }
        //项目提奖-项目类型系数
        KpiParameterConfigRecord projectTypeRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_TYPE.name());
        if(ObjectUtil.isNotEmpty(projectTypeRadioConfig)) {
            ProjectTypeRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBaseRecord(projectTypeRadioConfig, ProjectTypeRadioConfig.class);
            projectRadioConfig.setParameterBaseId(projectTypeRadioConfig.getParameterBaseId());
            projectRadioConfig.setConfigValue(JSONUtil.toList(projectTypeRadioConfig.getConfigValue(), ProjectTypeRadioConfig.ProjTypeRatio.class));
            bo.setBatchNumber(projectTypeRadioConfig.getBatchNumber());
            bo.setProjectTypeRadioConfig(projectRadioConfig);
        }
        //项目提奖-项目规模系数
        KpiParameterConfigRecord projectScaleRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_SCALE.name());
        if(ObjectUtil.isNotEmpty(projectScaleRadioConfig)) {
            ProjectScaleRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBaseRecord(projectScaleRadioConfig, ProjectScaleRadioConfig.class);
            projectRadioConfig.setParameterBaseId(projectScaleRadioConfig.getParameterBaseId());
            projectRadioConfig.setConfigValue(JSONUtil.toList(projectScaleRadioConfig.getConfigValue(), ProjectScaleRadioConfig.ProjScaleRatio.class));
            bo.setBatchNumber(projectScaleRadioConfig.getBatchNumber());
            bo.setProjectScaleRadioConfig(projectRadioConfig);
        }
        //项目提奖-投放奖金系数
        KpiParameterConfigRecord projectPaymentBonusRadioConfig = configRecordMap.get(KpiParameterConfigCodeEnum.PROJECT_RADIO_PAYMENT.name());
        if(ObjectUtil.isNotEmpty(projectPaymentBonusRadioConfig)) {
            ProjectPaymentBonusRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBaseRecord(projectPaymentBonusRadioConfig, ProjectPaymentBonusRadioConfig.class);
            projectRadioConfig.setParameterBaseId(projectPaymentBonusRadioConfig.getParameterBaseId());
            projectRadioConfig.setConfigValue(JSONUtil.toList(projectPaymentBonusRadioConfig.getConfigValue(), ProjectPaymentBonusRadioConfig.PaymentBonusRatio.class));
            bo.setBatchNumber(projectPaymentBonusRadioConfig.getBatchNumber());
            bo.setProjectPaymentBonusRadioConfig(projectRadioConfig);
        }
        return bo;
    }

}
