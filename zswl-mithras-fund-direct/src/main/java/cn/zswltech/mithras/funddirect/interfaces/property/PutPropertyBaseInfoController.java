package cn.zswltech.mithras.funddirect.interfaces.property;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.property.PutPopertyBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyRSP;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;
import cn.zswltech.mithras.funddirect.application.property.PutPropertyBaseInfoApplicationService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
public class PutPropertyBaseInfoController implements PutPopertyBaseInfoApi {

    @Resource
    private PutPropertyBaseInfoApplicationService putPropertyBaseInfoApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<List<FundFinancingPropertyRSP>> fundList(FundFinancingPropertyListREQ req) {
        return putPropertyBaseInfoApplicationService.fundList(req);
    }

    @Override
    public R<List<FundFinancingPropertyRSP>> directList(FundFinancingPropertyListREQ req) {
        return putPropertyBaseInfoApplicationService.directList(req);
    }

    @Override
    public R<List<FundFinancingPropertyRSP>> putPropertyList(PutPropertyBaseInfoListREQ req) {
        return putPropertyBaseInfoApplicationService.putPropertyList(req);
    }

    @Override
    public void putPropertyListDownload(PutPropertyBaseInfoListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资金管理-投放资产" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            putPropertyBaseInfoApplicationService.putPropertyListDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出投放资产发生未知异常", e);
            throw new MithrasException("导出投放资产发生未知异常");
        }
    }
}
