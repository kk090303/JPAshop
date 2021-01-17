package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//상품 서비스는 상품 리포지토리에 단순하게 위임만 하는 클래스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    //변강 감지 기능 사용을 통한 준영속 엔티티 수정
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity ) {
        Item findItem = itemRepository.findOne(itemId);
        //findItem.change(name,price,stockQuantity);
        //다음과 같이 메서드를 만들어서 메서드안에서 값을 변경하는 것이 좋다.
        //역추적하여 어디서 값이 변경되는지 찾기 편하다.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }
}
