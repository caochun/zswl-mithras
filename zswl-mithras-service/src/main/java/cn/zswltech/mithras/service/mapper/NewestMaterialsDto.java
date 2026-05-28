package cn.zswltech.mithras.service.mapper;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/15 19:16
 */
@Data
public class NewestMaterialsDto {

    private String businessType;
    private List<String> materialsTypes = new ArrayList<>();
    private List<Long> belongIds;
}
