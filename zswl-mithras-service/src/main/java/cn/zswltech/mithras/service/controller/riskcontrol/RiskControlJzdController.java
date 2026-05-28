package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlJzdReportApi;
import cn.zswltech.mithras.api.riskcontrol.model.*;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlJzdReport;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlJzdReportService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.service.enums.riskcontrol.jzd.report.JzdReportStatus.NOT_REPORT;

/**
 * @author yibin
 */
@RestController
public class RiskControlJzdController implements RiskControlJzdReportApi {

    @Resource
    private RiskControlJzdReportService jzdReportService;

    @Override
    public R<Void> submit(JzdReportSubmitREQ req) {
        jzdReportService.submit(req.getDataMonth());
        return R.ok();
    }

    @Override
    public R<Void> addManually(JzdReportAddREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }
        RiskControlJzdReport riskControlJzdReport = BeanUtil.copyProperties(req, RiskControlJzdReport.class);
        riskControlJzdReport.setReportStatus(NOT_REPORT.name());
        jzdReportService.add(riskControlJzdReport);
        return R.ok();
    }

    @Override
    public R<Void> remove(JzdReportRemoveREQ req) {
        jzdReportService.removeById(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> modify(JzdReportModifyREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }
        RiskControlJzdReport riskControlJzdReport = BeanUtil.copyProperties(req, RiskControlJzdReport.class);
        riskControlJzdReport.setReportStatus(NOT_REPORT.name());
        jzdReportService.updateById(riskControlJzdReport);
        return R.ok();
    }

    @Override
    public R<PageR<JzdReportListRSP>> list(JzdReportListREQ req) {
        if (req.getDataMonth() != null) {
            req.setDataMonth(req.getDataMonth().withDayOfMonth(1));
        }

        Page<RiskControlJzdReport> data = jzdReportService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskControlJzdReport>lambdaQuery()
                        .like(isNotBlank(req.getBizType()), RiskControlJzdReport::getBizType, req.getBizType())
                        .eq(isNotNull(req.getDataMonth()), RiskControlJzdReport::getDataMonth, req.getDataMonth())
                        .like(isNotBlank(req.getClientName()), RiskControlJzdReport::getClientName, req.getClientName())
                        .eq(isNotBlank(req.getCreateType()), RiskControlJzdReport::getCreateType, req.getCreateType())
        );

        List<JzdReportListRSP> list = BeanUtil.copyToList(data.getRecords(), JzdReportListRSP.class);
        PageR<JzdReportListRSP> pageR = PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize());
        boolean matched = list.stream().anyMatch(e -> NOT_REPORT.name().equals(e.getReportStatus()));
        pageR.getOthers().put("reportStatus", matched);
        return R.ok(pageR);
    }
}
