package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传一个图片 返回一个excel
 *
 * @author wangchuanhao
 * @date 2022/7/5 5:32 PM
 */
@Api(tags = "ocr图片检测-接口")
@RequestMapping("/ocr")
public interface OcrApi {

    /**
     * 如果是图片支持多张检测
     * 如果是pdf只支持一个
     * @param file
     * @param pdfIndex
     */
    @ApiOperation("图片检测")
    @PostMapping("/detect")
    void detect(@RequestParam("file") MultipartFile[] file, @RequestParam(name = "pdfIndex", required = false) String pdfIndex);

    /**
     * 如果是图片支持多张检测
     * 如果是pdf只支持一个
     * @param file
     * @param pdfIndex
     */
    @ApiOperation("图片检测（返回文件id）")
    @PostMapping("/detectOnline")
    R<Long> detectOnline(@RequestParam("file") MultipartFile[] file, @RequestParam(name = "pdfIndex", required = false) String pdfIndex);


}
