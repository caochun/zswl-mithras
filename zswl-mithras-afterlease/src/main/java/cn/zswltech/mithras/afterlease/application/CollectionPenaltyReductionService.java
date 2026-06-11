package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.afterlease.CollectionPenaltyReductionInfoListREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractREQ;
import cn.zswltech.mithras.dto.afterlease.CollectionRelationContractRSP;
import cn.zswltech.mithras.afterlease.mapper.model.CollectionPenaltyReductionInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


public interface CollectionPenaltyReductionService extends IService<CollectionPenaltyReductionInfo> {

    Page<CollectionPenaltyReductionInfo> list(CollectionPenaltyReductionInfoListREQ req);

    CollectionRelationContractRSP relationContract(CollectionRelationContractREQ req);

}
