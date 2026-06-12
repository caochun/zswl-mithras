package cn.zswltech.mithras.customer.mobile.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.app.AppPCVisitSummaryREQ;
import cn.zswltech.mithras.dto.app.AppPCVisitSummaryRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.customer.mobile.application.AppApplicationService;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.customer.mobile.excel.exporter.AppPcVisitRecordSummaryExcelExporter;
import cn.zswltech.mithras.customer.mobile.excel.model.AppPcVisitRecordSummaryModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AppPcVisitRecordSummaryExportHandle extends ExportHandle<AppPcVisitRecordSummaryModel, AppPcVisitRecordSummaryExcelExporter> {

    private static final String RECORDS = "records";
    private static final String SUM_DATE = "sumData";

    @Resource
    private AppApplicationService appApplicationService;

    @Override
    public String getBusinessType() {
        return FileExportEnum.APP_PC_VISIT_RECORD_SUMMARY.name();
    }

    @Override
    public AppPcVisitRecordSummaryExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(AppPcVisitRecordSummaryExcelExporter.class);
    }

    @Override
    public List<AppPcVisitRecordSummaryModel> req2ExportList(FileExportREQ req) {
        AppPCVisitSummaryREQ appPCVisitSummaryREQ = BeanUtil.copyProperties(req.getExt(), AppPCVisitSummaryREQ.class);
        R<Map<String, Object>> mapR = appApplicationService.pcSummary(appPCVisitSummaryREQ);
        Map<String, Object> data = mapR.getData();
        List<AppPCVisitSummaryRSP> lists = (List<AppPCVisitSummaryRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), AppPCVisitSummaryRSP.class));
        }
        List<AppPcVisitRecordSummaryModel> list = new ArrayList<>();
        for (AppPCVisitSummaryRSP rsp : lists) {
            if (rsp.getObjectInfoList() != null && !rsp.getObjectInfoList().isEmpty()) {
                for (AppPCVisitSummaryRSP.ObjectInfo objectInfo : rsp.getObjectInfoList()) {
                    AppPcVisitRecordSummaryModel model = new AppPcVisitRecordSummaryModel();
                    model.setDeptName(rsp.getDeptName());
                    model.setCreatedName(objectInfo.getCreatedName());
                    model.setVisitCount(String.valueOf(objectInfo.getVisitCount()));
                    model.setClientCount(String.valueOf(objectInfo.getClientCount()));
                    list.add(model);
                }
            }
        }
        return list;
    }

}
