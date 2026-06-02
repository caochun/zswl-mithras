package cn.zswltech.mithras.third.service.jk.res;

import lombok.Data;

/**
 * @author dingqi
 * @date 2023/8/7
 * @description
 */
@Data
public abstract class JinKongBasicRes {
    private Integer code;
    private String message;
    private Boolean success;
}
