package com.example.demo.src.mypage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mypage.model.*;
import com.example.demo.src.mypage.model.scrapbook.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Base64.Decoder;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/ohouse/mypage")
public class MypageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MypageProvider mypageProvider;
    @Autowired
    private final MypageService mypageService;
    @Autowired
    private final JwtService jwtService;

    public MypageController(MypageProvider mypageProvider, MypageService mypageService, JwtService jwtService){
        this.mypageProvider = mypageProvider;
        this.mypageService = mypageService;
        this.jwtService = jwtService;
    }

    /**
     * Followers 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/followers"/*, headers = "Authorization"*/)
    public BaseResponse<List<GetFollowers>> getFollowers (@PathVariable("userIdx") int userIdx) {
        try{
            int logonIdx = jwtService.getUserIdx();
            List<GetFollowers> getFollowers = mypageProvider.getFollowers(logonIdx,userIdx);
            return new BaseResponse<>(getFollowers);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * Following 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/followees") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<List<GetFollowers>> getFollowing (@PathVariable("userIdx") int userIdx/*, @PathVariable("logonIdx") int logonIdx, @RequestHeader("Authorization") String jwtToken*/) {
        try{
            int logonIdx = jwtService.getUserIdx();
            List<GetFollowers> getFollowers = mypageProvider.getFollowing(logonIdx,userIdx);
            return new BaseResponse<>(getFollowers);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Coupon 조회 API
     */
    @ResponseBody
    @GetMapping("/my-shopping/coupons")
    public BaseResponse<List<GetCoupons>> getCoupons () {
        try{
            int myIdx = jwtService.getUserIdx();
            List<GetCoupons> getCoupons = mypageProvider.getCoupons(myIdx);
            return new BaseResponse<>(getCoupons);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Coupon 받기 API
     */
    @ResponseBody
    @PostMapping("/my-shopping/coupons")
    public BaseResponse<String> PostPcouponsReq(@RequestBody PostCouponReq postPcouponsReq) {
        try{
            int myIdx = jwtService.getUserIdx();
            int received = mypageProvider.checkReceived(myIdx,postPcouponsReq);
            // 이미 받은 쿠폰인지 확인
            if ( received == 1)
                return new BaseResponse<>(ALREADY_RECEIVED_COUPON);
            mypageService.postPcouponsReq(myIdx,postPcouponsReq);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Coupon 발급 API 2 (Coupon code)
     */
    @ResponseBody
    @PostMapping("/my-shopping/coupons/new")
    public BaseResponse<String> postCouponCode(@RequestBody PostCouponReq postCouponReq/*@RequestBody PostCodeReq postCodeReq*/) {
        try{
            int myIdx = jwtService.getUserIdx();
            String code = postCouponReq.getCouponCode();
            // 아무 값도 입력하지 않은 경우
            if(/*postCodeReq.getCouponCode()*/ code.equals(""))
                return new BaseResponse<>(EMPTY_COUPON_CODE);

            String used = mypageProvider.checkUsed(/*postCodeReq*/code);
            // 이미 받은 쿠폰인지 확인
            if ( !used.equals("Y") )
                return new BaseResponse<>(INVALID_COUPON_CODE);
            mypageService.postCodeReq(myIdx,/*postCodeReq*/code);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 포인트 조회 API
     */
    @ResponseBody
    @GetMapping("/my-shopping/points")
    public BaseResponse<GetPoints> getPoints () {
        try{
            int myIdx = jwtService.getUserIdx();
            List<Point> pointList = mypageProvider.getPoints(myIdx);
            int usablePoints = mypageProvider.getUsablePoints(myIdx);
            GetPoints getPoints = new GetPoints(usablePoints, pointList);
            return new BaseResponse<>(getPoints);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 스크랩북 상단 UserName 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/scrapbook/userName")
    public BaseResponse<String> getUserName (@PathVariable("userIdx") int userIdx) {
        try{
            String getUserName = mypageProvider.getUserName(userIdx);
            return new BaseResponse<>(getUserName);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 모든 스크랩북 & 좋아요 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/{filter}")
    public BaseResponse<List<GetAllScraps>> getAllScraps (@PathVariable("userIdx") int userIdx,@PathVariable("filter") String filter) {
        try{
            //int myIdx = jwtService.getUserIdx();
            if(!(filter.equals("scrapbook")||filter.equals("praises")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            List<GetAllScraps> getAllScraps = mypageProvider.getAllScraps(userIdx, filter);
            return new BaseResponse<>(getAllScraps);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 사진 스크랩북 & 좋아요 조회 API
     */

    @ResponseBody
    @GetMapping("/{userIdx}/{filter}/pictures")
    public BaseResponse<List<GetPicScraps>> getPicScraps (@PathVariable("userIdx") int userIdx,@PathVariable("filter") String filter) {
        try{
            //int myIdx = jwtService.getUserIdx();
            if(!(filter.equals("scrapbook")||filter.equals("praises")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            List<GetPicScraps> getPicScraps = mypageProvider.getPicScraps(userIdx, filter);
            return new BaseResponse<>(getPicScraps);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 스크랩북 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/scrapbook/products")
    public BaseResponse<List<GetProdScraps>> getProdScraps (@PathVariable("userIdx") int userIdx) {
        try{
            //int myIdx = jwtService.getUserIdx();
            List<GetProdScraps> getProdScraps = mypageProvider.getProdScraps(userIdx);
            return new BaseResponse<>(getProdScraps);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 노하우&집들이 스크랩북 & 좋아요 조회 API
     */
    @ResponseBody
    @GetMapping("/{userIdx}/{filter}/{contents}")
    public BaseResponse<List<GetContentScraps>> getContentScraps (@PathVariable("userIdx") int userIdx, @PathVariable("filter") String filter, @PathVariable("contents") String contents) {
        try{
            //int myIdx = jwtService.getUserIdx();
            //String filter = "scrapbook";
            if(!(filter.equals("scrapbook")||filter.equals("praises")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            if(!(contents.equals("houses")||contents.equals("knowhows")))
                return new BaseResponse<>(INVALID_USER_ACCESS);
            List<GetContentScraps> getContentScraps = mypageProvider.getContentScraps(userIdx, filter, contents);
            return new BaseResponse<>(getContentScraps);
        } catch(BaseException exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}