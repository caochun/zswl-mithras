package cn.zswltech.mithras.others;//import bsh.commands.dir;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.third.repository.aliyun.resp.OcrDetectResp;
import cn.zswltech.mithras.service.util.HttpUtil;
import cn.zswltech.mithras.third.aliyun.ocr.infrastructure.util.PdfUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ocr_api20210707.models.RecognizeTableOcrRequest;
import com.aliyun.ocr_api20210707.models.RecognizeTableOcrResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 阿里云ocr返回值解析
 *
 * @author wangchuanhao
 * @date 2022/7/3 4:13 PM
 */
public class AliyunOCRExcelTest {

    static {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        for (int i = 0; i < loggerList.size(); i++) {
            loggerList.get(i).setLevel(ch.qos.logback.classic.Level.INFO);;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AliyunOCRExcelTest.class);

    public static void main(String[] args) throws Exception {
        File pDir = new File("/Users/wang/Desktop/财务报表_新测试");
        File[] sDirArray = pDir.listFiles();
        Long sDirCount = Stream.of(sDirArray).filter(s -> s.isDirectory()).count();
        //CountDownLatch countDownLatch = new CountDownLatch(Math.toIntExact(sDirCount));
        //ExecutorService fixPool = Executors.newFixedThreadPool(3);
        for (File sDir : sDirArray) {
            if (!sDir.isDirectory()) {
                continue;
            }
            // FileUtil.del(sDir.getAbsolutePath() + "/识别结果");
            // FileUtil.mkdir(sDir.getAbsolutePath() + "/识别结果");
//            CompletableFuture.runAsync(() -> {
//                try {
//                    // batchPdf2Image(sDir.getAbsolutePath(), sDir.getAbsolutePath() + "/img");
//                    batchDetect(sDir.getAbsolutePath() + "/img", sDir.getAbsolutePath() + "/识别结果");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    countDownLatch.countDown();
//                }
//            }, fixPool);
             batchDetect(sDir.getAbsolutePath() + "/img", sDir.getAbsolutePath() + "/识别结果");
          }
//        countDownLatch.await();
        System.out.println("处理完成");
        //batchPdf2Image("/Users/wang/Desktop/阿里云ocr批量测试gpu/pdf", "/Users/wang/Desktop/阿里云ocr批量测试/img");
        //batchDetect("/Users/wang/Desktop/阿里云ocr批量测试gpu/修复", "/Users/wang/Desktop/阿里云ocr批量测试gpu/识别结果");
    }

    /**
     * 文件夹下文件 批量测试
     */
    public static void batchDetect(String imgDirPath, String outputDirPath) throws Exception {
        File imgDir = new File(imgDirPath);
        File[] imgFileArray = imgDir.listFiles();
        Map<String, Object> paramMap = new HashMap<>();
        // 固定参数
        paramMap.put("method", "ocrService");
        paramMap.put("table", "true");
        paramMap.put("rotate", "true");
        paramMap.put("prob", "true");
        for (File imgFile : imgFileArray) {
            if (!"jpg".equals(FileNameUtil.extName(imgFile))) {
                continue;
            }
            // 创建文件夹
            String cellDir = outputDirPath + "/" + FileNameUtil.mainName(imgFile);
            if (!FileUtil.exist(cellDir)) {
                FileUtil.mkdir(cellDir);
            }
            // 图片抄过去
            FileUtil.copy(imgFile.getAbsolutePath(), cellDir + "/" + FileNameUtil.getName(imgFile), true);
            paramMap.put("img", Base64.encode(new FileInputStream(imgFile)));
            try {
                ExcelWriter excelWriter = new ExcelWriter(cellDir + "/" + "识别结果.xlsx");
                String gpuRes = HttpUtil.ocrRequest("http://121.41.13.92:8082/ocrapidocker/ocrservice.json", paramMap);
                //String cpuRes = HttpUtil.ocrRequest("http://121.41.13.92:80/ocrapidocker/ocrservice.json", paramMap);
//                String gyyResData = gyyTest(imgFile);
                OcrDetectResp gpuResp = JSONObject.parseObject(gpuRes, OcrDetectResp.class);
                //OcrDetectResp cpuResp = JSONObject.parseObject(cpuRes, OcrDetectResp.class);

//                JSONObject gyyData = JSONObject.parseObject(gyyResData);
//                JSONObject gyyObj = new JSONObject();
//                gyyObj.put("data", gyyData);
//                OcrDetectResp gyyResp = JSONObject.parseObject(gyyObj.toJSONString(), OcrDetectResp.class);

                //OcrController.writeExcel(cpuResp, excelWriter, "cpu识别结果");
//                OcrController.writeExcel(gpuResp, excelWriter, "gpu识别结果");
                // OcrController.writeExcel(gyyResp, excelWriter, "公有云识别结果");
                excelWriter.flush();
                excelWriter.close();
            } catch (Exception e) {
                log.error("阿里云识别失败", e);
            }
        }
    }

    /**
     * pdf文件转图片批量
     * @param pdfDirPath pdf文件夹路径
     * @param imgDirPath 图片目录
     */
    public static void batchPdf2Image(String pdfDirPath, String imgDirPath) throws Exception {
        File pdfDir = new File(pdfDirPath);
        File[] pdfFileArray = pdfDir.listFiles();
        for (File pdfFile : pdfFileArray) {
            if (!"pdf".equals(FileNameUtil.extName(pdfFile))) {
                continue;
            }
            PDDocument doc = PDDocument.load(new FileInputStream(pdfFile));
            PDFRenderer renderer = new PDFRenderer(doc);
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, PdfUtil.DEFAULT_DPI);
                ImageIO.write(image, PdfUtil.DEFAULT_FORMAT,
                        new File(String.format("%s/%s_第%s页.jpg", imgDirPath, FileNameUtil.mainName(pdfFile), i+1)));

            }
            doc.close();
        }
    }

    /**
     * 格式解析测试
     * @throws IOException
     */
    public static void analyzeTest() throws IOException {
        String outputFileName = "/Users/wang/Desktop/res2.xlsx";
        String res = IoUtil.read(new ClassPathResource("ocr/res2.json").getInputStream(), "UTF-8");
        JSONArray dataArray = JSONObject.parseObject(res)
                .getJSONObject("data")
                .getJSONArray("prism_tablesInfo")
                .getJSONObject(0)
                .getJSONArray("cellInfos");
        ExcelWriter excelWriter = new ExcelWriter(outputFileName);
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject data = dataArray.getJSONObject(i);
            Integer yec = data.getInteger("yec"), xec = data.getInteger("xec"),
                    ysc = data.getInteger("ysc"), xsc = data.getInteger("xsc");
            String content = data.getString("word");
            if (yec == ysc && xec == xsc) {
                // 只写一个单元格
                excelWriter.writeCellValue(xec, yec, content);
            } else {
                // 合并单元格
                excelWriter.merge(ysc, yec, xsc, xec, content, false);
            }
        }
        excelWriter.flush();
        excelWriter.close();
    }

    @SneakyThrows
    public static String gyyTest(File imgFile) {
        com.aliyun.ocr_api20210707.Client client = createClient("LTAI5tBkedz7mwQKnRG4zkJC", "WOxZoNsWaB6WfDIGgY5K5xPRnen55z");
        RecognizeTableOcrRequest recognizeTableOcrRequest = new RecognizeTableOcrRequest()
                .setBody(new FileInputStream(imgFile))
                .setNeedRotate(true);
        RuntimeOptions runtime = new RuntimeOptions();
        RecognizeTableOcrResponse response = client.recognizeTableOcrWithOptions(recognizeTableOcrRequest, runtime);
        return response.getBody().data;
    }

    public static com.aliyun.ocr_api20210707.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                .setConnectTimeout(5000)
                .setReadTimeout(120000);
        // 访问的域名
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }

}
