package cn.zswltech.mithras.service.mapper.managereport;

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
