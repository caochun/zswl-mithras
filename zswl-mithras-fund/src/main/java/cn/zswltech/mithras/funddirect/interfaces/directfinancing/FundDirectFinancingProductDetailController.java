package cn.zswltech.mithras.funddirect.interfaces.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingProductDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingProductDetailApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundDirectFinancingProductDetailController implements FundDirectFinancingProductDetailApi {

    @Resource
    private FundDirectFinancingProductDetailApplicationService fundDirectFinancingProductDetailApplicationService;

    @Override
    public R<Void> add(FundDirectFinancingProductDetailAddREQ req) {
        return fundDirectFinancingProductDetailApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingProductDetailModifyREQ req) {
        return fundDirectFinancingProductDetailApplicationService.modify(req);
    }

    @Override
    public R<FundDirectFinancingProductDetailListRSP> list(FundDirectFinancingProductDetailListREQ req) {
        return fundDirectFinancingProductDetailApplicationService.list(req);
    }

    @Override
    public R<FundDirectFinancingProductDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingProductDetailApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingProductDetailApplicationService.remove(req);
    }

    @Override
    public void exportExcel(FundDirectFinancingProductDetailListREQ req) {
        fundDirectFinancingProductDetailApplicationService.exportExcel(req);
    }

    @Override
    public R<List<ProductSelectRsp>> select(FundDirectFinancingProductDetailListREQ req) {
        return fundDirectFinancingProductDetailApplicationService.select(req);
    }
}
