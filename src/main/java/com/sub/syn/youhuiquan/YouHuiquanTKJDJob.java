package com.sub.syn.youhuiquan;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sub.syn.common.DateUtils;

/**
 * 淘客基地优惠券同步
 * @author wuyb
 *
 */
public class YouHuiquanTKJDJob extends ParentYhyJob{

	private static Log log=LogFactory.getLog(YouHuiquanTKJDJob.class);
	
	
	private String url = "http://api.tkjidi.com/getGoodsLink?appkey=b5eb0c143fde9427cb7cfa21d89afa06&type=www_lingquan&page=[page]";
	
	private int page = 1;
	
	private DecimalFormat df = new DecimalFormat("######0.00"); 
	
	private PYouHuiQuanService service=new PYouHuiQuanService();
	
	public void synJob() {
		String json = httpGetRequest(url.replace("[page]", page + ""));
		//log.info(json);
		List<YouHuiQuan> list = jsonToBean(json);
		try {
			while (list.size()>0) {
				service.deleteByIdsTbjd(list);
				service.saveBath(list);
				++page;
				log.info("淘客基地 记录条数："+list.size() + " 页数：" + page);
				json = httpGetRequest(url.replace("[page]", page + ""));
				try{
					list = jsonToBean(json);
				}catch(com.alibaba.fastjson.JSONException e){
					break;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			synJob();
		}
	}

	private List<YouHuiQuan> jsonToBean(String json) {
		String temp="";
		DateFormat datedf = new SimpleDateFormat("yyyyMMddhhmmss");
		String dateStr=datedf.format(new Date());
		
		List<YouHuiQuan> list = new ArrayList<YouHuiQuan>();
		com.alibaba.fastjson.JSONObject object = JSON.parseObject(json);
		if("".equals(object.getString("data"))){
			return list;
		}
		JSONArray items = object.getJSONArray("data");
		String s = "";
		YouHuiQuan bean = new YouHuiQuan();
		for (int i = 0; i < items.size(); i++) {
			bean = new YouHuiQuan();
			s = items.getString(i);
			JSONObject obj = JSONObject.parseObject(s);
			bean.setId(Integer.parseInt(obj.getString("id")));
			bean.setAliClick(obj.getString("goods_url"));
			bean.setCid(Integer.parseInt(obj.getString("cate_id")));
			bean.setCateName(obj.getString("cate_name"));
			bean.setD_title(obj.getString("D_title"));
			bean.setDsr(obj.getString("Dsr"));
			bean.setGoodsId(obj.getLongValue("goods_id"));//淘宝商品ID
			bean.setIntroduce(obj.getString("quan_guid_content"));//商品文案
			bean.setIsTmall(obj.getIntValue("IsTmall"));
			bean.setOrgPrice(Double.parseDouble(df.format(obj.getDoubleValue("price")))); /*正常售价*/
			bean.setPic(obj.getString("pic"));            //商品图片
			bean.setPrice(Double.parseDouble(df.format(obj.getDoubleValue("price_after_coupons"))) );   /*券后价*/
			bean.setqQuanMLink(obj.getString("Quan_m_link"));
			bean.setQuanCondition(obj.getString("quan_note"));//使用条件
			bean.setQuanLink(obj.getString("quan_link"));  /*领券链接*/
			bean.setQuanPrice(Double.parseDouble(df.format(obj.getDoubleValue("price_coupons"))) );//优惠券金额
			bean.setSalesNum(obj.getIntValue("sales"));  /*商品销量*/
			bean.setSellerID(obj.getString("SellerID"));
			bean.setTitle(obj.getString("goods_name"));
			bean.setCreateTime(new Date());
			bean.setQuanId(obj.getString("quan_id"));
			bean.setType(2);//淘客基地
			try {
				temp=obj.getString("quan_expired_time");
				if(!"".equals(temp)){
					temp=temp.replaceAll("-","").replaceAll(":","").replaceAll(" ","");
				}
//				if(Long.parseLong(temp)<=Long.parseLong(dateStr)){
//					continue;
//				}
				//System.out.println("temp:"+temp);
				bean.setQuanTime(DateUtils.parseDate(obj.getString("quan_expired_time"), "yyyy-mm-dd HH:MM:SS"));
			} catch (ParseException e) {
				e.printStackTrace();
			}//领券结束时间
			bean.setQuanSurplus(obj.getIntValue("quan_shengyu"));/*优惠券剩余数量*/
			bean.setNote(obj.getString("quan_note"));
			list.add(bean);
		}
		return list;
	}

	public static void main(String[] args) throws ParseException {
		long start=System.currentTimeMillis();
		YouHuiquanTKJDJob b = new YouHuiquanTKJDJob();
		b.synJob();
//		long end=System.currentTimeMillis();
//		System.out.println("耗时："+(end-start)/1000+"秒");
		
//		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
//		
//		String df1=df.format(new Date());
//		
//		Date dt1=DateUtils.parseDate("20160612172822", "yyyymmdd HH:MM:SS");
//		long d1=dt1.getTime();
//		Date dt2=new Date();
//		long d2=dt2.getTime();
//		System.out.println(d1+"||"+d2);
//		 if (dt1.getTime() > dt2.getTime()) {
//             System.out.println("dt1 在dt2前");
//         } else if (dt1.getTime() < dt2.getTime()) {
//             System.out.println("dt1在dt2后");
//         } 
		
	}
}
