package cn.zswltech.mithras.service.enums.lease;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description 租赁物类型，实际使用，提供给前端LeaseItemManagerLeaseItemType 为此类子集
 */
@AllArgsConstructor
@Getter
public enum LeaseItemManagerLeaseItemType implements PullDown {
    BUS("公交车", ListUtil.toList("名称*", "车架号/规格型号", "供应商/销货方", "数量", "单位", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    MOTOR_VEHICLE("机动车", ListUtil.toList("承租人", "种类*", "名称*", "车架号/规格型号", "数量", "单位", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    NO_PRODUCTION_LINE_EQUIPMENT("非生产线设备", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    PRODUCTION_EQUIPMENT("生产设备", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    PRODUCTION_LINE("生产线", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    VESSEL("船舶", ListUtil.toList("租赁船舶名称", "船籍港", "船型", "船舶识别号", "IMO编号", "建造厂家", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "其他")),
    COMMUNICATION_BASE_STATION("通信基站", ListUtil.toList("设备名称", "基站名称", "经度", "纬度", "基站配置", "详细地址", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "其他")),
    PUBLIC("公用事业", ListUtil.toList("名称*", "数量", "单位", "入账/购置日期", "评估原值（元）", "评估净值（元）", "存放地点*", "其他")),
    OTHER("其他", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    POWER_STATION("电站", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    ENVIRONMENT_PROTECTION("环保", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    PRIVATE_EDUCATION("民办教育", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    HEALTHCARE("医疗健康", ListUtil.toList("生产线名称", "设备名称*", "规格型号", "数量", "单位", "供应商/销货方", "入账/购置日期", "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）", "发票号", "存放地点*", "其他")),
    ;

    private final String display;
    private final List<String> templateHeaderList;

    public static LeaseItemManagerLeaseItemType of(String name) {
        for (LeaseItemManagerLeaseItemType item : values()) {
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
