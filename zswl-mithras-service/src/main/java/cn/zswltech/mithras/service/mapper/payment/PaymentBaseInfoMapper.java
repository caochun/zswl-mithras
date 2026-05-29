package cn.zswltech.mithras.service.mapper.payment;

import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListRSP;
import cn.zswltech.mithras.service.mapper.dto.PaymentListDto;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description payment_base_info
* @author zhaozhengkang
* @date 2022-08-12
*/
public interface PaymentBaseInfoMapper extends CustomBaseMapper<PaymentBaseInfo> {

    Page<PaymentBaseInfo> myList(Page<PaymentBaseInfo> page,
                                    @Param("dto") PaymentListDto selectDTO);

    Page<CollectionFlowCenterBusinessPaymentListRSP> paymentFlowList(Page<PaymentBaseInfo> page, @Param("dto") CollectionFlowCenterBusinessPaymentListREQ param);

    int updateFinanceStatusByCode(@Param("codes")List<String> codes);


    /**
     * 查询审批通过的付款申请列表
     * @param contractId 合同id
     * @return 付款申请列表
     */
    List<PaymentBaseInfo> queryListWithContractId(@Param("contractId") Long contractId);
}