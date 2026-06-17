package cn.zswltech.mithras.policy.persistence.mapper;

import cn.zswltech.mithras.dto.policy.PolicyInfoListREQ;
import cn.zswltech.mithras.policy.persistence.projection.NearPolicyEndTimeProjection;
import cn.zswltech.mithras.policy.persistence.projection.PaymentPolicyEndTimeProjection;
import cn.zswltech.mithras.policy.persistence.projection.PolicyListProjection;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-06-15
 **/
public interface PolicyInfoMapper extends CustomBaseMapper<PolicyInfo> {
    Page<PolicyListProjection> myList(Page<PolicyListProjection> page,
                               @Param("req") PolicyInfoListREQ req);

    List<NearPolicyEndTimeProjection> nearPolicyEndTimeList(@Param("minTime") LocalDate minTime);

    List<PaymentPolicyEndTimeProjection> paymentMaxTimeList(@Param("ids") List<Long> ids);

    int updateNotice(@Param("ids") List<Long> ids, @Param("code") Integer code);

    List<PolicyListProjection> listPaymentNeedRenewInsurance(@Param("ids") List<Long> ids, @Param("contractCode") String contractCode,@Param("contractStatus") String contractStatus);
}
