package cn.zswltech.mithras.blackgray.client.dto;

import lombok.Data;

import java.util.List;

/**
 * 集团户成员列表查询接口
 * @Author:fengming.dai
 */
@Data
public class OuterGroupMemberResponse {
    /**
     * 公司名称
     */
    String company_name;

    /**
     * 公司标签（区分A股、新三板、发债等）
     */
    List<String> company_tags;

    /**
     * 社会统一信用代码
     */
    String credit_code;

    /**
     * 企业编码
     */
    String enterprise_code;

    /**
     * 成立日期
     */
    String establishment_date;

    /**
     * 控股比例(%)
     */
    String holding_ratio;

    /**
     * 子集团ID
     */
    List<String> id_child_group;

    /**
     * 所属行业
     */
    String industry;

    /**
     * 所属行业代码
     */
    String industry_code;

    /**
     * 法人代表
     */
    String legal_representative;

    /**
     * 成员级别(controller-实控成员、core-核心成员、ordinary-普通成员、other-其他人员等)
     */
    String member_level;

    /**
     * 节点所在层级
     */
    Integer  node_level;

    /**
     * 经营状态
     */
    String reg_status;

    /**
     * 注册资金（万元）
     */
    String registered_capital;

    /**
     * 子集团所属的板块
     */
    List<String> sector;

    /**
     * 子集团所属的板块ID
     */
    List<String> sector_code;

    /**
     * 省份
     */
    String state;


    /**
     * 子集团户名称
     */
    List<String> title_child_group;

    

}
