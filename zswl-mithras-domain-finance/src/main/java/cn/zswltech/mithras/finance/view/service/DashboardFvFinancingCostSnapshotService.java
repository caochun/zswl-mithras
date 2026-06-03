package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsRSP;
import cn.zswltech.mithras.finance.view.entity.DashboardFvFinancingCostSnapshot;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardFundCostQuery;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * 工作台融资成本信息快照表(DashboardFvFinancingCostSnapshot)表服务接口
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
public interface DashboardFvFinancingCostSnapshotService extends IService<DashboardFvFinancingCostSnapshot> {

    void generate(Long mainId, LocalDate dataTime);

    PageR<DashboardFundFinanceFundsRSP> listCost(DashboardFundCostQuery query);
}

