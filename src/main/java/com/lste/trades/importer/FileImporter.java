package com.lste.trades.importer;

import java.io.IOException;
import java.util.List;

public abstract class FileImporter {
	
	protected String filePath;
	
	public FileImporter(String filePath) {
		this.filePath = filePath;
	}

	public abstract List getData() throws IOException;
}
