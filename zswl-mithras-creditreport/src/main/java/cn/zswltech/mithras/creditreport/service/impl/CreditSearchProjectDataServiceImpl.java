package cn.zswltech.mithras.creditreport.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.creditreport.service.CreditReportClientSnapshot;
import cn.zswltech.mithras.creditreport.service.CreditReportClientSupportPort;
import cn.zswltech.mithras.creditreport.service.CreditReportProjectDataPort;
import cn.zswltech.mithras.creditreport.service.CreditReportProjectSnapshot;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectDataService;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.creditreport.service.CreditReportPaymentPort;
import cn.zswltech.mithras.creditreport.service.CreditReportPaymentProjectSnapshot;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditSearchProjectDataServiceImpl implements CreditSearchProjectDataService {

    private static final String PROJ_ESTABLISH = "PROJ_ESTABLISH";
    private static final String PROJ_REVIEW = "PROJ_REVIEW";
    private static final String GROUP_CREDIT_ESTABLISH = "GROUP_CREDIT_ESTABLISH";
    private static final String GROUP_CREDIT_REVIEW = "GROUP_CREDIT_REVIEW";
    private static final String PAYMENT = "PAYMENT";

    @Resource
    private CreditReportPaymentPort creditReportPaymentPort;
    @Resource
    private CreditReportProjectDataPort creditReportProjectDataPort;
    @Resource
    private CreditReportClientSupportPort creditReportClientSupportPort;

    @Override
    public String findProjectCode(String bizType, Long projectId) {
        if (StrUtil.equals(bizType, PROJ_REVIEW)) {
            return getProjectCode(bizType, projectId);
        } else if (StrUtil.equals(bizType, PROJ_ESTABLISH)) {
            return getProjectCode(bizType, projectId);
        } else if (StrUtil.equals(bizType, GROUP_CREDIT_REVIEW)) {
            return getProjectCode(bizType, projectId);
        } else if (StrUtil.equals(bizType, GROUP_CREDIT_ESTABLISH)) {
            return getProjectCode(bizType, projectId);
        } else if (StrUtil.equals(bizType, PAYMENT)) {
            CreditReportPaymentProjectSnapshot paymentProjectSnapshot = creditReportPaymentPort.getProjectSnapshotByPaymentId(projectId);
            return Optional.ofNullable(paymentProjectSnapshot)
                    .map(CreditReportPaymentProjectSnapshot::getProjCode)
                    .orElse(null);
        }
        throw new MithrasException("未定义的处理类型");
    }

    @Override
    public CreditReportProjectReviewAddDTO buildProjectReviewAdd(String bizType, Long projectId) {
        if (StringUtils.isBlank(bizType)) {
            throw new MithrasException("业务类型不存在");
        }

        CreditReportProjectReviewAddDTO projectDTO = new CreditReportProjectReviewAddDTO();
        List<Long> clientIds = new ArrayList<>();

        if (PAYMENT.equals(bizType)) {
            CreditReportPaymentProjectSnapshot paymentProjectSnapshot = creditReportPaymentPort.getProjectSnapshotByPaymentId(projectId);
            if (paymentProjectSnapshot != null) {
                projectDTO.setProjCode(paymentProjectSnapshot.getProjCode()).setProjectName(paymentProjectSnapshot.getProjName());
                clientIds.addAll(Optional.ofNullable(paymentProjectSnapshot.getClientIds()).orElse(new ArrayList<>()));
            }
        } else if (isProjectBizType(bizType)) {
            CreditReportProjectSnapshot projectSnapshot = creditReportProjectDataPort.getProjectSnapshot(bizType, projectId);
            if (projectSnapshot != null) {
                projectDTO.setProjCode(projectSnapshot.getProjCode()).setProjectName(projectSnapshot.getProjName());
                clientIds.addAll(Optional.ofNullable(projectSnapshot.getClientIds()).orElse(new ArrayList<>()));
            }
        } else {
            throw new MithrasException("未定义的处理类型");
        }

        projectDTO.setClientInfos(buildClientInfos(clientIds));
        return projectDTO;
    }

    private String getProjectCode(String bizType, Long projectId) {
        return Optional.ofNullable(creditReportProjectDataPort.getProjectSnapshot(bizType, projectId))
                .map(CreditReportProjectSnapshot::getProjCode)
                .orElse(null);
    }

    private boolean isProjectBizType(String bizType) {
        return PROJ_ESTABLISH.equals(bizType)
                || PROJ_REVIEW.equals(bizType)
                || GROUP_CREDIT_ESTABLISH.equals(bizType)
                || GROUP_CREDIT_REVIEW.equals(bizType);
    }

    private List<CreditReportClientInfo> buildClientInfos(List<Long> clientIds) {
        return creditReportClientSupportPort.listCorporationClients(clientIds).stream()
                .map(this::toClientInfo)
                .collect(Collectors.toList());
    }

    private CreditReportClientInfo toClientInfo(CreditReportClientSnapshot client) {
        return new CreditReportClientInfo()
                .setClientId(client.getClientId())
                .setClientName(client.getClientName())
                .setCscCode(client.getCscCode())
                .setZhongZhengCode(client.getZhongZhengCode());
    }
}
