package cn.zswltech.mithras.finance.interfaces.stampduty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.stampduty.StampDutyApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.stampduty.*;
import cn.zswltech.mithras.finance.application.stampduty.StampDutyApplicationService;
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
public class StampDutyController implements StampDutyApi {

    @Resource
    private StampDutyApplicationService stampDutyApplicationService;
    @Resource
    private HttpServletResponse response;

    @Override
    public R<StampDutyListRSP> listPage(StampDutyListREQ req) {
        return stampDutyApplicationService.listPage(req);
    }

    @Override
    public R<StampDutyDetailREQ> add(StampDutyDetailREQ req) {
        return stampDutyApplicationService.add(req);
    }

    @Override
    public R<Void> remove(StampDutyBatchREQ req) {
        return stampDutyApplicationService.remove(req);
    }

    @Override
    public R<Void> importExcel(StampDutyImportREQ stampDutyImportREQ) {
        return stampDutyApplicationService.importExcel(stampDutyImportREQ);
    }

    @Override
    public R<StampDutyListRSP> contracts(StampDutyContractREQ req) {
        return stampDutyApplicationService.contracts(req);
    }

    @Override
    public void export(StampDutyBatchREQ param) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("印花税管理台账" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            stampDutyApplicationService.export(param, response.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出台账管理表发生未知异常", e);
            throw new MithrasException("导出台账管理表发生未知异常");
        }
    }

    @Override
    public R<List<SelectRSP>> orgList(String name, Integer type) {
        return stampDutyApplicationService.orgList(name, type);
    }
}
