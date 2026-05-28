package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationEntityEconomyServiceRemoveREQ;

/**
* @description 实体经济服务数据表
* @author vico
* @date 2025-04-18
*/
@Api(tags = "实体经济服务数据表-接口")
public interface AssociationEntityEconomyServiceApi {

    @ApiOperation("新增实体经济服务数据表")
    @PostMapping("/association/entity/economy/service/add")
    R<Void> add(@RequestBody @Valid AssociationEntityEconomyServiceAddREQ req);

    @ApiOperation("修改实体经济服务数据表")
    @PostMapping("/association/entity/economy/service/modify")
    R<Void> modify(@RequestBody @Valid AssociationEntityEconomyServiceModifyREQ req);

    @ApiOperation("实体经济服务数据表列表")
    @PostMapping("/association/entity/economy/service/list")
    R<PageR<AssociationEntityEconomyServiceListRSP>> list(@RequestBody @Valid AssociationEntityEconomyServiceListREQ req);

    @ApiOperation("删除实体经济服务数据表")
    @PostMapping("/association/entity/economy/service/remove")
    R<Void> remove(@RequestBody @Valid AssociationEntityEconomyServiceRemoveREQ req);

}