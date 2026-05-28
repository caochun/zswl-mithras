package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceLibRemoveREQ;

/**
* @description 实体经济服务数据(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "实体经济服务数据(流程节点记录版本表)-接口")
public interface AssociationEntityEconomyServiceLibApi {

    @ApiOperation("新增实体经济服务数据(流程节点记录版本表)")
    @PostMapping("/association/entity/economy/service/lib/add")
    R<Void> add(@RequestBody @Valid AssociationEntityEconomyServiceLibAddREQ req);

    @ApiOperation("修改实体经济服务数据(流程节点记录版本表)")
    @PostMapping("/association/entity/economy/service/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationEntityEconomyServiceLibModifyREQ req);

    @ApiOperation("实体经济服务数据(流程节点记录版本表)列表")
    @PostMapping("/association/entity/economy/service/lib/list")
    R<PageR<AssociationEntityEconomyServiceLibListRSP>> list(@RequestBody @Valid AssociationEntityEconomyServiceLibListREQ req);

    @ApiOperation("删除实体经济服务数据(流程节点记录版本表)")
    @PostMapping("/association/entity/economy/service/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationEntityEconomyServiceLibRemoveREQ req);

}