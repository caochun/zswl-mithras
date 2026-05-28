package cn.zswltech.mithras.service.enums.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 合同文件查询类型
 *
 * @author wangchuanhao
 * @date 2023/2/6 3:14 PM
 */
@AllArgsConstructor
@Getter
public enum ContractFileQueryType {

    COMMON("主界面查询", Arrays.asList(
            ContractTypeEnum.MAIN_CONTRACT.name(),
            ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(),
            ContractTypeEnum.CONSULTING_CONTRACT.name(),
            ContractTypeEnum.GUARANTEE_CONTRACT.name(),
            ContractTypeEnum.MORTGAGE_CONTRACT.name(),
            ContractTypeEnum.PLEDGE_CONTRACT.name(),
            ContractTypeEnum.MORTGAGE_PLEDGE_FILE.name(),
            ContractTypeEnum.RESOLUTION_FILE.name(),
            ContractTypeEnum.LEASE_ITEM_FILE.name(),
            ContractTypeEnum.OTHER_CONTRACT.name(),
            ContractTypeEnum.BASE_PROFILE.name())),
    CHANGE("变更材料", Arrays.asList(ContractChangeMaterialEnum.EXCHANGE_MATERIAL.name())),
    SETTLE("结清补充协议", Arrays.asList(ContractExtraFileTypeEnum.CONTRACT_SETTLE.name())),
    SETTLE_OWN("所有权转移证书", Arrays.asList(ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name())),
    START_RENT("起租材料", Arrays.asList(ContractExtraFileTypeEnum.START_RENT.name()));
    /**
     * 描述
     */
    private String desc;

    /**
     * 要查询的文件类型列表
     */
    private List<String> materialsTypeList;

    private static Map<String, ContractFileQueryType> map;

    static {
        map = Stream.of(ContractFileQueryType.values()).collect(Collectors.toMap(ContractFileQueryType::name, e -> e));
    }

    public static ContractFileQueryType of(String name) {
        return map.get(name);
    }

}
