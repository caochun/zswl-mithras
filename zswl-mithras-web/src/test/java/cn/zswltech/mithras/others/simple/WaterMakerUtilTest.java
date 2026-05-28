package cn.zswltech.mithras.others.simple;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.service.util.WatermarkUtil;
import lombok.SneakyThrows;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author luyi
 */
public class WaterMakerUtilTest {

    public static Path DIR = Paths.get(System.getProperty("user.home"), "Downloads");

    @SneakyThrows
    public static void main(String[] args) {
//        doc();
//        docx();
//        xls();
//        xlsx();
//        pdf();
//        ppt();
//        pptx();
        png();
    }

    @SneakyThrows
    public static void doc() {
        String filename = "aa2003.doc";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void docx() {
        String filename = "2023.docx";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void xls() {
        String filename = "2003.xls";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void xlsx() {
        String filename = "2023.xlsx";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void pdf() {
        String filename = "2023.pdf";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void ppt() {
        String filename = "2003.ppt";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void pptx() {
        String filename = "2023.pptx";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SneakyThrows
    private static void png() {
        String filename = "WechatIMG62.jpg";
        Path path = Paths.get(DIR.toString(), filename);
        Path target = Paths.get(path.getParent().toString(), "Marked-" + filename);
        FileOutputStream fileOutputStream;
        Pair<String, InputStream> pair = WatermarkUtil.watermark(
                Files.newInputStream(path.toFile().toPath()),
                "浙商租赁",
                path.toFile().getName()
        );
        try {
            fileOutputStream = new FileOutputStream(target.toFile());
            IoUtil.copy(pair.getValue(), fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
