package cn.zswltech.mithras.dashboard.interfaces.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.BusinessMonthCollectApi;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListRSP;
import cn.zswltech.mithras.dashboard.application.boss.BusinessMonthCollectApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/23 09:53
 * @description
 */
@Slf4j
@RestController
public class BusinessMonthCollectController implements BusinessMonthCollectApi {

    @Resource
    private BusinessMonthCollectApplicationService businessMonthCollectService;

    @Override
    public R<List<MonthCollectStatisticsListRSP>> getMonthCollectStatisticsList(MonthCollectStatisticsListREQ req) {
        return R.ok(businessMonthCollectService.getMonthCollectStatisticsList(req));
    }
}
