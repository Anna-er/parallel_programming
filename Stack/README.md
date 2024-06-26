# Treiber Stack

**Реализовано 2 параллельных алгоритма:**
- Стек Трайбера
- Стек Трайбера с элиминацией из книги 'The Art of Multiprocessor Programming'

Все вычисления выполнены на `Intel Core i7-12650H (2.30 ГГц)` с 10 ядрами

Время работы алгоритма = среднее время выполнения алгоритма на 10 запусках.

Каждый поток выполяняет 10^6 операций.

### Сравнение времени работы Treiber Stack и Treiber Stack With Elimination 

#### Специально подобранные операции (push + pop)
| Потоки | Стек Трайбера  | Стек Трайбера с элиминацией  | Ускорение | 
|--------|----------------|------------------------------|-----------|
| 1      | 351 ms         | 340 ms                       | 11 ms     |
| 2      | 427 ms         | 425 ms                       | 2 ms      |
| 4      | 1240 ms        | 701 ms                       | 539 ms    |
| 8      | 2456 ms        | 1627 ms                      | 829 ms    |
| 12     | 3586 ms        | 3528 ms                      | 58 ms     |

#### Рандомные операции на стеке

| Потоки | Стек Трайбера | Стек Трайбера с элиминацией  | Ускорение   |
|--------|---------------|------------------------------|-------------|
| 1      | 46 ms         | 47 ms                        | 1 ms        |
| 2      | 195 ms        | 102 ms                       | 93 ms       |
| 4      | 629 ms        | 377 ms                       | 252 ms      |
| 8      | 1476 ms       | 1300 ms                      | 176 ms      |
| 12     | 2347 ms       | 3255 ms                      | -           |

