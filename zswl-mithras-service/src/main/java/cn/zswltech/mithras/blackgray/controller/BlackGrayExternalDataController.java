package cn.zswltech.mithras.blackgray.controller;


import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.external.*;
import cn.zswltech.mithras.blackgray.service.BlackGrayExternalDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BlackGrayExternalDataController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/6 2:19 下午
 * @Version 1.0
 **/

@Api(tags = "黑灰名单库-查询外部数据接口")
@RestController
public class BlackGrayExternalDataController {
    @Resource(name = "blackGrayHSExternalDataService")
    private BlackGrayExternalDataService blackGrayExternalDataService;

    @ApiOperation("黑灰名单模糊查询企业信息")
    @PostMapping("/public/black/query/vague/enterprise")
    public R<List<VagueEnterpriseSearchRSP>> vagueEnterprise(@RequestBody VagueEnterpriseSearchREQ req) {
        return R.ok(blackGrayExternalDataService.vagueEnterpriseSearch(req));
    }

    @ApiOperation("所属企业")
    @PostMapping("/public/black/query/affiliated/enterprise")
    public R<AffiliatedEnterpriseSearchRSP> affiliatedEnterpriseSearch(@RequestBody AffiliatedEnterpriseSearchREQ req) {
        return R.ok(blackGrayExternalDataService.affiliatedEnterpriseSearch(req));
    }


    @ApiOperation("查询下属企业")
    @PostMapping("/public/black/query/associated/enterprise")
    public R<List<AssociatedEnterpriseSearchRSP>> associatedEnterpriseSearch(@RequestBody AssociatedEnterpriseSearchREQ req) {
        return R.ok(blackGrayExternalDataService.associatedEnterpriseSearch(req));
    }

    @ApiOperation("批量填充所属企业及下属企业")
    @PostMapping("/public/black/batch/query/associated/enterprise")
    public R<List<AssociatedEnterpriseBatchSearchRSP>> batchAffiliatedEnterpriseSearch(@RequestBody List<AffiliatedEnterpriseSearchREQ> req) {
        return R.ok(blackGrayExternalDataService.batchAffiliatedEnterpriseSearch(req));
    }

}
