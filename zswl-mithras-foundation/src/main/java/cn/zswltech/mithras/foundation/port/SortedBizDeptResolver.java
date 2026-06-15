package cn.zswltech.mithras.foundation.port;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;

import java.util.List;

/**
 * Resolves business departments in dashboard display order.
 */
public interface SortedBizDeptResolver {

    List<OrgDO> listBizDeptSort(String type);
}
