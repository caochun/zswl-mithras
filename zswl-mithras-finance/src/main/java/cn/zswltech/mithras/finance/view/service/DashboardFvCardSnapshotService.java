package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceStatisticsRSP;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCardSnapshot;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作台融资视图卡片快照信息表(DashboardFvCardSnapshot)表服务接口
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
public interface DashboardFvCardSnapshotService extends IService<DashboardFvCardSnapshot> {

    /**
     * 生成工作台融资视图卡片快照信息
     *
     * @param dataTime 数据时点
     */
    void generate(LocalDate dataTime);

    /**
     * 获取工作台融资视图卡片快照信息
     *
     * @param cardCode 工作台融资视图卡片编码
     * @param cardName 工作台融资视图卡片名称
     * @param dataTime               数据时点
     * @param data                   融资视图卡片数据Json字符串
     * @return Long 工作台融资视图卡片Id
     */
    Long generateDashboardFvCardSnapshot(String cardCode, String cardName, LocalDate dataTime, Object data);

    /**
     * 获取工作台融资视图卡片快照数据
     *
     * @param queryDate 查询日期
     * @return List<DashboardFundFinanceStatisticsRSP>
     */
    List<DashboardFundFinanceStatisticsRSP> statisticsList(LocalDate queryDate);
}

