package cn.zswltech.mithras.service.controller.incomeSharing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.incomeSharing.IncomeSharingApi;
import cn.zswltech.mithras.dto.incomeSharing.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yupengfei
 * @date 2024/6/7 19:19
 */
@Slf4j
@RestController
public class IncomeSharingController implements IncomeSharingApi {


    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<IncomeSharingListRSP>> incomeSharingList(IncomeSharingListREQ req) {
        return R.ok(contractIncomeSharingService.incomeSharingQuery(req));
    }

    @Override
    public R<PageR<IncomeSharingRSP>> incomeSharingDetail(IncomeSharingDetailREQ req) {
        return contractIncomeSharingService.incomeSharingDetail(req);
    }

    @Override
    public void incomeSharingDownload(IncomeSharingListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("收入分摊表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractIncomeSharingService.exportIncomeSharingList(req, httpServletResponse.getOutputStream());
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
            contractIncomeSharingService.exportIncomeSharingDetail(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出收入分摊明细页列表发生未知异常", e);
            throw new MithrasException("导出收入分摊明细页列表发生未知异常");
        }
    }
}
