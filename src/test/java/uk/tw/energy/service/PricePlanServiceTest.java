package uk.tw.energy.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class PricePlanServiceTest{

    
    private static final String PRICE_PLAN_1_ID = "test-supplier";
    private static final String PRICE_PLAN_2_ID = "best-supplier";
    private static final String PRICE_PLAN_3_ID = "second-best-supplier";
    private static final String SMART_METER_ID = "smart-meter-4";


    private PricePlanService pricePlanService;
    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {

        PricePlan pricePlan1 = new PricePlan(PRICE_PLAN_1_ID, null, BigDecimal.TEN, null);
        PricePlan pricePlan2 = new PricePlan(PRICE_PLAN_2_ID, null, BigDecimal.ONE, null);
        PricePlan pricePlan3 = new PricePlan(PRICE_PLAN_3_ID, null, BigDecimal.valueOf(2), null);
        Flux<List<PricePlan>> pricePlans = Flux.just(Arrays.asList(pricePlan1, pricePlan2, pricePlan3));

        meterReadingService = new MeterReadingService(new HashMap<>());

        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), 
        BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        pricePlanService = new PricePlanService(pricePlans, meterReadingService);
    }


    @Test
    public void shouldGetBestPlan(){
        Flux<Map<String, BigDecimal>> result = pricePlanService.
        getConsumptionCostOfElectricityReadingsForEachPricePlan(SMART_METER_ID);      
            assertThat(result.blockFirst().entrySet().stream()
            .filter(e -> e.getKey().equals("best-supplier"))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null))
           .isEqualTo(new BigDecimal("10.0"));
    }

    
}