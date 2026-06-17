package cn.zswltech.mithras.contract.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 合同组成文件保存/查询/删除命令。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContractConstitutionFileCommand {

    /**
     * 承租人或担保措施 id。
     */
    private Long tenantryId;

    /**
     * 合同 id。
     */
    private Long contractId;

    /**
     * 待上传文件。
     */
    private List<MultipartFile> multipartFileList;

    /**
     * 已保留的组成文件 id。
     */
    private List<Long> constitutionFileIds;

    /**
     * 文件类型编码。
     */
    private String fileType;
}
