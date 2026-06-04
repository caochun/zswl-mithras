package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 债权转让类型
 *
 * @author wangchuanhao
 * @date 2022/11/1 1:46 PM
 */
@AllArgsConstructor
@Getter
public enum ZrType implements PullDown {

    /**
     * 有追
     */
    yz("有追"),
    /**
     * 无追
     */
    wz("无追"),
    ;

    public String display;

    @Override
    public String display() {
        return display;
    }

    public static String getNameByDisplay(String display) {
        for (ZrType zrEnum : ZrType.values()) {
            if (zrEnum.getDisplay().equals(display)) {
                return zrEnum.name();
            }
        }
        return null;
    }

    public static ZrType of(String name) {
        for (ZrType zrType : ZrType.values()) {
            if (zrType.name().equals(name)) {
                return zrType;
            }
        }
        return null;
    }

}
