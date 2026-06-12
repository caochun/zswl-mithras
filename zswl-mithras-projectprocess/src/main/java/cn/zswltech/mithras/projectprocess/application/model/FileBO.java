package cn.zswltech.mithras.projectprocess.application.model;

import cn.zswltech.mithras.dto.MaterialsListIdType;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/2/28
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileBO {
    private Long fileId;
    private Integer idType;
    private String fileName;
    private String materialType;

    public static FileBO copyFromMaterial(MaterialsList materialsList) {
        return new FileBO(materialsList.getId(), MaterialsListIdType.EDIT_AREA, materialsList.getFilename(), materialsList.getMaterialsType());
    }

    public static FileBO copyFromMaterialLib(MaterialsListLib materialsListLib) {
        return new FileBO(materialsListLib.getId(), MaterialsListIdType.VERSIONED, materialsListLib.getFilename(), materialsListLib.getMaterialsType());
    }
}
