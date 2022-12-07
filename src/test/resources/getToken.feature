# language: ru
@withdrawal
Функция: Проверка получения токена
  @success
  Сценарий: Авторизованный пользователь может получить токен
    Дано Логин admin и пароль password123 пользователя
    Когда Пользователь получает токен
    Тогда Токен сохраняется

  @unsuccessful
  Сценарий: Неавторизованный пользователь не может получить токен
    Дано Логин user и пароль password123 пользователя
    Когда Пользователь получает токен ошибку при запросе токена
    Тогда Текст ошибки Bad credentials