package cn.zswltech.mithras.foundation.util;

import cn.zswltech.mithras.dto.CommonFileSortWeight;

import java.util.Comparator;

/**
 * @author dingqi
 * @date 2022/12/8
 * @description
 */
public class CommonFileSortComparator implements Comparator<CommonFileSortWeight> {
    @Override
    public int compare(CommonFileSortWeight o1, CommonFileSortWeight o2) {
        int weight1 = o1.calculateKeyWeight();
        int weight2 = o2.calculateKeyWeight();
        if (weight1 != weight2) {
            return weight1 - weight2;
        }
        // 如果按照名称排序权重一致则按照创建时间排序
        return Long.compare(o1.createTimestamp(), o2.createTimestamp());
//        return (int) (o1.createTimestamp() - o2.createTimestamp());
    }
}
