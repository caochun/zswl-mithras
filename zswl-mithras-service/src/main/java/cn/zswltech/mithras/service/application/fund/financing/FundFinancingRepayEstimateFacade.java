package cn.zswltech.mithras.service.application.fund.financing;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingRepayEstimateApplicationService;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayImportREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayEstimateService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Slf4j
@Service
public class FundFinancingRepayEstimateFacade implements FundFinancingRepayEstimateApplicationService {
    @Resource
    private FundFinancingRepayEstimateService financingRepayEstimateService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<List<FundFinancingRepayEstimateListRSP>> list(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingRepayEstimateService.list(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING)
    @Override
    public R<FundFinancingRepayActualImportRSP> importExcel(@Valid FundFinancingRepayImportREQ req) {
        Assert.isTrue(!req.getFile().isEmpty(), () -> MithrasException.newException("文件不能为空"));
        try {
            FundFinancingRepayActualImportRSP rsp = financingRepayEstimateService.importExcel(req.getFinancingId(), req.getFile().getInputStream(), req.getIsCheck());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入还款概算表发生未知异常", e);
            return R.fail("导入还款概算表发生未知异常");
        }
    }

    @Override
    public void exportExcel(@Valid SingleFinancingIdREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("还款概算表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            financingRepayEstimateService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        }  catch (Exception e) {
            log.error("导出还款概算表发生未知异常", e);
            throw new MithrasException("导出还款概算表发生未知异常");
        }
    }
}
