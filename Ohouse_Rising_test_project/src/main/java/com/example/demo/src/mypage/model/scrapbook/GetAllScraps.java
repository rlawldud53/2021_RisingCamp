package com.example.demo.src.mypage.model.scrapbook;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAllScraps {
    private String imagePath;
    private String flag;
    private int contentIdx;
}