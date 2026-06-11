package cn.zswltech.mithras.foundation.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * @author frank
 * @date 2024/6/27
 * @description
 */
public class LinkStyleHandler implements CellWriteHandler {
    private Map<String,Integer> map;

    public LinkStyleHandler(Map<String, Integer> map) {
        this.map = map;
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer integer, Integer integer1, Boolean aBoolean) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer integer, Boolean aBoolean) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
        int col = cell.getColumnIndex();
        if(col == 1){
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            String linkName = (cell.getStringCellValue().length()>28?cell.getStringCellValue().substring(0,28):cell.getStringCellValue()).toLowerCase();
            CreationHelper helper = workbook.getCreationHelper();
            Hyperlink hyperlink = helper.createHyperlink(HyperlinkType.DOCUMENT);
            if(map.containsKey(linkName)){
                map.put(linkName,map.get(linkName)+1);
                hyperlink.setAddress("#"+linkName+"_"+map.get(linkName)+"!A1");
            }else{
                map.put(linkName,1);
                hyperlink.setAddress("#"+linkName+"!A1");
            }
            cell.setHyperlink(hyperlink);
        }
    }
}

