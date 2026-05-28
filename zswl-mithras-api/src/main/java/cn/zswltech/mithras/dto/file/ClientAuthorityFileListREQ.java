package cn.zswltech.mithras.dto.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientAuthorityFileListREQ extends FileListREQ {
    private String batchNo;
}
