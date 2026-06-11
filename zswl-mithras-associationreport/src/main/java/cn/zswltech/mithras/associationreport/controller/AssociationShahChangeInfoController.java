package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationShahChangeInfoApi;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationShahChangeInfoService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationShahChangeInfo;

import java.util.List;

/**
* @description 股东股权信息一览表-股东变更记录
* @author hspcadmin
* @date 2025-08-25
*/
@RestController
public class AssociationShahChangeInfoController implements AssociationShahChangeInfoApi {

    @Resource
    private AssociationShahChangeInfoService associationShahChangeInfoService;

    @Override
    public R<Void> add(AssociationShahChangeInfoAddREQ req) {
        associationShahChangeInfoService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationShahChangeInfoModifyREQ req){
        associationShahChangeInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationShahChangeInfoListRSP>> list(AssociationShahChangeInfoListREQ req){
        Page<AssociationShahChangeInfo> data = associationShahChangeInfoService.list(req);
        List<AssociationShahChangeInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationShahChangeInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationShahChangeInfoRemoveREQ req){
        associationShahChangeInfoService.remove(req);
        return R.ok();
    }

}