package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.zswltech.mithras.finance.view.entity.DashboardFvFinanceInfoSnapshot;

import java.time.LocalDate;

/**
 * 工作台融资情况快照表(DashboardFvFinanceInfoSnapshot)表服务接口
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
public interface DashboardFvFinanceInfoSnapshotService extends IService<DashboardFvFinanceInfoSnapshot> {

    /**
     * 生成存量数据
     *
     * @param mainId
     * @param dataTime 数据时点
     */
    void generateAll(Long mainId, LocalDate dataTime);

    /**
     * 生成当年数据
     *
     * @param mainId
     * @param dataTime 数据时点
     */
    void generateYear(Long mainId, LocalDate dataTime);

    /**
     * 生成当月数据
     *
     * @param mainId
     * @param dataTime 数据时点
     */
    void generateMonth(Long mainId, LocalDate dataTime);

    PageR<DashboardFundFinanceLoanInfoRSP> listLoanInfo(DashboardFundFinanceLoanInfoREQ req);
}

