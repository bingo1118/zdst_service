package com.cloudfire.until.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.cloudfire.until.pojo.PageModel;


public interface HibernateDao {
	/**
     * 删除
     * @param entity
     */
    public <T> void delete(T entity);
  
    /**
     * Execute an HQL query. 
     * @param queryString
     * @return
     */
    public <T> List<T> find(String queryString);
    /**
     * Execute an HQL query. 
     * @param bean
     * @return
     */
    public <T> List<T> find(Class<T> bean);
    /**
     * 批量修改或删�?
     * @param queryString
     * @param values
     */
     public int bulkUpdate(String queryString, Object[] values);
     /**
      * 批量删除
      * @param entities
      */
     public <T> void deleteAll(Collection<T> entities);
    /**
     *  Execute an HQL query, binding a number of values to "?" 
     * @param queryString
     * @param values
     * @return
     */
    public List<?> find(String queryString, Object[] values);
    /**
     * 获取唯一实体
     * @param queryString HQL query string
     * @param params query object array params
     * @return unique object
     * @see org.hibernate#Session
     * @throws java.lang.IllegalArgumentException if queryString is null
     */
    public <T> T findUniqueEntity(String queryString, Object ... params);
    
    /**
	 * 多条件分页查�?
	 * @param hql query string
	 * @param startRow begin row
	 * @param pageSize page number
	 * @param params query object params array
	 * @return the query list<?> result
	 * @see org.hibernate#Session
     * @throws java.lang.IllegalArgumentException if queryString is null
	 */
    public <T> List<T> findByPage(String queryString, Integer startRow,
	    Integer pageSize, Object... params);

    /**
     * Execute a named query. 
     * @param queryName
     * @return
     */
    public <T> List<T> findByNamedQuery(String queryName);

    /**
     * Execute a named query binding a number of values to "?"
     * @param queryName
     * @param values
     * @return
     */
    public <T> List<T> findByNamedQuery(String queryName, Object... values);
    
    /**
     * 获取单个实体
     * @param entityClass
     * @param id
     * @return
     */
    public <T> T get(Class<T> entityClass, Serializable id);

    /**
     *  Execute a query for persistent instances. 
     * @param queryString
     * @return
     */
    public <T> Iterator<T> iterate(String queryString);
    
    /**
     *  条件查询返回�?��迭代�?
     * @param queryString
     * @param values
     * @return
     */
    public <T> Iterator<T> iterate(String queryString, Object ... values);

    /**
     *  load获取�?��实体
     * @param entityClass
     * @param id
     * @return
     */
    public <T> T load(Class<T> entityClass, Serializable id);
    
    /**
     * 持久化一个对�?
     * @param entity
     */
    public <T> void persist(T entity);

    /**
     * 刷新�?��对象
     * @param entity
     */
    public <T> void refresh(T entity);
    
    /**
     * 保存�?��对象
     * @param entity
     * @return
     */
    public <T> Serializable save(T entity);
    
    /**
     * 保存�?��集合
     * @param entities
     */
    public <T> void saveAll(Collection<T> entities);
    
    /**
     * 保存或修改一个实�?
     * @param entity
     */
    public <T> void saveOrUpdate(T entity);
    
    /**
     * 保存或修改一个集�?
     * @param entities
     */
    public <T> void saveOrUpdateAll(Collection<T> entities);
    
    /**
     * 修改�?��实体
     * @param entity
     */
    public <T> void update(T entity);
    
    /**
     * 修改�?��集合
     * @param entities
     */
    public <T> void updateAll(Collection<T> entities);

    /**
     * id对应的对象是否存�?
     * @param c
     * @param id
     * @return
     */
    public <T> boolean exist(Class<T> c, Serializable id);
    
    /**
     * 统计总条�?
     * @param hql
     * @return
     */
    public Integer count(String hql);
    
    /**
     * 根据条件统计总条�?
     * @param hql
     * @param obj
     * @return
     */
    public Integer count(String hql, Object ... obj);
    
	/**
	 * 多条件分页查�?
	 * @param queryString HQL语句
	 * @param startRow �?��行数
	 * @param pageSize 页数
	 * @param params   参数集合
	 * @return 分页查询结果
	 * @see #findByPage(String, Integer, Integer, Object...)
	 */
	public <T> List<T> findByPage(String queryString, PageModel pageModel, List<?> params);
}
