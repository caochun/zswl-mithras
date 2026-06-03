package cn.zswltech.mithras.service.service.workbench.Initjob;

import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchHyperlink;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchShortcuts;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.workbench.application.WorkbenchHyperlinkService;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutsService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/17 14:49
 */
@Component
public class Initjob {
    @Resource
    private UserService userService;

    @XxlJob("workbenchInitJob")
    @Transactional(rollbackFor = Throwable.class)
    public void workbenchInitJob() {
        System.out.println("workbenchInitJob");
        Set<Long> userIds = userService.selectAll().stream().map(UserDO::getId).collect(Collectors.toSet());
        initWorkbenchHyperlink(userIds);
        initWorkbenchShortcuts(userIds);
    }

    @Resource
    private WorkbenchHyperlinkService workbenchHyperlinkService;
    @Resource
    private WorkbenchShortcutsService workbenchShortcutsService;

    private void initWorkbenchHyperlink(Set<Long> userIds) {
        List<WorkbenchHyperlink> workbenchHyperlinks = new ArrayList<>();
        for (Long userId : userIds) {
            WorkbenchHyperlink zhongdeng = new WorkbenchHyperlink();
            zhongdeng.setUserId(userId);
            zhongdeng.setName("中登网");
            zhongdeng.setAddress("https://www.zhongdengwang.org.cn");
            workbenchHyperlinks.add(zhongdeng);

            WorkbenchHyperlink qcc = new WorkbenchHyperlink();
            qcc.setUserId(userId);
            qcc.setName("企查查");
            qcc.setAddress("https://www.qcc.com");
            workbenchHyperlinks.add(qcc);
        }
        workbenchHyperlinkService.saveBatch(workbenchHyperlinks);
    }

    @Resource
    private SysUserService sysUserService;

    private void initWorkbenchShortcuts(Set<Long> userIds) {
        Set<Long> dbUserIds = workbenchShortcutsService.list().stream().map(WorkbenchShortcuts::getUserId).collect(Collectors.toSet());
        List<WorkbenchShortcuts> shortcuts = new ArrayList<>();
        for (Long userId : userIds) {
            if (dbUserIds.contains(userId)) {
                continue;
            }
            if (sysUserService.userIsSpecificRole(userId, "XMJL")) {
                WorkbenchShortcuts workbenchShortcuts = new WorkbenchShortcuts();
                workbenchShortcuts.setUserId(userId);
                workbenchShortcuts.setMenuIds("[6,8,12,20]");
                workbenchShortcuts.setFunctionFlag(1);
                shortcuts.add(workbenchShortcuts);
            } else if (sysUserService.userIsSpecificRole(userId, "COMPREHENSIVE_MANAGEMENT")) {
                WorkbenchShortcuts workbenchShortcuts = new WorkbenchShortcuts();
                workbenchShortcuts.setUserId(userId);
                workbenchShortcuts.setMenuIds("[6,208,11]");
                workbenchShortcuts.setFunctionFlag(1);
                shortcuts.add(workbenchShortcuts);
            } else if (sysUserService.userIsSpecificRole(userId, "JSSYB_BDS", "HGJCYWB_BDS", "JXHJGYWB_BDS", "JCSSYWB_BDS", "LLWLTD_BDS", "XJZZHXJJTD_BDS", "JTYSYWB_BDS", "GGSY_BDS")) {
                WorkbenchShortcuts workbenchShortcuts = new WorkbenchShortcuts();
                workbenchShortcuts.setUserId(userId);
                workbenchShortcuts.setMenuIds("[6,98,11]");
                workbenchShortcuts.setFunctionFlag(1);
                shortcuts.add(workbenchShortcuts);
            }
        }
        workbenchShortcutsService.saveBatch(shortcuts);
    }
}
