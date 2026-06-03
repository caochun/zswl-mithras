package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.payment.domain.enums.LendingMaterialType;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.service.service.policy.PolicyInfoService;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 保单模块
 *
 */
@Component
public class PolicyFileListProvider extends AbstractFileListProvider {

    @Resource
    private PolicyInfoService policyInfoService;

    private final static String CONTRACT_ID = "contractId";

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.POLICY;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(LendingMaterialType.getByName(rsp.getMaterialsType())).map(LendingMaterialType::getSort).orElse(Integer.MAX_VALUE);
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        return listGroup(req, new FileListExtQuery());
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req, FileListExtQuery extQuery) {
        List<MaterialsList> dataList = listForGroup(req, extQuery);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<FileListRSP> rspList = dataList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
        fileConvert.fillName(rspList);
        rspList.sort(new CommonFileSortComparator());
        List<List<FileListRSP>> groupRspList = new ArrayList<>(rspList.stream()
                .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                .values());
        // 分组排序
        sortGroup(groupRspList);
        List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
        for (List<FileListRSP> groupRsp : groupRspList) {
            resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
        }
        return resList;
    }

    @Override
    protected List<MaterialsList> listForGroup(FileListREQ req, FileListExtQuery extQuery) {
        Map<String, Object> ext = req.getExt();
        if(ObjectUtils.isEmpty(ext)){
            return ListUtil.empty();
        }
        Long contractId = Long.valueOf(ext.get(CONTRACT_ID).toString());
        if(ObjectUtils.isEmpty(contractId)){
            return ListUtil.empty();
        }
        List<PolicyInfo> list = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getContractId, contractId));
        Map<Long, String> policyId2Code = list.stream().collect(Collectors.toMap(PolicyInfo::getId, PolicyInfo::getPolicyCode, (a, b) -> a));
        if(ObjectUtils.isEmpty(list)){
            return ListUtil.empty();
        }
        List<Long> policyIds = list.stream().map(PolicyInfo::getId).collect(Collectors.toList());
        List<MaterialsList> dataList = null;
        if (StringUtils.isBlank(req.getVersion())) {
            dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, req.getModuleType())
                    .in(MaterialsList::getBelongId, policyIds)
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsList::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
            );
        } else {
            dataList = materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getBusinessType, req.getModuleType())
                    .in(MaterialsListLib::getBelongId, policyIds)
                    .in(CollectionUtils.isNotEmpty(req.getMaterialsTypes()), MaterialsListLib::getMaterialsType, req.getMaterialsTypes())
                    .in(CollectionUtils.isNotEmpty(extQuery.getMaterialsTypes()), MaterialsList::getMaterialsType, extQuery.getMaterialsTypes())
            ).stream().map(fileConvert::actualLib2Entity).collect(Collectors.toList());
        }
        dataList.forEach(base -> {
            base.setMaterialsType(policyId2Code.get(base.getBelongId()));
        });
        return dataList;
    }
}
