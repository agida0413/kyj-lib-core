package com.kyj.fmk.core.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class Testdto {
    @NotNull(message = "테스트입ㄴ다.")
    @Max(value = 1,message = "dddddd123123")
    private int test;
}
