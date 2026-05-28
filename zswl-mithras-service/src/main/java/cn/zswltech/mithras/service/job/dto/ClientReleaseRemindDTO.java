package cn.zswltech.mithras.service.job.dto;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Data
public class ClientReleaseRemindDTO {

    //客户Id
    private Long clientId;

    //管护人id
    private Long userId;
    //客户类型
    private String clientType;

    //ClientRemindContentEnum source
    private List<String> sourceList;

    //触发不通过利息
    private Long projEstablishId;

    //触发不通过评审
    private Long  projReviewId;
}
