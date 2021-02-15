package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductSearchEngineTest
{
    private static final String TEST_PRODUCT_CODE = "test";
    private static final String TEST_PRODUCT_DESCRIPTION = "test";
    private static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal(1000000);
    private static final Currency TEST_PRODUCT_CURRENCY = Currency.getInstance("USD");
    private static final int TEST_PRODUCT_STOCK = 100;
    private static final String TEST_PRODUCT_IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg";
    private static final String BLANK_QUERY = "  ";
    private static final String DEFAULT_QUERY = "  ";
    private static final SortField DEFAULT_SORT_FIELD = null;
    private static final SortOrder DEFAULT_SORT_ORDER = null;
    private static final TreeMap<Date, BigDecimal> DEFAULT_PRICE_HISTORY = new TreeMap<>();

    @Mock
    private ProductDao productDao;

    private ProductSearchEngine searchEngine;

    @Before
    public void setup() {
        searchEngine = new ProductSearchEngine(productDao);
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        when(productDao.getProducts()).thenReturn(new ArrayList<Product>(SampleProducts.PRODUCTS));

        assertFalse(searchEngine.findProducts(null, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).isEmpty());
    }

    @Test
    public void shouldFindProductsWithBlankQuery() {
        when(productDao.getProducts()).thenReturn(new ArrayList<Product>(SampleProducts.PRODUCTS));

        assertFalse(searchEngine.findProducts(BLANK_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).isEmpty());
    }

    @Test
    public void shouldTestSortByDescriptionInAscendingOrder() {
        when(productDao.getProducts()).thenReturn(new ArrayList<Product>(SampleProducts.PRODUCTS));

        List<Product> products = searchEngine.findProducts(DEFAULT_QUERY, SortField.description, SortOrder.asc);

        for(int i = 0; i < products.size() - 1; i++) {
            assertTrue(products.get(i).getDescription().compareTo(products.get(i + 1).getDescription()) <= 0);
        }
    }

    @Test
    public void shouldTestSortByPriceDescendingOrder() {
        when(productDao.getProducts()).thenReturn(new ArrayList<Product>(SampleProducts.PRODUCTS));

        List<Product> products = searchEngine.findProducts(DEFAULT_QUERY, SortField.price, SortOrder.desc);

        for(int i = 0; i < products.size() - 1; i++) {
            assertTrue(products.get(i).getPrice().compareTo(products.get(i + 1).getPrice()) >= 0);
        }
    }

    @Test
    public void shouldNotFindProductsWithoutStock() {
        List<Product> products = new ArrayList<Product>(SampleProducts.PRODUCTS);
        when(productDao.getProducts()).thenReturn(products);
        int findProductSizeBeforeTestProductAddition = searchEngine.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();
        Long maxId = products
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, 0, TEST_PRODUCT_IMAGE_URL, DEFAULT_PRICE_HISTORY));
        int findProductSizeAfterTestProductAddition = searchEngine.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }

    @Test
    public void shouldNotFindProductsWithNullPrice() {
        List<Product> products = new ArrayList<Product>(SampleProducts.PRODUCTS);
        when(productDao.getProducts()).thenReturn(products);
        int findProductSizeBeforeTestProductAddition = searchEngine.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();
        Long maxId = products
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, null, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL, DEFAULT_PRICE_HISTORY));
        int findProductSizeAfterTestProductAddition = searchEngine.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }
}
