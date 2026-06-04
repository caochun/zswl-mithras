package cn.zswltech.mithras.projectprocess.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yupengfei
 * @date 2024/4/18 16:17
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContractConstitutionFileBO {
    /**
     * 承租人/担保措施 id
     */
    private Long tenantryId;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 章程文件
     */
    private List<MultipartFile> multipartFileList;

    /**
     * 章程文件id
     */
    private List<Long> constitutionFileIds;

    /**
     * 文件类型
     */
    private String fileType;

}
