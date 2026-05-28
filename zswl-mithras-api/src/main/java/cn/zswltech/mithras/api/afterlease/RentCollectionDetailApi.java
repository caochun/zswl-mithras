package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.rentcollection.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-11-17
 **/
@Api(tags = "租金催收-部分详情接口")
public interface RentCollectionDetailApi {

    @ApiOperation("租金催收-期项租金卡-租金信息")
    @PostMapping("/rent/collection/rent/detail")
    R<RentDetailInfoRSP> rentDetail(@RequestBody @Valid RentDetailInfoREQ req);

    @ApiOperation("租金催收-期项租金卡-逾期情况")
    @PostMapping("/rent/collection/overdue/detail")
    R<OverdueDetailInfoRSP> overdueDetail(@RequestBody @Valid RentDetailInfoREQ req);

//    @ApiOperation("租金催收-借据卡-项目基本信息")
//    @PostMapping("/rent/collection/proj/detail")
//    R<ProjEstablishBaseInfoListRSP> projDetail(@RequestBody @Valid ProjDetailInfoREQ req);

    @ApiOperation("租金催收-借据卡-租金信息")
    @PostMapping("/rent/collection/overdue/rent/detail")
    R<List<OverdueRentListInfoRSP>> overdueRentList(@RequestBody @Valid ProjDetailInfoREQ req);

}
