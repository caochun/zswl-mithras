package cn.zswltech.mithras.application.orchestration.facade.document.ocr;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.style.StyleUtil;
import cn.zswltech.mithras.document.service.api.OcrApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.third.aliyun.ocr.client.req.OcrDetectReq;
import cn.zswltech.mithras.third.aliyun.ocr.client.resp.OcrDetectResp;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.aliyun.ocr.util.PdfUtil;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口
 *
 * @author wangchuanhao
 * @date 2022/7/5 5:34 PM
 */
@Service
@Slf4j
public class OcrFacade implements OcrApplicationService {

    private static final String MATERIALS_TYPE = "OCR";
    private static final String TMP_BUSINESS_TYPE = "TMP";

    @Autowired
    private HttpServletResponse response;
    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Value("${ocr.probLimit}")
    private Integer probLimit;
    @Resource
    private MaterialsListService materialsListService;

    /**
     * hutool 创建excelWriter时默认有sheet1，不合理
     *
     * @param excelWriter
     * @param sheetName
     * @return
     */
    public static void resetSheetName(ExcelWriter excelWriter, String sheetName) {
        if ("sheet1".equalsIgnoreCase(excelWriter.getSheet().getSheetName())) {
            // 第一页特殊处理 不新增sheet
            excelWriter.renameSheet(sheetName);
        } else {
            // 后续需要新增sheet
            excelWriter.setSheet(sheetName);
        }
        excelWriter.getStyleSet().setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        excelWriter.getStyleSet().setWrapText();
        excelWriter.setDefaultRowHeight(30);
        for (int i = 0; i < 30; i++) {
            excelWriter.setColumnWidth(i, 15);
        }
    }

    @Override
    @SneakyThrows
    public void detect(MultipartFile[] fileArray, String pdfIndex) {
        ExcelWriter excelWriter = doDetect(fileArray, pdfIndex);
        String fileName = (fileArray.length == 1 ? FileNameUtil.mainName(fileArray[0].getOriginalFilename()) : "识别结果") + ".xlsx";
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        excelWriter.flush(response.getOutputStream(), true);
    }

