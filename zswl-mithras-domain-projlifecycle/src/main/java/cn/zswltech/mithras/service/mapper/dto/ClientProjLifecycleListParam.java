package cn.zswltech.mithras.service.mapper.dto;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientProjLifecycleListParam extends PageReq {

    private List<Long> clientIds;

}
