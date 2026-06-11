package cn.zswltech.mithras.collection.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contractcp.ContractCollectionPaymentApi;
import cn.zswltech.mithras.collection.application.facade.ContractCollectionPaymentApplicationService;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentDetailExportREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentDetailREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListRSP;
import cn.zswltech.mithras.dto.contractcp.ContractInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractRentActualInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractcpContractDetailREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
public class ContractCollectionPaymentController implements ContractCollectionPaymentApi {

    @Resource
    private ContractCollectionPaymentApplicationService contractCollectionPaymentApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<ContractCollectionPaymentListRSP>> list(@Valid ContractCollectionPaymentListREQ req) {
        return R.ok(contractCollectionPaymentApplicationService.list(req));
    }

    @Override
    public void exportList(ContractCollectionPaymentListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("合同收付款列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractCollectionPaymentApplicationService.exportList(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同收付款列表发生未知异常", e);
            throw new MithrasException("导出合同收付款列表发生未知异常");
        }
    }

    @Override
    public R<Void> pushRentNotify(ContractCollectionPaymentListREQ req) {
        return contractCollectionPaymentApplicationService.pushRentNotify(req);
    }

    @Override
    public R<List<SelectRSP>> contractList(@Valid ContractcpContractDetailREQ req) {
        return R.ok(contractCollectionPaymentApplicationService.contractList(req));
    }

    @Override
    public R<ContractInfoRSP> contractDetail(@Valid ContractcpContractDetailREQ req) {
        return R.ok(contractCollectionPaymentApplicationService.contractDetail(req));
    }

    @Override
    public R<List<SelectRSP>> cashList(@Valid ContractcpContractDetailREQ req) {
        return R.ok(contractCollectionPaymentApplicationService.cashList(req));
    }

    @Override
    public R<PageR<ContractRentActualInfoRSP>> cashDetail(@Valid ContractCollectionPaymentDetailREQ req) {
        return R.ok(contractCollectionPaymentApplicationService.cashDetail(req));
    }

    @Override
    public R<Void> exportCashDetail(ContractCollectionPaymentDetailExportREQ req) {
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractCollectionPaymentApplicationService.exportCashDetail(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流明细发生未知异常", e);
            return R.fail("导出现金流明细发生未知异常");
        }
    }
}
