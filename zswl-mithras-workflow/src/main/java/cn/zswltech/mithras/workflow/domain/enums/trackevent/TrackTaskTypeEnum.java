package cn.zswltech.mithras.workflow.domain.enums.trackevent;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 跟踪事项
 */
@AllArgsConstructor
@Getter
public enum TrackTaskTypeEnum implements PullDown {

    CONTRACT_FILE("合同文件后收",1),
    LEASE_FILE("租赁物资料后收",2),
    ASSET_ATTENTION_EVENT("资产关注事项",3),
    AFTER_LEASE_EVENT("租后事项跟踪",4),
    OTHER("其他",5),
    ;


    public final String display;
    public final int sort;

    @Override
    public String display() {
        return display;
    }

    public static TrackTaskTypeEnum find(String name) {
        for (TrackTaskTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
