
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {
  
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
   public RestTemplate getTemplate(){
    return this.restTemplate;
   }
    public PortfolioManagerImpl(){
    }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
    
  }
  public PortfolioManagerImpl(StockQuotesService stockQuotesService,RestTemplate reeRestTemplate) {
    this.stockQuotesService = stockQuotesService;
    this.restTemplate = restTemplate;
  }

class TotalReturnsComparator implements Comparator<TotalReturnsDto>{

    @Override
    public int compare(TotalReturnsDto arg0, TotalReturnsDto arg1) {
      // TODO Auto-generated method stub
      if(arg0.getClosingPrice()>arg1.getClosingPrice()){
        return 1;
      }
      else if(arg0.getClosingPrice()<arg1.getClosingPrice()){
        return -1;
      }else
      return 0;
    }

  }

  class AnnualizedReturnComparator implements Comparator<AnnualizedReturn>{

    @Override
    public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {
     return arg0.getTotalReturns().compareTo(arg1.getTotalReturns());
    }
    
  }


  private Comparator<AnnualizedReturn> getComparator() {
    Comparator<AnnualizedReturn> cmptr=new AnnualizedReturnComparator();
    // return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    return cmptr;
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        List<Candle> liCandles=new ArrayList<>();
        // RestTemplate templ=new RestTemplate();
        TiingoCandle[] candle=restTemplate.getForObject(buildUri(symbol, from, to), TiingoCandle[].class);
        System.out.println(Arrays.toString(candle));
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


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) throws JsonProcessingException  {
    // TODO Auto-generated method stub
    List<AnnualizedReturn> res=new ArrayList<>();
    for(PortfolioTrade s:portfolioTrades){
      List<Candle> liCandles = stockQuotesService.getStockQuote(s.getSymbol(), s.getPurchaseDate(), endDate );
      System.out.println(liCandles);
    
    // 
    Double buyPrice=liCandles.get(0).getOpen();;
    Double sellPrice=liCandles.get(liCandles.size()-1).getClose();
    
    Double totalRtn=(sellPrice-buyPrice)/buyPrice;
   
    LocalDate strtDate = s.getPurchaseDate();
    Double year = strtDate.until(endDate, ChronoUnit.DAYS)/365.24;
    // Double noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
    Double annualRtn=Math.pow((1+totalRtn),(1/year))-1;
    String symb=s.getSymbol();
    AnnualizedReturn ar= new AnnualizedReturn(symb, annualRtn, totalRtn);
    res.add(ar);
    }
    
    Collections.sort(res,(b,a) -> 
      a.getTotalReturns().compareTo(b.getTotalReturns())
    );
    // Collections.sort(res,getComparator());
    
    return res;
  }
  //   return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  // }



  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
