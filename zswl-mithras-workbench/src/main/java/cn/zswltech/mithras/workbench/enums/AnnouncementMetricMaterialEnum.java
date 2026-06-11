package cn.zswltech.mithras.workbench.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/22 10:02
 */
public enum AnnouncementMetricMaterialEnum implements PullDown, IMaterialsTypeConvert {
    /**
     * 公告图片
     */
    IMAGE("公告图片");
    private String dispaly;

    AnnouncementMetricMaterialEnum(String dispaly) {
        this.dispaly = dispaly;
    }

    @Override
    public String businessModule() {
        return "ANNOUNCEMENT";
    }

    @Override
    public String display() {
        return dispaly;
    }
}
