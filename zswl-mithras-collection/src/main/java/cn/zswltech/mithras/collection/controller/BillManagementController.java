package cn.zswltech.mithras.collection.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.collection.BillManagementApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.collection.mapper.model.BillManagement;
import cn.zswltech.mithras.collection.application.facade.BillManagementApplicationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
* @description 票据管理表
* @author vico
* @date 2023-06-05
*/
@RestController
public class BillManagementController implements BillManagementApi {

    @Resource
    private BillManagementApplicationService billManagementService;

    @Override
    public R<Void> add(BillManagementAddREQ req) {
        billManagementService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(BillManagementModifyREQ req){
        billManagementService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<BillManagementListRSP>> list(BillManagementListREQ req){
        Page<BillManagement> data = billManagementService.list(req);
        List<BillManagementListRSP> list = BeanUtil.copyToList(data.getRecords(), BillManagementListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(BillManagementRemoveREQ req){
        billManagementService.remove(req);
        return R.ok();
    }

}
