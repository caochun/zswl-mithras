package cn.zswltech.mithras.service.adapter.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationUserScopeService;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RiskControlConcentrationUserScopeServiceAdapter implements RiskControlConcentrationUserScopeService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public Set<Long> bizUserIds() {
        Set<Long> userIds = new HashSet<>();
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<OrgDO> orgList = sysUserService.getUserDeptList();
        for (OrgDO org : orgList) {
            if (ObjectUtil.equal(org.getType(), OrgConstants.BUSINESS_DEPT)) {
                Long leaderId = sysUserService.getUserIdByOrgJob(org.getId(), JobEnum.businesshead.name());
                if (ObjectUtil.equal(leaderId, currentUserId)) {
                    userIds.addAll(sysUserService.getUserByDeptCode(org.getCode()).stream().map(UserDO::getId).collect(Collectors.toList()));
                } else {
                    userIds.add(currentUserId);
                }
            } else if (ObjectUtil.equal(org.getType(), OrgConstants.LEADERSHIP)) {
                return null;
            }
        }
        return userIds;
    }
}
