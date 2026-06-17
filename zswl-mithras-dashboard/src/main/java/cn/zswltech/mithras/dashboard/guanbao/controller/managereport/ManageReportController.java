package cn.zswltech.mithras.dashboard.guanbao.controller.managereport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.managereport.ManageReportApi;
import cn.zswltech.mithras.dto.managereport.*;
import cn.zswltech.mithras.dashboard.guanbao.application.managereport.ManageReportApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ManageReportController implements ManageReportApi {

    @Resource
    private ManageReportApplicationService manageReportApplicationService;

    @Override
    public R<PageR<YeWuYunXingFenXiDetailRSP>> queryYeWuYunXingFenXiDetailWithPage(YeWuYunXingFenXiDetailREQ req) {
        return manageReportApplicationService.queryYeWuYunXingFenXiDetailWithPage(req);
    }

    @Override
    public R<List<YeWuYunXingFenXiStatisticRSP>> statisticYeWuYunXingFenXi(YeWuYunXingFenXiStatisticREQ req) {
        return manageReportApplicationService.statisticYeWuYunXingFenXi(req);
    }

    @Override
    public R<List<YunYingDaiBanDetailRSP>> queryYunYingDaiBanDetail(YunYingDaiBanDetailREQ req) {
        return manageReportApplicationService.queryYunYingDaiBanDetail(req);
    }

    @Override
    public R<List<YunYingDaiBanStatisticRSP>> statisticYunYingDaiBan(YunYingDaiBanStatisticREQ req) {
        return manageReportApplicationService.statisticYunYingDaiBan(req);
    }

    @Override
    public R<List<HeTongShiXiaoDetailRSP>> queryHeTongShiXiaoDetail(HeTongShiXiaoDetailREQ req) {
        return manageReportApplicationService.queryHeTongShiXiaoDetail(req);
    }

    @Override
    public R<List<HeTongShiXiaoStatisticRSP>> statisticHeTongShiXiao(HeTongShiXiaoStatisticREQ req) {
        return manageReportApplicationService.statisticHeTongShiXiao(req);
    }
}
