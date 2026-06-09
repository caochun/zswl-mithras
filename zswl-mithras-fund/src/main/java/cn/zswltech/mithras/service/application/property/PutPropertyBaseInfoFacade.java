package cn.zswltech.mithras.service.application.property;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.property.PutPopertyBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyRSP;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;
import cn.zswltech.mithras.funddirect.application.property.PutPropertyBaseInfoApplicationService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPropertyService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * @author ylzhang5
 * @description 投放资产
 * @date 20251213
 */
@Service
public class PutPropertyBaseInfoFacade implements PutPropertyBaseInfoApplicationService {

    @Resource
    private FundDirectFinancingPropertyService fundDirectFinancingPropertyService;
    public R<List<FundFinancingPropertyRSP>> fundList(FundFinancingPropertyListREQ req) {
        return R.ok(fundDirectFinancingPropertyService.list(req).getList());
    }

    public R<List<FundFinancingPropertyRSP>> directList(FundFinancingPropertyListREQ req) {
        return R.ok(fundDirectFinancingPropertyService.list(req).getList());
    }

    @Override
    public R<List<FundFinancingPropertyRSP>> putPropertyList(PutPropertyBaseInfoListREQ req) {
        return R.ok(fundDirectFinancingPropertyService.putPropertyList(req).getList());
    }

    @Override
    public void putPropertyListDownload(PutPropertyBaseInfoListREQ req, ServletOutputStream outputStream) {
        fundDirectFinancingPropertyService.putPropertyListDownload(req, outputStream);
    }
}
