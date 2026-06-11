package cn.zswltech.mithras.foundation.persistence.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限查询dto
 *
 * @author wangchuanhao
 * @date 2022/12/7 12:56 PM
 */
@Data
public class BaseAuthDTO {

    private List<Long> deptIdList;
    private Boolean isBizUser;
    private Long currentUserId;

}
