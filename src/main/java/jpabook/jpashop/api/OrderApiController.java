package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //V1. 엔티티 직접 노출
    // 엔티티가 변하면 API 스펙이 변한다.
    // Hibernate5Module 모듈 등록, LAZY=null 처리
    // 트랜잭션 안에서 지연 로딩 필요
    // 양방향 연관관계 문제 -> @JsonIgnore
    // order -> member , order -> delivery
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        //iter + tab = for문 자동생성
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) orderItem.getItem().getName();
        }
        return all;
    }

    //V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
    //- 트랜잭션 안에서 지연 로딩 필요
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        //엔티티 전체 조회
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        //루프로 돌려 OrderDto로 변환하면서 List로 만듬
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o)) //Dto로 만들면서 OrderDto생성자로 넘김
                .collect(toList());

        return result;
    }

    //    V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
//    페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
//    패치 조인으로 SQl이 1번만 실행됨
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }


    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
       V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
       페이징 가능
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //OrderItem을 list로 받아오면 OrderItem 엔티티 변경시 API스펙이 변경되므로
        //다음과같이 OrderItemDto 클래스를 생성하여 itemlist를 가져옴
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            //OrderItem 엔티티를 Dto로 변경하면서 생성자를 통해 Dto 생성
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }


    //dto를 사용하여 OrderItem 내에 상품명, 주문 가격, 주문 수량만 가져온다.
    @Data
    static class OrderItemDto {
        private String itemName;//상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
