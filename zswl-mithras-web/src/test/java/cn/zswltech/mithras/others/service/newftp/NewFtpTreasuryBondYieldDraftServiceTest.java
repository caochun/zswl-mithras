package cn.zswltech.mithras.others.service.newftp;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpTreasuryBondYieldConfigService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/21 13:59
 */
public class NewFtpTreasuryBondYieldDraftServiceTest extends ApplicationTest {
    @Resource
    private NewFtpTreasuryBondYieldConfigService service;

    @Test
    public void testImport() {
        File file = new File("/Users/zhaozhengkang/Downloads/中国_10年期国债收益率.xlsx");
        service.importFile(FileUtil.getInputStream(file));
        System.out.println();
    }
}
