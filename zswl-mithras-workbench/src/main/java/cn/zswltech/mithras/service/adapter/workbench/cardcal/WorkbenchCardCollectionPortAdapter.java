package cn.zswltech.mithras.service.adapter.workbench.cardcal;

import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.workbench.application.cardcal.WorkbenchCardCollectionPort;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchCollectionAmount;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkbenchCardCollectionPortAdapter implements WorkbenchCardCollectionPort {
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public List<WorkbenchCollectionAmount> listPlanCollections(LocalDate start, LocalDate end) {
        return collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .ge(CollectionBaseInfo::getPlanCollectionDate, start)
                        .le(CollectionBaseInfo::getPlanCollectionDate, end))
                .stream()
                .map(collection -> new WorkbenchCollectionAmount(
                        collection.getWriteOffStatus(),
                        collection.getPlanCollectionAmount(),
                        collection.getCollectionAmount()))
                .collect(Collectors.toList());
    }
}
