package cn.zswltech.mithras.fund.job;

import cn.zswltech.mithras.fund.application.organization.FundOrganizationInstitutionCodeSyncService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FundOrganizationJob {

    @Resource
    private FundOrganizationInstitutionCodeSyncService fundOrganizationInstitutionCodeSyncService;

    @XxlJob("FundOrganizationCodeJob")
    public void FundOrganizationCode() {
        log.info("FundOrganizationCodeJob, start");
        fundOrganizationInstitutionCodeSyncService.syncInstitutionCode();
        log.info("FundOrganizationCodeJob, end");
    }
}

