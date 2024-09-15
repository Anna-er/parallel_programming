# Treiber Stack

В результате выполнения задания был реализовано 2 concurrent алгоритма: Treiber Stack и Elimination Treiber Stack.

Ниже приведен эксперимент, сравнивающий скорость работы алгоритмов.

### Характеристики вычислительной машины

* Процессор — 12th Gen Intel(R) Core(TM) i7-12650H
* Оперативная память — 16.0 GB
* Операционная система — Windows 10 Pro

Время работы алгоритма = среднее время выполнения алгоритма на 10 запусках.

Каждый поток выполяняет 1000000 операций.

### Сравнение времени работы Treiber Stack и Elimination Treiber Stack на рандомных операциях на стеке

```
Execution time of Lock-Free Treiber Stack for 1 threads = 44 ms
Execution time of Lock-Free Treiber Stack for 2 threads = 154 ms
Execution time of Lock-Free Treiber Stack for 4 threads = 511 ms
Execution time of Lock-Free Treiber Stack for 8 threads = 1321 ms
Execution time of Lock-Free Treiber Stack for 12 threads = 2114 ms
```
```
Execution time of Elimination Treiber Stack for 1 threads = 47 ms
Execution time of Elimination Treiber Stack for 2 threads = 85 ms
Execution time of Elimination Treiber Stack for 4 threads = 230 ms
Execution time of Elimination Treiber Stack for 8 threads = 568 ms
Execution time of Elimination Treiber Stack for 12 threads = 1429 ms
```