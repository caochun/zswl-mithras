package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperationContractApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dashboard.application.DashboardOperationContractApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName DashboardOperationContractController
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/15 5:02 下午
 * @Version 1.0
 **/
@Slf4j
@RestController
public class DashboardOperationContractController implements DashboardOperationContractApi {

    @Resource
    private DashboardOperationContractApplicationService dashboardOperationService;
    @Value("${spring.profiles.active}")
    private String environment;

    @Override
    public R<List<DashboardApprovalListRSP>> approvalList(@Valid DashboardApprovalListREQ req) {
        if(Objects.equals(environment, "dev")) {
            return R.ok(dashboardOperationService.approvalList(req));
        }else{
            return R.ok(dashboardOperationService.approvalListGuanYuan(req));
        }
    }

    @Override
    public R<DashboardOperationApprovalStatisticsRSP> approvalStatistics(DashboardApprovalListREQ req) {
        return R.ok(dashboardOperationService.approvalStatistics(req));
    }

    @Override
    public R<List<DashboardContractReturnListRSP>> contractReturnList(@Valid DashboardContractReturnListREQ req) {
        return R.ok(dashboardOperationService.contractReturnList(req));
    }

    @Override
    public R<DashboardOperationContractReturnStatisticsRSP> contractReturnStatistics(DashboardContractReturnListREQ req) {
        return R.ok(dashboardOperationService.contractReturnStatistics(req));
    }
}
