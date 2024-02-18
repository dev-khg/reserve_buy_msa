package hg.reserve_buy.itemserviceapi.presentation;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.itemserviceapi.IntegrationTest;
import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemBriefDto;
import hg.reserve_buy.itemserviceapi.core.service.dto.ItemDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest extends IntegrationTest {

    @Test
    @DisplayName("아이템 리스트 조회시 정상적으로 응답이 와야한다.")
    void success_find_item_list() throws Exception {
        // given
        List<ItemBriefDto> savedList = savedItemInfoEntities.stream()
                .map(ItemInfoEntity::getItemEntity)
                .map(this::parseEntityToBrief)
                .sorted(Comparator.comparing(ItemBriefDto::getItemNumber))
                .toList();

        // when
        MvcResult mvcResult = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        ItemBriefDto[] itemBriefDtoArray
                = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), ItemBriefDto[].class);
        Arrays.sort(itemBriefDtoArray, Comparator.comparing(ItemBriefDto::getItemNumber));

        // then
        assertEquals(savedList.size(), itemBriefDtoArray.length);

        for (int i = 0; i < itemBriefDtoArray.length; i++) {
            ItemBriefDto savedDto = savedList.get(i);
            ItemBriefDto foundDto = itemBriefDtoArray[i];

            assertEquals(foundDto.getPrice(), savedDto.getPrice());
            assertEquals(foundDto.getType(), savedDto.getType());
            assertEquals(foundDto.getStartAt(), savedDto.getStartAt());
            assertEquals(foundDto.getName(), savedDto.getName());
            assertEquals(foundDto.getItemNumber(), savedDto.getItemNumber());
        }
    }

    @Test
    @DisplayName("아이템 디테일 조회시 정상적으로 응답이 와야한다.")
    void success_find_item_info() throws Exception {
        // given
        List<ItemDetailDto> savedDetails = savedItemInfoEntities.stream()
                .map(this::parseEntityToDetail)
                .toList();
        int savedLength = savedDetails.size();

        for (int i = 0; i < savedLength; i++) {
            // when
            ItemDetailDto savedDetail = savedDetails.get(i);
            MvcResult mvcResult = mockMvc.perform(get("/" + savedDetail.getItemNumber()))
                    .andExpect(status().isOk())
                    .andReturn();

            ItemDetailDto responseDetail
                    = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), ItemDetailDto.class);

            // then
            assertEquals(savedDetail.getItemNumber(), responseDetail.getItemNumber());
            assertEquals(savedDetail.getContent(), responseDetail.getContent());
            assertEquals(savedDetail.getType(), responseDetail.getType());
            assertEquals(savedDetail.getStartAt(), responseDetail.getStartAt());
            assertEquals(savedDetail.getName(), responseDetail.getName());
            assertEquals(savedDetail.getPrice(), responseDetail.getPrice());
        }
    }

    @Test
    @DisplayName("존재하지 않는 아이템 디테일 조회시 예외가 반환되여야 한다.")
    void failure_find_item_info() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(get("/" + Long.MAX_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}