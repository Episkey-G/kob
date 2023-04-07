package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.Game;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper extends BaseMapper<Game> {
}
