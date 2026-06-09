package cn.zswltech.mithras.finance.application.accountage;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.finance.application.accountage.api.FinanceAccountAgeItemApplicationService;
import cn.zswltech.mithras.dto.finance.accountage.*;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceAccountAgeItem;
import cn.zswltech.mithras.finance.service.accountage.FinanceAccountAgeItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 帐龄-详情表
* @author vico
* @date 2024-09-10
*/
@Service
public class FinanceAccountAgeItemFacade implements FinanceAccountAgeItemApplicationService {

    @Resource
    private FinanceAccountAgeItemService financeAccountAgeItemService;

    @Override
    public R<Void> add(FinanceAccountAgeItemAddREQ req) {
        financeAccountAgeItemService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(FinanceAccountAgeItemModifyREQ req){
        financeAccountAgeItemService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceAccountAgeItemListRSP>> list(FinanceAccountAgeItemListREQ req){
        Page<FinanceAccountAgeItem> data = financeAccountAgeItemService.list(req);
        List<FinanceAccountAgeItemListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceAccountAgeItemListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(FinanceAccountAgeItemRemoveREQ req){
        financeAccountAgeItemService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> regeneration(@Valid FinanceAccountAgeItemRemoveREQ req) {
        financeAccountAgeItemService.regeneration(req);
        return R.ok();
    }

    @Override
    public R<Void> sendRemote(@Valid FinanceAccountAgeItemRemoveREQ req) {
        financeAccountAgeItemService.sendRemote(req);
        return R.ok();
    }

}