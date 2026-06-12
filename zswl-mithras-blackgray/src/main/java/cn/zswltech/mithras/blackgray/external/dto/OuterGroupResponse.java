package cn.zswltech.mithras.blackgray.external.dto;

import lombok.Data;

import java.util.List;

/**
 * 集团户查询接口返回
 * @Author:fengming.dai
 */
@Data
public class OuterGroupResponse {
    Boolean status;

    Integer code;

    String message;

    List<GroupData> data;
}
