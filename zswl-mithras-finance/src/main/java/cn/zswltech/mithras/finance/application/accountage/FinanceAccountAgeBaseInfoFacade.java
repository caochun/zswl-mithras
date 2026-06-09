package cn.zswltech.mithras.finance.application.accountage;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.finance.application.accountage.api.FinanceAccountAgeBaseInfoApplicationService;
import cn.zswltech.mithras.dto.finance.accountage.*;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceAccountAgeBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.finance.service.accountage.FinanceAccountAgeBaseInfoService;
import cn.zswltech.mithras.finance.service.accountage.FinanceAccountAgeItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 帐龄主表
* @author vico
* @date 2024-09-10
*/
@Service
public class FinanceAccountAgeBaseInfoFacade implements FinanceAccountAgeBaseInfoApplicationService {

    @Resource
    private FinanceAccountAgeBaseInfoService financeAccountAgeBaseInfoService;
    @Resource
    private FinanceAccountAgeItemService financeAccountAgeItemService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<Long> add(FinanceAccountAgeBaseInfoAddREQ req) {
        return R.ok(financeAccountAgeBaseInfoService.add(req));
    }

    @Override
    public R<Void> close(FinanceAccountAgeBaseInfoCloseREQ req){
        financeAccountAgeBaseInfoService.close(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceAccountAgeBaseInfoListRSP>> list(FinanceAccountAgeBaseInfoListREQ req){
        Page<FinanceAccountAgeBaseInfo> data = financeAccountAgeBaseInfoService.list(req);
        List<FinanceAccountAgeBaseInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceAccountAgeBaseInfoListRSP.class);
        if(ObjectUtil.isNotEmpty(list)) {
            Map<Long, String> longStringMap = id2NameService.sysUserId2Name(list.stream().map(FinanceAccountAgeBaseInfoListRSP::getCreateBy).collect(Collectors.toSet()));
            list.forEach(e -> {
                e.setCreateByName(longStringMap.get(e.getCreateBy()));
            });
        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<FinanceAccountAgeBaseInfoDetailRSP> detail(@Valid FinanceAccountAgeBaseInfoDetailREQ req) {
        FinanceAccountAgeBaseInfo byId = financeAccountAgeBaseInfoService.getById(req.getId());
        FinanceAccountAgeBaseInfoDetailRSP rsp = BeanUtil.copyProperties(byId, FinanceAccountAgeBaseInfoDetailRSP.class);
        if(ObjectUtil.isNotEmpty(byId)) {
            rsp.setCreateByName(id2NameService.sysUserId2NameSingle(rsp.getCreateBy()));
        }
        return R.ok(rsp);
    }

    @Override
    public R<Void> remove(FinanceAccountAgeBaseInfoRemoveREQ req){
        financeAccountAgeBaseInfoService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> effect(@Valid FinanceAccountAgeBaseInfoRemoveREQ req) {
        financeAccountAgeBaseInfoService.effect(req);
        return R.ok();
    }

    @Override
    public R<FinanceAccountAgeCountRSP> count(@Valid FinanceAccountAgeBaseInfoDetailREQ req) {
        return R.ok(financeAccountAgeItemService.count(req));
    }

}