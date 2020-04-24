package com.ltse.trades.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.lste.trades.data.StartTimeCounter;
import com.lste.trades.data.Trade;

public class TradeValidatorTest {
	private TradeValidator validator;
	private String defaultTimeStamp = "10/5/2017 10:06:06";
	private String defaultBroker = "TESTBROKER";
	private String defaultSequenceId = "1";
	private String defaultType = "K";
	private String defaultSymbol = "BMW";
	private String defaultQuantity = "200";
	private String defaultPrice = "19.5";
	private String defaultSide = "Buy";
	
	@Before
	public void setup() {
		List<String> validSymbols = Arrays.asList("BMW");
		List<String> validFirms = Arrays.asList("GOLDMAN");
		validator = new TradeValidator(validSymbols, validFirms);		
	}
	
	@Test
	public void whenTradeHasEmptyPriceFieldThenItIsInvalid() {
		String price = "";
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, price, defaultSide);
		assertFalse(validator.validateIfFieldsAreNotBlank(trade));
	}
	
	@Test
	public void whenTradeHasNullPriceFieldThenItIsInvalid() {
		String price = null;
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, price, defaultSide);
		assertFalse(validator.validateIfFieldsAreNotBlank(trade));
	}
	
	@Test
	public void whenTradeHasNoNullOrBlankFielsThenItIsValid() {
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		assertTrue(validator.validateIfFieldsAreNotBlank(trade));
	}
	
	@Test
	public void whenSymbolIsInTheListOfValidSymbolsThenTradeIsValid() {
		String symbol = "BMW";
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, symbol, defaultQuantity, defaultPrice, defaultSide);
		assertTrue(validator.validateIfSymbolIsValid(trade));
	}
	
	@Test
	public void whenSymbolIsNotTheListOfValidSymbolsThenTradeIsInvalid() {
		String symbol = "DAX";
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, symbol, defaultQuantity, defaultPrice, defaultSide);
		assertFalse(validator.validateIfSymbolIsValid(trade));
	}
	
	@Test
	public void whenBrokerIsInTheListOfValidBrokersThenTradeIsValid() {
		String broker = "GOLDMAN";
		Trade trade = new Trade(defaultTimeStamp, broker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		assertTrue(validator.validateIfBrokerIsValid(trade));
	}
	
	@Test
	public void whenBrokerIsNotTheListOfValidBrokersThenTradeIsInvalid() {
		String broker = "BOFA";
		Trade trade = new Trade(defaultTimeStamp, broker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		assertFalse(validator.validateIfBrokerIsValid(trade));
	}
	
	@Test
	public void whenTradeIdIsUniqueForBrokerThenTradeIsValid() {
		String sequenceId = "1";
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, sequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);	
		assertTrue(validator.validateUniqueTradeIds(trade));
	}
	
	@Test
	public void whenTradeIdIsNotUniqueForBrokerThenTradeIsInvalid() {
		String sequenceId = "2";
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, sequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		Map <String, List<String>> brokerTradeIdsMap = new HashMap<>();
		brokerTradeIdsMap.put(defaultBroker, Arrays.asList(sequenceId));
		validator.setBrokerTradeIdsMap(brokerTradeIdsMap);
		assertFalse(validator.validateUniqueTradeIds(trade));
	}
	
	@Test
	public void whenTheFirstTradeIsSentFromBrokerThenItIsValid() {
		Trade trade = new Trade(defaultTimeStamp, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		assertTrue(validator.validateMaximumOrdersPerMinute(trade));
	}
	
	@Test
	public void whenTheThirdTradeIsSentFromBrokerWithinAMinuteThenItIsValid() {
		String defaultTimeStampPlus30Seconds = "10/5/2017 10:06:36";
		Trade trade = new Trade(defaultTimeStampPlus30Seconds, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		
		StartTimeCounter stc = new StartTimeCounter(defaultTimeStamp, 2);
		Map<String, StartTimeCounter> brokerStartTimeMap = new HashMap<>();
		brokerStartTimeMap.put(defaultBroker, stc);
		
		validator.setBrokerStartTimeMap(brokerStartTimeMap);
		assertTrue(validator.validateMaximumOrdersPerMinute(trade));
	}
	
	@Test
	public void whenTheFourthTradeIsSentFromBrokerWithinAMinuteThenItIsInvalid() {
		String defaultTimeStampPlus30Seconds = "10/5/2017 10:06:36";
		Trade trade = new Trade(defaultTimeStampPlus30Seconds, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		
		StartTimeCounter stc = new StartTimeCounter(defaultTimeStamp, 3);
		Map<String, StartTimeCounter> brokerStartTimeMap = new HashMap<>();
		brokerStartTimeMap.put(defaultBroker, stc);
		
		validator.setBrokerStartTimeMap(brokerStartTimeMap);
		assertFalse(validator.validateMaximumOrdersPerMinute(trade));
	}
	
	@Test
	public void whenTheTradeIsSentAfterAMinuteThenItIsValid() {
		String defaultTimeStampPlus1MinuteAnd1Second = "10/5/2017 10:07:01";
		Trade trade = new Trade(defaultTimeStampPlus1MinuteAnd1Second, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		
		StartTimeCounter stc = new StartTimeCounter(defaultTimeStamp, 1);
		Map<String, StartTimeCounter> brokerStartTimeMap = new HashMap<>();
		brokerStartTimeMap.put(defaultBroker, stc);
		
		validator.setBrokerStartTimeMap(brokerStartTimeMap);
		assertTrue(validator.validateMaximumOrdersPerMinute(trade));
	}
	
	@Test
	public void whenTradeTimeStampFormatIsWrongThenTradeIsInvalid() {
		String wrongTimestampFormat = "05/2017 22:07:01 AM";
		Trade trade = new Trade(wrongTimestampFormat, defaultBroker, defaultSequenceId, 
				defaultType, defaultSymbol, defaultQuantity, defaultPrice, defaultSide);
		
		assertFalse(validator.validateMaximumOrdersPerMinute(trade));
	}
}
