package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 五级分类 复核审批
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:47 PM
 */
@Component
public class AssetClassifyMeetingFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.ASSET_CLASSIFY;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        return super.listGroup(req, extQuery);
    }


    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(AssetClassifyMaterialsEnum.of(rsp.getMaterialsType())).map(AssetClassifyMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
