package cn.zswltech.mithras.application.orchestration.adapter.contract.overdue;

import cn.zswltech.mithras.contract.enums.overdue.CollectionActionFileType;
import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionLetterMaterialStore;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

@Service
public class CollectionLetterMaterialStoreImpl implements CollectionLetterMaterialStore {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public void removeGeneratedCollectionLetters(Long actionId) {
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, actionId)
                .eq(MaterialsList::getSystemGenerate, 1)
                .eq(MaterialsList::getIsEdit, YesOrNoNumberEnum.NO.getCode())
                .eq(MaterialsList::getMaterialsType, CollectionActionFileType.COLLECTION.name()));
    }

    @Override
    public void addGeneratedCollectionLetter(InputStream inputStream, String fileName, Long actionId) {
        materialsListService.add(inputStream, fileName, actionId, CollectionActionFileType.COLLECTION.name(), null,
                OverdueBusinessModule.OVERDUE_COLLECTION_ACTION.name(), YesOrNoNumberEnum.YES);
    }
}
