package cn.zswltech.mithras.service.controller.fund;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundOrganizationApi;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@RestController
public class FundOrganizationController implements FundOrganizationApi {
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Void> add(FundOrganizationAddREQ req) {
        //权限检验
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能创建!");
        }
        // 必填项校验
        if ("BA".equals(req.getOrganizationType()) && ObjectUtil.isEmpty(req.getInterBankNo())) {
            throw new MithrasException("银行联号为必填项!");
        }
        if ("ZL".equals(req.getOrganizationType()) && ObjectUtil.isEmpty(req.getUscCode())) {
            throw new MithrasException("社会统一信用代码为必填项!");
        }
        if ("JT".equals(req.getOrganizationType()) && ObjectUtil.isEmpty(req.getUscCode())) {
            throw new MithrasException("社会统一信用代码为必填项!");
        }
        // 唯一性校验
        FundOrganization onlyOne = fundOrganizationService.getOne(Wrappers.<FundOrganization>lambdaQuery()
                .eq(FundOrganization::getOrganizationName, req.getOrganizationName()));
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该机构名称已存在!");
        }
        if (ObjectUtil.isNotEmpty(req.getInterBankNo())) {
            onlyOne = fundOrganizationService.getOne(Wrappers.<FundOrganization>lambdaQuery()
                    .eq(FundOrganization::getInterBankNo, req.getInterBankNo()));
        }
        if (ObjectUtil.isNotEmpty(req.getUscCode())) {
            onlyOne = fundOrganizationService.getOne(Wrappers.<FundOrganization>lambdaQuery()
                    .eq(FundOrganization::getUscCode, req.getUscCode()));
        }
        if (ObjectUtil.isNotEmpty(onlyOne)) {
            throw new MithrasException("该机构已存在!");
        }
        fundOrganizationService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(FundOrganizationModifyREQ req) {
        FundOrganization toBeModify = fundOrganizationService.getById(req.getId());
        if (ObjectUtil.isNull(toBeModify)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可操作编辑！");
        }
        fundOrganizationService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundOrganizationListRSP>> list(FundOrganizationListREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundOrganizationService.list(req));
    }

    @Override
    public R<FundOrganizationDetailRSP> detail(FundOrganizationDetailREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以查看机构信息!");
        }
        return R.ok(fundOrganizationService.detail(req.getId()));
    }

    @Override
    public R<Void> remove(FundOrganizationRemoveREQ req) {
        List<FundOrganization> toBeRemoves = fundOrganizationService.listByIds(req.getIds());
        toBeRemoves.forEach(base -> {
            if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
                throw new MithrasException("只有资金经理可操作删除！");
            }
        });
        Integer creditCount = fundCreditService.selectCountByOrgIds(req.getIds());
        if (creditCount > 0) {
            throw new MithrasException("该机构存在关联的授信记录，不可删除！");
        }
        fundOrganizationService.removeByIds(req.getIds());
        return R.ok();
    }

    @Override
    public R<FundOrganizationCommonRSP> getInstitutionCode(FundOrganizationCommonREQ req) {
        if (StringUtils.isBlank(req.getUscCode())) {
            throw new MithrasException("信用机构代码不能为空");
        }
        FundOrganizationCommonRSP rsp = fundOrganizationService.getInstitutionCode(req.getUscCode());
        return R.ok(rsp);
    }

    @Override
    public R<List<String>> listDistinctAbbreviation(FundOrganizationCommonREQ req) {
        return R.ok(fundOrganizationService.listDistinctAbbreviation(req.getAbbreviation()));
    }
}