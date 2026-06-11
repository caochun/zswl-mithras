package cn.zswltech.mithras.foundation.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum CacheEnum {
    GLOBAL_TOAST_TEXT("global_toast_text", 5 * 60 * 1000L),

    /**
     * 流程提交锁 模块+主表id
     * effect_submit_lock:CLIENT_1
     */
    EFFECT_SUBMIT_LOCK("effect_submit_lock:%s_%s", -1L),

    /**
     * 合同起租提醒记录锁
     */
    CONTRACT_RENT_REMIND_LOCK("contract_rent_remind_lock", 2 * 1000L),

    /**
     * 立项创建提交锁
     */
    PROJ_ESTABLISH_ADD_LOCK("proj_establish_add_lock", 3 * 1000),

    /**
     * 集团授信立项创建提交锁
     */
    GROUP_CREDIT_ESTABLISH_ADD_LOCK("group_credit_establish_add_lock", 3 * 1000),

    /**
     * 租金催收发送email锁
     */
    RENT_COLLECTION_SEND_EMAIL_LOCK("rent_collection_send_email_lock:%s", -1L),

    /**
     * 操作项目评审时，集团授信发起的项目评审要校验剩余可用额度，加锁
     */
    GROUP_CREDIT_REVIEW_REMAIN_AMOUNT_LOCK("gcr_remain_amount_lock:%s", -1),

    /**
     * 操作项目定价时，集团授信发起的项目评审要校验剩余可用额度，加锁
     */
    GROUP_CREDIT_PRICING_REMAIN_AMOUNT_LOCK("gcr_remain_pricing_amount_lock:%s", -1),

    CLIENT_USER_REF_CREATE_LOCK("client_user_ref_create_lock", -1L),

    /**
     * 流动性管理指标计算时，避免数据集发生变化，加锁
     */
    LIQUIDITY_MANAGE_INDICATOR_CALCULATE_LOCK("liquidity_manage_indicator_calculate_lock", -1L),

    /**
     * 拨备计提本月风险金计算时，避免数据集发生变化，加锁
     */
    KPI_PROVISION_DETAIL_CALCULATE_LOCK("kpi_provision_detail_calculate_lock", -1L),
    ;

    private final String code;
    /**
     * 毫秒
     */
    private final long expire;

    public static String getCacheKey(CacheEnum cacheEnum, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mithras");
        stringBuilder.append(":");
        stringBuilder.append(cacheEnum.getCode());
        if (StrUtil.isNotBlank(suffix)) {
            stringBuilder.append(":");
            stringBuilder.append(suffix);
        }
        return stringBuilder.toString();
    }

    public String buildKey(Object... args) {
        return String.format("mithras:" + this.code, args);
    }
}
