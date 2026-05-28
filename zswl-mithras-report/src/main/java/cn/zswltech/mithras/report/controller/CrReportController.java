package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrReportApi;
import cn.zswltech.mithras.dto.report.BatchExportExcelREQ;
import cn.zswltech.mithras.dto.report.batch.BatchReportREQ;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 征信报送后门接口
 *
 * @author wangchuanhao
 * @date 2022/10/19 2:18 PM
 */
@RestController
public class CrReportController implements CrReportApi {

    @Resource
    private CrFacade crFacade;

    /**
     * 执行
     * @return
     */
    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> execute()  {
        //crFacade.handle(LocalDateTime.now());
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> submit(BatchReportREQ req) {
        crFacade.submit(req);
        return R.ok();
    }

    @Override
    public R<String> getBatchNumber() {
        return R.ok(crFacade.getBatchNumber());
    }

    @Override
    public R<Void> exportExcel(BatchExportExcelREQ req) {
        crFacade.exportExcel(req);
        return R.ok();
    }
}
