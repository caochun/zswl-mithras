package cn.zswltech.mithras.kpi.convert;

import cn.zswltech.mithras.dto.kpi.parameterconfig.KpiParameterConfigBase;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfigRecord;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description
 */
public class KpiParameterConfigConvert {
    public static <T extends KpiParameterConfigBase> T toConfigBase(KpiParameterConfig parameterConfig, Class<T> clz) throws Exception {
        T t = clz.newInstance();
        t.setId(parameterConfig.getId());
        t.setConfigCode(parameterConfig.getConfigCode());
        t.setConfigDesc(parameterConfig.getConfigDesc());
        return t;
    }

    public static <T extends KpiParameterConfigBase> T toConfigBaseRecord(KpiParameterConfigRecord parameterConfig, Class<T> clz) throws Exception {
        T t = clz.newInstance();
        t.setId(parameterConfig.getParameterConfigId());
        t.setConfigCode(parameterConfig.getConfigCode());
        t.setConfigDesc(parameterConfig.getConfigDesc());
        return t;
    }

}
