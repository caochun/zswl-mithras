package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.client.NormalSpouseApi;
import cn.zswltech.mithras.customer.application.client.api.NormalSpouseApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class NormalSpouseController implements NormalSpouseApi {
    @Resource
    private NormalSpouseApplicationService normalSpouseApplicationService;

    @Override
    public R<List<NormalSpouseSelectRSP>> select(@RequestBody @Valid NormalSpouseSelectREQ req) {
        return normalSpouseApplicationService.select(req);
    }

    @Override
    public R<Void> add(@RequestBody @Valid NormalSpouseAddREQ req) {
        return normalSpouseApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid NormalSpouseModifyREQ req) {
        return normalSpouseApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid NormalSpouseRemoveREQ req) {
        return normalSpouseApplicationService.remove(req);
    }

    @Override
    public R<PageR<NormalSpouseListRSP>> detail(@RequestBody @Valid NormalSpouseListREQ req) {
        return normalSpouseApplicationService.detail(req);
    }
}
