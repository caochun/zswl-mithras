package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardOperateTodoApi;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveRSP;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.DashboardOperateTodoApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@Slf4j
public class DashboardOperateTodoController implements DashboardOperateTodoApi {

    @Resource
    private DashboardOperateTodoApplicationService dashboardOperateTodoService;

    @Override
    public R<List<DashboardOperateTodoArriveRSP>> todoStatistics(DashboardOperateTodoArriveREQ req) {
        if(DashboardOperateTodoArriveREQ.ARRIVE.equals(req.getType())) {
            return R.ok(dashboardOperateTodoService.todoStatisticsArrive(req));
        }else if(DashboardOperateTodoArriveREQ.WILL_ARRIVE.equals(req.getType())){
            return R.ok(dashboardOperateTodoService.todoStatisticsWillArrive(req));
        }else{
            throw new MithrasException("该类型未注册");
        }
    }

}
