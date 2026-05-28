package cn.zswltech.mithras.service.overdue.domain.acl;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 09:08
 */
@Data
public class ClientRole {
    private String clientId;
    private Long contractId;
    private String role;
}
