package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayStatisticsRSP;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DashboardOperationPayApplicationService {

    List<DashboardOperationPayListRSP> payList(DashboardOperationPayListREQ req);

    List<DashboardOperationPayStatisticsRSP> payStatistics(DashboardOperationPayStatisticsREQ req);

    void importExcel(MultipartFile file, String fileType);
}
