package cn.zswltech.mithras.service.enums.overdue;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/30 14:09
 */
public enum CollectionActionFileType implements PullDown {

    /**
     * 催收函及相关函件
     */
    COLLECTION("催收函及相关函件",1),

    /**
     * 附件
     */
    ENCLOSURE("附件",2);

    private final String display;
    @Getter
    private final int sort;

    CollectionActionFileType(String display,int sort) {
        this.display = display;
        this.sort = sort;
    }

    @Override
    public String display() {
        return display;
    }

}
