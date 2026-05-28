package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * 租赁物清单-项目经理
 *
 * @author yangxiong
 * @date 2023-09-22
 */
@Component
public class LeaseDataListFileProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.LEASE_DATA_LIST;
    }

}
