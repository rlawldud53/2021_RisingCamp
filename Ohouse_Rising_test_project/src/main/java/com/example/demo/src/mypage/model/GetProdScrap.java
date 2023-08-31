package com.example.demo.src.mypage.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProdScrap {
    private String imagePath;
    private String companyName;
    private String productName;
    private int salePercent;
    private int productPrice;
    private float rate;
    private int reviewNum;
}