package cn.zswltech.mithras.service.controller.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjGuessBaseInfoApi;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.service.kpi.service.KpiProjGuessCalculateService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessBaseInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 绩效-项目测算表
* @author jackerhe
* @date 2023-06-15
*/
@RestController
public class KpiProjGuessBaseInfoController implements KpiProjGuessBaseInfoApi {

    @Resource
    private KpiProjGuessBaseInfoService kpiProjGuessBaseInfoService;
    @Resource
    private KpiProjGuessCalculateService kpiProjGuessCalculateService;


    @Override
    public R<PageR<KpiProjGuessContractIndexRSP>> contractList(@Valid KpiProjGuessIndexREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.contractList(req));
    }

    @Override
    public R<List<KpiProjGuessContractIndexRSP>> timeList(@Valid KpiProjGuessIndexREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.timeList(req));
    }

    @Override
    public R<List<KpiProjGuessContractIndexRSP>> deptList(@Valid KpiProjGuessIndexREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.deptList(req));
    }

    @Override
    public R<List<KpiProjGuessPeopleIndexRSP>> peopleList(@Valid KpiProjGuessIndexREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.peopleList(req));
    }

    @Override
    public R<PageR<KpiProjGuessDetailRSP>> contractDetail(@Valid KpiProjGuessDetailREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.contractDetail(req));
    }


    @Override
    public R<PageR<KpiProjGuessPeopleDetailRSP>> peopleDetail(@Valid KpiProjGuessDetailREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.peopleDetail(req));
    }

    @Override
    public R<Void> calculate(@Valid KpiProjGuessCalculateREQ req) {
        kpiProjGuessCalculateService.calculate(req.getContractIds(), req.getCalculateDate(), true);
        return R.ok();
    }

    @Override
    public R<List<KpiProjGuessProjManageIndexRSP>> projManager(@Valid KpiProjGuessProjManagerREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.projManager(req));
    }

    @Override
    public R<List<KpiProjGuessProjManageDetailRSP>> projManagerDetail(@Valid KpiProjGuessProjManagerDetailREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.projManagerDetail(req));
    }

    @Override
    public R<List<KpiProjGuessProjManageCompletionIndexRSP>> projManagerCompletion(@Valid KpiProjGuessProjManagerREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.projManagerCompletion(req));
    }

    @Override
    public R<List<KpiProjGuessProjManagerCompletionDetailRSP>> projManagerCompletionDetail(@Valid KpiProjGuessProjManagerDetailREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.projManagerCompletionDetail(req));
    }

    @Override
    public R<List<KpiProjGuessDeptPoolIndexRSP>> deptPool(@Valid KpiProjGuessDeptPoolREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.deptPool(req));
    }

    @Override
    public R<List<KpiProjGuessDeptPooleDetailRSP>> deptPoolDetail(@Valid KpiProjGuessDeptPoolREQ req) {
        return R.ok(kpiProjGuessBaseInfoService.deptPoolDetail(req));
    }


}