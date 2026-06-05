package cn.zswltech.mithras.leaseholdproperty.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.LeaseAppraisalApi;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseAppraisalApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LeaseAppraisalController implements LeaseAppraisalApi {
    @Resource
    private LeaseAppraisalApplicationService leaseAppraisalApplicationService;

    @Override
    public R<List<LeaseAppraisalItemListRSP>> appraisalLeaseList(LeaseAppraisalItemListREQ req) {
        return leaseAppraisalApplicationService.appraisalLeaseList(req);
    }

    @Override
    public R<PageR<LeaseAppraisalCompanyListRSP>> appraisalCompanyList(LeaseAppraisalCompanyListREQ req) {
        return leaseAppraisalApplicationService.appraisalCompanyList(req);
    }

    @Override
    public R<Long> appraisalAdd(LeaseAppraisalAddREQ req) {
        return leaseAppraisalApplicationService.appraisalAdd(req);
    }

    @Override
    public R<List<LeaseAppraisalQueryCompanyRSP>> appraisalQueryCompany(LeaseAppraisalQueryCompanyREQ req) {
        return leaseAppraisalApplicationService.appraisalQueryCompany(req);
    }

    @Override
    public R<LeaseAppraisalDetailRSP> detail(LeaseAppraisalDetailREQ req) {
        return leaseAppraisalApplicationService.detail(req);
    }

    @Override
    public R<Void> appraisalLasted(LeaseAppraisalLastedREQ req) {
        return leaseAppraisalApplicationService.appraisalLasted(req);
    }

    @Override
    public R<Void> relation(LeaseAppraisalRelationREQ req) {
        return leaseAppraisalApplicationService.relation(req);
    }
}
