package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 来自LeaseItemManagerLeaseItemType 提供给前端使用，供客户定制化调整
 */
@AllArgsConstructor
@Getter
public enum LeaseItemManagerLeaseItemViewType implements PullDown {
    PRODUCTION_EQUIPMENT("生产设备", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    VESSEL("船舶", Arrays.asList("租赁船舶名称", "船籍港", "船型", "船舶识别号", "IMO编号", "建造厂家", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "其他")),
    POWER_STATION("电站", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    COMMUNICATION_BASE_STATION("通信基站", Arrays.asList("设备名称", "基站名称", "经度", "纬度", "基站配置", "详细地址", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "其他")),
    PUBLIC("公用事业", Arrays.asList("名称*", "数量", "单位", "入账/购置日期", "评估原值（元）", "评估净值（元）", "存放地点*", "其他")),
    BUS("公交车", Arrays.asList("名称*", "车架号/规格型号", "供应商/销货方", "数量", "单位", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    ENVIRONMENT_PROTECTION("环保", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    PRIVATE_EDUCATION("民办教育", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    HEALTHCARE("医疗健康", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    MOTOR_VEHICLE("机动车", Arrays.asList("承租人", "种类*", "名称*", "车架号/规格型号", "数量", "单位", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    OTHER("其他", Arrays.asList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),

    ;

    private final String display;
    private final List<String> templateHeaderList;

    public static LeaseItemManagerLeaseItemViewType of(String name) {
        for (LeaseItemManagerLeaseItemViewType item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
