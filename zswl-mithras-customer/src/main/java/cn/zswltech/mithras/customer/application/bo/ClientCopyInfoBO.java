package cn.zswltech.mithras.customer.application.bo;

import cn.zswltech.mithras.customer.domain.enums.InfoModule;
import cn.zswltech.mithras.customer.domain.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientAuthority;
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
