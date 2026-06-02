package cn.zswltech.mithras.others.simple;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.repository.aliyun.req.OcrDetectReq;
import cn.zswltech.mithras.service.util.HttpUtil;
import cn.zswltech.mithras.service.util.PdfUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author luyi
 */
@Slf4j
public class TestBillTicketOcr {
    private String ocrHost = "http://10.158.251.7:80";


    @Test
    @SneakyThrows
    public void testPdf() {
        StringBuilder builder = new StringBuilder();
        // 识别pdf
        PDDocument doc = PDDocument.load(new FileInputStream("/Users/luyi/Desktop/租赁合同-回租.pdf"));
        PDFRenderer renderer = new PDFRenderer(doc);
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            String url = String.format("%s/ocrapidocker/ocrservice.json", ocrHost);
            long startTime = System.currentTimeMillis();
            try {
                OcrDetectReq ocrDetectReq = OcrDetectReq.builder()
                        .method("ocrService")
                        .table(false)
                        .rotate(false)
                        .img(ImgUtil.toBase64(renderer.renderImageWithDPI(i, PdfUtil.DEFAULT_DPI), PdfUtil.DEFAULT_FORMAT))
                        .prob(true)
                        .build();
                String responseData = null;
                responseData = HttpUtil.ocrRequest(url, BeanUtil.beanToMap(ocrDetectReq));
                long endTime = System.currentTimeMillis();
                log.info("request http,url:{},time:{}", url, endTime - startTime);
                JSONObject jo = JSONUtil.parseObj(responseData);
                String content = (String) JSONUtil.getByPath(jo, "data.content");
                /**
                 * 识别结果
                 * 西 上海增值税中子普通发票 日 统一发票监 发票代码：031001600311 发票号码：81471594 开票日期：2017年11月13日 上海市税务局 机器编号：499099774351 校验码：01519962196503160071 购 名 称：洋达国际货运代理(上海)有限公司 密 0325<*2*64*6*3<*46*8+3>-08+0 纳税人识别号：913100005647710460 79-864<458+<8355<07<379*053+ 买 码 地址、电话：延安东路175号旺角广场205室23279000 1++496+048+829+7-0907+-/883/ 方 开户行及账号：中国银行上海市九江路支行448159231184 区 05304><6-20194/219>/35+395+* 货物或应税劳务、服务名称 规格型号 单位 数量 单价 金 额 税率 税 额 快递服务价外费用 228.254717 228.25 6% 13.70 合 计 ￥228.25 ￥13.70 价税合计(大写) 贰佰肆拾壹圆玖角伍分 (小写)￥241.95 名 称：联邦快递(中国)有限公司上海分公司 730632668533 销 备 纳税人识别号：913100007421296207 中国 限公司上 售 地址、电话：上海市长宁区遵义路107号10楼021-62750808 年 中 方 注 ￥9300072296273 开户行及账号：中国工商银行上海市虹桥开发区支行1001242719300327691 发票专用章 收款人：蒋淼露 复核：王燕清 开票人：李骏 销售单位： (章 (2)
                 */
                builder.append(System.lineSeparator()).append(content);
            } catch (IOException e) {
                log.error("阿里云ocr调用超时", e);
                throw new MithrasException("ocr服务处理超时");
            }
        }
        System.out.println(builder.toString());
    }


    @Test
    public void testImg() {
        String url = String.format("%s/ocrapidocker/ocrservice.json", ocrHost);
        long startTime = System.currentTimeMillis();
        try {
            OcrDetectReq ocrDetectReq = OcrDetectReq.builder()
                    .method("ocrService")
                    .table(false)
                    .rotate(false)
                    .img(Base64.encode(IoUtil.toStream(new File("/Users/luyi/Downloads/发票.jpeg"))))
//                    .img(Base64.encode(IoUtil.toStream(new File("/Users/luyi/Desktop/租赁合同-回租.pdf"))))

                    .prob(true)
                    .build();
            String responseData = null;
            responseData = HttpUtil.ocrRequest(url, BeanUtil.beanToMap(ocrDetectReq));
            long endTime = System.currentTimeMillis();
            log.info("request http,url:{},time:{}", url, endTime - startTime);
            JSONObject jo = JSONUtil.parseObj(responseData);
            String content = (String) JSONUtil.getByPath(jo, "data.content");
            /**
             * 识别结果
             * 西 上海增值税中子普通发票 日 统一发票监 发票代码：031001600311 发票号码：81471594 开票日期：2017年11月13日 上海市税务局 机器编号：499099774351 校验码：01519962196503160071 购 名 称：洋达国际货运代理(上海)有限公司 密 0325<*2*64*6*3<*46*8+3>-08+0 纳税人识别号：913100005647710460 79-864<458+<8355<07<379*053+ 买 码 地址、电话：延安东路175号旺角广场205室23279000 1++496+048+829+7-0907+-/883/ 方 开户行及账号：中国银行上海市九江路支行448159231184 区 05304><6-20194/219>/35+395+* 货物或应税劳务、服务名称 规格型号 单位 数量 单价 金 额 税率 税 额 快递服务价外费用 228.254717 228.25 6% 13.70 合 计 ￥228.25 ￥13.70 价税合计(大写) 贰佰肆拾壹圆玖角伍分 (小写)￥241.95 名 称：联邦快递(中国)有限公司上海分公司 730632668533 销 备 纳税人识别号：913100007421296207 中国 限公司上 售 地址、电话：上海市长宁区遵义路107号10楼021-62750808 年 中 方 注 ￥9300072296273 开户行及账号：中国工商银行上海市虹桥开发区支行1001242719300327691 发票专用章 收款人：蒋淼露 复核：王燕清 开票人：李骏 销售单位： (章 (2)
             */
            System.out.println(content);
        } catch (IOException e) {
            log.error("阿里云ocr调用超时", e);
            throw new MithrasException("ocr服务处理超时");
        }
    }
}
