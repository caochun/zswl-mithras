package cn.zswltech.mithras.service.enums.workbench;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;

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
