package hg.reserve_buy.stockserviceapi.presentation;

import hg.reserve_buy.stockserviceapi.config.IntegrationTest;
import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockControllerTest extends IntegrationTest {

    @Test
    @DisplayName("존재하지 않는 아이템 재고 조회시 정상적으로 조회되어야 한다.")
    void failure_get_item_stock() throws Exception {
        // given

        // when
        mockMvc.perform(get("/stock/" + Long.MAX_VALUE))
                .andExpect(status().isBadRequest());

        // then
    }

    @Test
    @DisplayName("재고 조회시 정상적으로 조회되어야 한다.")
    void success_get_item_stock() throws Exception {
        // given
        StockEntity stockEntity = savedEntity;

        // when
        MvcResult mvcResult = mockMvc.perform(get("/stock/" + savedEntity.getItemNumber()))
                .andExpect(status().isOk())
                .andReturn();

        Integer stock = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), Integer.class);

        // then
        assertEquals(stock, stockEntity.getTotal());
    }

}