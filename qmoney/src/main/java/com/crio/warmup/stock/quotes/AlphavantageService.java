
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.sql.Date;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  
  private RestTemplate restTemplate;

  public AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate=restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    // TODO Auto-generated method stub
    List<Candle> liCand=new ArrayList<>();
    ObjectMapper om=getObjectMapper();
    // AlphavantageDailyResponse[] ac=restTemplate.getForObject(buildUrl(symbol),AlphavantageDailyResponse[].class);
    String str=restTemplate.getForObject(buildUrl(symbol),String.class);
    
    Map<LocalDate, AlphavantageCandle> candlesMap = om.readValue(str, AlphavantageDailyResponse.class).getCandles();
    // for(AlphavantageDailyResponse x:ac){
      for(Map.Entry<LocalDate,AlphavantageCandle> mp:candlesMap.entrySet()){
    //  Map<LocalDate, AlphavantageCandle> cc=bb.getCandles();
    //  for(Map.Entry<LocalDate,AlphavantageCandle> mp:d.entrySet()){
      LocalDate dd=mp.getKey();
      if((dd.isAfter(from)||dd.isEqual(from))&&(dd.isBefore(to)||dd.isEqual(to))){
        AlphavantageCandle z =mp.getValue();
        z.setDate(dd);
        liCand.add(z);
      }
    //  }
      // liCand.add(x.getCandles());
    }
    Collections.sort(liCand, (a,b)->{
      if(a.getDate().isAfter(b.getDate())){
        return 1;
      }
      else if(a.getDate().isBefore(b.getDate())){
        return -1;
      }
      else
      return 0;
    });
    return liCand;
  }

  private String buildUrl(String symbol) {
    String tkn="DC4HZLVJS5DZ7HIO";
    String uri="https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+"&apikey="+tkn;
    return uri;
  }
  
  // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=aapl&apikey=DC4HZLVJS5DZ7HIO



  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

}

