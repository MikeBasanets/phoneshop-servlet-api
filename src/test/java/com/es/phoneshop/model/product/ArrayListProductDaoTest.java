package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoTest
{
    private static final String TEST_PRODUCT_CODE = "test";
    private static final String TEST_PRODUCT_DESCRIPTION = "test";
    private static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal(1000000);
    private static final Currency TEST_PRODUCT_CURRENCY = Currency.getInstance("USD");
    private static final int TEST_PRODUCT_STOCK = 100;
    private static final String TEST_PRODUCT_IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg";
    private static final int PRODUCT_TO_BE_REPLACED_INDEX = 0;
    private static final String BLANK_QUERY = "  ";
    private static final String DEFAULT_QUERY = "  ";
    private static final SortField DEFAULT_SORT_FIELD = null;
    private static final SortOrder DEFAULT_SORT_ORDER = null;


    private ArrayListProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        productDao.setProducts(getTestProducts());
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        assertFalse(productDao.findProducts(null, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).isEmpty());
    }

    @Test
    public void shouldFindProductsWithBlankQuery() {
        assertFalse(productDao.findProducts(BLANK_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).isEmpty());
    }

    @Test
    public void shouldTestSortByDescriptionInAscendingOrder() {
        List<Product> products = productDao.findProducts(DEFAULT_QUERY, SortField.description, SortOrder.asc);

        for(int i = 0; i < products.size() - 1; i++) {
            assertTrue(products.get(i).getDescription().compareTo(products.get(i + 1).getDescription()) <= 0);
        }
    }

    @Test
    public void shouldTestSortByPriceDescendingOrder() {
        List<Product> products = productDao.findProducts(DEFAULT_QUERY, SortField.price, SortOrder.desc);

        for(int i = 0; i < products.size() - 1; i++) {
            assertTrue(products.get(i).getPrice().compareTo(products.get(i + 1).getPrice()) >= 0);
        }
    }

    @Test
    public void shouldDeleteSingleProduct() {
        int initialProductNumber = productDao.getProducts().size();

        productDao.delete(productDao.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).get(PRODUCT_TO_BE_REPLACED_INDEX).getId());

        assertTrue(productDao.getProducts().size() == initialProductNumber - 1);
    }

    @Test
    public void shouldDeleteAllProducts() {
        productDao.getProducts().forEach(product -> productDao.delete(product.getId()));

        assertTrue(productDao.getProducts().isEmpty());
    }

    @Test
    public void shouldAddNewProduct() {
        int initialProductNumber = productDao.getProducts().size();

        productDao.save(new Product(TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL));

        assertTrue(productDao.getProducts().size() == initialProductNumber + 1);
    }

    @Test
    public void shouldAddNewProductWithUniqueId() {
        int initialProductNumber = productDao.getProducts().size();
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();

        productDao.save(new Product(maxId + 1, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL));

        assertTrue(productDao.getProducts().size() == initialProductNumber + 1);
    }

    @Test
    public void shouldReplaceExistingProduct() throws NoSuchProductException {
        int initialProductNumber = productDao.getProducts().size();
        Long testProductId = productDao.getProducts().get(0).getId();
        Product testProduct = new Product(testProductId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL);

        productDao.save(testProduct);

        assertTrue(productDao.getProducts().size() == initialProductNumber);
        assertTrue(productDao.getProduct(testProductId) == testProduct);
    }

    @Test(expected = NoSuchProductException.class)
    public void shouldGetNonExistentProduct() throws NoSuchProductException {
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();

        productDao.getProduct(maxId + 1);
    }

    @Test(expected = NoSuchProductException.class)
    public void testGetProductWithNullId() throws NoSuchProductException {
        productDao.getProduct(null);
    }

    @Test
    public void shouldFindProductsWithoutStock() {
        int findProductSizeBeforeTestProductAddition = productDao.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        List<Product> products = productDao.getProducts();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, 0, TEST_PRODUCT_IMAGE_URL));
        productDao.setProducts(products);
        int findProductSizeAfterTestProductAddition = productDao.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }

    @Test
    public void shouldFindProductsWithNullPrice() {
        int findProductSizeBeforeTestProductAddition = productDao.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        List<Product> products = productDao.getProducts();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, null, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL));
        productDao.setProducts(products);
        int findProductSizeAfterTestProductAddition = productDao.findProducts(DEFAULT_QUERY, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }

    private List<Product> getTestProducts() {
        List<Product> result = new ArrayList<Product>();
        Currency usd = Currency.getInstance("USD");
        result.add(new Product(1L, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        result.add(new Product(2L, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        result.add(new Product(3L, "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        result.add(new Product(4L, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        result.add(new Product(5L, "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        result.add(new Product(6L, "htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        result.add(new Product(7L, "sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        result.add(new Product(8L, "xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        result.add(new Product(9L, "nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        result.add(new Product(10L, "palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        result.add(new Product(11L, "simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        result.add(new Product(12L, "simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        result.add(new Product(13L, "simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        return result;
    }
}
