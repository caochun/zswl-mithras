package cn.zswltech.mithras.dto.client;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @description 外部鉴权获取用户信息
 * @author vico
 * @date 2023-09-11
 */
@Data
@ApiModel("外部鉴权获取用户信息")
public class AuthUserInfoRSP {

    private Long id;

    private Date gmtCreate;

    private String account;

    private String userName;

    private Date expiration;

    private Integer status;

    private String phone;

    private String email;

}
