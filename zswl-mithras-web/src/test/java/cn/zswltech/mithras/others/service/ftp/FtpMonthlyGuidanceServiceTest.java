package cn.zswltech.mithras.others.service.ftp;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyGuidanceService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 15:11
 */
public class FtpMonthlyGuidanceServiceTest extends ApplicationTest {
    @Resource
    private FtpMonthlyGuidanceService service;


    @Test
    public void importExcelTest() throws Exception {
        File f = new File("/Users/zhaozhengkang/Downloads/月度FTP指导模版.xlsx");
        service.importExcel(FileUtil.getInputStream(f), 8L);
    }

    @Test
    public void exportExcelTest(){
        service.exportExcel(1L, FileUtil.getOutputStream("/Users/zhaozhengkang/月度FTP定价表单导出表.xlsx"));
    }
}
