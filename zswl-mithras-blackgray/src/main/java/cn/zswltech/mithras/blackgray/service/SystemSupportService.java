package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName
 * @Description 系统支持
 * @Author jackerhe
 * @Date 2023/12/5 10:21 上午
 * @Version 1.0
 **/
@Service
@Slf4j
@Deprecated // 弃用，通过ThreadContext + GruulAuthService来实现该类中的功能
public class SystemSupportService {

    @Resource
    private UserService userService;
    @Resource
    private OrgService orgService;

    public static final String ZSJK_CODE = "10000079";



//    public Boolean isBusiness(List<OrgDO> orgDOList) {
//        for(OrgDO orgDO : orgDOList) {
//            if (orgDO.getCode().equals(ZSJK_CODE)){
//                return false;
//            }
//        }
//        return true;
//    }

//    public Boolean isBusiness() {
//        UserVO data = this.getUserVo();
//        List<Long> orgList = data.getOrgRolesName().stream().map(OrgRoleVO.OrgVO::getOrgId).collect(Collectors.toList());
//        List<OrgDO> orgDos = orgService.selectByIds(orgList, 1);
//        return isBusiness(orgDos);
//    }

//    public String getUserCode(Long userId) {
//        UserDO userDO = userService.selectByPrimaryKey(userId);
//        return userDO == null ? null : userDO.getAccount();
//    }

//    public String getOrgName(String code){
//        OrgDO orgDO = new OrgDO();
//        orgDO.setCode(code);
//        OrgDO orgDO1 = orgService.selectOne(orgDO);
//        return ObjectUtil.isNotEmpty(orgDO1) ? orgDO1.getName() : null;
//    }

    // 仅剩单测中还有用到
    @Deprecated // 弃用，通过ThreadContext.getUser()来替换
    public UserVO getUserVo(){
      return userService.getUserInfo().getData();
    }

//    public OrgDO getUserOrg() {
//        UserVO userVo = getUserVo();
//        List<Long> orgList = userVo.getOrgRolesName().stream().map(OrgRoleVO.OrgVO::getOrgId).collect(Collectors.toList());
//        List<OrgDO> orgDos = orgService.selectByIds(orgList, 1);
//        if (CollectionUtil.isNotEmpty(orgDos)) {
//            return orgDos.get(0);
//        }
//        return null;
//    }
}
