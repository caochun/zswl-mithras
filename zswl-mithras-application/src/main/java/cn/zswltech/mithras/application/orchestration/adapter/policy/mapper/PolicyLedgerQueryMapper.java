package cn.zswltech.mithras.application.orchestration.adapter.policy.mapper;

import cn.zswltech.mithras.policy.persistence.projection.PolicyListParam;
import cn.zswltech.mithras.policy.persistence.projection.PolicyCodeCountProjection;
import cn.zswltech.mithras.policy.persistence.projection.PolicyCodeProjection;
import cn.zswltech.mithras.policy.persistence.projection.PolicyListProjection;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PolicyLedgerQueryMapper {

    Page<PolicyListProjection> ledgerList(Page<PolicyListProjection> page, @Param("req") PolicyListParam param);

    List<PolicyCodeCountProjection> countPolicyCodes(@Param("policyCodes") List<String> policyCodes);

    List<PolicyCodeProjection> countPolicyCodeNum(@Param("list") List<String> list);
}
