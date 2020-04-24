package com.lste.trades.runner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lste.trades.data.Trade;
import com.lste.trades.importer.CsvFileImporter;
import com.lste.trades.importer.FileImporter;
import com.lste.trades.importer.TxtFileImporter;
import com.ltse.trades.validator.TradeValidator;

public class TradesFilterMain {
	
	private static final String SYMBOLS_FILE = "symbols.txt";
	private static final String BROKERS_FILE = "firms.txt";
	private static final String TRADES_FILE = "trades.csv";

	public static void main(String[] args) {
		List<Trade> validOrders = new ArrayList<>();
		List<Trade> invalidOrders = new ArrayList<>();
		
		FileImporter symbolsImporter = new TxtFileImporter(SYMBOLS_FILE);
		FileImporter firmsImporter = new TxtFileImporter(BROKERS_FILE);
		FileImporter tradesImporter = new CsvFileImporter(TRADES_FILE);
		
		try {
			List<String> validSymbols = symbolsImporter.getData();
			List<String> validFirms = firmsImporter.getData();
			List<Trade> trades = tradesImporter.getData();
			
			TradeValidator validator = new TradeValidator(validSymbols, validFirms);
			
			try(BufferedWriter validBw = new BufferedWriter(new FileWriter("output/validOrders.txt"));
					BufferedWriter invalidBw = new BufferedWriter(new FileWriter("output/invalidOrders.txt"))) {		
				for (Trade trade : trades) {
					//making sure that no fields are blank before
					//we can work with the other validations
					if (validator.validateIfFieldsAreNotBlank(trade)
						&& validator.validateIfSymbolIsValid(trade)
						&& validator.validateIfBrokerIsValid(trade)
						&& validator.validateMaximumOrdersPerMinute(trade)
						&& validator.validateUniqueTradeIds(trade)) {
							validOrders.add(trade);
							validBw.write("Broker: " + trade.getBroker() + "; TradeId: " + trade.getSequenceId());
							validBw.newLine();
					} else {
						invalidOrders.add(trade);
						invalidBw.write("Broker: " + trade.getBroker() + "; TradeId: " + trade.getSequenceId());
						invalidBw.newLine();
					}	
				}
			}
		
		} catch (IOException ex) {
			System.out.println("Cannot read input files...");
		}
	}

}
