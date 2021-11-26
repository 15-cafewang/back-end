package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.queryInterface.PopularCafeInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Page<Cafe> findAll(Pageable pageable);

    @Query("select r from Cafe r join r.tagList t where t.name = :tagName")
    Page<Cafe> findAllByTag(String tagName, Pageable pageable);

    @Query("select r from Cafe r where r.title like concat('%',:keyword,'%') or r.content like concat('%',:keyword,'%')")
    Page<Cafe> findAllByTitleOrContent(String keyword, Pageable pageable);

    //카페 목록조회 좋아요 순
    @Query("select r from Cafe r left join r.cafeLikeList l group by r.id order by count(l.cafe) desc")
    Page<Cafe> findCafesOrderByLikeCountDesc(Pageable pageable);

    //  특정기간&인기카페 - 원하는 칼럼만 가져오는 jpql
    @Query("select r.id as cafeId, r.title as title, r.content as content , r.location as location " +
            "from Cafe r join r.cafeLikeList l " +
            "where l.regDate between :startDate and :endDate " +
            "group by r.id order by count(l.cafe) desc ")
    List<PopularCafeInterface> findPopularCafe(LocalDateTime startDate, LocalDateTime endDate);

    //특정기간&인기카페 - id만 가져오기..전체..jpql
    @Query("select r.id " +
            "from Cafe r join r.cafeLikeList l " +
            "where l.regDate between :startDate and :endDate " +
            "group by r.id order by count(l.cafe) ")
    List<Long> findPopularCafeId(LocalDateTime startDate, LocalDateTime endDate);

    //특정기간&인기카페 - id만 가져오기..top3..native sql
    @Query(value = "SELECT r.cafe_id " +
            "FROM cafe r JOIN cafe_like l ON r.cafe_id = l.cafe_id " +
            "WHERE l.reg_date BETWEEN :startDate AND :endDate " +
            "GROUP BY r.cafe_id order by count(l.cafe_id) desc limit 3",
            nativeQuery = true)
    List<Long> findPopularCafeId2(LocalDateTime startDate, LocalDateTime endDate);

    //한번에 좋아요 순으로 Cafe객체 가져오려는 시도 실패
