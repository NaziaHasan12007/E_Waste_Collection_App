package com.ewaste.client.controller.customer;

import com.ewaste.client.dto.response.RewardHistoryClientResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardViewControllerTest {

    @Test
    void mapsRewardHistoryApiDataToDisplayedTransactions() throws Exception {
        String responseJson = """
                [
                  {
                    "rewardId": 9,
                    "userId": 7,
                    "pickupId": 42,
                    "points": 20,
                    "calculationBasis": "Processing outcome: RECYCLE",
                    "createdAt": "2026-09-10T10:00:00"
                  },
                  {
                    "rewardId": 10,
                    "userId": 7,
                    "points": -5,
                    "calculationBasis": "Points redeemed",
                    "createdAt": "2026-09-10T10:05:00"
                  }
                ]
                """;

        List<RewardHistoryClientResponse> history = new ObjectMapper()
                .readValue(responseJson,
                        new com.fasterxml.jackson.core.type.TypeReference<>() {});

        List<RewardViewController.RewardTransactionItem> transactions =
                RewardViewController.toTransactionItems(history);

        assertEquals(2, transactions.size());
        assertEquals("Processing outcome: RECYCLE",
                transactions.get(0).getDescription());
        assertEquals(20, transactions.get(0).getPoints());
        assertEquals("EARNED", transactions.get(0).getType());
        assertEquals(-5, transactions.get(1).getPoints());
        assertEquals("REDEEMED", transactions.get(1).getType());
    }
}
