package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualExportREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractRentActualService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
public class ContractRentActualServiceTest extends ApplicationTest {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRentActualService contractRentActualService;

    @Test
    public void autoAdjustActualRentList() {
        Long contractId = 4631L;
        Long paymentId = 5163L;
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        contractBaseInfoService.autoAdjustmentRentActual(contractBaseInfo, paymentId);
    }

    @Test
    public void exportRichExcelTest() throws Exception {
        ContractRentActualExportREQ req = new ContractRentActualExportREQ();
        req.setReceiptId(6L);
        contractRentActualService.exportRichExcel(false, req, FileUtil.getOutputStream("/Users/mockorz/test.xlsx"));
    }
}
