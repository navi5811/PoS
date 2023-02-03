package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import org.apache.xpath.operations.Or;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TestReportDto extends AbstractUnitTest {
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

    @Test
    public void testBrandReport() throws ApiException {
        String brandName="brand";
        String brandCategory="category";
        BrandForm f=new BrandForm();
        f.setBrandName(brandName);
        f.setBrandCategory(brandCategory);
        brandDto.addBrand(f);
        List<BrandData> list= reportDto.getAllBrand();
        assertEquals(list.get(0).getBrandName(),brandName);
        assertEquals(list.get(0).getBrandCategory(),brandCategory);
    }

    @Test
    public void testInventoryReport() throws ApiException {
        String barcode = "barcode";
        String brandname = "name";
        String brandcategory = "category";
        String productname = "pname";
        Double mrp = 800.0;
        Integer quantity = 0;

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

        quantity=5;

        InventoryForm p=new InventoryForm();
        p.setInventoryProductBarcode(barcode);
        p.setProductQuantity(quantity);

        inventoryDto.updateInventory(p);
        //till now inventory added related to a product\

        List<ReportInventoryData> list=reportDto.getInventoryReport();

        assertEquals(list.get(0).getBrandName(),brandname);
        assertEquals(list.get(0).getBrandCategory(),brandcategory);
        assertEquals(list.get(0).getReportInventoryQuantity() , quantity);
    }
    @Test
    public void testSalesReport() throws ApiException, ParseException {
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
        //order created
        Date date=new Date();
        ReportSalesForm salesForm=new ReportSalesForm();
        salesForm.setBrand(brandname);
        salesForm.setCategory(brandcategory);
        salesForm.setEndDate("");
        salesForm.setStartDate("2000-02-02");
        List<ReportSalesData> salesDataList=reportDto.getSalesReport(salesForm);
        assertEquals(salesDataList.get(0).getBrand(),brandname);

        assertEquals(salesDataList.get(0).getQuantity(),quantity);
        assertEquals(salesDataList.get(0).getTotalAmount(),totalAmount);
        assertEquals(salesDataList.get(0).getCategory(),brandcategory);




    }








}
