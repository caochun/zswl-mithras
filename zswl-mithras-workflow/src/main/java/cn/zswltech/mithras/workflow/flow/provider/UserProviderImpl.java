package cn.zswltech.mithras.workflow.flow.provider;

import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.extension.provider.UserProvider;
import cn.zswltech.flow.core.extension.req.UserReq;
import cn.zswltech.flow.core.extension.resp.UserResp;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.tkmybatis.Page;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.workflow.flow.port.WorkflowNameQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户数据交互
 *
 * @author wangchuanhao
 * @date 2022/6/4 12:08 PM
 */
@Service
@Slf4j
public class UserProviderImpl implements UserProvider {

    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private WorkflowNameQueryPort workflowNameQueryPort;

    /**
     * 获取某个节点的审批人数据
     * @return
     */
    @Override
    public List<String> queryUserIdList(UserReq userReq) {
        // 如果没有获取到审批人 返回空列表
        UserDefineTypeEnum userDefineTypeEnum = UserDefineTypeEnum.getByType(userReq.getUserDefineType());
        if (UserDefineTypeEnum.ROLE.equals(userDefineTypeEnum)
                || UserDefineTypeEnum.DEPT.equals(userDefineTypeEnum)
                || UserDefineTypeEnum.PROCESS_START_ROLE.equals(userDefineTypeEnum)
                || UserDefineTypeEnum.PROCESS_START_DEPT.equals(userDefineTypeEnum)) {
            UserQuery userQuery = new UserQuery();
            userQuery.setPageSize(Integer.MAX_VALUE);
            userQuery.setStatus(0);
            userQuery.setActive(Boolean.TRUE);
            if (UserDefineTypeEnum.ROLE.equals(userDefineTypeEnum) || UserDefineTypeEnum.PROCESS_START_ROLE.equals(userDefineTypeEnum)) {
                userQuery.setRoleId(Long.valueOf(userReq.getRoleId()));
            } else {
                userQuery.setOrgId(Long.valueOf(userReq.getDeptId()));
            }
            Page<UserVO> userVOPage = userServiceAPI.queryUserSys(userQuery);
            return userVOPage.getContents().stream().map(UserVO::getId).map(String::valueOf).collect(Collectors.toList());
        } else if (UserDefineTypeEnum.PROCESS_START_JOB.equals(userDefineTypeEnum)) {
            // 根据job找用户
            return Optional.ofNullable(userServiceAPI.getUsersByjobcod(userReq.getJobCode())).orElse(new ArrayList<>())
                    .stream().map(UserDO::getId).distinct().map(String::valueOf).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public UserResp queryUser(String userId) {
        Response<UserVO> userVOResponse = userServiceAPI.getUserInfoById(Long.valueOf(userId));
        if (userVOResponse.isSuccess() && userVOResponse.getData() != null) {
            UserResp userResp = new UserResp();
            userResp.setUserId(userId);
            userResp.setName(userVOResponse.getData().getUserName());
            return userResp;
        }
        return null;
    }

    @Override
    public Map<String, UserResp> queryUserMap(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new HashMap<>();
        }
        Map<Long, String> userNameMap = workflowNameQueryPort.sysUserId2Name(userIds.stream().map(Long::valueOf).collect(Collectors.toSet()));
        Map<String, UserResp> userRespMap = new HashMap<>();
        userNameMap.entrySet().forEach(e -> {
            userRespMap.put(String.valueOf(e.getKey()), new UserResp(String.valueOf(e.getKey()), e.getValue()));
        });
        return userRespMap;
    }

}
