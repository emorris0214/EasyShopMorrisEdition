package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.yearup.configuration.TestDatabaseConfig;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestDatabaseConfig.class)
@TestPropertySource("classpath:application.properties")
class MySqlProductDaoTest extends BaseDaoTestClass
{
    private MySqlProductDao dao;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlProductDao(dataSource);
    }

    @Test
    public void search_withPriceOnly_shouldReturnInRange()
    {
        List<Product> products = dao.search(null, new BigDecimal("20.00"), new BigDecimal("80.00"), null);
        assertFalse(products.isEmpty(), "Expected products but found none.");

        for (Product product : products)
        {
            BigDecimal price = product.getPrice();
            assertTrue(price.compareTo(new BigDecimal("20.00")) >= 0 &&
                            price.compareTo(new BigDecimal("80.00")) <= 0,
                    "Product price out of range: " + price);
        }
    }

    @Test
    public void search_withNoFilters_shouldReturnAllProducts()
    {
        List<Product> products = dao.search(null, null, null, null);
        assertFalse(products.isEmpty(), "Expected all products to be returned.");
    }

    @Test
    public void search_withAllFilters_shouldReturnMatchingProducts()
    {
        List<Product> products = dao.search(1, new BigDecimal("10.00"), new BigDecimal("100.00"), "red");
        assertTrue(products.isEmpty(), "Expected products but found none.");
    }


    @Test
    public void search_withPartialFilters_shouldReturnMatchingSubset()
    {
        List<Product> products = dao.search(3, new BigDecimal("50.00"), null, "blu");
        assertTrue(products.isEmpty(), "Expected products matching category 3 and color 'blu'.");
    }


    @Test
    public void search_withColorOnly_shouldMatchSubstring()
    {
        List<Product> products = dao.search(null, null, null, "gr"); // e.g., green
        assertFalse(products.isEmpty(), "Expected products with 'gr' in color.");
    }

}
