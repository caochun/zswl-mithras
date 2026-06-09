package cn.zswltech.mithras.customer.domain.enums.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.customer.domain.enums.client.CustomerEntityClassify;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 企业性质
 * 下拉框，下拉选项：国有上市、国有非上市、民营上市、民营其他上市（创业板、科创板、港股）、民营非上市 、其他。
 * @author wangchuanhao
 * @date 2022/12/19 4:00 PM
 */
@AllArgsConstructor
@Getter
public enum EnterpriseNatureEnum implements PullDown {

    gyss("国有上市", 10),
    gyfss("国有非上市",30),
    myss("民营上市", 10),
    myqtss("民营其他上市（创业板、科创板、港股）",40),
    myfss("民营非上市", 99),
    other("其他", 99),
    ;

    private String display;

    private int sort;

    private static Map<String, EnterpriseNatureEnum> map;

    static {
        map = Stream.of(EnterpriseNatureEnum.values()).collect(Collectors.toMap(EnterpriseNatureEnum::name, e -> e));
    }

    public static EnterpriseNatureEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }

    public static CustomerEntityClassify changeProjReviewDisplay(Set<String> enterpriseNatureSet) {
        if (enterpriseNatureSet == null) {
            return null;
        }
        int result = other.sort;
        for (String enterpriseNature : enterpriseNatureSet) {
            if (StrUtil.equalsAny(enterpriseNature, gyss.name(), myss.name())) {
                result = Math.min(result, gyss.sort);
            } else if (gyfss.name().equals(enterpriseNature)) {
                result = Math.min(result, gyfss.sort);
            } else if (myqtss.name().equals(enterpriseNature)) {
                result = Math.min(result, myqtss.sort);
            }
        }
        if (ObjectUtil.equals(gyss.sort, result) || ObjectUtil.equals(myss.sort, result) || ObjectUtil.equals(gyfss.sort, result)) {
            return CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED;
        } else if(ObjectUtil.equals(myqtss.sort, result)){
            return CustomerEntityClassify.CUSTOMER_OTHER_LISTED;
        } else {
            return CustomerEntityClassify.OTHER;
        }
    }
}
