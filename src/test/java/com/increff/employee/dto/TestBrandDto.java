package com.increff.employee.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.employee.dao.BrandDao;
import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.service.AbstractUnitTest;
import com.increff.employee.service.ApiException;

public class TestBrandDto extends AbstractUnitTest {

	@Autowired
	private BrandDto dto;
	
	@Autowired
	private BrandDao dao;
	
	 @Test
	    public void testAddAndGetAll() throws ApiException {
	        String brand = "testBrand";
	        String category = "testCategory";

	        //Check The Insert Operation
	        BrandPojo p = new BrandPojo();
	        p.setBrandCategory(category);
	        p.setBrandName(brand);
	        dao.insert(p);

	        //Verifying the getAll operation
	        List<BrandData> list =dto.getAllBrand();
	        assertEquals(list.get(0).getBrandName(),brand);
	        assertEquals(list.get(0).getBrandCategory(),category);
	    }
	  @Test
	    public void testGetById() throws ApiException {
	        String brand = "testBrand";
	        String category = "testCategory";

	        BrandPojo p = new BrandPojo();
	        p.setBrandCategory(category);
	        p.setBrandName(brand);
	        dao.insert(p);
	        List<BrandData> list = dto.getAllBrand();

	        //Verifying the getById operation
	        BrandData data = dto.getBrand(list.get(0).getBrandId());
	        assertEquals(data.getBrandName(),brand);
	        assertEquals(data.getBrandCategory(),category);

	    }
	  
	   @Test
	    public void testUpdate() throws ApiException {
	        String brand = "testBrand";
	        String category = "testCategory";

	        BrandPojo p = new BrandPojo();
	        p.setBrandCategory(category); 
	        p.setBrandName(brand);
	        dao.insert(p);

	        List<BrandData> list = dto.getAllBrand();

	        //Verifying the Update function
	        brand="brand";
	        category="category";
	        int id = list.get(0).getBrandId(); 
	        //creating new brandform and entering the brand and category and sendinf it to update brand
	        BrandForm f= new BrandForm();
	        f.setBrandName(brand);
	        f.setBrandCategory(category);
	       
	        dto.updateBrand(list.get(0).getBrandId(),f);
	        list = dto.getAllBrand();
	        assertEquals(list.get(0).getBrandName(),brand);
	        assertEquals(list.get(0).getBrandCategory(),category);
	        assertEquals(list.get(0).getBrandId(),id);
	    }
	   
	   
	   
	   @Test
	   public void testaddbrand() throws ApiException  {
		   String brand = "brand";
	        String category = "category";
		   BrandForm f= new BrandForm();
		   f.setBrandName(brand);
		   f.setBrandCategory(category);

		   dto.addBrand(f);
		   List<BrandData> list = dto.getAllBrand();

		   //Verifying the getById operation

		   assertEquals(list.get(0).getBrandName(),brand);
		   assertEquals(list.get(0).getBrandCategory(),category);
	   }
	   @Test
	    public void testBrandCategoryUniqueness(){
		    String brand = "brand";
	        String category = "category";

	        BrandPojo p = new BrandPojo();
	        p.setBrandCategory(category);
	        p.setBrandName(brand);
	        dao.insert(p);
	        //Check Uniqueness at time of add
	        boolean flag = false;
	        try {
	        	BrandForm f= new BrandForm();
		        f.setBrandName(brand);
		        f.setBrandCategory(category);
	            dto.addBrand(f);
	        }catch (ApiException e){
	            flag=true;
	        }
	        if(Objects.equals(flag,false))fail();

	        //Check Uniqueness at the time of update
	        try {
	            List<BrandData> list = dto.getAllBrand();
	            BrandForm f= new BrandForm();
		        f.setBrandName(brand);
		        f.setBrandCategory(category);
	            dto.updateBrand(list.get(0).getBrandId(),f);
	        }catch (ApiException e){
	            return;
	        }
	        fail();
	    }
		@Test
		 public void testdeletebrand(){
			String brand = "brand";
			String category = "category";

			BrandPojo p = new BrandPojo();
			p.setBrandCategory(category);
			p.setBrandName(brand);
			dao.insert(p);



			List<BrandData> list = dto.getAllBrand();
			int id = list.get(0).getBrandId();
			dto.deleteBrand(id);

			try{
				dto.getBrand(id);
			}catch(ApiException e){
				return;
			}
			fail();
		}
		@Test
		 public void testfindbrand() throws ApiException{
			String brand = "testBrand";
	        String category = "testCategory";

	        BrandPojo p = new BrandPojo();
	        p.setBrandCategory(category);
	        p.setBrandName(brand);
	        dao.insert(p);
	        List<BrandData> list = dto.getAllBrand();

	        //Verifying the getById operation
	        BrandForm form = dto.findBrand(list.get(0).getBrandName(),list.get(0).getBrandCategory());
	        assertEquals(form.getBrandName(),brand);
	        assertEquals(form.getBrandCategory(),category);
		}
		
		@Test
		 public void testvalidation(){
			 String brand = "";
		        String category = "";


			   BrandForm f= new BrandForm();
			   f.setBrandName(brand);
			   f.setBrandCategory(category);

			   boolean flag=false;
			   try{
					dto.addBrand(f);
				}catch(ApiException e){
					   flag=true;
		        }
		        if(Objects.equals(flag,false))fail();
		        
		        brand="name";
		        category="";
		        BrandForm p= new BrandForm();
				   p.setBrandName(brand);
				   p.setBrandCategory(category);
		        
		        flag=false;
		        try{
					dto.addBrand(p);
				}catch(ApiException e){
					   flag=true;
		        }
		        if(Objects.equals(flag,false))fail();
		        
		}
		
		
		
	   
}
