## 스프링5 리액티브 프로그래밍 스터디

1. 테스트 코드 작성 

- FluxMonoTests.java <p></p>
    - flux와 mono의 flow 파악 <p></p>
    ```java
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
    ```