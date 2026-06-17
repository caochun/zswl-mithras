package cn.zswltech.mithras.others.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.ftp.oldftp.application.port.FtpInterestJobPort;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.payment.model.FtpAssessmentInfo;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.FtpService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpFinancingCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpMonthlyGuidanceDraftService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/5/23
 * @description
 */
public class FtpInterestJobTest extends ApplicationTest {
    @Resource
    private FtpInterestJobPort ftpInterestJobService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpService ftpService;

    @Test
    public void trtrtre() {
        SpringUtil.getBean(NewFtpFinancingCostPricingConfigService.class).add(LocalDate.of(2025, 6, 1));
    }

    @Test
    public void getCashFtpTest() {
        CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(4852L, LocalDate.of(2024,12,31));
        Integer cashFtp = ftpService.getCashFtp(cashFtpInfluenceBO, null);
        System.out.println(cashFtp);
    }

    @Test
    public void tetetetete() {
        SpringUtil.getBean(FtpAssessmentInfoService.class).tryRecalculateFtpAssessmentInfo(4972L);
    }

    @Test
    public void ttttt() {
        SpringUtil.getBean(NewFtpMonthlyGuidanceDraftService.class).monthlyGuidanceAdd(90L);
    }

    @Test
    public void ftpInterestCalculateTest() {
        Long ftpInterestId = 300L;
        LocalDate startDate = LocalDate.of(2025, 8, 17);
        LocalDate endDate = LocalDate.of(2025, 8, 18);
        ftpInterestJobService.calculateFtpInterest(ftpInterestId, startDate, endDate);
    }

    @Test
    public void ftpInterestChangeTest() {
        Long recordId = 107L;
        List<FtpAssessmentInfo> list = ftpAssessmentInfoService.listEffectByApplyId(recordId);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (FtpAssessmentInfo ftpAssessmentInfo : list) {
            try {
                ftpInterestBaseInfoService.calculateFtpInterestTimeRangeWithDiff(ftpAssessmentInfo, ftpAssessmentInfo.getFtpInterestDiffDate());
            } catch (Exception e) {
                log.error("FTP计息变更流程通过后重算FTP发生异常[{}]", JSONUtil.toJsonStr(ftpAssessmentInfo), e);
            }
        }
    }
}
