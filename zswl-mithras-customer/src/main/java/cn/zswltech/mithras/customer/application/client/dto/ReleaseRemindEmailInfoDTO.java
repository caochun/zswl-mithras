package cn.zswltech.mithras.customer.application.client.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description: 邮件封装使用
 * @Author: huangping
 * @Date: 2025/11/26  21:24
 * @Version: 1.0
 */
@Data
public class ReleaseRemindEmailInfoDTO {

    private List<ClientReleaseRemindDTO> clientReleaseRemindDTOList;

    private Map<Long, String> clientId2Name;
}
