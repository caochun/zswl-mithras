package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjGuessBaseInfoApi;
import cn.zswltech.mithras.kpi.application.KpiProjGuessBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;
import java.util.List;

@RestController
public class KpiProjGuessBaseInfoController implements KpiProjGuessBaseInfoApi {
    @Resource
    private KpiProjGuessBaseInfoApplicationService kpiProjGuessBaseInfoApplicationService;

    @Override
    public R<PageR<KpiProjGuessContractIndexRSP>> contractList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessBaseInfoApplicationService.contractList(req);
    }

    @Override
    public R<List<KpiProjGuessContractIndexRSP>> timeList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessBaseInfoApplicationService.timeList(req);
    }

    @Override
    public R<List<KpiProjGuessContractIndexRSP>> deptList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessBaseInfoApplicationService.deptList(req);
    }

    @Override
    public R<List<KpiProjGuessPeopleIndexRSP>> peopleList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessBaseInfoApplicationService.peopleList(req);
    }

    @Override
    public R<PageR<KpiProjGuessDetailRSP>> contractDetail(KpiProjGuessDetailREQ req) {
        return kpiProjGuessBaseInfoApplicationService.contractDetail(req);
    }

    @Override
    public R<PageR<KpiProjGuessPeopleDetailRSP>> peopleDetail(KpiProjGuessDetailREQ req) {
        return kpiProjGuessBaseInfoApplicationService.peopleDetail(req);
    }

    @Override
    public R<Void> calculate(KpiProjGuessCalculateREQ req) {
        return kpiProjGuessBaseInfoApplicationService.calculate(req);
    }

    @Override
    public R<List<KpiProjGuessProjManageIndexRSP>> projManager(KpiProjGuessProjManagerREQ req) {
        return kpiProjGuessBaseInfoApplicationService.projManager(req);
    }

    @Override
    public R<List<KpiProjGuessProjManageDetailRSP>> projManagerDetail(KpiProjGuessProjManagerDetailREQ req) {
        return kpiProjGuessBaseInfoApplicationService.projManagerDetail(req);
    }

    @Override
    public R<List<KpiProjGuessProjManageCompletionIndexRSP>> projManagerCompletion(KpiProjGuessProjManagerREQ req) {
        return kpiProjGuessBaseInfoApplicationService.projManagerCompletion(req);
    }

    @Override
    public R<List<KpiProjGuessProjManagerCompletionDetailRSP>> projManagerCompletionDetail(KpiProjGuessProjManagerDetailREQ req) {
        return kpiProjGuessBaseInfoApplicationService.projManagerCompletionDetail(req);
    }

    @Override
    public R<List<KpiProjGuessDeptPoolIndexRSP>> deptPool(KpiProjGuessDeptPoolREQ req) {
        return kpiProjGuessBaseInfoApplicationService.deptPool(req);
    }

    @Override
    public R<List<KpiProjGuessDeptPooleDetailRSP>> deptPoolDetail(KpiProjGuessDeptPoolREQ req) {
        return kpiProjGuessBaseInfoApplicationService.deptPoolDetail(req);
    }

}
