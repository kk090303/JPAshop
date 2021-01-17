package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
    private Long id;

    //item 공통 속성
    private String name;
    private int price;
    private int stockQuantity;

    //book 고유 속성
    private String author;
    private String isbn;

}

