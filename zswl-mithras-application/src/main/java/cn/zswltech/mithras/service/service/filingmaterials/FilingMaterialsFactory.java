package cn.zswltech.mithras.service.service.filingmaterials;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *@author: lllin
 *@CreateTime: 2026-01-09
 */
@Service
public class FilingMaterialsFactory {
    @Resource
    @Qualifier("FundFilingMaterialsService")
    private AbstractFilingMaterialsService fundFilingMaterialsService;
    @Resource
    @Qualifier("FilingMaterialsService")
    private AbstractFilingMaterialsService filingMaterialsService;
    @Resource
    @Qualifier("AfterFilingMaterialsService")
    private AbstractFilingMaterialsService afterFilingMaterialsService;
    @Resource
    @Qualifier("OtherFilingMaterialsService")
    private AbstractFilingMaterialsService otherFilingMaterialsService;
    private static final Map<String, AbstractFilingMaterialsService> serviceProcessors = new HashMap<>();

    @PostConstruct
    public void init() {
        serviceProcessors.put("FilingMaterialsApplyFlow",filingMaterialsService);
        serviceProcessors.put("FundFilingMaterialsApplyFlow",fundFilingMaterialsService);
        serviceProcessors.put("AfterFilingMaterialsApplyFlow",afterFilingMaterialsService);
        serviceProcessors.put("OtherFilingMaterialsApplyFlow",otherFilingMaterialsService);

    }

    public static AbstractFilingMaterialsService getProcessor(String processType){
        if(!serviceProcessors.containsKey(processType)){
            throw new MithrasException(String.format("未找到该流程类型：%s的处理对象！",processType));
        }
        return serviceProcessors.get(processType);
    }
}
