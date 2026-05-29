package cn.zswltech.mithras.service.mapper.policy;

import cn.zswltech.mithras.dto.policy.PolicyInfoListREQ;
import cn.zswltech.mithras.service.mapper.dto.*;
import cn.zswltech.mithras.service.mapper.model.policy.PolicyInfo;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-06-15
 **/
public interface PolicyInfoMapper extends CustomBaseMapper<PolicyInfo> {
    Page<PolicyListDTO> myList(Page<PolicyListDTO> page,
                               @Param("req") PolicyInfoListREQ req);

    Page<PolicyListDTO> ledgerList(Page<PolicyListDTO> page,
                               @Param("req") PolicyListParam param);

    List<NearPolicyEndTimeDTO> nearPolicyEndTimeList(@Param("minTime") LocalDate minTime);

    List<PaymentPolicyEndTimeDTO> paymentMaxTimeList(@Param("ids") List<Long> ids);

    int countPolicyCode(@Param("policyCode") String policyCode);

    List<PolicyCodeCountDTO> countPolicyCodes(@Param("policyCodes") List<String> policyCodes);

    List<PolicyCodeDTO> countPolicyCodeNum(@Param("list") List<String> list);

    int updateNotice(@Param("ids") List<Long> ids, @Param("code") Integer code);

    List<PolicyListDTO> listPaymentNeedRenewInsurance(@Param("ids") List<Long> ids, @Param("contractCode") String contractCode,@Param("contractStatus") String contractStatus);
}
