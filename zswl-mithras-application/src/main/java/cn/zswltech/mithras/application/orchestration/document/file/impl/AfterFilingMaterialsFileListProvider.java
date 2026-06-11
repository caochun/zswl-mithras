package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingMaterialsConfigDTO;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.query.FileListExtQuery;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.filingmaterials.AfterFilingMaterialsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 租后-归档
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class AfterFilingMaterialsFileListProvider extends AbstractFileListProvider {
    @Resource
    private AfterFilingMaterialsService afterFilingMaterialsService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.AFTER_LEASING_FILING;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        Map<String, Pair<String, List<FileListRSP>>> sortMap = new LinkedHashMap<>();
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOById = afterFilingMaterialsService.getFilingMaterialsConfigDTOById(req.getMainId());
        List<FilingMaterialsConfigDTO> collect = filingMaterialsConfigDTOById.stream().sorted(Comparator.comparing(FilingMaterialsConfigDTO::getSortCode)).collect(Collectors.toList());
        for (FilingMaterialsConfigDTO filingMaterialsConfigDTO : collect) {
            sortMap.put(filingMaterialsConfigDTO.getDirCode(), new Pair<>(filingMaterialsConfigDTO.getDirCode(), new ArrayList<>()));
        }
        List<MaterialsList> dataList = listForGroup(req, extQuery);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>(sortMap.values());
        }
        List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
        fileConvert.fillName(rspList);
        rspList.sort(new CommonFileSortComparator());
        rspList.stream().forEach(e -> {
            if (Objects.equals(FilingMaterialsConstants.BASIC_INFORMATION, e.getMaterialsType())
                    && Objects.equals(YesOrNoNumberEnum.YES.getCode(), e.getSystemGenerate())) {
                e.setCreateByName(null);
                e.setCreateTime(null);
            }
        });
        for (FileListRSP rsp : rspList) {
            if (sortMap.containsKey(rsp.getMaterialsType())) {
                sortMap.get(rsp.getMaterialsType()).getValue().add(rsp);
            }
        }
        return new ArrayList<>(sortMap.values());

    }

}
