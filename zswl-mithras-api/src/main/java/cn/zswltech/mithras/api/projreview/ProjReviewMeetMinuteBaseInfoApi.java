package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.meet.*;
import cn.zswltech.mithras.dto.trackEvent.TrackEventListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 项目评审会议纪要表
* @author vico
* @date 2025-03-18
*/
@Api(tags = "项目评审会议纪要表-接口")
public interface ProjReviewMeetMinuteBaseInfoApi {

    @ApiOperation("项目评审会议纪要表详情")
    @PostMapping("/proj/review/meet/minute/base/info/detail")
    R<ProjReviewMeetMinuteBaseInfoDetailRSP> detail(@RequestBody @Valid ProjReviewMeetMinuteBaseInfoDetailREQ req);

    @ApiOperation("修改项目评审会议纪要表-保存")
    @PostMapping("/proj/review/meet/minute/base/info/modify")
    R<Void> modify(@RequestBody @Valid ProjReviewMeetMinuteBaseInfoModifyREQ req);

    @ApiOperation("修改项目评审会议纪要表-提交")
    @PostMapping("/proj/review/meet/minute/base/info/submit")
    R<Void> submit(@RequestBody @Valid ProjReviewMeetMinuteBaseInfoModifyREQ req);

    @ApiOperation("项目评审会议纪要-获取交易结构用户")
    @PostMapping("/proj/review/meet/minute/get/related/customers")
    R<List<ClientInfo>> getRelatedCustomers(@RequestBody @Valid ProjReviewMeetMinuteRelatedCustomersREQ req);

    @ApiOperation("项目评审会议纪要表-授信到期日判断")
    @PostMapping("/proj/review/meet/minute/credit/date/check")
    R<ProjReviewMeetMinuteCreditDateCheckRSP> creditDateCheck(@RequestBody @Valid ProjReviewMeetMinuteBaseInfoDetailREQ req);

    @ApiOperation("项目评审会议纪要表-跟踪事项列表")
    @PostMapping("/proj/review/meet/minute/trackEvent/list")
    R<List<TrackEventListRSP>> trackEventList(@RequestBody @Valid ProjReviewMeetMinuteBaseInfoDetailREQ req);

}