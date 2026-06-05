package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;

public interface CreditSearchProjectDataService {

    String findProjectCode(String bizType, Long projectId);

    CreditReportProjectReviewAddDTO buildProjectReviewAdd(String bizType, Long projectId);
}
