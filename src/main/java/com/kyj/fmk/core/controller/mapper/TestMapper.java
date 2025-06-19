package com.kyj.fmk.core.controller.mapper;

import com.kyj.fmk.core.controller.Test2DTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
  Test2DTO selectTest123();
}
