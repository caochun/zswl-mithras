package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationEntityEconomyServiceApi;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationEntityEconomyServiceService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationEntityEconomyService;

/**
* @description 实体经济服务数据表
* @author vico
* @date 2025-04-18
*/
@RestController
public class AssociationEntityEconomyServiceController implements AssociationEntityEconomyServiceApi {

    @Resource
    private AssociationEntityEconomyServiceService associationEntityEconomyServiceService;

    @Override
    public R<Void> add(AssociationEntityEconomyServiceAddREQ req) {
        associationEntityEconomyServiceService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationEntityEconomyServiceModifyREQ req){
        associationEntityEconomyServiceService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationEntityEconomyServiceListRSP>> list(AssociationEntityEconomyServiceListREQ req){
        Page<AssociationEntityEconomyService> data = associationEntityEconomyServiceService.list(req);
        List<AssociationEntityEconomyServiceListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationEntityEconomyServiceListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationEntityEconomyServiceRemoveREQ req){
        associationEntityEconomyServiceService.remove(req);
        return R.ok();
    }

}