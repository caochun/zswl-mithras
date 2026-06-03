package cn.zswltech.mithras.dashboard.application.guanyuandata;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/9/14
 * @description 观远BI-业务合同情况汇总表数据集（字段不全，只取了一些需要使用的，后续要使用再加）
 */
@Data
public class BusinessContractSummaryDTO implements GuanYuanColumnPopulate {
    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 省份名称
     */
    private String provinceDisplay;

    /**
     * 业务模式
     */
    private String bizTypeDisplay;

    /**
     * 融资额（元）
     */
    private BigDecimal totalAmount;

    /**
     * 已收本金（元）
     */
    private BigDecimal collectPrincipal;

    @Override
    public void populate(Map<String, String> map) {
        this.contractCode = Optional.ofNullable(map.get("合同编号")).orElse(null);
        // 观远配置的数据集中字段名称携带了空格！！！#@¥%……&*（……%¥#@%……&*（）
        this.provinceDisplay = Optional.ofNullable(map.get("地区 ")).orElse(null);
        // 观远配置的数据集中字段名称携带了空格！！！#@¥%……&*（……%¥#@%……&*（）
        this.bizTypeDisplay = Optional.ofNullable(map.get("业务模式 ")).orElse(null);
        this.totalAmount = Optional.ofNullable(map.get("融资额")).map(e -> new BigDecimal(e).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO);
        this.collectPrincipal = Optional.ofNullable(map.get("已收本金")).map(e -> new BigDecimal(e).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO);
    }
}
