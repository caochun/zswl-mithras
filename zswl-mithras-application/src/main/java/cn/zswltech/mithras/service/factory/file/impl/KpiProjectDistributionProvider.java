package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * 项目绩效分配表
 */
@Component
public class KpiProjectDistributionProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION;
    }

}
