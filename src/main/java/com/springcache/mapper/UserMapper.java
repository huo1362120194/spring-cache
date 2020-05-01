package com.springcache.mapper;

import com.springcache.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM User WHERE id = #{id}")
    User findUserById(Integer id);

    @Update("update user set name=#{name},age=#{age} where id=#{id}")
    int updateUser(User user);

    @Insert("insert into user set name=#{name},age=#{age}")
    int create(User user);

    @Delete("DELETE FROM USER WHERE id=#{id}")
    int deleteUserById(Integer id);
}
