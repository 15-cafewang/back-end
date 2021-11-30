package com.sparta.backend.domain.cafe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.validator.CafeValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user"})
@Getter
@NoArgsConstructor
@Entity
public class Cafe extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1500)
    private String content;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 600)
    private String thumbNailImage;

    @OneToMany(mappedBy = "cafe",cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Tag> tagList;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeComment> cafeCommentList;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeLike> cafeLikeList;

    @OneToMany(mappedBy = "cafe",  cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<CafeImage> cafeImagesList;

    @OneToMany(mappedBy = "cafe",  cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<CafeDetailCount> cafeDetailCountList;

    public Cafe(String title, String content, String location, User user){
        //Edge케이스들 validation
        CafeValidator.validateCafeInput(title,content, location,user);
        this.title = title;
        this.content = content;
        this.location = location;
        this.user = user;
    }
    public Cafe(String title, String content, String location, User user,String thumbNailImage){
        CafeValidator.validateCafeInput(title,content, location,user);
        this.title = title;
        this.content = content;
        this.location = location;
        this.user = user;
        this.thumbNailImage = thumbNailImage;
    }
    public Cafe updateCafe(String title, String content, String location,String thumbNailImage) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.thumbNailImage = thumbNailImage;

        return this;
    }

    //test용(강제 id주입 위해)
    public Cafe(Long id, String title, String content, String location, User user){
        this.id = id;
        this.title = title;
        this.content = content;
        this.location = location;
        this.user = user;
    }

    public Cafe updateCafeNullThumbNail(String savedThumbNailUrl) {
        this.thumbNailImage = savedThumbNailUrl;
        return this;
    }
}
