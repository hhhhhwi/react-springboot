package com.hwiyeon.reactspringboot.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="MEMBER")
public class Member {
    @Id // pk
    @Column(name="MEMBER_ID")
    private Long memberId;
    private String name;

    public Member() {
    }

    public Member(Long memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }
}
