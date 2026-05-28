package cn.zswltech.mithras.dto.version;

import cn.zswltech.mithras.dto.file.FileListRSP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiffFile extends FileListRSP {

    /**
     * 变动类型
     * @see cn.zswltech.mithras.service.enums.InfoOperation
     */
    private String changeType;

    /**
     * 是否变动
     */
    private Boolean isChange;
}
