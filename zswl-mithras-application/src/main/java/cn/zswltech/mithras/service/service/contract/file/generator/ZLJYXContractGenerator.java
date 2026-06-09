package cn.zswltech.mithras.service.service.contract.file.generator;

import cn.zswltech.mithras.contract.enums.contract.ContractBizTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.file.AbstractContractGenerate;
import cn.zswltech.mithras.service.service.contract.file.SharedResources;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description 租赁-经营性租赁合同文本生成器
 * 可生成 咨询合同、保证合同、抵押合同
 */
@Component
public class ZLJYXContractGenerator extends AbstractContractGenerate {

    private final String BIZ_TYPE = ContractBizTypeEnum.ZLJY.name();

    @Override
    public void generate(ContractBaseInfo contractBaseInfo) throws Exception {
        contractAutoGenerate(contractBaseInfo,BIZ_TYPE);
//        this.generateConsultingMainContract(contractBaseInfo);
//        this.generateGuarantorContract(contractBaseInfo);
//        this.generateMortgageContract(contractBaseInfo);
//        this.generatePledgeContract(contractBaseInfo);
    }

    @Override
    @PostConstruct
    public void init() {
        Map<String, Map<String, ThrowingConsumer<ContractBaseInfo>>> sharedMap = SharedResources.sharedMap;
        Map<String, ThrowingConsumer<ContractBaseInfo>> map = new ConcurrentHashMap<>();
        map.put(ContractTypeEnum.CONSULTING_CONTRACT.name(), this::generateConsultingMainContract);
        map.put(ContractTypeEnum.GUARANTEE_CONTRACT.name(), this::generateGuarantorContract);
        map.put(ContractTypeEnum.MORTGAGE_CONTRACT.name(), this::generateMortgageContract);
        map.put(ContractTypeEnum.PLEDGE_CONTRACT.name(), this::generatePledgeContract);
        map.put(ContractTypeEnum.OTHER_CONTRACT.name(), this::generateOther);
        sharedMap.put(BIZ_TYPE,map);
    }
}
