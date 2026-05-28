package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQContractExt;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractFileQueryType;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import cn.zswltech.mithras.service.enums.payment.PaymentTypeEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 付款模块
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:37 PM
 */
@Component
public class PaymentFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PAYMENT;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(LendingMaterialType.getByName(rsp.getMaterialsType())).map(LendingMaterialType::getSort).orElse(Integer.MAX_VALUE);
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        Map<String, Object> ext = req.getExt();
        if (!Objects.isNull(ext)) {
            FileListExtQuery extQuery = new FileListExtQuery();
            extQuery.setMaterialsTypes(Collections.singletonList(String.valueOf(ext.get("queryType"))));
            return super.listGroup(req, extQuery);
        }
        List<MaterialsList> dataList;
        if (StringUtils.isBlank(req.getVersion())) {
            dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsList::getBelongId, req.getMainId())
                    .notIn(MaterialsList::getMaterialsType, PaymentTypeEnum.LOAN_REVIEW.name())
            );
        } else {
            dataList = materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getBusinessType, getBusinessModule().name())
                    .eq(MaterialsListLib::getBelongId, req.getMainId())
                    .notIn(MaterialsListLib::getMaterialsType, PaymentTypeEnum.LOAN_REVIEW.name())
                    .notIn(MaterialsList::getMaterialsType,PaymentTypeEnum.LOAN_REVIEW.name())
            ).stream().map(fileConvert::actualLib2Entity).collect(Collectors.toList());
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

}
