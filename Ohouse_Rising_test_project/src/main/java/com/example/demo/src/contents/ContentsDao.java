package com.example.demo.src.contents;


import com.example.demo.src.contents.model.*;
import com.example.demo.src.contents.model.house.*;
import com.example.demo.src.contents.model.knowhow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Repository
public class ContentsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     House Intro 조회 API
     */
    public List<GetHouseIntro> getHouseIntro(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getHouseIntroParams = new Object[]{userIdx, contentIdx};

        String getHouseIntroQuery = "SELECT H.coverImage, H.title, H.house, H.houseSize, H.work, H.worker, H.family, DATE(H.createdAt) createdAt, U.userName, U.userIdx, U.userIntro, IFNULL(followers2.status, 'N') AS followedByUser\n" +
                "FROM House H\n" +
                "INNER JOIN User U ON H.userIdx = U.userIdx\n" +
                "LEFT JOIN (SELECT F.status, F.userIdx FROM UserFollow F\n" +
                "                WHERE F.followuserIdx = ?) followers2 ON followers2.userIdx = H.userIdx\n" +
                "WHERE H.houseIdx = ?";

        return this.jdbcTemplate.query(getHouseIntroQuery,
                (rs, rowNum) -> new GetHouseIntro(
                        rs.getString("coverImage"),
                        rs.getString("title"),
                        rs.getString("house"),
                        rs.getString("houseSize"),
                        rs.getString("work"),
                        rs.getString("worker"),
                        rs.getString("family"),
                        rs.getString("createdAt"),
                        rs.getString("userName"),
                        rs.getInt("userIdx"),
                        rs.getString("userIntro"),
                        rs.getString("followedByUser")),
                getHouseIntroParams
        );
        //return this.jdbcTemplate.queryForObject(getHouseIntroQuery, GetHouseIntro.class, getHouseIntroParams);
    }

    /**
     Knowhow Intro 조회 API
     */
    public List<GetKnowhowIntro> getKnowhowIntro(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getKnowhowIntroParams = new Object[]{userIdx, contentIdx};

        String getKnowhowIntroQuery = "SELECT K.coverImage, K.title, DATE(K.createdAt) createdAt, U.userName, U.userIdx, U.userIntro, KHC.categoryName, IFNULL(followers2.status, 'N') AS followedByUser\n" +
                "FROM Knowhow K\n" +
                "INNER JOIN User U ON K.userIdx = U.userIdx\n" +
                "INNER JOIN KnowHowCategory KHC on K.knowhowCategory = KHC.knowHowCtgIdx\n" +
                "LEFT JOIN (SELECT F.status, F.userIdx FROM UserFollow F\n" +
                "                WHERE F.followuserIdx = ?) followers2 ON followers2.userIdx = K.userIdx\n" +
                "WHERE K.knowhowIdx = ?";

        return this.jdbcTemplate.query(getKnowhowIntroQuery,
                (rs, rowNum) -> new GetKnowhowIntro(
                        rs.getString("coverImage"),
                        rs.getString("title"),
                        rs.getString("createdAt"),
                        rs.getString("userName"),
                        rs.getInt("userIdx"),
                        rs.getString("userIntro"),
                        rs.getString("categoryName"),
                        rs.getString("followedByUser")),
                getKnowhowIntroParams
        );
        //return this.jdbcTemplate.queryForObject(getHouseIntroQuery, GetHouseIntro.class, getHouseIntroParams);
    }



    /**
     knowhow 중간 컨텐츠 조회 API
     */
    public List<GetKnowhowContents> getKnowhowContents(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;

        String getKnowhowContentIdxQuery = "SELECT  KC.contentIdx\n" +
                "FROM KnowHowContent KC\n" +
                "WHERE KC.KnowHowIdx =?";
        String getKnowhowContentQuery = "SELECT KC.knowhowImage, KC.knowhowText\n" +
                "FROM KnowHowContent KC\n" +
                "WHERE KC.KnowHowIdx = ?";
        String getproductQuery = "SELECT PI.productImage\n" +
                "FROM ProductImage PI\n" +
                "INNER JOIN KnowHowProduct KP on KP.productIdx = PI.productIdx AND PI.imageFlag = 'Y'\n" +
                "WHERE KP.ContentIdx = ?";

        List<Integer> ContentIdxList = this.jdbcTemplate.queryForList(getKnowhowContentIdxQuery, Integer.class, contentIdxParams);
        List<GetTempKnowhowContents> TempContentsList = this.jdbcTemplate.query(getKnowhowContentQuery,
                (rs, rowNum) -> new GetTempKnowhowContents(
                        rs.getString("knowhowImage"),
                        rs.getString("knowhowText")),
                contentIdxParams);
        List<GetKnowhowContents> result = new ArrayList<GetKnowhowContents>();

       for(int i = 0; i < ContentIdxList.size(); i++){
            List<String> products = this.jdbcTemplate.queryForList(getproductQuery , String.class, ContentIdxList.get(i));
            result.add(new GetKnowhowContents(TempContentsList.get(i),products));
        }

       return result;
    }

    /**
     집들이 중간 컨텐츠 조회 API
     */
    public List<GetHouseContents> getHouseContents(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;

        String getHouseContentIdxQuery = "SELECT  HC.HouseContentIdx\n" +
                "FROM HouseContent HC\n" +
                "WHERE HC.houseIdx =?";
        String getHouseContentQuery = "SELECT HC.houseImage, HC.houseText\n" +
                "FROM HouseContent HC\n" +
                "WHERE HC.houseIdx =?";
        String getproductQuery = "SELECT PI.productImage\n" +
                "FROM ProductImage PI\n" +
                "INNER JOIN HouseProduct HP on HP.productIdx = PI.productIdx AND PI.imageFlag = 'Y'\n" +
                "WHERE HP.HouseContentIdx = ?";

        List<Integer> ContentIdxList = this.jdbcTemplate.queryForList(getHouseContentIdxQuery, Integer.class, contentIdxParams);
        List<GetTempHouseContents> TempContentsList = this.jdbcTemplate.query(getHouseContentQuery,
                (rs, rowNum) -> new GetTempHouseContents(
                        rs.getString("houseImage"),
                        rs.getString("houseText")),
                contentIdxParams);
        List<GetHouseContents> result = new ArrayList<GetHouseContents>();

        for(int i = 0; i < ContentIdxList.size(); i++){
            List<String> products = this.jdbcTemplate.queryForList(getproductQuery , String.class, ContentIdxList.get(i));
            result.add(new GetHouseContents(TempContentsList.get(i),products));
        }

        return result;
    }

    /**
     집들이 SocialInfo 조회 API
     */
    public List<GetSocialInfo> getSocialInfo(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        int commentsCnt = 0;
        Object[] getSocialInfoParams = new Object[]{userIdx, contentIdx};
        Object[] getSocialInfoParams2 = new Object[]{contentIdx,contentIdx};

        String getLikeInfoQuery = "SELECT IFNULL(UL2.cnt,0) cnt, IFNULL(UL3.status,'N') AS likedByUser\n" +
                "FROM (SELECT UL.houseIdx, count(UL.houseIdx) cnt, UL.status FROM UserLike UL GROUP BY UL.houseIdx) UL2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(UL.houseIdx,0) houseIdx, UL.status FROM UserLike UL WHERE UL.userIdx = ? and UL.status = 'Y') UL3 ON UL3.houseIdx = UL2.houseIdx\n" +
                "WHERE UL2.houseIdx = ? AND UL2.status = 'Y'";

        String getScrapInfoQuery = "SELECT IFNULL(US2.cnt,0) cnt, IFNULL(UL3.status,'N') AS scrappedByUser\n" +
                "FROM (SELECT US.houseIdx, count(US.houseIdx) cnt, US.status FROM UserScrap US GROUP BY US.houseIdx) US2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(US.houseIdx,0) houseIdx, US.status FROM UserScrap US WHERE US.userIdx = ? and US.status = 'Y') UL3 ON UL3.houseIdx = US2.houseIdx\n" +
                "WHERE US2.houseIdx = ? AND US2.status = 'Y'";

        String getCommentsCntQuery = "SELECT (IFNULL(C2.cnt,0)) + (IFNULL(sum(Rec2.cnt),0)) AS cnt\n" +
                "FROM (SELECT C.houseIdx, count(C.houseIdx) cnt, C.status FROM Comment C GROUP BY C.houseIdx) C2\n" +
                "LEFT OUTER JOIN (SELECT Rec.commentIdx, count(Rec.recommentIdx) cnt, Rec.status FROM Recomment Rec\n" +
                "    INNER JOIN Comment C ON C.commentIdx = Rec.commentIdx AND C.houseIdx = ?\n" +
                "    GROUP BY Rec.commentIdx) Rec2 ON 1\n" +
                "WHERE C2.houseIdx = ? AND C2.status = 'Y'";


        List<SocialInfoFormat> LikeInfo = this.jdbcTemplate.query(getLikeInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("likedByUser")),
                getSocialInfoParams
        );
        List<SocialInfoFormat> ScrapInfo = this.jdbcTemplate.query(getScrapInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("scrappedByUser")),
                getSocialInfoParams
        );

        if(LikeInfo.size() == 0)
            LikeInfo.add(new SocialInfoFormat(0,"N"));
        if(ScrapInfo.size()==0)
            ScrapInfo.add(new SocialInfoFormat(0,"N"));

        try{
            commentsCnt = this.jdbcTemplate.queryForObject(getCommentsCntQuery, int.class, getSocialInfoParams2);
        }
        catch(EmptyResultDataAccessException exception){
            List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
            result.add(new GetSocialInfo(LikeInfo, ScrapInfo, 0));
            return result;
        }

        GetSocialInfo getSocialInfo = new GetSocialInfo(LikeInfo, ScrapInfo, commentsCnt);
        List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
        result.add(getSocialInfo);

        return result;
    }

    /**
     Knowhow SocialInfo 조회 API
     */
    public List<GetSocialInfo> getKnowhowSocialInfo(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        int commentsCnt = 0;
        Object[] getSocialInfoParams = new Object[]{userIdx, contentIdx};
        Object[] getSocialInfoParams2 = new Object[]{contentIdx,contentIdx};

        String getLikeInfoQuery = "SELECT IFNULL(UL2.cnt,0) cnt, IFNULL(UL3.status,'N') AS likedByUser\n" +
                "FROM (SELECT UL.knowhowIdx, IFNULL(count(UL.knowhowIdx),0) cnt, UL.status FROM UserLike UL GROUP BY UL.knowhowIdx) UL2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(UL.knowhowIdx,0) knowhowIdx, UL.status FROM UserLike UL WHERE UL.userIdx = ? and UL.status = 'Y') UL3 ON UL3.knowhowIdx = UL2.knowhowIdx\n" +
                "WHERE UL2.knowhowIdx = ? AND UL2.status = 'Y'";

        String getScrapInfoQuery = "SELECT IFNULL(US2.cnt,0) cnt, IFNULL(UL3.status,'N') AS scrappedByUser\n" +
                "FROM (SELECT US.knowhowIdx, count(US.knowhowIdx) cnt, US.status FROM UserScrap US GROUP BY US.knowhowIdx) US2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(US.knowhowIdx,0) knowhowIdx, US.status FROM UserScrap US WHERE US.userIdx = ? and US.status = 'Y') UL3 ON UL3.knowhowIdx = US2.knowhowIdx\n" +
                "WHERE US2.knowhowIdx = ? AND US2.status = 'Y'";

        String getCommentsCntQuery = "SELECT (IFNULL(C2.cnt,0)) + (IFNULL(sum(Rec2.cnt),0)) AS cnt\n" +
                "FROM (SELECT C.knowhowIdx, count(C.knowhowIdx) cnt, C.status FROM Comment C GROUP BY C.knowhowIdx) C2\n" +
                "LEFT OUTER JOIN (SELECT Rec.commentIdx, count(Rec.recommentIdx) cnt, Rec.status FROM Recomment Rec\n" +
                "    INNER JOIN Comment C ON C.commentIdx = Rec.commentIdx AND C.knowhowIdx = ?\n" +
                "    GROUP BY Rec.commentIdx) Rec2 ON 1\n" +
                "WHERE C2.knowhowIdx = ? AND C2.status = 'Y'\n";


        List<SocialInfoFormat> LikeInfo = this.jdbcTemplate.query(getLikeInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("likedByUser")),
                getSocialInfoParams
        );
        List<SocialInfoFormat> ScrapInfo = this.jdbcTemplate.query(getScrapInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("scrappedByUser")),
                getSocialInfoParams
        );

        if(LikeInfo.size() == 0)
            LikeInfo.add(new SocialInfoFormat(0,"N"));
        if(ScrapInfo.size()==0)
            ScrapInfo.add(new SocialInfoFormat(0,"N"));

        try{
            commentsCnt = this.jdbcTemplate.queryForObject(getCommentsCntQuery, int.class, getSocialInfoParams2);
        }
        catch(EmptyResultDataAccessException exception){
            List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
            result.add(new GetSocialInfo(LikeInfo, ScrapInfo, 0));
            return result;
        }

        GetSocialInfo getSocialInfo = new GetSocialInfo(LikeInfo, ScrapInfo, commentsCnt);
        List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
        result.add(getSocialInfo);

        return result;
    }

    /**
     집들이 최신 댓글 조회 API
     */
    public List<GetComments> getComments(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getCommentsParams = new Object[]{userIdx, contentIdx};

        String getCommentsQuery = "SELECT U.userName,C.cText, IFNULL(CL2.cnt,0) likeCnt, IFNULL(CL3.likeFlag,'N') AS likedByUser,\n" +
                "       IF(timestampdiff(year, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(year, C.createdAt,current_timestamp),'년'),\n" +
                "           IF(timestampdiff(month, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(month, C.createdAt,current_timestamp),'달'),\n" +
                "               IF(timestampdiff(day, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(day, C.createdAt,current_timestamp),'일'),\n" +
                "                   IF(timestampdiff(hour, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(hour, C.createdAt,current_timestamp),'시간'),\n" +
                "                       IF(timestampdiff(minute, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(minute, C.createdAt,current_timestamp),'분'),\n" +
                "                           IF(timestampdiff(second, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(SECOND, C.createdAt,current_timestamp),'초'),'N')))))) AS pastTime\n" +
                "FROM Comment C\n" +
                "INNER JOIN User U on U.userIdx = C.userIdx AND C.status = 'Y'\n" +
                "LEFT OUTER JOIN (SELECT CL.userIdx, CL.commentIdx, count(CL.commentIdx) cnt, CL.likeFlag FROM CommentLike CL GROUP BY CL.commentIdx) CL2 ON CL2.commentIdx = C.commentIdx AND CL2.likeFlag = 'Y'\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(CL.commentIdx,0) commentIdx, CL.likeFlag FROM CommentLike CL WHERE CL.userIdx = ? and CL.likeFlag = 'Y') CL3 ON CL3.commentIdx = C.commentIdx\n" +
                "WHERE C.houseIdx = ?\n" +
                "ORDER BY C.createdAt DESC\n" +
                "LIMIT 5";

        return this.jdbcTemplate.query(getCommentsQuery,
                (rs, rowNum) -> new GetComments(
                        rs.getString("userName"),
                        rs.getString("cText"),
                        rs.getInt("likeCnt"),
                        rs.getString("likedByUser"),
                        rs.getString("pastTime")),
                getCommentsParams
        );
    }

    /**
     Knowhow 최신 댓글 조회 API
     */
    public List<GetComments> getKnowhowComments(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getCommentsParams = new Object[]{userIdx, contentIdx};

        String getCommentsQuery = "SELECT U.userName,C.cText, IFNULL(CL2.cnt,0) likeCnt, IFNULL(CL3.likeFlag,'N') AS likedByUser,\n" +
                "       IF(timestampdiff(year, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(year, C.createdAt,current_timestamp),'년'),\n" +
                "           IF(timestampdiff(month, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(month, C.createdAt,current_timestamp),'달'),\n" +
                "               IF(timestampdiff(day, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(day, C.createdAt,current_timestamp),'일'),\n" +
                "                   IF(timestampdiff(hour, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(hour, C.createdAt,current_timestamp),'시간'),\n" +
                "                       IF(timestampdiff(minute, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(minute, C.createdAt,current_timestamp),'분'),\n" +
                "                           IF(timestampdiff(second, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(SECOND, C.createdAt,current_timestamp),'초'),'N')))))) AS pastTime\n" +
                "FROM Comment C\n" +
                "INNER JOIN User U on U.userIdx = C.userIdx AND C.status = 'Y'\n" +
                "LEFT OUTER JOIN (SELECT CL.userIdx, CL.commentIdx, count(CL.commentIdx) cnt, CL.likeFlag FROM CommentLike CL GROUP BY CL.commentIdx) CL2 ON CL2.commentIdx = C.commentIdx AND CL2.likeFlag = 'Y'\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(CL.commentIdx,0) commentIdx, CL.likeFlag FROM CommentLike CL WHERE CL.userIdx = ? and CL.likeFlag = 'Y') CL3 ON CL3.commentIdx = C.commentIdx\n" +
                "WHERE C.knowhowIdx = ?\n" +
                "ORDER BY C.createdAt DESC\n" +
                "LIMIT 5";

        return this.jdbcTemplate.query(getCommentsQuery,
                (rs, rowNum) -> new GetComments(
                        rs.getString("userName"),
                        rs.getString("cText"),
                        rs.getInt("likeCnt"),
                        rs.getString("likedByUser"),
                        rs.getString("pastTime")),
                getCommentsParams
        );
    }

    /**
     Like API
     */
    public int likeContents(String filter, int logonIdx, int contentIdx){
        String checkLikeHistoryQuery = "";
        String newLikeContentsQuery = "";
        String updateLikeContentsQuery = "";
        String flag = "";
        Object[] likeContentsParams = new Object[]{logonIdx, contentIdx};

        if(filter.equals("houses")){
            flag = "H";
            checkLikeHistoryQuery = "SELECT EXISTS(SELECT UL.status\n" +
                    "FROM UserLike AS UL\n" +
                    "WHERE UL.userIdx = ? AND UL.houseIdx = ?) likeHistory";
            newLikeContentsQuery = "INSERT INTO UserLike (userIdx, houseIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateLikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND houseIdx = ?";
        }
        else if(filter.equals("knowhows")){
            flag = "K";
            checkLikeHistoryQuery = "SELECT EXISTS(SELECT UL.status\n" +
                    "FROM UserLike AS UL\n" +
                    "WHERE UL.userIdx = ? AND UL.knowhowIdx = ?) likeHistory";
            newLikeContentsQuery = "INSERT INTO UserLike (userIdx, knowhowIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateLikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND knowhowIdx = ?";
        }
        else{
            flag = "P";
            checkLikeHistoryQuery = "SELECT EXISTS(SELECT UL.status\n" +
                    "FROM UserLike AS UL\n" +
                    "WHERE UL.userIdx = ? AND UL.pictureIdx = ?) likeHistory";
            newLikeContentsQuery = "INSERT INTO UserLike (userIdx, pictureIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateLikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND pictureIdx = ?";
        }

        Object[] insertLikeParams = new Object[]{logonIdx, contentIdx, flag};

        int likeHistory = this.jdbcTemplate.queryForObject(checkLikeHistoryQuery,int.class,likeContentsParams);

        if(likeHistory == 0)
            return this.jdbcTemplate.update(newLikeContentsQuery, insertLikeParams);
        else
            return this.jdbcTemplate.update(updateLikeContentsQuery, likeContentsParams);
    }

    /**
     Like 취소 API
     */
    public int unlikeContents(String filter, int logonIdx, int contentIdx){
        String unlikeContentsQuery = "";
        if(filter.equals("houses"))
            unlikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND houseIdx = ?";
        else if(filter.equals("knowhows"))
            unlikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND knowhowIdx = ?";
        else
            unlikeContentsQuery = "UPDATE UserLike\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND pictureIdx = ?";

        Object[] unlikeContentsParams = new Object[]{logonIdx, contentIdx};
        return this.jdbcTemplate.update(unlikeContentsQuery, unlikeContentsParams);
    }

    /**
    Scrap API
     */
    public int scrapContents(String filter, int logonIdx, int contentIdx){
        String checkScrapHistoryQuery = "";
        String newScrapContentsQuery = "";
        String updateScrapContentsQuery = "";
        String flag = "";
        Object[] scrapContentsParams = new Object[]{logonIdx, contentIdx};

        if(filter.equals("houses")){
            flag = "H";
            checkScrapHistoryQuery= "SELECT EXISTS(SELECT US.status\n" +
                    "FROM UserScrap AS US\n" +
                    "WHERE US.userIdx = ? AND US.houseIdx = ?) scrapHistory";
            newScrapContentsQuery  = "INSERT INTO UserScrap (userIdx, houseIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateScrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND houseIdx = ?";
        }
        else if(filter.equals("knowhows")){
            flag = "K";
            checkScrapHistoryQuery = "SELECT EXISTS(SELECT US.status\n" +
                    "FROM UserScrap AS US\n" +
                    "WHERE US.userIdx = ? AND US.knowhowIdx = ?) scrapHistory";
            newScrapContentsQuery  = "INSERT INTO UserScrap (userIdx, knowhowIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateScrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND knowhowIdx = ?";
        }
        else if(filter.equals("picture")){
            flag = "P";
            checkScrapHistoryQuery = "SELECT EXISTS(SELECT US.status\n" +
                    "FROM UserScrap AS US\n" +
                    "WHERE US.userIdx = ? AND US.pictureIdx = ?) scrapHistory";
            newScrapContentsQuery  = "INSERT INTO UserScrap (userIdx, pictureIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateScrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND pictureIdx = ?";
        }
        else{
            flag = "C";
            checkScrapHistoryQuery = "SELECT EXISTS(SELECT US.status\n" +
                    "FROM UserScrap AS US\n" +
                    "WHERE US.userIdx = ? AND US.productIdx = ?) scrapHistory";
            newScrapContentsQuery  = "INSERT INTO UserScrap (userIdx, productIdx, flag)\n" +
                    "VALUES (?,?,?)";
            updateScrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'Y', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND productIdx = ?";
        }

        Object[] insertScrapParams = new Object[]{logonIdx, contentIdx, flag};

        int scrapHistory = this.jdbcTemplate.queryForObject(checkScrapHistoryQuery,int.class,scrapContentsParams);

        if(scrapHistory == 0)
            return this.jdbcTemplate.update(newScrapContentsQuery, insertScrapParams);
        else
            return this.jdbcTemplate.update(updateScrapContentsQuery, scrapContentsParams);
    }

    /**
     Scrap 취소 API
     */
    public int unscrapContents(String filter, int logonIdx, int contentIdx){
        String unscrapContentsQuery = "";
        if(filter.equals("houses"))
            unscrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND houseIdx = ?";
        else if(filter.equals("knowhows"))
            unscrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND knowhowIdx = ?";
        else if(filter.equals("pictures"))
            unscrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND pictureIdx = ?";
        else
            unscrapContentsQuery = "UPDATE UserScrap\n" +
                    "SET status = 'N', updatedAt = current_timestamp\n" +
                    "WHERE userIdx = ? AND productIdx = ?";

        Object[] unscrapContentsParams = new Object[]{logonIdx, contentIdx};
        return this.jdbcTemplate.update(unscrapContentsQuery, unscrapContentsParams);
    }

    /**
     댓글 작성 API
     */
    public int writeComment(String filter, int logonIdx, int contentIdx, PostComment postComment){
        String writeCommentQuery = "";
        String flag = "";
        String commentParam = postComment.getComment();

        if(filter.equals("houses")){
            flag = "H";
            writeCommentQuery  = "INSERT INTO Comment(userIdx, houseIdx, cText, flag)\n" +
                    "VALUES (?,?,?,?)";
        }
        else if(filter.equals("knowhows")){
            flag = "K";
            writeCommentQuery  = "INSERT INTO Comment(userIdx, knowhowIdx, cText, flag)\n" +
                    "VALUES (?,?,?,?)";
        }
        else{
            flag = "P";
            writeCommentQuery  = "INSERT INTO Comment(userIdx, pictureIdx, cText, flag)\n" +
                    "VALUES (?,?,?,?)";
        }

        Object[] writeCommentParams = new Object[]{logonIdx, contentIdx, commentParam, flag};
        this.jdbcTemplate.update(writeCommentQuery, writeCommentParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    /**
     댓글 삭제 API
     */
    public int removeComment(int commentIdx){
        String removeCommentQuery = "UPDATE Comment\n" +
                "SET status = 'N', updatedAt = current_timestamp\n" +
                "WHERE commentIdx = ?";

        return this.jdbcTemplate.update(removeCommentQuery, commentIdx);
    }

    /**
     답글 작성 API
     */
    public int writeRecomment(int logonIdx, int commentIdx, PostComment postRecomment){
        String writeRecommentQuery = "INSERT INTO Recomment (rcText,userIdx,commentIdx)\n" +
                "VALUES (?,?,?)";
        String recommentParam = postRecomment.getComment();
        Object[] writeRecommentParams = new Object[]{recommentParam,logonIdx, commentIdx};
        this.jdbcTemplate.update(writeRecommentQuery, writeRecommentParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    /**
     답글 삭제 API
     */
    public int removeRecomment(int recommentIdx){
        String removeCommentQuery = "UPDATE Recomment\n" +
                "SET status = 'N', updatedAt = current_timestamp\n" +
                "WHERE recommentIdx = ?";

        return this.jdbcTemplate.update(removeCommentQuery, recommentIdx);
    }

}