//    @Query(value = "select re from Cafe re where re.id in (" +
//            "select r.id as cafeId " +
//            "from Cafe r join r.cafeLikesList l " +
//            "where l.regDate between :startDate and :endDate " +
//            "group by r.id order by count(l.cafe) desc) " +
//            "order by field(re.id, " +
//            "select r.id as cafeId " +
//            "from Cafe r join r.cafeLikesList l " +
//            "where l.regDate between :startDate and :endDate " +
//            "group by r.id order by count(l.cafe) desc) ")
//    List<Cafe> findPopularCafe2(LocalDateTime startDate, LocalDateTime endDate);
//
//    //위와 같은 시도 sql로 하려는 시도 실패
//    @Query(value = "SELECT * from cafe r2 " +
//            "where r2.cafe_id " +
//            "in " +
//            "(SELECT r.cafe_id " +
//            "FROM cafe r JOIN cafe_likes l ON r.cafe_id = l.cafe_id " +
//            "WHERE l.regDate BETWEEN '2021-11-01' AND '2021-11-08' " +
//            "GROUP BY r.cafe_id order by count(l.cafe_id) desc) " +
//            "ORDER BY FIELD(r2.cafe_id,(SELECT r.cafe_id " +
//            "                             FROM cafe r JOIN cafe_likes l ON r.cafe_id = l.cafe_id " +
//            "                             WHERE l.regDate BETWEEN :startDate AND :endDate " +
//            "                             GROUP BY r.cafe_id order by count(l.cafe_id) desc))"
//    ,nativeQuery = true)
//    List<Cafe> findPopularCafe3(LocalDateTime startDate, LocalDateTime endDdate);

    Page<Cafe> findAllByUser(Pageable pageable, User user);

    @Query("select r from Cafe r where r.id in (select rl.cafe.id from CafeLike rl where rl.user.id = :userId)")
    Page<Cafe> findAllByCafeLikesList(@Param("userId") Long userId, Pageable pageable);

    //최근카페(메인페이지) top4가져오기
    List<Cafe> findTop3ByOrderByRegDateDesc();

    @Query("select r from Cafe r left join r.cafeLikeList rl left join r.tagList tl " +
            "where tl.name = :keyword group by r.id order by count(rl.user) desc")
    Page<Cafe> findAllByTagOrderByLikeCount(String keyword, Pageable pageable);

    @Query("select r from Cafe r left join r.cafeLikeList rl " +
            "where r.title like %:keyword% or r.content like %:keyword% " +
            "group by r.id order by count(rl.user) desc ")
    Page<Cafe> findAllByTitleOrContentOrderByLikeCount(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select exists (select * from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
            "               where rl.user_id = :userId " +
            "                 and rl.reg_date between :start and :end " +
            "               ) as exist_like, " +
            "       exists (select sc.keyword, count(sc.keyword) cnt from cafe_search_count sc " +
            "               where sc.user_id = :userId" +
            "                 and sc.reg_date between :start and :end " +
            "               group by sc.keyword) as exist_search, " +
            "       exists (select t.name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
            "               where dc.user_id = :userId " +
            "               and dc.reg_date between :start and :end " +
            "               group by t.name) as exsit_detail;", nativeQuery = true)
    List<Object[]> checkUserHasData(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select cafe_id from( " +
            "                 select rdc.cafe_id cafe_id, count(rdc.cafe_id) cnt from cafe_detail_count rdc join tag t on rdc.cafe_id = t.cafe_id " +
            "                 where t.name = ( " +
            "                     select name from " +
            "                         (select t.name name, count(t.name)*2 cnt from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
            "                          where rl.user_id = :userId " +
            "                            and rl.reg_date between :start and :end " +
            "                          group by t.name " +
            "                          union all " +
            "                          select sc.keyword name, count(sc.keyword) cnt from cafe_search_count sc " +
            "                          where user_id = :userId " +
            "                            and sc.reg_date between :start and :end " +
            "                          group by sc.keyword " +
            "                          union all " +
            "                          select t.name name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
            "                          where dc.user_id = :userId and dc.reg_date between :start and :end " +
            "                          group by t.name) list " +
            "                     group by list.name " +
            "                     order by sum(list.cnt) desc limit 1 " +
            "                     ) " +
            "                 group by  t.name " +
            "                 union all " +
            "                 select  l.cafe_id cafe_id, count(l.cafe_id) cnt from cafe_like l join tag t on l.cafe_id = t.cafe_id " +
            "                 where t.name = ( " +
            "                     select name from " +
            "                         (select t.name name, count(t.name)*2 cnt from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
            "                          where rl.user_id = :userId " +
            "                            and rl.reg_date between :start and :end " +
            "                          group by t.name " +
            "                          union all " +
            "                          select sc.keyword name, count(sc.keyword) cnt from cafe_search_count sc " +
            "                          where user_id = :userId " +
            "                            and sc.reg_date between :start and :end " +
            "                          group by sc.keyword " +
            "                          union all " +
            "                          select t.name name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
            "                          where dc.user_id = :userId and dc.reg_date between :start and :end" +
            "                          group by t.name) list " +
            "                     group by list.name " +
            "                     order by sum(list.cnt) desc limit 1 " +
            "                 ) " +
            "                 group by t.name) list " +
            "order by cnt desc limit 1;", nativeQuery = true)
    List<Object[]> findRecommendedCafeIdBasedOne(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select r.cafe_id, t.name from cafe r " +
            "                           left join tag t on r.cafe_id= t.cafe_id " +
            "                           left join cafe_detail_count rdc on r.cafe_id= rdc.cafe_id " +
            "                           left join cafe_like l on r.cafe_id = l.cafe_id " +

            "where t.name = ( " +
            "    select name from " +
            "        (select t.name, count(t.name)*2 cnt from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
            "         where rl.reg_date between :start and :end " +
            "         group by t.name " +
            "         union all " +
            "         select sc.keyword, count(sc.keyword) cnt from cafe_search_count sc " +
            "         where sc.reg_date between :start and :end " +
            "         group by sc.keyword " +
            "         union all " +
            "         select t.name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
            "         where dc.reg_date between :start and :end " +
            "         group by t.name) list " +
            "    group by list.name " +
            "    order by  SUM(cnt) desc limit 1 " +
            ") " +
            "  and (rdc.reg_date between :start and :end " +
            "  or l.reg_date between :start and :end) " +
            "group by r.cafe_id limit 1", nativeQuery = true)
    List<Object[]> findRecommendedCafeIdBasedAll(LocalDateTime start, LocalDateTime end);

    @Query(value =
            "select name from " +
                    "    (select t.name name, count(t.name)*2 cnt from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
                    "     where rl.user_id = :userId " +
                    "       and rl.reg_date between :start and :end " +
                    "     group by t.name " +
                    "     union all " +
                    "     select sc.keyword name, count(sc.keyword) cnt from cafe_search_count sc " +
                    "     where user_id = :userId " +
                    "       and sc.reg_date between :start and :end " +
                    "     group by sc.keyword " +
                    "     union all " +
                    "     select t.name name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
                    "     where dc.user_id = :userId and dc.reg_date between :start and :end " +
                    "     group by t.name) list " +
                    "group by list.name " +
                    "order by sum(list.cnt) desc limit 1;"
            , nativeQuery = true)
    String findRecommendingTagNameBasedOne(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value =
            "select name from " +
                    "    (select t.name name, count(t.name)*2 cnt from tag t join cafe_like rl on t.cafe_id = rl.cafe_id " +
                    "     where rl.reg_date between :start and :end " +
                    "     group by t.name " +
                    "     union all " +
                    "     select sc.keyword name, count(sc.keyword) cnt from cafe_search_count sc " +
                    "     where sc.reg_date between :start and :end " +
                    "     group by sc.keyword " +
                    "     union all " +
                    "     select t.name name, count(t.name) cnt from tag t join cafe_detail_count dc on t.cafe_id = dc.cafe_id " +
                    "     where dc.reg_date between :start and :end " +
                    "     group by t.name) list " +
                    "group by list.name " +
                    "order by sum(list.cnt) desc limit 1;"
            , nativeQuery = true)
    String findRecommendingTagNameBasedAll(LocalDateTime start, LocalDateTime end);

    @Query(value ="select list.cafe_id from( " +
            "                 select rdc.cafe_id cafe_id, count(rdc.cafe_id) cnt from cafe_detail_count rdc join tag t on rdc.cafe_id = t.cafe_id " +
            "                 where t.name = :foundTagName " +
            "                 group by  t.name " +
            "                 union all " +
            "                 select  l.cafe_id cafe_id, count(l.cafe_id) cnt from cafe_like l join tag t on l.cafe_id = t.cafe_id " +
            "                 where t.name = :foundTagName " +
            "                 group by t.name) list " +
            "                 join tag t on list.cafe_id= t.cafe_id " +
            "order by cnt desc limit 1;", nativeQuery = true)
    Long findRecommendingCafeIdByTagName(String foundTagName);

    @Query(value = "select * from cafe order by rand() limit 1"
    ,nativeQuery = true)
    Cafe findRandomCafe();
}
