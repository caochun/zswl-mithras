package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationShahStorInfoApi;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationShahStorInfoService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfo;

import java.util.List;

/**
* @description 股东股权信息一览表-股东股权信息
* @author hspcadmin
* @date 2025-08-25
*/
@RestController
public class AssociationShahStorInfoController implements AssociationShahStorInfoApi {

    @Resource
    private AssociationShahStorInfoService associationShahStorInfoService;

    @Override
    public R<Void> add(AssociationShahStorInfoAddREQ req) {
        associationShahStorInfoService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationShahStorInfoModifyREQ req){
        associationShahStorInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationShahStorInfoListRSP>> list(AssociationShahStorInfoListREQ req){
        Page<AssociationShahStorInfo> data = associationShahStorInfoService.list(req);
        List<AssociationShahStorInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationShahStorInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationShahStorInfoRemoveREQ req){
        associationShahStorInfoService.remove(req);
        return R.ok();
    }

}