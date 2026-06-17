package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingRepayActualApplicationService;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayImportREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
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
public class FundFinancingRepayActualFacade implements FundFinancingRepayActualApplicationService {
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<List<FundFinancingRepayActualListRSP>> list(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingRepayActualService.list(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<FundFinancingRepayActualImportRSP> importExcel(@Valid FundFinancingRepayImportREQ req) {
        Assert.isTrue(!req.getFile().isEmpty(), () -> MithrasException.newException("文件不能为空"));
        Assert.notBlank(req.getScene(), () -> MithrasException.newException("导入场景不能为空"));
        Assert.notNull(req.getIsCheck(), () -> MithrasException.newException("是否校验利息差额不能为空"));
        try {
            FundFinancingRepayActualImportRSP rsp = financingRepayActualService.importExcel(req.getFinancingId(), req.getFile().getInputStream(), req.getScene(), req.getIsCheck());
            return R.ok(rsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入实际还款表发生未知异常", e);
            return R.fail("导入实际还款表发生未知异常");
        }
    }

    @Override
    public void exportExcel(@Valid SingleFinancingIdREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("实际还款表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            financingRepayActualService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出实际还款表发生未知异常", e);
            throw new MithrasException("导出实际还款表发生未知异常");
        }
    }
}
