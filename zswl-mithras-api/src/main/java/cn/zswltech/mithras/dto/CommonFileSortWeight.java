package cn.zswltech.mithras.dto;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author dingqi
 * @date 2022/12/8
 * @description
 */
public abstract class CommonFileSortWeight {
    protected abstract String sortKey();

    public abstract long createTimestamp();

    public int calculateKeyWeight() {
        int weight = 0;
        String key = this.sortKey();
        if (StrUtil.isBlank(key)) {
            return weight;
        }
        // 排序规则：无任何数字序号的 > 不带"-"的数字序号的 > 带"-"的数字序号的
        // eg: 现在有1 2 1-1 1-2 2-1 2-2 ，排序权重由小到大为 1 1-1 1-2 2 2-1 2-2 如果有不带任何序号的 则权重比1还要小
        weight = this.calculateScore(this.getTargetStr(key));
        return weight;
    }

    private String getTargetStr(String sourceStr) {
        int legalRange = sourceStr.length();
        for (int i = 0; i < sourceStr.length(); i++) {
            char c = sourceStr.charAt(i);
            if (!((c >= '0' && c <= '9') || c == '-')) {
                legalRange = i;
                break;
            }
        }
        return legalRange == 0 ? "" : sourceStr.substring(0, legalRange);
    }

    private int calculateScore(String str) {
        String[] array = str.split("-");
        int score = 0;
        int numberScore = 1000000;
        int key = 100;
        for (String s : array) {
            // TODO 暂时解决数字过长的情况 之后找产品确认 虽然理论上也不可能出现4位数字的排序?
            boolean legalNumberFlag = NumberUtil.isNumber(s) && s.length() <= 3;
            if (legalNumberFlag) {
                score = score + Integer.parseInt(s) * numberScore;
            }
            // 因为是按照"-"分割的，每个数字末尾默认带一个"-"
            // 遇到"-"进行数字分数降级，说明这是一个下级分类，得分应该比上级要少
            numberScore = numberScore / key;
            // 默认1分
            score = score + 1;
        }
        return score;
    }
}
