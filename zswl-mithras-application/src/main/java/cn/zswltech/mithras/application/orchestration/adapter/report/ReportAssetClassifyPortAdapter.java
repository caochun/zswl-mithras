package cn.zswltech.mithras.application.orchestration.adapter.report;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.api.report.ReportAssetClassifyClientSnapshot;
import cn.zswltech.mithras.api.report.ReportAssetClassifyPort;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyLib;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecordLib;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyLibService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyNodeRecordLibService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportAssetClassifyPortAdapter implements ReportAssetClassifyPort {

    @Resource
    private AssetClassifyLibService assetClassifyLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private AssetClassifyNodeRecordLibService assetClassifyNodeRecordLibService;

    @Override
    public List<ReportAssetClassifyClientSnapshot> findFinishedClientClassifySnapshots(LocalDate reportDate) {
        int year = reportDate.getYear();
        int quarter = (reportDate.getMonth().getValue() / 3) + 1;
        List<AssetClassifyLib> classifyList = assetClassifyLibService.list(Wrappers.<AssetClassifyLib>lambdaQuery()
                .eq(AssetClassifyLib::getYear, year)
                .eq(AssetClassifyLib::getVersionType, VersionTypeConstants.NORMAL)
                .eq(AssetClassifyLib::getQuarter, quarter)
                .eq(AssetClassifyLib::getFinish, YesOrNoNumberEnum.YES.getCode()));
        if (CollUtil.isEmpty(classifyList)) {
            return java.util.Collections.emptyList();
        }

        classifyList.sort(Comparator.comparing(AssetClassifyLib::getVersion).reversed());
        AssetClassifyLib assetClassifyLib = classifyList.get(0);

        List<AssetClassifyNodeRecordLib> nodeRecordList = assetClassifyNodeRecordLibService.list(Wrappers.<AssetClassifyNodeRecordLib>lambdaQuery()
                .eq(AssetClassifyNodeRecordLib::getAssetClassifyId, assetClassifyLib.getOriginId())
                .eq(AssetClassifyNodeRecordLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(AssetClassifyNodeRecordLib::getVersion));
        if (CollUtil.isEmpty(nodeRecordList)) {
            return java.util.Collections.emptyList();
        }
        nodeRecordList.sort(Comparator.comparing(AssetClassifyNodeRecordLib::getId).reversed());
        LocalDateTime identificationDate = nodeRecordList.get(0).getEndTime();

        List<AssetClassifyClientAuxiliaryLib> classifyClientList = assetClassifyClientAuxiliaryLibService.list(
                Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                        .eq(AssetClassifyClientAuxiliaryLib::getAssetClassifyId, assetClassifyLib.getOriginId())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersion, assetClassifyLib.getVersion())
                        .eq(AssetClassifyClientAuxiliaryLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(AssetClassifyClientAuxiliaryLib::getReviewStatus, PaymentStatusEnum.FINISHED.name()));
        if (CollUtil.isEmpty(classifyClientList)) {
            return java.util.Collections.emptyList();
        }
        return classifyClientList.stream()
                .map(client -> new ReportAssetClassifyClientSnapshot(client.getClientId(), client.getClassifyResult(), identificationDate))
                .collect(Collectors.toList());
    }
}
