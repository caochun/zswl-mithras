package cn.zswltech.mithras.metric.service;

import org.springframework.web.multipart.MultipartFile;

public interface RiskMetricFactorReportFileStore {

    Long addReportFile(MultipartFile file, Long factorFileId);
}
