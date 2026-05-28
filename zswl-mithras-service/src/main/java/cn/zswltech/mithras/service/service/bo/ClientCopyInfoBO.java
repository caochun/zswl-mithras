package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
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
