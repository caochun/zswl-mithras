package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Api(tags = "风控舆情监控-接口")
public interface RiskControlOpinionMonitorApi {

    /**
     * 处理舆情
     *
     * @return
     */
    @ApiOperation("处置舆情并提交审批")
    @PostMapping("/risk/control/opinion/monitor/handle")
    R<Void> handleOpinion(@RequestBody @Valid RiskControlOpinionHandleREQ req);

    @ApiOperation("处置舆情并提交审批")
    @PostMapping("/risk/control/opinion/monitor/advisement")
    R<Void> fixAdvisement(@RequestBody @Valid RiskControlOpinionHandleREQ req);

    @ApiOperation("风控舆情监控列表")
    @PostMapping("/risk/control/opinion/monitor/list")
    R<PageR<RiskControlOpinionMonitorListRSP>> list(@RequestBody @Valid RiskControlOpinionMonitorListREQ req);

    @ApiOperation("风控舆情变动接收")
    @PostMapping("/risk/control/opinion/monitor/notice")
    R<Void> notice(@RequestBody @Valid RiskControlOpinionNoticeReq req);

    @ApiOperation("风控舆情通知主办")
    @PostMapping("/risk/control/opinion/monitor/send")
    R<Void> send(@RequestBody @Valid RiskControlOpinionSendReq req);

    //项目经理处理舆情，相关接口
    @ApiOperation("我的未处理舆情列表")
    @PostMapping("/risk/control/opinion/monitor/unresolved")
    R<PageR<UnresolvedClientOpinionRSP>> unresolvedList(@RequestBody @Valid UnresolvedClientOpinionREQ req);

    @ApiOperation("风控舆情详情")
    @PostMapping("/risk/control/opinion/monitor/detail")
    R<RiskControlOpinionMonitorListRSP> detail(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("风控舆情-查看处理")
    @PostMapping("/risk/control/opinion/monitor/view")
    R<String> view(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("查询指定客户的需处理舆情数量")
    @PostMapping("/risk/control/opinion/monitor/count")
    R<Integer> countByClientId(@RequestBody @Valid ClientIdREQ req);

    @ApiOperation("舆情监测-关闭流程(提交审批)")
    @PostMapping("/risk/control/opinion/monitor/close")
    R<Void> close(@RequestBody @Valid OpinionMonitorCloseREQ req);

    @ApiOperation("人工录入舆情-确定按钮")
    @PostMapping("/risk/control/opinion/monitor/confirm")
    R<RiskControlOpinionManualRSP> confirm(@RequestBody @Valid ClientIdREQ req);

    @ApiOperation("人工录入舆情-保存按钮")
    @PostMapping("/risk/control/opinion/monitor/save")
    R<Void> save(@RequestBody @Valid RiskControlOpinionManualDetailREQ req);

    @ApiOperation("人工录入舆情-提交按钮")
    @PostMapping("/risk/control/opinion/monitor/submit")
    R<Void> submit(@RequestBody @Valid RiskControlOpinionManualDetailREQ req);

    @ApiOperation("人工录入舆情-删除按钮")
    @PostMapping("/risk/control/opinion/monitor/delete")
    R<Void> delete(@RequestBody @Valid SinglePkREQ req);
}