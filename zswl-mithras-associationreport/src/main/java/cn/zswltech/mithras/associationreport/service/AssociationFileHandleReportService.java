package cn.zswltech.mithras.associationreport.service;

import cn.zswltech.mithras.associationreport.excel.CsvColumn;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2025/4/21
 * 租赁协会文件生成报送
 */
@Slf4j
@Service
public class AssociationFileHandleReportService {

    @Value("${association.sftpHost:}")
    private String sftpHost;
    @Value("${association.sftpPort:}")
    private int sftpPort;
    @Value("${association.sftpUser:}")
    private String sftpUser;
    @Value("${association.sftpPassword:}")
    private String sftpPassword;

    /**
     * @param fileName, 文件名称
     * @param dataList 传输数据
     * @param remoteDir 目录
     * @param
     **/
    public <T> void createAndTransferCsv(String fileName, List<T> dataList, String remoteDir, Map<String, Integer> reportSortMap) throws Exception {
        // 1. 生成CSV文件
        File csvFile = generateCsvFile(fileName, dataList, reportSortMap);
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);
        ChannelSftp sftpChannel = null;
        // 2. SFTP传输
        try {
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(30000);//ms
            session.connect();
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            // 创建远程目录（如果不存在）
            mkdirs(sftpChannel, remoteDir);
            String reportFileName = csvFile.getName();
            String tempFileName = csvFile.getName().replace(".csv", ".tmp");

            // 传输文件
            try (FileInputStream fis = new FileInputStream(csvFile)) {
                sftpChannel.put(fis, remoteDir + "/" + tempFileName);
            } catch (Exception e) {
                log.error("上传远程文件异常", e);
                throw new MithrasException("上传远程文件异常");
            }
            //修改文件名称
            sftpChannel.rename(remoteDir + "/" + tempFileName, remoteDir + "/" + reportFileName);
        } catch (Exception e) {
            log.error("上传远程文件异常", e);
            throw new MithrasException("上传远程文件异常");
        } finally {
            // 3. 删除本地临时文件（可选）
            sftpChannel.disconnect();
            session.disconnect();
            csvFile.delete();
        }
    }

    //生成文件
    private <T> File generateCsvFile(String fileName, List<T> dataList, Map<String, Integer> reportSortMap) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            throw new MithrasException("数据不能为空");
        }
        File file = new File(fileName);
        Class<?> clazz = dataList.get(0).getClass();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // 写入表头
            List<FieldMetadata> fields = getAllFiled(clazz).stream().filter(field -> !isIgnored(field))
                    .map(field -> {
                        return new FieldMetadata(
                                field,
                                field.getName(),
                                reportSortMap != null ? reportSortMap.getOrDefault(field.getName(), Integer.MAX_VALUE) : Integer.MAX_VALUE
                        );
                    })
                    .sorted(Comparator.comparingInt(meta -> meta.order))
                    .collect(Collectors.toList());

            writer.write(String.join("|@|", getFieldNames(fields)) );
            writer.newLine();
            // 写入数据
            for (T item : dataList) {
                writer.write(String.join("|@|", getFieldValues(item, fields)));
                writer.newLine();
            }
        }
        return file;
    }

    private List<Field> getAllFiled(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        while (cls != null) {
            // 获取当前类的所有字段（包括私有）
             fields.addAll(Arrays.stream(cls.getDeclaredFields()).collect(Collectors.toList()));
            // 继续获取超类的字段
            cls = cls.getSuperclass();
        }
        return fields;
    }

    /**
     * 判断字段是否需要忽略
     */
    private boolean isIgnored(Field field) {
        CsvColumn ann = field.getAnnotation(CsvColumn.class);
        return ann != null && ann.ignored();
    }

    private static class FieldMetadata {
        final Field field;
        final String headerName;
        final int order;

        FieldMetadata(Field field, String headerName, int order) {
            this.field = field;
            this.headerName = headerName;
            this.order = order;
        }
    }

    private String[] getFieldNames(List<FieldMetadata> fields) {
        String[] names = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            names[i] = processField(camelToUnderscore(fields.get(i).headerName));
        }
        return names;
    }

    /**
     * 驼峰转下划线命名
     * @param camelStr 驼峰格式字符串，例如 "agmtMatuDate"
     * @return 下划线格式字符串，例如 "agmt_matu_date"
     */
    public String camelToUnderscore(String camelStr) {
        if (camelStr == null || camelStr.isEmpty()) {
            return camelStr;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char currentChar = camelStr.charAt(i);

            // 处理首字符（不添加前置下划线）
            if (i == 0) {
                result.append(Character.toLowerCase(currentChar));
                continue;
            }

            // 遇到大写字母时，添加下划线并转小写
            if (Character.isUpperCase(currentChar)) {
                result.append('_')
                        .append(Character.toLowerCase(currentChar));
            }
           /* // 处理数字前加下划线（可选）
            else if (Character.isDigit(currentChar) &&
                    !Character.isDigit(camelStr.charAt(i-1))) {
                result.append('_').append(currentChar);
            }*/
            // 其他字符直接追加
            else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * 处理字符串转义并按规则包裹双引号
     *
     * @param input 原始输入字符串
     * @return 处理后的字符串
     */
    public String processField(String input) {
        // 处理空值情况
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 步骤1：转义半角双引号（将"替换为""）
        String escapedStr = input.replace("\"", "\"\"");

        // 步骤2：判断是否需要包裹双引号
        boolean needWrap =
                input.contains("\"")           // 原始字符串含双引号（转义后需要包裹）
                        || escapedStr.contains(",")    // 转义后含逗号
                        || escapedStr.contains("|")    // 转义后含单竖线
                        || escapedStr.contains("|@|"); // 转义后含组合分隔符|@|

        // 步骤3：根据条件包裹双引号
        //return needWrap ? "\"" + escapedStr + "\"" : escapedStr;
        return "\"" + escapedStr + "\"";
    }


    private <T> String[] getFieldValues(T item, List<FieldMetadata> fields) {
        String[] values = new String[fields.size()];
        try {
            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).field.setAccessible(true);
                Object value = fields.get(i).field.get(item);
                if (fields.get(i).field.getType() == BigDecimal.class) {
                    values[i] = (value != null) ? processField(((BigDecimal) value).setScale(8, RoundingMode.HALF_UP).toPlainString()) : "0.00";
                } else {
                    values[i] = (value != null) ? processField(value.toString()) : "";
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Error accessing field values", e);
            throw new MithrasException("访问字段值时出错");
        }
        return values;
    }

    private void mkdirs(ChannelSftp sftp, String path) throws SftpException {
        String[] folders = path.split("/");
        String currentPath = "";
        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            currentPath += "/" + folder;
            try {
                sftp.stat(currentPath);
            } catch (SftpException e) {
                sftp.mkdir(currentPath);
            }
        }
    }
}