package cn.zswltech.mithras.dto;

import lombok.Data;

@Data
public class AccountReq {
    private Long id;
    private String account;
    private String expiration;
}