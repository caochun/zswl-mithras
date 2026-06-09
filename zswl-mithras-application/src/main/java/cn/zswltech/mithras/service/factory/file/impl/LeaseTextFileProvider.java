package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * 租赁物清单
 *
 * @author yangxiong
 * @date 2023-09-22
 */
@Component
public class LeaseTextFileProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.LEASE_TEXT;
    }

}
