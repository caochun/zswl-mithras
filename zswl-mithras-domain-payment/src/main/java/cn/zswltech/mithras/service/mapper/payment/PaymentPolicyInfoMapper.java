package cn.zswltech.mithras.service.mapper.payment;

import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description payment_policy_info
* @author zhaozhengkang
* @date 2022-09-13
*/
public interface PaymentPolicyInfoMapper extends CustomBaseMapper<PaymentPolicyInfo> {

    int countPolicyCode(@Param("policyCode") String policyCode);

    List<PaymentPolicyInfo> listPolicyCode(@Param("policyCodes") List<String> policyCodes);

    int occupyPolicy(@Param("contractId") Long contractId, @Param("paymentId") Long paymentId);

    int cancelOccupyPolicy(@Param("contractId") Long contractId, @Param("paymentId") Long paymentId);
}