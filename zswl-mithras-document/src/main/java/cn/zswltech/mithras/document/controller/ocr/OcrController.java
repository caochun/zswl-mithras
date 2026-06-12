package cn.zswltech.mithras.document.controller.ocr;

import cn.zswltech.mithras.api.OcrApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.document.application.OcrApplicationService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class OcrController implements OcrApi {

    @Resource
    private OcrApplicationService ocrApplicationService;

    @Override
    public void detect(MultipartFile[] file, String pdfIndex) {
        ocrApplicationService.detect(file, pdfIndex);
    }

    @Override
    public R<Long> detectOnline(MultipartFile[] file, String pdfIndex) {
        return ocrApplicationService.detectOnline(file, pdfIndex);
    }
}
