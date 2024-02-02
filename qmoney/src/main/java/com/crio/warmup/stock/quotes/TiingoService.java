
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    // TODO Auto-generated method stub
    List<Candle> liCandles=new ArrayList<>();
        // RestTemplate templ=new RestTemplate();
        String str=restTemplate.getForObject(buildUri(symbol, from, to), String.class);
        // TiingoCandle[] candle=restTemplate.getForObject(buildUri(symbol, from, to), TiingoCandle[].class);
        ObjectMapper om=getObjectMapper();
        TiingoCandle[] candle=om.readValue(str, TiingoCandle[].class);
        // liCandles.add(candle[0]);
        for(TiingoCandle t:candle){
        liCandles.add(t);
        }
        // System.out.print(liCandles);
      return liCandles;
  }
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String tkn="eb8c89943818d5d4e367aa4cc26c0f1947ae68bd";
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="+tkn;
      return uriTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
