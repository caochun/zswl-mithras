package cn.zswltech.mithras.service.controller.monthly;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.monthly.MonthlyManageApi;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.monthly.MonthlyManageApiService;
import liquibase.pro.packaged.L;
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
    private MonthlyManageApiService monthlyManageApiService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<MonthlyListRSP>> listPage(MonthlyListREQ req) {
        return R.ok(monthlyManageApiService.listPage(req));
    }

    @Override
    public R<Long> addBaseInfo(MonthlyAddREQ req) {
        return R.ok(monthlyManageApiService.addBaseInfo(req));
    }

    @Override
    public R<PageR<MonthlyAIRListRSP>> airPage(MonthlyAIRListREQ req) {
        return R.ok(monthlyManageApiService.airList(req));
    }

    @Override
    public R<PageR<MonthlyRPListRSP>> rpPage(MonthlyRPListREQ req) {
        return R.ok(monthlyManageApiService.rpList(req));
    }

    @Override
    public R<PageR<MonthlyStampDutyProjRSP>> projPage(MonthlyStampDutyProjREQ req) {
        return R.ok(monthlyManageApiService.projPage(req));
    }

    @Override
    public R<PageR<MonthlyStampDutyFinRSP>> finPage(MonthlyStampDutyFinREQ req) {
        return R.ok(monthlyManageApiService.finPage(req));
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
            monthlyManageApiService.download(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出月结管理数据发生未知异常", e);
            throw new MithrasException("导出月结管理数据发生未知异常");
        }
    }

    @Override
    public R<PageR<MonthlyCostRSP>> costPage(MonthlyCostREQ req) {
        return R.ok(monthlyManageApiService.costList(req));
    }

//    @Override
//    public R<Void> validate(MonthlyCostValidateREQ req) {
//        monthlyManageApiService.validate(req);
//        return R.ok();
//    }

    @Override
    public R<Void> submit(MonthlySubmitREQ req) {
        monthlyManageApiService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> freshById(MonthlyFreshDataREQ req) {
        monthlyManageApiService.freshById(req);
        return R.ok();
    }

    @Override
    public R<Void> close(MonthlyCloseREQ req) {
        monthlyManageApiService.close(req);
        return R.ok();
    }

    @Override
    public R<Void> updateStatus(MonthlyUpdateStatusREQ req) {
        monthlyManageApiService.updateStatus(req);
        return R.ok();
    }

    @Override
    public R<Void> closeValidate(MonthlyCloseREQ req) {
        monthlyManageApiService.closeValidate(req);
        return R.ok();
    }

    @Override
    public R<Void> updateSingle(MonthlyFreshSingleREQ req) {
        monthlyManageApiService.updateSingle(req);
        return R.ok();
    }

    @Override
    public R<Void> pushSingle(MonthlySubmitSingleREQ req) {
        monthlyManageApiService.pushSingle(req);
        return R.ok();
    }
}
