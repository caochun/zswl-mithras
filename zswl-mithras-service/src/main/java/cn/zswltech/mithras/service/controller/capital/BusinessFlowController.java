package cn.zswltech.mithras.service.controller.capital;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.api.capital.BusinessFlowApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.payment.PaymentMethod;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.capital.BusinessFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/20/19:24
 * @description
 */
@Slf4j
@RestController
public class BusinessFlowController implements BusinessFlowApi {

    @Resource
    private BusinessFlowService businessFlowService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<BusinessFlowFinanceListRSP>> selectList(BusinessFlowFinanceListREQ req) {
        return R.ok(businessFlowService.selectList(req));
    }

    @Override
    public void exportExcel(BusinessFlowFinanceListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("业务流水资金端流水" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            businessFlowService.exportExcel(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出质押明细表发生未知异常", e);
            throw new MithrasException("导出质押明细表发生未知异常");
        }
    }

    @Override
    public R<List<BusinessFlowFinanceDetailListRSP>> detailList(@Valid BusinessFlowFinanceDetailListREQ req) {
        return R.ok(businessFlowService.detailList(req));
    }

    @Override
    public R<Void> saveDetail(@Valid BusinessFlowFinanceDetailSaveREQ req) {
        // 判断结算方式
        if (!CharSequenceUtil.equalsAny(req.getSettleMethod(), PaymentMethod.PJ.name(), PaymentMethod.XYZ.name())) {
            throw new MithrasException("请前往【银行流水】页面进行银行流水认领");
        }
        businessFlowService.detailSave(req);
        return R.ok();
    }

    @Override
    public R<Void> manualPush(@Valid BusinessFlowFinanceManualPushREQ req) {
        businessFlowService.manualPushRepay(req.getCashFlowCodeList());
        return R.ok();
    }
}
