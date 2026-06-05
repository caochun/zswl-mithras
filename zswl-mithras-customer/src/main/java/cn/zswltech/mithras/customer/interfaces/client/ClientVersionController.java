package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionListREQ;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.ClientVersionApi;
import cn.zswltech.mithras.customer.application.client.api.ClientVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ClientVersionController implements ClientVersionApi {
    @Resource
    private ClientVersionApplicationService clientVersionApplicationService;

    @Override
    public R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req) {
        return clientVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ClientVersionDiffREQ req) {
        return clientVersionApplicationService.comparePreVersion(req);
    }
}
