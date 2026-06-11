package cn.zswltech.mithras.leaseholdproperty.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.AppraisalCompanyWhitelistApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class AppraisalCompanyWhitelistController implements AppraisalCompanyWhitelistApi {
    @Resource
    private AppraisalCompanyWhitelistApplicationService appraisalCompanyWhitelistApplicationService;

    @Override
    public R<PageR<AppraisalCompanyWhitelistPageRSP>> pageList(AppraisalCompanyWhitelistPageREQ req) {
        return appraisalCompanyWhitelistApplicationService.pageList(req);
    }

    @Override
    public R<Long> add(AppraisalCompanyWhitelistAddREQ req) {
        return appraisalCompanyWhitelistApplicationService.add(req);
    }

    @Override
    public R<AppraisalCompanyDetailRSP> detail(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.detail(req);
    }

    @Override
    public R<Void> refreshCommerce(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.refreshCommerce(req);
    }

    @Override
    public R<String> submit(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.submit(req);
    }

    @Override
    public R<String> submitOut(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.submitOut(req);
    }

    @Override
    public R<Void> deleteById(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.deleteById(req);
    }

    @Override
    public R<Void> cancel(SinglePkREQ req) {
        return appraisalCompanyWhitelistApplicationService.cancel(req);
    }

    @Override
    public R<Void> saveOutReason(AppraisalCompanyWhitelistModifyREQ req) {
        return appraisalCompanyWhitelistApplicationService.saveOutReason(req);
    }
}
