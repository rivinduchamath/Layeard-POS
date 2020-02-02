package lk.ijse.dep.pos.dao.custom;

import lk.ijse.dep.pos.dao.CrudDAO;
import lk.ijse.dep.pos.entity.Order;

public interface OrderDAO extends CrudDAO<Order, Integer> {

    int getLastOrderId() throws Exception;

    boolean existByCustomerId(String customerId) throws Exception;

    boolean existsByItemId(String itemCode) throws Exception;
}
