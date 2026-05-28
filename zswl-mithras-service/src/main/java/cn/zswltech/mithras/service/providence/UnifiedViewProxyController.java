package cn.zswltech.mithras.service.providence;

import cn.zswltech.mithras.factory.feign.ProvidenceCustomerUnifiedViewApiClient;
import cn.zswltech.mithras.service.service.client.ClientUnifiedViewService;
import com.zswltec.providence.api.CustomerUnifiedViewApi;
import com.zswltec.providence.dto.*;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import com.zswltec.providence.dto.req.*;
import com.zswltec.providence.dto.rsp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 18:39
 */
@RestController
@Slf4j
public class UnifiedViewProxyController implements CustomerUnifiedViewApi {

    @Resource
    private ProvidenceCustomerUnifiedViewApiClient providenceCustomerUnifiedViewApiClient;
    @Resource
    private ClientUnifiedViewService clientUnifiedViewService;
    @Override
    public R<CustomerViewDetailAreaEconomyRsp> areaEconomy(CustomerViewDetailAreaEconomyReq req) {
        return providenceCustomerUnifiedViewApiClient.areaEconomy(req);
    }

    @Override
    public R<CustomerViewDetailCtzReginEconomyRsp> ctzReginEconomy(CustomerViewDetailCtzReginEconomyReq req) {
        return providenceCustomerUnifiedViewApiClient.ctzReginEconomy(req);
    }

    @Override
    public R<PageR<CustomerViewInfoRsp>> queryCustomerViewInfo(CustomerViewInfoReq req) {
        return providenceCustomerUnifiedViewApiClient.queryCustomerViewInfo(req);
    }

    @Override
    public R<CustomerViewCompanyBasicInfoRsp> queryCompanyBasicInfo(CustomerViewCompanyBasicInfoReq req) {
        return providenceCustomerUnifiedViewApiClient.queryCompanyBasicInfo(req);
    }

    @Override
    public R<List<StockHolder>> queryStockHolders(StockHolderReq req) {
        return providenceCustomerUnifiedViewApiClient.queryStockHolders(req);
    }

    @Override
    public R<List<BondBasicInfoRsp>> queryBondBasicInfo(BondBasicInfoReq req) {
        return providenceCustomerUnifiedViewApiClient.queryBondBasicInfo(req);
    }

    @Override
    public R<CustomerViewBizRiskRsp> queryBizRisk(CustomerViewBizRiskReq req) {
        return providenceCustomerUnifiedViewApiClient.queryBizRisk(req);
    }

    @Override
    public R<PageR<OpiInfoRsp>> opiInfo(OpiInfoReq req) {
        return providenceCustomerUnifiedViewApiClient.opiInfo(req);
    }

    @Override
    public R<OpionStatistcRsp> opiStatistc(OpiInfoReq req) {
        return providenceCustomerUnifiedViewApiClient.opiStatistc(req);
    }

    @Override
    public R<MajorTaxViolationsDetailRsp> queryMajorTaxViolationsDetail(MajorTaxViolationsDetailReq req) {
        return providenceCustomerUnifiedViewApiClient.queryMajorTaxViolationsDetail(req);
    }

    @Override
    public R<GuaranteeEventDetailRsp> queryGuaranteeEventDetail(GuaranteeEventDetailReq req) {
        return providenceCustomerUnifiedViewApiClient.queryGuaranteeEventDetail(req);
    }

    @Override
    public R<ChattelMortageDetailRsp> queryChattelMortageDetail(ChattelMortageDetailReq req) {
        return providenceCustomerUnifiedViewApiClient.queryChattelMortageDetail(req);
    }

    @Override
    public R<CustomerViewLitigationRsp> queryLitigation(CustomerViewLitigationReq req) {
        return providenceCustomerUnifiedViewApiClient.queryLitigation(req);
    }

    @Override
    public R<JudicialAssistanceRsp> judicialAssistanceDetail(JudicialAssistanceReq req) {
        return providenceCustomerUnifiedViewApiClient.judicialAssistanceDetail(req);
    }

    @Override
    public R<JudgeDocDetailRsp> judgementDocumentDetail(JudgeDocDetailReq req) {
        return providenceCustomerUnifiedViewApiClient.judgementDocumentDetail(req);
    }

    @Override
    public R<LimitHighConsumeRsp> limitHighConsumeDetail(LimitHighConsumeReq req) {
        return providenceCustomerUnifiedViewApiClient.limitHighConsumeDetail(req);
    }

    @Override
    public R<List<OuterRatingRsp>> outerRating(OuterRatingReq req) {
        return providenceCustomerUnifiedViewApiClient.outerRating(req);
    }

    @Override
    public R<HeadBodyRsp> headBody(HeadBodyReq req) {
        R<HeadBodyRsp> headBodyRspR = providenceCustomerUnifiedViewApiClient.headBody(req);
        HeadBodyRsp data = headBodyRspR.getData();
        clientUnifiedViewService.headBodySupplementOther(data, req);
        return headBodyRspR;
    }

    @Override
    public R<AreaRatingRsp> areaRating(AreaRatingReq req) {
        return providenceCustomerUnifiedViewApiClient.areaRating(req);
    }

    @Override
    public R dailyRiskPoint(DailyRiskPointReq req) {
        return providenceCustomerUnifiedViewApiClient.dailyRiskPoint(req);
    }
}
