package com.lste.trades.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import com.lste.trades.data.Trade;

public class CsvFileImporter extends FileImporter {
	
	private final String COMMA = ",";
	
	public CsvFileImporter(String filePath) {
		super(filePath);
	}
	
	@Override
	public List<Trade> getData() throws IOException {
		List<Trade> data = new ArrayList<Trade>();
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//try with resources 
		try (InputStream stream = loader.getResourceAsStream(filePath);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader)) {
			String line;
			//skipping the header
			reader.readLine();
			while((line = reader.readLine()) != null) {
				String [] values = line.split(COMMA, -1);
				data.add(new Trade(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]));
			}
		}
		return data;
	}

}
