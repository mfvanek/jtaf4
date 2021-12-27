package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeriesRankingServiceTest {

    @Autowired
    private SeriesRankingService seriesRankingService;

    @Test
    void getClubRanking() {
        ClubRankingData clubRanking = seriesRankingService.getClubRanking(1L);

        assertThat(clubRanking.sortedResults().size()).isEqualTo(4);
    }

    @Test
    void getSeriesRanking() {
        SeriesRankingData seriesRanking = seriesRankingService.getSeriesRanking(3L);

        assertThat(seriesRanking.name()).isEqualTo("CIS 2019");
    }
}