package com.kyj.fmk.core.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class testdto {
    @NotNull(message = "테스트입ㄴ다.")
    private int test;
}
