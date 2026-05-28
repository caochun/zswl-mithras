//package hand.extract.process;
//
//import cn.hutool.core.util.ReflectUtil;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.ruiyun.jvppeteer.core.Puppeteer;
//import com.ruiyun.jvppeteer.core.browser.Browser;
//import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
//import com.ruiyun.jvppeteer.core.page.ElementHandle;
//import com.ruiyun.jvppeteer.core.page.Page;
//import com.ruiyun.jvppeteer.core.page.Target;
//import com.ruiyun.jvppeteer.core.page.TaskQueue;
//import com.ruiyun.jvppeteer.options.Clip;
//import com.ruiyun.jvppeteer.options.ClipOverwrite;
//import com.ruiyun.jvppeteer.options.LaunchOptions;
//import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
//import com.ruiyun.jvppeteer.options.PDFOptions;
//import com.ruiyun.jvppeteer.options.ScreenshotOptions;
//import com.ruiyun.jvppeteer.options.Viewport;
//import com.ruiyun.jvppeteer.protocol.emulation.ScreenOrientation;
//import com.ruiyun.jvppeteer.protocol.network.CookieParam;
//import com.ruiyun.jvppeteer.transport.CDPSession;
//import com.ruiyun.jvppeteer.util.StringUtil;
//import com.ruiyun.jvppeteer.util.ValidateUtil;
//import lombok.SneakyThrows;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
///**
// * 流程提取截图测试
// *
// * @author wangchuanhao
// * @date 2022/10/1 11:45 AM
// */
//public class PuppeeterTest {
//
//    public static void main(String[] args) throws Exception {
//
//        //自动下载，第一次下载后不会再下载
//        //BrowserFetcher.downloadIfNotExist(null);
//        ArrayList<String> argList = new ArrayList<>();
//        argList.add("--no-sandbox");
//        argList.add("--disable-setuid-sandbox");
//        argList.add("--start-maximized");
////        argList.add("--start-fullscreen");
//        argList.add("--window-size=1500,715");
//        Viewport viewport = new Viewport();
//        viewport.setWidth(1500);
//        viewport.setHeight(715);
//        LaunchOptions options = new LaunchOptionsBuilder().withExecutablePath("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome").withArgs(argList).withHeadless(false).build();
////        argList.add("--cast-initial-screen-width");
//        Browser browser = Puppeteer.launch(options);
//        Page page = browser.newPage();
//        page.setViewport(viewport);
////        page.goTo("https://www.cnblogs.com/daysme/p/15493523.html");
//        page.goTo("http://10.100.222.9:8080/zt_uat/login");
//        //等待元素加载完成，输入账号密码并提交
//        page.waitForSelector("#username");
//        page.type("#username", "admin", 100);
//        Thread.sleep(1000);
//        page.type("#password", "1234567", 100);
//        Thread.sleep(1000);
//        page.click(".button-login");
//        Thread.sleep(5000);
//        page.goTo("http://10.100.222.9:8080/zt_uat/modules/PRJ/PRJ_CHANCE/PRJ310/prj_chance_modify.lview?function_code=PRJ311F1&layout_code=PRJ311F1&chance_id=29&maintain_type=READONLY&function_usage=QUERY&wfl_flag=&wfl_flag_new=Y");
//        Thread.sleep(10000);
//        // left_166463440365292_bar_navigationBarContent
//        ElementHandle elementHandle = page.$(".navigationBarContent");
//        page.evaluate("document.getElementsByClassName(\"navigationBarContent\")[0].style.height = 'auto'");
//        Object height = page.evaluate("document.getElementsByClassName(\"navigationBarContent\")[0].style.height");
//        System.out.println(height);
//        Thread.sleep(3000);
//        ScreenshotOptions screenshotOptions = new ScreenshotOptions();
//        screenshotOptions.setFullPage(true);
//        //设置截图范围
////        Clip clip = new Clip(0,0,1440,4800);
////        screenshotOptions.setClip(clip);
//        //设置存放的路径
//        screenshotOptions.setPath("/Users/wang/Desktop/谷歌.png");
//        screenshotOptions.setType("png");
//        screenshot(page, screenshotOptions);
//        //page.screenshot(screenshotOptions);
//
//        PDFOptions pdfOptions = new PDFOptions();
//        pdfOptions.setPath("/Users/wang/Desktop/流程业务详情.pdf");
////        page.pdf(pdfOptions);
//    }
//
//    /**
//     * 覆写方法 解决截图存在大量空白区域问题
//     * @param page
//     * @param options
//     * @return
//     * @throws IOException
//     */
//    @SneakyThrows
//    public static String screenshot(Page page, ScreenshotOptions options) throws IOException {
//        String screenshotType = null;
//        // options.type takes precedence over inferring the type from options.path
//        // because it may be a 0-length file with no extension created beforehand (i.e. as a temp file).
//        if (StringUtil.isNotEmpty(options.getType())) {
//            ValidateUtil.assertArg("png".equals(options.getType()) || "jpeg".equals(options.getType()), "Unknown options.type value: " + options.getType());
//            screenshotType = options.getType();
//        } else if (StringUtil.isNotEmpty(options.getPath())) {
//            String mimeType = Files.probeContentType(Paths.get(options.getPath()));
//            if ("image/png".equals(mimeType))
//                screenshotType = "png";
//            else if ("image/jpeg".equals(mimeType))
//                screenshotType = "jpeg";
//            ValidateUtil.assertArg(StringUtil.isNotEmpty(screenshotType), "Unsupported screenshot mime type: " + mimeType);
//        }
//
//        if (StringUtil.isEmpty(screenshotType))
//            screenshotType = "png";
//
//        if (options.getQuality() > 0) {
//            ValidateUtil.assertArg("jpeg".equals(screenshotType), "options.quality is unsupported for the " + screenshotType + " screenshots");
//            ValidateUtil.assertArg(options.getQuality() <= 100, "Expected options.quality to be between 0 and 100 (inclusive), got " + options.getQuality());
//        }
//
//        ValidateUtil.assertArg(options.getClip() == null || !options.getFullPage(), "options.clip and options.fullPage are exclusive");
//        if (options.getClip() != null) {
//            ValidateUtil.assertArg(options.getClip().getWidth() != 0, "Expected options.clip.width not to be 0.");
//            ValidateUtil.assertArg(options.getClip().getHeight() != 0, "Expected options.clip.height not to be 0.");
//        }
//
//        Field screenshotTaskQueueField = ReflectUtil.getField(page.getClass(), "screenshotTaskQueue");
//        screenshotTaskQueueField.setAccessible(true);
//        TaskQueue<String> screenshotTaskQueue = (TaskQueue<String>) screenshotTaskQueueField.get(page);
//
//        return (String) screenshotTaskQueue.postTask((type, op) -> {
//            try {
//                return screenshotTask(page, type, op);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//        }, screenshotType, options);
//    }
//
//    @SneakyThrows
//    private static String screenshotTask(Page page, String format, ScreenshotOptions options) throws IOException, ExecutionException, InterruptedException {
//        Field targetField = ReflectUtil.getField(page.getClass(), "target");
//        targetField.setAccessible(true);
//        Target target = (Target) targetField.get(page);
//        Field clientField = ReflectUtil.getField(page.getClass(), "client");
//        clientField.setAccessible(true);
//        CDPSession client = (CDPSession) clientField.get(page);
//        Field viewportField = ReflectUtil.getField(page.getClass(), "viewport");
//        viewportField.setAccessible(true);
//        Viewport viewport = (Viewport) viewportField.get(page);
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("targetId", target.getTargetId());
//        client.send("Target.activateTarget", params, true);
//        ClipOverwrite clip = null;
////        if (options.getClip() != null) {
////            clip = processClip(options.getClip());
////        }
//        if (options.getFullPage()) {
//            JsonNode metrics = client.send("Page.getLayoutMetrics", null, true);
//            // 此处是存在大量空白区域的原因：原字段contentSize，改为cssContentSize
//            double width = Math.ceil(metrics.get("cssContentSize").get("width").asDouble());
//            double height = Math.ceil(metrics.get("cssContentSize").get("height").asDouble());
//
//            clip = new ClipOverwrite(0, 0, width, height, 1);
//            ScreenOrientation screenOrientation;
//            if (viewport.getIsLandscape()) {
//                screenOrientation = new ScreenOrientation(90, "landscapePrimary");
//            } else {
//                screenOrientation = new ScreenOrientation(0, "portraitPrimary");
//            }
//            params.clear();
//            params.put("mobile", viewport.getIsMobile());
//            params.put("width", width);
//            params.put("height", height);
//            params.put("deviceScaleFactor", viewport.getDeviceScaleFactor());
//            params.put("screenOrientation", screenOrientation);
//            client.send("Emulation.setDeviceMetricsOverride", params, true);
//        }
////        boolean shouldSetDefaultBackground = options.getOmitBackground() && "png".equals(format);
////        if (shouldSetDefaultBackground) {
////            setTransparentBackgroundColor();
////        }
//        params.clear();
//        params.put("format", format);
//        params.put("quality", options.getQuality());
//        params.put("clip", clip);
//        JsonNode result = client.send("Page.captureScreenshot", params, true);
////        if (shouldSetDefaultBackground) {
////            this.client.send("Emulation.setDefaultBackgroundColorOverride", null, true);
////        }
//        if (options.getFullPage() && viewport != null)
//            page.setViewport(viewport);
//        String data = result.get("data").asText();
////            byte[] buffer = decoder.decodeBuffer(data);
//        byte[] buffer = Base64.getDecoder().decode(data);
//        if (StringUtil.isNotEmpty(options.getPath())) {
//            Files.write(Paths.get(options.getPath()), buffer, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
//        }
//        return data;
//    }
//
//}
