package ru.practicum.ewm.utility;

import org.springframework.data.domain.PageRequest;

public class PageQualifier {
    public static PageRequest definePage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
