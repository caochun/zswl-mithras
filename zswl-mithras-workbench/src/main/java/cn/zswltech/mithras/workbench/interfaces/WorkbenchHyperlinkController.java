package cn.zswltech.mithras.workbench.interfaces;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchHyperlinkApi;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkDto;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkListReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkModifyReq;
import cn.zswltech.mithras.workbench.application.WorkbenchHyperlinkService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@RestController
public class WorkbenchHyperlinkController implements WorkbenchHyperlinkApi {

    @Resource
    private WorkbenchHyperlinkService workbenchHyperlinkService;

    @Override
    public R<Void> modify(WorkbenchHyperlinkModifyReq req) {
        if (ObjectUtil.isEmpty(req.getUserId())) {
            req.setUserId(AccountUtil.getLoginInfo().getId());
        }
        workbenchHyperlinkService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<WorkbenchHyperlinkDto>> list(WorkbenchHyperlinkListReq req) {
        if (ObjectUtil.isEmpty(req.getUserId())) {
            req.setUserId(AccountUtil.getLoginInfo().getId());
        }
        return R.ok(workbenchHyperlinkService.list(req));
    }
}