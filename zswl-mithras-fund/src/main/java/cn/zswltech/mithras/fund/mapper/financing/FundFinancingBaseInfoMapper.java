package cn.zswltech.mithras.fund.mapper.financing;

import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceListREQ;
import cn.zswltech.mithras.dto.capital.FinanceCodeListRSP;
import cn.zswltech.mithras.dto.capital.UnionSelectAllPlanDTO;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RepayPrincipalInterestListREQ;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.application.financing.dto.RepayPrincipalInterestDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yangxiong
 */
@Mapper
public interface FundFinancingBaseInfoMapper extends CustomBaseMapper<FundFinancingBaseInfo> {

    Page<UnionSelectAllPlanDTO> unionSelectAllPlan(Page<UnionSelectAllPlanDTO> page, @Param(value = "req") BusinessFlowFinanceListREQ req);

    List<FinanceCodeListRSP> financeCodeList(@Param("orgName") String orgName);

    Page<DashboardFundFinanceLoanInfoRSP> listLoanInfo(Page<DashboardFundFinanceLoanInfoRSP> page, @Param("req") DashboardFundFinanceLoanInfoREQ req);

    @Deprecated
    Page<DashboardFundFinanceBalanceRSP> listBalance(Page<DashboardFundFinanceBalanceRSP> page, @Param("req") DashboardFundFinanceBalanceREQ req);

    Page<RepayPrincipalInterestDto> selectRepayPrincipalInterestList(Page<RepayPrincipalInterestDto> page, @Param("req") RepayPrincipalInterestListREQ req);
}
