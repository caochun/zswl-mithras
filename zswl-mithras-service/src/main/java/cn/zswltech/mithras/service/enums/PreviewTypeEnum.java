package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2022-08-23
 **/
public enum PreviewTypeEnum implements PullDown {
    ONLYOFFICE("onlyoffice"),
    IMAGE("图片"),
    VIDEO("视频"),
    UNKNOWN("未知");
    public String display;
    PreviewTypeEnum(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
