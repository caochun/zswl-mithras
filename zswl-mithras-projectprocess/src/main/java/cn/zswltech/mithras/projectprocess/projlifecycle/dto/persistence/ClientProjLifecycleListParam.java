package cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

import java.util.List;

@Data
public class ClientProjLifecycleListParam extends PageReq {

    private List<Long> clientIds;

}
