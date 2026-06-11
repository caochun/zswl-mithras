package cn.zswltech.mithras.monthly.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.monthly.MonthlyManageApi;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.monthly.application.MonthlyManageApplicationService;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
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
public class MonthlyManageController implements MonthlyManageApi {

    @Resource
    private MonthlyManageApplicationService monthlyManageApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<MonthlyListRSP>> listPage(MonthlyListREQ req) {
        return monthlyManageApplicationService.listPage(req);
    }

    @Override
    public R<Long> addBaseInfo(MonthlyAddREQ req) {
        return monthlyManageApplicationService.addBaseInfo(req);
    }

    @Override
    public R<PageR<MonthlyAIRListRSP>> airPage(MonthlyAIRListREQ req) {
        return monthlyManageApplicationService.airPage(req);
    }

    @Override
    public R<PageR<MonthlyRPListRSP>> rpPage(MonthlyRPListREQ req) {
        return monthlyManageApplicationService.rpPage(req);
    }

    @Override
    public R<PageR<MonthlyStampDutyProjRSP>> projPage(MonthlyStampDutyProjREQ req) {
        return monthlyManageApplicationService.projPage(req);
    }

    @Override
    public R<PageR<MonthlyStampDutyFinRSP>> finPage(MonthlyStampDutyFinREQ req) {
        return monthlyManageApplicationService.finPage(req);
    }

    @Override
    public void download(MonthlyExcelREQ req) {
        MonthlyModuleTypeEnum monthlyModuleTypeEnum = MonthlyModuleTypeEnum.find(req.getModuleType());
        if (monthlyModuleTypeEnum == null) {
            throw new MithrasException("此类型不存在");
        }
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(monthlyModuleTypeEnum.getDisplay() + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            monthlyManageApplicationService.download(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出月结管理数据发生未知异常", e);
            throw new MithrasException("导出月结管理数据发生未知异常");
        }
    }

    @Override
    public R<PageR<MonthlyCostRSP>> costPage(MonthlyCostREQ req) {
        return monthlyManageApplicationService.costPage(req);
    }

    @Override
    public R<Void> submit(MonthlySubmitREQ req) {
        return monthlyManageApplicationService.submit(req);
    }

    @Override
    public R<Void> freshById(MonthlyFreshDataREQ req) {
        return monthlyManageApplicationService.freshById(req);
    }

    @Override
    public R<Void> close(MonthlyCloseREQ req) {
        return monthlyManageApplicationService.close(req);
    }

    @Override
    public R<Void> updateStatus(MonthlyUpdateStatusREQ req) {
        return monthlyManageApplicationService.updateStatus(req);
    }

    @Override
    public R<Void> closeValidate(MonthlyCloseREQ req) {
        return monthlyManageApplicationService.closeValidate(req);
    }

    @Override
    public R<Void> updateSingle(MonthlyFreshSingleREQ req) {
        return monthlyManageApplicationService.updateSingle(req);
    }

    @Override
    public R<Void> pushSingle(MonthlySubmitSingleREQ req) {
        return monthlyManageApplicationService.pushSingle(req);
    }
}
