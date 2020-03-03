# Yet Another Bash Architecture

![CI](https://github.com/kry127/Yet-Another-Bash-Architecture/workflows/CI/badge.svg)

Homework project of "Software Design" classes, Joint MSc program of ITMO University and JetBrains.

Учебный проект по курсу "Software Desing", совместная магистерская программа Университета ИТМО и JetBrains.

## WARNING
USING THIS SOFTWARE YOU ARE THE ONLY RESPONSIBLE PERSON FOR ALL CASUALTIES THAT CAN BE
MET DURING EXPLOITATION OF THE SOFTWARE. DEVELOPER IS NOT RESPONSIBLE FOR MALFUNCTION ON
DIFFERENT OPERATIONAL SYSTEMS, DATA LOSS OR SYSTEM SECURITY FAULTS WHILE USING THIS SOFTWARE!

## ВНИМАНИЕ
ИСПОЛЬЗУЯ ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ, ВЫ ИСКЛЮЧИТЕЛЬНО ПОЛАГАЕТЕСЬ
 НА СВОЙ СТРАХ И РИСК. РАЗРАБОТЧИК НЕ НЕСЁТ ОТВЕТСТВЕННОСТИ ЗА НЕПРАВИЛЬНУЮ РАБОТУ КОДА НА ОПЕРАЦИОННЫХ СИСТЕМАХ,
 А ТАКЖЕ ПОТЕРЮ ДАННЫХ И ПОРЧУ СИСТЕМЫ ПРИ ИСПОЛЬЗОВНИИ ДАННОГО ПО!


## Применяемость программы
1. Не стоит применять в реальных условиях. Возможно, только в учебных для изучения исходного кода

## Архитектурные артефакты

Артефакты, сгенерированные до начала работы над проектом

#### Диаграмма без классификации (создана во время пары)

![Strange Diagram](img/CLI%20architecture-Strange%20Diagram.png)


#### Use-case диаграмма

![Use Case Diagram](img/CLI%20architecture-Use%20Case%20Diagram.png)


#### UML диаграмма классов

![Use Case Diagram](img/CLI%20architecture-Class%20Diagram.png)

##Резюме
Декомпозиция на классы помогла выявить ряд существенных ошибок, которые были исправлены во время
прототипирования. Однако, не все были найдены -- полученная архитектура слегка отличается от изначально
запланированной, посколько не было учтено несколько моментов, касаемо функционала системы: групповая конкатенация
литералов различной природы в один составной литерал, а также интерполяция $ непосредственно в командах (учитывая,
что названия программ тоже могут быть составными названиями программ). Для построенной архитектуры это оказалось
вызов, но она испортилась только на один класс, который пока нельзя логично вписать в иерархию объектов:
__LiteralConcat__, роль которого в диаграмме классов на самом деле играет класс Literal.