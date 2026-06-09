package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

public interface ClientUserDeptPort {
    List<OrgDO> getSpecificUserDeptList(Long userId);
}
