package com.sgj.microschoolsystem.mapper;

import com.sgj.microschoolsystem.model.PageantLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface PageantLikeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pageant_like
     *
     * @mbg.generated Mon Jun 03 05:05:30 CST 2019
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pageant_like
     *
     * @mbg.generated Mon Jun 03 05:05:30 CST 2019
     */
    int insert(PageantLike record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pageant_like
     *
     * @mbg.generated Mon Jun 03 05:05:30 CST 2019
     */
    PageantLike selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pageant_like
     *
     * @mbg.generated Mon Jun 03 05:05:30 CST 2019
     */
    List<PageantLike> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pageant_like
     *
     * @mbg.generated Mon Jun 03 05:05:30 CST 2019
     */
    int updateByPrimaryKey(PageantLike record);

    Integer countVoter(@Param("voter") String voter);

}