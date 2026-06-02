package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.projreview.ProjectClassify;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/19 11:29
 */
public enum AssetIndustryClassify implements PullDown {
    /**
     * 鼓励介入类
     */
    ENCOURAGE_INTERVENTION("鼓励介入类"),
    /**
     * 适度支持类
     */
    MODERATE_SUPPORT("适度支持类"),
    /**
     * 谨慎支持类
     */
    CAUTIOUS_SUPPORT("谨慎支持类"),
    ;

    private final String display;

    AssetIndustryClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    public static AssetIndustryClassify getByProjectClassify(ProjectClassify projectClassify) {
        if(projectClassify == null){
            return null;
        }
        switch (projectClassify){
            case ENCOURAGEMENT:
            case CONSTRUCTION_MACHINERY:
            case INTRA_GROUP_COLLABORATION:
                return ENCOURAGE_INTERVENTION;
            case MODERATE_SUPPORT:
                return MODERATE_SUPPORT;
            case CAUTIOUS:
                return CAUTIOUS_SUPPORT;
        }
        return null;
    }
}
