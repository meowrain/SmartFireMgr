package com.xszx;

import com.xszx.util.WhiteList;
import org.junit.jupiter.api.Test;

public class WhiteListTest {
    @Test
    void test() {
        System.out.println(WhiteList.isInWhiteList("/api/admin/login"));
        assert WhiteList.isInWhiteList("/api/admin/login");
    }
}
