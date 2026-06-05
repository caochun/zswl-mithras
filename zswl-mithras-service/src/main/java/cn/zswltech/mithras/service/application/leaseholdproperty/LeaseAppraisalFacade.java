package cn.zswltech.mithras.service.application.leaseholdproperty;

import cn.hutool.db.Page;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseAppraisalApplicationService;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseAppraisalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class LeaseAppraisalFacade implements LeaseAppraisalApplicationService {

    @Resource
    private LeaseAppraisalService leaseAppraisalService;

    @Override
    public R<List<LeaseAppraisalItemListRSP>> appraisalLeaseList(LeaseAppraisalItemListREQ req) {
        return R.ok(leaseAppraisalService.appraisalLeaseList(req));
    }

    @Override
    public R<PageR<LeaseAppraisalCompanyListRSP>> appraisalCompanyList(LeaseAppraisalCompanyListREQ req) {
        return R.ok(leaseAppraisalService.appraisalCompanyList(req));
    }

    @Override
    public R<Long> appraisalAdd(LeaseAppraisalAddREQ req) {
        return R.ok(leaseAppraisalService.appraisalAdd(req));
    }

    @Override
    public R<List<LeaseAppraisalQueryCompanyRSP>> appraisalQueryCompany(LeaseAppraisalQueryCompanyREQ req) {
        return R.ok(leaseAppraisalService.appraisalQueryCompany(req));
    }

    @Override
    public R<LeaseAppraisalDetailRSP> detail(LeaseAppraisalDetailREQ req) {
        return R.ok(leaseAppraisalService.detail(req));
    }

    @Override
    public R<Void> appraisalLasted(LeaseAppraisalLastedREQ req) {
        leaseAppraisalService.appraisalLasted(req);
        return R.ok();
    }

    @Override
    public R<Void> relation(LeaseAppraisalRelationREQ req) {
        leaseAppraisalService.relation(req);
        return R.ok();
    }
}
