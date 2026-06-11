package cn.zswltech.mithras.dashboard.mapper;

import cn.zswltech.mithras.dashboard.model.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
public interface DashboardFundFinanceMapper {
    List<DashboardFundRepayResult> listRepay(DashboardFundRepayQuery query);

    List<DashboardFundCreditResult> listCredit(DashboardFundCreditQuery query);

    Page<DashboardFundCostResult> listCost(Page<DashboardFundCostResult> page, @Param("query") DashboardFundCostQuery query);

    List<DashboardFundRepayDateResult> repayDateList(DashboardRepayDateQuery query);
}
