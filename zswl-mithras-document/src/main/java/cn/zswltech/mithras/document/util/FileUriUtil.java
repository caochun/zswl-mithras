package cn.zswltech.mithras.document.util;

import cn.hutool.core.io.file.FileNameUtil;
import java.util.Set;

/**
 * @create: 2022-07-28
 **/

public class FileUriUtil {
    public static String fileNameDeduplication(Set<String> pathset,String filepath){
        return getNewFileNameDeduplication(pathset,filepath,filepath,1);
    }
    public static String getNewFileNameDeduplication(Set<String> pathset,String filepath,String newFilepath,int i){
        String fileUrl = filepath;
        if (!pathset.contains(newFilepath)){
            pathset.add(newFilepath);
            return newFilepath;
        }else {
            String suffix = FileNameUtil.getSuffix(fileUrl);
            int indexOf = fileUrl.lastIndexOf(suffix);
            if (indexOf == fileUrl.length()) {
                fileUrl = fileUrl + "(" + i + ")";
            } else {
                fileUrl = fileUrl.substring(0, indexOf - 1) + "(" + i + ")" + fileUrl.substring(indexOf - 1);
            }
            return getNewFileNameDeduplication(pathset, filepath,fileUrl, ++i);
        }
    }

}
