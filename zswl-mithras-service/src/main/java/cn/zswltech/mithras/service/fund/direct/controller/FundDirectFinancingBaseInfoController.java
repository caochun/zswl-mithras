package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
* @description 直接融资-详情信息
* @author zhaozhengkang
* @date 2023-06-17
*/
@RestController
public class FundDirectFinancingBaseInfoController implements FundDirectFinancingBaseInfoApi {

    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;

    @Override
    public R<Long> add(FundDirectFinancingBaseInfoAddREQ req) {
        return R.ok(fundDirectFinancingBaseInfoService.add(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> modify(FundDirectFinancingBaseInfoModifyREQ req) {
        fundDirectFinancingBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundDirectFinancingBaseInfoListRSP>> list(FundDirectFinancingBaseInfoListREQ req) {
        return R.ok(fundDirectFinancingBaseInfoService.list(req));
    }

    @Override
    public R<FundDirectFinancingBaseInfoListRSP> sum(FundDirectFinancingBaseInfoListREQ req) {
        return R.ok(fundDirectFinancingBaseInfoService.sum(req));
    }

    @Override
    public R<FundDirectFinancingBaseInfoDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return R.ok(fundDirectFinancingBaseInfoService.detail(req.getId()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> obsolete(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingBaseInfoService.obsolete(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> sync(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingBaseInfoService.sync(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> delete(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingBaseInfoService.delete(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> settle(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingBaseInfoService.settle(req);
        return R.ok();
    }

    @Override
    public void batchDownload(@Valid FundFinancingBatchDownloadREQ req){
        fundDirectFinancingBaseInfoService.batchDownload(req);
    }

    @Override
    public void download(@Valid FileDownloadREQ fileDownloadREQ){
        fundDirectFinancingBaseInfoService.download(fileDownloadREQ);
    }

}