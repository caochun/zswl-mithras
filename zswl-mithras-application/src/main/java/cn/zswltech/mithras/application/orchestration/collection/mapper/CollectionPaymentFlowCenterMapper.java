package cn.zswltech.mithras.application.orchestration.collection.mapper;

import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListRSP;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CollectionPaymentFlowCenterMapper {

    Page<CollectionFlowCenterBusinessPaymentListRSP> paymentFlowList(Page<CollectionFlowCenterBusinessPaymentListRSP> page,
                                                                     @Param("dto") CollectionFlowCenterBusinessPaymentListREQ param);
}
