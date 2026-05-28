package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingMaterialsConfigDTO;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.filingmaterials.OtherFilingMaterialsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 其他-归档
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class OtherFilingMaterialsFileListProvider extends AbstractFileListProvider {
    @Resource
    private OtherFilingMaterialsService otherFilingMaterialsService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.OTHER_FILING;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        Map<String, Pair<String, List<FileListRSP>>> sortMap = new LinkedHashMap<>();
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOById = otherFilingMaterialsService.getFilingMaterialsConfigDTOById(req.getMainId());
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
