package cn.zswltech.mithras.contract.overdue.domain.litigation;

import cn.zswltech.mithras.contract.overdue.domain.share.Identifier;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:04
 */
@Data
@NoArgsConstructor
public class LongId implements Identifier, Comparable<LongId> {
    private Long id;

    public LongId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongId)) return false;
        LongId that = (LongId) o;
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

    @Override
    public int compareTo(LongId o) {
        return id.compareTo(o.id);
    }
}
