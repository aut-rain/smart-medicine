package com.medical.smartmedicine.illness.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.illness.dto.IllnessCreateDTO;
import com.medical.smartmedicine.illness.dto.IllnessQueryDTO;
import com.medical.smartmedicine.illness.dto.IllnessUpdateDTO;
import com.medical.smartmedicine.illness.vo.IllnessDetailVO;
import com.medical.smartmedicine.illness.vo.IllnessVO;

import java.util.List;

/**
 * 疾病服务接口
 * 负责疾病信息查询、浏览等业务
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface IllnessService {

    /**
     * 分页查询疾病列表
     *
     * @param queryDTO 查询参数
     * @return 疾病列表
     */
    PageResult<IllnessVO> listIllnesses(IllnessQueryDTO queryDTO);

    /**
     * 根据ID获取疾病详情
     * 包含分类信息和关联药品
     *
     * @param id 疾病ID
     * @return 疾病详情
     */
    IllnessDetailVO getIllnessById(Integer id);

    /**
     * 获取热门疾病列表
     * 按浏览量排序
     *
     * @param limit 返回数量
     * @return 热门疾病列表
     */
    List<IllnessVO> getHotIllnesses(Integer limit);

    /**
     * 搜索疾病
     * 根据关键词模糊搜索疾病名称和症状
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<IllnessVO> searchIllnesses(String keyword);

    /**
     * 分页搜索疾病
     * 根据关键词模糊搜索疾病名称和症状，支持分页
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 分页搜索结果
     */
    PageResult<IllnessVO> searchIllnessesPaged(String keyword, Integer page, Integer size);

    /**
     * 增加疾病浏览量
     *
     * @param id 疾病ID
     */
    void increasePageview(Integer id);

    /**
     * 创建疾病(管理员)
     *
     * @param createDTO 创建信息
     * @return 创建的疾病信息
     */
    IllnessVO createIllness(IllnessCreateDTO createDTO);

    /**
     * 更新疾病(管理员)
     *
     * @param id 疾病ID
     * @param updateDTO 更新信息
     * @return 更新后的疾病信息
     */
    IllnessVO updateIllness(Integer id, IllnessUpdateDTO updateDTO);

    /**
     * 删除疾病(管理员)
     *
     * @param id 疾病ID
     */
    void deleteIllness(Integer id);
}
