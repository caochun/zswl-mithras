package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

public interface UserBizDeptInfoResolver {

    OrgDO getBizDeptByUserId(Long userId);
}
