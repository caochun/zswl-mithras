package cn.zswltech.mithras.system.service;

import cn.zswltech.gruul.biz.service.impl.OrgServiceImpl;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import org.springframework.stereotype.Service;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author yibin
 */
@Service
public class FlowAssistService {

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
        OrgDO flhgb = SpringContextHolder.getBean(OrgServiceImpl.class).selectOne(condition);
        err(isNull(flhgb), "流程审批中需要的部门信息【" + deptDesc + "】未找到");
        return SpringContextHolder.getBean(SysUserService.class).getUserIdByOrgJob(flhgb.getId(), jobCode);
    }
}
