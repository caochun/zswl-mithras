package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationSeniorExecutiveInfoApi;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationSeniorExecutiveInfoService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationSeniorExecutiveInfo;


/**
* @description 高管信息一览表
* @author hspcadmin
* @date 2025-08-26
*/
@RestController
public class AssociationSeniorExecutiveInfoController implements AssociationSeniorExecutiveInfoApi {

    @Resource
    private AssociationSeniorExecutiveInfoService associationSeniorExecutiveInfoService;

    @Override
    public R<Void> add(AssociationSeniorExecutiveInfoAddREQ req) {
        associationSeniorExecutiveInfoService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationSeniorExecutiveInfoModifyREQ req){
        associationSeniorExecutiveInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationSeniorExecutiveInfoListRSP>> list(AssociationSeniorExecutiveInfoListREQ req){
        Page<AssociationSeniorExecutiveInfo> data = associationSeniorExecutiveInfoService.list(req);
        List<AssociationSeniorExecutiveInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationSeniorExecutiveInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationSeniorExecutiveInfoRemoveREQ req){
        associationSeniorExecutiveInfoService.remove(req);
        return R.ok();
    }

}