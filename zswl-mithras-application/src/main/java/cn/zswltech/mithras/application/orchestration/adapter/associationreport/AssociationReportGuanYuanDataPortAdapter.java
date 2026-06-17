package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportGuanYuanDataPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportPayIncomeSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportProjectSituationSnapshot;
import cn.zswltech.mithras.dashboard.application.GuanYuanOperationService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.PayIncomeDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.ProjectSituationDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssociationReportGuanYuanDataPortAdapter implements AssociationReportGuanYuanDataPort {

    @Resource
    private GuanYuanOperationService guanYuanOperationService;

    @Override
    public List<AssociationReportPayIncomeSnapshot> listPayIncome(LocalDate queryFrom, LocalDate queryTo) {
        return guanYuanOperationService.listPayIncome(queryFrom, queryTo).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssociationReportProjectSituationSnapshot> listProjectSituation(LocalDate targetDate) {
        return guanYuanOperationService.listProjectSituation(targetDate).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private AssociationReportPayIncomeSnapshot toSnapshot(PayIncomeDTO source) {
        return AssociationReportPayIncomeSnapshot.builder()
                .deptId(source.getDeptId())
                .industryDisplay(source.getIndustryDisplay())
                .riskControlIndustryClassifyDisplay(source.getRiskControlIndustryClassifyDisplay())
                .leaseTypeDisplay(source.getLeaseTypeDisplay())
                .projectAmount(source.getProjectAmount())
                .orgScaleDisplay(source.getOrgScaleDisplay())
                .build();
    }

    private AssociationReportProjectSituationSnapshot toSnapshot(ProjectSituationDTO source) {
        return AssociationReportProjectSituationSnapshot.builder()
                .clientId(source.getClientId())
                .clientName(source.getClientName())
                .isRelated(source.getIsRelated())
                .belongGroupClientId(source.getBelongGroupClientId())
                .principalBalance(source.getPrincipalBalance())
                .marginBalance(source.getMarginBalance())
                .build();
    }
}
