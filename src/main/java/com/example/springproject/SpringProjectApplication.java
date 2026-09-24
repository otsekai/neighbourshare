package com.example.springproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringProjectApplication {
    // :NOTE: Теперь когда всё +- работает переходим к другому этапу, нужно сделать так чтобы сервис можно было запустить буквально одной командой,
    // :NOTE: пусть это будет например, что-нибудь связанное с докером (готово), далее нужно заслать это на гитхаб (позже), потом добавить новые функции в сервис
    // :NOTE: (удалить вещь(нельзя во время брони), забрать вещь с бронирования досрочно(owner)), также надо пофиксить пару моментов:
    // :NOTE: всё ещё проблемы с панелью админа + она всё ещё отображается на сайте, при учёте что пользователь не админ, также добавить кое-что новое:

    // :NOTE: список броней, которые были у пользователя(owner), но не входящие, а именно историю бронирования, внешний вид сайта(frontend),
    // :NOTE: цвет статуса "completed", что такое действие(хз), поменять чуток формат периода аренды(на определённые сроки: 1h, 3h, 6h, 1d, 3d, 1w, 1m)

    // :NOTE: Затем перехожу на расширение стека технологий (не считая докер)
    public static void main(String[] args) {
        SpringApplication.run(SpringProjectApplication.class, args);
    }

}
