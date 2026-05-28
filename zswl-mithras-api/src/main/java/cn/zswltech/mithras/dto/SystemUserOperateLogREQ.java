package cn.zswltech.mithras.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemUserOperateLogREQ extends PageReq {
    private Long userId;
}
