package cn.zswltech.mithras.customer.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientViewByRiskControl {

    private String clientId;      // 客户id,对应client表的id
    private String certNumber;    // 证件号码,对应client表的usc_code
    private String clientName;    // 客户名称,对应client表的client_name

}
