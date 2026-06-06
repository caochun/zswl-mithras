package cn.zswltech.mithras.others.service.ftp;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListReq;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceListRsp;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.ftp.newftp.job.NewFtpJob;
import cn.zswltech.mithras.service.service.ftp.FtpQuarterlyGuidanceService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 11:18
 */
public class FtpImportServiceTest extends ApplicationTest {

    @Resource
    private FtpQuarterlyGuidanceService mainService;
    @Resource
    private NewFtpJob newFtpJob;

    @Test
    public void importExcelTest() throws Exception {
        File f = new File("/Users/zhaozhengkang/季度最低收益率指导模版.xlsx");
        mainService.importExcel(FileUtil.getInputStream(f), 1L);
    }

    @Test
    public void listTest(){
        FtpQuarterlyGuidanceListReq req = new FtpQuarterlyGuidanceListReq();
        req.setPage(1);
        req.setPageSize(10);
        PageR<FtpQuarterlyGuidanceListRsp> list = mainService.list(req);
        System.out.println(list);
    }

    @Test
    public void exportExcel(){
        mainService.exportExcel(1L, FileUtil.getOutputStream("/Users/zhaozhengkang/季度最低收益率指导导出表.xlsx"));
    }
    @Test
    public void doubleTest(){
        double numericCellValue = 3.1264;
        double v = (double) Math.round(numericCellValue * 100) / 100;
        BigDecimal bg = new BigDecimal(numericCellValue).setScale(2, RoundingMode.UP);
        System.out.println();
    }

    @Test
    public void calculateFtpGuaranteeCostPricing(){
        newFtpJob.calculateFtpGuaranteeCostPricing();
    }

}
