package cn.zswltech.mithras.api.report;

import java.time.LocalDate;
import java.util.List;

public interface ReportAssetClassifyPort {

    List<ReportAssetClassifyClientSnapshot> findFinishedClientClassifySnapshots(LocalDate reportDate);
}
