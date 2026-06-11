package cn.zswltech.mithras.application.orchestration.contract.file.generator;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractBizTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.wz.WzBaoLiBizRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.wz.WzBaoLiTransferConfirmRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.wz.WzBaoLiTransferNotifyRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.wz.WzBaoLiTransferRegisterProtocolRender;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.file.AbstractContractGenerate;
import cn.zswltech.mithras.contract.application.file.ContractGenerateAction;
import cn.zswltech.mithras.contract.application.file.SharedResources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum.CONTRACT;

/**
 * 保理无追合同生成
 */
@Slf4j
@Component
public class BLWZContractGenerator extends AbstractContractGenerate {

    private final String BIZ_TYPE = ContractBizTypeEnum.BLWZ.name();

    @Override
    public void generate(ContractBaseInfo contractBaseInfo) throws Exception {
        contractAutoGenerate(contractBaseInfo,BIZ_TYPE);
//        this.generateGuarantorContract(contractBaseInfo);
//        this.generateMortgageContract(contractBaseInfo);
//        this.generatePledgeContract(contractBaseInfo);
//        this.generateBlMainContract(contractBaseInfo);
    }

    private void generateBlMainContract(ContractBaseInfo contractBaseInfo) {
        this.bizRender(contractBaseInfo, getBean(WzBaoLiBizRender.class));
        this.bizRender(contractBaseInfo, getBean(WzBaoLiTransferConfirmRender.class));
        this.bizRender(contractBaseInfo, getBean(WzBaoLiTransferNotifyRender.class));
        this.bizRender(contractBaseInfo, getBean(WzBaoLiTransferRegisterProtocolRender.class));
    }

    private void bizRender(ContractBaseInfo contractBaseInfo, AbstractContractRender<ContractBaseInfo> render) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String filename = render.render(os, contractBaseInfo);
            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                materialsListService.add(is, filename, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, CONTRACT.name(), YesOrNoNumberEnum.YES);
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
        Map<String, Map<String, ContractGenerateAction<ContractBaseInfo>>> sharedMap = SharedResources.sharedMap;
        Map<String, ContractGenerateAction<ContractBaseInfo>> map = new ConcurrentHashMap<>();
        map.put(ContractTypeEnum.GUARANTEE_CONTRACT.name(), this::generateGuarantorContract);
        map.put(ContractTypeEnum.MORTGAGE_CONTRACT.name(), this::generateMortgageContract);
        map.put(ContractTypeEnum.PLEDGE_CONTRACT.name(), this::generatePledgeContract);
        map.put(ContractTypeEnum.MAIN_CONTRACT.name(), this::generateBlMainContract);
        map.put(ContractTypeEnum.OTHER_CONTRACT.name(), this::generateOther);
        sharedMap.put(BIZ_TYPE,map);
    }
}
