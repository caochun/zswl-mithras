package cn.zswltech.mithras.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author zhouning
 * @date 2025/04/28
 * @description
 */

@Data
public class SystemUserAuthCodeREQ {
    @NotNull
    private String account;
    @NotNull
    private String password;
}
