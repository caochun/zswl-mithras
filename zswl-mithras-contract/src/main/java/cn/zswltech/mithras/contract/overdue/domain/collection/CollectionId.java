package cn.zswltech.mithras.contract.overdue.domain.collection;

import cn.zswltech.mithras.contract.overdue.domain.share.Identifier;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:40
 */
@Data
@NoArgsConstructor
public class CollectionId implements Identifier {
    private Long id;

    public CollectionId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionId)) return false;
        CollectionId that = (CollectionId) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean isNull() {
        return id ==null;
    }
}
