package cn.zswltech.mithras.liquidity.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum LiquidityColorEnum implements PullDown {
    /**
     *
     */
    BLACK("黑色"),
    ORANGE("橙色"),
    RED("红色"),
    ;

    private String display;

    public static LiquidityColorEnum findByDisplay(String display) {
        for (LiquidityColorEnum item : values()) {
            if (item.getDisplay().equals(display)) {
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
