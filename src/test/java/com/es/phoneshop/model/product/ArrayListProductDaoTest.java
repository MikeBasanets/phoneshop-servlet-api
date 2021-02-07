package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
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

    private ArrayListProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        assertFalse(productDao.findProducts(null).isEmpty());
    }

    @Test
    public void shouldFindProductsWithBlankQuery() {
        assertFalse(productDao.findProducts(BLANK_QUERY).isEmpty());
    }

    @Test
    public void shouldDeleteSingleProduct() {
        int initialProductNumber = productDao.getProducts().size();

        productDao.delete(productDao.findProducts(null).get(PRODUCT_TO_BE_REPLACED_INDEX).getId());

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
        int findProductSizeBeforeTestProductAddition = productDao.findProducts(null).size();
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        List<Product> products = productDao.getProducts();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE, TEST_PRODUCT_CURRENCY, 0, TEST_PRODUCT_IMAGE_URL));
        productDao.setProducts(products);
        int findProductSizeAfterTestProductAddition = productDao.findProducts(null).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }

    @Test
    public void shouldFindProductsWithNullPrice() {
        int findProductSizeBeforeTestProductAddition = productDao.findProducts(null).size();
        Long maxId = productDao.getProducts()
                .stream()
                .map(product -> product.getId())
                .max(Comparator.comparing(Long::valueOf))
                .get();
        List<Product> products = productDao.getProducts();
        products.add(new Product(maxId, TEST_PRODUCT_CODE, TEST_PRODUCT_DESCRIPTION, null, TEST_PRODUCT_CURRENCY, TEST_PRODUCT_STOCK, TEST_PRODUCT_IMAGE_URL));
        productDao.setProducts(products);
        int findProductSizeAfterTestProductAddition = productDao.findProducts(null).size();

        assertTrue(findProductSizeBeforeTestProductAddition == findProductSizeAfterTestProductAddition);
    }
}
