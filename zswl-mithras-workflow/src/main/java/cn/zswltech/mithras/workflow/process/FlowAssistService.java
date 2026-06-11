package cn.zswltech.mithras.workflow.process;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.biz.service.impl.OrgServiceImpl;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 * @author yibin
 */
@Service
public class FlowAssistService {
    @Resource
    private OrgServiceImpl orgService;
    @Resource(name = "userServiceAPI")
    private UserService userService;

    /**
     * 法律合规部
     */
    public static final String FLHGB = "FLHGB_ZCBQ";
    public static final String FLHGB_DESC = "法律合规部";

    public static final String YYGLB = "YYGLB";
    public static final String YYGLB_DESC = "运营管理部";

    public Long deptLeader(String deptCode, String deptDesc, String jobCode) {
        OrgDO condition = new OrgDO();
        condition.setCode(deptCode);
        OrgDO flhgb = orgService.selectOne(condition);
        err(isNull(flhgb), "流程审批中需要的部门信息【" + deptDesc + "】未找到");
        List<UserDO> users = userService.jobUsers(flhgb.getId(), jobCode);
        if (CollUtil.isNotEmpty(users)) {
            return users.get(0).getId();
        }
        return null;
    }
}
