package cn.zswltech.mithras.customer.mobile.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordREQ;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.customer.mobile.application.AppApplicationService;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.customer.mobile.enums.VisitPhaseStatus;
import cn.zswltech.mithras.customer.mobile.enums.VisitTypeStatus;
import cn.zswltech.mithras.customer.mobile.enums.VisitWayStatus;
import cn.zswltech.mithras.customer.mobile.excel.exporter.AppPcVisitRecordDetailExcelExporter;
import cn.zswltech.mithras.customer.mobile.excel.model.AppPcVisitRecordDetailModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AppPcVisitRecordDetailExportHandle extends ExportHandle<AppPcVisitRecordDetailModel, AppPcVisitRecordDetailExcelExporter> {

    @Resource
    private AppApplicationService appApplicationService;

    @Override
    public String getBusinessType() {
        return FileExportEnum.APP_PC_VISIT_RECORD_DETAIL.name();
    }

    @Override
    public AppPcVisitRecordDetailExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(AppPcVisitRecordDetailExcelExporter.class);
    }

    @Override
    public List<AppPcVisitRecordDetailModel> req2ExportList(FileExportREQ req) {
        AppPCVisitRecordREQ appPCVisitRecordREQ = BeanUtil.copyProperties(req.getExt(), AppPCVisitRecordREQ.class);
        appPCVisitRecordREQ.setPage(1);
        appPCVisitRecordREQ.setPageSize(Integer.MAX_VALUE);
        R<PageR<AppPCVisitRecordRSP>> recordRsp = appApplicationService.pcList(appPCVisitRecordREQ);
        PageR<AppPCVisitRecordRSP> data = recordRsp.getData();
        List<AppPCVisitRecordRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        // 如果勾选了指定id则过滤出指定id的数据（不另外写方法是为了减少改动，复用pcList方法的返回结果）
        if (CollectionUtil.isNotEmpty(req.getIds())) {
            lists.removeIf(e -> !req.getIds().contains(e.getId()));
        }
        List<AppPcVisitRecordDetailModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            AppPcVisitRecordDetailModel model = BeanUtil.copyProperties(rsp, AppPcVisitRecordDetailModel.class);
            model.setVisitPhase(Objects.requireNonNull(VisitPhaseStatus.of(rsp.getVisitPhase())).display());
            model.setVisitType(Objects.requireNonNull(VisitTypeStatus.of(rsp.getVisitType())).display());
            model.setVisitWay(Objects.requireNonNull(VisitWayStatus.of(rsp.getVisitWay())).display());
            list.add(model);
        });
        return list;
    }

}
