//package com.increff.employee.dto;
//
//import com.increff.employee.service.ApiException;
//import com.increff.employee.dao.BrandDao;
//import com.increff.employee.dao.ProductDao;
//import com.increff.employee.model.BrandForm;
//import com.increff.employee.model.ProductData;
//import com.increff.employee.model.ProductForm;
//import com.increff.employee.service.AbstractUnitTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class TestProductDto extends AbstractUnitTest {
//
//
//	@Autowired
//	private ProductDto dto;
//	
//	@Autowired
//	private ProductDao dao;
//
//    @Test
//    public void testaddbrand() throws ApiException {
//        String barcode = "barcode";
//        String brandname = "name";
//        String brandcategory ="category";
//        String productname="pname";
//        double mrp=800;
//
//        ProductForm f= new ProductForm();
//        f.setProductBarcode(barcode);
//        f.setProductBrandName(brandname);
//        f.setProductBrandCategoryName(brandcategory);
//        f.setProductName(productname);
//        f.setProductMrp(mrp);
//        dto.addProduct(f);
//        List<ProductData> list = dto.getAllProduct();
//
//        //Verifying the getById operation
//
//        assertEquals(list.get(0).getProductBarcode(),barcode);
//        assertEquals(list.get(0).getProductBrandName(),brandname);
//        assertEquals(list.get(0).getProductBrandCategoryName(),brandcategory);
//        assertEquals(list.get(0).getProductName(),productname);
//        assertEquals("list.get(0).getProductMrp()","mrp");
//    }
//
//}
