package com.kyj.fmk.core.controller.mapper;

import com.kyj.fmk.core.controller.Test2DTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class Repo {

    private final TestMapper testMapper;
    public Test2DTO selectTest(){
        return testMapper.selectTest123();
    }
}
