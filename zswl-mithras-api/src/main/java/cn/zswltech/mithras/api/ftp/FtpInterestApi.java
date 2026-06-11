package cn.zswltech.mithras.api.ftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Api(tags = "FTP计息相关接口")
@RequestMapping(path = "/ftp/interest")
public interface FtpInterestApi {
    @ApiOperation("FTP计息-列表")
    @PostMapping(path = "/pagelist")
    R<PageR<FtpInterestPageListRsp>> pageList(@RequestBody FtpInterestPageListReq req);

    @ApiOperation("FTP计息-计息任务重跑")
    @PostMapping(path = "/recalculate")
    R<Void> recalculate(@RequestBody @Valid FtpInterestRecalculateReq req);

    @ApiOperation("FTP计息-最新数据月份")
    @PostMapping(path = "/latest/month")
    R<LocalDate> getLatestMonth();

    @ApiOperation("FTP计息-基本信息-详情")
    @PostMapping(path = "/baseinfo/get")
    R<FtpInterestBaseInfoRsp> getBaseInfo(@RequestBody @Valid FtpInterestIdReq req);

    @ApiOperation("FTP计息-每日计息详情-列表")
    @PostMapping(path = "/detail/pagelist")
    R<PageR<FtpInterestDetailRsp>> listDetailRecordWithPage(@RequestBody @Valid FtpInterestDetailReq req);
}
