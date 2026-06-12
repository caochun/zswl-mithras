package cn.zswltech.mithras.application.orchestration.facade.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.fund.persistence.model.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.persistence.model.FundGuaranteeInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.credit.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.application.orchestration.fund.FundGuaranteeAgencyService;
import cn.zswltech.mithras.application.orchestration.fund.FundGuaranteeInfoService;
import cn.zswltech.mithras.fund.application.credit.FundGuaranteeInfoApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Service
public class FundGuaranteeInfoFacade implements FundGuaranteeInfoApplicationService {

    @Resource
    private FundGuaranteeInfoService fundGuaranteeInfoService;
    @Resource
    private FundGuaranteeAgencyService guaranteeAgencyService;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Void> add(MultipartFile[] files, FundGuaranteeInfoAddREQ req) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可新增担保信息");
        }
        fundGuaranteeInfoService.add(files, req);
        return R.ok();
    }

    @Override
    public R<Void> modify(MultipartFile[] addFiles, FundGuaranteeInfoModifyREQ req) {
        // 权限校验放在sevice中
        fundGuaranteeInfoService.modify(addFiles, req);
        return R.ok();
    }

    @Override
    public R<PageR<FundGuaranteeInfoListRSP>> list(FundGuaranteeInfoListREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundGuaranteeInfoService.list(req));
    }

    @Override
    public R<FundGuaranteeInfoDetailRSP> detail(FundGuaranteeInfoDetailREQ req) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            return R.ok(null);
        }
        return R.ok(fundGuaranteeInfoService.detail(req.getId()));
    }

    @Override
    public R<Void> remove(FundGuaranteeInfoRemoveREQ req) {
        FundGuaranteeInfo guaranteeInfo = fundGuaranteeInfoService.getById(req.getIds().get(0));
        FundGuaranteeAgency guaranteeAgency = guaranteeAgencyService.getById(guaranteeInfo.getAgencyId());
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可删除担保信息！");
        }
        Integer detailCount = guaranteeDetailService.selectCountByAgencyId(guaranteeInfo.getAgencyId());
        if (detailCount > 0) {
            throw new MithrasException("该担保机构存在关联的授信记录，不可删除担保信息！");
        }
        req.setAgencyId(guaranteeAgency.getId());
        fundGuaranteeInfoService.remove(req);
        return R.ok();
    }

}
