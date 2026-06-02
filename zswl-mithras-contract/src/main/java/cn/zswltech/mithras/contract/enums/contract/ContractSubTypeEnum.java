package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractSubTypeEnum implements IMaterialsTypeConvert {
    // 租赁-回租合同
    LEASE_ITEM(ContractTypeEnum.MAIN_CONTRACT, "租赁物清单", 1),
    ESTIMATE_RENT(ContractTypeEnum.MAIN_CONTRACT, "租赁附表（概算表）", 2),
    ACTUAL_RENT(ContractTypeEnum.MAIN_CONTRACT, "实际租金支付表", 3),
    ASSETS_OWNER_CONFIRM(ContractTypeEnum.MAIN_CONTRACT, "资产所有权转移确认书", 4),
    LEASE_ITEM_QUALIFIED_CONFIRM(ContractTypeEnum.MAIN_CONTRACT, "租赁物合格接收确认书", 5),
    COLLECTION_CONFIRM(ContractTypeEnum.MAIN_CONTRACT, "收款确认书", 6),
    YSZKZR_SQ_JQRS(ContractTypeEnum.MAIN_CONTRACT, "应收账款转让申请暨确认书", 1),
    ACCEPT_CONSULTING_CONFIRM(ContractTypeEnum.CONSULTING_CONTRACT, "接受咨询服务确认函", 1),
    MORTGAGE_ITEM(ContractTypeEnum.MORTGAGE_CONTRACT, "抵押物清单", 1),

    // 租赁-直租合同
    ZL_ZZ_TERMINATE_AGREEMENT(ContractTypeEnum.MAIN_CONTRACT, "0.终止协议（原采购合同）", 0),
    ZL_ZZ_DEAL(ContractTypeEnum.MAIN_CONTRACT, "1-1.直租买卖合同（可根据实际情况修改）", 1),
    ZL_ZZ_LEASE_ITEM(ContractTypeEnum.MAIN_CONTRACT, "1-2.租赁物清单", 2),
    ZL_ZZ_ESTIMATE_PAY(ContractTypeEnum.MAIN_CONTRACT, "1-3.概算租金及租前息支付表", 3),
    ZL_ZZ_ACTUAL_PAY(ContractTypeEnum.MAIN_CONTRACT, "1-4.实际租金及租前息支付表", 4),
    ZL_ZZ_LEASE_ITEM_ACCEPT(ContractTypeEnum.MAIN_CONTRACT, "1-5.租赁物接受书", 5),
    ZL_ZZ_START_RENT(ContractTypeEnum.MAIN_CONTRACT, "1-6.起租通知书", 6),
    ZL_ZZ_RENT_BEFOREINTEREST_PAY(ContractTypeEnum.MAIN_CONTRACT, "1-7.租金租前息支付通知书", 7),
    ZL_ZZ_SUPPLE_EARNEST(ContractTypeEnum.MAIN_CONTRACT, "1-8.补足租赁保证金通知书", 8),
    ZL_ZZ_OWNER_CHANGE(ContractTypeEnum.MAIN_CONTRACT, "1-9.所有权转移证书", 9),
    ZL_ZZ_BEFOREINTEREST_ADJUST(ContractTypeEnum.MAIN_CONTRACT, "1-10.租金租前息调整通知书", 10)
    ;

    private final ContractTypeEnum parent;
    private final String display;
    private final int sort;

    private static final Map<String, ContractSubTypeEnum> map = new HashMap<>();

    static {
        for (ContractSubTypeEnum contractSubTypeEnum : values()) {
            map.put(contractSubTypeEnum.name(), contractSubTypeEnum);
        }
    }

    public static ContractSubTypeEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return display;
    }
}
