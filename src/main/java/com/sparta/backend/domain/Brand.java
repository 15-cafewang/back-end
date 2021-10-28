package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.backend.dto.request.brand.PostBrandRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Brand extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Product> productList;

    public Brand(PostBrandRequestDto requestDto) {
        this.name = requestDto.getName();
    }
}
