package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationEntityEconomyServiceLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationEntityEconomyServiceLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationEntityEconomyServiceLib;

import java.util.List;

/**
* @description 实体经济服务数据(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationEntityEconomyServiceLibController implements AssociationEntityEconomyServiceLibApi {

    @Resource
    private AssociationEntityEconomyServiceLibService associationEntityEconomyServiceLibService;

    @Override
    public R<Void> add(AssociationEntityEconomyServiceLibAddREQ req) {
        associationEntityEconomyServiceLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationEntityEconomyServiceLibModifyREQ req){
        associationEntityEconomyServiceLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationEntityEconomyServiceLibListRSP>> list(AssociationEntityEconomyServiceLibListREQ req){
        Page<AssociationEntityEconomyServiceLib> data = associationEntityEconomyServiceLibService.list(req);
        List<AssociationEntityEconomyServiceLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationEntityEconomyServiceLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationEntityEconomyServiceLibRemoveREQ req){
        associationEntityEconomyServiceLibService.remove(req);
        return R.ok();
    }

}