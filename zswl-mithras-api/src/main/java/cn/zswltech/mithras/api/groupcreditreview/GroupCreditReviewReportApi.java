package cn.zswltech.mithras.api.groupcreditreview;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Api(tags = "集团授信评审报告文件-接口")
public interface GroupCreditReviewReportApi {

    @ApiOperation("集团授信评审报告文件-上传")
    @PostMapping("/group/credit/review/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @Valid GroupCreditReviewReportUploadREQ req);

    @ApiOperation("集团授信评审报告文件列表")
    @PostMapping("/group/credit/review/report/list")
    R<List<Pair<String, List<GroupCreditReviewReportListRSP>>>> list(@RequestBody @Valid GroupCreditReviewReportListREQ req);

    @ApiOperation("集团授信评审报告文件-删除")
    @PostMapping("/group/credit/review/report/remove")
    R<Void> remove(@RequestBody @Valid GroupCreditReviewReportRemoveREQ req);

    @ApiOperation("集团授信评审报告文件-下载")
    @GetMapping("/group/credit/review/report/download")
    R<FileListRSP> download(@Valid GroupCreditReviewReportDownloadREQ req);

}
