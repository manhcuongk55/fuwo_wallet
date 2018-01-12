package vn.plusplusc.fuwo.dao;

import java.util.List;

import vn.plusplusc.fuwo.model.CartInfo;
import vn.plusplusc.fuwo.model.OrderDetailInfo;
import vn.plusplusc.fuwo.model.OrderInfo;
import vn.plusplusc.fuwo.model.PaginationResult;

public interface OrderDAO {
	 
    public void saveOrder(CartInfo cartInfo);
 
    public PaginationResult<OrderInfo> listOrderInfo(int page,
            int maxResult, int maxNavigationPage);
    
    public OrderInfo getOrderInfo(String orderId);
    
    public List<OrderDetailInfo> listOrderDetailInfos(String orderId);
 
}