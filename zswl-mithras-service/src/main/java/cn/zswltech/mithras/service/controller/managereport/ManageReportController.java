package cn.zswltech.mithras.service.controller.managereport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.managereport.ManageReportApi;
import cn.zswltech.mithras.dto.managereport.*;
import cn.zswltech.mithras.service.service.managereport.ManageReportService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@RestController
public class ManageReportController implements ManageReportApi {
    @Resource
    private ManageReportService manageReportService;

    @Override
    public R<PageR<YeWuYunXingFenXiDetailRSP>> queryYeWuYunXingFenXiDetailWithPage(YeWuYunXingFenXiDetailREQ req) {
        return R.ok(manageReportService.queryYeWuYunXingFenXiDetailWithPage(req));
    }

    @Override
    public R<List<YeWuYunXingFenXiStatisticRSP>> statisticYeWuYunXingFenXi(YeWuYunXingFenXiStatisticREQ req) {
        return R.ok(manageReportService.statisticYeWuYunXingFenXi(req));
    }

    @Override
    public R<List<YunYingDaiBanDetailRSP>> queryYunYingDaiBanDetail(YunYingDaiBanDetailREQ req) {
        return R.ok(manageReportService.queryYunYingDaiBanDetail(req));
    }

    @Override
    public R<List<YunYingDaiBanStatisticRSP>> statisticYunYingDaiBan(YunYingDaiBanStatisticREQ req) {
        return R.ok(manageReportService.statisticYunYingDaiBan(req));
    }

    @Override
    public R<List<HeTongShiXiaoDetailRSP>> queryHeTongShiXiaoDetail(HeTongShiXiaoDetailREQ req) {
        return R.ok(manageReportService.queryHeTongShiXiaoDetail(req));
    }

    @Override
    public R<List<HeTongShiXiaoStatisticRSP>> statisticHeTongShiXiao(HeTongShiXiaoStatisticREQ req) {
        return R.ok(manageReportService.statisticHeTongShiXiao(req));
    }
}
