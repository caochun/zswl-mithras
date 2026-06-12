package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyCheckMaterialsEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.query.FileListExtQuery;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 五级分类 复核审批
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:47 PM
 */
@Component
public class AssetClassifyReviewFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.ASSET_CLASSIFY_REVIEW;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        if (CollectionUtils.isNotEmpty(req.getMaterialsTypes())
                && req.getMaterialsTypes().size() == 1
                && AssetClassifyMaterialsEnum.ASSET_CLASSIFY_CHECK_REPORT.name().equals(req.getMaterialsTypes().get(0))
        ) {
            // 特殊处理 此处根据子类型分组
            List<MaterialsList> dataList = listForGroup(req, extQuery);
            if (CollectionUtils.isEmpty(dataList)) {
                return new ArrayList<>();
            }
            List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
            fileConvert.fillName(rspList);
            rspList.sort(new CommonFileSortComparator());
            List<List<FileListRSP>> groupRspList = rspList.stream()
                    .filter(s -> Objects.nonNull(s.getMaterialSubType()))
                    .collect(Collectors.groupingBy(FileListRSP::getMaterialSubType))
                    .values().stream()
                    .collect(Collectors.toList());
            // 分组排序
            groupRspList.sort(Comparator.comparing(rsp -> Optional.ofNullable(AssetClassifyCheckMaterialsEnum.of(rsp.get(0).getMaterialSubType())).map(AssetClassifyCheckMaterialsEnum::getSort).orElse(Integer.MAX_VALUE)));
            List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
            for (List<FileListRSP> groupRsp : groupRspList) {
                resList.add(new Pair<>(groupRsp.get(0).getMaterialSubType(), groupRsp));
            }
            return resList;
        } else {
            return super.listGroup(req, extQuery);
        }
    }


    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(AssetClassifyMaterialsEnum.of(rsp.getMaterialsType())).map(AssetClassifyMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
