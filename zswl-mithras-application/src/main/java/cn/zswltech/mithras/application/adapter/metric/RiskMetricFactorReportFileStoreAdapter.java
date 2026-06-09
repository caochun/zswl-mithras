package cn.zswltech.mithras.application.adapter.metric;

import cn.zswltech.mithras.metric.enums.RiskMetricMaterialsEnum;
import cn.zswltech.mithras.metric.service.RiskMetricFactorReportFileStore;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.enums.FileDownloadZipPathEnum.RISK_METRIC_FACTOR_FILE;

@Component
public class RiskMetricFactorReportFileStoreAdapter implements RiskMetricFactorReportFileStore {

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public Long addReportFile(MultipartFile file, Long factorFileId) {
        return materialsListService.add(file, factorFileId, RiskMetricMaterialsEnum.REPORT_FILE.name(),
                RISK_METRIC_FACTOR_FILE.name());
    }
}
