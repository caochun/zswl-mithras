package cn.zswltech.mithras.leaseholdproperty.application.facade;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistApplicationService;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.leaseholdproperty.application.lib.appraisalcompanywhitelist.AppraisalCompanyWhitelistVersionService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/9/3
 * @description
 */
@Service
public class AppraisalCompanyWhitelistFacade implements AppraisalCompanyWhitelistApplicationService {
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
