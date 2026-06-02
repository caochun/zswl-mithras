package cn.zswltech.mithras.contract.overdue.domain.collection;

import cn.zswltech.mithras.contract.overdue.domain.share.Identifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionActionId implements Identifier {
    private Long id;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionActionId)) return false;
        CollectionActionId that = (CollectionActionId) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean isNull() {
        return id == null;
    }
}
