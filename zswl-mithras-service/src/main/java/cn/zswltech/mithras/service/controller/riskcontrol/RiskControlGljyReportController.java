package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlGljyReportApi;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.*;
import cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportLevel;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReport;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlGljyReportService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlRelatedClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.riskcontrol.report.gljy.GljyReportStatus.REPORTED;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author yibin
 */
@RestController
@Slf4j
public class RiskControlGljyReportController implements RiskControlGljyReportApi {

    @Resource
    private RiskControlGljyReportService gljyReportService;
    @Resource
    private RiskControlRelatedClientService relatedClientService;

    @Override
    public R<Void> addManually(GljyReportAddREQ req) {
        err(req.getLevel().equals(GljyReportLevel.IMPORTANT.name()) && isBlank(req.getImportantReason()),
                "重大原因不能为空");
        gljyReportService.addManually(copyProperties(req, RiskControlGljyReport.class));
        return R.ok();
    }

    @Override
    public R<Void> remove(GljyReportRemoveREQ req) {
        RiskControlGljyReport report = gljyReportService.getById(req.getId());
        if (isNotNull(report) && REPORTED.name().equals(report.getReportStatus())) {
            err("记录已报送，不能删除");
        }
        gljyReportService.removeById(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> modify(GljyReportModifyREQ req) {
        err(req.getLevel().equals(GljyReportLevel.IMPORTANT.name()) && isBlank(req.getImportantReason()),
                "重大原因不能为空");
        if (req.getLevel().equals(GljyReportLevel.NORMAL.name())) {
            req.setImportantReason("");
        }
        RiskControlGljyReport report = gljyReportService.getById(req.getId());
        if (isNotNull(report) && REPORTED.name().equals(report.getReportStatus())) {
            err("记录已报送，不能修改");
        }
        gljyReportService.updateById(copyProperties(req, RiskControlGljyReport.class));
        return R.ok();
    }

    @Override
    public R<PageR<GljyReportListRSP>> list(GljyReportListREQ req) {
        Page<RiskControlGljyReport> data = gljyReportService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<RiskControlGljyReport>lambdaQuery()
                        .like(isNotBlank(req.getLevel()), RiskControlGljyReport::getLevel, req.getLevel())
                        .ge(isNotNull(req.getAmountFrom()), RiskControlGljyReport::getAmount, req.getAmountFrom())
                        .le(isNotNull(req.getAmountTo()), RiskControlGljyReport::getAmount, req.getAmountTo())
                        .eq(isNotBlank(req.getReportStatus()), RiskControlGljyReport::getReportStatus, req.getReportStatus())
                        .like(isNotBlank(req.getTradePartyName()), RiskControlGljyReport::getTradePartyName, req.getTradePartyName())
                        .ge(isNotNull(req.getTradeDateFrom()), RiskControlGljyReport::getTradeDate, req.getTradeDateFrom())
                        .le(isNotNull(req.getTradeDateTo()), RiskControlGljyReport::getTradeDate, req.getTradeDateTo())
                        .orderByAsc(RiskControlGljyReport::getReportStatus)
                        .orderByDesc(RiskControlGljyReport::getTradeDate)
        );

        List<GljyReportListRSP> list = BeanUtil.copyToList(data.getRecords(), GljyReportListRSP.class);
        PageR<GljyReportListRSP> pageR = PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize());
        return R.ok(pageR);
    }

    @Override
    public R<Void> submit(GljyReportSubmitREQ req) {
        gljyReportService.submit(req.getIdList());
        return R.ok();
    }

    @Override
    public R<List<String>> relatedClientList(GljyReportRelatedClientREQ req) {
        Page<RiskControlRelatedClient> page = relatedClientService.page(
                new Page<>(1, 50),
                Wrappers.<RiskControlRelatedClient>lambdaQuery()
                        .like(isNotBlank(req.getName()), RiskControlRelatedClient::getClientName, req.getName())
        );
        return R.ok(
                page.getRecords().stream().map(RiskControlRelatedClient::getClientName)
                        .collect(Collectors.toList()));
    }
}
