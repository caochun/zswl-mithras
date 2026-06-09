package cn.zswltech.mithras.application.facade.finance.monthly;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.monthly.application.MonthlyManageApplicationService;
import cn.zswltech.mithras.service.service.monthly.MonthlyManageApiService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;


@Service
public class MonthlyManageFacade implements MonthlyManageApplicationService {

    @Resource
    private MonthlyManageApiService monthlyManageApiService;

    @Override
    public R<PageR<MonthlyListRSP>> listPage(MonthlyListREQ req) {
        return R.ok(monthlyManageApiService.listPage(req));
    }

    @Override
    public R<Long> addBaseInfo(MonthlyAddREQ req) {
        return R.ok(monthlyManageApiService.addBaseInfo(req));
    }

    @Override
    public R<PageR<MonthlyAIRListRSP>> airPage(MonthlyAIRListREQ req) {
        return R.ok(monthlyManageApiService.airList(req));
    }

    @Override
    public R<PageR<MonthlyRPListRSP>> rpPage(MonthlyRPListREQ req) {
        return R.ok(monthlyManageApiService.rpList(req));
    }

    @Override
    public R<PageR<MonthlyStampDutyProjRSP>> projPage(MonthlyStampDutyProjREQ req) {
        return R.ok(monthlyManageApiService.projPage(req));
    }

    @Override
    public R<PageR<MonthlyStampDutyFinRSP>> finPage(MonthlyStampDutyFinREQ req) {
        return R.ok(monthlyManageApiService.finPage(req));
    }

    @Override
    public void download(ServletOutputStream outputStream, MonthlyExcelREQ req) {
        monthlyManageApiService.download(outputStream, req);
    }

    @Override
    public R<PageR<MonthlyCostRSP>> costPage(MonthlyCostREQ req) {
        return R.ok(monthlyManageApiService.costList(req));
    }

//    @Override
//    public R<Void> validate(MonthlyCostValidateREQ req) {
//        monthlyManageApiService.validate(req);
//        return R.ok();
//    }

    @Override
    public R<Void> submit(MonthlySubmitREQ req) {
        monthlyManageApiService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> freshById(MonthlyFreshDataREQ req) {
        monthlyManageApiService.freshById(req);
        return R.ok();
    }

    @Override
    public R<Void> close(MonthlyCloseREQ req) {
        monthlyManageApiService.close(req);
        return R.ok();
    }

    @Override
    public R<Void> updateStatus(MonthlyUpdateStatusREQ req) {
        monthlyManageApiService.updateStatus(req);
        return R.ok();
    }

    @Override
    public R<Void> closeValidate(MonthlyCloseREQ req) {
        monthlyManageApiService.closeValidate(req);
        return R.ok();
    }

    @Override
    public R<Void> updateSingle(MonthlyFreshSingleREQ req) {
        monthlyManageApiService.updateSingle(req);
        return R.ok();
    }

    @Override
    public R<Void> pushSingle(MonthlySubmitSingleREQ req) {
        monthlyManageApiService.pushSingle(req);
        return R.ok();
    }
}
