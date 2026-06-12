package cn.zswltech.mithras.blackgray.service.impl;

import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.RoleDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgRoleDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.RoleDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgRoleDO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限模块的防腐实现
 *
 * @author ao.li
 * <br/>created on 2022/8/1 16:50
 */
@Service
@Slf4j
public class GruulAuthServiceImpl implements GruulAuthService {


    @Resource
    private RoleDOMapper roleDOMapper;

    @Resource
    private UserOrgRoleDOMapper userOrgRoleDOMapper;

    @Resource
    private UserService userServiceImpl;

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private OrgDOMapper orgDOMapper;

    @Override
    public Map<String, List<UserVO>> getUserByRoleCodes(List<String> roleCodes) {
        Example example = new Example(RoleDO.class);
        example.createCriteria().andIn("code", roleCodes);
        example.selectProperties("id", "code");
        Map<Long, String> roleIdCodeMaps = roleDOMapper.selectByExample(example).stream().collect(Collectors.toMap(RoleDO::getId, RoleDO::getCode, (k1,k2)->k1));

        if ( roleIdCodeMaps.isEmpty()){
            return Collections.emptyMap();
        }
        Map<Long, List<UserVO>> roleIdUserMap = getUserByRoleIds(new ArrayList<>(roleIdCodeMaps.keySet()));

        return roleIdUserMap.entrySet().stream().collect(Collectors.toMap(t -> roleIdCodeMaps.get(t.getKey()), Map.Entry::getValue, (k1,k2)->k1));
    }

    @Override
    public Map<Long, List<UserVO>> getUserByRoleIds(List<Long> roleIds) {
        Example example = new Example(UserOrgRoleDO.class);
        example.createCriteria().andIn("roleId", roleIds);
        List<UserOrgRoleDO> userOrgRoleDOList = userOrgRoleDOMapper.selectByExample(example);
        Set<Long> userIds = userOrgRoleDOList.stream().map(UserOrgRoleDO::getUserId).collect(Collectors.toSet());
        Map<Long, UserVO> userMap = userServiceImpl.getUserInfoByIds(new ArrayList<>(userIds))
                .stream().collect(Collectors.toMap(UserDO::getId, Function.identity(), (k1,k2)->k1));

        Map<Long, List<UserVO>> res = userOrgRoleDOList.stream()
                .collect(Collectors.groupingBy(UserOrgRoleDO::getRoleId,
                        Collectors.mapping(t -> userMap.get(t.getUserId()), Collectors.toList())));
        return res;
    }

    @Override
    public String getAccountById(Long id) {
        if (id == null) {
            return null;
        }
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        return userDO == null ? null : userDO.getAccount();
    }

    @Override
    public Map<String, String> batchGetOrgNameMap(List<String> orgCodes) {
        Map<String, OrgDO> orgMap = getOrgMap(orgCodes);
        Map<String, String> orgNameMap = new HashMap<>(orgMap.size());
        for (Map.Entry<String, OrgDO> entry : orgMap.entrySet()) {
            orgNameMap.put(entry.getKey(), entry.getValue().getName());
        }

        return orgNameMap;
    }

    @Override
    public Map<String, OrgDO> getOrgMap(List<String> orgCodes) {
        if (CollectionUtils.isEmpty(orgCodes)) {
            return new HashMap<>(0);
        }
        Example example = new Example(OrgDO.class);
        example.createCriteria().andIn("code", orgCodes);
        List<OrgDO> orgDOS = orgDOMapper.selectByExample(example);

        return orgDOS.stream().collect(Collectors.toMap(OrgDO::getCode, orgDO -> orgDO, (o1, o2) -> o2));
    }



}
