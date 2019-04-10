package com.masker.excelMasker.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.masker.excelMasker.service.FileService;

@Controller
public class UploadController {

	@Autowired
	private FileService service;

	@PostMapping("/upload") // //new annotation since 4.3
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
												   @RequestParam("isHeaderPresent") boolean isHeaderPresent) {

		if (file.isEmpty()) {
			return new ResponseEntity<>("Please upload a non-empty file!!", HttpStatus.BAD_REQUEST);
		}
		String columnHeaders = service.readExcel(file, isHeaderPresent);
		return new ResponseEntity<>(columnHeaders, HttpStatus.OK);
	}

	@PostMapping("/upload/withHeader") // //new annotation since 4.3
	public ResponseEntity<InputStreamResource> getMaskedFile(@RequestParam("file") MultipartFile file,
												@RequestParam("isHeaderPresent") boolean isHeaderPresent,
												@RequestParam("selectedHeaders") List<String> columnHeaders) throws IOException{

		if (file.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ByteArrayInputStream in = service.maskSelectedColumns(file, isHeaderPresent, columnHeaders);
	    
	    HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment"); 
	        headers.add("filename", file.getOriginalFilename());
	        headers.add("Content-Type", "multipart/form-data");
	    
	     return ResponseEntity
	                  .ok()
	                  .headers(headers)
	                  .body(new InputStreamResource(in));
	}

}