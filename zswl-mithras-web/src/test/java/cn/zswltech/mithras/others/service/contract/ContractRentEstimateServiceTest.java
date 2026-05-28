package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateExportREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.contract.ContractRentEstimateService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
public class ContractRentEstimateServiceTest extends ApplicationTest {
    @Resource
    private ContractRentEstimateService contractRentEstimateService;

    @Test
    public void exportRichExcelTest() throws Exception {
        ContractRentEstimateExportREQ req = new ContractRentEstimateExportREQ();
        req.setContractId(51L);
        contractRentEstimateService.exportRichExcel(req, FileUtil.getOutputStream("/Users/mockorz/test.xlsx"));
    }
}
