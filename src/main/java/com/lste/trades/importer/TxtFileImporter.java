package com.lste.trades.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TxtFileImporter extends FileImporter{

	public TxtFileImporter(String filePath) {
		super(filePath);
	}

	@Override
	public List<String> getData() throws IOException {
		List<String> data = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		//try with resource closes reader 
		try (InputStream stream = loader.getResourceAsStream(filePath);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader)) {
			
			while(reader.ready()) {
				data.add(reader.readLine());
			}
		}
		
		return data;
	}
	
	
}
