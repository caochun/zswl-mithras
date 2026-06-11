package cn.zswltech.mithras.creditreport.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.creditreport.constant.CreditReportConstants;
import cn.zswltech.mithras.creditreport.service.CreditReportQueryService;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectDataService;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectService;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectCmd;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectQuery;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CreditSearchProjectServiceImpl implements CreditSearchProjectService {

    @Resource
    private CreditSearchProjectDataService creditSearchProjectDataService;
    @Resource
    private CreditReportQueryService creditReportQueryService;

    @Override
    public PageR<CreditReportListDTO> list(CreditSearchProjectQuery req) {
        String projCode = creditSearchProjectDataService.findProjectCode(req.getProjIdDataType(), req.getProjectId());
        if (StrUtil.isBlank(projCode)) {
            throw new MithrasException("无法确定项目编号");
        }
        if (ObjectUtil.isEmpty(projCode)) {
            return null;
        }
        CreditReportListREQ reportListREQ = new CreditReportListREQ();
        reportListREQ.setProjCode(projCode);
        return creditReportQueryService.list(reportListREQ);
    }

    @Override
    public CreditReportProjectReviewAddDTO showCreditReportByProjId(CreditSearchProjectCmd cmd) {
        CreditReportProjectReviewAddDTO projectDTO = creditSearchProjectDataService.buildProjectReviewAdd(cmd.getBizType(), cmd.getProjectId());
        projectDTO.setProjId(cmd.getProjectId())
                .setReportFormat(CreditReportConstants.REPORT_FORMAT)
                .setSelectVersion(CreditReportConstants.SELECT_VERSION);
        return projectDTO;
    }
}
