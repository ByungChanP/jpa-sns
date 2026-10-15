package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable // 다른 Entity에 내장되어 있어 PK를 사용하지 않는 경우
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(length = 10)
    private String zipcode;

    @Column
    private String roadAddress;

    @Column(length = 100)
    private String detailAddress;

    public Address(String zipcode, String roadAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
