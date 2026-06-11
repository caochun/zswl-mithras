package cn.zswltech.mithras.api.monthly;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.monthly.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(tags = "月结管理接口")
@RestController
public interface MonthlyManageApi {

    @ApiOperation("列表")
    @PostMapping("/monthly/list/page")
    R<PageR<MonthlyListRSP>> listPage(@RequestBody @Valid MonthlyListREQ req);

    @ApiOperation("新增月结")
    @PostMapping("/monthly/base/add")
    R<Long> addBaseInfo(@RequestBody @Valid MonthlyAddREQ req);

    @ApiOperation("收入确认-实际利率法列表")
    @PostMapping("/monthly/air/page")
    R<PageR<MonthlyAIRListRSP>> airPage(@RequestBody @Valid MonthlyAIRListREQ req);

    @ApiOperation("收入计提-剩余本金法列表")
    @PostMapping("/monthly/rp/page")
    R<PageR<MonthlyRPListRSP>> rpPage(@RequestBody @Valid MonthlyRPListREQ req);

    @ApiOperation("印花税计提-项目端")
    @PostMapping("/monthly/stampDuty/proj/page")
    R<PageR<MonthlyStampDutyProjRSP>> projPage(@RequestBody @Valid MonthlyStampDutyProjREQ req);

    @ApiOperation("印花税计提-资金端")
    @PostMapping("/monthly/stampDuty/fin/page")
    R<PageR<MonthlyStampDutyFinRSP>> finPage(@RequestBody @Valid MonthlyStampDutyFinREQ req);

    @ApiOperation("Excel导出")
    @PostMapping("/monthly/download")
    void download(@RequestBody @Valid MonthlyExcelREQ req);

    @ApiOperation("每月成本计提")
    @PostMapping("/monthly/cost/list")
    R<PageR<MonthlyCostRSP>> costPage(@RequestBody @Valid MonthlyCostREQ req);

//    @ApiOperation("校验")
//    @PostMapping("/monthly/validate")
//    R<Void> validate(@RequestBody @Valid MonthlyCostValidateREQ req);


    @ApiOperation("提交")
    @PostMapping("/monthly/submit")
    R<Void> submit(@RequestBody @Valid MonthlySubmitREQ req);

    @ApiOperation(value = "刷新数据")
    @PostMapping("/monthly/fresh")
    R<Void> freshById(@RequestBody @Valid MonthlyFreshDataREQ req);

    @ApiOperation(value = "更新记录的生效状态")
    @PostMapping("/monthly/update/status")
    R<Void> updateStatus(@RequestBody @Valid MonthlyUpdateStatusREQ req);

    @ApiOperation(value = "月结关账")
    @PostMapping("/monthly/close")
    R<Void> close(@RequestBody @Valid MonthlyCloseREQ req);

    @ApiOperation(value = "关账校验")
    @PostMapping("/monthly/close/validate")
    R<Void> closeValidate(@RequestBody @Valid MonthlyCloseREQ req);

    @ApiOperation(value = "推送单条数据到苍穹")
    @PostMapping("/monthly/push/single")
    R<Void> pushSingle(@RequestBody @Valid MonthlySubmitSingleREQ req);

    @ApiOperation(value = "更新单条数据")
    @PostMapping("/monthly/update/single")
    R<Void> updateSingle(@RequestBody @Valid MonthlyFreshSingleREQ req);
}
