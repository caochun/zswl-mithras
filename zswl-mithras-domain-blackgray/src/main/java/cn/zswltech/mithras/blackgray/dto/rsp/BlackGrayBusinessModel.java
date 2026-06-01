package cn.zswltech.mithras.blackgray.dto.rsp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BlackGrayBusinessModel
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/13 2:13 下午
 * @Version 1.0
 **/
@Data
@Builder
public class BlackGrayBusinessModel {

    private String dName;

    private String name;

    private List<BlackGrayBusinessModel> children;

}
