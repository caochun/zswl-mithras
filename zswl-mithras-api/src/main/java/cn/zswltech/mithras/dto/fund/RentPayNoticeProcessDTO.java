package cn.zswltech.mithras.dto.fund;

import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/13/11:14
 * @description
 */
@Data
public class RentPayNoticeProcessDTO {
    /**
     * 部门key
     */
    private String deptCode;

    /**
     * 用户ID列表
     */
    private List<Long> userIds;
}
