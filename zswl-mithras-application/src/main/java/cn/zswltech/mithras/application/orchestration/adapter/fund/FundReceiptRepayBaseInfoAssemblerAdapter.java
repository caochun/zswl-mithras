package cn.zswltech.mithras.application.orchestration.adapter.fund;

import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.fund.versioning.receiptrepay.FundReceiptRepayBaseInfoAssembler;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptRepayConverter;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FundReceiptRepayBaseInfoAssemblerAdapter implements FundReceiptRepayBaseInfoAssembler {

    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundReceiptRepayConverter fundReceiptRepayConverter;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;

    @Override
    public FundReceiptRepayBaseInfoDetailRSP lib2Rsp(FundReceiptRepayBaseInfo baseInfo,
                                                     FundReceiptRepayBaseInfoLib lib) {
        SingleFinancingIdREQ req = new SingleFinancingIdREQ();
        req.setFinancingId(lib.getFinancingId());
        req.setVersion(lib.getFinancingVersion());
        if ("DIRECT".equals(lib.getFinancingType())) {
            FundDirectFinancingBaseInfoDetailRSP directFinancingBaseInfo =
                    fundDirectFinancingBaseInfoService.detail(req.getFinancingId());
            return fundReceiptRepayConverter.joinDirectDetailRsp(baseInfo, directFinancingBaseInfo);
        }

        FundFinancingBaseInfoDetailRSP financingBaseInfo = fundFinancingBaseInfoService.detail(req);
        FundFinancingPlanDetailRSP financingPlan = fundFinancingPlanService.detail(req);
        FundReceiptRepayBaseInfoDetailRSP rsp =
                fundReceiptRepayConverter.joinDetailRsp(baseInfo, financingBaseInfo, financingPlan);
        List<FundOrganization> organizations = organizationService.getByFinancingId(lib.getFinancingId());
        rsp.setFinancingOrgName(organizations.stream()
                .map(FundOrganization::getOrganizationName)
                .collect(Collectors.toList()));
        return rsp;
    }
}
