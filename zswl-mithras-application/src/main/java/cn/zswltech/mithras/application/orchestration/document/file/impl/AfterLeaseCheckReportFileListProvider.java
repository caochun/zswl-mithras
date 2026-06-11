package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportMaterialsEnum;
import cn.zswltech.mithras.afterlease.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.bo.FileListExtQuery;
import cn.zswltech.mithras.document.model.MaterialsList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 租后检查
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:36 PM
 */
@Component
public class AfterLeaseCheckReportFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        if (CollectionUtils.isNotEmpty(req.getMaterialsTypes())
                && req.getMaterialsTypes().size() == 1
                && NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_NON_PUBLIC_ATTACHMENT.name().equals(req.getMaterialsTypes().get(0))
        ) {
            // 该类型文件使用了subType进行分组 所以需要单独实现逻辑
            Map<String, Pair<String, List<FileListRSP>>> sortMap = new LinkedHashMap<>();
            for (AfterLeaseCheckReportMaterialsEnum checkReportMaterialsEnum : AfterLeaseCheckReportMaterialsEnum.values()) {
                sortMap.put(checkReportMaterialsEnum.name(), new Pair<>(checkReportMaterialsEnum.name(), new ArrayList<>()));
            }
            List<MaterialsList> dataList = listForGroup(req, extQuery);
            if (CollectionUtils.isEmpty(dataList)) {
                return new ArrayList<>(sortMap.values());
            }
            List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
            fileConvert.fillName(rspList);
            rspList.sort(new CommonFileSortComparator());
            for (FileListRSP rsp : rspList) {
                if (sortMap.containsKey(rsp.getMaterialSubType())) {
                    sortMap.get(rsp.getMaterialSubType()).getValue().add(rsp);
                }
            }
            return new ArrayList<>(sortMap.values());
        } else {
            return super.listGroup(req, extQuery);
        }
    }


    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(AfterLeaseAdjustMaterialsEnum.getByName(rsp.getMaterialsType())).map(AfterLeaseAdjustMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
