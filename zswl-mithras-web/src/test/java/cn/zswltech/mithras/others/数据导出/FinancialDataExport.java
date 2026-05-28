//package cn.zswltech.mithras.others.数据导出;
//
//import cn.zswltech.mithras.dto.rating.RatingParamFieldRSP;
//import cn.zswltech.mithras.dto.rating.ratingclient.RatingQualitativeRSP;
//import cn.zswltech.mithras.others.service.ApplicationTest;
//import cn.zswltech.mithras.service.mapper.model.fund.FundFinancialSystemCallRecord;
//import cn.zswltech.mithras.service.service.fund.financial.FundFinancialSystemCallRecordService;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.Test;
//import org.springframework.test.context.ActiveProfiles;
//
//import javax.annotation.Resource;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Slf4j
//@ActiveProfiles(value = "pre")
//public class FinancialDataExport extends ApplicationTest  {
//
//    @Resource
//    private FundFinancialSystemCallRecordService financialSystemCallRecordService;
//
//
//    @Test
//    public void test(){
//        financialSystemCallRecordService.list(Wrappers.<FundFinancialSystemCallRecord>lambdaQuery()
//                .eq(FundFinancialSystemCallRecord::getBatchNumber, ""))
//    }
//
//    public void exportAreaInfo(Map<String, List<TempData>> resultMap){
//        // 创建一个新的工作簿
//        Workbook workbook = new XSSFWorkbook();
//        for (Map.Entry<String, List<TempData>> entry : resultMap.entrySet()) {
//            String modelKey = entry.getKey();
//
//            String[] headers = new String[]{"客户名称", "模型类型", "初评结果(原)", "初评结果(现)", "调整后评级(原)", "调整后评级(现)" ,"区域得分(原)", "区域得分(现)"};
//
//            List<TempData> tempDataList = entry.getValue();
//            // 创建一个工作表
//            Sheet sheet = workbook.createSheet(modelName);
//            // 创建表头
//            Row headerRow = sheet.createRow(0);
//
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//            }
//
//            // 计算二维数组的大小
//            int rowCount = tempDataList.size();
//
//            // 创建二维数组
//            Object[][] dataArray = new Object[rowCount][headers.length];
//
//            int j = 0;
//            // 遍历列表并填充二维数组
//            for (int i = 0; i < rowCount; i++) {
//                TempData tempData = tempDataList.get(i);
//                dataArray[i][j++] = tempData.getClientName();
//                dataArray[i][j++] = modelName;
//                dataArray[i][j++] = tempData.getFirstScore();
//                dataArray[i][j++] = tempData.getFirstScoreNew();
//                dataArray[i][j++] = tempData.getScore();
//                dataArray[i][j++] = tempData.getScoreNew();
//                dataArray[i][j++] = tempData.getAreaScore();
//                dataArray[i][j++] = tempData.getAreaScoreNew();
//                j = 0;
//            }
//
//
//            int rowNum = 1;
//            for (Object[] datum : dataArray) {
//                Row row = sheet.createRow(rowNum++);
//                for (int i = 0; i < datum.length; i++) {
//                    Cell cell = row.createCell(i);
//                    if (datum[i] instanceof Integer) {
//                        cell.setCellValue((Integer) datum[i]);
//                    } else if (datum[i] instanceof String) {
//                        cell.setCellValue((String) datum[i]);
//                    }
//                }
//            }
//
//            // 自动调整列宽
//            for (int i = 0; i < headers.length; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//        }
//
//        // 将工作簿写入文件
//        try (FileOutputStream fileOut = new FileOutputStream("/Users/zswl/Desktop/评级导出(区域)_1202.xlsx")) {
//            workbook.write(fileOut);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 关闭工作簿
//        try {
//            workbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//
//}
//
