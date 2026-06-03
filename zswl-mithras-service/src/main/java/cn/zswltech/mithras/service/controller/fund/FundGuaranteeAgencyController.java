package cn.zswltech.mithras.service.controller.fund;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundGuaranteeAgencyApi;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundGuaranteeAgency;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.fund.application.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.service.service.fund.FundGuaranteeAgencyService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@RestController
public class FundGuaranteeAgencyController implements FundGuaranteeAgencyApi {

    @Resource
    private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private SysUserService sysUserService;


    @Override
    public R<Long> addAndSync(FundGuaranteeAgencyAddREQ req) {
        //权限检验
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能创建!");
        }
        // 唯一性校验
        FundGuaranteeAgency onlyOne = fundGuaranteeAgencyService.getOne(Wrappers.<FundGuaranteeAgency>lambdaQuery()
                .eq(FundGuaranteeAgency::getGuaranteeAgencyName, req.getGuaranteeAgencyName()));
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该担保机构已存在!");
        }
        if (req.getUscCode().length() != 18) {
            throw new MithrasException("统一社会信用码长度不合法，请重新输入!");
        }
        // 唯一性校验
        onlyOne = fundGuaranteeAgencyService.getOne(Wrappers.<FundGuaranteeAgency>lambdaQuery()
                .eq(FundGuaranteeAgency::getUscCode, req.getUscCode()));
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该担保机构已存在!");
        }
        return R.ok(fundGuaranteeAgencyService.addAndSync(req));
    }

    @Override
    public R<Long> addHalf(FundGuaranteeAgencyAddREQ req) {
        //权限检验
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能创建!");
        }
        // 唯一性校验
        FundGuaranteeAgency onlyOne = fundGuaranteeAgencyService.getOne(Wrappers.<FundGuaranteeAgency>lambdaQuery()
                .eq(FundGuaranteeAgency::getGuaranteeAgencyName, req.getGuaranteeAgencyName()));
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该担保机构已存在!");
        }
        if (req.getUscCode().length() != 18) {
            throw new MithrasException("统一社会信用码长度不合法，请重新输入!");
        }
        // 唯一性校验
        onlyOne = fundGuaranteeAgencyService.getOne(Wrappers.<FundGuaranteeAgency>lambdaQuery()
                .eq(FundGuaranteeAgency::getUscCode, req.getUscCode()));
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该担保机构已存在!");
        }
        return R.ok(fundGuaranteeAgencyService.addHalf(req));
    }

    @Override
    public R<Void> modify(FundGuaranteeAgencyModifyREQ req) {
        FundGuaranteeAgency toBeModify = fundGuaranteeAgencyService.getById(req.getId());
        if (ObjectUtil.isNull(toBeModify)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可编辑！");
        }
        fundGuaranteeAgencyService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundGuaranteeAgencyDetailRSP> sync(FundGuaranteeAgencySyncREQ req) {
        FundGuaranteeAgency toBeModify = fundGuaranteeAgencyService.getById(req.getId());
        if (ObjectUtil.isNull(toBeModify)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可操作同步！");
        }
        return R.ok(fundGuaranteeAgencyService.sync(req));
    }

    @Override
    public R<PageR<FundGuaranteeAgencyListRSP>> list(FundGuaranteeAgencyListREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundGuaranteeAgencyService.list(req));
    }

    @Override
    public R<List<FundGuaranteeAgencyListRSP>> pulldown(@Valid FundGuaranteeAgencyPullDownREQ req) {
        return R.ok(fundGuaranteeAgencyService.pulldown(req));
    }


    @Override
    public R<FundGuaranteeAgencyDetailRSP> detail(FundGuaranteeAgencyDetailREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以查看机构信息!");
        }
        return R.ok(fundGuaranteeAgencyService.detail(req.getId()));
    }

    @Override
    public R<Void> remove(FundGuaranteeAgencyRemoveREQ req) {
        List<FundGuaranteeAgency> toBeRemoves = fundGuaranteeAgencyService.listByIds(req.getIds());
        toBeRemoves.forEach(base -> {
            if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
                throw new MithrasException("只有资金经理可操作删除！");
            }
            Integer detailCount = guaranteeDetailService.selectCountByAgencyId(base.getId());
            if (detailCount > 0) {
                throw new MithrasException("该担保机构存在关联的授信记录，不可删除！");
            }
        });
        fundGuaranteeAgencyService.remove(req);
        return R.ok();
    }

    @Override
    public R<FundGuaranteeLimitDetailRSP> limitDetail(FundGuaranteeSingletonIdREQ req) {
        return R.ok(fundGuaranteeAgencyService.limitDetail(req));
    }

}