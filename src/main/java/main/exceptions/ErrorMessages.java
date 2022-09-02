package main.exceptions;

public class ErrorMessages {

    public static final String EMPTY_QUERY = "Задан пустой поисковый запрос";
    public static final String INCORRECT_QUERY = "Задан некорректный поисковый запрос: ";
    public static final String EMPTY_SITE = "Не указан сайт для поиска";
    public static final String UNINDEXED_SITE = "Указанный сайт не проиндексирован";
    public static final String INDEXING_STARTED_YET = "Индексация уже запущена";
    public static final String INDEXING_NOT_STARTED = "Индексация не запущена";
    public static final String SITE_OUT_OF_RANGE = "Данный сайт находится вне списка сайтов, указанных в конфигурационном файле: ";
    public static final String PAGE_OUT_OF_RANGE = "Данная страница находится вне списка сайтов, указанных в конфигурационном файле: ";
}
