package cn.zswltech.mithras.api.trackEvent;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.OrgUserRSP;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.trackEvent.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "跟踪事项-接口")
@RequestMapping("/trackEvent")
public interface TrackEventApi {

    @ApiOperation("跟踪事项列表")
    @PostMapping("/list")
    R<PageR<TrackEventListRSP>> list(@RequestBody TrackEventListREQ req);

    @ApiOperation("跟踪事项详情")
    @GetMapping("/detail")
    R<TrackEventDetailRSP> detail(@RequestParam Long id);

    @ApiOperation("信息回显")
    @PostMapping("/contractInfo")
    R<TrackEventContractInfoRSP> contractInfo(@RequestBody TrackEventContractInfoREQ req);

    @ApiOperation("跟踪事项新增")
    @PostMapping("/add")
    R<Boolean> add(@RequestBody @Valid TrackEventAddREQ rsp);

//    @ApiOperation("跟踪事项编辑")
//    @PostMapping("/update")
//    R<Boolean> update(@RequestBody @Valid TrackEventUpdateREQ rsp);

    @ApiOperation("关闭任务")
    @GetMapping("/close")
    R<Boolean> close(@RequestParam Long id);

    @ApiOperation("处理人下拉框")
    @GetMapping("/queryProcessor")
    R<List<UserRSP>> queryProcess();

    @ApiOperation("合同编号下拉框")
    @GetMapping("/contractCodeList")
    R<List<String>> contractCodeList();

    @ApiOperation("项目名称下拉框")
    @GetMapping("/projNameList")
    R<List<String>> projNameList();

    @ApiOperation("批量导出")
    @PostMapping("/download")
    void download(@RequestBody TrackEventMainREQ req);


    @GetMapping("/testEffect")
    R<Void> testEffect();
}
