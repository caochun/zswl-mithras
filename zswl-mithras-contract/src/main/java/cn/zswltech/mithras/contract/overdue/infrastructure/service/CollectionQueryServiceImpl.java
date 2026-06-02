package cn.zswltech.mithras.contract.overdue.infrastructure.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionQueryService;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.OverdueCollectionMapper;
import cn.zswltech.mithras.service.service.CurrentUserDataScopeResolver;
import cn.zswltech.mithras.service.service.CurrentUserResolver;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 17:04
 */
@Service
public class CollectionQueryServiceImpl implements CollectionQueryService {

    @Resource
    private OverdueCollectionMapper overdueCollectionMapper;
    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Override
    public List<CollectionListDto> page(CollectionPageQuery query) {
        query.setPage(1);
        query.setPageSize(Integer.MAX_VALUE);
        Page<CollectionListDto> allPage = overdueCollectionMapper.advancedList(new Page<>(query.getPage(), query.getPageSize()), query);
        if (allPage.getRecords().isEmpty()) {
            return new ArrayList<>();
        }
        //客户下合同状态=起租的主办或协办为自己的逾期客户清单
        List<CollectionListDto> collectionListDtoList = allPage.getRecords();
        Set<Long> clientIds = collectionListDtoList.stream().map(CollectionListDto::getClientId).collect(Collectors.toSet());
        Long currentUserId = currentUserResolver.currentUserId();
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getClientId, clientIds)
                        .eq(ContractBaseInfo::getContractStatus, "START_RENT"));
        Set<Long> targetClientIDs = new HashSet<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            if (contractBaseInfo.getProjSponsorUserId() != null
                    && contractBaseInfo.getProjSponsorUserId().equals(currentUserId)) {
                targetClientIDs.add(contractBaseInfo.getClientId());
            }
            if (contractBaseInfo.getProjCosponsorUserIds() != null) {
                List<Long> cosponsorList = StringUtils.isBlank(contractBaseInfo.getProjCosponsorUserIds())
                        ? new ArrayList<>() : JSONArray.parseArray(contractBaseInfo.getProjCosponsorUserIds(), Long.class);
                if (!cosponsorList.isEmpty() && cosponsorList.contains(currentUserId)) {
                    targetClientIDs.add(contractBaseInfo.getClientId());
                }
            }
        }
        Set<CollectionListDto> res = new HashSet<>();
        for (CollectionListDto collectionListDto : collectionListDtoList) {
            if (targetClientIDs.contains(collectionListDto.getClientId())) {
                res.add(collectionListDto);
            }
        }
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        query.setIsBizUser(isBizUser);
        if (ObjectUtil.isEmpty(canViewDeptIds)) {
            query.setDeptIdList(Collections.singletonList(0L));
        } else {
            query.setDeptIdList(canViewDeptIds);
        }
        query.setCurrentUserId(currentUserId);
        Page<CollectionListDto> collectionListDtoPage = overdueCollectionMapper.advancedList(new Page<>(query.getPage(), query.getPageSize()), query);
        res.addAll(collectionListDtoPage.getRecords());
        return new ArrayList<>(res);
    }
}
