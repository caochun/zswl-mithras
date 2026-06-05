package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRSP;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.ZhongdengInfoApi;
import cn.zswltech.mithras.customer.application.client.api.ZhongdengInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ZhongdengInfoController implements ZhongdengInfoApi {
    @Resource
    private ZhongdengInfoApplicationService zhongdengInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid ZhongdengInfoAddREQ req) {
        return zhongdengInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid ZhongdengInfoModifyREQ req) {
        return zhongdengInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<ZhongdengInfoRSP>> list(@RequestBody @Valid ExternalPageREQ req) {
        return zhongdengInfoApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ZhongdengInfoRemoveREQ req) {
        return zhongdengInfoApplicationService.remove(req);
    }
}
