package cn.zswltech.mithras.blackgray.service.external.remote;

import lombok.Data;

/**
 * @Author:fengming.dai
 */
@Data
public class GroupData {
    /**
     * 集团户子集团总数
     */
    Integer child_group_membe;

    /**
     * 实控人
     */
    String controller_name;

    /**
     * 实控人类型
     */
    Integer controller_shattribute;

    /**
     * 核心成员数量
     */
    Integer core_member;

    /**
     * 集团户 id
     */
    String id;

    /**
     * 上市公司数量
     */
    Integer listed_company_total;

    /**
     * 集团户名称
     */
    String title;

    /**
     * 集团户成员总数
     */
    Integer total_member;

}
