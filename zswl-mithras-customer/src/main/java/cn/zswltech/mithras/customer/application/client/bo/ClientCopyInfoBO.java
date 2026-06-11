package cn.zswltech.mithras.customer.application.client.bo;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientCopyInfoBO {
    private Long clientId;
    private Long currentUserId;
    private Long dbUserId;
    private List<InfoModule> moduleList;
    private ClientLevelEnum clientLevel;
    private Integer isReleased;
    private Client client;
}
