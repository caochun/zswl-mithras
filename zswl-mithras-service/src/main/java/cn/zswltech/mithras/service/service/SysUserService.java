package cn.zswltech.mithras.service.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.RoleService;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgRoleDOMapper;
import cn.zswltech.gruul.dao.dal.entity.*;
import cn.zswltech.gruul.dao.dal.query.JobUserQuery;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.OrgJobVO;
import cn.zswltech.gruul.dao.dal.vo.OrgRoleVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.OrgUserRSP;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationBaseREQ;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.service.service.bo.UserOrgJobInfoBO;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotEmpty;

/**
 * @author junke
 */
@Slf4j
@Service
public class SysUserService implements CurrentUserOrgResolver, CurrentUserDataScopeResolver, CurrentUserResolver, UserBizDeptResolver, JobUserResolver {

    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource(name = "userServiceAPI")
    private UserService userService;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private RoleService roleService;
    @Resource
    private OrgService orgService;
    @Resource
    private Id2NameService id2NameService;

    public final static String RISK_MANAGER_IDS_ORDER_BY_DEPT_ID = "riskManagerIdsOrderByDeptId";


    private final Map<String, Long> DEPT_CODE_ID = new HashMap<>();

    private Map<Long, List<Long>> org2UserIdMap = new HashMap<>();

    public UserDO getSpecificUser(Long userId) {
        return userDOMapper.selectByPrimaryKey(userId);
    }

    public List<OrgDO> listAllDept() {
        Example example = new Example(OrgDO.class);
        return orgDOMapper.selectByExample(example);
    }

    public UserOrgJobInfoBO getUserOrgJobInfo(Long userId) {
        Example example = new Example(UserOrgJobDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<UserOrgJobDO> userOrgJobList = userOrgJobDOMapper.selectByExample(example);
        UserOrgJobInfoBO result = new UserOrgJobInfoBO();
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            result.setBizDept(false);
            result.setOrgJobMap(Collections.emptyMap());
            return result;
        }
        List<Long> orgIds = userOrgJobList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
        List<OrgDO> orgList = orgDOMapper.selectByIds(orgIds, null);
        Map<Long, OrgDO> orgMap = orgList.stream().collect(Collectors.toMap(OrgDO::getId, e -> e));
        boolean isBizDept = false;
        Map<String, List<OrgDO>> jobOrgMap = new HashMap<>();
        for (UserOrgJobDO userOrgJobDO : userOrgJobList) {
            OrgDO org = orgMap.get(userOrgJobDO.getOrgId());
            if (Objects.isNull(org)) {
                continue;
            }
            if (Objects.equals(org.getType(), OrgConstants.BUSINESS_DEPT)) {
                isBizDept = true;
            }
            List<OrgDO> list = jobOrgMap.get(userOrgJobDO.getJobCode());
            if (Objects.isNull(list)) {
                list = new LinkedList<>();
                jobOrgMap.put(userOrgJobDO.getJobCode(), list);
            }
            list.add(org);
        }
        result.setBizDept(isBizDept);
        result.setOrgJobMap(jobOrgMap);
        return result;
    }

    public List<OrgDO> listBizDept() {
        Example example = new Example(OrgDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 1);
        return orgDOMapper.selectByExample(example);
    }

    public List<OrgDO> listEffectBizDept() {
        List<OrgDO> list = this.listBizDept();
        list.removeIf(e -> Objects.equals(e.getState(), YesOrNoNumberEnum.NO.getCode()));
        return list;
    }

    public List<OrgDO> listBizDeptSort(String type){
        List<String> deptNameList = null;
        if(DashboardOperationBaseREQ.publicType.equals(type)){
            deptNameList = Arrays.asList("浙江业务部","公用事业业务部");
        }
        return orgDOMapper.listBizDeptSort(deptNameList);
    }

