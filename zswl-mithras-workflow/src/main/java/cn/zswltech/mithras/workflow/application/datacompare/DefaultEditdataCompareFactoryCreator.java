package cn.zswltech.mithras.workflow.application.datacompare;

import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactoryCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class DefaultEditdataCompareFactoryCreator implements EditdataCompareFactoryCreator {
    @Resource
    private Map<String, EditdataCompareFactory> factoryMap;

    @Override
    public EditdataCompareFactory buildFactory(String modulCode) {
        return factoryMap.get(modulCode);
    }
}
