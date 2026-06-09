package cn.zswltech.mithras.application.adapter.fund;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.fund.application.process.prepare.FundProcessPrepareMaterialPort;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
public class FundProcessPrepareMaterialPortAdapter implements FundProcessPrepareMaterialPort {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public boolean hasMaterials(String businessType, List<String> materialTypes, Long belongId) {
        return CollectionUtil.isNotEmpty(materialsListService.list(businessType, materialTypes, Collections.singletonList(belongId)));
    }
}
