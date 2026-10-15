package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.likelion.bebc25.jpasns.common.entity.BasePostReaction;
import net.likelion.bebc25.jpasns.common.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_bookmark_member_post", columnNames = {"member_id", "post_id"})
})
public class Bookmark extends BasePostReaction {

    public Bookmark(Member member, Post post) {
        super(member, post);
    }
}
