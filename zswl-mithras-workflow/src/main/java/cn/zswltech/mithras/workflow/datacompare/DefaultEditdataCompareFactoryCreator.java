package cn.zswltech.mithras.workflow.datacompare;

import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactoryCreator;
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
