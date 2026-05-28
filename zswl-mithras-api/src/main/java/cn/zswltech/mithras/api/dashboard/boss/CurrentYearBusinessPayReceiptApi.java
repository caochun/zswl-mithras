package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/22 11:42
 * @description
 */
@RequestMapping(path = "/dashboard")
@Api(tags = "管理工作台-本年租赁业务投放收益率情况表接口")
public interface CurrentYearBusinessPayReceiptApi {

    @PostMapping(path = "/pay/receipt/rate")
    @ApiOperation(value = "本年租赁业务投放收益率情况表")
    R<CurrentYearBusinessPayReceiptRateListRSP> payReceiptRateList(@RequestBody @Valid CurrentYearBusinessPayReceiptRateListREQ req);

}
