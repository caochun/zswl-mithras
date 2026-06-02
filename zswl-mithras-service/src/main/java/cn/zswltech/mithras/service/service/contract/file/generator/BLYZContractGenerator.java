package cn.zswltech.mithras.service.service.contract.file.generator;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractBizTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy.YzDyBaoLiBizRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferConfirmRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferNotifyRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferRegisterProtocolRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt.YzGtBaoLiBizRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferConfirmRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferNotifyRender;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferRegisterProtocolRender;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.contract.file.AbstractContractGenerate;
import cn.zswltech.mithras.service.service.contract.file.SharedResources;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.CONTRACT;
import static cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum.MAIN_CONTRACT;

/**
 * 保理有追合同生成
 */
@Slf4j
@Component
public class BLYZContractGenerator extends AbstractContractGenerate {

    private final String BIZ_TYPE = ContractBizTypeEnum.BLYZ.name();

    @Override
    public void generate(ContractBaseInfo contractBaseInfo) throws Exception {
        contractAutoGenerate(contractBaseInfo,BIZ_TYPE);
//        this.generateGuarantorContract(contractBaseInfo);
//        this.generateMortgageContract(contractBaseInfo);
//        this.generatePledgeContract(contractBaseInfo);
//        this.mainContract(contractBaseInfo);
    }

    public void mainContract(ContractBaseInfo contractBaseInfo){
        //判断是否有多个债权人
        long tenantryCount = getBean(ContractTenantryService.class).listByContractId(contractBaseInfo.getId()).stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).count();
        if (tenantryCount > 1) {
            this.generateBlGtMainContract(contractBaseInfo);
        } else {
            this.generateBlDyMainContract(contractBaseInfo);
        }
    }

    /**
     * 共同卖方
     *
     * @param contractBaseInfo
     */
    private void generateBlGtMainContract(ContractBaseInfo contractBaseInfo) {
        this.bizRender(contractBaseInfo, getBean(YzGtBaoLiBizRender.class));
        this.bizRender(contractBaseInfo, getBean(YzGtBaoLiTransferConfirmRender.class));
        this.bizRender(contractBaseInfo, getBean(YzGtBaoLiTransferNotifyRender.class));
        this.bizRender(contractBaseInfo, getBean(YzGtBaoLiTransferRegisterProtocolRender.class));
    }

    /**
     * 单一卖方
     *
     * @param contractBaseInfo
     */
    private void generateBlDyMainContract(ContractBaseInfo contractBaseInfo) {
        this.bizRender(contractBaseInfo, getBean(YzDyBaoLiBizRender.class));
        this.bizRender(contractBaseInfo, getBean(YzDyBaoLiTransferConfirmRender.class));
        this.bizRender(contractBaseInfo, getBean(YzDyBaoLiTransferNotifyRender.class));
        this.bizRender(contractBaseInfo, getBean(YzDyBaoLiTransferRegisterProtocolRender.class));
    }

    private void bizRender(ContractBaseInfo contractBaseInfo, AbstractContractRender<ContractBaseInfo> render) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String filename = render.render(os, contractBaseInfo);
            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                materialsListService.add(is, filename, contractBaseInfo.getId(), MAIN_CONTRACT.name(), null, CONTRACT.name(), YesOrNoNumberEnum.YES);
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成无追保理主业务失败，合同id：{}", contractBaseInfo.getId(), e);
            throw new MithrasException("生成无追保理主合同失败");
        }

    }


    @Override
    @PostConstruct
    public void init() {
        Map<String, Map<String, ThrowingConsumer<ContractBaseInfo>>> sharedMap = SharedResources.sharedMap;
        Map<String, ThrowingConsumer<ContractBaseInfo>> map = new ConcurrentHashMap<>();
        map.put(ContractTypeEnum.GUARANTEE_CONTRACT.name(), this::generateGuarantorContract);
        map.put(ContractTypeEnum.MORTGAGE_CONTRACT.name(), this::generateMortgageContract);
        map.put(ContractTypeEnum.PLEDGE_CONTRACT.name(), this::generatePledgeContract);
        map.put(ContractTypeEnum.MAIN_CONTRACT.name(), this::mainContract);
        map.put(ContractTypeEnum.OTHER_CONTRACT.name(), this::generateOther);
        sharedMap.put(BIZ_TYPE,map);
    }
}
