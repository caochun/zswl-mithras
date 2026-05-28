package cn.zswltech.mithras.others.bigbear;

import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.impl.newimpl.CrRepayPlanNewHandler;
import cn.zswltech.mithras.report.job.ReportJob;
import cn.zswltech.mithras.service.job.data_init.FtpIncomeRateInitJob;
import cn.zswltech.mithras.web.MithrasApplication;
import liquibase.pro.packaged.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/25 11:11
 **/
@ActiveProfiles("pre")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FtpIncomeRateTests {

    @Autowired
    private FtpIncomeRateInitJob ftpIncomeRateInitJob;

    @Autowired
    private ReportJob reportJob;

    @Autowired
    private CrRepayPlanNewHandler crRepayPlanNewHandler;



    @Test
    public void testImportExcel(){
        ftpIncomeRateInitJob.ftpIncomeRateInitJob();
    }


    @Test
    public void testReport() throws InterruptedException {


    }


}
