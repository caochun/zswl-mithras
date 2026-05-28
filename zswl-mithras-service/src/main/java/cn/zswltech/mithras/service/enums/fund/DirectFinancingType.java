package cn.zswltech.mithras.service.enums.fund;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/16 16:28
 */
public enum DirectFinancingType implements PullDown {
    //ABS、ABN、CP、SCP、私募债、公募债
    ABS("ABS"),
    ABN("ABN"),
    MTN("MTN"),
    CP("CP"),
    SCP("SCP"),
    PRIVATE_BOND("公司债")
//    PUBLIC_BOND("公募债"),
//    PRIVATE_BOND_NEW("私募债")
    ;
    private final String display;

    DirectFinancingType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    public static DirectFinancingType findByName(String name) {
        for (DirectFinancingType item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static String name2Display(String name){
        DirectFinancingType financingType = findByName(name);
        if (financingType != null){
            return financingType.display;
        }
        return Strings.EMPTY;
    }
}
