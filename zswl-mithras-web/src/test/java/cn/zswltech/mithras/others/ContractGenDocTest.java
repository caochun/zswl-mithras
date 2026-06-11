package cn.zswltech.mithras.others;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.gendoc.render.*;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractMortgageMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractPledge;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.FileOutputStream;

/**
 * 合同生成测试
 *
 * @author wangchuanhao
 * @date 2022/8/24 4:10 PM
 */
public class ContractGenDocTest extends ApplicationTest {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractMortgageMapper contractMortgageMapper;
    @Resource
    private ContractMainAssetsOwnerChangeRender contractMainAssetsOwnerChangeRender;
    @Resource
    private ContractMainRentEstimateRender contractMainRentEstimateRender;
    @Resource
    private ContractMainLeaseItemRender contractMainLeaseItemRender;
    @Resource
    private ContractMainRentActualRender contractMainRentActualRender;
    @Resource
    private ContractMortgageItemRender contractMortgageItemRender;

    @Test
    public void contractNormalCreditAuthRenderTest() throws Exception {
        Client client = SpringUtil.getBean(ClientService.class).getById(2063L);
        SpringUtil.getBean(ContractNormalCreditAuthRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/个人征信授权书_%s.docx", client.getClientName())), client);
    }

    @Test
    public void contractFileSignBillRenderTest() throws Exception {
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(1284L);
        SpringUtil.getBean(ContractFileSignBillRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/文件签收单_%s.docx", contractBaseInfo.getProjName())), contractBaseInfo);
    }

    @Test
    public void contractMortgageRenderTest() throws Exception {
        ContractMortgage contractMortgage = SpringUtil.getBean(ContractMortgageService.class).getById(2870L);
        SpringUtil.getBean(ContractMortgageRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_抵押合同_动产抵押_%s.docx", contractMortgage.getContractId())), contractMortgage);
    }

    @Test
    public void contractGuarantorRenderTest() throws Exception {
        ContractGuarantor contractGuarantor = SpringUtil.getBean(ContractGuarantorService.class).getById(2918L);
        SpringUtil.getBean(ContractGuarantorRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_保证合同_法人_%s.docx", contractGuarantor.getContractId())), contractGuarantor);
    }

    @Test
    public void contractPledgeReceivableRenderTest() throws Exception {
        ContractPledge contractPledge = SpringUtil.getBean(ContractPledgeService.class).getById(159L);
        SpringUtil.getBean(ContractPledgeReceivableRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_质押合同_应收账款_%s.docx", contractPledge.getContractId())), contractPledge);
    }

    @Test
    public void contractPledgeStockRenderTest() throws Exception {
        ContractPledge contractPledge = SpringUtil.getBean(ContractPledgeService.class).getById(159L);
        SpringUtil.getBean(ContractPledgeStockRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_质押合同_股权_%s.docx", contractPledge.getContractId())), contractPledge);
    }

    @Test
    public void contractConsultingRenderTest() throws Exception {
        Long contractId = 4837L;
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(contractId);
        SpringUtil.getBean(ContractConsultingRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_咨询合同_%s.docx", contractId)), contractBaseInfo);
    }

    @Test
    public void contractMainZLHZRenderTest() throws Exception {
        Long contractId = 4837L;
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(contractId);
        SpringUtil.getBean(ContractMainZLHZRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/合同_租赁合同_主合同_回租_%s.docx", contractId)), contractBaseInfo);
    }

    @Test
    public void contractMainCollectionConfirmRenderTest() throws Exception {
        Long contractId = 4837L;
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(contractId);
        SpringUtil.getBean(ContractMainCollectionConfirmRender.class).render(FileUtil.getOutputStream(String.format("/Users/dingqi/收款确认书_%s.docx", contractId)), contractBaseInfo);
    }

    @Test
    public void contractMainAssetsOwnerChangeRender() throws Exception {
        contractMainAssetsOwnerChangeRender.render(new FileOutputStream(String.format("/Users/wang/Desktop/合同文件生成/合同_租赁合同_附属_资产所有权转移确认书_%s.docx", 51)),
                contractBaseInfoMapper.selectById(51L));
    }

    @Test
    public void contractMainRentEstimateRender() throws Exception {
        Long contractId = 4863L;
        contractMainRentEstimateRender.render(new FileOutputStream(String.format("/Users/dingqi/合同_租赁合同_附属_租赁附表（概算表）_%s.docx", contractId)),
                contractBaseInfoMapper.selectById(contractId));
    }

    @Test
    public void contractMainLeaseItemRender() throws Exception {
        contractMainLeaseItemRender.render(new FileOutputStream("/Users/wang/Desktop/contract_lease_item.docx"),
                contractBaseInfoMapper.selectById(1808L));
    }

    @Test
    public void contractMortgageItemRender() throws Exception {
        contractMortgageItemRender.render(new FileOutputStream("/Users/wang/Desktop/mortgage_item.docx"),
                contractMortgageMapper.selectById(1178L));
    }

    @Test
    public void contractMainRentActualRender() throws Exception {
        contractMainRentActualRender.render(new FileOutputStream(String.format("/Users/wang/Desktop/合同文件生成/合同_租赁合同_附属_实际租金表_%s.docx", 51)),
                contractBaseInfoMapper.selectById(51L));
    }

}
