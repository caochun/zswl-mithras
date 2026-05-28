package cn.zswltech.mithras.service.service.datacompare.Impl;

import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactoryCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @create: 2022-08-03
 **/
@Service
public class DefaultEditdataCompareFactoryCreator implements EditdataCompareFactoryCreator {
    @Resource
    private Map<String,EditdataCompareFactory> factoryMap;

    @Override
    public EditdataCompareFactory buildFactory(String modulCode) {
        return factoryMap.get(modulCode);
    }
}
