package cn.zswltech.mithras.others.service.newftp;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpShiborInterestRateConfigService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/21 16:13
 */
public class NewFtpShiborInterestRateServiceTest extends ApplicationTest {

    @Resource
    private NewFtpShiborInterestRateConfigService rateService;

    @Test
    public void testImportFile() {
        File file = new File("/Users/zhaozhengkang/Downloads/SHIBOR_1年.xlsx");
        rateService.importFile(FileUtil.getInputStream(file));

    }
}
