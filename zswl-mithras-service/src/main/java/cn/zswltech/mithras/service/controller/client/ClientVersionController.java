package cn.zswltech.mithras.service.controller.client;

import cn.zswltech.mithras.api.client.ClientVersionApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.InfoModule;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.customer.application.lib.client.impl.ClientVersionServiceImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户变更版本
 *
 * @author wangchuanhao
 * @date 2022/6/23 2:39 PM
 */
@RestController
public class ClientVersionController implements ClientVersionApi {

    @Resource
    private ClientVersionServiceImpl clientVersionService;

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        if(StringUtils.isEmpty(req.getModule())){
            req.setModule(BusinessModuleEnum.CLIENT.name());
        }
        PageR<CommonVersionListRSP> data = clientVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ClientVersionDiffREQ req) {
        //版本权限控制，非主办不可查询
        CommonVersionDiffRSP commonVersionDiffRSP = clientVersionService.comparePreVersion(req.getId());
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            Map<String, Boolean> moduleChanged = new HashMap<>();
//            Map<String, List> oldData = new HashMap<>();
//            Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
//            moduleChanged.put(InfoModule.CORP_COMMERCE.name(), commonVersionDiffRSP.getModuleChanged().get(InfoModule.CORP_COMMERCE.name()));
//            oldData.put(InfoModule.CORP_COMMERCE.name(), commonVersionDiffRSP.getOldData().get(InfoModule.CORP_COMMERCE.name()));
//            newData.put(InfoModule.CORP_COMMERCE.name(), commonVersionDiffRSP.getNewData().get(InfoModule.CORP_COMMERCE.name()));
//            commonVersionDiffRSP.setModuleChanged(moduleChanged);
//            commonVersionDiffRSP.setNewData(newData);
//            commonVersionDiffRSP.setOldData(oldData);
//        }
        return R.ok(commonVersionDiffRSP);
    }

}
