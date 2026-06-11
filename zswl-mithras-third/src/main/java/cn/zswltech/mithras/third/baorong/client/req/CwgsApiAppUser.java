package cn.zswltech.mithras.third.baorong.client.req;

import lombok.Data;

@Data
public class CwgsApiAppUser {
    private String operator;//操作员编号 财务公司分配的编号：ZJZSRZZLYXGS
    private String organ;//机构财务公司分配的机构编号：ZJZSRZZL
}