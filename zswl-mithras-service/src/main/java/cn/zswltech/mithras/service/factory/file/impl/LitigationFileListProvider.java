package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQAdjustExt;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.service.enums.overdue.LitigationFileType;
import cn.zswltech.mithras.service.enums.trackEvent.TrackEventMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/6 14:43
 */
@Component
public class LitigationFileListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.LITIGATION_REGISTRATION;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(Stream.of(LitigationFileType.values()).map(LitigationFileType::name).collect(Collectors.toList()));
        return listGroup(req, extQuery);
    }


    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.of(LitigationFileType.valueOf(rsp.getMaterialsType())).map(LitigationFileType::getSort).orElse(Integer.MAX_VALUE);
    }
}
