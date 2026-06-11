package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionInfoListREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.afterlease.mapper.CollectionPenaltyReductionInfoMapper;
import cn.zswltech.mithras.afterlease.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.CollectionPenaltyReductionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName CollectionPenaltyReductionServiceImpl
 * 罚息减免
 * @Author jackerhe
 * @Date 2022/11/19 2:23 下午
 * @Version 1.0
 **/
@Service
public class CollectionPenaltyReductionServiceImpl extends ServiceImpl<CollectionPenaltyReductionInfoMapper, CollectionPenaltyReductionInfo> implements CollectionPenaltyReductionService  {
    @Override
    public Page<CollectionPenaltyReductionInfo> list(CollectionPenaltyReductionInfoListREQ req) {
        return page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CollectionPenaltyReductionInfo>lambdaQuery()
                .eq(CollectionPenaltyReductionInfo::getContractId, req.getContractId())
                .eq(ObjectUtil.isNotNull(req.getId()), CollectionPenaltyReductionInfo::getId, req.getId())
                .in(ObjectUtil.isNotEmpty(req.getCollectionStatus()), CollectionPenaltyReductionInfo::getCollectionStatus, req.getCollectionStatus())
                .orderByDesc(CollectionPenaltyReductionInfo::getCreateTime));
    }

    @Override
    public CollectionRelationContractRSP relationContract(CollectionRelationContractREQ req) {
        CollectionPenaltyReductionInfo baseInfo = getById(req.getReduceId());
        if(ObjectUtil.isNull(baseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return CollectionRelationContractRSP.builder().contractId(baseInfo.getContractId()).build();
    }


}
