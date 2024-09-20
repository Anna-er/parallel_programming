# Инструменты анализа кода

## Анализируемый проект
[Выбранный репозиторий](https://github.com/renanGit/Crazy-Professor-Synchronization/tree/master) рассматривает задачу о сумасшедшем профессоре и учениках, которые хотят задать вопросы.
Для этого студенты должны синхронизироваться друг с другом и с профессором. Подробнее с задачей можно ознакомиться
[здесь](https://github.com/renanGit/Crazy-Professor-Synchronization/blob/master/README.md).

Проект написан на `C` без элементов `OpenMP`.

## Анализ
### Thread sanitizer
Компиляция файла происходила с помощью флага `-fsanitize=thread`.

Запуск `./carzy 4` выводит следующие предупреждения:
* `ThreadSanitizer: data race`
* `ThreadSanitizer: lock-order-inversion`

Всего 14 `WARNING`

### Helgrind
Программа запускалась таким образом:
```
valgrind --tool=helgrind ./crazy 4
```

Все выведенные ошибки были связаны с гонкой данных.

### Причины
После более детального анализа кода стало понятно, что ситуации возникновения ошибок невозможны из-за особенности задачи:
* Профессор был один => можно было лишь единожды (сразу после старта) запустить функцию `Professor`, которая выполняется в одном потоке. Поэтому доступ к мьютексам внутри этой функции всегда был эксклюзивен
    * Мьютекс  использовался в качестве блокировки в контексте conditional variable(`pthread_cond_wait()`)
    * Несмотря на то, что локи являются глобальными, используются они только внутри функции `Professor`

### Гонка данных
Добавлена искусственная гонка данных, чтобы проверить работу инструментов.

Был убран мьютекс, который отвечал за корректное взаимодействие с глобальными переменными (в часности `numStud`).

После запуска преобразованного кода, используя `Thread sanitizer` , программа вывела следующее предупреждение, указывающее на измененную часть кода:
```
WARNING: ThreadSanitizer: data race (pid=10880)
  Write of size 4 at 0x56041da7912c by thread T2:
    #0 startStudent <null> (crazy+0x1942)

  Previous read of size 4 at 0x56041da7912c by thread T1:
    #0 startProfessor <null> (crazy+0x1711)

  Location is global 'numStud' of size 4 at 0x56041da7912c (crazy+0x00000000412c)

  <...>

  SUMMARY: ThreadSanitizer: data race (/home/anna/Crazy-Professor-Synchronization/CPS/crazy+0x1942) in startStudent
```

`Helgrin` тоже с задачей справился и вывел соответсующие предупреждения:

```
<...>

==10404== Possible data race during read of size 4 at 0x10C0EC by thread #5
==10404== Locks held: 1, at address 0x10C240
==10404==    at 0x1097A0: startStudent (in /home/anna/Crazy-Professor-Synchronization/CPS/crazy)
==10404==    by 0x483F876: mythread_wrapper (hg_intercepts.c:387)
==10404==    by 0x4866EA6: start_thread (pthread_create.c:477)
==10404==    by 0x497CACE: clone (clone.S:95)
==10404== 
==10404== This conflicts with a previous write of size 4 by thread #4
==10404== Locks held: 1, at address 0x10C180
==10404==    at 0x109949: QuestionDone (in /home/anna/Crazy-Professor-Synchronization/CPS/crazy)
==10404==    by 0x10985E: startStudent (in /home/anna/Crazy-Professor-Synchronization/CPS/crazy)
==10404==    by 0x483F876: mythread_wrapper (hg_intercepts.c:387)
==10404==    by 0x4866EA6: start_thread (pthread_create.c:477)
==10404==    by 0x497CACE: clone (clone.S:95)
==10404==  Address 0x10c0ec is 0 bytes inside data symbol "numStud"

<...>
```

## Итоги
`Thread sanitizer`, `Helgrin` подходят для анализа проектов, содержащих элементы параллельного программирования. Однако существуют ложные срабатывания. Верить каждой выыводимой ошибке не стоит. 