package cn.zswltech.mithras.document.file.bo;

import lombok.Data;

import java.util.List;

/**
 * 过滤查询额外扩展条件。
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:34 PM
 */
@Data
public class FileListExtQuery {

    /**
     * 文件类型
     */
    private List<String> materialsTypes;

    private List<String> materialsSubTypes;

    private Long clientId;

}
