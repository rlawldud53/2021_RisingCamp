package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@Getter
@Setter
@AllArgsConstructor
@JsonAutoDetect
public class PostCouponReq {
    private String couponCode;
    private int couponId;
}