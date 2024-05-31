# Инструменты анализа кода

## Анализируемый проект
[Выбранный репозиторий](https://github.com/progschj/ThreadPool/tree/master) рассматривает задачу о сумасшедшем профессоре и учениках, которые хотят задать вопросы.
Для этого студенты должны синхронизироваться друг с другом и с профессором.

Проект написан на `C++` без элементов `OpenMP`.

## Анализ

### Helgrind
Программа запускалась таким образом:
```
valgrind --tool=helgrind ./example
```

Все выведенные ошибки были связаны с гонкой данных.
Helgrind сообщает о гонке данных, которая возникает из-за одновременной записи в один и тот же буфер файла потоками #1 и #2.
Записи происходят в стандартные потоковые объекты (например, `std::cout`), которые не защищены мьютексами, что может приводить к некорректным результатам при многопоточном доступе.

Это реальная проблема гонки данных. Одновременный доступ к `std::ostream` объектам из разных потоков без должной синхронизации может привести к повреждению данных, неожиданным результатам или сбоям программы.
Потенциальная проблема может проявиться в виде некорректного вывода, пропущенных или поврежденных данных в выводе, и это может быть трудно отладить.

Для устранения гонки данных можно использовать мьютексы для защиты доступа к объектам вывода.

### Thread sanitizer
Компиляция файла происходила с помощью флага `-fsanitize=thread`.

Запуск `./example` не вывел явных предупреждений от ThreadSanitizer.


### Гонка данных
Введена искусственная гонка данных, чтобы проверить работу инструментов. 

После запуска преобразованного кода, используя `Thread sanitizer` , программа вывела следующее предупреждение, указывающее на измененную часть кода:
```
WARNING: ThreadSanitizer: data race (pid=120254)
  Read of size 4 at 0x55e1ec925194 by thread T2:
    #0 race_function() <null> (example_tsan+0x3a24)

  Previous write of size 4 at 0x55e1ec925194 by thread T1:
    #0 race_function() <null> (example_tsan+0x3a3c)

  Location is global 'shared_value' of size 4 at 0x55e1ec925194 (example_tsan+0x00000001e194)
  
  <...>

SUMMARY: ThreadSanitizer: data race (/mnt/c/Users/brung/parallels/ThreadPool/example_tsan+0x3a24) in race_function()
```

`Helgrin` тоже с задачей справился и вывел соответсующие предупреждения:

```
<...>

==128499== Possible data race during read of size 4 at 0x5468840 by thread #4
==128499== Locks held: none
==128499==    at 0x52CCF8B: fwrite (in /usr/lib/x86_64-linux-gnu/libc.so.6)
==128499==    by 0x515DB64: std::basic_ostream<char, std::char_traits<char> >& std::__ostream_insert<char, std::char_traits<char> >(std::basic_ostream<char, std::char_traits<char> >&, char const*, long) (in /usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.30)
==128499==    by 0x515DEBA: std::basic_ostream<char, std::char_traits<char> >& std::operator<< <std::char_traits<char> >(std::basic_ostream<char, std::char_traits<char> >&, char const*) (in /usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.30)
==128499==    by 0x10B8ED: main::{lambda()#1}::operator()() const (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E633: int std::__invoke_impl<int, main::{lambda()#1}&>(std::__invoke_other, main::{lambda()#1}&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E2E2: std::__invoke_result<main::{lambda()#1}&>::type std::__invoke<main::{lambda()#1}&>(main::{lambda()#1}&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E10D: int std::_Bind<main::{lambda()#1} ()>::__call<int>(std::tuple<>&&, std::_Index_tuple<>) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10DD76: int std::_Bind<main::{lambda()#1} ()>::operator()<, int>() (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10DAF0: int std::__invoke_impl<int, std::_Bind<main::{lambda()#1} ()>&>(std::__invoke_other, std::_Bind<main::{lambda()#1} ()>&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10D8CF: std::enable_if<std::__and_<std::__not_<std::is_void<int> >, std::is_convertible<std::__invoke_result<std::_Bind<main::{lambda()#1} ()>&>::type, int> >::value, int>::type std::__invoke_r<int, std::_Bind<main::{lambda()#1} ()>&>(std::_Bind<main::{lambda()#1} ()>&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10D62E: std::__future_base::_Task_state<std::_Bind<main::{lambda()#1} ()>, std::allocator<int>, int ()>::_M_run()::{lambda()#1}::operator()() const (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E327: std::__future_base::_Task_setter<std::unique_ptr<std::__future_base::_Result<int>, std::__future_base::_Result_base::_Deleter>, std::__future_base::_Task_state<std::_Bind<main::{lambda()#1} ()>, std::allocator<int>, int ()>::_M_run()::{lambda()#1}, int>::operator()() const (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499== 
==128499== This conflicts with a previous write of size 4 by thread #2
==128499== Locks held: none
==128499==    at 0x52CCF99: fwrite (in /usr/lib/x86_64-linux-gnu/libc.so.6)
==128499==    by 0x515DB64: std::basic_ostream<char, std::char_traits<char> >& std::__ostream_insert<char, std::char_traits<char> >(std::basic_ostream<char, std::char_traits<char> >&, char const*, long) (in /usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.30)
==128499==    by 0x515DEBA: std::basic_ostream<char, std::char_traits<char> >& std::operator<< <std::char_traits<char> >(std::basic_ostream<char, std::char_traits<char> >&, char const*) (in /usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.30)
==128499==    by 0x10B889: main::{lambda()#1}::operator()() const (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E633: int std::__invoke_impl<int, main::{lambda()#1}&>(std::__invoke_other, main::{lambda()#1}&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E2E2: std::__invoke_result<main::{lambda()#1}&>::type std::__invoke<main::{lambda()#1}&>(main::{lambda()#1}&) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10E10D: int std::_Bind<main::{lambda()#1} ()>::__call<int>(std::tuple<>&&, std::_Index_tuple<>) (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==    by 0x10DD76: int std::_Bind<main::{lambda()#1} ()>::operator()<, int>() (in /mnt/c/Users/brung/parallels/ThreadPool/example)
==128499==  Address 0x5468840 is 192 bytes inside data symbol "_IO_2_1_stdout_"

<...>
```

## Итоги
`Thread sanitizer`, `Helgrin` подходят для анализа проектов, содержащих элементы параллельного программирования. Однако `Thread sanitizer` может пропустить потенциально опасные места и не написать предупреждение. Поэтлиу лучше использовать сразу два инструмента. Существуют также ложные срабатывания, верить каждой выыводимой ошибке не стоит.
