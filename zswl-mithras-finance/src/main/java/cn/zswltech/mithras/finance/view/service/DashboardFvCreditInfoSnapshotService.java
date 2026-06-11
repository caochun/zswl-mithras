package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceCreditInfoRSP;
import cn.zswltech.mithras.dashboard.mapper.model.DashboardFundCreditQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCreditInfoSnapshot;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作台授信信息快照表(DashboardFvCreditInfoSnapshot)表服务接口
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
public interface DashboardFvCreditInfoSnapshotService extends IService<DashboardFvCreditInfoSnapshot> {

    void generate(Long mainId, LocalDate dataTime);

    List<DashboardFundFinanceCreditInfoRSP> listCredit(DashboardFundCreditQuery query);
}

