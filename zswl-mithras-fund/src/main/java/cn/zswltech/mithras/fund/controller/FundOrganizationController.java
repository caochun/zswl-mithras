package cn.zswltech.mithras.fund.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundOrganizationApi;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.fund.application.FundOrganizationApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundOrganizationController implements FundOrganizationApi {
    @Resource
    private FundOrganizationApplicationService fundOrganizationApplicationService;

    @Override
    public R<Void> add(FundOrganizationAddREQ req) {
        return fundOrganizationApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundOrganizationModifyREQ req) {
        return fundOrganizationApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundOrganizationListRSP>> list(FundOrganizationListREQ req) {
        return fundOrganizationApplicationService.list(req);
    }

    @Override
    public R<FundOrganizationDetailRSP> detail(FundOrganizationDetailREQ req) {
        return fundOrganizationApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundOrganizationRemoveREQ req) {
        return fundOrganizationApplicationService.remove(req);
    }

    @Override
    public R<FundOrganizationCommonRSP> getInstitutionCode(FundOrganizationCommonREQ req) {
        return fundOrganizationApplicationService.getInstitutionCode(req);
    }

    @Override
    public R<List<String>> listDistinctAbbreviation(FundOrganizationCommonREQ req) {
        return fundOrganizationApplicationService.listDistinctAbbreviation(req);
    }
}
