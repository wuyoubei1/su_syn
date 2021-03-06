package com.sub.syn.youhuiquan;

import java.util.List;

public interface PYouHuiQuanDao {

    
    /**
     * 批量增加抢购商品
     * @param pCategorys
     */
    public void addBatch(List<YouHuiQuan> beans);
    
    public void updateBatch(List<YouHuiQuan> beans);  
    
    /***saveOrUpdate**/
    public void saveOrUpdate(YouHuiQuan beans);
    
    public void insert(YouHuiQuan bean);
    
    public void update(YouHuiQuan bean);
    
    public YouHuiQuan select (YouHuiQuan bean);
    
    /**
     * 获取某个时间段内是否有同步过
     * @param pQianggou
     * @return
     */
    public int getCount(YouHuiQuan bean);
    
    public void deleteByTime(YouHuiQuan bean);
    
    public void deleteByIds(List<YouHuiQuan> id);
    
    /**
     * 删除淘宝基地的数据
     * @param id
     */
    public void deleteByIdsTbjd(List<YouHuiQuan> id);
    
    /***清除无用的优惠券信息**/
    public void delete4EndTime();
    
    

}