# language: ru
@withdrawal
Функция: Создание и проверка бронирования
  @success
  Сценарий: Авторизованный пользователь может создать бронирование
    Дано Даты въезда 2022-09-01 и выезда 2022-09-11
    Когда Отправляем запрос на создание бронирования
    Тогда Проверка бронирование существует

  @unsuccessful
  Сценарий: Авторизованный пользователь не может создать бронирование при неверных данных
    Дано Даты въезда 2022-09-01 и выезда 2022-09-11
    Когда Отправляем запрос на создание бронирования с некорректным телом
    Тогда Текст ошибки HTTP/1.1 500 Internal Server Error

  @success
  Сценарий: Авторизованный пользователь может создать и обновить бронирование
    Дано Даты въезда 2022-09-01 и выезда 2022-09-11
    И Новая дата выезда 2022-09-11
    И Логин admin и пароль password123 пользователя
    Когда Пользователь получает токен
    И Отправляем запрос на создание бронирования
    И Отправляет запрос на обновление бронирования
    Тогда Проверка бронирование существует

  @success
  Сценарий: Авторизованный пользователь может создать и удалить бронирование
    Дано Даты въезда 2022-09-01 и выезда 2022-09-11
    И Логин admin и пароль password123 пользователя
    Когда Пользователь получает токен
    И Отправляем запрос на создание бронирования
    И Проверка бронирование существует
    И Удаляет бронирование
    Тогда Проверяет бронирование не существует

  @unsuccessful
  Сценарий: Пользователь проверяет бронирование с несуществующим ID
    Дано Несуществующий ID бронирования 5000000
    Когда Проверка бронирование не существует
    Тогда Текст ошибки Not Found

  @unsuccessful
  Сценарий: Пользователь удаляет бронирование с несуществующим ID
    Дано Логин admin и пароль password123 пользователя
    И Несуществующий ID бронирования 50000000
    Когда Пользователь получает токен
    И Удаляет бронирование с несуществующим ID
    Тогда Текст ошибки Method Not Allowed