package space.banterbox.feature.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class UsersFollowerId implements Serializable {
    private static final long serialVersionUID = 1221832017302281430L;
    @NotNull
    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @NotNull
    @Column(name = "following_id", nullable = false)
    private UUID followingId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersFollowerId entity = (UsersFollowerId) o;
        return Objects.equals(this.followingId, entity.followingId) &&
                Objects.equals(this.followerId, entity.followerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followingId, followerId);
    }

}