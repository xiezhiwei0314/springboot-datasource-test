package com.linzhi.datasource.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class LomTest {

    public static void main(String[] args) {
        test1();
    }

    public static  void test1(){
        System.out.println(Optional.ofNullable("正品").orElse("替代品"));
        System.out.println(Optional.ofNullable(null).orElse("替代品"));

        System.out.println(Optional.ofNullable("宝马").orElseGet(()->"走路"));
        System.out.println(Optional.ofNullable(null).orElseGet(()->"自行车"));
        System.out.println(Optional.ofNullable(null).orElseGet(()->"电动车"));

    }


    public static void test2(){

      /*  DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // data list
        List<User> userList = Arrays.asList(
                User.builder().id(123456).name("Zhang, San").city("ShangHai").sex("man").birthDay(LocalDateTime.parse("2022-07-01 12:00:00", df)).build(),
                User.builder().id(777777).name("Zhang, San").city("ShangHai").sex("woman").birthDay(LocalDateTime.parse("2022-07-01 12:00:00", df)).build(),
                User.builder().id(888888).name("Li, Si").city("ShangHai").sex("man").birthDay(LocalDateTime.parse("2022-07-01 12:00:00", df)).build(),
                User.builder().id(999999).name("Zhan, San").city("HangZhou").sex("woman").birthDay(LocalDateTime.parse("2022-07-01 12:00:00", df)).build(),
                User.builder().id(555555).name("Li, Si").city("NaJin").sex("man").birthDay(LocalDateTime.parse("2022-07-01 12:00:00", df)).build()
        );
        Map<String, List<User>> groupMap = userList.stream()
                .collect(Collectors.groupingBy(User::getCity));
        groupMap.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });*/
    }
}
