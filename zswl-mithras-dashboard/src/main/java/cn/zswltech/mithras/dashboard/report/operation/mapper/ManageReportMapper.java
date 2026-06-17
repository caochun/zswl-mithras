package cn.zswltech.mithras.dashboard.report.operation.mapper;

import cn.zswltech.mithras.dashboard.report.operation.model.YeWuYunXingFenXiQuery;
import cn.zswltech.mithras.dashboard.report.operation.model.YeWuYunXingFenXiResult;
import cn.zswltech.mithras.dashboard.report.operation.model.YeWuYunXingFenXiStatisticResult;
import cn.zswltech.mithras.dashboard.report.operation.model.YunYingDaiBanQuery;
import cn.zswltech.mithras.dashboard.report.operation.model.YunYingDaiBanResult;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
public interface ManageReportMapper {
    List<YeWuYunXingFenXiResult> selectYeWuYunXingFenXi(YeWuYunXingFenXiQuery query);

    long countYeWuYunXingFenXi(YeWuYunXingFenXiQuery query);

    List<YeWuYunXingFenXiStatisticResult> statisticYeWuYunXingFenXi(YeWuYunXingFenXiQuery query);

    List<YunYingDaiBanResult> selectYunYingDaiBan(YunYingDaiBanQuery query);
}
