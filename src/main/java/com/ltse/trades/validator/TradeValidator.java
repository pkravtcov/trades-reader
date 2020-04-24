package com.ltse.trades.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lste.trades.data.StartTimeCounter;
import com.lste.trades.data.Trade;

public class TradeValidator {
	private List<String> validSymbols;
	private List<String> validFirms;
	private static final int MAX_TRADES_PER_MINUTE = 3;
	private static final long ONE_MINUTE = 60000L;
	private Map<String, List<String>> brokerTradeIdsMap = new HashMap<>();
	private Map<String, StartTimeCounter> brokerStartTimeMap = new HashMap<>();
	
	public TradeValidator(List<String> validSymbols, List<String> validFirms) {
		this.validSymbols = validSymbols;
		this.validFirms = validFirms;
	}
	
	
	public boolean validateIfFieldsAreNotBlank(Trade trade) {
		if(StringUtils.isNotBlank(trade.getSymbol())
				&& StringUtils.isNotBlank(trade.getSymbol())
				&& StringUtils.isNotBlank(trade.getType())
				&& StringUtils.isNotBlank(trade.getQuantity())
				&& StringUtils.isNotBlank(trade.getSequenceId())
				&& StringUtils.isNotBlank(trade.getSide())
				&& StringUtils.isNotBlank(trade.getPrice())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateIfSymbolIsValid(Trade trade) {
		String symbol = trade.getSymbol();
		if(validSymbols.contains(symbol)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateIfBrokerIsValid(Trade trade) {
		String broker = trade.getBroker();
		if(validFirms.contains(broker)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateUniqueTradeIds(Trade trade) {
		List<String> tradeIdsList = brokerTradeIdsMap.get(trade.getBroker());
		
		if (tradeIdsList != null && tradeIdsList.contains(trade.getSequenceId())) {
			return false;
		} else {
			brokerTradeIdsMap.put(trade.getBroker(), Arrays.asList(trade.getSequenceId()));
			return true;
		} 
	}
	
	public boolean validateMaximumOrdersPerMinute(Trade trade) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		String broker = trade.getBroker();
		String tradeTimeStamp = trade.getTimeStamp();
		
		StartTimeCounter stc = brokerStartTimeMap.get(broker);
		//if no info about broker then initializing startTime and counter
		if (stc == null) {
			stc = new StartTimeCounter(tradeTimeStamp, 0);
			brokerStartTimeMap.put(broker, stc);
		}

		Date startTime, tradeTime;
		try {
			startTime = formatter.parse(stc.getStartTime());
			tradeTime = formatter.parse(tradeTimeStamp);
		} catch (ParseException e) {
			System.out.println("Invalid date format for trade - " + trade.toString());
			return false;
		}
		
		Date plusMinuteStartTime = new Date(startTime.getTime() + ONE_MINUTE);
		int orderCount = stc.getOrderCount();
		
		if (tradeTime.after(plusMinuteStartTime) ) {
			//resetting startTime and counter
			brokerStartTimeMap.put(broker, new StartTimeCounter(tradeTimeStamp, 1));
			return true;
		} else if (stc.getOrderCount() < MAX_TRADES_PER_MINUTE) {
			stc.setOrderCount(orderCount + 1);
			return true;
		} else {
			return false;
		}
	}
	
	public Map<String, List<String>> getBrokerTradeIdsMap() {
		return brokerTradeIdsMap;
	}
	
	public void setBrokerTradeIdsMap(Map<String, List<String>> brokerTradeIdsMap) {
		this.brokerTradeIdsMap = brokerTradeIdsMap;
	}
	
	public Map<String, StartTimeCounter> getBrokerStartTimeMap() {
		return brokerStartTimeMap;
	}
	
	public void setBrokerStartTimeMap(Map<String, StartTimeCounter> brokerStartTimeMap) {
		this.brokerStartTimeMap = brokerStartTimeMap;
	}
	
}
