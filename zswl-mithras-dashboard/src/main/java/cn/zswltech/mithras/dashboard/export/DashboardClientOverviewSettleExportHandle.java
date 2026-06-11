package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSettledREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSettledRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardClientOverviewController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardClientOverviewSettleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewSettleModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardClientOverviewSettleExportHandle extends ExportHandle<DashboardClientOverviewSettleModel, DashboardClientOverviewSettleExcelExporter> {

    @Resource
    private DashboardClientOverviewController dashboardClientOverviewController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_CLIENT_OVERVIEW_SETTLED.name();
    }

    @Override
    public DashboardClientOverviewSettleExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardClientOverviewSettleExcelExporter.class);
    }

    @Override
    public List<DashboardClientOverviewSettleModel> req2ExportList(FileExportREQ req) {
        DashboardClientOverviewSettledREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardClientOverviewSettledREQ.class);
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardClientOverviewController.settledPageList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardClientOverviewSettledRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardClientOverviewSettledRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardClientOverviewSettledRSP.class));
        }
        List<DashboardClientOverviewSettleModel> list = new ArrayList<>();

        dashboardClientOverviewAllRSPS.forEach(one -> {
            List<DashboardClientOverviewSettledRSP.OverviewSettledProjRSP> projReviewList = one.getProjReviewList();
            if(ObjectUtil.isNotEmpty(projReviewList)){
                projReviewList.forEach(proj -> {
                    List<DashboardClientOverviewSettledRSP.OverviewSettledContractRSP> contractList = proj.getContractList();
                    if(ObjectUtil.isNotEmpty(contractList)){
                        for (DashboardClientOverviewSettledRSP.OverviewSettledContractRSP contract : contractList) {
                            DashboardClientOverviewSettleModel model = new DashboardClientOverviewSettleModel();
                            model.setClientName(one.getClientName());
                            model.setBizDeptName(one.getBizDeptName());
                            model.setProjSponsorUserName(one.getProjSponsorUserName());
                            model.setProjName(proj.getProjName());
                            model.setContractCode(contract.getContractCode());
                            model.setBizType(Optional.ofNullable(ProjectBizType.of(contract.getBizType())).map(ProjectBizType::display).orElse(null));
                            model.setContractBizDeptName(contract.getBizDeptName());
                            model.setContractSponsorUserName(contract.getProjSponsorUserName());
                            if(ObjectUtil.isNotEmpty(contract.getProjCosponsorUserNames())){
                                model.setProjCosponsorUserNames(String.join(",", contract.getProjCosponsorUserNames()));
                            }
                            list.add(model);
                        }
                    }
                });
            }
        });
        return list;
    }

}
