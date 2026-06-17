package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundRepayQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.zswltech.mithras.finance.view.entity.DashboardFvRepayPrincipalInterestSnapshot;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作台还本付息信息表(DashboardFvRepayPrincipalInterestSnapshot)表服务接口
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
public interface DashboardFvRepayPrincipalInterestSnapshotService extends IService<DashboardFvRepayPrincipalInterestSnapshot> {

    void generate(Long mainId, LocalDate dataTime);

    List<DashboardFundFinanceRepayRSP> listRepay(DashboardFundRepayQuery query);
}

