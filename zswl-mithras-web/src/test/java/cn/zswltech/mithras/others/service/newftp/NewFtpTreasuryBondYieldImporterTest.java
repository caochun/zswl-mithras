package cn.zswltech.mithras.others.service.newftp;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpShiborInterestRateExcelModel;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpShiborInterestRateImporter;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpTreasuryBondYieldExcelModel;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpTreasuryBondYieldImporter;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/21 10:44
 */
public class NewFtpTreasuryBondYieldImporterTest extends ApplicationTest {

    @Resource
    private NewFtpTreasuryBondYieldImporter importer;
    @Resource
    private NewFtpShiborInterestRateImporter shiborInterestRateImporter;

    @Test
    public void testImport() {
        File file = new File("/Users/zhaozhengkang/Downloads/中国_10年期国债收益率.xlsx");
        List<NewFtpTreasuryBondYieldExcelModel> treasuryBondYieldExcelModels = importer.parse(FileUtil.getInputStream(file));
        File file1 = new File("/Users/zhaozhengkang/Downloads/SHIBOR_1年.xlsx");
        List<NewFtpShiborInterestRateExcelModel> shiborInterestRateExcelModels = shiborInterestRateImporter.parse(FileUtil.getInputStream(file1));
        System.out.println();
    }
}
