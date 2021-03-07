package com.es.phoneshop.model.order;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;

public class ArrayListOrderDaoTest
{
    private static final Currency TEST_CURRENCY = Currency.getInstance(Locale.US);
    private static final String SECURE_ID = "1234-abcd-5678-efgh";
    private static final Long ID = 1L;

    private Order order;

    private ArrayListOrderDao orderDao;

    @Before
    public void setup() {
        orderDao = ArrayListOrderDao.getInstance();
        order = new Order(TEST_CURRENCY);
    }

    @Test
    public void shouldTestSave() {
        orderDao.setOrders(new ArrayList<>());
        orderDao.save(order);

        assertFalse(orderDao.getOrders().isEmpty());
    }

    @Test
    public void shouldTestGetById() {
        order.setId(ID);
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        orderDao.setOrders(orderList);

        orderDao.getOrder(ID);
    }

    @Test
    public void shouldTestGetBySecureId() {
        order.setSecureId(SECURE_ID);
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        orderDao.setOrders(orderList);

        orderDao.getOrderBySecureId(SECURE_ID);
    }
}
