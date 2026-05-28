package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.relatedenterprise.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人关联企业-接口")
public interface CorpRelatedEnterpriseApi {

    @ApiOperation("关联企业新增")
    @PostMapping("/corp/related/enterprise/add")
    R<Void> add(@RequestBody @Valid CorpRelatedEnterpriseAddREQ req);

    @ApiOperation("关联企业变更")
    @PostMapping("/corp/related/enterprise/modify")
    R<Void> modify(@RequestBody @Valid CorpRelatedEnterpriseModifyREQ req);

    @ApiOperation("关联企业删除")
    @PostMapping("/corp/related/enterprise/remove")
    R<Void> remove(@RequestBody @Valid CorpRelatedEnterpriseRemoveREQ req);

    @ApiOperation("关联企业列表")
    @PostMapping("/corp/related/enterprise/list")
    R<PageR<CorpRelatedEnterpriseListRSP>> list(@RequestBody @Valid CorpRelatedEnterpriseListREQ req);
}
