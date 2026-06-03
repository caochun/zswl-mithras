package cn.zswltech.mithras.third.yunhu.infrastructure.client.res;

import lombok.Data;

/**
 * @author zhouning
 * @date 2025/04/22
 * @description
 */
@Data
public abstract class YunHuBasicRes {
    private Integer code;
    private String message;
    private Boolean success;
}