    public List<OrgDO> listOrgByJob(Long userId, String jobCode) {
        Example example = new Example(UserOrgJobDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("jobCode", jobCode);
        List<UserOrgJobDO> userOrgJobDOList = userOrgJobDOMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(userOrgJobDOList)) {
            return Collections.emptyList();
        }
        return orgDOMapper.selectByIds(userOrgJobDOList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList()), null);
    }


    /**
     * @return 返回null代表全部可访问, 返回空list代表真的没有一个部门
     * 2022-12-07改造 业务部门只有业务领导能看到全部门业务，项目经理只能看到自己的
     */
    public List<Long> canViewDeptIds() {
        return canViewDeptIds(AccountUtil.getLoginInfo());
    }

    @Override
    public Long currentUserId() {
        return AccountUtil.getLoginInfo().getId();
    }

    public List<Long> canViewDeptIds(AccountVO accountVO) {
        Long userId = accountVO.getId();
        UserVO userVO = userService.getUserInfoById(userId).getData();
        Map<Long, Set<String>> orgJobCodeMap = Optional.ofNullable(userVO.getJobsName()).orElse(new ArrayList<>())
                .stream().collect(Collectors.toMap(OrgJobVO::getOrgId, o -> o.getJobNames().stream().map(OrgJobVO.Job::getJobCode).collect(Collectors.toSet())));
        List<OrgDO> userDeptList = getUserDeptList(accountVO);
        List<Long> bizOrgIdList = new ArrayList<>();
        for (OrgDO orgDO : userDeptList) {
            if (OrgConstants.BUSINESS_DEPT == orgDO.getType()) {
                Set<String> curOrgJobCodeSet = orgJobCodeMap.get(orgDO.getId());
                if (curOrgJobCodeSet.contains(JobEnum.leaderincharge.name()) || curOrgJobCodeSet.contains(JobEnum.businesshead.name())) {
                    // 只有业务分管领导 和 业务负责人 能看到该部门情况
                    bizOrgIdList.add(orgDO.getId());
                }
            } else {
                log.info("当前用户非业务部门，可查看所有");
                return null;
            }
        }
        return bizOrgIdList;
    }


    /*
    /**
     * @return 返回null代表全部可访问；返回空list代表不能访问
     */
    /*public List<Long> canViewUserIds() {
        List<OrgDO> userDeptList = getUserDeptList();
        List<Long> bizOrgIdList = new ArrayList<>();
        for (OrgDO orgDO : userDeptList) {
            if (OrgConstants.BUSINESS_DEPT == orgDO.getType()) {
                bizOrgIdList.add(orgDO.getId());
            } else {
                log.info("当前用户非业务部门，可查看所有");
                return null;
            }
        }
        List<Long> userIds = new ArrayList<>();
        for (Long orgId : bizOrgIdList) {
            userIds.addAll(userOrgRoleDOMapper.selectUserIdByOrgIdAndRoleId(orgId, null));
        }
        return userIds;
    }*/
    public OrgDO currentUserBizDept() {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        //todo 岗位
//        List<Long> orgIds = userOrgRoleDOMapper.selectOrgIdsByUserId(userId);
        List<Long> orgIds = userOrgJobDOMapper.selectOrgIdByUserId(userId);
        List<OrgDO> orgDOS = orgDOMapper.selectByIds(orgIds, OrgConstants.BUSINESS_DEPT);
        if (!orgDOS.isEmpty()) {
            return orgDOS.get(0);
        }
        return null;
    }

    public OrgDO geBizDeptByOrgId(Long orgId) {
        OrgDO orgDOS = orgDOMapper.selectByPrimaryKey(orgId);
        return orgDOS;
    }

    public String currentUserName() {
        return getUserName(AccountUtil.getLoginInfo().getId());
    }

    public String getUserName(Long userId) {
        UserVO user = userService.getUserInfoById(userId).getData();
        return ObjectUtil.isNotEmpty(user) ? user.getUserName() : null;
    }

    public List<OrgDO> getUserBizDeptList() {
        List<OrgDO> orgList = this.getUserDeptList();
        if (CollectionUtil.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        return orgList.stream().filter(e -> Objects.equals(e.getType(), OrgConstants.BUSINESS_DEPT)).collect(Collectors.toList());
    }

    @Override
    public List<OrgDO> getUserDeptList() {
        return this.getUserDeptList(AccountUtil.getLoginInfo());
    }

    public List<OrgDO> getUserDeptList(AccountVO loginInfo) {
        Long userId = loginInfo.getId();
        return this.getSpecificUserDeptList(userId);
    }

    public List<UserDO> getUserByDeptId(Long aLong) {
        HashSet<Long> userIds = new HashSet<>();
        UserOrgJobDO userOrgJobDO = new UserOrgJobDO();
        userOrgJobDO.setOrgId(aLong);
        userOrgJobDOMapper.select(userOrgJobDO).forEach(userOrgJobDO1 -> {
            userIds.add(userOrgJobDO1.getUserId());
        });
        UserOrgRoleDO userOrgRoleDO = new UserOrgRoleDO();
        userOrgRoleDO.setOrgId(aLong);
        userOrgRoleDOMapper.select(userOrgRoleDO).forEach(userOrgRoleDO1 -> {
            userIds.add(userOrgRoleDO1.getUserId());
        });
        if (userIds.isEmpty()) {
            return ListUtil.empty();
        }

        Example example = new Example(UserDO.class);
        example.createCriteria()
                .andIn("id", userIds)
                .andEqualTo("status", 0);
        return userService.selectByExample(example);
    }

    public List<UserDO> getUserByDeptCode(String orgCode) {
        if (DEPT_CODE_ID.isEmpty()) {
            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
        }
        return getUserByDeptId(DEPT_CODE_ID.get(orgCode));
    }

    @Override
    public OrgDO getUserDept() {
        List<OrgDO> orgList = this.getUserDeptList();
        if (CollectionUtil.isEmpty(orgList)) {
            return null;
        }
        return orgList.get(0);
    }

    public List<OrgDO> getSpecificUserDeptList(Long userId) {
//        List<Long> orgIds = userOrgRoleDOMapper.selectOrgIdsByUserId(userId);
        List<Long> orgIds = userOrgJobDOMapper.selectOrgIdByUserId(userId);
        return orgDOMapper.selectByIds(orgIds, null);
    }

    public OrgDO getBizDeptByUserId(Long userId) {
        List<OrgDO> orgList = this.getSpecificUserDeptList(userId);
        if (CollectionUtil.isEmpty(orgList)) {
            return null;
        }
        for (OrgDO org : orgList) {
            if (Objects.equals(org.getType(), OrgConstants.BUSINESS_DEPT)) {
                return org;
            }
        }
        return null;
    }

    @Override
    public String getBizDeptNameByUserId(Long userId) {
        OrgDO orgDO = getBizDeptByUserId(userId);
        return Objects.isNull(orgDO) ? null : orgDO.getName();
    }

    public boolean currentUserIsSpecificDept(String... deptCodes) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        List<OrgDO> deptList = getSpecificUserDeptList(userId);
        for (OrgDO orgDO : deptList) {
            for (String deptCode : deptCodes) {
                if (deptCode.equals(orgDO.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Long getUserIdByOrgJob(Long orgId, String jobCode) {
        List<UserDO> userDOS = userService.jobUsers(orgId, jobCode);
        if (CollUtil.isNotEmpty(userDOS)) {
            return userDOS.get(0).getId();
        }
        return null;
    }

    public Set<Long> getUserIdsByRole(String roleCode) {
        RoleDO roleDO = new RoleDO();
        roleDO.setCode(roleCode);
        RoleDO roleRes = roleService.selectOne(roleDO);

        Example example = new Example(UserOrgRoleDO.class);
        example.createCriteria().andEqualTo("roleId", roleRes.getId());
        return userOrgRoleDOMapper.selectByExample(example).stream().map(UserOrgRoleDO::getUserId).collect(Collectors.toSet());
    }

    /**
     * 根据岗位code查询用户id
     *
     * @param jobCode
     * @return
     */
    public List<Long> jobUsers(Set<String> jobCode) {
        if (ObjectUtil.isEmpty(jobCode)) {
            return new ArrayList<>();
        }
        Example example = new Example(UserOrgJobDO.class);
        example.createCriteria().andIn("jobCode", jobCode);
        return userOrgJobDOMapper.selectByExample(example).stream()
                .map(UserOrgJobDO::getUserId).distinct().collect(Collectors.toList());
    }

    /**
     * 查询某个用户的岗位
     *
     * @param userId
     * @return
     */
    //todo 岗位
    public List<String> queryUserJobList(Long userId) {
        List<UserOrgJobDO> userOrgJobDOS = userOrgJobDOMapper.selectJobCodeByUserId(Collections.singletonList(userId));
        if (CollectionUtil.isNotEmpty(userOrgJobDOS)) {
            return userOrgJobDOS.stream().map(UserOrgJobDO::getJobCode).distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 根据岗位找人
     *
     * @param jobCode
     * @return
     */
    public List<Long> queryJobUserIds(String jobCode) {
        return Optional.ofNullable(userService.getUsersByjobcod(jobCode)).orElse(new ArrayList<>())
                .stream().map(UserDO::getId).distinct().collect(Collectors.toList());
    }

    public boolean currentUserIsSpecificJob(String... jobNames) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        return this.userIsSpecificJob(userId, jobNames);
    }


    public boolean currentUserIsSpecificJob(Long createdBy, String... jobNames) {
        Long userId;
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (loginInfo == null) {
            userId = createdBy;
        } else {
            userId = loginInfo.getId();
        }
        return this.userIsSpecificJob(userId, jobNames);
    }


    public boolean userIsSpecificJob(Long userId, String... jobNames) {
        List<String> jobs = this.queryUserJobList(userId);
        if (CollectionUtil.isEmpty(jobs)) {
            return false;
        }
        for (String jobName : jobNames) {
            if (jobs.contains(jobName)) {
                return true;
            }
        }
        return false;
    }

    public boolean userIsSpecificRole(Long userId, String... roleCodes) {
        List<OrgRoleVO> userOrgRoleDOS = userOrgRoleDOMapper.selectOrgAndRoleInfo(Collections.singletonList(userId));
        Set<Long> roleIds = userOrgRoleDOS.stream().map(OrgRoleVO::getRoleId).collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return false;
        }
        Example example = new Example(RoleDO.class);
        example.createCriteria().andIn("id", roleIds);
        Set<String> userRoleCodes = roleService.selectByExample(example).stream().map(RoleDO::getCode).collect(Collectors.toSet());
        for (String roleName : roleCodes) {
            if (userRoleCodes.contains(roleName)) {
                return true;
            }
        }
        return false;
    }

    public boolean currentUserIsBizDept() {
        List<OrgDO> orgList = this.getUserDeptList();
        if (CollectionUtil.isEmpty(orgList)) {
            return false;
        }
        for (OrgDO orgDO : orgList) {
            if (Objects.equals(orgDO.getType(), OrgConstants.BUSINESS_DEPT)) {
                return true;
            }
        }
        return false;
    }

    @Resource
    private UserOrgRoleDOMapper userOrgRoleDOMapper;//roleDOMapper可能有其他同名bean，启动时冲突

    public boolean currentUserIsSpecificRole(String... roleNames) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        List<OrgRoleVO> orgRoleVOS = userOrgRoleDOMapper.selectOrgAndRoleInfo(Arrays.asList(userId));
        if (CollectionUtil.isEmpty(orgRoleVOS)) {
            return false;
        }
        for (String roleName : roleNames) {
            for (OrgRoleVO orgRoleVO : orgRoleVOS) {
                if (orgRoleVO.getRoleName().equals(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<UserDO> listSpecificOrgJobUser(Long orgId, String jobCode) {
        JobUserQuery query = new JobUserQuery();
        query.setOrgId(orgId);
        query.setJob(jobCode);
        List<Long> longs = userOrgJobDOMapper.selectUserIdByOrgIdAndJobCode(query);
        if (ObjectUtil.isEmpty(longs)) {
            return Collections.emptyList();
        }
        return userDOMapper.selectByIds(longs);
    }

    public List<UserDO> listEffectSpecificOrgJobUser(Long orgId, String jobCode) {
        JobUserQuery query = new JobUserQuery();
        query.setOrgId(orgId);
        query.setJob(jobCode);
        List<Long> longs = userOrgJobDOMapper.selectUserIdByOrgIdAndJobCode(query);
        if (ObjectUtil.isEmpty(longs)) {
            return Collections.emptyList();
        }
        List<UserDO> userDOS = userDOMapper.selectByIds(longs);
        if (CollectionUtil.isEmpty(userDOS)) {
            return Collections.emptyList();
        }
        userDOS.removeIf(e -> Objects.equals(e.getStatus(), YesOrNoNumberEnum.YES.getCode()));
        if (ObjectUtil.isEmpty(longs)) {
            return Collections.emptyList();
        }
        return userDOS;
    }

    public Optional<String> getSpecificOrgLeader(Long orgId) {
        JobUserQuery query = new JobUserQuery();
        query.setOrgId(orgId);
        query.setJob("leaderincharge");
        List<Long> longs = userOrgJobDOMapper.selectUserIdByOrgIdAndJobCode(query);
        if (ObjectUtil.isEmpty(longs)) {
            return Optional.empty();
        }
        String userName = getUserName(longs.get(0));
        return Optional.ofNullable(userName);
    }

    public Optional<String> getMoneyManagerLeaderName() {
        JobUserQuery query = new JobUserQuery();
        query.setJob("moneymanagerleader");
        List<Long> longs = userOrgJobDOMapper.selectUserIdByOrgIdAndJobCode(query);
        if (longs.size() != 1) {
            return Optional.empty();
        }
        String userName = getUserName(longs.get(0));
        return Optional.ofNullable(userName);
    }

    /**
     * 判断是否管理员
     *
     * @return
     */
    public boolean adminAuth() {
        Long loginUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(-1L);
        SystemConfigDO systemConfigDO = systemConfigService.getConfig("adminAuthUserIdList").getData();
        if (Objects.isNull(systemConfigDO)) {
            // 没配置 只开给id = 3的管理员
            return Objects.equals(3L, loginUserId);
        }
        List<Long> adminUserIdList = Optional.ofNullable(JSONArray.parseArray(systemConfigDO.getConfigValue(), Long.class)).orElse(new ArrayList<>());
        return adminUserIdList.contains(loginUserId);
    }

    public List<UserDO> queryUserByAccounts(List<String> accounts) {
        if (CollectionUtil.isEmpty(accounts)) {
            return new ArrayList<>();
        }
        Example example = new Example(UserDO.class);
        example.createCriteria().andIn("account", accounts);
        return userDOMapper.selectByExample(example);
    }

    public Set<Long> getAllRiskControlManagerIds() {
        UserOrgJobDO userOrgJobDO = new UserOrgJobDO();
        userOrgJobDO.setJobCode(JobEnum.riskmanager.name());
        return userOrgJobDOMapper.select(userOrgJobDO).stream()
                .map(UserOrgJobDO::getUserId)
                .collect(Collectors.toSet());
    }

    //获取部门对应的风控经理
    public Map<Long, List<String>> getRiskManagerIdsOrderByDeptId() {
        SystemConfig systemConfig = SpringUtil.getBean(SystemConfigMapper.class)
                .selectOne(Wrappers.<SystemConfig>lambdaQuery()
                        .select(SystemConfig::getConfigValue)
                        .eq(SystemConfig::getConfigKey, RISK_MANAGER_IDS_ORDER_BY_DEPT_ID)
                        .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                        .last(StringUtil.mysqlLimitOne()));
        if (systemConfig == null){
            return new HashMap<>();
        }
        return JSON.parseObject(systemConfig.getConfigValue(), new TypeReference<Map<Long, List<String>>>() {
        });
    }


    // 部门id获取风控经理
    public Set<Long> getRiskControlManagerIdsByDeptCode(String deptCode) {
        Response<SystemConfigDO> specifyRiskManager = systemConfigService.getConfig("specifyRiskManager");
        if (specifyRiskManager.isSuccess()) {
            String configValue = specifyRiskManager.getData().getConfigValue();
            Map<String, List<String>> stringListMap = JSON.parseObject(configValue, new TypeReference<Map<String, List<String>>>() {
            });
            if (stringListMap.containsKey(deptCode)) {
                List<String> riskManagerList = stringListMap.get(deptCode);
                Set<Long> riskManagerIds = queryUserByAccounts(riskManagerList).stream().map(UserDO::getId)
                        .collect(Collectors.toSet());
                if (isNotEmpty(riskManagerIds)) {
                    return riskManagerIds;
                }
            }
        }
        return getAllRiskControlManagerIds();
    }

    /**
     * 付款核销流程-获取财务经理
     * 未找到对应用户则返回所有的财务经理
     */
    public Set<String> getFinancialManagerIdsByDeptCode(Long deptId) {
        Response<SystemConfigDO> paymentFinancialManager = systemConfigService.getConfig("paymentFinancialManager");
        if (paymentFinancialManager.isSuccess()) {
            String configValue = paymentFinancialManager.getData().getConfigValue();
            Map<String, List<String>> stringListMap = JSON.parseObject(configValue, new TypeReference<Map<String, List<String>>>() {
            });
            OrgDO orgDO = orgService.selectByPrimaryKey(deptId);
            if (orgDO != null && stringListMap.containsKey(orgDO.getCode())) {
                List<String> riskManagerList = stringListMap.get(orgDO.getCode());
                Set<String> riskManagerIds = queryUserByAccounts(riskManagerList).stream().map(UserDO::getId).map(String::valueOf)
                        .collect(Collectors.toSet());
                if (isNotEmpty(riskManagerIds)) {
                    return riskManagerIds;
                }
            }
        }
        UserOrgJobDO userOrgJobDO = new UserOrgJobDO();
        userOrgJobDO.setJobCode(JobEnum.financialmanager.name());
        return userOrgJobDOMapper.select(userOrgJobDO).stream()
                .map(UserOrgJobDO::getUserId)
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }

    public int countUserByDeptCode(String orgCode) {
        if (DEPT_CODE_ID.isEmpty()) {
            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
        }
        return countUserByDeptId(DEPT_CODE_ID.get(orgCode));
    }

    public int countUserByDeptId(Long aLong) {
        HashSet<Long> userIds = new HashSet<>();
        UserOrgJobDO userOrgJobDO = new UserOrgJobDO();
        userOrgJobDO.setOrgId(aLong);
        userOrgJobDOMapper.select(userOrgJobDO).forEach(userOrgJobDO1 -> {
            userIds.add(userOrgJobDO1.getUserId());
        });
        UserOrgRoleDO userOrgRoleDO = new UserOrgRoleDO();
        userOrgRoleDO.setOrgId(aLong);
        userOrgRoleDOMapper.select(userOrgRoleDO).forEach(userOrgRoleDO1 -> {
            userIds.add(userOrgRoleDO1.getUserId());
        });

        if (userIds.isEmpty()) {
            return 0;
        }
        Example example = new Example(UserDO.class);
        example.createCriteria()
                .andIn("id", userIds)
                .andEqualTo("status", 0);
        return userService.selectByExample(example).size();
    }

    public int countUserByRoleAndOrg(String orgCode) {
        if (DEPT_CODE_ID.isEmpty()) {
            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
        }
        Set<Long> userIds = new HashSet<>();
        if (orgCode != null) {
            Example example = new Example(UserOrgJobDO.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orgId", DEPT_CODE_ID.get(orgCode));
            userIds = userOrgJobDOMapper.selectByExample(example).stream().map(UserOrgJobDO::getUserId).collect(Collectors.toSet());
        }
        Example example = new Example(UserDO.class);
        Example.Criteria criteria = example.createCriteria()
                .andNotIn("account", Arrays.asList("admin", "readonly"))
                .andEqualTo("status", 0);
        if (isNotEmpty(userIds)) {
            criteria.andIn("id", userIds);
        }
        return userService.selectCountByExample(example);
    }

    public int countUserByDeptIds(Set<Long> ids) {
        HashSet<Long> userIds = new HashSet<>();
        Example example_1 = new Example(UserOrgJobDO.class);
        example_1.createCriteria()
                .andIn("orgId", ids);
        userOrgJobDOMapper.selectByExample(example_1).forEach(userOrgJobDO1 -> {
            userIds.add(userOrgJobDO1.getUserId());
        });
        Example example_2 = new Example(UserOrgJobDO.class);
        example_2.createCriteria()
                .andIn("orgId", ids);
        userOrgRoleDOMapper.selectByExample(example_2).forEach(userOrgRoleDO1 -> {
            userIds.add(userOrgRoleDO1.getUserId());
        });

        Example example = new Example(UserDO.class);
        example.createCriteria()
                .andIn("id", userIds)
                .andEqualTo("status", 0);
        return userService.selectByExample(example).size();
    }


    public List<String> getCurrentUserRoles() {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        List<OrgRoleVO> orgRoleVOS = userOrgRoleDOMapper.selectOrgAndRoleInfo(Arrays.asList(userId));
        if (CollectionUtil.isEmpty(orgRoleVOS)) {
            return Collections.emptyList();
        }
        Set<Long> roleIds = orgRoleVOS.stream().map(OrgRoleVO::getRoleId).collect(Collectors.toSet());
        Example example = new Example(RoleDO.class);
        example.createCriteria()
                .andIn("id", roleIds);
        List<RoleDO> roleDOS = roleService.selectByExample(example);
        return roleDOS.stream().map(RoleDO::getCode).collect(Collectors.toList());
    }

    public Long getOrgIdByCode(String orgCode) {
        if (DEPT_CODE_ID.isEmpty()) {
            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
        }
        return DEPT_CODE_ID.get(orgCode);
    }

    /**
     * 获取用户与所属机构
     * @param filterOrgCode 需要过滤的机构code
     * @return
     */
    public List<UserRSP> getUserList(List<String> filterOrgCode) {
        List<OrgDO> orgDOList = orgDOMapper.queryAll().stream().filter(f -> f.getLevel() != 1).collect(Collectors.toList());
        Map<Long, String> orgId2NameMap = Optional.of(orgDOList.stream().filter(f -> !filterOrgCode.contains(f.getCode()))
                .collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (m1, m2) -> m1))).orElse(new HashMap<>());
        Map<Long, List<Long>> userId2OrgMap = userOrgJobDOMapper.selectAll().stream().filter(f -> orgId2NameMap.containsKey(f.getOrgId())).collect(Collectors.groupingBy(
                UserOrgJobDO::getUserId, Collectors.mapping(UserOrgJobDO::getOrgId, Collectors.toList())));

        Set<Long> userIdList = userId2OrgMap.keySet();
        Map<Long, String> userId2Name = id2NameService.sysUserId2NameNotLimitSize(userId2OrgMap.keySet());
        return userIdList.stream().map(userId ->
                        new UserRSP(userId, userId2Name.get(userId), String.join(",", userId2OrgMap.get(userId).stream().map(orgId2NameMap::get).collect(Collectors.toSet()))))
                .collect(Collectors.toList());
    }


    /**
     * 构建机构用户树
     * @param filterOrgCode 需要过滤的机构
     * @return
     */
    public List<OrgUserRSP> getOrgUserList(List<String> filterOrgCode) {
        org2UserIdMap = userOrgRoleDOMapper.selectAll().stream().collect(Collectors.groupingBy(
                UserOrgRoleDO::getOrgId, Collectors.mapping(UserOrgRoleDO::getUserId, Collectors.toList())));

        List<OrgDO> orgListRSP = orgService.getOrgList().getData();
        // 过滤机构
        if(CollectionUtil.isNotEmpty(filterOrgCode)) {
            orgListRSP = orgListRSP.stream().filter(f -> !filterOrgCode.contains(f.getCode())).collect(Collectors.toList());
        }
        List<OrgUserRSP> orgUserList = convertOrg(orgListRSP);
        supplyOrgUser(orgUserList);
        return orgUserList;
    }

    /**
     * 处理字段信息，返回结果集
     * @param orgDOList
     * @return
     */
    private List<OrgUserRSP> convertOrg(List<OrgDO> orgDOList) {
        if (orgDOList == null) {
            return null;
        }
        List<OrgUserRSP> orgUserList = new ArrayList<>();
        for (OrgDO orgDO : orgDOList) {
            OrgUserRSP orgRSP = new OrgUserRSP();
            orgRSP.setId(orgDO.getId());
            orgRSP.setOrgCode(orgDO.getCode());
            orgRSP.setName(orgDO.getName());
            orgRSP.setType("org");
            orgRSP.setChildren(convertOrg(orgDO.getChildren()));
            orgUserList.add(orgRSP);
        }
        return orgUserList;
    }

    /**
     * 为机构下补充用户
     * @param orgUserList
     */
    private void supplyOrgUser(List<OrgUserRSP> orgUserList) {
        for (OrgUserRSP orgUserRSP : orgUserList) {
            if (!orgUserRSP.getType().equals("org")) {
                continue;
            }
            List<OrgUserRSP> userList = getUserListByOrgCode(orgUserRSP.getId());
            List<OrgUserRSP> children = orgUserRSP.getChildren();
            if (children != null) {
                supplyOrgUser(children);
                children.addAll(userList);
            } else {
                orgUserRSP.setChildren(userList);
            }
        }
    }

    private List<OrgUserRSP> getUserListByOrgCode(Long orgId) {
        UserQuery query = new UserQuery();
        query.setOrgId(orgId);
        List<Long> idList = org2UserIdMap.get(orgId);
        if (CollectionUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<UserDO> userDOS = userDOMapper.selectByIds(idList);
        return userDOS.stream().map(user -> {
            OrgUserRSP rsp = new OrgUserRSP();
            rsp.setAccount(user.getAccount());
            rsp.setType("user");
            rsp.setId(user.getId());
            rsp.setName(user.getUserName());
            rsp.setAccount(user.getAccount());
            return rsp;
        }).collect(Collectors.toList());
    }
}
