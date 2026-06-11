package cn.zswltech.mithras.foundation.metadata;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/25 13:58
 */
public interface PullDown {
    String name();
    String display();
    /**
     * 用于兼容非name形式的枚举
     * @return
     */
    default String valueKey() {
        return name();
    }

    default int level() {
        return 0;
    }

    default Integer state() {
        return 0;
    }

    default String childSelectName() {return null;}
}
