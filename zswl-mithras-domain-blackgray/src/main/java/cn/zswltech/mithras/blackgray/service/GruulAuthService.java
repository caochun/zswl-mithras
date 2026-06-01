package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 权限模块的防腐接口
 *
 * @author ao.li
 * <br/>created on 2022/8/1 16:50
 */
public interface GruulAuthService {

    /**
     * 根据roleIds 批量获取该角色下用户accounts
     *
     * @param roleIds
     * @return key->roleCode,value->UserVO
     */
    Map<Long, List<UserVO>> getUserByRoleIds(List<Long> roleIds);


    /**
     * 根据roleCodes获取该角色下用户accounts
     *
     * @param roleCodes
     * @return
     */
    Map<String, List<UserVO>> getUserByRoleCodes(List<String> roleCodes);

    /**
     * 根据账户名获取用户名
     *
     * @return
     */
    String getAccountById(Long id);

    /**
     * 根据机构标识批量获取机构的名称map
     *
     * @param orgCodes 机构标识列表
     * @return 机构名称map，key-机构标识，value-机构名称
     */
    Map<String, String> batchGetOrgNameMap(List<String> orgCodes);

    /**
     * 根据机构标识批量获取机构对象的map
     *
     * @param orgCodes 机构标识列表
     * @return key-机构标识，value-机构对象
     */
    Map<String, OrgDO> getOrgMap(List<String> orgCodes);

}
