package cn.zswltech.mithras.service.application.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.DisplayGroupDO;
import cn.zswltech.gruul.dao.dal.entity.DisplayMenuTreeDO;
import cn.zswltech.gruul.web.api.controller.LoginController;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsMaintainReq;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchShortcutBaseInfo;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchShortcuts;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutsApplicationService;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutBaseInfoService;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutsService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Service
public class WorkbenchShortcutsFacade implements WorkbenchShortcutsApplicationService {

    @Resource
    private LoginController loginController;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private WorkbenchShortcutsService workbenchShortcutsService;
    @Resource
    private WorkbenchShortcutBaseInfoService workbenchShortcutBaseInfoService;

    @Override
    public R<List<WorkbenchShortcutsListRsp>> list() {
        Response<DisplayMenuTreeDO> displayMenuTreeDOResponse = loginController.menuTree(httpServletRequest);
        Map<String, WorkbenchShortcutBaseInfo> shortcutsMap = new HashMap<>();
        for (WorkbenchShortcutBaseInfo base : workbenchShortcutBaseInfoService.list()) {
            if (ObjectUtil.isNotEmpty(base.getFunctionCode())) {
                shortcutsMap.put(base.getFunctionCode(), base);
            }
        }
        DisplayMenuTreeDO data = displayMenuTreeDOResponse.getData();
        //DisplayGroupDO
        if(ObjectUtil.isEmpty(data)){
            return R.ok();
        }
        List<WorkbenchShortcutsListRsp> rsps = new ArrayList<>();
        WorkbenchShortcuts workbenchShortcuts = workbenchShortcutsService.getOne(Wrappers.<WorkbenchShortcuts>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(AccountUtil.getLoginInfo().getId()), WorkbenchShortcuts::getUserId, AccountUtil.getLoginInfo().getId())
                .orderByDesc(WorkbenchShortcuts::getId)
                .last(StringUtil.mysqlLimitOne()));
        List<Long> longs = null;
        if(ObjectUtil.isNotEmpty(workbenchShortcuts) && ObjectUtil.isNotEmpty(workbenchShortcuts.getMenuIds())){
            longs = JSON.parseArray(workbenchShortcuts.getMenuIds(), Long.class);
        }
        Set<Long> set = new HashSet<>();
        if(longs != null){
            set.addAll(longs);
        }

        //封装权限类
        List<DisplayGroupDO> menuTree = data.getMenuTree();
        menuTree.forEach(menu -> {
            buildRspRecursion(menu, rsps, set, shortcutsMap, menu.getIcon());
        });
        return R.ok(rsps);
    }

    @Override
    public R<Void> add(@Valid WorkbenchShortcutsMaintainReq req) {
        WorkbenchShortcuts workbenchShortcuts = new WorkbenchShortcuts();
        WorkbenchShortcuts one = workbenchShortcutsService.getOne(Wrappers.<WorkbenchShortcuts>lambdaQuery()
                .eq(WorkbenchShortcuts::getUserId, AccountUtil.getLoginInfo().getId())
                .orderByDesc(WorkbenchShortcuts::getId)
                .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isNotEmpty(one)){
            workbenchShortcuts.setId(one.getId());
        }
        workbenchShortcuts.setUserId(AccountUtil.getLoginInfo().getId());
        workbenchShortcuts.setMenuIds(JSON.toJSONString(req.getMenuIds()));
        workbenchShortcuts.setFunctionFlag(YesOrNoNumberEnum.YES.getCode());
        workbenchShortcutsService.saveOrUpdate(workbenchShortcuts);
        return R.ok();
    }

    private void buildRspRecursion(DisplayGroupDO displayGroupDO, List<WorkbenchShortcutsListRsp> rsps, Set<Long> set,  Map<String,
            WorkbenchShortcutBaseInfo> shortcutsMap, String icon){
        WorkbenchShortcutsListRsp rsp;
        if(ObjectUtil.isNotEmpty(displayGroupDO.getPath())){
            rsp = new WorkbenchShortcutsListRsp();
            rsp.setMenuId(displayGroupDO.getMenuId());
            rsp.setPath(displayGroupDO.getPath());
            workbenchShortcutBaseInfoService.shortcutSpecialHandle(displayGroupDO.getCode(), rsp);
            rsp.setMenuName(displayGroupDO.getMenuName());
            if(ObjectUtil.isNotEmpty(shortcutsMap.get(displayGroupDO.getCode()))){
                if(ObjectUtil.isNotEmpty(shortcutsMap.get(displayGroupDO.getCode()).getShortCode())){
                    rsp.setPath(String.format("%s?%s", rsp.getPath(), shortcutsMap.get(displayGroupDO.getCode()).getShortCode()));
                }
                rsp.setMenuName(shortcutsMap.get(displayGroupDO.getCode()).getName());
            }
            rsp.setIcon(icon);
            if(ObjectUtil.isNotEmpty(shortcutsMap.get(displayGroupDO.getCode())) && ObjectUtil.isNotEmpty(shortcutsMap.get(displayGroupDO.getCode()).getIcon())){
                rsp.setIcon(shortcutsMap.get(displayGroupDO.getCode()).getIcon());
            }
            if(set.size() > 0){
                rsp.setIsCollect(set.contains(displayGroupDO.getMenuId()) ? 1 : 0);
            } else {
                rsp.setIsCollect(ObjectUtil.isNotEmpty(shortcutsMap.get(displayGroupDO.getCode())) ?
                        shortcutsMap.get(displayGroupDO.getCode()).getShortcutType() : 0);
            }
            rsps.add(rsp);
        }
        if(ObjectUtil.isNotEmpty(displayGroupDO.getChildren())){
            displayGroupDO.getChildren().forEach(child -> {
                DisplayGroupDO childDO = JSON.toJavaObject(child, DisplayGroupDO.class);
                buildRspRecursion(childDO, rsps, set, shortcutsMap, icon);
            });
        }
    }


}
