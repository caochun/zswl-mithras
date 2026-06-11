package cn.zswltech.mithras.leaseholdproperty.application;


import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemAppraisalRelation;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface LeaseAppraisalService {

    List<LeaseAppraisalItemListRSP> appraisalLeaseList(LeaseAppraisalItemListREQ req);

    Long appraisalAdd(LeaseAppraisalAddREQ req);

    List<LeaseAppraisalQueryCompanyRSP> appraisalQueryCompany(LeaseAppraisalQueryCompanyREQ req);

    LeaseAppraisalDetailRSP detail(LeaseAppraisalDetailREQ req);

    PageR<LeaseAppraisalCompanyListRSP> appraisalCompanyList(LeaseAppraisalCompanyListREQ req);

    void appraisalLasted(LeaseAppraisalLastedREQ req);

    void relation(LeaseAppraisalRelationREQ req);

    List<LeaseItemAppraisalRelation> queryLastestListByContractId(@NotNull Long contractId);
}
