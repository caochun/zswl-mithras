package cn.zswltech.mithras.others.client;

import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author yibin
 */
@Slf4j
@ActiveProfiles("dev")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleTest {

    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private SysUserService sysUserService;

    @Test
    public void test() {
        Long deptId = null;
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(1L);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        err(isNull(deptId), "原主办的部门不存在");
        Long finalDeptId = deptId;
        Long originDeptLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
//        Long targetDeptLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.businesshead.name());
//        Long targetDivisionLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.leaderincharge.name());
    }
}
