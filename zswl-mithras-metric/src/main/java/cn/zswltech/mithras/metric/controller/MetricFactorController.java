package cn.zswltech.mithras.metric.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.metric.MetricFactorApi;
import cn.zswltech.mithras.dto.metric.factor.*;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorFile;
import cn.zswltech.mithras.metric.service.RiskMetricFactorRefreshClient;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

/**
 * @author yibin
 * 报表导入
 */
@MapperScan(value = "cn.zswltech.mithras.metric.mapper")
@RestController
@Slf4j
public class MetricFactorController implements MetricFactorApi {

    @Resource
    private RiskMetricFactorService metricFactorService;
    @Resource
    private RiskMetricFactorRefreshClient refreshClient;

    @Override
    public R<PageR<RiskMetricFactorPageListRsp>> detailPageList(@Valid RiskMetricFactorPageListReq req) {
        return R.ok(metricFactorService.detailPageList(req));
    }

    @Override
    public R<Boolean> removeFile(RiskMetricFactorFileRemoveReq req) {
        metricFactorService.removeFile(req.getId());
        return R.ok(true);
    }

    @Override
    public R<PageR<RiskMetricFactorFileListRsp>> fileList(RiskMetricFactorFileListReq req) {
        Page<RiskMetricFactorFile> data = metricFactorService.fileList(req);
        List<RiskMetricFactorFile> records = data.getRecords();
        List<RiskMetricFactorFileListRsp> list = BeanUtil.copyToList(records, RiskMetricFactorFileListRsp.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> importFactors(MultipartFile file) {
        metricFactorService.importByFile(file);
        return R.ok();
    }

    @Override
    public R<PageR<RiskMetricFactorListRsp>> list(RiskMetricFactorListReq req) {
        Page<RiskMetricFactor> data = metricFactorService.list(req);
        List<RiskMetricFactor> records = data.getRecords();
        List<RiskMetricFactorListRsp> list = BeanUtil.copyToList(records, RiskMetricFactorListRsp.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> removeFactors(RiskMetricFactorImportRemoveReq req) {
        metricFactorService.remove(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, req.getFactorDate())
                .eq(isNotBlank(req.getFactorTable()), RiskMetricFactor::getFactorTable, req.getFactorTable())
        );
        return R.ok();
    }

    @Override
    @SneakyThrows
    public R<Void> refresh(RiskMetricFactorRefreshReq req) {
        LocalDate localDate = req.getDate().with(TemporalAdjusters.lastDayOfMonth());
        // 各个报表之间多线程处理，提高处理速度
        CompletableFuture<Boolean> assetCF = CompletableFuture.supplyAsync(() -> refreshClient.refreshAsset(localDate));
        CompletableFuture<Boolean> profitCF = CompletableFuture.supplyAsync(() -> refreshClient.refreshProfit(localDate));
        CompletableFuture<Boolean> cashFlowCF = CompletableFuture.supplyAsync(() -> refreshClient.refreshCashFlow(localDate));
        CompletableFuture<Boolean> subjectBalanceCF = CompletableFuture.supplyAsync(() -> refreshClient.refreshSubjectBalance(localDate));
        // 处理返回结果
        List<String> msgList = new LinkedList<>();
        if (Objects.nonNull(assetCF.get()) && !assetCF.get()) {
            msgList.add("资产负债表");
        }
        if (Objects.nonNull(profitCF.get()) && !profitCF.get()) {
            msgList.add("利润表");
        }
        if (Objects.nonNull(cashFlowCF.get()) && !cashFlowCF.get()) {
            msgList.add("现金流量表");
        }
        if (Objects.nonNull(subjectBalanceCF.get()) && !subjectBalanceCF.get()) {
            msgList.add("科目余额表");
        }
        if (CollectionUtil.isNotEmpty(msgList)) {
            String s = StrUtil.join("，", msgList) + "刷新数据失败";
            throw new MithrasException(s);
        }
        return R.ok();
    }

    @Override
    public void test() {
        refreshClient.testProfitSync();
    }
}
