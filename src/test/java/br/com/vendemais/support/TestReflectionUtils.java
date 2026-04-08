package br.com.vendemais.support;

import org.springframework.test.util.ReflectionTestUtils;

public final class TestReflectionUtils {

    private TestReflectionUtils() {
    }

    public static void setId(Object target, Long id) {
        ReflectionTestUtils.setField(target, "id", id);
    }
}
