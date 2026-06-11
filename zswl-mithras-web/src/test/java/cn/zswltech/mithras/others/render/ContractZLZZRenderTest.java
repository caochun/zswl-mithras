package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.gendoc.render.contractzlzz.*;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/4/19
 * @description
 */
public class ContractZLZZRenderTest extends ApplicationTest {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractZLZZMainRender contractZLZZMainRender;
    @Resource
    private ContractZLZZDealRender contractZLZZDealRender;
    @Resource
    private ContractZLZZLeaseItemRender contractZLZZLeaseItemRender;
    @Resource
    private ContractZLZZLeaseItemAcceptRender contractZLZZLeaseItemAcceptRender;
    @Resource
    private ContractZLZZStartRentRender contractZLZZStartRentRender;
    @Resource
    private ContractZLZZRentPayRender contractZLZZRentPayRender;
    @Resource
    private ContractZLZZSuppleEarnestRender contractZLZZSuppleEarnestRender;
    @Resource
    private ContractZLZZOwnerChangeRender contractZLZZOwnerChangeRender;
    @Resource
    private ContractZLZZRentAdjustRender contractZLZZRentAdjustRender;
    @Resource
    private ContractZLZZTerminateAgreementRender contractZLZZTerminateAgreementRender;
    @Resource
    private ContractZLZZEstimatePayRender contractZLZZEstimatePayRender;
    @Resource
    private ContractZLZZActualPayRender contractZLZZActualPayRender;

    @Test
    public void mainTest() throws Exception {
        contractZLZZMainRender.render(FileUtil.getOutputStream("/Users/mockorz/main.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void dealTest() throws Exception {
        contractZLZZDealRender.render(FileUtil.getOutputStream("/Users/mockorz/deal.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void leaseItemTest() throws Exception {
        contractZLZZLeaseItemRender.render(FileUtil.getOutputStream("/Users/mockorz/lease_item.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void leaseItemAcceptTest() throws Exception {
        contractZLZZLeaseItemAcceptRender.render(FileUtil.getOutputStream("/Users/mockorz/lease_item_accept.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void startRentTest() throws Exception {
        contractZLZZStartRentRender.render(FileUtil.getOutputStream("/Users/mockorz/start_rent.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void rentPayTest() throws Exception {
        contractZLZZRentPayRender.render(FileUtil.getOutputStream("/Users/mockorz/rent_pay.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void suppleEarnestTest() throws Exception {
        contractZLZZSuppleEarnestRender.render(FileUtil.getOutputStream("/Users/mockorz/supple_earnest.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void ownerChangeTest() throws Exception {
        contractZLZZOwnerChangeRender.render(FileUtil.getOutputStream("/Users/mockorz/owner_change.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void rentAdjustTest() throws Exception {
        contractZLZZRentAdjustRender.render(FileUtil.getOutputStream("/Users/mockorz/rent_adjust.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void terminateAgreementTest() throws Exception {
        contractZLZZTerminateAgreementRender.render(FileUtil.getOutputStream("/Users/mockorz/terminate_agreement.docx"), contractBaseInfoService.getById(2368L));
    }

    @Test
    public void estimateRentTest() throws Exception {
        contractZLZZEstimatePayRender.render(FileUtil.getOutputStream("/Users/mockorz/estimate_rent.docx"), contractBaseInfoService.getById(2508L));
    }

    @Test
    public void actualRentTest() throws Exception {
        contractZLZZActualPayRender.render(FileUtil.getOutputStream("/Users/mockorz/actual_rent.docx"), contractBaseInfoService.getById(2368L));
    }
}
