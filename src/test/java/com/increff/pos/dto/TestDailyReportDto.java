package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.AboutAppServiceTest;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDailyReportDto extends AbstractUnitTest {

    @Autowired
    private ReportDto reportDto;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private DailyReportDto dailyReportDto;



    @Test
    public void testGetAll() throws Exception {
        String barcode = "barcode";
        String brandname = "name";
        String brandcategory = "category";
        String productname = "pname";
        Double mrp = 800.0;

        BrandForm brandform = new BrandForm();
        brandform.setBrandName(brandname);
        brandform.setBrandCategory(brandcategory);

        brandDto.addBrand(brandform);
        ProductForm f = new ProductForm();
        f.setProductBarcode(barcode);
        f.setProductBrandName(brandname);
        f.setProductBrandCategoryName(brandcategory);
        f.setProductName(productname);
        f.setProductMrp(mrp);
        productDto.addProduct(f);

        InventoryForm inventoryform = new InventoryForm();
        inventoryform.setInventoryProductBarcode(barcode);
        inventoryform.setProductQuantity(50);
        inventoryDto.updateInventory(inventoryform);


        Integer quantity = 5;
        Double sellingPrice = 50.0;
        Double totalAmount = sellingPrice * quantity;

        OrderItemForm form = new OrderItemForm();
        form.setProductBarcode(barcode);
        form.setProductQuantity(quantity);
        form.setProductSellingPrice(sellingPrice);
        List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
        orderItemForm.add(form);
        orderDto.createOrder(orderItemForm);
        List<OrderItemPojo> orderItemPojoList=orderDto.getAll();

        orderDto.createInvoice(orderItemPojoList.get(0).getOrderId());
        //order created

        dailyReportDto.addReport();
        List<DailyReportData> dailyReportData=dailyReportDto.getAllReport();

        Integer items=1;
        Integer orders=1;
        assertEquals(dailyReportData.get(0).getTotal(),totalAmount);
        assertEquals(dailyReportData.get(0).getNumberOfItems(),items);
        assertEquals(dailyReportData.get(0).getNumberOfOrders(),orders);



    }


}
