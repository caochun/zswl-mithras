package cn.zswltech.mithras.service.controller.leaseholdproperty;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.AppraisalCompanyWhitelistApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.leaseholdproperty.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.service.service.lib.appraisalcompanywhitelist.AppraisalCompanyWhitelistVersionService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/9/3
 * @description
 */
@RestController
public class AppraisalCompanyWhitelistController implements AppraisalCompanyWhitelistApi {
    @Resource
    private AppraisalCompanyWhitelistService appraisalCompanyWhitelistService;
    @Resource
    private AppraisalCompanyWhitelistVersionService appraisalCompanyWhitelistVersionService;

    @Override
    public R<PageR<AppraisalCompanyWhitelistPageRSP>> pageList(AppraisalCompanyWhitelistPageREQ req) {
        return R.ok(appraisalCompanyWhitelistService.pageList(req));
    }

    @Override
    public R<Long> add(AppraisalCompanyWhitelistAddREQ req) {
        return R.ok(appraisalCompanyWhitelistService.add(req));
    }

    @Override
    public R<AppraisalCompanyDetailRSP> detail(SinglePkREQ req) {
        return R.ok(appraisalCompanyWhitelistService.detail(req));
    }

    @Override
    public R<Void> refreshCommerce(SinglePkREQ req) {
        appraisalCompanyWhitelistService.refreshCommerce(req);
        return R.ok();
    }

    @Override
    public R<String> submit(SinglePkREQ req) {
        return R.ok(appraisalCompanyWhitelistService.submit(req));
    }

    @Override
    public R<String> submitOut(SinglePkREQ req) {
        return R.ok(appraisalCompanyWhitelistService.submitOut(req));
    }

    @Override
    public R<Void> deleteById(SinglePkREQ req) {
        appraisalCompanyWhitelistService.deleteById(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> cancel(SinglePkREQ req) {
        appraisalCompanyWhitelistVersionService.reset(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> saveOutReason(AppraisalCompanyWhitelistModifyREQ req) {
        if (StrUtil.isBlank(req.getOutReason())) {
            throw new MithrasException("出库原因不能为空");
        }
        appraisalCompanyWhitelistService.modify(req);
        return R.ok();
    }
}
