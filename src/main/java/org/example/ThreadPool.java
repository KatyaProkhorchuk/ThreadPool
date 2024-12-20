package org.example;

public interface ThreadPool {
    void start(); // запускает потоки. Потоки бездействуют, до тех пор пока не появится новое задание в очереди (см. execute)

    void execute(Runnable runnable); // складывает это задание в очередь. Освободившийся поток должен выполнить это задание. Каждое задание должно быть выполнено ровно 1 раз

    void stop(boolean cancelTasks) throws InterruptedException; // останавливает запущенный потоки
}
