package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.service.job.CreditReportJob;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/20 10:13
 */
class CreditReportServiceTest extends ApplicationTest{

    @Resource
    private CreditReportJob creditReportJob;


    @Test
    void cleanContract() {
        SpringContextHolder.getBean(CreditReportBaseInfoService.class).remoteXJManage(7L);
    }


    @Test
    void creditReportQueryResult() {
        creditReportJob.creditReportQueryResult();;
    }


}