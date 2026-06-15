package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

/**
 * Resolves business departments and their user counts for shared calculations.
 */
public interface BizDeptResolver {

    List<OrgDO> listBizDept();

    int countUserByDeptId(Long deptId);
}
