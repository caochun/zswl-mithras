package cn.zswltech.mithras.dto.fund;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 10:07
 */
@Data
public class AccountInfo {
    private String accountName;
    private String depositBank;
    private String account;
    private Boolean mainAccount;
}
