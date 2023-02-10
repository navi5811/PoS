package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;

public class TestBrandDto extends AbstractUnitTest {

	@Autowired
	private BrandDto dto;

	@Test
	public void testAddAndGetAll() throws ApiException {
		String brand = "testbrand";
		String category = "testcategory";

		// Check The Insert Operation
		BrandForm f = new BrandForm();
		f.setBrandCategory(category);
		f.setBrandName(brand);
		dto.addBrand(f);

		// Verifying the getAll operation
		List<BrandData> list = dto.getAllBrand();
		assertEquals(list.get(0).getBrandName(), brand);
		assertEquals(list.get(0).getBrandCategory(), category);
	}

	@Test
	public void testGetById() throws ApiException {
		String brand = "testbrand";
		String category = "testcategory";

		BrandForm f = new BrandForm();
		f.setBrandCategory(category);
		f.setBrandName(brand);
		dto.addBrand(f);
		List<BrandData> list = dto.getAllBrand();

		// Verifying the getById operation
		BrandData data = dto.getBrand(list.get(0).getBrandId());
		assertEquals(data.getBrandName(), brand);
		assertEquals(data.getBrandCategory(), category);

	}

	@Test
	public void testUpdate() throws ApiException {
		String brand = "testbrand";
		String category = "testcategory";

		BrandForm f = new BrandForm();
		f.setBrandCategory(category);
		f.setBrandName(brand);
		dto.addBrand(f);

		List<BrandData> list = dto.getAllBrand();

		// Verifying the Update function
		brand = "brand";
		category = "category";
		int id = list.get(0).getBrandId();
		// creating new brandform and entering the brand and category and sendinf it to
		// update brand
		BrandForm nf = new BrandForm();
		nf.setBrandName(brand);
		nf.setBrandCategory(category);

		dto.updateBrand(list.get(0).getBrandId(), nf);
		list = dto.getAllBrand();
		assertEquals(list.get(0).getBrandName(), brand);
		assertEquals(list.get(0).getBrandCategory(), category);
		assertEquals(list.get(0).getBrandId(), id);
	}

	@Test
	public void testaddbrand() throws ApiException {
		String brand = "brand";
		String category = "category";
		BrandForm f = new BrandForm();
		f.setBrandName(brand);
		f.setBrandCategory(category);

		dto.addBrand(f);
		List<BrandData> list = dto.getAllBrand();

		// Verifying the getById operation

		assertEquals(list.get(0).getBrandName(), brand);
		assertEquals(list.get(0).getBrandCategory(), category);
	}

	@Test
	public void testBrandCategoryUniqueness() throws ApiException {
		String brand = "brand";
		String category = "category";

		BrandForm f = new BrandForm();
		f.setBrandCategory(category);
		f.setBrandName(brand);
		dto.addBrand(f);
		// Check Uniqueness at time of add
		boolean flag = false;
		try {
			BrandForm nf = new BrandForm();
			nf.setBrandName(brand);
			nf.setBrandCategory(category);
			dto.addBrand(nf);
		} catch (ApiException e) {
			flag = true;
		}
		if (Objects.equals(flag, false))
			fail();

		// Check Uniqueness at the time of update
		try {
			List<BrandData> list = dto.getAllBrand();
			BrandForm brandForm = new BrandForm();
			brandForm.setBrandName(brand);
			brandForm.setBrandCategory(category);
			dto.updateBrand(list.get(0).getBrandId(), brandForm);
		} catch (ApiException e) {
			return;
		}
		fail();
	}
	@Test
	public void testfindbrand() throws ApiException {
		String brand = "testbrand";
		String category = "testcategory";

		BrandForm f = new BrandForm();
		f.setBrandCategory(category);
		f.setBrandName(brand);
		dto.addBrand(f);

		List<BrandData> list = dto.getAllBrand();

		// Verifying the getById operation
		BrandForm form = dto.findBrand(list.get(0).getBrandName(), list.get(0).getBrandCategory());
		assertEquals(form.getBrandName(), brand);
		assertEquals(form.getBrandCategory(), category);

		// testing on random brand and category
		String newbrand = "randombrand";
		String newcategory = "randomcategory";
		try {
			dto.findBrand(newbrand,newcategory);
		} catch (ApiException e) {
			return;
		}
		fail();

	}

	@Test
	public void testvalidation() {
		String brand = "";
		String category = "";

		BrandForm f = new BrandForm();
		f.setBrandName(brand);
		f.setBrandCategory(category);

		boolean flag = false;
		try {
			dto.addBrand(f);
		} catch (ApiException e) {
			flag = true;
		}
		if (Objects.equals(flag, false))
			fail();

		brand = "name";
		category = "";
		BrandForm p = new BrandForm();
		p.setBrandName(brand);
		p.setBrandCategory(category);

		flag = false;
		try {
			dto.addBrand(p);
		} catch (ApiException e) {
			flag = true;
		}
		if (Objects.equals(flag, false))
			fail();

	}

}
