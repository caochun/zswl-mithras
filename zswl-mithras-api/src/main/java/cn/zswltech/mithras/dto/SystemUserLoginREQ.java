package cn.zswltech.mithras.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhouning
 * @date 2025/04/28
 * @description
 */

@Data
public class SystemUserLoginREQ {
    @NotNull
    private String account;
    @NotNull
    private String password;
    @NotNull
    private String tempRandom;
}