    @Override
    public R<Long> detectOnline(MultipartFile[] fileArray, String pdfIndex) {
        ExcelWriter excelWriter = doDetect(fileArray, pdfIndex);
        String fileName = (fileArray.length == 1 ? FileNameUtil.mainName(fileArray[0].getOriginalFilename()) : "识别结果") + ".xlsx";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        excelWriter.flush(os, false);
        Long fileId = materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName, -1L, MATERIALS_TYPE, TMP_BUSINESS_TYPE);
        return R.ok(fileId);
    }

    @SneakyThrows
    public ExcelWriter doDetect(MultipartFile[] fileArray, String pdfIndex) {
        if (fileArray.length == 0) {
            throw new MithrasException("识别文件必传");
        }
        boolean pdfFlag = false;
        // 如果是pdf只允许有一个文件
        for (MultipartFile file : fileArray) {
            if ("application/pdf".equals(file.getContentType())) {
                if (fileArray.length > 1) {
                    throw new MithrasException("pdf只支持单文件识别");
                }
                pdfFlag = true;
            }
        }
        PlatformApiHandler<OcrDetectReq, OcrDetectResp> ocrApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.ALI_OCR);
        OcrDetectReq ocrDetectReq = OcrDetectReq.builder()
                .method("ocrService")
                .table(true)
                .rotate(false)
                .prob(true)
                .build();
        ExcelWriter excelWriter = new ExcelWriter(true);
        excelWriter.getStyleSet().setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        excelWriter.getStyleSet().setWrapText();
        if (pdfFlag) {
            if (StringUtils.isBlank(pdfIndex)) {
                throw new MithrasException("pdf页码不合法");
            }
            // 识别pdf
            PDDocument doc = PDDocument.load(fileArray[0].getInputStream());
            // 过滤出有效页码
            List<Integer> indexList = PdfUtil.range2List(pdfIndex)
                    .stream().filter(i -> doc.getNumberOfPages() > i - 1).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(indexList)) {
                throw new MithrasException("pdf页码不合法");
            }
            PDFRenderer renderer = new PDFRenderer(doc);
            for (Integer index : indexList) {
//                if (doc.getNumberOfPages() <= index-1) {
//                    continue;
//                }
                ocrDetectReq.setImg(ImgUtil.toBase64(renderer.renderImageWithDPI(index - 1, PdfUtil.DEFAULT_DPI), PdfUtil.DEFAULT_FORMAT));
                OcrDetectResp ocrDetectResp = ocrApiHandler.execute(ocrDetectReq);
                log.info("阿里云调用返回值:{}", JSON.toJSONString(ocrDetectResp));
                writeExcel(ocrDetectResp, excelWriter, String.format("第%s页", index));
            }
        } else {
            // 识别图片
            // 不做 图片格式校验
            for (MultipartFile file : fileArray) {
                ocrDetectReq.setImg(Base64.encode(file.getInputStream()));
                OcrDetectResp ocrDetectResp = ocrApiHandler.execute(ocrDetectReq);
                log.info("阿里云调用返回值:{}", JSON.toJSONString(ocrDetectResp));
                writeExcel(ocrDetectResp, excelWriter, FileNameUtil.mainName(file.getOriginalFilename()));
            }
        }
        return excelWriter;
    }

    public void writeExcel(OcrDetectResp ocrDetectResp, ExcelWriter excelWriter, String sheetName) {
        List<OcrDetectResp.PrismTablesInfoDTO> tableList = Optional.ofNullable(ocrDetectResp)
                .map(resp -> resp.getData())
                .map(d -> d.getPrismTablesinfo())
                .orElse(null);
        // 阿里云调用失败 在第一个单元格写原因
        if (CollectionUtils.isEmpty(tableList)) {
            resetSheetName(excelWriter, sheetName);
            excelWriter.writeCellValue(0, 0, "ocr识别失败或图片中不包含表格");
            return;
        }
        // 置信度字体数组
        List<OcrDetectResp.PrismWordsInfoDTO> wordList = Optional.ofNullable(ocrDetectResp)
                .map(resp -> resp.getData())
                .map(d -> d.getPrismWordsinfo())
                .orElse(new ArrayList<>());
        Map<Integer, Map<Integer, Integer>> probMap = new HashMap<>();
        for (OcrDetectResp.PrismWordsInfoDTO wordsInfoDTO : wordList) {
            if (Objects.nonNull(wordsInfoDTO.getTableId())
                    && Objects.nonNull(wordsInfoDTO.getTableCellId())) {
                Map<Integer, Integer> tableCellMap = probMap.computeIfAbsent(wordsInfoDTO.getTableId(), k -> new HashMap<>());
                tableCellMap.put(wordsInfoDTO.getTableCellId(), wordsInfoDTO.getProb());
            }
        }

        // 处理表格
        for (OcrDetectResp.PrismTablesInfoDTO tablesInfoDTO : tableList) {
            List<OcrDetectResp.CellInfosDTO> cellList = tablesInfoDTO.getCellInfos();
            if (CollectionUtils.isEmpty(cellList)) {
                continue;
            }
            Integer tableId = tablesInfoDTO.getTableId();
            // sheet更换
            if (tableList.size() == 1) {
                resetSheetName(excelWriter, sheetName);
            } else {
                resetSheetName(excelWriter, sheetName + String.format("-第%s个表格", tablesInfoDTO.getTableId() + 1));
            }

            for (OcrDetectResp.CellInfosDTO cellInfosDTO : cellList) {
                int yec = cellInfosDTO.getYec(), xec = cellInfosDTO.getXec(),
                        ysc = cellInfosDTO.getYsc(), xsc = cellInfosDTO.getXsc();
                String content = cellInfosDTO.getWord();
                if (yec == ysc && xec == xsc) {
                    // 只写一个单元格
                    excelWriter.writeCellValue(xec, yec, content);
                } else {
                    // 合并单元格
                    try {
                        excelWriter.merge(ysc, yec, xsc, xec, content, false);
                    } catch (Exception e) {
                        // 阿里云识别有问题 会重复操作同一个单元格 不做处理
                        log.error("ocr识别结果异常，无需处理", e);
                    }
                }
                // 置信度较低的单元格 标红
                Integer prob = Optional.ofNullable(probMap.get(tableId))
                        .map(m -> m.get(cellInfosDTO.getTableCellId()))
                        .orElse(100);
                if (prob < probLimit) {
                    CellStyle riskCellStyle = excelWriter.createCellStyle(xec, yec);
                    riskCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                    riskCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    StyleUtil.setAlign(riskCellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
                    riskCellStyle.setWrapText(true);
                    excelWriter.setStyle(riskCellStyle, xec, yec);
                }
            }
        }
    }

}
