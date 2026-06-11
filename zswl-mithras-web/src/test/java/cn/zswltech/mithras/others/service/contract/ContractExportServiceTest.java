package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.contract.script.ContractExportService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 11:18
 */
public class ContractExportServiceTest extends ApplicationTest {

    @Resource
    private ContractExportService contractExportService;

    @Test
    public void exportContractsTest() throws Exception {
        contractExportService.exportContracts(FileUtil.getOutputStream("/Users/zhaozhengkang/合同统计表.xlsx"));
    }

    @Test
    public void export2() throws Exception {
        contractExportService.export2(FileUtil.getOutputStream("/Users/zhaozhengkang/Desktop/合同统计表2.xlsx"));
    }

}