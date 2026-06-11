package cn.zswltech.mithras.contract.controller.incomesharing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.incomesharing.IncomeSharingApi;
import cn.zswltech.mithras.contract.application.incomesharing.IncomeSharingApplicationService;
import cn.zswltech.mithras.dto.incomesharing.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class IncomeSharingController implements IncomeSharingApi {

    @Resource
    private IncomeSharingApplicationService incomeSharingApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<IncomeSharingListRSP>> incomeSharingList(IncomeSharingListREQ req) {
        return incomeSharingApplicationService.incomeSharingList(req);
    }

    @Override
    public R<PageR<IncomeSharingRSP>> incomeSharingDetail(IncomeSharingDetailREQ req) {
        return incomeSharingApplicationService.incomeSharingDetail(req);
    }

    @Override
    public void incomeSharingDownload(IncomeSharingListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收入分摊表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            incomeSharingApplicationService.incomeSharingDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出收入分摊表发生未知异常", e);
            throw new MithrasException("导出收入分摊表发生未知异常");
        }
    }

    @Override
    public void incomeSharingDetailDownload(IncomeSharingDetailREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收入分摊明细页列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            incomeSharingApplicationService.incomeSharingDetailDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出收入分摊明细页列表发生未知异常", e);
            throw new MithrasException("导出收入分摊明细页列表发生未知异常");
        }
    }
}
