package cn.zswltech.mithras.service.service.materialsfile.export.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordREQ;
import cn.zswltech.mithras.dto.app.AppPCVisitRecordRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.app.AppController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.customer.domain.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitTypeStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitWayStatus;
import cn.zswltech.mithras.service.excel.exporter.app.AppPcVisitRecordDetailExcelExporter;
import cn.zswltech.mithras.service.excel.model.app.AppPcVisitRecordDetailModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AppPcVisitRecordDetailExportHandle extends ExportHandle<AppPcVisitRecordDetailModel, AppPcVisitRecordDetailExcelExporter> {

    @Resource
    private AppController appController;

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
        R<PageR<AppPCVisitRecordRSP>> recordRsp = appController.pcList(appPCVisitRecordREQ);
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
