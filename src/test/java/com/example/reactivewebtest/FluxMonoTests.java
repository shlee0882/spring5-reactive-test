package com.example.reactivewebtest;

import com.example.reactivewebtest.model.Food;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FluxMonoTests {

    @Test
    public void fluxStr() {
        Flux<String> strFlux = Flux.just("조현영", "초아", "써니")
                .log();

        strFlux.filter(a -> !a.equals("초아"))
                .doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    public void fluxCompleteFlow() {
        Flux<String> strFlux = Flux.just("조현영", "초아", "써니")
                .concatWith(Flux.just("박봄"))
                .log();

        // subscribe에서는 요소를 한개씩 읽어들인다.
        // 모두 읽었다면 onComplete() 호출된다.
        strFlux.subscribe(System.out::println
                , (e) -> System.err.println("익셉션은 : " + e)
                , () -> System.out.println("완료했음"));
    }

    @Test
    public void fluxErrorFlow() {
        Flux<String> strFlux = Flux.just("조현영", "초아", "써니")
                .concatWith(Flux.error(new RuntimeException("런타임 익셉션 발생!!")))
                .concatWith(Flux.just("박봄"))
                .log();

        // subscribe에서는 요소를 한개씩 읽어들인다.
        // 읽어들이는 중간에 에러가 있다면 subscribe가 종료된다.
        strFlux.subscribe(System.out::println
                , (e) -> System.err.println("익셉션은 : " + e)
                , () -> System.out.println("완료했음"));
    }

    @Test
    public void monoStr() {
        // Mono have 1 String argument
        String str = "치킨";
        Mono<String> strMono = Mono.just(str).log();
        strMono.subscribe(System.out::println);
    }

    @Test
    public void monoList() {
        // Mono have 1 List argument
        List<String> strList = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> strList.add("문자" + i));

        Mono<List<String>> strMono = Mono.just(strList)
                .log();

        strMono.flatMapMany(Flux::fromIterable)
                .subscribe(System.out::println);
    }

    @Test
    public void fluxMultiList() {
        // Flux is N arguments
        List<Integer> integerList1 = new ArrayList<>();
        List<Integer> integerList2 = new ArrayList<>();

        IntStream.range(0, 3).forEach(i -> integerList1.add(i));
        IntStream.range(4, 7).forEach(i -> integerList2.add(i));

        // integerList1 [0,1,2] , integerList2 [4,5,6]
        Flux<List<Integer>> integerFlux = Flux.just(integerList1, integerList2).log();

        integerFlux.flatMap(Flux::fromIterable)
                .filter(a -> !a.equals(0) && !a.equals(4))
                .doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    public void fluxUseModel() {
        Food food1 = new Food("햄버거", 4900);
        Food food2 = new Food("피자", 7900);

        List<Food> foodList = new ArrayList<>();
        foodList.add(food1);
        foodList.add(food2);

        Flux<List<Food>> fluxList = Flux.just(foodList).log();
        fluxList.flatMap(Flux::fromIterable)
                .map(a->{
                    if("햄버거".equals(a.getName())) a.setPrice(2500);
                    if("피자".equals(a.getName())) a.setPrice(9900);
                    return a;
                })
                .subscribe(System.out::println);
        List<List<Food>> convertList = fluxList.collectList().block();
        List<Food> returnList = new ArrayList<>();
        convertList.stream().forEach(a->a.stream().forEach(b->returnList.add(b)));
        returnList.stream().forEach(a->System.out.println("returnList : "+ a));
        Assert.assertEquals(foodList, returnList);
    }
}
