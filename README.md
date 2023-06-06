# AccountantExpenses
Экзаменационный проект по курсу Scala

# Что нужно для запуска?
1) Необходимо создать базу данных, в ней таблицу:
CREATE TABLE CASH_TRANSACTIONS (
  id SERIAL PRIMARY KEY,
  amount NUMERIC NOT NULL,
  name VARCHAR(255) NOT NULL,
  date DATE NOT NULL,
  source VARCHAR(255) NOT NULL
)

2) В файле application.conf указать свои параметры для сервера и базы данных
