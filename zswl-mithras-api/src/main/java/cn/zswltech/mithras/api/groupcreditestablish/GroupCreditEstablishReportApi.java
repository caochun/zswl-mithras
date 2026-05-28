package cn.zswltech.mithras.api.groupcreditestablish;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.*;
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
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Api(tags = "集团授信立项报告文件-接口")
public interface GroupCreditEstablishReportApi {

    @ApiOperation("集团授信立项报告文件-上传")
    @PostMapping("/group/credit/establish/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @Valid GroupCreditEstablishReportUploadREQ req);

    @ApiOperation("集团授信立项报告文件列表")
    @PostMapping("/group/credit/establish/report/list")
    R<List<Pair<String, List<GroupCreditEstablishReportListRSP>>>> list(@RequestBody @Valid GroupCreditEstablishReportListREQ req);

    @ApiOperation("集团授信立项报告文件-删除")
    @PostMapping("/group/credit/establish/report/remove")
    R<Void> remove(@RequestBody @Valid GroupCreditEstablishReportRemoveREQ req);

    @ApiOperation("集团授信立项报告文件-下载")
    @GetMapping("/group/credit/establish/report/download")
    R<FileListRSP> download(@Valid GroupCreditEstablishReportDownloadREQ req);

}
