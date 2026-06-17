package cn.zswltech.mithras.dashboard.guanbao.mapper.managereport;

import cn.zswltech.mithras.dashboard.guanbao.model.managereport.YeWuYunXingFenXiQuery;
import cn.zswltech.mithras.dashboard.guanbao.model.managereport.YeWuYunXingFenXiResult;
import cn.zswltech.mithras.dashboard.guanbao.model.managereport.YeWuYunXingFenXiStatisticResult;
import cn.zswltech.mithras.dashboard.guanbao.model.managereport.YunYingDaiBanQuery;
import cn.zswltech.mithras.dashboard.guanbao.model.managereport.YunYingDaiBanResult;

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
