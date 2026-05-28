package cn.zswltech.mithras.service.service.bo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/9/26
 * @description
 */
@Data
public class ProjectBizTypeBO {
    private String projectBizType;
    private List<String> leaseTypeList;

    public String text() {
        if (StrUtil.isBlank(projectBizType)) {
            return null;
        }
        ProjectBizType pbt = ProjectBizType.of(projectBizType);
        if (Objects.isNull(pbt)) {
            return null;
        }
        // 保理或者债权转让直接展示大类型
        if (pbt == ProjectBizType.BL || pbt == ProjectBizType.ZR) {
            return pbt.display;
        }
        // 租赁或者转租赁展示小类型，多个用顿号分隔
        if (CollectionUtil.isNotEmpty(leaseTypeList)) {
            List<String> nameList = new ArrayList<>(leaseTypeList.size());
            for (String leaseType : leaseTypeList) {
                LeaseType lt = LeaseType.of(leaseType);
                if (Objects.isNull(lt)) {
                    continue;
                }
                nameList.add(lt.display);
            }
            return CharSequenceUtil.join("、", nameList);
        }
        return null;
    }
}
